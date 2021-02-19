package com.smsanalytic.lotto.common;

public class BaoCongNo {
    private  double tong;
    private String content;
    private boolean hasNumber;

    public BaoCongNo(double tong, String content, boolean hasNumber) {
        this.tong = tong;
        this.content = content;
        this.hasNumber = hasNumber;
    }

    public boolean isHasNumber() {
        return hasNumber;
    }

    public void setHasNumber(boolean hasNumber) {
        this.hasNumber = hasNumber;
    }

    public double getTong() {
        return tong;
    }

    public void setTong(double tong) {
        this.tong = tong;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
