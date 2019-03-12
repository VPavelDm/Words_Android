package com.itechart.vpaveldm.words.core.extension

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.disposedBy(disposables: CompositeDisposable) {
    disposables.add(this)
}