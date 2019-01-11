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

    fun getSubscribers(): Single<List<User>> = Single.create { subscriber ->
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        userRef
            .child(userID)
            .child("subscribers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    subscriber.tryOnError(error.toException())
                }

                override fun onDataChange(usersSnapshot: DataSnapshot) {
                    val subscribers = usersSnapshot.children.mapNotNull { convert(it) }
                    subscriber.onSuccess(subscribers)
                }

            })
    }

    // Add me to user's subscribers
    fun subscribe(user: User): Completable = Completable.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val auth = FirebaseAuth.getInstance()
        val myNickname = auth.currentUser?.displayName ?: return@create
        val me = User(name = myNickname)
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        userRef
            .child(user.key)
            .child("subscribers")
            .child(userID)
            .setValue(me)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun saveUser(nickname: String): Completable = Completable.create { subscriber ->
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return@create
        val user = User(name = nickname)
        val userDBRef = FirebaseDatabase.getInstance().getReference("users")
        userDBRef
            .child(userID)
            .setValue(user)
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { error -> subscriber.tryOnError(error) }
    }

    private fun convert(snapshot: DataSnapshot): User? {
        val user = snapshot.getValue(User::class.java) ?: return null
        user.key = snapshot.key ?: return null
        return user
    }

}