package edu.temple.smartprompter_v3.admin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

import edu.temple.smartprompter_v3.admin.BaseActivity;
import edu.temple.smartprompter_v3.admin.R;
import edu.temple.smartprompter_v3.res_lib.data.Alarm;
import edu.temple.smartprompter_v3.res_lib.data.FirebaseConnector;
import edu.temple.smartprompter_v3.res_lib.fragments.DatePickerFragment;
import edu.temple.smartprompter_v3.res_lib.fragments.TimePickerFragment;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;
import edu.temple.smartprompter_v3.res_lib.utils.FbaEventLogger;
import edu.temple.smartprompter_v3.res_lib.utils.MediaUtil;
import edu.temple.smartprompter_v3.res_lib.utils.StorageUtil;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class AlarmDetailsFragment extends Fragment  {

    private static String ALARM_DESC_IMG_PATH = "";

    private FirebaseAuth mFbAuth;
    private FbaEventLogger mFbaEventLogger;
    private AlarmDetailsListener mListener;

    private Alarm mAlarm;
    private String mAlarmGUID;

    private View mRootView;
    private TextView mDateText, mTimeText;

    public interface AlarmDetailsListener {
        void onDateRequested(String mAlarmGUID, int[] date);
        void onTimeRequested(String mAlarmGUID, int[] time);
        void onImageRequested(String alarmGUID);
        void onAlarmSaved(Alarm alarm);
        void onAlarmDeleted(Alarm alarm);
    }

    public AlarmDetailsFragment() {
        // required empty constructor
    }

    public static AlarmDetailsFragment newInstance(String alarmGUID) {
        AlarmDetailsFragment fragment = new AlarmDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGUID);
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(LOG_TAG, "Fragment attached!");

        try {
            mListener = (AlarmDetailsFragment.AlarmDetailsListener) context;

            mFbAuth = FirebaseAuth.getInstance();
            String email = (mFbAuth.getCurrentUser() == null
                    ? "" : mFbAuth.getCurrentUser().getEmail());
            mFbaEventLogger = new FbaEventLogger(context, email);
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement AlarmDetailsListener";
            Log.e(LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "Fragment detached!");
        mListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Fragment created!");
        if (getArguments() != null) {
            mAlarmGUID = getArguments().getString(Constants.BUNDLE_ARG_ALARM_GUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Fragment view created!");
        mRootView = inflater.inflate(R.layout.fragment_alarm_details, container, false);
        if (mAlarm != null) initialize();
        else showAlarmDetails();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Fragment resumed!");

        /* if (ALARM_DESC_IMG_PATH != null
                && !ALARM_DESC_IMG_PATH.isEmpty()
                && !mAlarm.getDescImgPath().equals(ALARM_DESC_IMG_PATH)) {
            Log.i(LOG_TAG, "Hack to fix the save state of the descriptive image path.");
            mAlarm.setDescImgPath(ALARM_DESC_IMG_PATH);
            initDescImg();
        } */
    }

    public void onDatePicked(String alarmGuid, int year, int month, int day) {
        mFbaEventLogger.fieldUpdate(this.getClass(), "AlarmDate", 0,
                month + "/" + day + "/" + year, alarmGuid);
        mAlarm.updateAlarmDate(year, month, day);
        mDateText.setText(mAlarm.getAlarmDateString());
    }

    public void onTimePicked(String alarmGuid, int hourOfDay, int minute) {
        mFbaEventLogger.fieldUpdate(this.getClass(), "AlarmTime", 0,
                hourOfDay + ":" + minute, alarmGuid);
        mAlarm.updateAlarmTime(hourOfDay, minute);
        mTimeText.setText(mAlarm.getAlarmTimeString());
    }

    public void onImageAccepted(String alarmGuid, byte[] bytes) {
        assert(mAlarm.getGuid().equals(alarmGuid));

        mAlarm.setDescImgPath();
        ALARM_DESC_IMG_PATH = mAlarm.getDescImgPath();
        Log.i(LOG_TAG, "User has successfully taken and approved a task "
                + "description picture.  Updating alarm and saving image to path: "
                + ALARM_DESC_IMG_PATH);

        Bitmap media = MediaUtil.convertToBitmap(bytes);
        StorageUtil.writeImageToFile(getActivity(), getActivity().getPackageName(), mAlarm.getDescImgPath(), media);
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    private void showAlarmDetails() {
        if (mAlarmGUID == null || mAlarmGUID.equals("")) {
            Log.e(Constants.LOG_TAG, "SHOW DEFAULT DETAILS FOR NEW ALARM.");
            TextView headerTv = mRootView.findViewById(R.id.alarm_details_header);
            headerTv.setText("New Alarm Details");
            mAlarm = new Alarm();
            mAlarmGUID = mAlarm.getGuid();
            initialize();
        } else {
            Log.i(BaseActivity.LOG_TAG, "Show current details for existing alarm with GUID: " + mAlarmGUID);
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
    }

    private void initialize() {
        initLabel();
        initDescImg();
        initDatePicker();
        initTimePicker();

        initSaveButton();
        initCancelButton();
        initDeleteButton();

        TextView statusText = mRootView.findViewById(R.id.status_text);
        statusText.setText(mAlarm.getStatus().toString());

    }

    private void initLabel() {
        final TextView labelText = mRootView.findViewById(R.id.label_text);
        labelText.setText(mAlarm.getDesc());

        LinearLayout labelLayout = mRootView.findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbaEventLogger.fieldClick(this.getClass(), "AlarmDescLabel",
                        view.getId(), mAlarm.getDesc(), mAlarm);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFbaEventLogger.buttonClick(this.getClass(), "DialogOk", which);
                        String label = input.getText().toString();
                        mAlarm.updateDesc(label);
                        labelText.setText(label);
                        Log.i(BaseActivity.LOG_TAG, "Updated alarm label: " + label
                                + " \t for guid: " + mAlarm.getGuid());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFbaEventLogger.buttonClick(this.getClass(), "DialogCancel", which);
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void initDescImg() {
        LinearLayout labelLayout = mRootView.findViewById(R.id.image_layout);
        labelLayout.setOnClickListener(view -> {
            Log.i(LOG_TAG, "Camera preview requested.  Saving any intermittent changes.");
            FirebaseConnector.saveAlarm(mAlarm, saveResult -> {
                        Log.i(LOG_TAG, "Updates saved to alarm with GUID: " + mAlarmGUID
                                + " \t\t Displaying camera preview fragment.");
                        mListener.onImageRequested(mAlarmGUID);
                    }, (saveError) ->
                            Log.e(BaseActivity.LOG_TAG, "Something went wrong while attempting to "
                                    + "save changes to alarm with GUID: " + mAlarmGUID, saveError)
            );
        });

        final ImageView imageView = mRootView.findViewById(R.id.image_view);
        final TextView imgPath = mRootView.findViewById(R.id.image_path);
        final TextView errorMsg = mRootView.findViewById(R.id.picture_empty_view);

        Bitmap bitmap = StorageUtil.getImageFromFile(getActivity(), getActivity().getPackageName(), mAlarm.getDescImgPath());
        if (bitmap != null) {
            Log.i(Constants.LOG_TAG, "Attempt to retrieve completion image was "
                    + "successful.  Forwarding Bitmap to image viewer.");
            errorMsg.setVisibility(View.GONE);

            imageView.getLayoutParams().height = 450;
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            imgPath.setText(mAlarm.getDescImgPath());
            imgPath.setVisibility(View.VISIBLE);
        } else {
            Log.i(Constants.LOG_TAG, "Unable to retrieve completion image.  "
                    + "Displaying default text.");
            errorMsg.setVisibility(View.VISIBLE);

            imageView.setVisibility(View.GONE);
            imgPath.setVisibility(View.GONE);
        }
    }

    private void initDatePicker() {
        mDateText = mRootView.findViewById(R.id.date_text);
        mDateText.setText(mAlarm.getAlarmDateString());

        LinearLayout dateLayout = mRootView.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(view -> {
            int[] date = mAlarm.getAlarmDate();
            String dateString = date[1] + "/" + date[2] + "/" + date[0];

            Log.i(BaseActivity.LOG_TAG, "Attempting to launch date picker for alarm guid: " + mAlarmGUID);
            mFbaEventLogger.fieldClick(this.getClass(), "AlarmDate", 0,
                    dateString, mAlarmGUID);
            mListener.onDateRequested(mAlarmGUID, date);
        });
    }

    private void initTimePicker() {
        mTimeText = mRootView.findViewById(R.id.time_text);
        mTimeText.setText(mAlarm.getAlarmTimeString());

        LinearLayout timeLayout = mRootView.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(view -> {
            int[] time = mAlarm.getAlarmTime();
            mFbaEventLogger.fieldClick(this.getClass(), "AlarmTime", 0,
                    time[0] + ":" + time[1], mAlarmGUID);
            mListener.onTimeRequested(mAlarmGUID, time);
        });
    }

    private void initSaveButton() {
        Button saveButton = mRootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> mListener.onAlarmSaved(mAlarm));
    }

    private void initCancelButton() {
        Button cancelButton = mRootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFbaEventLogger.buttonClick(this.getClass(), "Cancel", mAlarm);
                showAlarmDetails();
            }
        });
    }

    private void initDeleteButton() {
        Button deleteButton = mRootView.findViewById(R.id.delete_button);
        if (mAlarmGUID.equals(Constants.DEFAULT_ALARM_GUID))
            deleteButton.setEnabled(false);
        else {
            deleteButton.setOnClickListener(view -> mListener.onAlarmDeleted(mAlarm));
        }
    }

}
