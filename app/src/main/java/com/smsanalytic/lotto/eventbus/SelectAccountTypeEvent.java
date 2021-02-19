package com.smsanalytic.lotto.eventbus;

public class SelectAccountTypeEvent {
    int accountType;

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public SelectAccountTypeEvent(int accountType) {
        this.accountType = accountType;
    }
}
