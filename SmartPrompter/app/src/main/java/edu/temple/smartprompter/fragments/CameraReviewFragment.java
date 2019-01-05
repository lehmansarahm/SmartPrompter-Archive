package edu.temple.smartprompter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class CameraReviewFragment extends Fragment {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    public CameraReviewFragment() {
        // required empty constructor
    }

    public static CameraReviewFragment newInstance(int alarmID) {
        CameraReviewFragment fragment = new CameraReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_ARG_ALARM_ID, alarmID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int alarmID = getArguments().getInt(Constants.BUNDLE_ARG_ALARM_ID);
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
        }
    }

}