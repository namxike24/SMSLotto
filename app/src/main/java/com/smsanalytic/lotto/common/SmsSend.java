package com.smsanalytic.lotto.common;

public class SmsSend {
    String sms;
    boolean isSend;

    public SmsSend(String sms, boolean isSend) {
        this.sms = sms;
        this.isSend = isSend;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
