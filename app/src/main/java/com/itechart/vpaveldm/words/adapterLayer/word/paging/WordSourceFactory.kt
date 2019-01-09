package com.itechart.vpaveldm.words.adapterLayer.word.paging

import android.arch.paging.DataSource
import com.itechart.vpaveldm.words.dataLayer.word.Word

class WordSourceFactory: DataSource.Factory<Int, Word>() {
    override fun create(): DataSource<Int, Word> {
        return WordPositionalDataSource()
    }
}