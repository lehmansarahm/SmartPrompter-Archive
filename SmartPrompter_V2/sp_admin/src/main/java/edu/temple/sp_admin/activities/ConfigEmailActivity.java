package edu.temple.sp_admin.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.fragments.AlarmDetailsFragment;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.DateTimeUtil;
import edu.temple.sp_res_lib.utils.EmailUtil;
import edu.temple.sp_res_lib.utils.Log;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

import static edu.temple.sp_admin.SpAdmin.LOG_TAG;

public class ConfigEmailActivity extends BaseActivity implements EmailUtil.EmailUtilListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_email);
        initLabel();

        String nextExportString = StorageUtil.getNextExportCheck(ConfigEmailActivity.this);
        if (nextExportString != null && !nextExportString.equals("")) {
            long nextExport = Long.parseLong(nextExportString);
            final TextView nextExportText = findViewById(R.id.next_export_text);
            nextExportText.setText(DateTimeUtil.formatTimeInMillis(nextExport, DateTimeUtil.FORMAT.DateTime));
        }

        final Button emailButton = findViewById(R.id.export_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO - schedule to run at midnight ... archive all emailed documents
                EmailUtil emailUtil = new EmailUtil();
                emailUtil.send(ConfigEmailActivity.this,
                        "Daily Logs",
                        "\n \n \n SmartPrompter - a Temple University application",
                        ConfigEmailActivity.this);
            }
        });
    }

    @Override
    public void onSendComplete(boolean success) {
        if (success) {
            Toast.makeText(ConfigEmailActivity.this, "Log email sent!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ConfigEmailActivity.this, "Email send failed!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initLabel() {
        final Context context = ConfigEmailActivity.this;
        final TextView labelText = findViewById(R.id.device_label_text);
        labelText.setText(StorageUtil.getDeviceLabel(context));

        LinearLayout labelLayout = findViewById(R.id.label_layout);
        labelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alarm Label");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label = input.getText().toString();
                        StorageUtil.updateDeviceLabel(context, label);
                        labelText.setText(label);
                        Log.ui(Constants.LOG_TAG, context, "Updated device label: " + label);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.ui(Constants.LOG_TAG, context, "User canceled device label change");
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

}