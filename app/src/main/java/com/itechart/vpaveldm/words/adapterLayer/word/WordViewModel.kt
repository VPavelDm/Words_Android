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
        addUntrackedWords()
    }

    // This method is used to get notification about adding word and cache this word
    private fun addNewWordNotification() {
        val disposable = wordManager.subscribeOnWordUpdating()
            .observeOn(Schedulers.newThread())
            .subscribe { word ->
                Application.wordDao.addWord(word)
            }
        disposables.add(disposable)
    }

    // This method is used to cache words that were added after user closed app
    private fun addUntrackedWords() {
        val untrackedWordsDisposable = wordManager.getWords()
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe({ words ->
                Application.wordDao.addWords(words)
                addNewWordNotification()
            }, { _ ->
                // TODO: Add error handling
            })
        disposables.add(untrackedWordsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}