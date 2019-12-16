package edu.temple.smartprompter_v3.res_lib.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.temple.smartprompter_v3.res_lib.utils.Constants;

public class FirebaseConnector {

    public interface FbDataClass {
        OnCompleteListener<QuerySnapshot> getListCompletionListener(FbQueryListener listener);
        OnCompleteListener<DocumentSnapshot> getSingletonCompletionListener(FbDocListener listener);
        boolean wasSuccessful(Task task);
        Map<String, Object> getFbProperties();
    }

    public interface FbQueryListener {
        void OnResultsAvailable(List<FbDataClass> results);
    }

    public interface FbDocListener {
        void OnResultsAvailable(FbDataClass result);
    }

    private static FirebaseFirestore mFbFirestore = FirebaseFirestore.getInstance();


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    private static final List<String> ALARM_TASK_STATUSES = Arrays.asList(
            Alarm.STATUS.Active.toString(),
            Alarm.STATUS.Unacknowledged.toString(),
            Alarm.STATUS.Incomplete.toString()
    );

    public static void getLatestAlarm(final FbDocListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .orderBy(Alarm.FIELD_REQUEST_CODE, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task1 -> {
                    DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                    listener.OnResultsAvailable(new Alarm(documentSnapshot));
                });
    }

    public static void getAlarms(final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getAlarmByGuid(final String guid,
                                      final FbDocListener listener) {
        Log.i(Constants.LOG_TAG, "Attempting to retrieve alarm by ID: " + guid);
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .get()
                .addOnCompleteListener((new Alarm()).getSingletonCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getAlarmsByEmail(final String email,
                                        final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, email)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getActiveAlarmTasks(final String email,
                                           final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereIn(Alarm.FIELD_STATUS, ALARM_TASK_STATUSES)
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, email)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getAlarmsByStatus(final Alarm.STATUS status,
                                         final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_STATUS, status.toString())
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void saveAlarm(final Alarm alarm) {
        Log.i(Constants.LOG_TAG, "Attempting to save alarm record with properties: "
                + alarm.getFbPropertiesString());
        OnFailureListener onFailureListener = e -> Log.e(Constants.LOG_TAG,
                "Something went wrong while attempting to create alarm " +
                " record with guid: " + alarm.getGuid());

        if (alarm.getGuid().equals(Constants.DEFAULT_ALARM_GUID)) {
            mFbFirestore.collection(Alarm.COLLECTION)
                    .add(alarm.getFbProperties())
                    .addOnFailureListener(onFailureListener);
        } else {
            mFbFirestore.collection(Alarm.COLLECTION)
                    .document(alarm.getGuid())
                    .set(alarm.getFbProperties())
                    .addOnFailureListener(onFailureListener);
        }
    }

    public static void deleteAlarm(final String guid) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .delete()
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG,
                        "Something went wrong while attempting to delete alarm " +
                                "record with GUID: " + guid));
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public static void getUsers(final FbQueryListener listener) {
        mFbFirestore.collection(User.COLLECTION)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + User.COLLECTION, e));
    }

    public static void getUsersByEmail(final String email,
                                       final FbQueryListener listener) {
        mFbFirestore.collection(User.COLLECTION)
                .whereEqualTo(User.FIELD_EMAIL, email)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(Constants.LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + User.COLLECTION, e));
    }

}