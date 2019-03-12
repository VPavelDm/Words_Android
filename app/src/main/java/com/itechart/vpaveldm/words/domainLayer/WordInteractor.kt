package com.itechart.vpaveldm.words.domainLayer

import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordInteractor {

    fun getSubscriptionsWords(): Single<List<Word>> = WordManager.getSubscriptionsWords()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())

}