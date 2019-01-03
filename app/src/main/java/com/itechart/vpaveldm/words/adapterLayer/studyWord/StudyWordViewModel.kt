package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.itechart.vpaveldm.words.core.extension.moveToEndAt
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class StudyWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()
    private var words: ArrayList<Word> = arrayListOf()

    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)
    val word = ObservableField<String>("")

    init {
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

    fun knowWord() {
        words.removeAt(0)
        updateCard()
    }

    fun doNotKnowWord() {
        words.moveToEndAt(index = 0)
        updateCard()
    }

    private fun initWords(words: List<Word>) {
        if (words.isNotEmpty()) {
            this.words = ArrayList(words)
            updateCard()
        }
    }

    private fun updateCard() {
        if (words.size > 0) {
            word.set(words.first().word)
        } else {
            emptyWordsTextViewVisible.set(true)
        }
    }

}