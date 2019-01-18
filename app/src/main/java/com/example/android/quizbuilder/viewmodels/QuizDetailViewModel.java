package com.example.android.quizbuilder.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;

public class QuizDetailViewModel extends ViewModel {

    private LiveData<QuizEntry> mQuizEntry;
    private MutableLiveData<Long> mIdQuery = new MutableLiveData<>();
    private QuizRepository mRepository;

    private MutableLiveData<QuizEntry> mEditedQuizEntry = new MutableLiveData<>();

    public QuizDetailViewModel(QuizRepository repository) {
        mRepository = repository;
        mQuizEntry = Transformations.switchMap(mIdQuery, new Function<Long, LiveData<QuizEntry>>() {
            @Override
            public LiveData<QuizEntry> apply(Long input) {
                return mRepository.getItem(input);
            }
        });
    }

    public LiveData<QuizEntry> getQuizEntry() {
        return mQuizEntry;
    }

    public LiveData<QuizEntry> getQuizEntry(long quizId) {
        return mRepository.getItem(quizId);
    }

    public void initQuizEntry(long quizId) {
        if (mIdQuery.getValue() == null || mIdQuery.getValue() != quizId) {
            mIdQuery.setValue(quizId);
        }
    }

    public LiveData<Long> insertItem(QuizEntry quizEntry) {
        return mRepository.insertItem(quizEntry);
    }

    public void updateItem(QuizEntry quizEntry) {
        mRepository.updateItem(quizEntry);
    }

    public void deleteItem(QuizEntry quizEntry) {
        mRepository.deleteItem(quizEntry);
    }

    public MutableLiveData<QuizEntry> getEditedQuizEntry() {
        return mEditedQuizEntry;
    }

    public void setEditedQuizEntry(QuizEntry quizEntry) {
        mEditedQuizEntry.setValue(quizEntry);
    }
}
