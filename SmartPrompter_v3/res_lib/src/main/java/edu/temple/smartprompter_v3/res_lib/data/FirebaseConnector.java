package edu.temple.smartprompter_v3.res_lib.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    public interface FbFailureListener {
        void OnQueryFailed(Exception e);
    }

    private static FirebaseFirestore mFbFirestore = FirebaseFirestore.getInstance();


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    private static final List<String> ALARM_TASK_STATUSES = Arrays.asList(
            Alarm.STATUS.Unacknowledged.toString(),
            Alarm.STATUS.Incomplete.toString()
    );

    public static void getLatestAlarm(final FbDocListener listener,
                                      final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .orderBy(Alarm.FIELD_REQUEST_CODE, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task1 -> {
                    DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                    listener.OnResultsAvailable(new Alarm(documentSnapshot));
                })
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getAlarms(final FbQueryListener listener,
                                 final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getAlarmByGuid(final String guid,
                                      final FbDocListener listener,
                                      final FbFailureListener failureListener) {
        Log.i(Constants.LOG_TAG, "Attempting to retrieve alarm by ID: " + guid);
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .get()
                .addOnCompleteListener(task -> {
                    Alarm newAlarm = new Alarm(task.getResult());
                    listener.OnResultsAvailable(newAlarm);
                })
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getAlarmsByEmail(final String email,
                                        final FbQueryListener listener,
                                        final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, email)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getActiveAlarmTasks(final String email,
                                           final FbQueryListener listener,
                                           final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereIn(Alarm.FIELD_STATUS, ALARM_TASK_STATUSES)
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, email)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getAlarmsByStatus(final String email,
                                         final Alarm.STATUS status,
                                         final FbQueryListener listener,
                                         final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_STATUS, status.toString())
                .whereEqualTo(Alarm.FIELD_USER_EMAIL, email)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void saveAlarm(final Alarm alarm,
                                 final OnCompleteListener listener,
                                 final FbFailureListener failureListener) {
        Log.i(Constants.LOG_TAG, "Attempting to save existing alarm record with properties: "
                + alarm.getFbPropertiesString());

        mFbFirestore.collection(Alarm.COLLECTION)
                .document(alarm.getGuid())
                .set(alarm.getFbProperties())
                .addOnCompleteListener(listener)
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void saveNewAlarm(final Alarm alarm,
                                 final OnSuccessListener listener,
                                 final FbFailureListener failureListener) {
        Log.i(Constants.LOG_TAG, "Attempting to save new alarm record with properties: "
                + alarm.getFbPropertiesString());

        mFbFirestore.collection(Alarm.COLLECTION)
                .add(alarm.getFbProperties())
                .addOnSuccessListener(listener)
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void deleteAlarm(final String guid,
                                   final FbFailureListener failureListener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .delete()
                .addOnFailureListener(failureListener::OnQueryFailed);
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public static void getUsers(final FbQueryListener listener,
                                final FbFailureListener failureListener) {
        mFbFirestore.collection(User.COLLECTION)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

    public static void getUsersByEmail(final String email,
                                       final FbQueryListener listener,
                                       final FbFailureListener failureListener) {
        mFbFirestore.collection(User.COLLECTION)
                .whereEqualTo(User.FIELD_EMAIL, email)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(failureListener::OnQueryFailed);
    }

}