package com.itechart.vpaveldm.words.uiLayer.registration

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.login.AuthorizationViewModel
import com.itechart.vpaveldm.words.adapterLayer.login.ViewModelFactory
import com.itechart.vpaveldm.words.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val viewModel = ViewModelFactory(navController).create(AuthorizationViewModel::class.java)
        binding.handler = viewModel
        return binding.root
    }

}