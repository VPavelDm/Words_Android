package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.word.Word
import kotlinx.android.synthetic.main.recycler_item_word.view.*


class WordAdapter : PagedListAdapter<Word, WordAdapter.WordHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent, false)
        return WordHolder(view)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        val word = getItem(position) ?: return
        holder.bind(word)
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

private val DIFF_UTIL = object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldWord: Word, newWord: Word): Boolean {
        Log.i("myAppTAG", "oldWord = ${oldWord.key}, newWord = ${newWord.key}")
        return oldWord.key == newWord.key
    }

    override fun areContentsTheSame(oldWord: Word, newWord: Word): Boolean {
        return oldWord == newWord
    }

}