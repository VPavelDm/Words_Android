package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User(
        @get:Exclude
        var key: String = "",
        var name: String = "",
        @get:Exclude
        var isSubscriber: Boolean = false
)