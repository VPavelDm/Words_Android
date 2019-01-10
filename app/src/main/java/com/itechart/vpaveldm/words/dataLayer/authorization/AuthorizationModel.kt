package com.itechart.vpaveldm.words.dataLayer.authorization

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.itechart.vpaveldm.words.dataLayer.user.User
import io.reactivex.Completable

class AuthorizationModel {

    fun signIn(login: String, password: String): Completable = Completable.create { subscriber ->
        if (login.isEmpty() || password.isEmpty()) {
            subscriber.tryOnError(IllegalArgumentException("Введите логин и пароль"))
        } else {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(login, password)
                .addOnSuccessListener { subscriber.onComplete() }
                .addOnFailureListener { subscriber.tryOnError(it) }
        }
    }

    fun signUp(login: String, password: String, confirmPassword: String, nickname: String): Completable =
        Completable.create { subscriber ->
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                subscriber.tryOnError(IllegalArgumentException("Введите логин, пароль и подтверждение пароля"))
            } else if (password != confirmPassword) {
                subscriber.tryOnError(IllegalArgumentException("Пароли не совпадают"))
            } else {
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(login, password)
                    .addOnSuccessListener {
                        val userProfile = UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build()

                        val currentUser = auth.currentUser ?: return@addOnSuccessListener

                        currentUser.updateProfile(userProfile)
                        val user = User(name = nickname)
                        // TODO: There are an error. If account is created but database isn't filled. Fix it
                        val userDBRef = FirebaseDatabase.getInstance().getReference("users")
                        userDBRef
                            .child(currentUser.uid)
                            .setValue(user)
                            .addOnSuccessListener { subscriber.onComplete() }
                            .addOnFailureListener { error -> subscriber.tryOnError(error) }

                    }
                    .addOnFailureListener { subscriber.tryOnError(it) }
            }
        }

}