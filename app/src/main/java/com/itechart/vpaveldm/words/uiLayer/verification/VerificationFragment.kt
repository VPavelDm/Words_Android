package com.itechart.vpaveldm.words.uiLayer.verification

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.login.AuthorizationViewModel
import com.itechart.vpaveldm.words.adapterLayer.login.ViewModelFactory
import com.itechart.vpaveldm.words.databinding.FragmentVerificationBinding

class VerificationFragment: Fragment() {

    private lateinit var binding: FragmentVerificationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val viewModel = ViewModelProviders.of(this, ViewModelFactory(navController)).get(AuthorizationViewModel::class.java)
        binding.handler = viewModel
        viewModel.sendVerificationMail()
        return binding.root
    }

}