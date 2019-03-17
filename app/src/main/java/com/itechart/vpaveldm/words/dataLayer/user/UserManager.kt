package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.core.error.UserSubscribeError
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordSection.NOTIFICATION
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.Single.create

class UserManager {

    fun getUsers(name: String): Single<List<User>> = create { subscriber ->
        userNameAndID().first?.let { userName ->
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
                            val userModel = snapshot.getValue(UserModel::class.java) ?: continue
                            snapshot.key?.let { key ->
                                val me = Subscriber(userName)
                                val isSubscriber = userModel.subscribers.containsValue(me)
                                val user = User(key, userModel.name, isSubscriber)
                                users += user
                            } ?: continue
                        }
                        subscriber.onSuccess(users)
                    }

                })
        } ?: subscriber.tryOnError(UserError())
    }

    fun getSubscribers(): Single<List<User>> = Single.create { subscriber ->
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        userNameAndID().second?.let { userID ->
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
        } ?: subscriber.tryOnError(UserError())
    }

    /**
     * Send request to get subscribers that has the word in notification section
     */
    fun getSubscribersWithWordInNotification(word: Word): Single<List<User>> = create { subscriber ->
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        val section = NOTIFICATION.description()
        userNameAndID().second?.let { userID ->
            userRef
                .orderByChild("$section/${word.key}/key")
                .equalTo(word.key)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        subscriber.tryOnError(error.toException())
                    }

                    override fun onDataChange(usersSnapshot: DataSnapshot) {
                        val subscribers = usersSnapshot.children.mapNotNull { convert(it) }
                        subscriber.onSuccess(subscribers)
                    }

                })
        } ?: subscriber.tryOnError(UserError())
    }

    // Add me to user's subscribers
    fun subscribe(user: User): Completable = Completable.create { subscriber ->
        val (userName, userID) = userNameAndID()
        if (userName != null && userID != null) {
            if (user.name == userName) {
                subscriber.tryOnError(UserSubscribeError(userName))
                return@create
            }
            val userUpdates = HashMap<String, Any?>()
            if (user.isSubscriber) {
                userUpdates["/${user.key}/subscribers/$userID"] = User(name = userName)
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
        } else subscriber.tryOnError(UserError())
    }

    fun saveUser(nickname: String): Completable = Completable.create { subscriber ->
        userNameAndID().second?.let { userID ->
            val user = User(name = nickname)
            val userDBRef = FirebaseDatabase.getInstance().getReference("users")
            userDBRef
                .child(userID)
                .setValue(user)
                .addOnSuccessListener { subscriber.onComplete() }
                .addOnFailureListener { error -> subscriber.tryOnError(error) }
        } ?: subscriber.tryOnError(UserError())
    }

    fun userNameAndID(): Pair<String?, String?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName
        val userID = currentUser?.uid
        return userName to userID
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