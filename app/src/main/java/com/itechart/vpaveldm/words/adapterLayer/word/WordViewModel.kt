package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.Application
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()

    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)

    init {
        wordManager.getWordCount()
            .filter { it == 0L }
            .doOnSuccess {
                // This will be called if there aren't any words
                progressBarVisible.set(false)
                emptyWordsTextViewVisible.set(true)
            }
            .subscribe()
        val disposable = wordManager.subscribeOnWordUpdating()
            .observeOn(Schedulers.newThread())
            .subscribe { word ->
                Application.wordDao.addWord(word)
            }
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}