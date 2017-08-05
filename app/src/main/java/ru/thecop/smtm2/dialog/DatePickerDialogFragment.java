package ru.thecop.smtm2.dialog;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import org.joda.time.LocalDateTime;

public class DatePickerDialogFragment extends DialogFragment {

    private LocalDateTime date;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public static DatePickerDialogFragment newInstance(LocalDateTime date, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Bundle args = new Bundle();
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(args);
        fragment.date = date;
        fragment.onDateSetListener = onDateSetListener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                onDateSetListener,
                date.getYear(),
                date.getMonthOfYear() - 1,
                date.getDayOfMonth());
        return datePickerDialog;
    }
}
