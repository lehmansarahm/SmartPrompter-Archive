package edu.temple.mci_res_lib2.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.temple.mci_res_lib2.activities.CompletionConfirmationActivity;
import edu.temple.mci_res_lib2.utils.Constants;
import edu.temple.mci_res_lib2.activities.CompletionPromptActivity;
import edu.temple.mci_res_lib2.activities.AlarmListActivity;
import edu.temple.mci_res_lib2.activities.TaskPromptActivity;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.DEFAULT_ALARM_ID;
import static edu.temple.mci_res_lib2.utils.Constants.NOTIFICATION_PLAY_TIME;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "Alarm broadcast receiver activated!");
        if (intent != null) {
            int alarmID = intent.getIntExtra(INTENT_PARAM_ALARM_ID, DEFAULT_ALARM_ID);
            if (alarmID != DEFAULT_ALARM_ID) {
                MCIAlarmManager.initAlarmList(context);
                MCIAlarmManager.getExecModeFromSharedPrefs();

                // cancel any prior alarms that we may have collided with
                for (int i = 0; i < alarmID; i++) {
                    Alarm priorAlarm = MCIAlarmManager.getAlarm(i);
                    if (!priorAlarm.wasWrittenToFile()) {
                        if (priorAlarm.getStatus().equals(Alarm.STATUS.Unacknowledged))
                            MCIAlarmManager.cancelAcknowledgementReminder(context, i, true);
                        else if (priorAlarm.getStatus().equals(Alarm.STATUS.Incomplete)) ;
                            MCIAlarmManager.cancelCompletionReminder(context, i, true);
                    }
                }

                // update the alarm status if necessary
                if (MCIAlarmManager.getAlarm(alarmID).getStatus().equals(Alarm.STATUS.Active))
                    MCIAlarmManager.updateAlarmStatus(context, alarmID, Alarm.STATUS.Unacknowledged);

                // fire up the new intent for the received alarm ID
                Intent newIntent = MCIAlarmManager.getIntentForAlarmStatus(context, alarmID);
                newIntent.putExtra(INTENT_PARAM_ALARM_ID, alarmID);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(newIntent);
            }
            else Log.e(Constants.LOG_TAG, "RECEIVED ALARM ID FROM BROADCAST INTENT, BUT IT WAS INVALID.  CANNOT CONTINUE.");
        }
        else Log.e(Constants.LOG_TAG, "BROADCAST INTENT WAS NULL.  CANNOT RETRIEVE ALARM ID.  CANNOT CONTINUE.");
    }

}