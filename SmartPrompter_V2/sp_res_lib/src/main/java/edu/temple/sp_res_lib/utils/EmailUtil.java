package edu.temple.sp_res_lib.utils;

import android.content.Context;

import edu.temple.sp_res_lib.email.EmailTask;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class EmailUtil implements EmailTask.TaskCompletionListener {

    private Context context;

    public void send(Context context, String subject, String body) {
        this.context = context;
        subject += (" - " + StorageUtil.getDeviceLabel(context));
        String[] logs = StorageUtil.getLogsDirContents(context);
        (new EmailTask(context, subject, body, logs, this)).execute();
    }

    @Override
    public void onTaskComplete(boolean success) {
        Log.i(LOG_TAG, "Email task completed!  Was successful: " + success);
        StorageUtil.archiveLogsDirContents(context);
    }

}