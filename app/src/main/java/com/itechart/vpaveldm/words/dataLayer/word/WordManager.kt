package com.itechart.vpaveldm.words.dataLayer.word

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.core.extension.timeIntervalSince1970
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.*
import kotlin.collections.HashMap
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener
import com.itechart.vpaveldm.words.core.extension.ValueEventListener as DelegateValueEventListener

object WordManager {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userManager = UserManager()

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val (userName, userID) = userManager.userNameAndID()
        if (userName != null && userID != null) {
            Log.i("appLifeCycle", "WordManager: send 'add word' request to local db")
            val key = usersRef.child("$userID/words").push().key ?: return@create
            val addWord = word.copy(
                key = key,
                owner = userName,
                count = 0,
                date = Date().timeIntervalSince1970,
                word = word.word.toLowerCase(),
                transcription = word.transcription.toLowerCase(),
                translate = word.translate.toLowerCase(),
                account = userName
            )
            addWord.examples.forEach { it.wordId = key }
            Application.wordDao.addWordWithExamples(addWord)
            sendWordToRemoteDB(addWord, word.owner.isEmpty())
            subscriber.onComplete()
        } else subscriber.onError(UserError())
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child("words")
            .child(word.key)
            .setValue(word)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun removeWordFromNotification(word: Word, toAdd: Boolean): Completable = Completable.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child("notification")
            .child(word.key)
            .setValue(null)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }.andThen { if (toAdd) addWord(word) }

    fun removeWordFromProfile(word: Word): Completable = Completable.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child("words")
            .child(word.key)
            .setValue(null)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child(WordSection.WORDS.description())
            .orderByChild("date")
            .endAt(plusDays(1).resetTime().timeIntervalSince1970.toDouble())
            .addListenerForSingleValueEvent(singleListener(subscriber))
    }

    fun getWords(section: WordSection): Single<List<Word>> = Single.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child(section.description())
            .addListenerForSingleValueEvent(singleListener(subscriber))
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
        val userName = userManager.userNameAndID().first ?: return null
        snapshot.key?.let { wordKey ->
            word.key = wordKey
            word.examples.forEach { it.wordId = wordKey }
            word.account = userName
            return word
        } ?: return null
    }

    private fun singleListener(subscriber: SingleEmitter<List<Word>>): ValueEventListener =
        object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError) {
                subscriber.tryOnError(snapshot.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val words = snapshot.children.mapNotNull { convert(it) }
                subscriber.onSuccess(words)
            }
        }

}