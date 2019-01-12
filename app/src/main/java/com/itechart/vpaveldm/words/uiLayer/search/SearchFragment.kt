package com.itechart.vpaveldm.words.uiLayer.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Icon
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.SearchView
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.searchViewModel.ISubscribeUser
import com.itechart.vpaveldm.words.adapterLayer.searchViewModel.SearchViewModel
import com.itechart.vpaveldm.words.adapterLayer.searchViewModel.UserAdapter
import com.itechart.vpaveldm.words.core.extension.toast
import com.itechart.vpaveldm.words.dataLayer.user.User
import com.itechart.vpaveldm.words.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), ISubscribeUser {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        binding.handler = viewModel
        val greenIcon = Icon.createWithResource(context, R.drawable.ic_track_green)
        val blackIcon = Icon.createWithResource(context, R.drawable.ic_track)
        viewModel.users.observe(this, Observer { users ->
            users?.let {
                binding.searchRV.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@SearchFragment.context)
                    adapter = UserAdapter(it, this@SearchFragment, greenIcon, blackIcon)
                    addItemDecoration(ItemDivider(context))
                }
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search, menu)
        val searchItem = menu?.findItem(R.id.search_users)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.findUsers(query) }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun subscribe(user: User) {
        viewModel.subscribe(user) {
            if (user.isSubscriber) context!!.toast("Вы подписаны") else context!!.toast("Вы отписаны")
        }
    }

}