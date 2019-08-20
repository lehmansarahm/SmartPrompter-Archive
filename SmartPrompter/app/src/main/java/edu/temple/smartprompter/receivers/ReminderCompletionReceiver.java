package edu.temple.smartprompter.receivers;

import android.content.Context;
import android.content.Intent;

import edu.temple.smartprompter.TaskCompletionActivity;

public class ReminderCompletionReceiver extends BaseReminderReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CHANNEL_ID = "smartprompter_comp";
        CHANNEL_NAME = "channel_smartprompter_comp";
        CHANNEL_DESCRIPTION = "channel for smartprompter comp. notifications";
        super.intent = new Intent(context, TaskCompletionActivity.class);
        super.onReceive(context, intent);
    }

}
