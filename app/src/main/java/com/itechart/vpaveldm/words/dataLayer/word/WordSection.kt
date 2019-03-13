package com.itechart.vpaveldm.words.dataLayer.word

enum class WordSection {
    NOTIFICATION, WORDS;

    fun description(): String = this.name.toLowerCase()
}