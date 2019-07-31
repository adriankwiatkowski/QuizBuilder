package com.example.android.quizbuilder.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.data.database.QuizPage;
import com.example.android.quizbuilder.interfaces.DetailFragmentListener;
import com.example.android.quizbuilder.ui.fragments.QuizAnswerFragment;
import com.example.android.quizbuilder.ui.fragments.QuizBuilderFragment;
import com.example.android.quizbuilder.utilities.QuizUtils;
import com.example.android.quizbuilder.utilities.leaks.IMMLeaks;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModel;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModelFactory;

import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_NAMING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

public class DetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DetailFragmentListener {

    public static final String QUIZ_ID_KEY = "quiz_id";
    public static final long QUIZ_DEFAULT_ID = -1;

    private static final String BUILDER_TAG = "builder";

    private Toolbar mToolbar;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private QuizDetailViewModel mViewModel;
    private long mQuizId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        IMMLeaks.fixFocusedViewLeak(getApplication());

        initViews();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuizName();
            }
        });

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        QuizDetailViewModelFactory factory = new QuizDetailViewModelFactory(QuizRepository.getInstance(this));
        mViewModel = ViewModelProviders.of(this, factory).get(QuizDetailViewModel.class);

        Intent intent = getIntent();
        mQuizId = intent == null ? QUIZ_DEFAULT_ID : intent.getLongExtra(QUIZ_ID_KEY, QUIZ_DEFAULT_ID);
        mViewModel.initQuizEntry(mQuizId);

        if (savedInstanceState == null) {
            mViewModel.getQuizEntry().observe(this, new Observer<QuizEntry>() {
                @Override
                public void onChanged(@Nullable QuizEntry quizEntry) {
                    mViewModel.getQuizEntry().removeObserver(this);
                    if (QuizUtils.canPlay(DetailActivity.this, quizEntry)) {
                        // Play game =]
                        play();
                    } else {
                        // Build game!
                        build();
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_camera:
                changeBuildForm(TYPE_NAMING);
                break;
            case R.id.nav_gallery:
                changeBuildForm(TYPE_CHOOSER);
                break;
            case R.id.nav_slideshow:
                changeBuildForm(TYPE_PICKER);
                break;
            case R.id.nav_manage:
                changeBuildForm(TYPE_SWITCH);
                break;
            case R.id.nav_share:
                changeBuildForm(TYPE_TEXT_ANSWER);
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void lockDrawer() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void unlockDrawer() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void buildQuiz() {
        build();
    }

    @Override
    public void playQuiz(QuizEntry quizEntry) {
        if (QuizUtils.canPlay(DetailActivity.this, quizEntry)) {
            play();
        }
    }

    private void changeBuildForm(int quizType) {
        QuizBuilderFragment fragment = (QuizBuilderFragment)
                getSupportFragmentManager().findFragmentByTag(BUILDER_TAG);
        if (fragment != null && fragment.isVisible()) {
            fragment.setQuizType(quizType);
        }
    }

    private void play() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.quiz_detail_container, new QuizAnswerFragment())
                .commit();
    }

    private void build() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.quiz_detail_container, new QuizBuilderFragment(), BUILDER_TAG)
                .commit();
    }

    private void changeQuizName() {
        changeBuildForm(TYPE_NAMING);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
    }
}
