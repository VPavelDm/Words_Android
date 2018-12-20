package com.itechart.vpaveldm.words.dataLayer.translate

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexService {
    @GET("tr.json/translate?lang=en-ru")
    fun getTranslate(@Query("text") word: String, @Query("key") apiKey: String): Call<Translate>
}

class Translate(val text: List<String>)