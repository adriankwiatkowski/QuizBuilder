package com.example.android.quizbuilder.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.adapters.QuizAdapter;
import com.example.android.quizbuilder.data.QuizRepository;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.interfaces.DeleteDialogListener;
import com.example.android.quizbuilder.ui.dialogs.DeleteAlertDialogFragment;

import java.util.List;

public class ListFragment extends Fragment
        implements QuizAdapter.OnQuizClick, DeleteDialogListener {

    private static final String DELETE_DIALOG_TAG = "delete_dialog";

    public interface OnQuizSelected {
        void onQuizSelected(long quizId);
        void onNewQuiz();
    }

    private OnQuizSelected mCallback;
    private RecyclerView mRecyclerView;

    private QuizAdapter mAdapter;

    private FloatingActionButton mFab;
    private QuizRepository mRepository;

    private boolean mIsEmpty = true;

    public ListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mFab = rootView.findViewById(R.id.fab);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRepository = QuizRepository.getInstance(getContext());

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new QuizAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNewQuiz();
            }
        });

        mRepository.getItems().observe(this, new Observer<List<QuizEntry>>() {
            @Override
            public void onChanged(@Nullable List<QuizEntry> quizEntries) {
                mAdapter.setData(quizEntries);
                mIsEmpty = quizEntries == null || quizEntries.isEmpty();
                getActivity().invalidateOptionsMenu();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final QuizEntry quizEntry = mAdapter.getQuizList().get(viewHolder.getAdapterPosition());
                mAdapter.removeQuiz(position);
                Snackbar.make(viewHolder.itemView, getString(R.string.quiz_deleted), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAdapter.restoreQuiz(quizEntry, position);
                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    mRepository.deleteItem(quizEntry);
                                } else {
                                    super.onDismissed(transientBottomBar, event);
                                }
                            }
                        })
                        .show();

            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onQuizClick(long quizId) {
        mCallback.onQuizSelected(quizId);
    }

    @Override
    public void deletePositiveClick() {
        mRepository.deleteAllItems();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_delete_all);
        if (mIsEmpty) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteConfirmationDialog() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DELETE_DIALOG_TAG);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        DialogFragment dialogFragment = new DeleteAlertDialogFragment();
        dialogFragment.setTargetFragment(ListFragment.this, 0);
        dialogFragment.show(fragmentManager, DELETE_DIALOG_TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnQuizSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnQuizSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
