package com.itechart.vpaveldm.words.uiLayer.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.profile.IProfileAdapter
import com.itechart.vpaveldm.words.adapterLayer.profile.ProfileAdapter
import com.itechart.vpaveldm.words.adapterLayer.profile.ProfileViewModel
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.FragmentProfileBinding
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardDialogFragment

class ProfileFragment : Fragment(), IProfileAdapter {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private val adapter = ProfileAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        fetchWords()
        viewModel.sendRequestToGetSubscriptionsWords()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        setupRecyclerView()
        attachItemTouchHelper()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.exit -> {
                viewModel.logOut()
                activity!!.finish()
                startActivity(activity!!.intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun wordCardClicked(word: Word) {
        val fragment = CardDialogFragment.create(word)
        fragment.show(activity!!.supportFragmentManager, null)
    }

    override fun wordCardSwipedToRemove(word: Word) {
        viewModel.removeWord(word)
    }

    private fun fetchWords() {
        viewModel.words.observe(this, Observer { adapter.swapData(words = it ?: return@Observer) })
    }

    private fun setupRecyclerView() {
        binding.wordRecyclerView.apply {
            adapter = this@ProfileFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun attachItemTouchHelper() {
        val touchHelper = ItemTouchHelper(WordItemTouchCallback(activity!!.applicationContext, adapter))
        touchHelper.attachToRecyclerView(binding.wordRecyclerView)
    }

}