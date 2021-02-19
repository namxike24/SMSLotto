package com.smsanalytic.lotto.cal;

public class ReportCalEntity {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_NAME = 1;
    public static final int TYPE_VALUE = 2;
    private int type;
    private String date;
    private String customerID;
    private String customerName;
    private double value;

    public ReportCalEntity() {
    }

    public ReportCalEntity(int type, String date) {
        this.type = type;
        this.date = date;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
