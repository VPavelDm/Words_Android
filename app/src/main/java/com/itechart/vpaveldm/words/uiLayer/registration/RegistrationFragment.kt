package com.itechart.vpaveldm.words.uiLayer.registration

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.databinding.FragmentRegistrationBinding

class RegistrationFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

}