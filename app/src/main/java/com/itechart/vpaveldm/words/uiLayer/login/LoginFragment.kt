package com.itechart.vpaveldm.words.uiLayer.login

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.login.AuthorizationViewModel
import com.itechart.vpaveldm.words.adapterLayer.login.ViewModelFactory
import com.itechart.vpaveldm.words.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var listener: ILoginFragmentListener
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is ILoginFragmentListener) {
            error("Activity does not conform ILoginFragmentListener interface")
        }
        listener = activity as ILoginFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener.create()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val viewModel = ViewModelFactory(navController).create(AuthorizationViewModel::class.java)
        binding.handler = viewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener.destroy()
    }

}

interface ILoginFragmentListener {
    fun destroy()
    fun create()
}