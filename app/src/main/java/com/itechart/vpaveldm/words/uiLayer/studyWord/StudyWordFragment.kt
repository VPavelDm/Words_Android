package com.itechart.vpaveldm.words.uiLayer.studyWord

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.studyWord.StudyWordViewModel
import com.itechart.vpaveldm.words.databinding.FragmentStudyWordBinding

class StudyWordFragment : Fragment() {

    private lateinit var binding: FragmentStudyWordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStudyWordBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProviders.of(this).get(StudyWordViewModel::class.java)
        binding.handler = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.handler?.getWordsToStudy()
    }

}