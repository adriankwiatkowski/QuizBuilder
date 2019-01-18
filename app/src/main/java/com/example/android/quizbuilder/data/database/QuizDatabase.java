package com.example.android.quizbuilder.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {QuizEntry.class}, version = 1, exportSchema = false)
@TypeConverters(QuizConverter.class)
public abstract class QuizDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "quiz_db";

    public abstract QuizDao quizDao();
}
