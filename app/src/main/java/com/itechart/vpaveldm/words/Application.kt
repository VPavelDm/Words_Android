package com.itechart.vpaveldm.words

import android.app.Application
import com.itechart.vpaveldm.words.dataLayer.translate.YandexService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        initDictionaryService()
        dictionaryApiKey = getString(R.string.yandex_dictionary_api_key)
    }

    private fun initDictionaryService() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://dictionary.yandex.net/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        dictionaryService = retrofit.create(YandexService::class.java)
    }

    companion object {
        lateinit var dictionaryService: YandexService
            private set
        lateinit var dictionaryApiKey: String
            private set
    }

}