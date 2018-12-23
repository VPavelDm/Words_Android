package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable

class WordManager {

    private var listener: ChildEventListener? = null
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val userWords = "words"

    fun subscribeOnWordUpdating(): Observable<Word> = Observable.create<Word> { subscriber ->
        removeListener()
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val wordsRef = usersRef.child(userID).child(userWords)
        listener = wordsRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
                val word = snapshot.getValue(Word::class.java) ?: return
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

    private fun removeListener() {
        listener?.let { usersRef.removeEventListener(it) }
    }

}