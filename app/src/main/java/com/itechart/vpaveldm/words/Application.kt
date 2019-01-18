package com.itechart.vpaveldm.words

import android.app.Application
import android.arch.persistence.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.itechart.vpaveldm.words.dataLayer.local.WordDao
import com.itechart.vpaveldm.words.dataLayer.local.WordDatabase
import com.itechart.vpaveldm.words.dataLayer.translate.YandexService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        initDictionaryService()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        dictionaryApiKey = getString(R.string.yandex_dictionary_api_key)
        wordDao = Room.databaseBuilder(applicationContext, WordDatabase::class.java, "database").build().wordDao()
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
        lateinit var wordDao: WordDao
    }

}