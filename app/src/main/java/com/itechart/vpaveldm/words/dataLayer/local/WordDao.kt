package com.itechart.vpaveldm.words.dataLayer.local

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.itechart.vpaveldm.words.dataLayer.word.Word
import java.util.*

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWords(words: List<Word>)

    @Query("SELECT * FROM words WHERE owner NOT LIKE :arg0")
    fun getWords(userName: String): DataSource.Factory<Int, Word>

    @Update
    fun updateWord(word: Word)

    @Query("SELECT COUNT(`key`) FROM words WHERE owner LIKE :userName")
    fun getWordCount(userName: String): Int

    @Query("SELECT * FROM words WHERE date < :date")
    fun getWordsToStudy(date: Date): List<Word>

}