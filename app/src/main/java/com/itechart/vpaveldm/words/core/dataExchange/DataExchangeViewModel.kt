package com.itechart.vpaveldm.words.core.dataExchange

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class DataExchangeViewModel<T> : ViewModel() {

    private val dataProvider = MutableLiveData<T>()
    val data: LiveData<T> = dataProvider

    fun put(data: T) {
        dataProvider.value = data
    }

    fun clear() {
        dataProvider.value = null
    }

}