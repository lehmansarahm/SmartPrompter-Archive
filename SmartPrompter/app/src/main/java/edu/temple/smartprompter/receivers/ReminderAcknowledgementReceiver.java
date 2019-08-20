package edu.temple.smartprompter.receivers;

import android.content.Context;
import android.content.Intent;

import edu.temple.smartprompter.TaskAcknowledgementActivity;

public class ReminderAcknowledgementReceiver extends BaseReminderReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CHANNEL_ID = "smartprompter_ack";
        CHANNEL_NAME = "channel_smartprompter_ack";
        CHANNEL_DESCRIPTION = "channel for smartprompter ack. notifications";
        super.intent = new Intent(context, TaskAcknowledgementActivity.class);
        super.onReceive(context, intent);
    }


}
