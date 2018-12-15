package com.itechart.vpaveldm.words.adapterLayer.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.databinding.ObservableField
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.itechart.vpaveldm.words.R

class LoginViewModel(private val navController: NavController) : ViewModel() {

    val error = ObservableField<String>()
    private val auth = FirebaseAuth.getInstance()

    fun signIn(context: Context, login: String, password: String) {
        if (login.isEmpty() || password.isEmpty()) {
            error.set(context.getString(R.string.error_message_empty_login_or_password_field))
            return
        }
        auth.signInWithEmailAndPassword(login, password)
                .addOnSuccessListener { navController.popBackStack() }
                .addOnFailureListener { error.set(it.localizedMessage) }
    }

}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val navController: NavController) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(navController) as T
    }

}