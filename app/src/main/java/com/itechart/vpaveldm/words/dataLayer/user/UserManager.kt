package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single

class UserManager {

    fun getUsers(name: String): Single<List<User>> = Single.create { subscriber ->
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        userRef
                .orderByChild("name")
                .equalTo(name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        subscriber.tryOnError(error.toException())
                    }

                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        val users = usersSnapshot.children.mapNotNull { it.getValue(User::class.java) }
                        subscriber.onSuccess(users)
                    }

                })
    }

}