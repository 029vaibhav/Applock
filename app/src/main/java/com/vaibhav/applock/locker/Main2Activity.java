package com.vaibhav.applock.locker;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vaibhav.applock.R;
import com.vaibhav.applock.Utilities;

public class Main2Activity extends AppCompatActivity {

    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final EditText editText = (EditText) findViewById(R.id.pass);

        final int[] count = {0};
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utilities.getInstance().isPassCorrect(Main2Activity.this, Utilities.PASS_KEY, editText.getText().toString()))
                    minimizeApp();
                else {
                    Toast.makeText(Main2Activity.this, "please enter correct pass", Toast.LENGTH_SHORT).show();
                    count[0]++;
                    if (count[0] >= 3) {
                        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                        mDPM.wipeData(0);
                        button.setEnabled(false);
                    }
                }
            }
        });
    }

    public void minimizeApp() {
        moveTaskToBack(true);

    }
}
