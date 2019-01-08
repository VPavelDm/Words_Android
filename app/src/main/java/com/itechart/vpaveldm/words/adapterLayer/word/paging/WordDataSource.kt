package com.itechart.vpaveldm.words.adapterLayer.word.paging

import android.annotation.SuppressLint
import android.arch.paging.PositionalDataSource
import android.util.Log
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.dataLayer.word.WordManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordPositionalDataSource : PositionalDataSource<Word>() {

    private val wordManager = WordManager()
    private var lastWord: Word? = null

    @SuppressLint("CheckResult")
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Word>) {
        Log.i("myAppTAG", "startPosition = ${params.startPosition}, count = ${params.loadSize}")
        wordManager.getWords(fromWord = lastWord, count = params.loadSize)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ words ->
                if (words.isNotEmpty()) {
                    lastWord = words.last()
                }
                callback.onResult(words)
            }, { _ ->
                //TODO: Add error handling
            })
    }

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Word>) {
        Log.i("myAppTAG", "startPosition = ${params.requestedStartPosition}, count = ${params.requestedLoadSize}")
        wordManager.getWords(count = params.requestedLoadSize)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ words ->
                if (words.isNotEmpty()) {
                    lastWord = words.last()
                }
                callback.onResult(words, 0)
            }, { _ ->
                //TODO: Add error handling
            })
    }

}