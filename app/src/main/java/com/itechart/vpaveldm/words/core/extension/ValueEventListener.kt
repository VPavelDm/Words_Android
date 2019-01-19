package com.itechart.vpaveldm.words.core.extension

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

interface ValueEventListener : ValueEventListener {
    override fun onCancelled(error: DatabaseError) {

    }

    override fun onDataChange(snapshot: DataSnapshot) {

    }
}