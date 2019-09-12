package edu.temple.smartprompter_v2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import edu.temple.smartprompter_v2.R;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class ConfirmationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // TODO - flesh out game features (FUTURE)
        // TODO - flesh out survey questions (FUTURE)

        ImageView confirmationImage = findViewById(R.id.confirmation_image);
        confirmationImage.setImageResource(R.drawable.high_five_dog);

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.ui(LOG_TAG, ConfirmationActivity.this, "Confirmation return button clicked.");
                Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                intent.putExtra(Constants.BUNDLE_TASK_COMPLETE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                finish();
            }
        });
    }

}