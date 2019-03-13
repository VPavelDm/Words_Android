package com.itechart.vpaveldm.words.adapterLayer.profile

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardItemTouchHelperAdapter
import kotlinx.android.synthetic.main.recycler_item_word.view.*

class ProfileAdapter(private val listener: IProfileAdapter, private var words: List<Word> = listOf()) :
    RecyclerView.Adapter<ProfileAdapter.WordViewHolder>(),
    CardItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int = words.size

    override fun onItemSwipedToLeft(position: Int) {
        listener.wordCardSwipedToRemove(words[position])
    }

    override fun onItemSwipedToRight(position: Int) {
        throw UnsupportedOperationException("Данная операция не поддерживается")
    }

    fun swapData(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    inner class WordViewHolder(itemView: View) : ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val word = this@ProfileAdapter.words[adapterPosition]
                listener.wordCardClicked(word)
            }
        }

        fun bind(word: Word) {
            itemView.nicknameTV.text = word.owner
            itemView.wordTV.text = word.word
            itemView.translateTV.text = word.translate
            itemView.transcriptionTV.text = word.transcription
        }
    }

}

interface IProfileAdapter {
    fun wordCardClicked(word: Word)
    fun wordCardSwipedToRemove(word: Word)
}