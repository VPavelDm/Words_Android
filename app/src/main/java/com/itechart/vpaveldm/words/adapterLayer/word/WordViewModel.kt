package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WordViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

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
    }

    fun getSubscriptionsWords(): DataSource.Factory<Int, Word> {
        return WordManager.getSubscriptionsWords()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}