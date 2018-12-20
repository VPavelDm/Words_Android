package com.itechart.vpaveldm.words.dataLayer.translate

import com.itechart.vpaveldm.words.Application
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YandexTranslateManager {

    fun getTranslate(word: String): Single<String> = Single.create { subscriber ->
        Application.service.getTranslate(word, apiKey = Application.yandexApiKey)
                .enqueue(object : Callback<Translate> {

                    override fun onFailure(call: Call<Translate>, t: Throwable) {
                        subscriber.onError(t)
                    }

                    override fun onResponse(call: Call<Translate>, response: Response<Translate>) {
                        if (response.isSuccessful) {
                            val translate = response.body()?.text?.first() ?: ""
                            subscriber.onSuccess(translate)
                        }
                    }

                })
    }

}