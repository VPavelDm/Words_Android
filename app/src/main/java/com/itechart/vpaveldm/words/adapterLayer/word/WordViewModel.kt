package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.core.extension.disposedBy
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.domainLayer.WordInteractor
import io.reactivex.disposables.CompositeDisposable

class WordViewModel : ViewModel() {

    private val wordsProvider = MutableLiveData<List<Word>>()
    private val disposables = CompositeDisposable()
    private val interactor = WordInteractor()

    val words: LiveData<List<Word>> = wordsProvider
    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)

    fun sendRequestToGetSubscriptionsWords() {
        interactor.getSubscriptionsWords()
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { _, _ -> progressBarVisible.set(false) }
            .subscribe({ words ->
                if (words.isEmpty())
                    emptyWordsTextViewVisible.set(true)
                else
                    wordsProvider.value = words
            }, {
                // TODO: Add error handling
            })
            .disposedBy(disposables)
    }

    fun removeWord(word: Word, toAdd: Boolean) {
        interactor.removeWordFromNotification(word, toAdd)
            .subscribe({}, { _ ->
                // TODO: Add error handling
            })
            .disposedBy(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}