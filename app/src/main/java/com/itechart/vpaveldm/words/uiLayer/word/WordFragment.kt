package com.itechart.vpaveldm.words.uiLayer.word

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.word.WordAdapter
import com.itechart.vpaveldm.words.adapterLayer.word.WordViewModel
import com.itechart.vpaveldm.words.adapterLayer.word.paging.WordSourceFactory
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.FragmentWordBinding
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.concurrent.Executors

class WordFragment : Fragment() {

    private lateinit var listener: IAuthorization
    private lateinit var binding: FragmentWordBinding
    private lateinit var viewModel: WordViewModel
    private var adapter = WordAdapter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is IAuthorization) {
            error("Activity does not conform ILoginFragmentListener interface")
        }
        listener = activity as IAuthorization
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.wordRecyclerView.apply {
            adapter = this@WordFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        binding.handler = viewModel
        initPageList()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        listener.authorized()
    }

    private fun initPageList() {
        val sourceFactory = WordSourceFactory()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()
        val pagedListData = LivePagedListBuilder<Int, Word>(sourceFactory, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
        pagedListData.observe(this, Observer { pagedList ->
            pagedList?.let {
                Log.i("myAppTAG", "pagedList size = ${pagedList.size}")
                adapter.submitList(it)
            }
        })
    }

}

interface IAuthorization {
    fun authorized()
}