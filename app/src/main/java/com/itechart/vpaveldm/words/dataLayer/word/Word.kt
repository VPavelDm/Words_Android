package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Word(
        @get:Exclude
        var key: String = "",
        val word: String = "",
        val transcription: String = "",
        val translate: String= "",
        val date: Date = Date(),
        val count: Int = 0
)