package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.itechart.vpaveldm.words.core.extension.moveToEndAt
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.*

class StudyWordViewModel : ViewModel() {

    private val wordManager = WordManager()
    private val disposables = CompositeDisposable()
    private var words: ArrayList<Word> = arrayListOf()

    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(false)
    val updateWordProgressBarVisible = ObservableBoolean(false)
    val transcriptionVisible = ObservableBoolean(true)
    val word = ObservableField<String>("")
    val transcription = ObservableField<String>("")

    var delegate: WeakReference<IStudyWordDelegate>? = null

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
        val word = words.removeAt(0)
        val newWord = word.copy(
                date = word.date.plusDays(word.count + 1),
                count = word.count + 1
        )
        updateWord(newWord) {
            updateCard(false)
        }
    }

    fun doNotKnowWord() {
        val word = words.moveToEndAt(index = 0)
        val newWord = word.copy(
                date = Date().resetTime(),
                count = 0
        )
        updateWord(newWord) {
            updateCard(false)
        }
    }

    fun showAnswer() {
        word.set(words.first().translate)
        transcriptionVisible.set(false)
    }

    private fun initWords(words: List<Word>) {
        if (words.isNotEmpty()) {
            this.words = ArrayList(words)
        }
        updateCard(true)
    }

    private fun updateWord(word: Word, callback: () -> Unit) {
        val disposable = wordManager.updateWord(word)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { updateWordProgressBarVisible.set(true) }
                .doOnEvent { updateWordProgressBarVisible.set(false) }
                .subscribe({
                    callback()
                }, { _ ->
                    //TODO: Handle error
                })
        disposables.add(disposable)
    }

    private fun updateCard(isFirst: Boolean) {
        if (words.size > 0) {
            if (!isFirst) delegate?.get()?.cardClicked {
                word.set(words.first().word)
                transcription.set(words.first().transcription)
                transcriptionVisible.set(true)
            } else {
                word.set(words.first().word)
                transcription.set(words.first().transcription)
            }
        } else {
            emptyWordsTextViewVisible.set(true)
        }
    }

}

interface IStudyWordDelegate {
    fun cardClicked(callback: () -> Unit)
}