package com.example.android.quizbuilder.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.interfaces.DeleteDialogListener;

public class DeleteAlertDialogFragment extends DialogFragment {

    private static final String MESSAGE_KEY = "message";

    private DeleteDialogListener mCallback;

    public static DeleteAlertDialogFragment newInstance(String message) {
        DeleteAlertDialogFragment fragment = new DeleteAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE_KEY, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String message = getArguments().getString(MESSAGE_KEY);
        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.deletePositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }).create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (DeleteDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getContext() + " must implement DeleteDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
