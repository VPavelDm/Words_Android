package com.itechart.vpaveldm.words.dataLayer.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import com.itechart.vpaveldm.words.core.converter.Converters;
import com.itechart.vpaveldm.words.dataLayer.word.Word;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class WordDatabase extends RoomDatabase {
    public abstract WordDao wordDao();
}
