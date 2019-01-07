package edu.temple.smartprompter.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;
import edu.temple.sp_res_lib.SpMediaManager;

public class CompleteAlarmDetailsFragment extends Fragment {

    private SpMediaManager mMediaMgr;
    private SpAlarmManager mAlarmMgr;
    private Alarm mAlarm;

    public CompleteAlarmDetailsFragment() {
        // required empty constructor
    }

    public static CompleteAlarmDetailsFragment newInstance(int alarmID) {
        CompleteAlarmDetailsFragment fragment = new CompleteAlarmDetailsFragment();
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
            mMediaMgr = new SpMediaManager(getActivity());
            mAlarmMgr = new SpAlarmManager(getActivity());
            mAlarm = mAlarmMgr.get(alarmID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_complete_alarm_detail,
                container, false);

        // --------------------------------------------------------------------------------------
        //          LABEL
        // --------------------------------------------------------------------------------------
        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getLabel());

        // --------------------------------------------------------------------------------------
        //          DATE
        // --------------------------------------------------------------------------------------
        TextView dateText = rootView.findViewById(R.id.date_text);
        dateText.setText(mAlarm.getDateString());

        // --------------------------------------------------------------------------------------
        //          TIME
        // --------------------------------------------------------------------------------------
        TextView timeText = rootView.findViewById(R.id.time_text);
        timeText.setText(mAlarm.getTimeString());

        // --------------------------------------------------------------------------------------
        //          ALARM_STATUS
        // --------------------------------------------------------------------------------------
        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatusString());

        if (mAlarm.isActive()) {
            Log.i(Constants.LOG_TAG, "Alarm is active!  Setting button to 'off' mode.");
            statusText.setBackgroundColor(Color.GREEN);
        } else {
            Log.i(Constants.LOG_TAG, "Alarm is inactive!  Setting button to 'on' mode.");
            statusText.setBackgroundColor(Color.WHITE);
        }

        // --------------------------------------------------------------------------------------
        //          TIME ACKNOWLEDGED
        // --------------------------------------------------------------------------------------
        TextView ackText = rootView.findViewById(R.id.ack_text);
        String rawAckTime = mAlarm.getTimeAcknowledged();

        if (rawAckTime == null || rawAckTime.isEmpty())
            ackText.setText(R.string.not_applicable);
        else ackText.setText(mAlarm.getTimeAcknowledged());

        // --------------------------------------------------------------------------------------
        //          TIME COMPLETE
        // --------------------------------------------------------------------------------------
        TextView compText = rootView.findViewById(R.id.comp_text);
        String rawCompTime = mAlarm.getTimeCompleted();

        if (rawCompTime == null || rawCompTime.isEmpty())
            compText.setText(R.string.not_applicable);
        else compText.setText(mAlarm.getTimeCompleted());

        // --------------------------------------------------------------------------------------
        //          COMPLETION IMAGE
        // --------------------------------------------------------------------------------------
        String rawImageID = mAlarm.getCompletionMediaID();
        Bitmap bitmap = mMediaMgr.getImage(rawImageID);

        ImageView imageView = rootView.findViewById(R.id.image_view);
        TextView errorMsg = rootView.findViewById(R.id.empty_view);

        if (bitmap != null) {
            Log.i(Constants.LOG_TAG, "Attempt to retrieve completion image was "
                    + "successful.  Forwarding Bitmap to image viewer.");
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            errorMsg.setVisibility(View.GONE);
        } else {
            Log.i(Constants.LOG_TAG, "Unable to retrieve completion image.  "
                    + "Displaying default text.");
            imageView.setVisibility(View.GONE);
            errorMsg.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

}