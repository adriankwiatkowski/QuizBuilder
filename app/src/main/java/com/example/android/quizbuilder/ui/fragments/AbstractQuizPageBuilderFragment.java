package com.example.android.quizbuilder.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.database.QuizPage;

import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_NAMING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SAVING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

abstract class AbstractQuizPageBuilderFragment extends AbstractBuilderFragment
        implements View.OnClickListener {

    private static final String TAG = AbstractQuizPageBuilderFragment.class.getSimpleName();

    private static final String CURRENT_PAGE_KEY = "current_page";
    protected static final int UNKNOWN_CURRENT_PAGE = -1;

    private static final String TYPE_KEY = "status";
    protected static final int DEFAULT_STATUS = -1;

    private int mCurrentType = DEFAULT_STATUS;
    private int mCurrentPage = UNKNOWN_CURRENT_PAGE;

    /**
     * Views used for different status types
     */
    private Button mPreviousButton, mNextButton;
    private TextView mCurrentPageTv;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_NAMING}
     */
    private EditText mQuizNameEdit;
    private Button mSaveButton;

    private long mLastClickTime = 0;

    @Override
    public View provideFragmentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_builder, container, false);

        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mCurrentPageTv = rootView.findViewById(R.id.current_page_tv);

        mQuizNameEdit = rootView.findViewById(R.id.quiz_name_edit);
        mSaveButton = rootView.findViewById(R.id.save_button);

        mPreviousButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        mQuizNameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return onQuizNameEditKey(v, keyCode, event);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCurrentType = savedInstanceState != null ? savedInstanceState.getInt(TYPE_KEY, DEFAULT_STATUS) : DEFAULT_STATUS;

        if (savedInstanceState != null) {
            mCurrentType = savedInstanceState.getInt(TYPE_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        }
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            // Prevents double click.
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            case R.id.save_button:
                onSaveButtonClick();
                break;
            case R.id.previous_button:
                onPreviousButtonClick();
                break;
            case R.id.next_button:
                onNextButtonClick();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE_KEY, mCurrentType);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
    }

    @Override
    protected void invalidateViews(int type) {
        super.invalidateViews(type);
        setupBottomViews();
    }

    protected abstract void onSaveButtonClick();

    protected abstract void onPreviousButtonClick();

    protected abstract void onNextButtonClick();

    protected abstract boolean onQuizNameEditKey(View v, int keyCode, KeyEvent event);

    protected abstract boolean isQuizNotNull();

    protected abstract int getQuizPages();

    protected int getCurrentPage() {
        return mCurrentPage;
    }

    protected void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    protected void setNextPage() {
        mCurrentPage++;
    }

    protected void setPreviousPage() {
        mCurrentPage--;
    }

    protected int getCurrentType() {
        return mCurrentType;
    }

    protected void setCurrentType(int type) {
        mCurrentType = type;
    }

    protected String getQuizName() {
        return mQuizNameEdit.getText().toString().trim();
    }

    protected void setQuizNameEditText(String text) {
        mQuizNameEdit.setText(text);
    }

    protected void hideQuizNameKeyboard() {
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mQuizNameEdit.getWindowToken(), 0);
    }

    protected void setCorrectAnswers(QuizPage quizPage, int type) {
        List<String> correctAnswers = quizPage.getCorrectAnswers();
        if (correctAnswers.isEmpty()) {
            return;
        }
        if (type == TYPE_CHOOSER ||
                type == TYPE_PICKER ||
                type == TYPE_SWITCH) {
            CompoundButton[] views = getCompoundButtons(type);
            for (int i = 0; i < correctAnswers.size(); i++) {
                int correctAnswer = Integer.parseInt(correctAnswers.get(i));
                views[correctAnswer].setChecked(true);
            }
        } else if (type == TYPE_TEXT_ANSWER) {
            setAnswerEditText(correctAnswers.get(0));
        } else {
            throw new IllegalStateException("Not supported type for correctAnswer: " + type);
        }
    }

    private void setupBottomViews() {
        if (mCurrentType == TYPE_SAVING) {
            mCurrentPageTv.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.INVISIBLE);
            mSaveButton.setVisibility(View.INVISIBLE);
        } else if (mCurrentType == TYPE_NAMING) {
            mCurrentPageTv.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.INVISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
        } else if (isQuizNotNull()) {
            mSaveButton.setVisibility(View.INVISIBLE);
            int pageCount = getQuizPages();
            if (pageCount > 0) {
                mCurrentPageTv.setVisibility(View.VISIBLE);
                mCurrentPageTv.setText(getString(R.string.current_page_args, mCurrentPage + 1, pageCount));
            } else {
                mCurrentPageTv.setVisibility(View.INVISIBLE);
            }
            if (mCurrentPage < pageCount) {
                mNextButton.setVisibility(View.VISIBLE);
                if (mCurrentPage >= pageCount - 1) {
                    mNextButton.setText(getString(R.string.add));
                } else {
                    mNextButton.setText(getString(R.string.next));
                }
            } else {
                mNextButton.setVisibility(View.INVISIBLE);
            }
            if (mCurrentPage > 0) {
                mPreviousButton.setVisibility(View.VISIBLE);
            } else {
                mPreviousButton.setVisibility(View.INVISIBLE);
            }
        } else {
            mSaveButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        }
    }
}
