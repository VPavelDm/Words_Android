package com.itechart.vpaveldm.words.dataLayer.word

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Example(
    var text: String = "",
    var translate: String = ""
): Serializable