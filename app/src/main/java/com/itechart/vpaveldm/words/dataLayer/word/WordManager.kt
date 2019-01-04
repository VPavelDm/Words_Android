package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.itechart.vpaveldm.words.core.extension.ChildEventListener as DelegateChildEventListener
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

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

    fun getWordsToStudy(): Single<List<Word>> = Single.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val wordsRef = usersRef.child(userID).child(userWords)
        wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

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