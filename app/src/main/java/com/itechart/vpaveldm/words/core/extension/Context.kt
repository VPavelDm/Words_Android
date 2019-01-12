package com.itechart.vpaveldm.words.core.extension

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("ShowToast")
fun Context.toast(message: String) {
    if (current_toast == null) {
        current_toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }
    current_toast?.setText(message)
    current_toast?.show()
}

private var current_toast: Toast? = null