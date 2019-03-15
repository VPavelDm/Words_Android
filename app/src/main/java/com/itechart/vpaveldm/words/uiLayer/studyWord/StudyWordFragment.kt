package com.itechart.vpaveldm.words.uiLayer.studyWord

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.studyWord.IStudyWordDelegate
import com.itechart.vpaveldm.words.adapterLayer.studyWord.StudyWordAdapter
import com.itechart.vpaveldm.words.adapterLayer.studyWord.StudyWordViewModel
import com.itechart.vpaveldm.words.core.interfaces.AnimationListener
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.FragmentStudyWordBinding
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardItemDivider
import java.lang.ref.WeakReference

class StudyWordFragment : Fragment(), IStudyWordDelegate, Animation.AnimationListener by AnimationListener() {

    private lateinit var binding: FragmentStudyWordBinding
    private lateinit var adapter: StudyWordAdapter
    private lateinit var viewModel: StudyWordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStudyWordBinding.inflate(inflater, container, false)
        setupViewModel()
        adapter = StudyWordAdapter(viewModel)
        binding.handler = viewModel
        setupRecyclerView()
        return binding.root
    }

    override fun startNextCardAnimation(callback: () -> Unit) {
        val animation = prepareAnimation(callback)
        binding.wordCard.startAnimation(animation)
    }

    override fun showWord(word: Word) {
        adapter.swapWord(word)
    }

    override fun showTranslate() {
        adapter.showAnswer()
    }

    private fun setupRecyclerView() {
        binding.studyWordRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@StudyWordFragment.adapter
            addItemDecoration(CardItemDivider(context))
            setOnTouchListener { _, event ->
                if (!viewModel.translateVisible.get() && event.action == MotionEvent.ACTION_UP) {
                    viewModel.showAnswer()
                    return@setOnTouchListener true
                } else return@setOnTouchListener false
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(StudyWordViewModel::class.java)
        viewModel.delegate = WeakReference(this)
    }

    private fun prepareAnimation(completion: () -> Unit): Animation {
        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_in_out)
        animation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationRepeat(animation: Animation?) {
                super.onAnimationRepeat(animation)
                completion()
            }
        })
        return animation
    }

}