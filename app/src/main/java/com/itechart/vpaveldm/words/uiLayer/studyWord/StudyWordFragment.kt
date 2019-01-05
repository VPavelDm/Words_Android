package com.itechart.vpaveldm.words.uiLayer.studyWord

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.studyWord.IStudyWordDelegate
import com.itechart.vpaveldm.words.adapterLayer.studyWord.StudyWordViewModel
import com.itechart.vpaveldm.words.core.interfaces.AnimationListener
import com.itechart.vpaveldm.words.databinding.FragmentStudyWordBinding
import java.lang.ref.WeakReference

class StudyWordFragment : Fragment(), IStudyWordDelegate, Animation.AnimationListener by AnimationListener() {

    private lateinit var binding: FragmentStudyWordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStudyWordBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProviders.of(this).get(StudyWordViewModel::class.java)
        viewModel.delegate = WeakReference(this)
        binding.handler = viewModel
        return binding.root
    }

    override fun cardClicked(callback: () -> Unit) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_in_out)
        animation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationRepeat(animation: Animation?) {
                super.onAnimationRepeat(animation)
                callback()
            }
        })
        binding.wordCard.startAnimation(animation)
    }

    override fun showTranslateClicked(callback: () -> Unit) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate_card)
        animation.setAnimationListener(object : AnimationListener() {
            override fun onAnimationRepeat(animation: Animation?) {
                super.onAnimationRepeat(animation)
                callback()
            }
        })
        binding.wordCard.startAnimation(animation)
    }

}