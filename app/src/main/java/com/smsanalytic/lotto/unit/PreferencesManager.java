package com.smsanalytic.lotto.unit;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "smsmanagementsystem.smsanalytic.com";

    public static final String SMS_FROM = "sms_from";
    public static final String GIULAILONNHAT_DEFAULT = "giulailonnhat_default";
    public static final String GIULAITHEOKHUC_DEFAULT = "giulaitheokhuc_default";
    public static final String GIULAITHEOKHUCPHANTRAM_DEFAULT = "giulaitheokhucphantram_default";
    public static final String SETTING_DEFAULT = "setting_default";
    public static final String COUNT_SMS_SUCCES = "countSmsSuccess";
    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setValue(String key, String value) {
        mPref.edit()
                .putString(key, value)
                .apply();

    }

    public void setValueBoolean(String key, boolean value) {
        mPref.edit()
                .putBoolean(key, value)
                .apply();
    }


    public void setValueInt(String key, int value) {
        mPref.edit()
                .putInt(key, value)
                .apply();
    }


    public int getValueInt(String key,int de) {
        return mPref.getInt(key, de);
    }

    public boolean getValueBoolean(String key,boolean de) {
        return mPref.getBoolean(key, de);
    }

    public String getValue(String key,String defalt) {
        return mPref.getString(key, defalt);
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}