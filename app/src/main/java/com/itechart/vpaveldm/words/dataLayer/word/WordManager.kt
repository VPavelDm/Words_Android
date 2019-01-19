package com.itechart.vpaveldm.words.dataLayer.word

import android.annotation.SuppressLint
import android.arch.paging.DataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.Executors
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener
import com.itechart.vpaveldm.words.core.extension.ValueEventListener as DelegateValueEventListener

object WordManager {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userManager = UserManager()
    private val executors = Executors.newSingleThreadExecutor()

    init {
        // Local database synchronization
        // Called when program is started...
        userNameAndID()?.let { (_, userID) ->
            usersRef
                .child(userID)
                .child("notification")
                .addChildEventListener(object : DelegateChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                        val word = convert(snapshot) ?: return
                        executors.submit { Application.wordDao.addWords(word) }
                    }
                })
            usersRef
                .child(userID)
                .child("words")
                .addListenerForSingleValueEvent(object : DelegateValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val words = snapshot.children.mapNotNull { convert(it) }.toTypedArray()
                        executors.submit { Application.wordDao.addWords(*words) }
                    }
                })
        }
    }

    fun getSubscriptionsWords(): DataSource.Factory<Int, Word> {
        val (userName, _) = userNameAndID() ?: "" to ""
        return Application.wordDao.getSubscriptionsWords(userName)
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        userNameAndID()?.let { (userName, userID) ->
            val key = usersRef.child("$userID/words").push().key ?: return@create
            val addWord = word.copy(key = key, owner = userName, count = 0, date = Date())
            Application.wordDao.addWords(addWord)
            sendWordToRemoteDB(addWord, word.owner.isEmpty())
            subscriber.onComplete()
        } ?: subscriber.onError(UserError())
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        Application.wordDao.updateWord(word)
        updateWordAtRemoteDB(word)
        subscriber.onComplete()
    }

    fun removeWord(word: Word, toAdd: Boolean): Completable = Completable.create { subscriber ->
        Application.wordDao.removeWord(word)
        removeWordFromRemoteDB(word)
        if (toAdd)
            addWord(word).subscribe()
        subscriber.onComplete()
    }

    fun getWordCount(): Single<Int> = Single.create { subscriber ->
        userNameAndID()?.let { (userName, _) ->
            val count = Application.wordDao.getWordCount(userName)
            subscriber.onSuccess(count)
        } ?: subscriber.onError(UserError())
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        userNameAndID()?.let { (userName, _) ->
            val currentDate = Date().plusDays(1).resetTime()
            val words = Application.wordDao.getWordsToStudy(userName, currentDate)
            subscriber.onSuccess(words)
        } ?: subscriber.onError(UserError())
    }

    private fun removeWordFromRemoteDB(word: Word) {
        val (_, userID) = userNameAndID() ?: return
        usersRef
            .child(userID)
            .child("notification")
            .child(word.key)
            .setValue(null)
    }

    private fun updateWordAtRemoteDB(word: Word) {
        val (_, userID) = userNameAndID() ?: return
        usersRef
            .child(userID)
            .child("words")
            .child(word.key)
            .setValue(word)
    }

    @SuppressLint("CheckResult")
    private fun sendWordToRemoteDB(word: Word, doNotifySubscribers: Boolean) {
        val (_, userID) = userNameAndID() ?: return
        val userUpdates = HashMap<String, Any>()
        // If it is my word I have to notify all subscribers and other way not
        if (doNotifySubscribers) {
            userManager.getSubscribers().subscribe { subscribers ->
                subscribers.forEach {
                    val key = usersRef.child("${it.key}/notification").push().key
                    userUpdates["/${it.key}/notification/$key"] = word
                }
                userUpdates["/$userID/words/${word.key}"] = word
                usersRef.updateChildren(userUpdates)
            }
        } else {
            userUpdates["/$userID/words/${word.key}"] = word
            usersRef.updateChildren(userUpdates)
        }
    }

    private fun convert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let {
            word.key = it
            return word
        } ?: return null
    }

    private fun userNameAndID(): Pair<String, String>? {
        val userName = FirebaseAuth.getInstance().currentUser?.displayName ?: return null
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return null
        return userName to userID
    }

}