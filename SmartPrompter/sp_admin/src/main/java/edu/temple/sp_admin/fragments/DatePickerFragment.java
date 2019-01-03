package edu.temple.sp_admin.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import edu.temple.sp_admin.utils.Constants;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        void onDatePickerRequested(int alarmID, int[] date);
        void onDatePicked(int alarmID, int year, int month, int day);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final String BUNDLE_ARG_ALARM_ID = "bundle_arg_alarm_id";
    private static final String BUNDLE_ARG_YEAR = "bundle_arg_year";
    private static final String BUNDLE_ARG_MONTH = "bundle_arg_month";
    private static final String BUNDLE_ARG_DAY = "bundle_arg_day";

    private int mAlarmID;
    private int mYear, mMonth, mDay;

    private DatePickerListener mListener;

    public static DatePickerFragment newInstance(int alarmID, int[] date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_ALARM_ID, alarmID);
        args.putInt(BUNDLE_ARG_YEAR, date[0]);
        args.putInt(BUNDLE_ARG_MONTH, date[1]);
        args.putInt(BUNDLE_ARG_DAY, date[2]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DatePickerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null) {
            Log.e(Constants.LOG_TAG, "Cannot initialize dialog without appropriate settings!");
            return null;
        }

        mAlarmID = getArguments().getInt(BUNDLE_ARG_ALARM_ID);
        mYear = getArguments().getInt(BUNDLE_ARG_YEAR);
        mMonth = getArguments().getInt(BUNDLE_ARG_MONTH);
        mDay = getArguments().getInt(BUNDLE_ARG_DAY);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDatePicked(mAlarmID, year, month, day);
    }

}