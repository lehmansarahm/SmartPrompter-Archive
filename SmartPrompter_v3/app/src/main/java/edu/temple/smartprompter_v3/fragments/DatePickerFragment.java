package edu.temple.smartprompter_v3.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import edu.temple.smartprompter_v3.utils.Constants;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        void onDatePicked(String getGuid, int year, int month, int day);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private String mAlarmGUID;
    private int mYear, mMonth, mDay;

    private DatePickerListener mListener;

    public static DatePickerFragment newInstance(String alarmGUID, int[] date) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGUID);
        args.putInt(Constants.BUNDLE_ARG_YEAR, date[0]);
        args.putInt(Constants.BUNDLE_ARG_MONTH, date[1]);
        args.putInt(Constants.BUNDLE_ARG_DAY, date[2]);
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

        mAlarmGUID = getArguments().getString(Constants.BUNDLE_ARG_ALARM_GUID);
        mYear = getArguments().getInt(Constants.BUNDLE_ARG_YEAR);
        mMonth = getArguments().getInt(Constants.BUNDLE_ARG_MONTH);
        mDay = getArguments().getInt(Constants.BUNDLE_ARG_DAY);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDatePicked(mAlarmGUID, year, month, day);
    }

}