package com.itechart.vpaveldm.words.uiLayer.word

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.word.WordAdapter
import com.itechart.vpaveldm.words.adapterLayer.word.WordViewModel
import com.itechart.vpaveldm.words.databinding.FragmentWordBinding

class WordFragment : Fragment() {

    private lateinit var listener: IAuthorization
    private lateinit var binding: FragmentWordBinding
    private lateinit var viewModel: WordViewModel
    private var adapter = WordAdapter.create()

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
        viewModel.words.observe(this, Observer { word ->
            word?.let {
                val dataSource = adapter.currentList?.dataSource
                dataSource?.invalidate()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.wordRecyclerView.apply {
            adapter = this@WordFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        binding.handler = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        listener.authorized()
    }

}

interface IAuthorization {
    fun authorized()
}