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

    val progressBarVisible = ObservableBoolean(false)
    val users: LiveData<List<User>> = usersProvider

    fun findUsers(name: String) {
        val disposable = userManager.getUsers(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    usersProvider.value = users
                }, { _ ->
                    // TODO: Add error handling
                })
        disposables.add(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}