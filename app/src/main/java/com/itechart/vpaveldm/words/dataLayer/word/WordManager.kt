package com.itechart.vpaveldm.words.dataLayer.word

import android.annotation.SuppressLint
import android.arch.paging.DataSource
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.HashMap
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener
import com.itechart.vpaveldm.words.core.extension.ValueEventListener as DelegateValueEventListener

object WordManager {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userManager = UserManager()
    private val executors = Executors.newSingleThreadExecutor()

    init {
        // Local database synchronization
        // Called when program is started...
        Log.i("appLifeCycle", "WordManager: Subscribed to notification updating")
        userManager.userNameAndID().second?.let { userID ->
            usersRef
                .child(userID)
                .child("notification")
                .addChildEventListener(object : DelegateChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                        val word = convert(snapshot) ?: return
                        Log.i("appLifeCycle", "WordManager: get word in notification section: word = ${word.word}")
                        executors.submit { Application.wordDao.addWordWithExamples(word) }
                    }
                })
            Log.i("appLifeCycle", "WordManager: Subscribed to words updating")
            usersRef
                .child(userID)
                .child("words")
                .addListenerForSingleValueEvent(
                    object : DelegateValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val words = snapshot.children.mapNotNull { convert(it) }.toTypedArray()
                            Log.i("appLifeCycle", "WordManager: get words in words section: count = ${words.size}")
                            executors.submit { Application.wordDao.addWordWithExamples(*words) }
                        }
                    })
        }
    }

    fun getSubscriptionsWords(): Single<DataSource.Factory<Int, Word>> = Single.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: get subscriptions' words")
        val userName = userManager.userNameAndID().first ?: ""
        subscriber.onSuccess(Application.wordDao.getSubscriptionsWords(userName))
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val (userName, userID) = userManager.userNameAndID()
        if (userName != null && userID != null) {
            Log.i("appLifeCycle", "WordManager: send 'add word' request to local db")
            val key = usersRef.child("$userID/words").push().key ?: return@create
            val addWord = word.copy(
                key = key,
                owner = userName,
                count = 0,
                date = Date(),
                word = word.word.toLowerCase(),
                transcription = word.transcription.toLowerCase(),
                translate = word.translate.toLowerCase()
            )
            addWord.examples.forEach { it.wordId = key }
            Application.wordDao.addWordWithExamples(addWord)
            sendWordToRemoteDB(addWord, word.owner.isEmpty())
            subscriber.onComplete()
        } else subscriber.onError(UserError())
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: send 'update word' request to local db")
        val updateWord = word.copy(
            word = word.word.toLowerCase(),
            transcription = word.transcription.toLowerCase(),
            translate = word.translate.toLowerCase()
        )
        Application.wordDao.updateWord(updateWord)
        updateWordAtRemoteDB(updateWord)
        subscriber.onComplete()
    }

    fun removeWordFromNotification(word: Word, toAdd: Boolean): Completable = Completable.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: send 'remove word from notification' request to local db")
        Application.wordDao.removeWordWithExamples(word)
        removeWordFromRemoteDB(word, "notification")
        if (toAdd)
            addWord(word).subscribe()
        subscriber.onComplete()
    }

    fun removeWordFromProfile(word: Word): Completable = Completable.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: send 'remove word from profile' request to local db")
        Application.wordDao.removeWordWithExamples(word)
        removeWordFromRemoteDB(word, "words")
        subscriber.onComplete()
    }

    fun getSubscriptionsWordCount(): Single<Int> = Single.create { subscriber ->
        userManager.userNameAndID().first?.let { userName ->
            val count = Application.wordDao.getSubscriptionsWordCount(userName)
            Log.i("appLifeCycle", "WordManager: get subscriptions' words count = $count")
            subscriber.onSuccess(count)
        } ?: subscriber.onError(UserError())
    }

    fun getWordCount(): Single<Int> = Single.create { subscriber ->
        userManager.userNameAndID().first?.let { userName ->
            val count = Application.wordDao.getWordCount(userName)
            Log.i("appLifeCycle", "WordManager: get my words count = $count")
            subscriber.onSuccess(count)
        } ?: subscriber.onError(UserError())
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: get words to study")
        userManager.userNameAndID().first?.let { userName ->
            val words = Application.wordDao.getWordsWithExamplesToStudy(userName)
            subscriber.onSuccess(words)
        } ?: subscriber.onError(UserError())
    }

    fun getWords(): Single<DataSource.Factory<Int, Word>> = Single.create { subscriber ->
        Log.i("appLifeCycle", "WordManager: get all words")
        userManager.userNameAndID().first?.let { userName ->
            val dataSource = Application.wordDao.getWordsWithExamples(userName)
            subscriber.onSuccess(dataSource)
        } ?: subscriber.onError(UserError())
    }

    private fun removeWordFromRemoteDB(word: Word, section: String) {
        Log.i("appLifeCycle", "WordManager: send 'remove word' request to remote db")
        userManager.userNameAndID().second?.let { userID ->
            usersRef
                .child(userID)
                .child(section)
                .child(word.key)
                .setValue(null)
                .addOnSuccessListener { Log.i("appLifeCycle", "WordManager: remove word response is successful") }
                .addOnFailureListener { Log.i("appLifeCycle", "WordManager: remove word response is fail") }
        }
    }

    private fun updateWordAtRemoteDB(word: Word) {
        Log.i("appLifeCycle", "WordManager: send 'update word' request to remote db")
        userManager.userNameAndID().second?.let { userID ->
            usersRef
                .child(userID)
                .child("words")
                .child(word.key)
                .setValue(word)
                .addOnSuccessListener { Log.i("appLifeCycle", "WordManager: update word response is successful") }
                .addOnFailureListener { Log.i("appLifeCycle", "WordManager: update word response is fail") }
        }
    }

    @SuppressLint("CheckResult")
    private fun sendWordToRemoteDB(word: Word, doNotifySubscribers: Boolean) {
        fun sendRequest(map: HashMap<String, Any>) {
            usersRef.updateChildren(map)
                .addOnSuccessListener { Log.i("appLifeCycle", "WordManager: send word response is successful") }
                .addOnFailureListener { Log.i("appLifeCycle", "WordManager: send word response is fail") }
        }
        Log.i("appLifeCycle", "WordManager: send 'send word' request to remote db")
        val userID = userManager.userNameAndID().second
        val userUpdates = HashMap<String, Any>()
        if (userID != null) {
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
                sendRequest(userUpdates)
            }
        } else {
            userUpdates["/$userID/words/${word.key}"] = word
            sendRequest(userUpdates)
        }
    }

    private fun convert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let { wordKey ->
            word.key = wordKey
            word.examples.forEach { it.wordId = wordKey }
            return word
        } ?: return null
    }

}