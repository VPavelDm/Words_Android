package com.itechart.vpaveldm.words.dataLayer.authorization

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.itechart.vpaveldm.words.core.UserError
import com.itechart.vpaveldm.words.core.error.UserVerificationError
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
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
                    .addOnSuccessListener {
                        val currentUser = auth.currentUser ?: return@addOnSuccessListener
                        val nickname = currentUser.email ?: return@addOnSuccessListener
                        val userProfile = UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build()

                        currentUser.updateProfile(userProfile)
                        val userManager = UserManager()
                        // TODO: There are an error. If account is created but database isn't filled. Fix it
                        userManager.saveUser(nickname)
                            .subscribe({ subscriber.onComplete() }, { error ->
                                subscriber.tryOnError(error)
                            })
                    }
                    .addOnFailureListener { subscriber.tryOnError(it) }
            }
        }

    fun verifyEmail(user: FirebaseUser): Completable = Completable.create { subscriber ->
        user.sendEmailVerification()
            .addOnSuccessListener { subscriber.onComplete() }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun checkVerification(user: FirebaseUser): Completable = Completable.create { subscriber ->
        user.reload()
            .addOnSuccessListener {
                if (user.isEmailVerified)
                    subscriber.onComplete()
                else
                    subscriber.onError(UserVerificationError())
            }
            .addOnFailureListener { subscriber.tryOnError(it) }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun changeEmail(newEmail: String, password: String): Completable = Completable.create { subscriber ->
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val oldEmail = user.email
            if (oldEmail != null) {
                val credential = EmailAuthProvider.getCredential(oldEmail, password)
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user
                            .updateEmail(newEmail)
                            .addOnSuccessListener { subscriber.onComplete() }
                            .addOnFailureListener { error -> subscriber.tryOnError(error) }
                    }
                    .addOnFailureListener { error -> subscriber.tryOnError(error) }
            } else {
                subscriber.tryOnError(UserError())
            }
            return@let
        } ?: subscriber.tryOnError(UserError())
    }

}