package com.itechart.vpaveldm.words.dataLayer.word

import android.arch.paging.DataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import com.itechart.vpaveldm.words.dataLayer.word.WordState.*
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener

class WordManager {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    fun syncLocalDatabase() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        usersRef
            .child(userID)
            .child("notification")
            .addChildEventListener(object : DelegateChildEventListener() {
                override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                    Thread(Runnable {
                        val word = convert(snapshot) ?: return@Runnable
                        Application.wordDao.addWords(listOf(word))
                    }).start()
                }
            })
    }

    fun getSubscriptionsWords(): DataSource.Factory<Int, Word> {
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        return Application.wordDao.getSubscriptionsWords(userName)
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val words = listOf(word.copy(state = ADD, owner = userName))
        Application.wordDao.addWords(words)
        sync()
        subscriber.onComplete()
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        Application.wordDao.updateWord(word.copy(state = UPDATE))
        sync()
        subscriber.onComplete()
    }

    fun getWordCount(): Single<Int> = Single.create { subscriber ->
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val count = Application.wordDao.getWordCount(userName)
        subscriber.onSuccess(count)
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val currentDate = Date().plusDays(1).resetTime()
        val words = Application.wordDao.getWordsToStudy(currentDate)
        subscriber.onSuccess(words)
    }

    private fun sync() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: return
        val words = Application.wordDao.getWords()
        words.forEach { word ->
            when (word.state) {
                ADD -> {
                    val userManager = UserManager()
                    val userUpdates = HashMap<String, Any>()
                    userManager.getSubscribers().subscribe { subscribers ->
                        subscribers.forEach {
                            val key = usersRef.child("${it.key}/notification").push().key
                            userUpdates["/${it.key}/notification/$key"] = word
                        }
                        val key = usersRef.child("$userID/words").push().key
                        userUpdates["/$userID/words/$key"] = word
                        usersRef.updateChildren(userUpdates)
                            .addOnSuccessListener {
                                Thread(Runnable {
                                    Application.wordDao.updateWord(word.copy(state = NOTHING))
                                }).start()
                            }
                    }
                }
                UPDATE -> {
                    usersRef
                        .child(userID)
                        .child("words")
                        .child(word.key)
                        .setValue(word)
                        .addOnSuccessListener {
                            Thread(Runnable {
                                Application.wordDao.updateWord(word.copy(state = NOTHING))
                            }).start()
                        }
                }
                REMOVE -> {
                    usersRef
                        .child(userID)
                        .child("notification")
                        .child(word.key)
                        .setValue(null)
                        .addOnSuccessListener {
                            Thread(Runnable {
                                Application.wordDao.removeWord(word)
                            }).start()
                        }
                }
                NOTHING -> {
                }
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

}