package com.smsanalytic.lotto.eventbus;

public class PushCountSmsWaitEvent {
    private int countSmsWait;

    public PushCountSmsWaitEvent(int countSmsWait) {
        this.countSmsWait = countSmsWait;
    }

    public int getCountSmsWait() {
        return countSmsWait;
    }

    public void setCountSmsWait(int countSmsWait) {
        this.countSmsWait = countSmsWait;
    }
}
