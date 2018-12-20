package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.itechart.vpaveldm.words.dataLayer.translate.YandexTranslateManager
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val yandexTranslateManager = YandexTranslateManager()
    private val disposables = CompositeDisposable()

    val translateObservable = ObservableField<String>("")
    val wordObservable = ObservableField<String>("")
    val transcriptionObservable = ObservableField<String>("")

    val addWordProgressBarVisible = ObservableBoolean(false)
    val translateProgressBarVisible = ObservableBoolean(false)
    val transcriptionProgressBarVisible = ObservableBoolean(false)

    fun addWord() {
        val newWord = Word(
                word = wordObservable.get() ?: "",
                translate = translateObservable.get() ?: "",
                transcription = transcriptionObservable.get() ?: "")
        val disposable = wordManager.addWord(newWord)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { addWordProgressBarVisible.set(true) }
                .doOnEvent { addWordProgressBarVisible.set(false) }
                .subscribe {
                    wordObservable.set("")
                    translateObservable.set("")
                    transcriptionObservable.set("")
                }
        disposables.add(disposable)
    }

    fun loadTranslate() {
        val word = wordObservable.get() ?: ""
        val disposable = yandexTranslateManager.getTranslate(word)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { translateProgressBarVisible.set(true) }
                .doOnEvent { _, _ -> translateProgressBarVisible.set(false) }
                .subscribe({ translate ->
                    translateObservable.set(translate)
                }, {
                    //TODO: Handle error
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}