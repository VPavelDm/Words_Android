package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StudyWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()
    private var words: List<Word> = arrayListOf()

    val progressBarVisible = ObservableBoolean(false)
    val word = ObservableField<String>("")

    fun getWordsToStudy() {
        val disposable = wordManager.getWordsToStudy()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { _, _ -> progressBarVisible.set(false) }
                .subscribe({ words ->
                    initWords(words)
                }, { _ ->
                    // TODO: Add error handling
                })
        disposables.add(disposable)
    }

    private fun initWords(words: List<Word>) {
        if (words.isNotEmpty()) {
            this.words = words
            word.set(words.first().word)
        }
    }

}