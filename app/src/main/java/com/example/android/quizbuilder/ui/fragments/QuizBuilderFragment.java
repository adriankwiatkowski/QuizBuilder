package com.example.android.quizbuilder.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.interfaces.DeleteDialogListener;
import com.example.android.quizbuilder.interfaces.DeletePageDialogListener;
import com.example.android.quizbuilder.interfaces.UnsavedDialogListener;
import com.example.android.quizbuilder.ui.dialogs.DeleteAlertDialogFragment;
import com.example.android.quizbuilder.ui.dialogs.DeletePageDialogFragment;
import com.example.android.quizbuilder.ui.dialogs.UnsavedAlertDialogFragment;

public class QuizBuilderFragment extends AbstractQuizBuilderFragment
        implements DeletePageDialogListener, DeleteDialogListener, UnsavedDialogListener {

    private static final String TAG = QuizBuilderFragment.class.getSimpleName();

    private static final String DELETE_DIALOG_TAG = "delete_tag";
    private static final String DELETE_PAGE_DIALOG_TAG = "delete_page_tag";
    private static final String UNSAVED_DIALOG_TAG = "unsaved_tag";
    private static final String UNSAVED_PLAY_DIALOG_TAG = "unsaved_play_tag";

    public QuizBuilderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        if (getQuizEntry() == null) {
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
            if (getEditedQuizEntry() != null && getCurrentPage() < getEditedQuizEntry().getPages().size()) {
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
                setEditedQuizEntry(getEditedQuizEntry());
                if (getQuizEntry().equals(getEditedQuizEntry())) {
                    playQuiz();
                } else {
                    showUnsavedDialog(UNSAVED_PLAY_DIALOG_TAG);
                }
                return true;
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_add_page:
                addNextBlankPage();
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
    public void deletePositiveClick() {
        deleteItem();
    }

    @Override
    public void deletePagePositiveClick() {
        deletePage();
        if (getCurrentPage() > 0) {
            setPreviousPage();
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
}
