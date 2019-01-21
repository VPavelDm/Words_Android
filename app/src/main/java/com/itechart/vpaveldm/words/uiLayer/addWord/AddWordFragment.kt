package com.itechart.vpaveldm.words.uiLayer.addWord

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.addWord.AddWordAdapter
import com.itechart.vpaveldm.words.adapterLayer.addWord.AddWordViewModel
import com.itechart.vpaveldm.words.adapterLayer.addWord.IAddWordDelegate
import com.itechart.vpaveldm.words.adapterLayer.chooseTranslate.TranslateViewModel
import com.itechart.vpaveldm.words.databinding.FragmentAddWordBinding
import com.itechart.vpaveldm.words.uiLayer.chooseTranslate.ChooseTranslateFragment
import java.lang.ref.WeakReference

class AddWordFragment : Fragment(), IAddWordDelegate {

    private lateinit var translateViewModel: TranslateViewModel
    private lateinit var viewModel: AddWordViewModel
    private lateinit var binding: FragmentAddWordBinding
    private lateinit var adapter: AddWordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddWordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this).get(AddWordViewModel::class.java)
        adapter = AddWordAdapter(activity!!.applicationContext, viewModel)
        translateViewModel = ViewModelProviders.of(activity!!).get(TranslateViewModel::class.java)
        translateViewModel.translateProvider.observe(this, Observer { translate ->
            translate?.let {
                viewModel.translateObservable.set(it)
                translateViewModel.translateProvider.value = null
            }
        })
        viewModel.delegate = WeakReference(this)
        binding.handler = adapter
        binding.addWordRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@AddWordFragment.adapter
            itemAnimator = WordDefaultItemAnimator()
        }
        val touchHelper = ItemTouchHelper(ExampleTouchCallback(adapter))
        touchHelper.attachToRecyclerView(binding.addWordRV)
        adapter.registerAdapterDataObserver(listener)
        return binding.root
    }

    override fun translatesLoaded(translates: List<String>) {
        val chooseTranslateFragment = ChooseTranslateFragment.create(translates)
        chooseTranslateFragment.show(fragmentManager, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.unregisterAdapterDataObserver(listener)
    }

    private val listener = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val position = adapter.itemCount - 1
            binding.addWordRV.scrollToPosition(position)
        }
    }

}