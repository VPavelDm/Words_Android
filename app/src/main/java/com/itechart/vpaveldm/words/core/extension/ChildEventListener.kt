package com.itechart.vpaveldm.words.core.extension

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface ChildEventListener: ChildEventListener {
    override fun onCancelled(p0: DatabaseError) {
    }

    override fun onChildMoved(p0: DataSnapshot, prevName: String?) {
    }

    override fun onChildChanged(wordSnapshot: DataSnapshot, prevName: String?) {
    }

    override fun onChildAdded(snapshot: DataSnapshot, prevName: String?) {
    }

    override fun onChildRemoved(p0: DataSnapshot) {
    }
}