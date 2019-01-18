package com.example.android.quizbuilder.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface QuizDao {

    @Query("SELECT * FROM quiz ORDER BY date DESC")
    LiveData<List<QuizEntry>> getAllItems();

    @Query("SELECT * FROM quiz WHERE id = :quizId")
    LiveData<QuizEntry> getItem(long quizId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertItem(QuizEntry quizEntry);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateItem(QuizEntry quizEntry);

    @Delete
    void deleteItem(QuizEntry quizEntry);

    @Query("DELETE FROM quiz")
    void deleteAllItems();
}
