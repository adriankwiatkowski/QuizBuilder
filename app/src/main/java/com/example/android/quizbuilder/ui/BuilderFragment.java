package com.example.android.quizbuilder.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.data.database.QuizPage;
import com.example.android.quizbuilder.interfaces.DeleteDialogListener;
import com.example.android.quizbuilder.interfaces.DeletePageDialogListener;
import com.example.android.quizbuilder.interfaces.DetailFragmentListener;
import com.example.android.quizbuilder.interfaces.UnsavedDialogListener;
import com.example.android.quizbuilder.ui.dialogs.DeleteAlertDialogFragment;
import com.example.android.quizbuilder.ui.dialogs.DeletePageDialogFragment;
import com.example.android.quizbuilder.ui.dialogs.UnsavedAlertDialogFragment;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModel;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModelFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_FIRST;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_FOURTH;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_SECOND;
import static com.example.android.quizbuilder.data.database.QuizConstants.CORRECT_ANSWER_THIRD;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_NAMING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SAVING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

public class BuilderFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, DeletePageDialogListener,
        DeleteDialogListener, UnsavedDialogListener {

    private static final String TAG = BuilderFragment.class.getSimpleName();

    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final int UNKNOWN_CURRENT_PAGE = -1;

    private static final String TYPE_KEY = "status";
    private static final int DEFAULT_STATUS = -1;

    private static final String DELETE_DIALOG_TAG = "delete_tag";
    private static final String DELETE_PAGE_DIALOG_TAG = "delete_page_tag";
    private static final String UNSAVED_DIALOG_TAG = "unsaved_tag";
    private static final String UNSAVED_PLAY_DIALOG_TAG = "unsaved_play_tag";

    private int mCurrentType = DEFAULT_STATUS;
    private int mCurrentPage = UNKNOWN_CURRENT_PAGE;

    /**
     * Views used for different status types
     */
    private Button mPreviousButton, mNextButton;
    private EditText mQuizQuestionEdit;
    private TextView mCurrentPageTv;
    private TextView mQuizQuestionLabelTv;
    private EditText mQuizEdit1, mQuizEdit2, mQuizEdit3, mQuizEdit4;

    /**
     * Views for {@link com.example.android.quizbuilder.data.database.QuizConstants#TYPE_NAMING}
     */
    private EditText mQuizNameEdit;
    private Button mSaveButton;

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

    private DetailFragmentListener mDrawerLockListener;

    private QuizDetailViewModel mViewModel;

    private QuizEntry mQuizEntry;
    private QuizEntry mEditedQuizEntry;

    private long mLastClickTime = 0;

    public BuilderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_builder, container, false);

        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mCurrentPageTv = rootView.findViewById(R.id.current_page_tv);

        mQuizNameEdit = rootView.findViewById(R.id.quiz_name_edit);
        mSaveButton = rootView.findViewById(R.id.save_button);

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

        mPreviousButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        mQuizNameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mQuizEntry == null) {
                        saveItem();
                    } else {
                        mEditedQuizEntry.setName(mQuizNameEdit.getText().toString().trim());
                        mCurrentPage = 0;
                        mCurrentType = mEditedQuizEntry.getPages().get(0).getType();
                        showPage();
                        mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
                    }
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mQuizNameEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCurrentType = savedInstanceState != null ? savedInstanceState.getInt(TYPE_KEY, DEFAULT_STATUS) : DEFAULT_STATUS;

        final Bundle finalSavedInstanceState = savedInstanceState;

        QuizDetailViewModelFactory factory = new QuizDetailViewModelFactory(QuizRepository.getInstance(getContext()));
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(QuizDetailViewModel.class);
        mViewModel.getQuizEntry().observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry quizEntry) {
                mQuizEntry = quizEntry;
                if (quizEntry == null) {
                    if (mCurrentPage == UNKNOWN_CURRENT_PAGE) {
                        mCurrentPage = 0;
                    }
                    mCurrentType = TYPE_NAMING;
                    mDrawerLockListener.lockDrawer();
                    mDrawerLockListener.setTitle(getString(R.string.add_quiz_title));
                } else {
                    if (mCurrentPage == UNKNOWN_CURRENT_PAGE) {
                        mCurrentPage = quizEntry.getPages().size() - 1;
                    }
                    if (mCurrentType == TYPE_SAVING) {
                        mCurrentType = TYPE_CHOOSER;
                    }
                    if (mCurrentType == DEFAULT_STATUS) {
                        mCurrentType = TYPE_CHOOSER;
                    }
                    if (finalSavedInstanceState == null) {
                        mViewModel.setEditedQuizEntry(new QuizEntry(quizEntry));
                    }
                    mDrawerLockListener.unlockDrawer();
                }
                invalidateViews();
                showPage();
                getActivity().invalidateOptionsMenu();
            }
        });
        mViewModel.getEditedQuizEntry().observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry editedQuizEntry) {
                if (editedQuizEntry == null && mQuizEntry != null) {
                    // Copy values.
                    mEditedQuizEntry = new QuizEntry(mQuizEntry);
                } else {
                    mEditedQuizEntry = editedQuizEntry;
                }
                invalidateViews();
                showPage();
                getActivity().invalidateOptionsMenu();
                if (mEditedQuizEntry != null) {
                    mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
                }
            }
        });

        if (savedInstanceState != null) {
            mCurrentType = savedInstanceState.getInt(TYPE_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_builder, menu);
        MenuItem playItem = menu.findItem(R.id.action_play);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        MenuItem addPageItem = menu.findItem(R.id.action_add_page);
        MenuItem deletePageItem = menu.findItem(R.id.action_delete_page);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (mQuizEntry == null) {
            playItem.setVisible(false);
            saveItem.setVisible(true);
            addPageItem.setVisible(false);
            deletePageItem.setVisible(false);
            deleteItem.setVisible(false);
        } else {
            playItem.setVisible(true);
            saveItem.setVisible(true);
            addPageItem.setVisible(true);
            deleteItem.setVisible(true);
            if (mEditedQuizEntry != null && mCurrentPage < mEditedQuizEntry.getPages().size()) {
                deletePageItem.setVisible(true);
            } else {
                deletePageItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                setEditedQuizEntry(mEditedQuizEntry);
                if (mQuizEntry.equals(mEditedQuizEntry)) {
                    playQuiz();
                } else {
                    showUnsavedDialog(UNSAVED_PLAY_DIALOG_TAG);
                }
                return true;
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_add_page:
                addNextPage();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog(getString(R.string.delete_quiz_message));
                return true;
            case R.id.action_delete_page:
                String discardTitle = getString(R.string.discard_dialog_title);
                String discardMessage = getString(R.string.discard_dialog_message);
                showDeletePageDialog(discardTitle, discardMessage);
                return true;
            case android.R.id.home:
                showUnsavedDialog(UNSAVED_DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                if (mQuizEntry == null) {
                    saveItem();
                } else {
                    mEditedQuizEntry.setName(mQuizNameEdit.getText().toString().trim());
                    mCurrentPage = 0;
                    mCurrentType = mEditedQuizEntry.getPages().get(0).getType();
                    showPage();
                    mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
                }
            case R.id.previous_button:
                if (mCurrentPage > 0) {
                    setEditedQuizEntry(mEditedQuizEntry);
                    mCurrentPage--;
                    showPage();
                }
                break;
            case R.id.next_button:
                if (mQuizEntry == null) {
                    saveItem();
                } else if (mCurrentType == TYPE_NAMING) {
                    mEditedQuizEntry.setName(mQuizNameEdit.getText().toString().trim());
                    mCurrentPage = 0;
                    mCurrentType = mEditedQuizEntry.getPages().get(0).getType();
                    showPage();
                } else if (mCurrentPage <= mEditedQuizEntry.getPages().size()) {
                    setEditedQuizEntry(mEditedQuizEntry);
                    mCurrentPage++;
                    showPage();
                } else {
                    // This should never happen.
                    Log.w(TAG, "mCurrentPage: " + mCurrentPage + "\tpages size: " + mEditedQuizEntry.getPages().size());
                    mCurrentPage = 0;
                    showPage();
                }
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
        if (mCurrentType == TYPE_SAVING) {
            return;
        }
        if (mEditedQuizEntry != null && mCurrentPage < mEditedQuizEntry.getPages().size()) {
            mEditedQuizEntry.getPages().get(mCurrentPage).setCorrectAnswers(getCorrectAnswers());
        }
    }

    @Override
    public void deletePositiveClick() {
        deleteItem();
    }

    @Override
    public void deletePagePositiveClick() {
        deletePage();
        if (mCurrentPage > 0) {
            mCurrentPage--;
        }
        getActivity().invalidateOptionsMenu();
        showPage();
    }

    @Override
    public void unsavedPositiveClick(String tag) {
        if (UNSAVED_DIALOG_TAG.equals(tag)) {
            getActivity().finish();
        } else if (UNSAVED_PLAY_DIALOG_TAG.equals(tag)) {
            playQuiz();
        } else {
            throw new IllegalArgumentException("Unknown fragment tag: " + tag);
        }
    }

    private void deletePage() {
        int pageCount = mEditedQuizEntry.getPages().size();
        if (mCurrentPage < pageCount) {
            mEditedQuizEntry.getPages().remove(mCurrentPage);
        }
    }

    private void playQuiz() {
        mDrawerLockListener.playQuiz(mEditedQuizEntry);
    }

    /**
     * Add page on next position
     */
    private void addNextPage() {
        QuizPage quizPage = new QuizPage(mCurrentType, "", new ArrayList<String>(), new ArrayList<String>());
        mEditedQuizEntry.getPages().add(mCurrentPage + 1, quizPage);
        mCurrentPage++;
        showPage();
    }

    /**
     * Set new type on {@link #mCurrentType}.
     * Changes quiz form in current page.
     *
     * @param quizType type to be set.{@link com.example.android.quizbuilder.data.database.QuizConstants}
     */
    public void setQuizType(int quizType) {
        if (mQuizEntry != null && mCurrentType != quizType) {
            if ((mCurrentType == TYPE_CHOOSER || mCurrentType == TYPE_PICKER || mCurrentType == TYPE_SWITCH)
                    && (quizType == TYPE_CHOOSER || quizType == TYPE_PICKER || quizType == TYPE_SWITCH)) {
                mCurrentType = quizType;
            } else {
                mCurrentType = quizType;
                clearPage();
            }
            invalidateViews();
        }
    }

    /**
     * Get user input and if is valid save {@link QuizEntry} into database, otherwise return.
     */
    private void saveItem() {

        if (mQuizEntry == null) {

            // Read from input fields.
            QuizEntry quizEntry = new QuizEntry(new Date(), "");

            setEditedQuizEntry(quizEntry);
            if (TextUtils.isEmpty(quizEntry.getName())) {
                Toast.makeText(getContext(), getString(R.string.error_quiz_name), Toast.LENGTH_SHORT).show();
                return;
            }

            mCurrentType = TYPE_SAVING;
            invalidateViews();

            // Insert new QuizEntry into database.
            final LiveData<Long> newQuizIdLive = mViewModel.insertItem(quizEntry);
            // Observe LiveData in order to get newly inserted id.
            newQuizIdLive.observe(this, new Observer<Long>() {
                @Override
                public void onChanged(@Nullable Long quizId) {
                    if (quizId != null) {

                        newQuizIdLive.removeObserver(this);

                        mViewModel.initQuizEntry(quizId);

                        if (quizId != -1) {

                            getActivity().getIntent().putExtra(DetailActivity.QUIZ_ID_KEY, quizId);

                            Toast.makeText(getContext(), getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.error_insert, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } else {

            setEditedQuizEntry(mEditedQuizEntry);
            mEditedQuizEntry.setDate(new Date());

            mCurrentType = TYPE_SAVING;
            invalidateViews();

            mViewModel.updateItem(mEditedQuizEntry);
            mViewModel.setEditedQuizEntry(mEditedQuizEntry);
            Toast.makeText(getContext(), getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * User confirmed to delete item.
     */
    private void deleteItem() {
        // Observe LiveData in order to get current QuizEntry.
        final LiveData<QuizEntry> quizDetailFragmentLiveData = mViewModel.getQuizEntry(mQuizEntry.getId());
        quizDetailFragmentLiveData.observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry quizEntry) {
                // Remove observer. There is no need to observe it anymore.
                quizDetailFragmentLiveData.removeObserver(this);
                if (quizEntry != null) {
                    // If QuizEntry isnt null delete QuizEntry.
                    mViewModel.deleteItem(quizEntry);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private void showPage() {
        clearPage();
        if (mEditedQuizEntry != null && !mEditedQuizEntry.getPages().isEmpty() && mCurrentPage < mEditedQuizEntry.getPages().size()) {
            // Show specific page.
            QuizPage quizPage = mEditedQuizEntry.getPages().get(mCurrentPage);
            mCurrentType = quizPage.getType();

            invalidateViews();

            mQuizQuestionEdit.setText(quizPage.getQuestion());

            if (mCurrentType == TYPE_CHOOSER ||
                    mCurrentType == TYPE_PICKER ||
                    mCurrentType == TYPE_SWITCH) {
                if (!quizPage.getAnswers().isEmpty()) {
                    mQuizEdit1.setText(quizPage.getAnswers().get(0));
                    mQuizEdit2.setText(quizPage.getAnswers().get(1));
                    mQuizEdit3.setText(quizPage.getAnswers().get(2));
                    mQuizEdit4.setText(quizPage.getAnswers().get(3));
                }
            } else if (mCurrentPage == TYPE_NAMING) {
                mQuizNameEdit.setText(mEditedQuizEntry.getName());
            }
            setCorrectAnswers(quizPage);
        } else {
            invalidateViews();
        }
    }

    /**
     * Set page on {@link QuizEntry} object.
     *
     * @param quizEntry object on which page will be set.
     */
    private void setEditedQuizEntry(QuizEntry quizEntry) {

        if (quizEntry == null) {
            return;
        }

        if (mCurrentType == TYPE_NAMING) {

            quizEntry.setDate(new Date());
            String quizName = mQuizNameEdit.getText().toString().trim();
            quizEntry.setName(quizName);
            return;
        }

        String question = mQuizQuestionEdit.getText().toString().trim();

        List<String> answers = new ArrayList<>();

        if (mCurrentType == TYPE_CHOOSER ||
                mCurrentType == TYPE_PICKER ||
                mCurrentType == TYPE_SWITCH) {

            String answer1 = mQuizEdit1.getText().toString().trim();
            String answer2 = mQuizEdit2.getText().toString().trim();
            String answer3 = mQuizEdit3.getText().toString().trim();
            String answer4 = mQuizEdit4.getText().toString().trim();

            answers = new ArrayList<>();
            answers.add(answer1);
            answers.add(answer2);
            answers.add(answer3);
            answers.add(answer4);
        }

        List<String> correctAnswers = getCorrectAnswers();

        if (shouldSave()) {
            if (quizEntry.getPages().size() <= 0 || mCurrentPage >= quizEntry.getPages().size()) {
                // Add new page.
                QuizPage quizPage = new QuizPage(mCurrentType, question, correctAnswers, answers);
                quizEntry.getPages().add(quizPage);
            } else {
                // Update current page.
                quizEntry.getPages().get(mCurrentPage).setType(mCurrentType);
                quizEntry.getPages().get(mCurrentPage).setQuestion(question);
                quizEntry.getPages().get(mCurrentPage).setAnswers(answers);
                quizEntry.getPages().get(mCurrentPage).setCorrectAnswers(correctAnswers);
            }
        } else {
            deletePage();
        }
    }

    /**
     * @return <code>false</code> if all fields are empty.
     * <code>true</code> otherwise.
     */
    private boolean shouldSave() {

        boolean shouldSave = true;

        if (mCurrentType == TYPE_CHOOSER ||
                mCurrentType == TYPE_PICKER ||
                mCurrentType == TYPE_SWITCH) {

            String answer1 = mQuizEdit1.getText().toString().trim();
            String answer2 = mQuizEdit2.getText().toString().trim();
            String answer3 = mQuizEdit3.getText().toString().trim();
            String answer4 = mQuizEdit4.getText().toString().trim();

            if (TextUtils.isEmpty(answer1) && TextUtils.isEmpty(answer2) &&
                    TextUtils.isEmpty(answer3) && TextUtils.isEmpty(answer4)) {
                String question = mQuizQuestionEdit.getText().toString().trim();
                if (TextUtils.isEmpty(question)) {
                    List<String> correctAnswers = getCorrectAnswers();
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

    private void clearPage() {
        mQuizQuestionEdit.setText("");
        mQuizEdit1.setText("");
        mQuizEdit2.setText("");
        mQuizEdit3.setText("");
        mQuizEdit4.setText("");
        mAnswerEdit.setText("");
    }

    /**
     * Used to invalidate views.
     * Based on {@link #mCurrentType} set visibility on views.
     */
    private void invalidateViews() {

        setupBottomViews();

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
        mQuizNameEdit.setVisibility(View.INVISIBLE);

        switch (mCurrentType) {
            case TYPE_NAMING:
                mQuizNameEdit.setVisibility(View.VISIBLE);
                break;
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
        } else if (mEditedQuizEntry != null) {
            mSaveButton.setVisibility(View.INVISIBLE);
            int pageCount = mEditedQuizEntry.getPages().size();
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

    private void setCorrectAnswers(QuizPage quizPage) {
        List<String> correctAnswers = quizPage.getCorrectAnswers();
        if (correctAnswers.isEmpty()) {
            return;
        }
        if (mCurrentType == TYPE_CHOOSER ||
                mCurrentType == TYPE_PICKER ||
                mCurrentType == TYPE_SWITCH) {
            CompoundButton[] views = new CompoundButton[4];
            if (mCurrentType == TYPE_CHOOSER) {
                views[0] = mRadioButton1;
                views[1] = mRadioButton2;
                views[2] = mRadioButton3;
                views[3] = mRadioButton4;
            } else if (mCurrentType == TYPE_PICKER) {
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
            for (int i = 0; i < correctAnswers.size(); i++) {
                int correctAnswer = Integer.parseInt(correctAnswers.get(i));
                views[correctAnswer].setChecked(true);
            }
        } else if (mCurrentType == TYPE_TEXT_ANSWER) {
            mAnswerEdit.setText(correctAnswers.get(0));
        } else {
            throw new IllegalStateException("Not supported type for correctAnswer: " + mCurrentType);
        }
    }

    /**
     * Get correct answers from user input.
     *
     * @return {@link List<String>} with correct answers.
     */
    private List<String> getCorrectAnswers() {
        List<String> correctAnswers = new ArrayList<>();
        if (mCurrentType == TYPE_CHOOSER ||
                mCurrentType == TYPE_PICKER ||
                mCurrentType == TYPE_SWITCH) {
            CompoundButton[] views = new CompoundButton[4];
            int[] answers = new int[]{CORRECT_ANSWER_FIRST,
                    CORRECT_ANSWER_SECOND,
                    CORRECT_ANSWER_THIRD,
                    CORRECT_ANSWER_FOURTH};
            if (mCurrentType == TYPE_CHOOSER) {
                views[0] = mRadioButton1;
                views[1] = mRadioButton2;
                views[2] = mRadioButton3;
                views[3] = mRadioButton4;
            } else if (mCurrentType == TYPE_PICKER) {
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
        } else if (mCurrentType == TYPE_TEXT_ANSWER) {
            String answer = mAnswerEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(answer)) {
                correctAnswers.add(answer);
            }
        } else {
            throw new IllegalStateException("Not supported type for correctAnswer: " + mCurrentType);
        }
        return correctAnswers;
    }

    private void showDeletePageDialog(String title, String message) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DELETE_PAGE_DIALOG_TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        DialogFragment dialogFragment = DeletePageDialogFragment.newInstance(title, message);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(fragmentManager, DELETE_PAGE_DIALOG_TAG);
    }

    private void showDeleteConfirmationDialog(String message) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DELETE_DIALOG_TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        DialogFragment dialogFragment = DeleteAlertDialogFragment.newInstance(message);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(fragmentManager, DELETE_DIALOG_TAG);
    }

    private void showUnsavedDialog(String unsavedDialogTag) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(unsavedDialogTag);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        DialogFragment dialogFragment = new UnsavedAlertDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(fragmentManager, unsavedDialogTag);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE_KEY, mCurrentType);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        mViewModel.setEditedQuizEntry(mEditedQuizEntry);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDrawerLockListener = (DetailFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DetailFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDrawerLockListener = null;
    }
}
