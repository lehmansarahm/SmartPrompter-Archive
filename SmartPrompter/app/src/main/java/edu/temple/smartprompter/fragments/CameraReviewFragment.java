package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class CameraReviewFragment extends Fragment {

    public interface ImageReviewListener {
        void onImageAccepted(int alarmID);
        void onImageRejected(int alarmID);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private ImageReviewListener mListener;
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

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ImageReviewListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement ImageReviewListener";
            Log.e(edu.temple.sp_res_lib.utils.Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

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
        final View rootView = inflater.inflate(R.layout.fragment_camera_review,
                container, false);
        return rootView;
    }

}