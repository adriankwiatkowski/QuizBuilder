package com.example.android.quizbuilder.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.database.QuizPage;
import com.example.android.quizbuilder.utilities.QuizUtils;

import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

abstract class AbstractQuizAnswerFragment extends Fragment
        implements View.OnClickListener, View.OnKeyListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = AbstractQuizAnswerFragment.class.getSimpleName();

    private static final String SCORE_KEY = "score";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String USER_NAME_KEY = "user_name";
    protected static final int DEFAULT_PAGE = -1;

    private Button mPlayAgainButton;
    private TextView mCongratzTv, mScoreTv;

    private Button mNextButton;
    private TextView mUserNameLabelTv, mQuestionTv;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4;
    private CheckBox mCheckBox1, mCheckBox2, mCheckBox3, mCheckBox4;
    private Switch mSwitch1, mSwitch2, mSwitch3, mSwitch4;
    private EditText mUserNameEdit, mAnswerEdit;

    private int mCurrentPage = DEFAULT_PAGE;
    private int mScore = 0;
    private String mUserName = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answer, container, false);
        mPlayAgainButton = rootView.findViewById(R.id.play_again_button);
        mCongratzTv = rootView.findViewById(R.id.congratz_tv);
        mScoreTv = rootView.findViewById(R.id.score_tv);
        mNextButton = rootView.findViewById(R.id.next_button);
        mQuestionTv = rootView.findViewById(R.id.question_name_tv);
        mRadioButton1 = rootView.findViewById(R.id.radio_button_1);
        mRadioButton2 = rootView.findViewById(R.id.radio_button_2);
        mRadioButton3 = rootView.findViewById(R.id.radio_button_3);
        mRadioButton4 = rootView.findViewById(R.id.radio_button_4);
        mCheckBox1 = rootView.findViewById(R.id.checkbox_1);
        mCheckBox2 = rootView.findViewById(R.id.checkbox_2);
        mCheckBox3 = rootView.findViewById(R.id.checkbox_3);
        mCheckBox4 = rootView.findViewById(R.id.checkbox_4);
        mSwitch1 = rootView.findViewById(R.id.switch_1);
        mSwitch2 = rootView.findViewById(R.id.switch_2);
        mSwitch3 = rootView.findViewById(R.id.switch_3);
        mSwitch4 = rootView.findViewById(R.id.switch_4);
        mAnswerEdit = rootView.findViewById(R.id.edit_answer);
        mUserNameEdit = rootView.findViewById(R.id.user_name_edit);
        mUserNameLabelTv = rootView.findViewById(R.id.user_name_label);
        mPlayAgainButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mRadioButton1.setOnCheckedChangeListener(this);
        mRadioButton2.setOnCheckedChangeListener(this);
        mRadioButton3.setOnCheckedChangeListener(this);
        mRadioButton4.setOnCheckedChangeListener(this);
        mUserNameEdit.setOnKeyListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            mScore = savedInstanceState.getInt(SCORE_KEY);
            mUserName = savedInstanceState.getString(USER_NAME_KEY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button:
                onNextButtonClick();
                break;
            case R.id.play_again_button:
                onPlayAgainButtonClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView instanceof RadioButton) {
            CompoundButton[] views = new CompoundButton[]{mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4};
            if (isChecked) {
                for (CompoundButton view : views) {
                    if (view.getId() != buttonView.getId()) {
                        view.setChecked(false);
                    }
                }
            }
        }
    }

    protected void showPage(List<QuizPage> pageList) {

        clearPage();

        invalidateViews(pageList);

        if (mCurrentPage >= pageList.size()) {
            // Show score.
            int maxScore = pageList.size();
            setEndScoreText(maxScore);
        } else if (mCurrentPage != DEFAULT_PAGE) {
            // Show question.
            QuizPage quizPage = pageList.get(mCurrentPage);
            mQuestionTv.setText(quizPage.getQuestion());
            int type = quizPage.getType();
            if (type == TYPE_CHOOSER || type == TYPE_PICKER || type == TYPE_SWITCH) {
                CompoundButton[] views = getCompoundButtons(quizPage.getType());
                for (int i = 0; i < quizPage.getAnswers().size(); i++) {
                    views[i].setText(quizPage.getAnswers().get(i));
                }
            }
        }
        // Else show welcome screen.
    }

    protected void addScore(QuizPage quizPage) {
        boolean isCorrect = false;
        int type = quizPage.getType();
        List<String> correctAnswers = quizPage.getCorrectAnswers();
        if (type == TYPE_CHOOSER || type == TYPE_PICKER || type == TYPE_SWITCH) {
            CompoundButton[] views = getCompoundButtons(type);
            for (int i = 0; i < correctAnswers.size(); i++) {
                int correctAnswer = Integer.parseInt(correctAnswers.get(i));
                if (!views[correctAnswer].isChecked()) {
                    isCorrect = false;
                    break;
                } else {
                    isCorrect = true;
                }
            }
        } else if (type == TYPE_TEXT_ANSWER) {
            String answer = mAnswerEdit.getText().toString().trim();
            if (answer.equalsIgnoreCase(quizPage.getCorrectAnswers().get(0))) {
                isCorrect = true;
            }
        }
        if (isCorrect) {
            mScore++;
        }
    }

    private void clearPage() {
        mRadioButton1.setChecked(true);
        mRadioButton2.setChecked(false);
        mRadioButton3.setChecked(false);
        mRadioButton4.setChecked(false);
        mCheckBox1.setChecked(false);
        mCheckBox2.setChecked(false);
        mCheckBox3.setChecked(false);
        mCheckBox4.setChecked(false);
        mSwitch1.setChecked(false);
        mSwitch2.setChecked(false);
        mSwitch3.setChecked(false);
        mSwitch4.setChecked(false);
        mAnswerEdit.setText("");
    }

    private void invalidateViews(List<QuizPage> pageList) {

        // Hide all views.
        mUserNameEdit.setVisibility(View.INVISIBLE);
        mUserNameLabelTv.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
        mQuestionTv.setVisibility(View.INVISIBLE);
        mRadioButton1.setVisibility(View.INVISIBLE);
        mRadioButton2.setVisibility(View.INVISIBLE);
        mRadioButton3.setVisibility(View.INVISIBLE);
        mRadioButton4.setVisibility(View.INVISIBLE);
        mCheckBox1.setVisibility(View.INVISIBLE);
        mCheckBox2.setVisibility(View.INVISIBLE);
        mCheckBox3.setVisibility(View.INVISIBLE);
        mCheckBox4.setVisibility(View.INVISIBLE);
        mSwitch1.setVisibility(View.INVISIBLE);
        mSwitch2.setVisibility(View.INVISIBLE);
        mSwitch3.setVisibility(View.INVISIBLE);
        mSwitch4.setVisibility(View.INVISIBLE);
        mAnswerEdit.setVisibility(View.INVISIBLE);
        mScoreTv.setVisibility(View.INVISIBLE);
        mCongratzTv.setVisibility(View.INVISIBLE);
        mPlayAgainButton.setVisibility(View.INVISIBLE);

        if (mCurrentPage >= pageList.size()) {
            // Show score
            mScoreTv.setVisibility(View.VISIBLE);
            mCongratzTv.setVisibility(View.VISIBLE);
            mPlayAgainButton.setVisibility(View.VISIBLE);
        } else if (mCurrentPage != DEFAULT_PAGE) {
            // Show question.
            QuizPage quizPage = pageList.get(mCurrentPage);
            switch (quizPage.getType()) {
                case TYPE_CHOOSER:
                    mNextButton.setVisibility(View.VISIBLE);
                    mQuestionTv.setVisibility(View.VISIBLE);
                    mRadioButton1.setVisibility(View.VISIBLE);
                    mRadioButton2.setVisibility(View.VISIBLE);
                    mRadioButton3.setVisibility(View.VISIBLE);
                    mRadioButton4.setVisibility(View.VISIBLE);
                    break;
                case TYPE_PICKER:
                    mNextButton.setVisibility(View.VISIBLE);
                    mQuestionTv.setVisibility(View.VISIBLE);
                    mCheckBox1.setVisibility(View.VISIBLE);
                    mCheckBox2.setVisibility(View.VISIBLE);
                    mCheckBox3.setVisibility(View.VISIBLE);
                    mCheckBox4.setVisibility(View.VISIBLE);
                    break;
                case TYPE_SWITCH:
                    mNextButton.setVisibility(View.VISIBLE);
                    mQuestionTv.setVisibility(View.VISIBLE);
                    mSwitch1.setVisibility(View.VISIBLE);
                    mSwitch2.setVisibility(View.VISIBLE);
                    mSwitch3.setVisibility(View.VISIBLE);
                    mSwitch4.setVisibility(View.VISIBLE);
                    break;
                case TYPE_TEXT_ANSWER:
                    mNextButton.setVisibility(View.VISIBLE);
                    mQuestionTv.setVisibility(View.VISIBLE);
                    mAnswerEdit.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            // Show welcome screen.
            mNextButton.setVisibility(View.VISIBLE);
            mUserNameEdit.setVisibility(View.VISIBLE);
            mUserNameLabelTv.setVisibility(View.VISIBLE);
        }
    }

    protected CompoundButton[] getCompoundButtons(int type) {
        CompoundButton[] views = new CompoundButton[4];
        switch (type) {
            case TYPE_CHOOSER:
                views[0] = mRadioButton1;
                views[1] = mRadioButton2;
                views[2] = mRadioButton3;
                views[3] = mRadioButton4;
                break;
            case TYPE_PICKER:
                views[0] = mCheckBox1;
                views[1] = mCheckBox2;
                views[2] = mCheckBox3;
                views[3] = mCheckBox4;
                break;
            case TYPE_SWITCH:
                views[0] = mSwitch1;
                views[1] = mSwitch2;
                views[2] = mSwitch3;
                views[3] = mSwitch4;
                break;
            default:
                throw new IllegalArgumentException("Not supported type: " + type);
        }
        return views;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        outState.putInt(SCORE_KEY, mScore);
        outState.putString(USER_NAME_KEY, mUserName);
    }

    protected abstract void onNextButtonClick();

    protected abstract void  onPlayAgainButtonClick();

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUserNameEdit.getWindowToken(), 0);
    }

    protected int getCurrentPage() {
        return mCurrentPage;
    }

    protected void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    protected void setNextPage() {
        mCurrentPage++;
    }

    protected int getScore() {
        return mScore;
    }

    protected void setScore(int score) {
        mScore = score;
    }

    protected void addOneScore() {
        mScore++;
    }

    protected String getUserNameInput() {
        return mUserNameEdit.getText().toString().trim();
    }

    protected void setUsername(String username) {
        mUserName = username;
    }

    protected void setEndScoreText(int maxScore) {
        mScoreTv.setText(getString(R.string.score_points_args, mUserName, mScore, maxScore));
        String endScoreMessage = QuizUtils.getEndScoreMessage(getContext(), mScore, maxScore, mUserName);
        mCongratzTv.setText(endScoreMessage);
    }
}
