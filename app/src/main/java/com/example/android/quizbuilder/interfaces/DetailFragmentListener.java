package com.example.android.quizbuilder.interfaces;

import com.example.android.quizbuilder.data.database.QuizEntry;

public interface DetailFragmentListener {
    void lockDrawer();
    void unlockDrawer();
    void setTitle(String title);
    void buildQuiz();
    void playQuiz(QuizEntry QuizEntry);
}
