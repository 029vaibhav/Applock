package com.vaibhav.applock;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vaibhav on 2/1/17.
 */

public class Utilities {

    private static Utilities utilities;
    public static String PASS_KEY = "passKey";

    public static Utilities getInstance() {
        if (utilities == null)
            utilities = new Utilities();
        return utilities;
    }

    public void storePrefs(Context c, String key) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, true).apply();
    }

    public void storePrefs(Context c, String key, String value) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value).commit();
    }

    public boolean isPassCorrect(Context c, String key, String value) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (value.length() == 0) {
            value = null;
        }
        if (string.equals(value)) return true;
        return false;
    }

    public boolean getValue(Context c, String key) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);

    }


    public void remove(Context c, String key) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(key).apply();
    }

    public void action(Context c, boolean check, String key) {
        if (check) {
            storePrefs(c, key);
            return;
        }
        remove(c, key);
    }

}
