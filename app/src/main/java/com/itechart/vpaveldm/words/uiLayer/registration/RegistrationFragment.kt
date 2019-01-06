package com.itechart.vpaveldm.words.uiLayer.registration

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
import com.itechart.vpaveldm.words.databinding.FragmentRegistrationBinding
import com.itechart.vpaveldm.words.databinding.FragmentRegistrationFirstStepBinding

class RegistrationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val step = arguments?.getInt("step")
        when (step) {
            1 -> {
                val binding = FragmentRegistrationFirstStepBinding.inflate(inflater, container, false)
                val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
                val viewModel = ViewModelProviders.of(activity!!, ViewModelFactory(navController)).get(AuthorizationViewModel::class.java)
                binding.handler = viewModel
                return binding.root
            }
            2 -> {
                val binding = FragmentRegistrationBinding.inflate(inflater, container, false)
                val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
                val viewModel = ViewModelProviders.of(activity!!, ViewModelFactory(navController)).get(AuthorizationViewModel::class.java)
                binding.handler = viewModel
                return binding.root
            }
            else -> {
                throw IllegalArgumentException("Add fragment handling")
            }
        }
    }

}