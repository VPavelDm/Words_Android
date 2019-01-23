package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                        val myNickname = FirebaseAuth.getInstance().currentUser?.displayName
                                ?: return
                        val users = arrayListOf<User>()
                        for (snapshot in usersSnapshot.children) {
                            val userModel = snapshot.getValue(UserModel::class.java) ?: continue
                            snapshot.key?.let { key ->
                                val me = Subscriber(myNickname)
                                val isSubscriber = userModel.subscribers.containsValue(me)
                                val user = User(key, userModel.name, isSubscriber)
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
        val userUpdates = HashMap<String, Any?>()
        if (user.isSubscriber) {
            userUpdates["/${user.key}/subscribers/$userID"] = User(name = myNickname)
            userUpdates["/$userID/subscriptions/${user.key}"] = user
        } else {
            userUpdates["/${user.key}/subscribers/$userID"] = null
            userUpdates["/$userID/subscriptions/${user.key}"] = null
        }
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        userRef
                .updateChildren(userUpdates)
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

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun convert(snapshot: DataSnapshot): User? {
        val user = snapshot.getValue(User::class.java) ?: return null
        user.key = snapshot.key ?: return null
        return user
    }

}

private data class Subscriber(val name: String = "")
private data class UserModel(
        val name: String = "",
        val subscribers: HashMap<String, Subscriber> = hashMapOf()
)