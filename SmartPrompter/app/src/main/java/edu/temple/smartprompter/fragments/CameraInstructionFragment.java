package edu.temple.smartprompter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class CameraInstructionFragment extends Fragment {

    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    public CameraInstructionFragment() {
        // required empty constructor
    }

    public static CameraInstructionFragment newInstance(int alarmID) {
        CameraInstructionFragment fragment = new CameraInstructionFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_instruction,
                container, false);
        return rootView;
    }

}