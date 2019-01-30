package com.itechart.vpaveldm.words.dataLayer.local

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.itechart.vpaveldm.words.core.extension.plusDays
import com.itechart.vpaveldm.words.core.extension.resetTime
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.dataLayer.word.Word
import java.util.*

@Dao
abstract class WordDao {

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    abstract fun addWords(vararg words: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addExamples(vararg examples: Example)

    @Query("SELECT * FROM words WHERE owner LIKE :user ORDER BY date DESC")
    abstract fun getWordsDS(user: String): DataSource.Factory<Int, Word>

    @Query("SELECT * FROM words WHERE owner NOT LIKE :user ORDER BY date DESC")
    abstract fun getSubscriptionsWordsDS(user: String): DataSource.Factory<Int, Word>

    @Query("SELECT * FROM wordExamples WHERE wordId LIKE :key")
    abstract fun getExamples(key: String): List<Example>

    @Update
    abstract fun updateWord(word: Word)

    @Query("SELECT COUNT(`key`) FROM words WHERE owner LIKE :userName")
    abstract fun getWordCount(userName: String): Int

    @Query("SELECT COUNT(`key`) FROM words WHERE owner NOT LIKE :userName")
    abstract fun getSubscriptionsWordCount(userName: String): Int

    @Query("SELECT * FROM words WHERE date < :date AND owner LIKE :userName ORDER BY RANDOM() LIMIT 10")
    abstract fun getWordsToStudy(userName: String, date: Date): List<Word>

    @Query("SELECT * FROM words WHERE owner LIKE :userName")
    abstract fun getWords(userName: String): List<Word>

    @Delete
    abstract fun removeWord(word: Word)

    @Delete
    abstract fun removeExamples(vararg examples: Example)

    @Transaction
    open fun getWordsWithExamples(userName: String): DataSource.Factory<Int, Word> {
        return getWordsDS(userName).map { word ->
            word.examples = getExamples(word.key)
            return@map word
        }
    }

    @Transaction
    open fun getWordsWithExamplesToStudy(userName: String): List<Word> {
        val currentDate = plusDays(1).resetTime()
        return getWordsToStudy(userName, currentDate).map { word ->
            word.examples = getExamples(word.key)
            return@map word
        }
    }

    @Transaction
    open fun removeWordWithExamples(word: Word) {
        removeExamples(*word.examples.toTypedArray())
        removeWord(word)
    }

    @Transaction
    open fun addWordWithExamples(vararg words: Word) {
        words.forEach { word ->
            addExamples(*word.examples.toTypedArray())
            addWords(word)
        }
    }

    @Transaction
    open fun getSubscriptionsWords(userName: String): DataSource.Factory<Int, Word> {
        return getSubscriptionsWordsDS(userName).map { word ->
            word.examples = getExamples(word.key)
            return@map word
        }
    }

}