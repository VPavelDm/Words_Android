package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.disposables.CompositeDisposable

class AddWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()

    val progressBarVisible = ObservableBoolean(false)

    fun addWord(word: String, translate: String, transcription: String) {
        val newWord = Word(word, translate, transcription)
        val disposable = wordManager.addWord(newWord)
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { progressBarVisible.set(false) }
                .subscribe()
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}