package com.smsanalytic.lotto.entities;

public class SmsSendObject {
    public  double money;
    public  String content;
    public  int type;

    public SmsSendObject(double money, String content) {
        this.money = money;
        this.content = content;
    }

    public SmsSendObject(double money, String content, int type) {
        this.money = money;
        this.content = content;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
