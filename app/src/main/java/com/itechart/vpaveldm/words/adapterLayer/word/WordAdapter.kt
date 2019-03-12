package com.itechart.vpaveldm.words.adapterLayer.word

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardItemTouchHelperAdapter
import kotlinx.android.synthetic.main.recycler_item_word.view.*


class WordAdapter(private val listener: IWordAdapter, private var words: List<Word> = listOf()) :
    RecyclerView.Adapter<WordAdapter.WordHolder>(),
    CardItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent, false)
        return WordHolder(view)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int = words.size

    override fun onItemSwipedToRight(position: Int) {
        listener.onItemSwiped(words[position], toAdd = true)
    }

    override fun onItemSwipedToLeft(position: Int) {
        listener.onItemSwiped(words[position], toAdd = false)
    }

    fun swapData(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    class WordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(word: Word) {
            itemView.wordTV.text = word.word
            itemView.translateTV.text = word.translate
            itemView.transcriptionTV.text = word.transcription
            itemView.nicknameTV.text = word.owner
        }
    }

}

interface IWordAdapter {
    fun onItemSwiped(word: Word, toAdd: Boolean)
}