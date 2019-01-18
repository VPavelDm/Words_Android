package com.itechart.vpaveldm.words.core.converter

import android.arch.persistence.room.TypeConverter
import com.itechart.vpaveldm.words.dataLayer.word.WordState
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stateToString(state: WordState?): String? {
        return state?.toString()
    }

    @TypeConverter
    fun stateStringToState(stateString: String?): WordState? {
        return if (stateString == null) null else WordState.valueOf(stateString)
    }

}