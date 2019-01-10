package com.itechart.vpaveldm.words.dataLayer.local

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.itechart.vpaveldm.words.dataLayer.word.Word
import java.util.*

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWord(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addWords(words: List<Word>)

    @Query("SELECT * FROM words")
    fun getWords(): DataSource.Factory<Int, Word>

    @Query("SELECT date FROM words ORDER BY date DESC LIMIT 1")
    fun getLastAddedWordDate(): Date?

}