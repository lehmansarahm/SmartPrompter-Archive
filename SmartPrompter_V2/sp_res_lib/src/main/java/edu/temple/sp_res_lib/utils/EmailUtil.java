package edu.temple.sp_res_lib.utils;

import android.content.Context;

import edu.temple.sp_res_lib.email.EmailTask;

public class EmailUtil {

    public static void send(final Context context, final String subject, final String body) {
        String[] logs = StorageUtil.getLogsDirContents(context);
        (new EmailTask(context, subject, body, logs)).execute();
    }

}