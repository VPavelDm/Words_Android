package com.itechart.vpaveldm.words.dataLayer.local

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.itechart.vpaveldm.words.dataLayer.word.Word
import java.util.*

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addWord(word: Word)

    @Query("SELECT * FROM words WHERE owner NOT LIKE :arg0")
    fun getSubscriptionsWords(userName: String): DataSource.Factory<Int, Word>

    @Update
    fun updateWord(word: Word)

    @Query("SELECT COUNT(`key`) FROM words WHERE owner LIKE :userName")
    fun getWordCount(userName: String): Int

    @Query("SELECT * FROM words WHERE date < :date AND owner LIKE :userName")
    fun getWordsToStudy(userName: String, date: Date): List<Word>

    @Delete
    fun removeWord(word: Word)

}