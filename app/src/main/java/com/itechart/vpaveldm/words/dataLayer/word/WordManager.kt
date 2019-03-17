package com.itechart.vpaveldm.words.dataLayer.word

import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.core.extension.timeIntervalSince1970
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.*
import kotlin.collections.HashMap
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener
import com.itechart.vpaveldm.words.core.extension.ValueEventListener as DelegateValueEventListener

object WordManager {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userManager = UserManager()

    /**
     * The method is used to keep the data synchronized
     */
    fun sync() {
        val userID = userManager.userNameAndID().second ?: return
        usersRef.child(userID).child(WordSection.WORDS.description()).keepSynced(true)
        usersRef.child(userID).child(WordSection.NOTIFICATION.description()).keepSynced(true)
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val (userName, userID) = userManager.userNameAndID()
        if (userName != null && userID != null) {
            val key = usersRef.child("$userID/words").push().key ?: return@create
            val addWord = word.copy(
                key = key,
                owner = userName,
                count = 0,
                date = Date().timeIntervalSince1970
            )
            sendWordToRemoteDB(subscriber, addWord, word.owner.isEmpty())
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
    }.andThen(Completable.defer { if (toAdd) addWord(word) else Completable.complete() })

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

    fun editWord(word: Word): Completable = Completable.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        userManager.getSubscribersWithWordInNotification(word).subscribe({ subscribers ->
            val userUpdates = HashMap<String, Any>()
            subscribers.forEach {
                userUpdates["/${it.key}/notification/${word.key}"] = word
            }
            userUpdates["/$userID/words/${word.key}"] = word
            usersRef.updateChildren(userUpdates)
                .addOnSuccessListener { subscriber.onComplete() }
                .addOnFailureListener { subscriber.tryOnError(it) }
        }, {
            subscriber.tryOnError(it)
        })
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        val date = plusDays(1).resetTime().timeIntervalSince1970.toDouble()
        usersRef
            .child(userID)
            .child(WordSection.WORDS.description())
            .orderByChild("date")
            .endAt(date)
            .addListenerForSingleValueEvent(singleListener(subscriber))
    }

    fun getWords(section: WordSection): Single<List<Word>> = Single.create { subscriber ->
        val userID = userManager.userNameAndID().second ?: return@create
        usersRef
            .child(userID)
            .child(section.description())
            .orderByChild("createDate")
            .addListenerForSingleValueEvent(singleListener(subscriber))
    }

    @SuppressLint("CheckResult")
    private fun sendWordToRemoteDB(subscriber: CompletableEmitter, word: Word, doNotifySubscribers: Boolean) {
        fun sendRequest(map: HashMap<String, Any>) {
            usersRef.updateChildren(map)
                .addOnSuccessListener { subscriber.onComplete() }
                .addOnFailureListener { subscriber.tryOnError(it) }
        }

        val userID = userManager.userNameAndID().second
        val userUpdates = HashMap<String, Any>()
        if (userID != null) {
            // If it is my word I have to notify all subscribers and other way not
            if (doNotifySubscribers) {
                userManager.getSubscribers().subscribe { subscribers ->
                    subscribers.forEach {
                        userUpdates["/${it.key}/notification/${word.key}"] = word
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

    private fun migrationConvert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let { wordKey ->
            word.key = wordKey
            return word
        } ?: return null
    }

    private fun singleListener(subscriber: SingleEmitter<List<Word>>): ValueEventListener =
        object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError) {
                subscriber.tryOnError(snapshot.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val words = snapshot.children.mapNotNull { migrationConvert(it) }.reversed()
                subscriber.onSuccess(words)
            }
        }

}