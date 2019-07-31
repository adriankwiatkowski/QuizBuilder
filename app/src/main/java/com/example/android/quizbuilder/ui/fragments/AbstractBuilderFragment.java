package com.example.android.quizbuilder.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.android.quizbuilder.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_FIRST;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_FOURTH;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_SECOND;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_THIRD;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

abstract class AbstractBuilderFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = AbstractBuilderFragment.class.getSimpleName();

    /**
     * Views used for different status types
     */
    private EditText mQuizQuestionEdit;
    private TextView mQuizQuestionLabelTv;
    private EditText mQuizEdit1, mQuizEdit2, mQuizEdit3, mQuizEdit4;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_CHOOSER}
     */
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_PICKER}
     */
    private CheckBox mCheckBox1, mCheckBox2, mCheckBox3, mCheckBox4;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_SWITCH}
     */
    private Switch mSwitch1, mSwitch2, mSwitch3, mSwitch4;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_TEXT_ANSWER}
     */
    private EditText mAnswerEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = provideFragmentView(inflater, container, savedInstanceState);

        mQuizQuestionEdit = rootView.findViewById(R.id.question_edit);
        mQuizQuestionLabelTv = rootView.findViewById(R.id.question_label_tv);
        mQuizEdit1 = rootView.findViewById(R.id.edit_1);
        mQuizEdit2 = rootView.findViewById(R.id.edit_2);
        mQuizEdit3 = rootView.findViewById(R.id.edit_3);
        mQuizEdit4 = rootView.findViewById(R.id.edit_4);

        mRadioButton1 = rootView.findViewById(R.id.edit_radio_button_1);
        mRadioButton2 = rootView.findViewById(R.id.edit_radio_button_2);
        mRadioButton3 = rootView.findViewById(R.id.edit_radio_button_3);
        mRadioButton4 = rootView.findViewById(R.id.edit_radio_button_4);

        mCheckBox1 = rootView.findViewById(R.id.edit_checkbox_1);
        mCheckBox2 = rootView.findViewById(R.id.edit_checkbox_2);
        mCheckBox3 = rootView.findViewById(R.id.edit_checkbox_3);
        mCheckBox4 = rootView.findViewById(R.id.edit_checkbox_4);

        mSwitch1 = rootView.findViewById(R.id.edit_switch_1);
        mSwitch2 = rootView.findViewById(R.id.edit_switch_2);
        mSwitch3 = rootView.findViewById(R.id.edit_switch_3);
        mSwitch4 = rootView.findViewById(R.id.edit_switch_4);

        mAnswerEdit = rootView.findViewById(R.id.edit_answer);

        mRadioButton1.setOnCheckedChangeListener(this);
        mRadioButton2.setOnCheckedChangeListener(this);
        mRadioButton3.setOnCheckedChangeListener(this);
        mRadioButton4.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView instanceof RadioButton) {
            if (isChecked) {
                CompoundButton[] views =
                        new CompoundButton[]
                                {mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4};
                for (CompoundButton view : views) {
                    if (view.getId() != buttonView.getId()) {
                        view.setChecked(false);
                    }
                }
            }
        }
    }

    protected abstract View provideFragmentView(@NonNull LayoutInflater inflater,
                                                @Nullable ViewGroup container,
                                                @Nullable Bundle savedInstanceState);

    protected void clearPage() {
        mQuizQuestionEdit.setText("");
        mQuizEdit1.setText("");
        mQuizEdit2.setText("");
        mQuizEdit3.setText("");
        mQuizEdit4.setText("");
        mAnswerEdit.setText("");
    }

    protected void invalidateViews(int type) {

        // Hide all views.
        mQuizQuestionEdit.setVisibility(View.INVISIBLE);
        mQuizQuestionLabelTv.setVisibility(View.INVISIBLE);
        mQuizEdit1.setVisibility(View.INVISIBLE);
        mQuizEdit2.setVisibility(View.INVISIBLE);
        mQuizEdit3.setVisibility(View.INVISIBLE);
        mQuizEdit4.setVisibility(View.INVISIBLE);
        mRadioButton1.setVisibility(View.INVISIBLE);
        mRadioButton2.setVisibility(View.INVISIBLE);
        mRadioButton3.setVisibility(View.INVISIBLE);
        mRadioButton4.setVisibility(View.INVISIBLE);
        mAnswerEdit.setVisibility(View.INVISIBLE);
        mCheckBox1.setVisibility(View.INVISIBLE);
        mCheckBox2.setVisibility(View.INVISIBLE);
        mCheckBox3.setVisibility(View.INVISIBLE);
        mCheckBox4.setVisibility(View.INVISIBLE);
        mSwitch1.setVisibility(View.INVISIBLE);
        mSwitch2.setVisibility(View.INVISIBLE);
        mSwitch3.setVisibility(View.INVISIBLE);
        mSwitch4.setVisibility(View.INVISIBLE);

        switch (type) {
            case TYPE_CHOOSER:
                mQuizQuestionEdit.setVisibility(View.VISIBLE);
                mQuizQuestionLabelTv.setVisibility(View.VISIBLE);
                mQuizEdit1.setVisibility(View.VISIBLE);
                mQuizEdit2.setVisibility(View.VISIBLE);
                mQuizEdit3.setVisibility(View.VISIBLE);
                mQuizEdit4.setVisibility(View.VISIBLE);
                mRadioButton1.setVisibility(View.VISIBLE);
                mRadioButton2.setVisibility(View.VISIBLE);
                mRadioButton3.setVisibility(View.VISIBLE);
                mRadioButton4.setVisibility(View.VISIBLE);
                break;
            case TYPE_PICKER:
                mQuizQuestionEdit.setVisibility(View.VISIBLE);
                mQuizQuestionLabelTv.setVisibility(View.VISIBLE);
                mQuizEdit1.setVisibility(View.VISIBLE);
                mQuizEdit2.setVisibility(View.VISIBLE);
                mQuizEdit3.setVisibility(View.VISIBLE);
                mQuizEdit4.setVisibility(View.VISIBLE);
                mCheckBox1.setVisibility(View.VISIBLE);
                mCheckBox2.setVisibility(View.VISIBLE);
                mCheckBox3.setVisibility(View.VISIBLE);
                mCheckBox4.setVisibility(View.VISIBLE);
                break;
            case TYPE_SWITCH:
                mQuizQuestionEdit.setVisibility(View.VISIBLE);
                mQuizQuestionLabelTv.setVisibility(View.VISIBLE);
                mQuizEdit1.setVisibility(View.VISIBLE);
                mQuizEdit2.setVisibility(View.VISIBLE);
                mQuizEdit3.setVisibility(View.VISIBLE);
                mQuizEdit4.setVisibility(View.VISIBLE);
                mSwitch1.setVisibility(View.VISIBLE);
                mSwitch2.setVisibility(View.VISIBLE);
                mSwitch3.setVisibility(View.VISIBLE);
                mSwitch4.setVisibility(View.VISIBLE);
                break;
            case TYPE_TEXT_ANSWER:
                mQuizQuestionEdit.setVisibility(View.VISIBLE);
                mQuizQuestionLabelTv.setVisibility(View.VISIBLE);
                mAnswerEdit.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * Get correct answers from user input.
     *
     * @return {@link List <String>} with correct answers.
     */
    protected List<String> getCorrectAnswers(int type) {
        List<String> correctAnswers = new ArrayList<>();
        if (type == TYPE_CHOOSER ||
                type == TYPE_PICKER ||
                type == TYPE_SWITCH) {
            CompoundButton[] views = new CompoundButton[4];
            int[] answers = new int[]{CORRECT_ANSWER_FIRST,
                    CORRECT_ANSWER_SECOND,
                    CORRECT_ANSWER_THIRD,
                    CORRECT_ANSWER_FOURTH};
            if (type == TYPE_CHOOSER) {
                views[0] = mRadioButton1;
                views[1] = mRadioButton2;
                views[2] = mRadioButton3;
                views[3] = mRadioButton4;
            } else if (type == TYPE_PICKER) {
                views[0] = mCheckBox1;
                views[1] = mCheckBox2;
                views[2] = mCheckBox3;
                views[3] = mCheckBox4;
            } else {
                views[0] = mSwitch1;
                views[1] = mSwitch2;
                views[2] = mSwitch3;
                views[3] = mSwitch4;
            }
            for (int i = 0; i < views.length; i++) {
                if (views[i].isChecked()) {
                    correctAnswers.add(String.valueOf(answers[i]));
                }
            }
        } else if (type == TYPE_TEXT_ANSWER) {
            String answer = mAnswerEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(answer)) {
                correctAnswers.add(answer);
            }
        } else {
            throw new IllegalStateException("Not supported type for correctAnswer: " + type);
        }
        return correctAnswers;
    }

    protected CompoundButton[] getCompoundButtons(int type) {
        CompoundButton[] compoundButtons = new CompoundButton[4];
        if (type == TYPE_CHOOSER) {
            compoundButtons[0] = mRadioButton1;
            compoundButtons[1] = mRadioButton2;
            compoundButtons[2] = mRadioButton3;
            compoundButtons[3] = mRadioButton4;
        } else if (type == TYPE_PICKER) {
            compoundButtons[0] = mCheckBox1;
            compoundButtons[1] = mCheckBox2;
            compoundButtons[2] = mCheckBox3;
            compoundButtons[3] = mCheckBox4;
        } else {
            compoundButtons[0] = mSwitch1;
            compoundButtons[1] = mSwitch2;
            compoundButtons[2] = mSwitch3;
            compoundButtons[3] = mSwitch4;
        }
        return compoundButtons;
    }

    protected void setAnswers(List<String> answerList, int type) {
        if (type == TYPE_CHOOSER || type == TYPE_PICKER || type == TYPE_SWITCH) {

            String answer1 = mQuizEdit1.getText().toString().trim();
            String answer2 = mQuizEdit2.getText().toString().trim();
            String answer3 = mQuizEdit3.getText().toString().trim();
            String answer4 = mQuizEdit4.getText().toString().trim();

            answerList.add(answer1);
            answerList.add(answer2);
            answerList.add(answer3);
            answerList.add(answer4);
        }
    }

    protected void setAnswerEditText(String answerText) {
        mAnswerEdit.setText(answerText);
    }

    protected void setQuizQuestionEditText(String text) {
        mQuizQuestionEdit.setText(text);
    }

    protected void setQuizAnswers(List<String> answers) {
        mQuizEdit1.setText(answers.get(0));
        mQuizEdit2.setText(answers.get(1));
        mQuizEdit3.setText(answers.get(2));
        mQuizEdit4.setText(answers.get(3));
    }

    protected List<String> getQuizAnswers(int type) {
        List<String> answers = new ArrayList<>();

        if (type == TYPE_CHOOSER || type == TYPE_PICKER || type == TYPE_SWITCH) {
            String answer1 = mQuizEdit1.getText().toString().trim();
            String answer2 = mQuizEdit2.getText().toString().trim();
            String answer3 = mQuizEdit3.getText().toString().trim();
            String answer4 = mQuizEdit4.getText().toString().trim();

            answers.add(answer1);
            answers.add(answer2);
            answers.add(answer3);
            answers.add(answer4);
        }

        return answers;
    }

    protected String getQuizQuestion() {
        return mQuizQuestionEdit.getText().toString().trim();
    }

    /**
     * @return <code>false</code> if all fields are empty.
     * <code>true</code> otherwise.
     */
    protected boolean shouldSave(int type) {

        boolean shouldSave = true;

        if (type == TYPE_CHOOSER || type == TYPE_PICKER || type == TYPE_SWITCH) {
            String answer1 = mQuizEdit1.getText().toString().trim();
            String answer2 = mQuizEdit2.getText().toString().trim();
            String answer3 = mQuizEdit3.getText().toString().trim();
            String answer4 = mQuizEdit4.getText().toString().trim();

            if (TextUtils.isEmpty(answer1) && TextUtils.isEmpty(answer2) &&
                    TextUtils.isEmpty(answer3) && TextUtils.isEmpty(answer4)) {
                String question = mQuizQuestionEdit.getText().toString().trim();
                if (TextUtils.isEmpty(question)) {
                    List<String> correctAnswers = getCorrectAnswers(type);
                    if (correctAnswers == null || correctAnswers.isEmpty()) {
                        shouldSave = false;
                    } else {
                        boolean isNotEmpty = false;
                        for (int i = 0; i < correctAnswers.size(); i++) {
                            if (!TextUtils.isEmpty(correctAnswers.get(i))) {
                                isNotEmpty = true;
                                break;
                            }
                        }
                        shouldSave = isNotEmpty;
                    }
                }
            }
        }

        return shouldSave;
    }
}
