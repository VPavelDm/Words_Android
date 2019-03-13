package com.itechart.vpaveldm.words.dataLayer.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Transaction
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.dataLayer.word.Word

@Dao
abstract class WordDao {

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    abstract fun addWords(vararg words: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addExamples(vararg examples: Example)

    @Transaction
    open fun addWordWithExamples(vararg words: Word) {
        words.forEach { word ->
            addExamples(*word.examples.toTypedArray())
            addWords(word)
        }
    }

}