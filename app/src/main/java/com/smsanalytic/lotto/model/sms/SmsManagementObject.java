package com.smsanalytic.lotto.model.sms;

import java.util.List;

import com.smsanalytic.lotto.database.SmsObject;

public class SmsManagementObject {
    private String AccountName;
    private String desciption;
    private int waitprocessNumber;
    private List<SmsObject> smsObject;

    public SmsManagementObject(String accountName, String desciption, int waitprocessNumber,List<SmsObject> smsObject) {
        AccountName = accountName;
        this.desciption = desciption;
        this.waitprocessNumber = waitprocessNumber;
        this.smsObject=smsObject;
    }

    public List<SmsObject> getSmsObject() {
        return smsObject;
    }

    public void setSmsObject(List<SmsObject> smsObject) {
        this.smsObject = smsObject;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public int getWaitprocessNumber() {
        return waitprocessNumber;
    }

    public void setWaitprocessNumber(int waitprocessNumber) {
        this.waitprocessNumber = waitprocessNumber;
    }
}
