package com.itechart.vpaveldm.words.dataLayer.translate

import com.itechart.vpaveldm.words.Application
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YandexTranslateManager {

    fun getTranslate(word: String): Single<List<String>> = Single.create { subscriber ->
        Application.translateService.getTranslate(word, apiKey = Application.translateApiKey)
                .enqueue(object : Callback<Translate> {

                    override fun onFailure(call: Call<Translate>, t: Throwable) {
                        subscriber.onError(t)
                    }

                    override fun onResponse(call: Call<Translate>, response: Response<Translate>) {
                        if (response.isSuccessful) {
                            val translate = response.body()?.text?.first() ?: ""
                            subscriber.onSuccess(listOf(translate))
                        } else {
                            subscriber.onError(Error(response.message()))
                        }
                    }

                })
    }

    fun getTranscription(word: String): Single<String> = Single.create { subscriber ->
        Application.transcriptionService.getTranscription(word, apiKey = Application.transcriptionApiKey)
                .enqueue(object : Callback<TranscriptionResponse> {

                    override fun onFailure(call: Call<TranscriptionResponse>, t: Throwable) {
                        subscriber.onError(t)
                    }

                    override fun onResponse(call: Call<TranscriptionResponse>, response: Response<TranscriptionResponse>) {
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