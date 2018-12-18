package com.itechart.vpaveldm.words.uiLayer.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.Navigation
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.login.AuthorizationViewModel
import com.itechart.vpaveldm.words.adapterLayer.login.ViewModelFactory
import com.itechart.vpaveldm.words.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val viewModel = ViewModelProviders.of(this, ViewModelFactory(navController)).get(AuthorizationViewModel::class.java)
        binding.handler = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.loginButton.setOnClickListener {
            binding.loginET.onEditorAction(EditorInfo.IME_ACTION_DONE)
            binding.passwordET.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
    }

}