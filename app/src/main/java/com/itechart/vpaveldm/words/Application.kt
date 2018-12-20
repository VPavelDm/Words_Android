package com.itechart.vpaveldm.words

import android.app.Application
import com.itechart.vpaveldm.words.dataLayer.translate.YandexService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/api/v1.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(YandexService::class.java)
        yandexApiKey = getString(R.string.yandex_api_key)
    }

    companion object {
        lateinit var service: YandexService
            private set
        lateinit var yandexApiKey: String
            private set
    }

}