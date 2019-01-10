package com.itechart.vpaveldm.words.dataLayer.user

class User(
    val name: String = "",
    subscribers: List<User> = listOf(),
    subscriptions: List<User> = listOf()
)