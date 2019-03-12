package com.itechart.vpaveldm.words.domainLayer

import com.itechart.vpaveldm.words.dataLayer.authorization.AuthorizationModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthorizationInteractor {

    private val authModel = AuthorizationModel()

    fun signIn(login: String, password: String): Completable =
        authModel.signIn(login, password)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun signUp(login: String, password: String, confirmPassword: String): Completable =
        authModel.signUp(login, password, confirmPassword)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun checkVerification(): Completable =
        authModel.checkVerification()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun changeEmail(newEmail: String, password: String): Completable =
        authModel.changeEmail(newEmail, password)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun verifyEmail(): Completable =
            authModel.verifyEmail()
}