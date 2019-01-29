package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.core.extension.moveToEndAt
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class StudyWordViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    private var words: ArrayList<Word> = arrayListOf()

    val progressBarVisible = ObservableBoolean(false)
    val emptyWordsTextViewVisible = ObservableBoolean(true)
    val translateVisible = ObservableBoolean(false)

    var delegate: WeakReference<IStudyWordDelegate>? = null

    init {
        getWords(doOnSuccess = { updateUI() })
    }

    fun knowWord() {
        val word = words.removeAt(0)
        val newWord = word.copy(
                date = word.date.plusDays(word.count + 1),
                count = word.count + 1
        )
        updateWordInDatabase(newWord, doOnSuccess = {
            if (words.isNotEmpty()) {
                progressBarVisible.set(false)
                delegate?.get()?.startNextCardAnimation { updateUI() }
            } else {
                getWords(doOnSuccess = { delegate?.get()?.startNextCardAnimation { updateUI() } })
            }
        })
    }

    fun doNotKnowWord() {
        val word = words.moveToEndAt(index = 0)
        val newWord = word.copy(
                date = Date(),
                count = 0
        )
        updateWordInDatabase(newWord, doOnSuccess = {
            progressBarVisible.set(false)
            delegate?.get()?.startNextCardAnimation { updateUI() }
        })
    }

    fun showAnswer() {
        // You can show translate just once
        if (!translateVisible.get()) {
            translateVisible.set(true)
            delegate?.get()?.showTranslate()
        }
    }

    private fun updateUI() {
        if (words.isNotEmpty()) {
            emptyWordsTextViewVisible.set(false)
            delegate?.get()?.showWord(words.first())
        } else {
            emptyWordsTextViewVisible.set(true)
        }
    }

    private fun updateWordInDatabase(word: Word, doOnSuccess: () -> Unit) {
        val disposable = WordManager.updateWord(word)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnError { progressBarVisible.set(false) }
                .subscribe({
                    // Note that progress bar is still visible.. You have to hide it by yourself
                    doOnSuccess()
                }, {
                    //TODO: Handle error
                })
        disposables.add(disposable)
    }

    private fun getWords(doOnSuccess: () -> Unit) {
        val disposable = WordManager.getWordsToStudy()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarVisible.set(true) }
                .doOnEvent { _, _ -> progressBarVisible.set(false) }
                .subscribe({ words ->
                    this.words = ArrayList(words)
                    doOnSuccess()
                }, {
                    // TODO: Add error handling
                })
        disposables.add(disposable)
    }

}

interface IStudyWordDelegate {
    fun startNextCardAnimation(callback: () -> Unit)
    fun showTranslate()
    fun showWord(word: Word)
}