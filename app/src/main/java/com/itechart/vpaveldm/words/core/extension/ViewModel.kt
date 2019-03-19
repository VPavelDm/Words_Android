package com.itechart.vpaveldm.words.core.extension

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity): T {
    return ViewModelProviders.of(activity)[T::class.java]
}