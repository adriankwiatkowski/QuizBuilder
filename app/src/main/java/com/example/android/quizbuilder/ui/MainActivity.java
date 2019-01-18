package com.example.android.quizbuilder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.quizbuilder.R;

public class MainActivity extends AppCompatActivity implements ListFragment.OnQuizSelected {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.quiz_container, new ListFragment())
                    .commit();
        }
    }

    @Override
    public void onQuizSelected(long quizId) {
        navigateToDetail(quizId);
    }

    @Override
    public void onNewQuiz() {
        navigateToDetail(DetailActivity.QUIZ_DEFAULT_ID);
    }

    private void navigateToDetail(long quizId) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.QUIZ_ID_KEY, quizId);
        startActivity(intent);
    }
}
