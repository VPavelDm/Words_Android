package com.itechart.vpaveldm.words.dataLayer.word

import android.annotation.SuppressLint
import android.arch.paging.DataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import com.itechart.vpaveldm.words.dataLayer.word.WordState.*
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.Executors
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener

class WordManager private constructor() {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val executors = Executors.newSingleThreadExecutor()

    init {
        // Remote database synchronization
        // Called when program is started...
        executors.submit {
            val words = Application.wordDao.getWords()
            words.forEach { word ->
                when (word.state) {
                    ADD -> {
                        sendWordToRemoteDB(word)
                    }
                    UPDATE -> {
                        updateWordAtRemoteDB(word)
                    }
                    REMOVE -> {
                        removeWordFromRemoteDB(word)
                    }
                    NOTHING -> {
                    }
                }
            }
        }
        // Local database synchronization
        // Called when program is started...
        executors.submit {
            val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@submit
            usersRef
                .child(userID)
                .child("notification")
                .addChildEventListener(object : DelegateChildEventListener() {
                    override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                        val word = convert(snapshot) ?: return
                        executors.submit { Application.wordDao.addWords(listOf(word)) }
                    }
                })
        }
    }

    fun getSubscriptionsWords(): DataSource.Factory<Int, Word> {
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        return Application.wordDao.getSubscriptionsWords(userName)
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return@create
        val key = usersRef.child("$userID/words").push().key ?: return@create
        val addWord = word.copy(key = key, state = ADD, owner = userName)
        val words = listOf(addWord)
        Application.wordDao.addWords(words)
        sendWordToRemoteDB(addWord)
        subscriber.onComplete()
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        val updateWord = word.copy(state = UPDATE)
        Application.wordDao.updateWord(updateWord)
        updateWordAtRemoteDB(updateWord)
        subscriber.onComplete()
    }

    fun getWordCount(): Single<Int> = Single.create { subscriber ->
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val count = Application.wordDao.getWordCount(userName)
        subscriber.onSuccess(count)
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val currentDate = Date().plusDays(1).resetTime()
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val words = Application.wordDao.getWordsToStudy(userName, currentDate)
        subscriber.onSuccess(words)
    }

    private fun removeWordFromRemoteDB(word: Word) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return
        usersRef
            .child(userID)
            .child("notification")
            .child(word.key)
            .setValue(null)
            .addOnSuccessListener {
                executors.submit { Application.wordDao.removeWord(word) }
            }
    }

    private fun updateWordAtRemoteDB(word: Word) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return
        usersRef
            .child(userID)
            .child("words")
            .child(word.key)
            .setValue(word)
            .addOnSuccessListener {
                executors.submit { Application.wordDao.updateWord(word.copy(state = NOTHING)) }
            }
    }

    @SuppressLint("CheckResult")
    private fun sendWordToRemoteDB(word: Word) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return
        val userManager = UserManager()
        val userUpdates = HashMap<String, Any>()
        userManager.getSubscribers().subscribe { subscribers ->
            subscribers.forEach {
                val key = usersRef.child("${it.key}/notification").push().key
                userUpdates["/${it.key}/notification/$key"] = word
            }
            userUpdates["/$userID/words/${word.key}"] = word
            usersRef.updateChildren(userUpdates)
                .addOnSuccessListener {
                    executors.submit { Application.wordDao.updateWord(word.copy(state = NOTHING)) }
                }
        }
    }

    private fun convert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let {
            word.key = it
            return word
        } ?: return null
    }

    companion object {
        val shared = WordManager()
    }

}