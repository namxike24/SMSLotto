package com.smsanalytic.lotto.eventbus;

import com.smsanalytic.lotto.database.SmsObject;

public class PushNotificationEvent {
    private SmsObject smsObject;

    public PushNotificationEvent(SmsObject smsObject) {
        this.smsObject = smsObject;
    }

    public PushNotificationEvent() {
    }

    public SmsObject getSmsObject() {
        return smsObject;
    }

    public void setSmsObject(SmsObject smsObject) {
        this.smsObject = smsObject;
    }
}
