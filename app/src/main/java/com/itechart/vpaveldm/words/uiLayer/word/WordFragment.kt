package com.itechart.vpaveldm.words.uiLayer.word

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.word.WordAdapter
import com.itechart.vpaveldm.words.databinding.FragmentWordBinding

class WordFragment: Fragment() {

    private lateinit var listener: IAuthorization

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is IAuthorization) {
            error("Activity does not conform ILoginFragmentListener interface")
        }
        listener = activity as IAuthorization
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentWordBinding.inflate(inflater, container, false)
        binding.wordRecyclerView.apply {
            adapter = WordAdapter()
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
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