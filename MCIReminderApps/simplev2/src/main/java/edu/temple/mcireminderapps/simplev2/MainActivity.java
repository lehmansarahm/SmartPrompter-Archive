package edu.temple.mcireminderapps.simplev2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.temple.mci_res_lib2.activities.AlarmListActivity;
import edu.temple.mci_res_lib2.utils.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AlarmListActivity.class);
        intent.putExtra(Constants.INTENT_PARAM_EXEC_MODE, Constants.EXEC_MODES.Simple.toString());
        startActivity(intent);
        finish();
    }

}