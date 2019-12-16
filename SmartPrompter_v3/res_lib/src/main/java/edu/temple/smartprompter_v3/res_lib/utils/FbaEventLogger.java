package edu.temple.smartprompter_v3.res_lib.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import edu.temple.smartprompter_v3.res_lib.data.Alarm;

public class FbaEventLogger {

    protected FirebaseAnalytics mFbAnalytics;

    public FbaEventLogger(Context ctx) {
        mFbAnalytics = FirebaseAnalytics.getInstance(ctx.getApplicationContext());
    }

    public void alarmTaskPhaseChange(Class callingClass, String taskPhase, Alarm alarm) {
        Bundle params = new Bundle();
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_TaskPhaseChange_" + taskPhase), params);
    }

    public void alarmTaskResume(Class callingClass, String taskPhase, Alarm alarm) {
        Bundle params = new Bundle();
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_TaskResume_" + taskPhase), params);
    }

    public void alarmTaskSnooze(Class callingClass, String taskPhase, Alarm alarm) {
        Bundle params = new Bundle();
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_TaskSnooze_" + taskPhase), params);
    }

    public void broadcastReceived(Class callingClass, String actionName, String alarmGuid) {
        Bundle params = new Bundle();
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGuid);
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_BroadcastReceived_" + actionName), params);
    }

    public void buttonClick(Class callingClass, String eventName, int buttonID) {
        Bundle params = new Bundle();
        params.putInt(Constants.BUNDLE_ARG_BUTTON_ID, buttonID);
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_ButtonClick_" + eventName), params);
    }

    public void buttonClick(Class callingClass, String eventName, Alarm alarm) {
        Bundle params = new Bundle();
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_ButtonClick_" + eventName), params);
    }

    public void spinnerSelection(Class callingClass, String eventName) {
        Bundle params = new Bundle();
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_SpinnerSelection_" + eventName), params);
    }

    public void fieldClick(Class callingClass, String eventName, int fieldID, String fieldOldVal, String alarmGuid) {
        Bundle params = new Bundle();
        params.putInt(Constants.BUNDLE_ARG_FIELD_ID, fieldID);
        params.putString(Constants.BUNDLE_ARG_FIELD_OLD_VAL, fieldOldVal);
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGuid);
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_FieldClick_" + eventName), params);
    }

    public void fieldClick(Class callingClass, String eventName, int fieldID, String fieldOldVal, Alarm alarm) {
        Bundle params = new Bundle();
        params.putInt(Constants.BUNDLE_ARG_FIELD_ID, fieldID);
        params.putString(Constants.BUNDLE_ARG_FIELD_OLD_VAL, fieldOldVal);
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_FieldClick_" + eventName), params);
    }

    public void fieldUpdate(Class callingClass, String eventName, int fieldID, String fieldNewVal, String alarmGuid) {
        Bundle params = new Bundle();
        params.putInt(Constants.BUNDLE_ARG_FIELD_ID, fieldID);
        params.putString(Constants.BUNDLE_ARG_FIELD_NEW_VAL, fieldNewVal);
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGuid);
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_FieldUpdate_" + eventName), params);
    }

    public void fieldUpdate(Class callingClass, String eventName, int fieldID, String fieldNewVal, Alarm alarm) {
        Bundle params = new Bundle();
        params.putInt(Constants.BUNDLE_ARG_FIELD_ID, fieldID);
        params.putString(Constants.BUNDLE_ARG_FIELD_NEW_VAL, fieldNewVal);
        params.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarm.getGuid());
        params.putString(Constants.BUNDLE_ARG_ALARM_DESC, alarm.getDesc());
        mFbAnalytics.logEvent((callingClass.getSimpleName() + "_FieldUpdate_" + eventName), params);
    }

}