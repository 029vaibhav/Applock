package com.vaibhav.applock;

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vaibhav.applock.adapter.PackageAdapter;
import com.vaibhav.applock.bean.AppInfo;
import com.vaibhav.applock.service.MyService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button button;
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;

    protected static final int REQUEST_ENABLE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        button = (Button) findViewById(R.id.set_pass);


        PackageAdapter packageAdapter = new PackageAdapter(this, getPackages());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(packageAdapter);
        Intent intent2 = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(MainActivity.this, AdminAccess.class);
        if (!mDPM.isAdminActive(mDeviceAdmin)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    mDeviceAdmin);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Give me access");
            startActivityForResult(intent, REQUEST_ENABLE);
        }

        Intent service = new Intent(getApplicationContext(), MyService.class);
        getApplicationContext().startService(service);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:
                    Log.v("MainActivity", "Enabling Policies Now");
                    mDPM.setMaximumTimeToLock(
                            mDeviceAdmin, 30000L);
                    mDPM.setMaximumFailedPasswordsForWipe(
                            mDeviceAdmin, 5);
                    mDPM.setPasswordQuality(
                            mDeviceAdmin,
                            DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
                    mDPM.setCameraDisabled(
                            mDeviceAdmin, true);
                    boolean isSufficient = mDPM
                            .isActivePasswordSufficient();
                    if (isSufficient) {
                        mDPM.lockNow();
                    }
                    break;
            }
        } else {
            Log.d("Main_Activit", "not successfull");
        }
    }


    private ArrayList<AppInfo> getPackages() {
        ArrayList<AppInfo> apps = getInstalledApps(false); /* false = no system packages */
//        final int max = apps.size();
        return apps;
    }

    private ArrayList<AppInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<AppInfo> res = new ArrayList<AppInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            AppInfo newInfo = new AppInfo();
            newInfo.setAppname(p.applicationInfo.loadLabel(getPackageManager()).toString());
            newInfo.setPname(p.packageName);
            newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
            res.add(newInfo);
        }
        return res;
    }


    private void showDialog() {
        final String[] password = new String[1];
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        password[0] = input.getText().toString();
                        if (password[0].length() == 0) {
                            Toast.makeText(MainActivity.this, "please enter one word", Toast.LENGTH_SHORT).show();
                        } else {
                            Utilities.getInstance().storePrefs(MainActivity.this, Utilities.PASS_KEY, password[0]);
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    public class AdminAccess extends DeviceAdminReceiver {

        public AdminAccess() {
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            Toast.makeText(context, "Applock's Device Admin Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            Toast.makeText(context, "Applock's Device Admin is now enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            CharSequence disableRequestedSeq = "Requesting to disable Device Admin";
            return disableRequestedSeq;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            Toast.makeText(context, "Device password is now changed",
                    Toast.LENGTH_SHORT).show();
            DevicePolicyManager localDPM = (DevicePolicyManager) context
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName localComponent = new ComponentName(context,
                    AdminAccess.class);
            localDPM.setPasswordExpirationTimeout(localComponent, 0L);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
            // This would require API 11 an above
            Toast.makeText(
                    context,
                    "Applock's Device password is going to expire, please change to a new password",
                    Toast.LENGTH_LONG).show();

            DevicePolicyManager localDPM = (DevicePolicyManager) context
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName localComponent = new ComponentName(context,
                    AdminAccess.class);
            long expr = localDPM.getPasswordExpiration(localComponent);
            long delta = expr - System.currentTimeMillis();
            boolean expired = delta < 0L;
            if (expired) {
                localDPM.setPasswordExpirationTimeout(localComponent, 10000L);
                Intent passwordChangeIntent = new Intent(
                        DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                passwordChangeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(passwordChangeIntent);
            }
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
            Toast.makeText(context, "Password failed", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
            Toast.makeText(context, "Access Granted", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Admin_Access", "MyDevicePolicyReciever Received: " + intent.getAction());
            super.onReceive(context, intent);
        }
    }
}
