package com.example.android.quizbuilder;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class QuizApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
