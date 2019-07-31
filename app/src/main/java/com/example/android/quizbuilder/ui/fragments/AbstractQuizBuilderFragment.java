package com.example.android.quizbuilder.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.data.database.QuizPage;
import com.example.android.quizbuilder.interfaces.DetailFragmentListener;
import com.example.android.quizbuilder.ui.DetailActivity;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModel;
import com.example.android.quizbuilder.viewmodels.QuizDetailViewModelFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_CHOOSER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_NAMING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_PICKER;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SAVING;
import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_SWITCH;

abstract class AbstractQuizBuilderFragment extends AbstractQuizPageBuilderFragment {

    private static final String TAG = AbstractQuizBuilderFragment.class.getSimpleName();

    private DetailFragmentListener mDrawerLockListener;

    private QuizDetailViewModel mViewModel;

    private QuizEntry mQuizEntry;
    private QuizEntry mEditedQuizEntry;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle finalSavedInstanceState = savedInstanceState;

        QuizDetailViewModelFactory factory = new QuizDetailViewModelFactory(QuizRepository.getInstance(getContext()));
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(QuizDetailViewModel.class);
        mViewModel.getQuizEntry().observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry quizEntry) {
                mQuizEntry = quizEntry;
                if (quizEntry == null) {
                    if (getCurrentPage() == UNKNOWN_CURRENT_PAGE) {
                        setCurrentPage(0);
                    }
                    setCurrentType(TYPE_NAMING);
                    mDrawerLockListener.lockDrawer();
                    mDrawerLockListener.setTitle(getString(R.string.add_quiz_title));
                } else {
                    if (getCurrentPage() == UNKNOWN_CURRENT_PAGE) {
                        setCurrentPage(quizEntry.getPages().size() - 1);
                    }
                    if (getCurrentType() == TYPE_SAVING || getCurrentType() == DEFAULT_STATUS) {
                        setCurrentType(TYPE_CHOOSER);
                    }
                    if (finalSavedInstanceState == null) {
                        mViewModel.setEditedQuizEntry(new QuizEntry(quizEntry));
                    }
                    mDrawerLockListener.unlockDrawer();
                }
                invalidateViews(getCurrentType());
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
                invalidateViews(getCurrentType());
                showPage();
                getActivity().invalidateOptionsMenu();
                if (mEditedQuizEntry != null) {
                    mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
                }
            }
        });
    }

    @Override
    protected boolean onQuizNameEditKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mQuizEntry == null) {
                saveItem();
            } else {
                mEditedQuizEntry.setName(getQuizName());
                setCurrentPage(0);
                setCurrentType(mEditedQuizEntry.getPages().get(0).getType());
                showPage();
                mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
            }
            hideQuizNameKeyboard();
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveButtonClick() {
        if (mQuizEntry == null) {
            saveItem();
        } else {
            mEditedQuizEntry.setName(getQuizName());
            setCurrentPage(0);
            setCurrentType(mEditedQuizEntry.getPages().get(0).getType());
            showPage();
            mDrawerLockListener.setTitle(mEditedQuizEntry.getName());
        }
    }

    @Override
    protected void onPreviousButtonClick() {
        if (getCurrentPage() > 0) {
            setEditedQuizEntry(mEditedQuizEntry);
            setPreviousPage();
            showPage();
        }
    }

    @Override
    protected void onNextButtonClick() {
        if (mQuizEntry == null) {
            saveItem();
        } else if (getCurrentType() == TYPE_NAMING) {
            mEditedQuizEntry.setName(getQuizName());
            setCurrentPage(0);
            setCurrentType(mEditedQuizEntry.getPages().get(0).getType());
            showPage();
        } else if (getCurrentPage() <= mEditedQuizEntry.getPages().size()) {
            setEditedQuizEntry(mEditedQuizEntry);
            setNextPage();
            showPage();
        } else {
            // This should never happen.
            Log.w(TAG, "mCurrentPage: " + getCurrentPage() + "\tpages size: " + mEditedQuizEntry.getPages().size());
            setCurrentPage(0);
            showPage();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        if (getCurrentType() == TYPE_SAVING) {
            return;
        }
        if (mEditedQuizEntry != null && getCurrentPage() < mEditedQuizEntry.getPages().size()) {
            mEditedQuizEntry.getPages().get(getCurrentPage()).setCorrectAnswers(getCorrectAnswers(getCurrentType()));
        }
    }

    @Override
    protected boolean isQuizNotNull() {
        return mEditedQuizEntry != null;
    }

    @Override
    protected int getQuizPages() {
        return mEditedQuizEntry.getPages().size();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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

    /**
     * Changes quiz form in current page.
     *
     * @param quizType type to be set.{@link com.example.android.quizbuilder.data.database.QuizConstants}
     */
    public void setQuizType(int quizType) {
        if (mQuizEntry != null && getCurrentType() != quizType) {
            if ((getCurrentType() == TYPE_CHOOSER || getCurrentType() == TYPE_PICKER || getCurrentType() == TYPE_SWITCH) &&
                    (quizType == TYPE_CHOOSER || quizType == TYPE_PICKER || quizType == TYPE_SWITCH)) {
                setCurrentType(quizType);
            } else {
                setCurrentType(quizType);
                clearPage();
            }
            invalidateViews(getCurrentType());
        }
    }

    protected boolean deletePage() {
        int pageCount = mEditedQuizEntry.getPages().size();
        if (getCurrentPage() < pageCount) {
            mEditedQuizEntry.getPages().remove(getCurrentPage());
            return true;
        }
        return false;
    }

    protected void playQuiz() {
        mDrawerLockListener.playQuiz(mEditedQuizEntry);
    }

    /**
     * Add blank page on next position
     */
    protected void addNextBlankPage() {
        QuizPage quizPage = new QuizPage(getCurrentType(), "", new ArrayList<String>(), new ArrayList<String>());
        mEditedQuizEntry.getPages().add(getCurrentPage() + 1, quizPage);
        setNextPage();
        showPage();
    }

    /**
     * Get user input and if is valid save {@link QuizEntry} into database, otherwise return.
     */
    protected void saveItem() {

        setCurrentType(TYPE_SAVING);

        if (mQuizEntry == null) {

            // Read from input fields.
            QuizEntry quizEntry = new QuizEntry(new Date(), "");

            setEditedQuizEntry(quizEntry);
            if (TextUtils.isEmpty(quizEntry.getName())) {
                Toast.makeText(getContext(), getString(R.string.error_quiz_name), Toast.LENGTH_SHORT).show();
                return;
            }

            invalidateViews(getCurrentType());

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

            invalidateViews(getCurrentType());

            mViewModel.updateItem(mEditedQuizEntry);
            mViewModel.setEditedQuizEntry(mEditedQuizEntry);
            Toast.makeText(getContext(), getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * User confirmed to delete item.
     */
    protected void deleteItem() {
        // Observe LiveData in order to get current QuizEntry.
        final LiveData<QuizEntry> quizDetailFragmentLiveData = mViewModel.getQuizEntry(mQuizEntry.getId());
        quizDetailFragmentLiveData.observe(this, new Observer<QuizEntry>() {
            @Override
            public void onChanged(@Nullable QuizEntry quizEntry) {
                // Remove observer. There is no need to observe it anymore.
                quizDetailFragmentLiveData.removeObserver(this);
                if (quizEntry != null) {
                    // If QuizEntry isn't null delete QuizEntry.
                    mViewModel.deleteItem(quizEntry);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });
    }

    protected void showPage() {
        clearPage();
        if (mEditedQuizEntry != null && !mEditedQuizEntry.getPages().isEmpty() && getCurrentPage() < mEditedQuizEntry.getPages().size()) {
            // Show specific page.
            QuizPage quizPage = mEditedQuizEntry.getPages().get(getCurrentPage());
            setCurrentType(quizPage.getType());

            invalidateViews(getCurrentType());

            setQuizQuestionEditText(quizPage.getQuestion());

            if (getCurrentType() == TYPE_CHOOSER ||
                    getCurrentType() == TYPE_PICKER ||
                    getCurrentType() == TYPE_SWITCH) {
                if (!quizPage.getAnswers().isEmpty()) {
                    setQuizAnswers(quizPage.getAnswers());
                }
            } else if (getCurrentType() == TYPE_NAMING) {
                setQuizNameEditText(mEditedQuizEntry.getName());
            }
            setCorrectAnswers(quizPage, getCurrentType());
        } else {
            invalidateViews(getCurrentType());
        }
    }

    /**
     * Set page on {@link QuizEntry} object.
     *
     * @param quizEntry object on which page will be set.
     */
    protected void setEditedQuizEntry(QuizEntry quizEntry) {

        if (quizEntry == null) {
            return;
        }

        if (getCurrentType() == TYPE_NAMING) {

            quizEntry.setDate(new Date());
            String quizName = getQuizName();
            quizEntry.setName(quizName);
            return;
        }

        String question = getQuizQuestion();

        List<String> answers = getQuizAnswers(getCurrentType());

        List<String> correctAnswers = getCorrectAnswers(getCurrentType());

        if (shouldSave(getCurrentType())) {
            if (quizEntry.getPages().size() <= 0 || getCurrentPage() >= quizEntry.getPages().size()) {
                // Add new page.
                QuizPage quizPage = new QuizPage(getCurrentType(), question, correctAnswers, answers);
                quizEntry.getPages().add(quizPage);
            } else {
                // Update current page.
                quizEntry.getPages().get(getCurrentPage()).setType(getCurrentType());
                quizEntry.getPages().get(getCurrentPage()).setQuestion(question);
                quizEntry.getPages().get(getCurrentPage()).setAnswers(answers);
                quizEntry.getPages().get(getCurrentPage()).setCorrectAnswers(correctAnswers);
            }
        } else {
            deletePage();
        }
    }

    protected QuizEntry getQuizEntry() {
        return mQuizEntry;
    }

    protected QuizEntry getEditedQuizEntry() {
        return mEditedQuizEntry;
    }

}
