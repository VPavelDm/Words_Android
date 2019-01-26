package com.itechart.vpaveldm.words.adapterLayer.profile

import com.itechart.vpaveldm.words.adapterLayer.wordCard.CardAdapter
import com.itechart.vpaveldm.words.dataLayer.word.Word

class WordAdapter(private val word: Word) : CardAdapter() {
    override fun getItemCount(): Int = word.examples.size + 2

    override fun isEnglishCard(): Boolean = true

    override fun isFullCardVisible(): Boolean = true

    override fun getWord(): Word = word
}