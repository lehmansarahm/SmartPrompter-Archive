package edu.temple.sp_res_lib.utils;

import android.content.Context;

import edu.temple.sp_res_lib.email.EmailTask;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class EmailUtil implements EmailTask.TaskCompletionListener {

    public interface EmailUtilListener {
        void onSendComplete(boolean success);
    }

    private Context context;
    private EmailUtilListener listener;

    public void send(Context context, String subject, String body, EmailUtilListener listener) {
        this.context = context;
        this.listener = listener;

        subject += (" - " + StorageUtil.getDeviceLabel(context));
        String[] logs = StorageUtil.getLogsDirContents(context);
        (new EmailTask(subject, body, logs, this)).execute();
    }

    @Override
    public void onTaskComplete(boolean success) {
        if (success) {
            Log.i(LOG_TAG, "Email task completed!  Was successful: " + success);
        } else {
            Log.e(LOG_TAG, "Email task completed!  Was successful: " + success);
        }
        listener.onSendComplete(success);
        StorageUtil.archiveTodaysAlarms(context);
    }

}