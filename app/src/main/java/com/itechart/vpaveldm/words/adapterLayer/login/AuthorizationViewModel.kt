package com.itechart.vpaveldm.words.adapterLayer.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import androidx.navigation.NavController
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.authorization.AuthorizationModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthorizationViewModel(private val navController: NavController) : ViewModel() {

    val error = ObservableField<String>()
    val progressBarVisible = ObservableBoolean(false)
    private val authModel = AuthorizationModel()
    private val disposables = CompositeDisposable()
    private var nickname: String = ""

    fun signIn(login: String, password: String) {
        val disposable = authModel.signIn(login, password)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({
                navController.popBackStack()
            }, { error ->
                this.error.set(error.localizedMessage)
            })
        disposables.add(disposable)
    }

    fun signUp(nickname: String) {
        this.nickname = nickname
        navController.navigate(R.id.action_registrationFragment_to_registrationFragment2)
    }

    fun signUp(login: String, password: String, confirmPassword: String) {
        val disposable = authModel.signUp(login, password, confirmPassword, nickname)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { progressBarVisible.set(false) }
            .subscribe({
                navController.popBackStack(R.id.loginFragment, true)
            }, {
                error.set(it.localizedMessage)
            })
        disposables.add(disposable)
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