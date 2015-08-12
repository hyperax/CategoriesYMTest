package ru.yandex.money.categories.view.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Window;

public class ProgressDialogFragment extends DialogFragment{

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MESSAGE = "arg_message";
    private static final String ARG_CANCELABLE = "arg_cancelable";

    public static ProgressDialogFragment newInstance(String title, String message, boolean cancelable) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_CANCELABLE, cancelable);

        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(getArguments().getBoolean(ARG_CANCELABLE, false));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE, null);
        if (TextUtils.isEmpty(title)) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle(title);
        }
        dialog.setMessage(args.getString(ARG_MESSAGE));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }
}