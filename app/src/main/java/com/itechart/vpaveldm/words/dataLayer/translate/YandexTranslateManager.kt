package com.itechart.vpaveldm.words.dataLayer.translate

import com.itechart.vpaveldm.words.Application
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YandexTranslateManager {

    fun getTranslate(word: String): Single<List<String>> = Single.create { subscriber ->
        Application.dictionaryService.getWordDescription(word, apiKey = Application.dictionaryApiKey)
                .enqueue(object : Callback<WordDescriptionResponse> {
                    override fun onFailure(call: Call<WordDescriptionResponse>, t: Throwable) {
                        subscriber.tryOnError(t)
                    }

                    override fun onResponse(call: Call<WordDescriptionResponse>, response: Response<WordDescriptionResponse>) {
                        if (response.isSuccessful) {
                            val wordDescriptions = response.body()?.objects ?: return
                            val translates = wordDescriptions.flatMap { wordDescription ->
                                wordDescription.translates.map { translate ->
                                    translate.text
                                }.union(wordDescription.translates.flatMap { translate ->
                                    translate.synonyms?.map { it.text } ?: listOf()
                                })
                            }
                            subscriber.onSuccess(translates)
                        } else {
                            subscriber.onError(Error(response.message()))
                        }
                    }

                })
    }

    fun getTranscription(word: String): Single<String> = Single.create { subscriber ->
        Application.dictionaryService.getWordDescription(word, apiKey = Application.dictionaryApiKey)
                .enqueue(object : Callback<WordDescriptionResponse> {

                    override fun onFailure(call: Call<WordDescriptionResponse>, t: Throwable) {
                        subscriber.onError(t)
                    }

                    override fun onResponse(call: Call<WordDescriptionResponse>, response: Response<WordDescriptionResponse>) {
                        if (response.isSuccessful) {
                            val transcriptions = response.body()?.objects ?: return
                            if (transcriptions.isNotEmpty()) {
                                val transcription = transcriptions.first().transcription
                                subscriber.onSuccess("[$transcription]")
                            } else {
                                subscriber.onSuccess("[]")
                            }
                        } else {
                            subscriber.onError(Error(response.message()))
                        }
                    }

                })
    }

}