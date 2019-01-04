package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.itechart.vpaveldm.words.core.extension.resetTime
import java.util.*

@IgnoreExtraProperties
data class Word(
        @get:Exclude
        var key: String = "",
        val word: String = "",
        val transcription: String = "",
        val translate: String= "",
        val date: Date = Date().resetTime(),
        val count: Int = 0
)