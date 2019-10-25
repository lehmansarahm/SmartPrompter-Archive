package edu.temple.sp_res_lib.email;

import android.os.AsyncTask;

import java.io.File;

import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class EmailTask extends AsyncTask<String, Void, Boolean> {

    private static final String acct_username = "cct.research.team";
    private static final String acct_email = "cct.research.team@gmail.com";
    private static final String acct_pw = "VirtualKitchen#2018";
    private static final String recip_email = "smlehman@temple.edu";

    private String subject, body;
    private String[] logs;
    private TaskCompletionListener listener;

    public interface TaskCompletionListener {
        void onTaskComplete(boolean success);
    }

    public EmailTask(String subject, String body, String[] logs, TaskCompletionListener listener) {
        this.subject = subject;
        this.body = body;
        this.logs = logs;
        this.listener = listener;
    }

    protected Boolean doInBackground(String... params) {
        try {
            GmailSender sender = new GmailSender(acct_username, acct_pw);
            for (String log : logs) {
                Log.i(LOG_TAG, "Adding log attachment: " + log);
                File file = new File(log);
                sender.addAttachment(log, file.getName());
            }

            sender.sendMail(recip_email, acct_email, subject, body);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
    }

    protected void onPostExecute(Boolean success) {
        listener.onTaskComplete(success);
    }

}