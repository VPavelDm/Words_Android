package com.itechart.vpaveldm.words.adapterLayer.word

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.dataLayer.word.Word

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordHolder>() {

    val words: List<Word> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent)
        return WordHolder(view)
    }

    override fun getItemCount(): Int = words.size

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
    }


    class WordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(word: Word) {

        }
    }

}