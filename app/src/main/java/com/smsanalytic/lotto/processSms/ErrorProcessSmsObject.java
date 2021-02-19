package com.smsanalytic.lotto.processSms;

public class ErrorProcessSmsObject {
    private String smsFormatFailed;
    private String smsError;

    public ErrorProcessSmsObject(String smsFormatFailed, String smsError) {
        this.smsFormatFailed = smsFormatFailed;
        this.smsError = smsError;
    }

    public String getSmsFormatFailed() {
        return smsFormatFailed;
    }

    public void setSmsFormatFailed(String smsFormatFailed) {
        this.smsFormatFailed = smsFormatFailed;
    }

    public String getSmsError() {
        return smsError;
    }

    public void setSmsError(String smsError) {
        this.smsError = smsError;
    }
}
