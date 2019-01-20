package com.itechart.vpaveldm.words.dataLayer.word

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "wordExamples")
@IgnoreExtraProperties
data class Example(
    @PrimaryKey(autoGenerate = true)
    @get:Exclude
    var id: Long = 0,
    var text: String = "",
    var translate: String = "",
    @get:Exclude
    var wordId: String = ""
)