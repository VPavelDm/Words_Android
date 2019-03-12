package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.util.Log
import com.itechart.vpaveldm.words.core.extension.disposedBy
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import com.itechart.vpaveldm.words.domainLayer.WordInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WordViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    private val interactor = WordInteractor()

    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)

    fun getSubscriptionsWords(completion: (words: List<Word>) -> Unit) {
        interactor.getSubscriptionsWords()
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { _, _ -> progressBarVisible.set(false) }
            .subscribe({ words ->
                if (words.isEmpty())
                    emptyWordsTextViewVisible.set(true)
                else
                    completion(words)
            }, { error ->
                Log.i("myAppTAGError", "error = ${error.localizedMessage}")
            })
            .disposedBy(disposables)
    }

    fun removeWord(word: Word, toAdd: Boolean) {
        WordManager.removeWordFromNotification(word, toAdd)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}