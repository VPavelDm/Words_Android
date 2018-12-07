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
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var listener: ILoginFragmentListener
    private lateinit var auth: FirebaseAuth

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.loginButton.setOnClickListener { button ->
            val login = view.loginET.text.toString()
            val password = view.passwordET.text.toString()
            if (login.isEmpty() || password.isEmpty()) {
                view.errorTV.text = context?.getString(R.string.error_message_empty_login_or_password_field) ?: ""
                view.errorTV.visibility = View.VISIBLE
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(login, password)
                    .addOnSuccessListener { _ ->
                        Navigation.findNavController(button).popBackStack()
                    }

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