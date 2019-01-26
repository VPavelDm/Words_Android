package com.itechart.vpaveldm.words.adapterLayer.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.dataLayer.authorization.AuthorizationModel
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel : ViewModel() {

    private val wordsProvider = MutableLiveData<DataSource.Factory<Int, Word>>()
    private val disposables = CompositeDisposable()
    private val authManager = AuthorizationModel()

    val words: LiveData<DataSource.Factory<Int, Word>> = wordsProvider
    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)

    init {
        WordManager.getWordCount()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it == 0 }
                .doOnSuccess {
                    // This will be called if there aren't any words
                    progressBarVisible.set(false)
                    emptyWordsTextViewVisible.set(true)
                }
                .subscribe()
        val disposable = WordManager.getWords()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { _, _ -> progressBarVisible.set(false) }
                .subscribe({ wordsProvider.value = it }, { _ ->
                    // TODO: Add error handling
                })
        disposables.add(disposable)
    }

    fun removeWord(word: Word) {
        WordManager.removeWordFromProfile(word)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun logOut() {
        authManager.logOut()
    }

}