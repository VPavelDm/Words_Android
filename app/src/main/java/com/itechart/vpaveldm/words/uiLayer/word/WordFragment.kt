package com.itechart.vpaveldm.words.uiLayer.word

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        fetchWords()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.handler = viewModel
        setupRecyclerView()
        attachItemTouchHelper()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.sendRequestToGetSubscriptionsWords()
    }

    override fun onResume() {
        super.onResume()
        listener.authorized()
    }

    override fun onItemSwiped(word: Word, toAdd: Boolean) {
        viewModel.removeWord(word, toAdd)
    }

    private fun fetchWords() {
        viewModel.words.observe(this, Observer { adapter.swapData(words = it ?: return@Observer) })
    }

    private fun setupRecyclerView() {
        binding.wordRecyclerView.apply {
            adapter = this@WordFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun attachItemTouchHelper() {
        val touchHelper = ItemTouchHelper(WordItemTouchCallback(activity!!.applicationContext, adapter))
        touchHelper.attachToRecyclerView(binding.wordRecyclerView)
    }

}

interface IAuthorization {
    fun authorized()
}