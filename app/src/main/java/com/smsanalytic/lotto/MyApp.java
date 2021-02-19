package com.smsanalytic.lotto;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.multidex.MultiDexApplication;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.KyTuThayThe;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.database.DaoMaster;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.model.WordReplaceEntity;
import com.smsanalytic.lotto.ui.register.AccountEntity;
import com.smsanalytic.lotto.unit.PreferencesManager;


public class MyApp extends MultiDexApplication {
    private DaoSession daoSession;
    private static Context context;
    private static MyApp instance;

    public static ArrayList<WordReplaceEntity> listWordDefault;
    public static ArrayList<WordReplaceEntity> listWordCustom;


    public  static HashMap<String, StatusBarNotification> replyHaspMap;

    public static DatabaseReference mFirebaseDatabase;
    public static FirebaseDatabase mFirebaseInstance;

    public static ArrayList<Bundle> listNotificationBundle = new ArrayList<>();

    public static AccountEntity currentAccount;
    public static boolean isApprunning = true;
    public static String imei;



    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        PreferencesManager.initializeInstance(getBaseContext());
        String DB_NAME = "Loto_DB_V1.0";
        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        Database db = helper.getWritableDb();
        StringCalculate.getInstance(this);
        processWordReplace();
        MultiDex.install(this);
        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");
        daoSession = new DaoMaster(db).newSession();
        FirebaseApp.initializeApp(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

    }

    private void processWordReplace() {
        try {
            String cacheDefault = PreferencesManager.getInstance().getValue(Common.LIST_WORD_DEFAULT, "");
            if (TextUtils.isEmpty(cacheDefault)) {
                listWordDefault = new ArrayList<>();
            } else {
                TypeToken<List<WordReplaceEntity>> typeToken = new TypeToken<List<WordReplaceEntity>>() {
                };
                listWordDefault = new Gson().fromJson(cacheDefault, typeToken.getType());

                for (WordReplaceEntity entity : listWordDefault) {
                    for (KyTuThayThe kyTuThayThe : StringCalculate.listKyTuThayThe) {
                        if (kyTuThayThe.getType().trim().equalsIgnoreCase(entity.getWord())){
                            kyTuThayThe.getDatas().add(entity.getWordDisplay());
                            break;
                        }
                    }
                }
            }

            String cacheCustom = PreferencesManager.getInstance().getValue(Common.LIST_WORD_CUSTOM, "");
            if (TextUtils.isEmpty(cacheCustom)) {
                listWordCustom = new ArrayList<>();
            } else {
                TypeToken<List<WordReplaceEntity>> typeToken = new TypeToken<List<WordReplaceEntity>>() {
                };
                listWordCustom = new Gson().fromJson(cacheCustom, typeToken.getType());
                for (WordReplaceEntity entity : listWordCustom) {
                    for (KyTuThayThe kyTuThayThe : StringCalculate.listKyTuThayThe) {
                        if (kyTuThayThe.getType().trim().equalsIgnoreCase(entity.getWord())){
                            kyTuThayThe.getDatas().add(entity.getWordDisplay());
                            break;
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


    public static Context getContext() {
        return context;
    }
}
