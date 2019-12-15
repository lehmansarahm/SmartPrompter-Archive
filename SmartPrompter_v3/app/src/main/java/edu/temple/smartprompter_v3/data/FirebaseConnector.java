package edu.temple.smartprompter_v3.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import static edu.temple.smartprompter_v3.utils.Constants.LOG_TAG;

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


    public static void getAlarms(final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getAlarmByGuid(final String guid,
                                      final FbDocListener listener) {
        Log.i(LOG_TAG, "Attempting to retrieve alarm by ID: " + guid);
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .get()
                .addOnCompleteListener((new Alarm()).getSingletonCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void getAlarmsByStatus(final Alarm.STATUS status,
                                         final FbQueryListener listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .whereEqualTo(Alarm.FIELD_STATUS, status.toString())
                .get()
                .addOnCompleteListener((new Alarm()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + Alarm.COLLECTION, e));
    }

    public static void saveAlarm(final Map<String, Object> newItemProps,
                                 final OnCompleteListener<DocumentReference> listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .add(newItemProps)
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "attempting to create new alarm record with properties: "
                        + newItemProps));
    }

    public static void deleteAlarm(final String guid,
                                   final OnCompleteListener<Void> listener) {
        mFbFirestore.collection(Alarm.COLLECTION)
                .document(guid)
                .delete()
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "attempting to delete alarm record with GUID: " + guid));
    }


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    public static void getUsers(final FbQueryListener listener) {
        mFbFirestore.collection(User.COLLECTION)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + User.COLLECTION, e));
    }

    public static void getUsersByEmail(final String email,
                                       final FbQueryListener listener) {
        mFbFirestore.collection(User.COLLECTION)
                .whereEqualTo(User.FIELD_EMAIL, email)
                .get()
                .addOnCompleteListener((new User()).getListCompletionListener(listener))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Something went wrong while "
                        + "trying to retrieve documents from collection: " + User.COLLECTION, e));
    }

}