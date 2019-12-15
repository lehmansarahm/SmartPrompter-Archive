package edu.temple.smartprompter_v3.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.temple.smartprompter_v3.utils.Constants.LOG_TAG;

public class User implements FirebaseConnector.FbDataClass {

    public enum ROLE { Researcher, Caretaker, Participant }

    public static final String                      // MAKE SURE THESE MATCH WHAT'S IN FIRE BASE!!
            COLLECTION = "users",
            FIELD_FIRST_NAME = "firstName",
            FIELD_LAST_NAME = "lastName",
            FIELD_EMAIL = "email",
            FIELD_ROLE = "role",
            DEFAULT_VALUE = "n/a";


    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------


    private String id, firstName, lastName, email;
    private ROLE role;

    public User() {}

    public User (DocumentSnapshot document) {
        id = document.getId();
        firstName = document.contains(FIELD_FIRST_NAME)
                ? document.get(FIELD_FIRST_NAME).toString()
                : DEFAULT_VALUE;
        lastName = document.contains(FIELD_LAST_NAME)
                ? document.get(FIELD_LAST_NAME).toString()
                : DEFAULT_VALUE;
        email = document.contains(FIELD_EMAIL)
                ? document.get(FIELD_EMAIL).toString()
                : DEFAULT_VALUE;

        // -------------------------------------------------------------------------------

        try {
            role = document.contains(FIELD_ROLE)
                    ? ROLE.valueOf(document.get(FIELD_ROLE).toString())
                    : ROLE.Researcher;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Something went wrong while attempting to parse role: "
                    + document.get(FIELD_ROLE) + "\t\t for user: " + firstName + " " + lastName);
            role = ROLE.Researcher;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public ROLE getRole() {
        return role;
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


    @Override
    public OnCompleteListener<QuerySnapshot> getListCompletionListener(FirebaseConnector.FbQueryListener listener) {
        return task -> {
            if (!wasSuccessful(task)) return;
            try {
                List<FirebaseConnector.FbDataClass> results = new ArrayList<>();
                for (QueryDocumentSnapshot result : task.getResult()) {
                    results.add(new User(result));
                }
                listener.OnResultsAvailable(results);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Something went wrong while trying to convert "
                        + "task results to class instances: "
                        + getClass().getSimpleName(), ex);
            }
        };
    }

    @Override
    public OnCompleteListener<DocumentSnapshot> getSingletonCompletionListener(FirebaseConnector.FbDocListener listener) {
        return task -> {
            if (!wasSuccessful(task)) return;
            try {
                listener.OnResultsAvailable(new User(task.getResult()));
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Something went wrong while trying to convert "
                        + "task results to class instances: "
                        + getClass().getSimpleName(), ex);
            }
        };
    }

    @Override
    public boolean wasSuccessful(Task task) {
        if (task.isSuccessful()) return true;
        Log.e(LOG_TAG, "Something went wrong while trying to retrieve "
                + "documents from collection: " + COLLECTION);
        return false;
    }

    @Override
    public Map<String, Object> getFbProperties() {
        Map<String, Object> fbProps = new HashMap<>();
        fbProps.put(FIELD_EMAIL, email);
        fbProps.put(FIELD_FIRST_NAME, firstName);
        fbProps.put(FIELD_LAST_NAME, lastName);
        fbProps.put(FIELD_ROLE, role);
        return fbProps;
    }

}