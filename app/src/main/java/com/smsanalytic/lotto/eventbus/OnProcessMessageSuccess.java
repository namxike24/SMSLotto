package com.smsanalytic.lotto.eventbus;

import com.smsanalytic.lotto.database.SmsObject;

public class OnProcessMessageSuccess {
    private SmsObject data;

    public OnProcessMessageSuccess(SmsObject data) {
        this.data = data;
    }

    public SmsObject getData() {
        return data;
    }

    public void setData(SmsObject data) {
        this.data = data;
    }
}
