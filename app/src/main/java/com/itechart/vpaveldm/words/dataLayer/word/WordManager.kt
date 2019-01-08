package com.itechart.vpaveldm.words.dataLayer.word

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itechart.vpaveldm.words.core.extension.resetTime
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener

class WordManager {

    private var listener: ChildEventListener? = null
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userWords = "words"

    fun subscribeOnWordUpdating(): Observable<Word> = Observable.create<Word> { subscriber ->
        removeListener()
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val wordsRef = usersRef.child(userID).child(userWords)
        listener = wordsRef.addChildEventListener(object : DelegateChildEventListener() {

            override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                val word = convert(snapshot) ?: return
                subscriber.onNext(word)
            }

        })
    }.doOnDispose { removeListener() }

    fun addWord(word: Word): Completable = Completable.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        usersRef
            .child(userID)
            .child(userWords)
            .push()
            .setValue(word)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }

    }

    fun updateWord(word: Word): Completable = Completable.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        usersRef
            .child(userID)
            .child(userWords)
            .child(word.key)
            .setValue(word)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun getWordCount(): Single<Long> = Single.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val wordsRef = usersRef.child(userID).child(userWords)
        wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                subscriber.onSuccess(snapshot.childrenCount)
            }
        })
    }

    fun getWords(fromKey: String? = null, count: Int): Single<List<Word>> = Single.create { subscriber ->
        Log.i("myAppTAG", "fromKey = $fromKey")
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        usersRef
            .child(userID)
            .child(userWords)
            .orderByKey()
            .startAt(fromKey ?: "")
            .limitToFirst(count)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    subscriber.onError(error.toException())
                }

                override fun onDataChange(wordsSnapshot: DataSnapshot) {
                    val words = wordsSnapshot.children.mapNotNull { convert(it) }.filter { it.key != fromKey }
                    Log.i("myAppTAG", "word count = ${words.size}")
                    subscriber.onSuccess(words)
                }

            })
    }

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val currentDate = Date().resetTime().time.toDouble()
        usersRef
            .child(userID)
            .child(userWords)
            .orderByChild("date/time")
            .endAt(currentDate)
            .limitToFirst(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //TODO: Add error handling
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val words = arrayListOf<Word>()
                    for (wordSnapshot in snapshot.children) {
                        val word = convert(wordSnapshot) ?: continue
                        words += word
                    }
                    subscriber.onSuccess(words)
                }

            })
    }

    private fun removeListener() {
        listener?.let { usersRef.removeEventListener(it) }
    }

    private fun convert(snapshot: DataSnapshot): Word? {
        val word = snapshot.getValue(Word::class.java) ?: return null
        snapshot.key?.let {
            word.key = it
            return word
        } ?: return null
    }

}