package edu.temple.smartprompter_v3.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.data.Alarm;
import edu.temple.smartprompter_v3.data.FirebaseConnector;
import edu.temple.smartprompter_v3.utils.Constants;
import edu.temple.smartprompter_v3.utils.StorageUtil;

public class AlarmLogFragment extends Fragment {

    private Alarm mAlarm;
    private View rootView;

    public AlarmLogFragment() {
        // Required empty public constructor
    }

    public static AlarmLogFragment newInstance(String guid) {
        AlarmLogFragment fragment = new AlarmLogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_ARG_ALARM_GUID, guid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String alarmGUID = getArguments().getString(Constants.BUNDLE_ARG_ALARM_GUID);
            FirebaseConnector.getAlarmByGuid(alarmGUID, result -> {
                mAlarm = (Alarm)result;
                initView();
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alarm_log, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initView() {
        TextView labelText = rootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        TextView statusText = rootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());

        TextView origDateTimeText = rootView.findViewById(R.id.orig_date_time_text);
        origDateTimeText.setText(mAlarm.getAlarmDateTimeString());

        TextView timeAcknowledgedText = rootView.findViewById(R.id.time_acknowledged_text);
        timeAcknowledgedText.setText(mAlarm.getAcknowledgedDateTimeString());

        TextView timeCompletedText = rootView.findViewById(R.id.time_completed_text);
        timeCompletedText.setText(mAlarm.getCompletionDateTimeString());

        TextView photoPathText = rootView.findViewById(R.id.photo_path_text);
        photoPathText.setText(mAlarm.getPhotoPath());

        ImageView imageView = rootView.findViewById(R.id.image_view);
        TextView errorMsg = rootView.findViewById(R.id.empty_view);

        Bitmap bitmap = StorageUtil.getImageFromFile(this.getContext(), mAlarm.getPhotoPath());
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
    }

}