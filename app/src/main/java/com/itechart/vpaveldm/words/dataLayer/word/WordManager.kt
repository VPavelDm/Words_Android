package com.itechart.vpaveldm.words.dataLayer.word

import android.arch.paging.DataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
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
        return Application.wordDao.getWords(userName)
    }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val userName = FirebaseAuth.getInstance().currentUser!!.displayName!!
        val words = listOf(word.copy(state = WordState.ADD, owner = userName))
        Application.wordDao.addWords(words)
        subscriber.onComplete()
    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        Application.wordDao.updateWord(word.copy(state = WordState.UPDATE))
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

    private fun convert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let {
            word.key = it
            return word
        } ?: return null
    }

}