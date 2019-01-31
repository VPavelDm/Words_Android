package com.itechart.vpaveldm.words.core.extension

import java.util.*

/**
 * Method to convert current time to start of day
 */
fun Date.resetTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun plusDays(count: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, count)
    return calendar.time
}

val Date.timeIntervalSince1970: Long
    get() = this.time / 1000