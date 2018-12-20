package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.disposables.CompositeDisposable

class WordViewModel: ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()
    private val wordsObservable = MutableLiveData<Word>()

    val progressBarVisible = ObservableBoolean(false)
    val words: LiveData<Word> = wordsObservable

    fun subscribeOnUpdate() {
        val disposable = wordManager.subscribeOnWordUpdating()
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEach { progressBarVisible.set(false) }
                .subscribe { wordsObservable.value = it }
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}