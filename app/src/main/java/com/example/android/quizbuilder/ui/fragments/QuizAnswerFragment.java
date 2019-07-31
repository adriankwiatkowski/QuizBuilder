package com.example.android.quizbuilder.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.interfaces.DetailFragmentListener;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModel;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModelFactory;

public class QuizAnswerFragment extends AbstractQuizAnswerFragment
        implements View.OnClickListener, View.OnKeyListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = QuizAnswerFragment.class.getSimpleName();

    private DetailFragmentListener mDetailFragmentListener;

    private QuizEntry mQuizEntry;

    private QuizDetailViewModel mViewModel;

    public QuizAnswerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDetailFragmentListener.lockDrawer();

        QuizDetailViewModelFactory factory = new QuizDetailViewModelFactory(QuizRepository.getInstance(getContext()));
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(QuizDetailViewModel.class);
        mViewModel.getQuizEntry().observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry quizEntry) {
                mQuizEntry = quizEntry;
                if (quizEntry != null) {
                    mDetailFragmentListener.setTitle(mQuizEntry.getName());
                    showPage(mQuizEntry.getPages());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_answer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_build:
                mDetailFragmentListener.buildQuiz();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNextButtonClick() {
        if (getCurrentPage() == DEFAULT_PAGE) {
            String name = getUserNameInput();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Please insert name", Toast.LENGTH_SHORT).show();
                return;
            }
            setUsername(name);
        } else {
            addScore(mQuizEntry.getPages().get(getCurrentPage()));
        }
        setNextPage();
        showPage(mQuizEntry.getPages());
    }

    @Override
    protected void onPlayAgainButtonClick() {
        setCurrentPage(DEFAULT_PAGE);
        setScore(0);
        showPage(mQuizEntry.getPages());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            if (getCurrentPage() == DEFAULT_PAGE) {
                String name = getUserNameInput();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Please insert name", Toast.LENGTH_SHORT).show();
                } else {
                    setUsername(name);
                    setNextPage();
                    showPage(mQuizEntry.getPages());
                    hideKeyboard();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDetailFragmentListener = (DetailFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DetailFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDetailFragmentListener = null;
    }
}
