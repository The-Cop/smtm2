package ru.thecop.smtm2.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import ru.thecop.smtm2.R;

public class ConfirmDialogFragment extends DialogFragment {
    private String message;
    private DialogInterface.OnClickListener onClickListener;


    public static ConfirmDialogFragment newInstance(String message, DialogInterface.OnClickListener confirmOnClickListener) {
        ConfirmDialogFragment dialog = new ConfirmDialogFragment();
        dialog.message = message;
        dialog.onClickListener = confirmOnClickListener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.dialog_confirm_ok, onClickListener)
                .setNegativeButton(R.string.dialog_confirm_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
