package com.smsanalytic.lotto.processSms;

public class Content {
    int type;
    String value;
    boolean quaGio=false;
    long date;

    public Content(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public Content(int type, String value, long date) {
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isQuaGio() {
        return quaGio;
    }

    public void setQuaGio(boolean quaGio) {
        this.quaGio = quaGio;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
