package com.itechart.vpaveldm.words.adapterLayer.chooseTranslate

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class TranslateViewModel : ViewModel() {

    val translateProvider = MutableLiveData<String>()

}