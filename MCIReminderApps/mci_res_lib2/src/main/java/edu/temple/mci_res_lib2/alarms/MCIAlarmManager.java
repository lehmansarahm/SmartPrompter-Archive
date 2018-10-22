package edu.temple.mci_res_lib2.alarms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.temple.mci_res_lib2.activities.AlarmDetailActivity;
import edu.temple.mci_res_lib2.activities.AlarmListActivity;
import edu.temple.mci_res_lib2.activities.CompletionConfirmationActivity;
import edu.temple.mci_res_lib2.activities.CompletionPromptActivity;
import edu.temple.mci_res_lib2.activities.TaskPromptActivity;
import edu.temple.mci_res_lib2.utils.Constants;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;

public class MCIAlarmManager {

    private static final String DEFAULT_SHARED_PREFS_FILE = "MCIReminderApps_ResLibV2";
    private static final List<Alarm> ALARM_LIST = new ArrayList<>();
    private static final int COUNT = 4;

    private static Context context;
    private static AlarmManager alarmMgr;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    private static Constants.EXEC_MODES EXEC_MODE = Constants.EXEC_MODES.None;

    public static Constants.EXEC_MODES getExecMode() { return EXEC_MODE; }

    public static void setExecMode(Context context, Constants.EXEC_MODES newExecMode) {
        // set runtime property
        EXEC_MODE = newExecMode;

        // auto-commit to shared prefs (relying on fact that this won't change during course of app use)
        SharedPreferences sharedPrefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(Constants.INTENT_PARAM_EXEC_MODE, EXEC_MODE.toString());
        prefsEditor.commit();

        Log.i(Constants.LOG_TAG, "Saved new exec mode: " + EXEC_MODE.toString());
    }

    public static void getExecModeFromSharedPrefs() {
        SharedPreferences sharedPrefs = context.getSharedPreferences(DEFAULT_SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String execModeString = sharedPrefs.getString(Constants.INTENT_PARAM_EXEC_MODE, "");
        if (!execModeString.isEmpty()) EXEC_MODE = Constants.EXEC_MODES.valueOf(execModeString);
        else EXEC_MODE = Constants.EXEC_MODES.None;
        Log.i(Constants.LOG_TAG, "Restoring app with exec mode: " + EXEC_MODE.toString());
    }

    public static boolean isDefaultExecMode() {
        return EXEC_MODE.equals(Constants.EXEC_MODES.None);
    }

    private static final int getBaseAlarmID() {
        switch (EXEC_MODE) {
            case Advanced:
                Log.i(Constants.LOG_TAG, "Returning 'Advanced Mode' base alarm ID.");
                return 100;
            case Simple:
                Log.i(Constants.LOG_TAG, "Returning 'Simple Mode' base alarm ID.");
                return 200;
            default:
                Log.i(Constants.LOG_TAG, "Returning default base alarm ID.");
                return 0;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    //
    //      Handlers for managing details of alarm input list as provided by the user
    //
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public static void initAlarmList(Context context) {
        Log.i(Constants.LOG_TAG, "Initializing alarm list...");
        if (ALARM_LIST.size() == 0) {
            for (int i = 0; i < COUNT; i++) {
                Alarm defaultAlarm = createDefaultAlarm(i+1);
                Alarm storedAlarm = getAlarmFromSharedPrefs(context, defaultAlarm.getTitle());
                Alarm alarmToUse = (storedAlarm != null) ? storedAlarm : defaultAlarm;

                if (ALARM_LIST.size() < COUNT)
                    ALARM_LIST.add(alarmToUse);
                else {
                    ALARM_LIST.remove(i);
                    ALARM_LIST.add(i, alarmToUse);
                }
            }
        }
    }

    public static List<Alarm> getAlarmList() {
        return ALARM_LIST;
    }

    public static int getCompletedAlarmCount() {
        int count = 0;
        for (int i = 0; i < ALARM_LIST.size(); i++)
            if (ALARM_LIST.get(i).getStatus().equals(Alarm.STATUS.Complete))
                count++;
        return count;
    }

    public static void startAlarmsInList(Context context) {
        for (int i = 0; i < ALARM_LIST.size(); i++) {
            MCIAlarmManager.setNewAlarm(context, i);
        }
    }

    public static void resetAlarmList(Context context) {
        Log.i(Constants.LOG_TAG, "Resetting any current active alarms...");
        for (int i = 0; i < ALARM_LIST.size(); i++) {
            Alarm alarm = ALARM_LIST.get(i);
            cancelAlarm(context, alarm.getId());
        }

        Log.i(Constants.LOG_TAG, "Refreshing alarm list...");
        ALARM_LIST.clear();
        initAlarmList(context);
    }

    public static Alarm getAlarm(int alarmID) {
        return ALARM_LIST.get(alarmID);
    }

    public static void updateAlarm(int alarmID, Alarm updatedAlarm) {
        ALARM_LIST.remove(alarmID);
        ALARM_LIST.add(alarmID, updatedAlarm);
    }

    public static void updateAlarmStatus(Context context, int alarmID, Alarm.STATUS newStatus) {
        updateAlarmStatus(context, alarmID, newStatus, "");
    }

    public static void updateAlarmStatus(Context context, int alarmID, Alarm.STATUS newStatus, String photoPath) {
        Log.i(Constants.LOG_TAG, "Attempting to update alarm status.  Item list size: " + ALARM_LIST.size());
        Alarm currentAlarm = ALARM_LIST.get(alarmID);
        currentAlarm.setStatus(newStatus);

        if (newStatus.equals(Alarm.STATUS.Complete)) {
            Log.i(Constants.LOG_TAG, "Alarm activity complete.  Dumping log to file system.");
            currentAlarm.setCompPhotoName(photoPath);
            currentAlarm.writeToFile(EXEC_MODE.toString());
        }

        saveAlarmListToSharedPrefs(context);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    //
    //      Handlers for persistent I/O of alarm list details
    //
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    private static Alarm getAlarmFromSharedPrefs(Context context, String title) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(getSharedPrefsFilename(), Context.MODE_PRIVATE);
        String json = sharedPrefs.getString(title, "");
        Alarm retrievedAlarm = (new Gson()).fromJson(json, Alarm.class);
        return retrievedAlarm;
    }

    public static void saveAlarmListToSharedPrefs(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(getSharedPrefsFilename(), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

        Gson gson = new Gson();
        for (Alarm alarm : ALARM_LIST) {
            String json = gson.toJson(alarm);
            prefsEditor.putString(alarm.getTitle(), json);
        }

        prefsEditor.commit();
    }

    public static void clearAlarmListFromSharedPrefs(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(getSharedPrefsFilename(), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
        Log.i(Constants.LOG_TAG, "All data cleared from shared preferences.");
    }

    private static final String getSharedPrefsFilename() {
        switch (EXEC_MODE) {
            case Advanced:
                return "MCIReminderApps_AdvancedV2";
            case Simple:
                return "MCIReminderApps_SimpleV2";
            default:
                return DEFAULT_SHARED_PREFS_FILE;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    //
    //      Handlers for actual device alarms
    //
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public static void setNewAlarm(Context newContext, int alarmID) {
        init(newContext);
        Alarm alarm = ALARM_LIST.get(alarmID);
        if (alarm.getStatus().equals(Alarm.STATUS.Active)) {
            Calendar alarmCal = alarm.getOriginalAlarm();
            Log.i(Constants.LOG_TAG, "Setting new alarm for time: " + alarmCal.getTime().toString());
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), getSingleBroadcastPI(alarmID));
        }
        else Log.i(Constants.LOG_TAG, "Cannot start non-active alarm: " + alarm.getTitle());
    }

    public static boolean setNewAcknowledgementReminder(Activity newContext, int alarmID) {
        init(newContext);
        Alarm alarm = ALARM_LIST.get(alarmID);
        if (alarm.getStatus().equals(Alarm.STATUS.Unacknowledged)) {
            if (alarm.hasAcknowledgementRemindersRemaining()) {
                Calendar alarmCal = alarm.getNextAcknowledgementCalendar();
                Log.i(Constants.LOG_TAG, "Setting new acknowledgement reminder for time: " + alarmCal.getTime().toString());
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), getSingleBroadcastPI(alarmID));
                saveAlarmListToSharedPrefs(newContext);
            } else {
                Log.i(Constants.LOG_TAG, "Acknowledgement reminder limit reached for alarm: " + alarm.getTitle());
                Log.i(Constants.LOG_TAG, "Dumping alarm log to file system.");
                alarm.writeToFile(EXEC_MODE.toString());
                return false;
            }
        }
        else Log.i(Constants.LOG_TAG, "Cannot start acknowledgement reminder for non-active alarm: " + alarm.getTitle());
        return true;
    }

    public static void cancelAcknowledgementReminder(Context newContext, int alarmID) {
        cancelAcknowledgementReminder(newContext, alarmID, false);
    }

    public static void cancelAcknowledgementReminder(Context newContext, int alarmID, boolean writeToFile) {
        Log.i(Constants.LOG_TAG, "Cancelling acknowledgement reminder.");
        init(newContext);
        ALARM_LIST.get(alarmID).cancelAckReminder();
        alarmMgr.cancel(getSingleBroadcastPI(alarmID));
        if (writeToFile) ALARM_LIST.get(alarmID).writeToFile(EXEC_MODE.toString());
    }

    public static boolean setNewCompletionReminder(Context newContext, int alarmID) {
        init(newContext);
        Alarm alarm = ALARM_LIST.get(alarmID);
        if (alarm.getStatus().equals(Alarm.STATUS.Incomplete)) {
            if (alarm.hasCompletionRemindersRemaining()) {
                Calendar alarmCal = alarm.getNextCompletionCalendar();
                Log.i(Constants.LOG_TAG, "Setting new completion reminder for time: " + alarmCal.getTime().toString());
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), getSingleBroadcastPI(alarmID));
                saveAlarmListToSharedPrefs(newContext);
            } else {
                Log.i(Constants.LOG_TAG, "Completion reminder limit reached for alarm: " + alarm.getTitle());
                Log.i(Constants.LOG_TAG, "Dumping alarm log to file system.");
                alarm.writeToFile(EXEC_MODE.toString());
                return false;
            }
        }
        else Log.i(Constants.LOG_TAG, "Cannot start completion reminder for unacknowledged alarm: " + alarm.getTitle());
        return true;
    }

    public static void cancelCompletionReminder(Context newContext, int alarmID) {
        cancelCompletionReminder(newContext, alarmID, false);
    }

    public static void cancelCompletionReminder(Context newContext, int alarmID, boolean writeToFile) {
        Log.i(Constants.LOG_TAG, "Cancelling completion reminder.");
        init(newContext);
        ALARM_LIST.get(alarmID).cancelCompReminder();
        alarmMgr.cancel(getSingleBroadcastPI(alarmID));
        if (writeToFile) ALARM_LIST.get(alarmID).writeToFile(EXEC_MODE.toString());
    }

    public static Intent getIntentForAlarmStatus(Context context, int alarmID) {
        if (getAlarm(alarmID).wasWrittenToFile()) {
            Log.i(Constants.LOG_TAG, "Current alarm has been committed to file with status: "
                    + getAlarm(alarmID).getStatus().toString()
                    + "... Launching completion confirmation activity.");
            return new Intent(context, CompletionConfirmationActivity.class);
        }

        Intent newIntent;
        Alarm.STATUS status = getAlarm(alarmID).getStatus();

        switch (status) {
            case Active:
            case Inactive:
                newIntent = new Intent(context, AlarmDetailActivity.class);
                break;
            case Unacknowledged:
                Log.i(Constants.LOG_TAG, "Active / unacknowledged alarm status ... Launching task prompt activity.");
                newIntent = new Intent(context, TaskPromptActivity.class);
                break;
            case Incomplete:
                Log.i(Constants.LOG_TAG, "Acknowledged but incomplete alarm status ... Launching completion prompt activity.");
                newIntent = new Intent(context, CompletionPromptActivity.class);
                break;
            case Complete:
                Log.i(Constants.LOG_TAG, "Current alarm has been completed ... Launching completion confirmation activity.");
                newIntent = new Intent(context, CompletionConfirmationActivity.class);
                break;
            default:
                Log.i(Constants.LOG_TAG, "Inappropriate alarm status: " + status + "... Launching main activity.");
                newIntent = new Intent(context, AlarmListActivity.class);
                break;
        }

        return newIntent;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    //
    //      General-purpose private reference methods
    //
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    private static void init(Context newContext) {
        context = newContext.getApplicationContext();
        if (alarmMgr == null)
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent getSingleBroadcastPI(int alarmID) {
        int requestCode = (getBaseAlarmID() + alarmID);
        Log.i(Constants.LOG_TAG, "Generating PI with request code: "
                + requestCode + ", for alarm ID: " + alarmID);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(INTENT_PARAM_ALARM_ID, alarmID);
        PendingIntent newPI = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return newPI;
    }

    private static Alarm createDefaultAlarm(int position) {
        return new Alarm(position, 12, 0, true, Alarm.STATUS.Inactive);
    }

    private static void cancelAlarm(Context newContext, int alarmID) {
        Log.i(Constants.LOG_TAG, "Cancelling alarm.");
        init(newContext);
        alarmMgr.cancel(getSingleBroadcastPI(alarmID));
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

}