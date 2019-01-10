package com.itechart.vpaveldm.words.uiLayer.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.searchViewModel.SearchViewModel
import com.itechart.vpaveldm.words.adapterLayer.searchViewModel.UserAdapter
import com.itechart.vpaveldm.words.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        binding.handler = viewModel
        viewModel.users.observe(this, Observer { users ->
            users?.let {
                binding.searchRV.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@SearchFragment.context)
                    adapter = UserAdapter(it)
                    addItemDecoration(ItemDivider(context))
                }
            }
        })

        return binding.root
    }

}