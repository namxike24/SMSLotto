package com.smsanalytic.lotto.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

// Bảng lưu trữ thông tin số loto
@Entity(nameInDb = "loto_number_tb")
public class LotoNumberObject {
    @Id(autoincrement = true)
    private Long idLotoNumber;
    @Property(nameInDb = "value_1")
    private String value1; // giá trị 1

    @Property(nameInDb = "value_2")
    private String value2; // giá trị 2

    @Property(nameInDb = "value_3")
    private String value3; // giá trị 3

    @Property(nameInDb = "value_4")
    private String value4; // giá trị 4

    @Property(nameInDb = "money_take")
    private double moneyTake; // tiền đánh

    @Property(nameInDb = "money_send")
    private double moneySend; // tiền chuyển\

    @Property(nameInDb = "money_keep")
    private double moneyKeep; // tiền giữ lại



    @Property(nameInDb = "account_send")
    private String accountSend; // khách hàng gửi


    @Property(nameInDb = "date_take")
    private long dateTake; // thời gian nhận
    @Property(nameInDb = "date_string")
    private String dateString; // thời gian nhận type String

    @Property(nameInDb = "guid")
    private String guid; // id của tin nhắn gửi

    @Property(nameInDb = "nhay")
    private int nhay=1; // Trúng mấy nháy lô
    @Property(nameInDb = "hit")
    private boolean hit; // đánh trúng

    @Property(nameInDb = "type")
    private int type; // Loại

    @Property(nameInDb = "se_chuyen")
    private double seChuyen;


    @Property(nameInDb = "xien_format")
    private String xienFormat;

    @Property(nameInDb = "sms_status")
    private int smsStatus; // tin đến, tin đi

    public int getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(int smsStatus) {
        this.smsStatus = smsStatus;
    }

    public double getSeChuyen() {
        return seChuyen;
    }

    public void setSeChuyen(double seChuyen) {
        this.seChuyen = seChuyen;
    }

    @Generated(hash = 1194225653)
    @Keep
    public LotoNumberObject() {
    }

    public String getXienFormat() {
        return xienFormat;
    }

    public void setXienFormat(String xienFormat) {
        this.xienFormat = xienFormat;
    }

    public LotoNumberObject(long idLotoNumber, int type, String value1, double moneyTake, double moneySend) {
        this.idLotoNumber = idLotoNumber;
        this.type = type;
        this.value1 = value1;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
    }

    public LotoNumberObject(int type, double moneyTake, double moneySend, double seChuyen, double moneyKeep) {
        this.type = type;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
        this.moneyKeep = moneyKeep;
        this.seChuyen = seChuyen;
    }

    public LotoNumberObject(String xienFormat, String value1, String value2, int type, double moneyTake, double moneySend) {
        this.xienFormat = xienFormat;
        this.value1 = value1;
        this.value2 = value2;
        this.type = type;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
    }

    public LotoNumberObject(String  xienFormat, String value1, String value2, String value3, int type, double moneyTake, double moneySend) {
        this.xienFormat = xienFormat;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.type = type;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
    }

    public LotoNumberObject(String  xienFormat, String value1, String value2, String value3, String value4, int type, double moneyTake, double moneySend) {
        this.xienFormat = xienFormat;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.type = type;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
    }

    @Generated(hash = 565751433)
    public LotoNumberObject(Long idLotoNumber, String value1, String value2, String value3, String value4, double moneyTake, double moneySend, double moneyKeep,
            String accountSend, long dateTake, String dateString, String guid, int nhay, boolean hit, int type, double seChuyen, String xienFormat, int smsStatus) {
        this.idLotoNumber = idLotoNumber;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.moneyTake = moneyTake;
        this.moneySend = moneySend;
        this.moneyKeep = moneyKeep;
        this.accountSend = accountSend;
        this.dateTake = dateTake;
        this.dateString = dateString;
        this.guid = guid;
        this.nhay = nhay;
        this.hit = hit;
        this.type = type;
        this.seChuyen = seChuyen;
        this.xienFormat = xienFormat;
        this.smsStatus = smsStatus;
    }


    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getNhay(int nhayMax) {
        if (nhayMax<nhay){
            return nhayMax;
        }
        return nhay;
    }
    public int getNhay() {
        return nhay;
    }

    public void setNhay(int nhay) {
        this.nhay = nhay;
    }

    public Long getIdLotoNumber() {
        return this.idLotoNumber;
    }


    public void setIdLotoNumber(Long idLotoNumber) {
        this.idLotoNumber = idLotoNumber;
    }


    public String getValue1() {
        return this.value1;
    }


    public void setValue1(String value1) {
        this.value1 = value1;
    }


    public String getValue2() {
        return this.value2;
    }


    public void setValue2(String value2) {
        this.value2 = value2;
    }


    public String getValue3() {
        return this.value3;
    }


    public void setValue3(String value3) {
        this.value3 = value3;
    }


    public String getValue4() {
        return this.value4;
    }


    public void setValue4(String value4) {
        this.value4 = value4;
    }


    public double getMoneyTake() {
        return this.moneyTake;
    }


    public void setMoneyTake(double moneyTake) {
        this.moneyTake = moneyTake;
    }


    public double getMoneySend() {
        return this.moneySend;
    }


    public void setMoneySend(double moneySend) {
        this.moneySend = moneySend;
    }


    public double getMoneyKeep() {
        return this.moneyKeep;
    }


    public void setMoneyKeep(double moneyKeep) {
        this.moneyKeep = moneyKeep;
    }





    public String getAccountSend() {
        return this.accountSend;
    }


    public void setAccountSend(String accountSend) {
        this.accountSend = accountSend;
    }


    public long getDateTake() {
        return this.dateTake;
    }


    public void setDateTake(long dateTake) {
        this.dateTake = dateTake;
    }





    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean getHit() {
        return this.hit;
    }


    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isHit() {
        return hit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
