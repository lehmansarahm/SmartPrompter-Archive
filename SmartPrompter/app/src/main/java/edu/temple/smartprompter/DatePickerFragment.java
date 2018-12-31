package edu.temple.smartprompter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        void onDatePickerRequested(int[] date);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final String BUNDLE_ARG_YEAR = "bundle_arg_year";
    private static final String BUNDLE_ARG_MONTH = "bundle_arg_month";
    private static final String BUNDLE_ARG_DAY = "bundle_arg_day";

    private int mYear, mMonth, mDay;

    public static DatePickerFragment newInstance(int[] date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_YEAR, date[0]);
        args.putInt(BUNDLE_ARG_MONTH, date[1]);
        args.putInt(BUNDLE_ARG_DAY, date[2]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mYear = getArguments().getInt(BUNDLE_ARG_YEAR);
            mMonth = getArguments().getInt(BUNDLE_ARG_MONTH);
            mDay = getArguments().getInt(BUNDLE_ARG_DAY);
        } else {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // TODO - do something with the date chosen by the user
    }
}