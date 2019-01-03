package com.itechart.vpaveldm.words.core.extension

fun <T> ArrayList<T>.moveToEndAt(index: Int) {
    val element = this.removeAt(index)
    this.add(element)
}
