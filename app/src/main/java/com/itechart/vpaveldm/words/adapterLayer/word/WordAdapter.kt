package com.itechart.vpaveldm.words.adapterLayer.word

import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.os.Handler
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.word.paging.WordPositionalDataSource
import com.itechart.vpaveldm.words.dataLayer.word.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.recycler_item_word.view.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.os.Looper


class WordAdapter() : PagedListAdapter<Word, WordAdapter.WordHolder>(DIFF_UTIL) {

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
        }
    }

    companion object {
        fun create(): WordAdapter {
            val dataSource = WordPositionalDataSource()
            val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build()
            val wordsList = PagedList.Builder<Int, Word>(dataSource, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setNotifyExecutor(MainThreadExecutor())
                .build()
            val adapter = WordAdapter()
            adapter.submitList(wordsList)
            return adapter
        }
    }

}

private class MainThreadExecutor : Executor {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable?) {
        mHandler.post(command);
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