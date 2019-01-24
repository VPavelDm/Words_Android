package com.itechart.vpaveldm.words.adapterLayer.searchViewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.dataLayer.user.User
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel : ViewModel() {

    private val userManager = UserManager()
    private val disposables = CompositeDisposable()
    private val usersProvider = MutableLiveData<List<User>>()
    private val errorProvider = MutableLiveData<String>()

    val progressBarVisible = ObservableBoolean(false)
    val users: LiveData<List<User>> = usersProvider
    val error: LiveData<String> = errorProvider

    fun findUsers(name: String) {
        val disposable = userManager.getUsers(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { _, _ -> progressBarVisible.set(false) }
                .subscribe({ users ->
                    usersProvider.value = users
                }, { _ ->
                    // TODO: Add error handling
                })
        disposables.add(disposable)
    }

    fun subscribe(user: User, callback: () -> Unit) {
        val disposable = userManager.subscribe(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { progressBarVisible.set(false) }
                .subscribe({ callback() }, { error ->
                    errorProvider.value = error.localizedMessage
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}