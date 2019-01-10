package com.itechart.vpaveldm.words.dataLayer.user

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User(
        @get:Exclude
        var key: String = "",
        val name: String = "",
        val subscriptions: ArrayList<String> = arrayListOf()
)