package com.itechart.vpaveldm.words.uiLayer.word

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.word.IWordAdapter
import com.itechart.vpaveldm.words.adapterLayer.word.WordAdapter
import com.itechart.vpaveldm.words.adapterLayer.word.WordViewModel
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.FragmentWordBinding
import java.util.concurrent.Executors

class WordFragment : Fragment(), IWordAdapter {

    private lateinit var listener: IAuthorization
    private lateinit var binding: FragmentWordBinding
    private lateinit var viewModel: WordViewModel
    private val adapter = WordAdapter(this)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is IAuthorization) {
            error("Activity does not conform ILoginFragmentListener interface")
        }
        listener = activity as IAuthorization
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.wordRecyclerView.apply {
            adapter = this@WordFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        val touchHelper = ItemTouchHelper(WordItemTouchCallback(activity!!.applicationContext, adapter))
        touchHelper.attachToRecyclerView(binding.wordRecyclerView)
        binding.handler = viewModel
        initPageList()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        listener.authorized()
    }

    override fun onItemSwiped(word: Word, toAdd: Boolean) {
        viewModel.removeWord(word, toAdd)
    }

    private fun initPageList() {
        viewModel.getSubscriptionsWords { sourceFactory ->
            val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build()
            val pagedListData = LivePagedListBuilder<Int, Word>(sourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build()
            pagedListData.observe(this, Observer { pagedList ->
                pagedList?.let {
                    viewModel.emptyWordsTextViewVisible.set(it.size == 0)
                    adapter.submitList(it)
                }
            })
        }
    }

}

interface IAuthorization {
    fun authorized()
}