package edu.temple.smartprompter.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public interface TimePickerListener {
        void onTimePickerRequested(int[] time);
        void onTimePicked(int hourOfDay, int minute);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static final String BUNDLE_ARG_HOUR = "bundle_arg_hour";
    private static final String BUNDLE_ARG_MINUTE = "bundle_arg_minute";

    private int mHour, mMinute;
    private boolean mIs24Hr;

    private TimePickerListener mListener;

    public static TimePickerFragment newInstance(int[] time) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ARG_HOUR, time[0]);
        args.putInt(BUNDLE_ARG_MINUTE, time[1]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TimePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TimePickerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mHour = getArguments().getInt(BUNDLE_ARG_HOUR);
            mMinute = getArguments().getInt(BUNDLE_ARG_MINUTE);
            mIs24Hr = false;
        } else {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mIs24Hr = DateFormat.is24HourFormat(getActivity());
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, mHour, mMinute, mIs24Hr);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimePicked(hourOfDay, minute);
    }
}