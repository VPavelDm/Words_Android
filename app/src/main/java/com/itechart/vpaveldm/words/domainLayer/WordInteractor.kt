package com.itechart.vpaveldm.words.domainLayer

import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import com.itechart.vpaveldm.words.dataLayer.word.WordSection
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordInteractor {

    fun getSubscriptionsWords(): Single<List<Word>> =
        WordManager.getWords(WordSection.NOTIFICATION)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun removeWordFromNotification(word: Word, toAdd: Boolean): Completable =
        WordManager.removeWordFromNotification(word, toAdd)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun removeWordFromProfile(word: Word): Completable =
        WordManager.removeWordFromProfile(word)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateWord(word: Word): Completable =
        WordManager.updateWord(word)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun getWords(): Single<List<Word>> =
        WordManager.getWords(WordSection.WORDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun getWordsToStudy(): Single<List<Word>> =
        WordManager.getWordsToStudy()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

}