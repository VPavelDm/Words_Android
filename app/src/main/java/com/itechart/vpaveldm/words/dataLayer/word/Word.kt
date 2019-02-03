package com.itechart.vpaveldm.words.dataLayer.word

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.itechart.vpaveldm.words.core.extension.timeIntervalSince1970
import java.io.Serializable
import java.util.*

@Entity(tableName = "words")
@IgnoreExtraProperties
data class Word(
    @PrimaryKey
    @get:Exclude
    var key: String = "",
    var word: String = "",
    var transcription: String = "",
    var translate: String = "",
    var date: Long = 0L,
    var count: Int = 0,
    var owner: String = "",
    @Ignore
    var examples: List<Example> = listOf(),
    @get:Exclude
    var account: String = "",
    var createDate: Long = Date().timeIntervalSince1970
) : Serializable