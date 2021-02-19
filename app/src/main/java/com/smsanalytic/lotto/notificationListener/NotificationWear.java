package com.smsanalytic.lotto.notificationListener;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class NotificationWear implements Serializable  {
    @SerializedName("id")
    private String id = UUID.randomUUID().toString();
    @SerializedName("packageName")
    private String packageName = "";
    @SerializedName("pendingIntent")
    private PendingIntent pendingIntent;
    @SerializedName("remoteInputs")
    private ArrayList<RemoteInput> remoteInputs = new ArrayList<>();
    @SerializedName("bundle")
    private Bundle bundle;
    @SerializedName("tag")
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public ArrayList<RemoteInput> getRemoteInputs() {
        return remoteInputs;
    }

    public void setRemoteInputs(ArrayList<RemoteInput> remoteInputs) {
        this.remoteInputs = remoteInputs;
    }


    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
