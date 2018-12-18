package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable

class WordManager {

    private var listener: ValueEventListener? = null
    private val wordsRef = FirebaseDatabase.getInstance().getReference("user").child("words")

    fun subscribeOnWordUpdating(): Observable<List<Word>> = Observable.create<List<Word>> { subscriber ->
        removeListener()
        listener = wordsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                subscriber.tryOnError(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val words = snapshot.children.mapNotNull { it.getValue(Word::class.java) }
                subscriber.onNext(words)
            }

        })
    }.doOnDispose { removeListener() }

    private fun removeListener() {
        listener?.let { wordsRef.removeEventListener(it) }
    }

}