package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.database.IgnoreExtraProperties
import com.itechart.vpaveldm.words.core.extension.timeIntervalSince1970
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Word(
    var key: String = "",
    var word: String = "",
    var transcription: String = "",
    var translate: String = "",
    var date: Long = 0L,
    var count: Int = 0,
    var owner: String = "",
    var examples: List<Example> = listOf(),
    var createDate: Long = Date().timeIntervalSince1970
) : Serializable