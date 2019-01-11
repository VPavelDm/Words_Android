package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.itechart.vpaveldm.words.dataLayer.translate.YandexTranslateManager
import com.itechart.vpaveldm.words.dataLayer.user.UserManager
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class AddWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val userManager = UserManager()
    private val yandexTranslateManager = YandexTranslateManager()
    private val disposables = CompositeDisposable()

    val translateObservable = ObservableField<String>("")
    val wordObservable = ObservableField<String>("")
    val transcriptionObservable = ObservableField<String>("")

    val addWordProgressBarVisible = ObservableBoolean(false)
    val translateProgressBarVisible = ObservableBoolean(false)
    val transcriptionProgressBarVisible = ObservableBoolean(false)

    var delegate: WeakReference<IAddWordDelegate>? = null

    fun addWord() {
        val newWord = Word(
            word = wordObservable.get() ?: "",
            translate = translateObservable.get() ?: "",
            transcription = transcriptionObservable.get() ?: ""
        )
        getSubscribers { subscribers, error ->
            if (error != null) {
                // TODO: Add error handling
            } else {
                val disposable = wordManager.addWord(newWord, subscribers!!)
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
        }
    }

    fun loadTranslate() {
        val word = wordObservable.get() ?: ""
        val disposable = yandexTranslateManager.getTranslate(word)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { translateProgressBarVisible.set(true) }
            .doOnEvent { _, _ -> translateProgressBarVisible.set(false) }
            .subscribe({ translates ->
                delegate?.get()?.translatesLoaded(translates)
            }, {
                //TODO: Handle error
            })
        disposables.add(disposable)
    }

    fun loadTranscription() {
        val word = wordObservable.get() ?: ""
        val disposable = yandexTranslateManager.getTranscription(word)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { transcriptionProgressBarVisible.set(true) }
            .doOnEvent { _, _ -> transcriptionProgressBarVisible.set(false) }
            .subscribe({ transcription ->
                transcriptionObservable.set(transcription)
            }, {
                //TODO: Handle error
            })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun getSubscribers(callback: (List<String>?, Throwable?) -> Unit) {
        val disposable = userManager.getSubscribers()
            .subscribe({ subscribers ->
                callback(subscribers, null)
            }, { error ->
                callback(null, error)
            })
        disposables.add(disposable)
    }

}

interface IAddWordDelegate {
    fun translatesLoaded(translates: List<String>)
}