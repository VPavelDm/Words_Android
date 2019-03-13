package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.itechart.vpaveldm.words.core.extension.disposedBy
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.timeIntervalSince1970
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.domainLayer.WordInteractor
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class StudyWordViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    private var words: ArrayList<Word> = arrayListOf()
    private val interactor = WordInteractor()

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
            date = plusDays(word.count + 1).timeIntervalSince1970,
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
        val word = words.removeAt(0)
        val newWord = word.copy(
            date = Date().timeIntervalSince1970,
            count = 0
        )
        words.add(newWord)
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
        translateVisible.set(false)
        if (words.isNotEmpty()) {
            emptyWordsTextViewVisible.set(false)
            delegate?.get()?.showWord(words.first())
        } else {
            emptyWordsTextViewVisible.set(true)
        }
    }

    private fun updateWordInDatabase(word: Word, doOnSuccess: () -> Unit) {
        interactor.updateWord(word)
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnError { progressBarVisible.set(false) }
            .subscribe({
                // Note that progress bar is still visible.. You have to hide it by yourself
                doOnSuccess()
            }, {
                //TODO: Handle error
            })
            .disposedBy(disposables)
    }

    private fun getWords(doOnSuccess: () -> Unit) {
        interactor.getWordsToStudy()
            .doOnSubscribe { progressBarVisible.set(true) }
            .doOnEvent { _, _ -> progressBarVisible.set(false) }
            .subscribe({ words ->
                this.words = ArrayList(words)
                doOnSuccess()
            }, {
                // TODO: Add error handling
            })
            .disposedBy(disposables)
    }

}

interface IStudyWordDelegate {
    fun startNextCardAnimation(callback: () -> Unit)
    fun showTranslate()
    fun showWord(word: Word)
}