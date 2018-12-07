package com.itechart.vpaveldm.words.uiLayer.login

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.itechart.vpaveldm.words.R
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment: Fragment() {

    private lateinit var listener: ILoginFragmentListener

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.loginButton.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        return view
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