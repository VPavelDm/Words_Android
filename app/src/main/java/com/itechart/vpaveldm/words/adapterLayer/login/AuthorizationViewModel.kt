package com.itechart.vpaveldm.words.adapterLayer.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import androidx.navigation.NavController
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.core.extension.disposedBy
import com.itechart.vpaveldm.words.domainLayer.AuthorizationInteractor
import io.reactivex.disposables.CompositeDisposable

class AuthorizationViewModel(private val navController: NavController) : ViewModel() {

    val error = ObservableField<String>()
    val progressBarVisible = ObservableBoolean(false)
    private val disposables = CompositeDisposable()
    private val authorizationInteractor = AuthorizationInteractor()

    fun signIn(login: String, password: String) {
        authorizationInteractor.signIn(login, password)
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({
                navController.popBackStack()
            }, { error ->
                this.error.set(error.localizedMessage)
            })
            .disposedBy(disposables)
    }

    fun signUp(login: String, password: String, confirmPassword: String) {
        authorizationInteractor.signUp(login, password, confirmPassword)
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({
                navController.navigate(R.id.action_registrationFragment_to_verificationFragment)
            }, {
                error.set(it.localizedMessage)
            })
            .disposedBy(disposables)
    }

    fun sendVerificationMail() {
        authorizationInteractor.verifyEmail()
            .subscribe({}, { error ->
                this.error.set(error.localizedMessage)
            })
            .disposedBy(disposables)
    }

    fun checkVerification() {
        authorizationInteractor.checkVerification()
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({
                navController.popBackStack(R.id.wordFragment, false)
            }, { error ->
                this.error.set(error.localizedMessage)
            })
            .disposedBy(disposables)
    }

    fun changeEmailAddress(newLogin: String, password: String) {
        authorizationInteractor.changeEmail(newLogin, password)
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({ navController.popBackStack() }, { error ->
                this.error.set(error.localizedMessage)
            })
            .disposedBy(disposables)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val navController: NavController) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthorizationViewModel(navController) as T
    }

}