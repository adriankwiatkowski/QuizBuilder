package com.example.android.quizbuilder.data;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.quizbuilder.data.database.QuizDatabase;
import com.example.android.quizbuilder.data.database.QuizEntry;

import java.util.List;

public class QuizRepository {

    private static final String TAG = QuizRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static QuizRepository sInstance;

    private QuizDatabase mDb;

    private QuizRepository(Context context) {
        Log.d(TAG, "Creating new database instance");
        mDb = Room.databaseBuilder(context.getApplicationContext(),
                QuizDatabase.class, QuizDatabase.DATABASE_NAME)
                .build();
    }

    public synchronized static QuizRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new QuizRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    @SuppressLint("StaticFieldLeak")
    public LiveData<Long> insertItem(final QuizEntry quizEntry) {
        final MediatorLiveData<Long> quizIdMediator = new MediatorLiveData<>();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                long quizId = mDb.quizDao().insertItem(quizEntry);
                quizIdMediator.postValue(quizId);
                return null;
            }
        }.execute();
        return quizIdMediator;
    }

    @SuppressLint("StaticFieldLeak")
    public void updateItem(final QuizEntry quizEntry) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDb.quizDao().updateItem(quizEntry);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteItem(final QuizEntry quizEntry) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDb.quizDao().deleteItem(quizEntry);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteAllItems() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDb.quizDao().deleteAllItems();
                return null;
            }
        }.execute();
    }

    public LiveData<QuizEntry> getItem(long quizId) {
        return mDb.quizDao().getItem(quizId);
    }

    public LiveData<List<QuizEntry>> getItems() {
        return mDb.quizDao().getAllItems();
    }
}
