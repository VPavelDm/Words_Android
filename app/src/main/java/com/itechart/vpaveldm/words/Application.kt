package com.itechart.vpaveldm.words

import android.app.Application
import com.itechart.vpaveldm.words.dataLayer.translate.YandexService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        initTranslateService()
        initTranscriptionService()
        translateApiKey = getString(R.string.yandex_translate_api_key)
        transcriptionApiKey = getString(R.string.yandex_dictionary_api_key)
    }

    private fun initTranslateService() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/api/v1.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        translateService = retrofit.create(YandexService::class.java)
    }

    private fun initTranscriptionService() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://dictionary.yandex.net/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        transcriptionService = retrofit.create(YandexService::class.java)
    }

    companion object {
        lateinit var translateService: YandexService
            private set
        lateinit var transcriptionService: YandexService
            private set
        lateinit var translateApiKey: String
            private set
        lateinit var transcriptionApiKey: String
            private set
    }

}