package edu.temple.smartprompter_v3.admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.StorageUtil;

public class AlarmLogActivity extends BaseActivity {

    private String mAlarmGUID;
    private Alarm mAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmGUID = getIntent().getStringExtra(Constants.BUNDLE_ARG_ALARM_GUID);
        FirebaseConnector.getAlarmByGuid(mAlarmGUID, result -> {
                if (result != null) mAlarm = (Alarm)result;
                else {
                    Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                            + "retrieve alarm with GUID: " + mAlarmGUID
                            + ".  Displaying default record.");
                    mAlarm = new Alarm();
                    mAlarmGUID = mAlarm.getGuid();
                }
                initialize();
            },
                (error) -> Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                + "retrieve matching alarm record for GUID: " + mAlarmGUID, error));
    }

    private void initialize() {
        setContentView(R.layout.activity_alarm_log);

        TextView labelText = findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());

        TextView origDateTimeText = findViewById(R.id.orig_date_time_text);
        origDateTimeText.setText(mAlarm.getAlarmDateTimeString());

        TextView timeAcknowledgedText = findViewById(R.id.time_acknowledged_text);
        timeAcknowledgedText.setText(mAlarm.getAcknowledgedDateTimeString());

        TextView timeCompletedText = findViewById(R.id.time_completed_text);
        timeCompletedText.setText(mAlarm.getCompletionDateTimeString());

        TextView photoPathText = findViewById(R.id.photo_path_text);
        photoPathText.setText(mAlarm.getPhotoPath());

        ImageView imageView = findViewById(R.id.image_view);
        TextView errorMsg = findViewById(R.id.empty_view);

        Bitmap bitmap = StorageUtil.getImageFromFile(this, Constants.PACKAGE_NAME_PATIENT, mAlarm.getPhotoPath());
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