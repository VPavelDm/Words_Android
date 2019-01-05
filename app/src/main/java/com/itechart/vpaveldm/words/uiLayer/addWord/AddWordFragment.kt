package com.itechart.vpaveldm.words.uiLayer.addWord

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.addWord.AddWordViewModel
import com.itechart.vpaveldm.words.adapterLayer.addWord.IAddWordDelegate
import com.itechart.vpaveldm.words.adapterLayer.chooseTranslate.TranslateViewModel
import com.itechart.vpaveldm.words.databinding.FragmentAddWordBinding
import com.itechart.vpaveldm.words.uiLayer.chooseTranslate.ChooseTranslateFragment
import java.lang.ref.WeakReference

class AddWordFragment : Fragment(), IAddWordDelegate {

    private lateinit var translateViewModel: TranslateViewModel
    private lateinit var viewModel: AddWordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentAddWordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this).get(AddWordViewModel::class.java)
        translateViewModel = ViewModelProviders.of(activity!!).get(TranslateViewModel::class.java)
        translateViewModel.translateProvider.observe(this, Observer { translate ->
            translate?.let {
                viewModel.translateObservable.set(it)
                translateViewModel.translateProvider.value = null
            }
        })
        viewModel.delegate = WeakReference(this)
        binding.handler = viewModel
        return binding.root
    }

    override fun translatesLoaded(translates: List<String>) {
        val chooseTranslateFragment = ChooseTranslateFragment.create(translates)
        chooseTranslateFragment.show(fragmentManager, null)
    }

}