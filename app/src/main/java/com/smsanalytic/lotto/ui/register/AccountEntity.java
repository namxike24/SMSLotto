package com.smsanalytic.lotto.ui.register;

public class AccountEntity {
    public  String name;
    public  String imei;
    public  String pass;
    public  String passDelete;
    public  String dateExpired;
    public  boolean isLock;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPassDelete() {
        return passDelete;
    }

    public void setPassDelete(String passDelete) {
        this.passDelete = passDelete;
    }

    public String getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(String dateExpired) {
        this.dateExpired = dateExpired;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }
}
