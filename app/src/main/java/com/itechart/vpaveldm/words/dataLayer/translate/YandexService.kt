package com.itechart.vpaveldm.words.dataLayer.translate

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface YandexService {

    @GET("dicservice.json/lookup?lang=en-ru")
    fun getWordDescription(@Query("text") word: String, @Query("key") apiKey: String): Call<WordDescriptionResponse>
}

class WordDescriptionResponse(@SerializedName("def") val objects: List<WordDescription>)

class WordDescription(@SerializedName("ts") val transcription: String, @SerializedName("tr") val translates: List<WordTranslate>)

class WordTranslate(val text: String, @SerializedName("syn") val synonyms: List<Synonym>?)

class Synonym(val text: String)