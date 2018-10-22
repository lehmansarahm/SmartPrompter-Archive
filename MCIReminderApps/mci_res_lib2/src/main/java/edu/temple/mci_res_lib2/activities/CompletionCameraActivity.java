package edu.temple.mci_res_lib2.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.temple.mci_res_lib2.alarms.Alarm;
import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;
import edu.temple.mci_res_lib2.R;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.CAMERA_REQUEST_CODE;
import static edu.temple.mci_res_lib2.utils.Constants.DEFAULT_ALARM_ID;

public class CompletionCameraActivity extends AppCompatActivity {

    private static final Intent CAMERA_INTENT = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private static int alarmID;

    private ImageView imageView;
    private String mCurrentPhotoPath = "";
    private Uri mCurrentPhotoURI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_camera);
        Log.i(Constants.LOG_TAG, "LAUNCHING CAMERA VIEW ACTIVITY\n\n");

        alarmID = getIntent().getIntExtra(INTENT_PARAM_ALARM_ID, DEFAULT_ALARM_ID);
        Log.i(Constants.LOG_TAG, "Preparing to take completion photo for alarm ID: " + alarmID);

        if (alarmID != DEFAULT_ALARM_ID) {
            final Button closeButton = findViewById(R.id.closeButton);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { finish(); }
            });
            imageView = findViewById(R.id.imageViewer);

            if (CAMERA_INTENT.resolveActivity(getPackageManager()) != null) {
                mCurrentPhotoURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getPhotoValues());
                Log.i(Constants.LOG_TAG, "Received new photo URI: " + mCurrentPhotoURI);

                CAMERA_INTENT.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoURI);
                CAMERA_INTENT.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                CAMERA_INTENT.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(CAMERA_INTENT, CAMERA_REQUEST_CODE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            Log.d(Constants.LOG_TAG, "RECEIVED CAMERA REQUEST RESULT.  Result code (ok = -1, cancelled = 0): " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(mCurrentPhotoURI, projection, null, null, null);
                int data_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                mCurrentPhotoPath = cursor.getString(data_column_index);
                Log.i(Constants.LOG_TAG, "Received new photo path: " + mCurrentPhotoPath);

                File imgFile = new  File(mCurrentPhotoPath);
                if (imgFile.exists()) {
                    try {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(myBitmap);
                        completeAlarm();
                    } catch (OutOfMemoryError error) {

                        // -------------------------------------------------------------------------
                        // -------------------------------------------------------------------------
                        // Getting the error when the user ignores an alarm for an extended period...
                        // I think we're getting this error because the screen is technically "on" this whole time??
                        // (placed on top to make sure it is the first thing the user sees when they wake the screen up)
                        // -------------------------------------------------------------------------
                        // -------------------------------------------------------------------------

                        // write error details to log
                        Log.e(Constants.LOG_TAG, "Could not decode image file: " + imgFile.getAbsolutePath(), error);

                        // show message to user
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.imageViewer),
                                "Something went wrong!  Let's try that again...", Snackbar.LENGTH_LONG);
                        mySnackbar.show();

                        // get rid of any lingering artifacts
                        imgFile.delete();

                        // restart the activity
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                } else {
                    Log.i(Constants.LOG_TAG, "Could not display current photo from file.  Displaying default");
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
                startActivityForResult(CAMERA_INTENT, CAMERA_REQUEST_CODE);
        }
    }

    private ContentValues getPhotoValues() {
        ContentValues photoValues = new ContentValues();
        photoValues.put(MediaStore.Images.Media.TITLE, "MCIReminders_"
                + (new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())) + ".jpg");
        photoValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        photoValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return photoValues;
    }

    private void completeAlarm() {
        MCIAlarmManager.cancelCompletionReminder(CompletionCameraActivity.this, alarmID);
        MCIAlarmManager.updateAlarmStatus(CompletionCameraActivity.this, alarmID, Alarm.STATUS.Complete, mCurrentPhotoPath);
        Log.i(Constants.LOG_TAG, "Alarm with ID: " + alarmID + " has been marked as 'complete'.");

        Intent newIntent = new Intent(CompletionCameraActivity.this, CompletionConfirmationActivity.class);
        startActivity(newIntent);
        finish();
    }

}