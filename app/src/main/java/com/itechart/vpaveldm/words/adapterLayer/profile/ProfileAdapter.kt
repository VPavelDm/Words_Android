package com.itechart.vpaveldm.words.adapterLayer.profile

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.word.Word
import kotlinx.android.synthetic.main.recycler_item_word.view.*

class ProfileAdapter(private val listener: IProfileAdapter) : PagedListAdapter<Word, ProfileAdapter.WordViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position) ?: return
        holder.bind(word)
    }

    inner class WordViewHolder(itemView: View) : ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val word = this@ProfileAdapter.getItem(adapterPosition) ?: return@setOnClickListener
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

private val DIFF_UTIL = object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldWord: Word, newWord: Word): Boolean {
        return oldWord.key == newWord.key
    }

    override fun areContentsTheSame(oldWord: Word, newWord: Word): Boolean {
        return oldWord == newWord
    }

}

interface IProfileAdapter {
    fun wordCardClicked(word: Word)
}