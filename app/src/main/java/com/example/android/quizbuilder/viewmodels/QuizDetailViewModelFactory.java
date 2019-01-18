package com.example.android.quizbuilder.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.quizbuilder.data.QuizRepository;

public class QuizDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final QuizRepository mRepository;

    public QuizDetailViewModelFactory(QuizRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new QuizDetailViewModel(mRepository);
    }
}