package com.itechart.vpaveldm.words.dataLayer.authorization

import com.google.firebase.auth.FirebaseAuth
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

    fun signUp(login: String, password: String, confirmPassword: String): Completable =
        Completable.create { subscriber ->
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                subscriber.tryOnError(IllegalArgumentException("Введите логин, пароль и подтверждение пароля"))
            } else if (password != confirmPassword) {
                subscriber.tryOnError(IllegalArgumentException("Пароли не совпадают"))
            } else {
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(login, password)
                    .addOnSuccessListener { subscriber.onComplete() }
                    .addOnFailureListener { subscriber.tryOnError(it) }
            }
        }

}