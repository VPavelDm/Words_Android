package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable
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
                        val users = arrayListOf<User>()
                        for (snapshot in usersSnapshot.children) {
                            val user = snapshot.getValue(User::class.java) ?: continue
                            snapshot.key?.let { key ->
                                user.key = key
                                users += user
                            } ?: continue
                        }
                        subscriber.onSuccess(users)
                    }

                })
    }

    // Add user to my subscriptions
    fun subscribe(user: User): Completable = Completable.create { subscriber ->
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        userRef
                .child(userID)
                .child("subscriptions")
                .push()
                .setValue(user)
                .addOnSuccessListener { subscriber.onComplete() }
                .addOnFailureListener { subscriber.tryOnError(it) }
    }

}