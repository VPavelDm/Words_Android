package com.itechart.vpaveldm.words.adapterLayer.studyWord

import com.itechart.vpaveldm.words.adapterLayer.wordCard.CardAdapter
import com.itechart.vpaveldm.words.dataLayer.word.Word

class StudyWordAdapter(private val viewModel: StudyWordViewModel) : CardAdapter() {

    private var word: Word = Word()

    override fun getItemCount(): Int = if (viewModel.translateVisible.get()) word.examples.size + 2 else 1

    override fun isEnglishCard(): Boolean = word.count % 2 == 0

    override fun isFullCardVisible(): Boolean = viewModel.translateVisible.get()

    override fun getWord(): Word = word

    fun swapWord(word: Word) {
        this.word = word
        notifyDataSetChanged()
    }

    fun showAnswer() {
        notifyItemRangeInserted(1, word.examples.size + 1)
    }

}