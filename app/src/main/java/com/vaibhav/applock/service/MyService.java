package com.vaibhav.applock.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.vaibhav.applock.Utilities;
import com.vaibhav.applock.locker.Main2Activity;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by vaibhav on 2/1/17.
 */

public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getForegroundProcess(Context context) {
        String topPackageName = null;
        UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (runningTask.isEmpty()) {
                return null;
            }
            topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
        }
        if (topPackageName == null) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            context.startActivity(intent);
        }

        return topPackageName;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        String foregroundProcess = getForegroundProcess(MyService.this);
        Toast.makeText(this, foregroundProcess, Toast.LENGTH_LONG).show();

        final String str = "";
        Timer timer = new Timer();
        final String[] previousForeGround = {""};
        boolean check = false;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                String foregroundProcess = getForegroundProcess(MyService.this);


                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

                for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfo) {
                    String processName = appProcess.processName;
                    Log.d(foregroundProcess, "is running");
                    boolean value = Utilities.getInstance().getValue(MyService.this, foregroundProcess);
                    if (value && !previousForeGround[0].equals(foregroundProcess)) {
                        if (!foregroundProcess.equals(processName)) {
                            previousForeGround[0] = foregroundProcess;
                            Intent intent1 = new Intent(MyService.this, Main2Activity.class);
                            startActivity(intent1);
                        }

                    } else {
                        if (!foregroundProcess.equals(processName))
                            previousForeGround[0] = foregroundProcess;
                    }
                }
            }
        }, 2000, 3000);

        return START_STICKY;
    }
}
