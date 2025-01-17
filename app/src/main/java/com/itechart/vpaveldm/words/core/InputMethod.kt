package com.itechart.vpaveldm.words.core

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(currentFocus: View?) {
    if (currentFocus != null) {
        val inputMethodManager = currentFocus.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}