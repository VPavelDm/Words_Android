package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import androidx.navigation.NavController
import com.itechart.vpaveldm.words.core.extension.disposedBy
import com.itechart.vpaveldm.words.dataLayer.translate.YandexTranslateManager
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.domainLayer.WordInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class AddWordViewModel(private val navController: NavController) : ViewModel() {

    private val yandexTranslateManager = YandexTranslateManager()
    private val disposables = CompositeDisposable()
    private val interactor = WordInteractor()

    val translateObservable = ObservableField<String>("")
    val wordObservable = ObservableField<String>("")
    val transcriptionObservable = ObservableField<String>("")

    val addWordProgressBarVisible = ObservableBoolean(false)
    val translateProgressBarVisible = ObservableBoolean(false)
    val transcriptionProgressBarVisible = ObservableBoolean(false)

    var delegate: WeakReference<IAddWordDelegate>? = null

    fun addWord(examples: List<Example>, callback: () -> Unit) {
        val newWord = Word(
            word = wordObservable.get() ?: "",
            translate = translateObservable.get() ?: "",
            transcription = transcriptionObservable.get() ?: "",
            examples = examples
        )
        interactor.addWord(newWord)
            .doOnSubscribe { addWordProgressBarVisible.set(true) }
            .doOnEvent { addWordProgressBarVisible.set(false) }
            .subscribe {
                wordObservable.set("")
                translateObservable.set("")
                transcriptionObservable.set("")
                callback()
            }
            .disposedBy(disposables)
    }

    fun editWord(word: Word) {
        val newWord = word.copy(
            word = wordObservable.get() ?: "",
            translate = translateObservable.get() ?: "",
            transcription = transcriptionObservable.get() ?: ""
        )
        interactor.editWord(newWord)
            .doOnSubscribe { addWordProgressBarVisible.set(true) }
            .doOnEvent { addWordProgressBarVisible.set(false) }
            .subscribe {
                navController.popBackStack()
            }
            .disposedBy(disposables)
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

}

interface IAddWordDelegate {
    fun translatesLoaded(translates: List<String>)
}