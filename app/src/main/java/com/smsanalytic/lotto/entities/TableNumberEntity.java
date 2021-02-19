package com.smsanalytic.lotto.entities;

public class TableNumberEntity {
    public  String so;
    public  double tien;
    public  boolean isHit;
    public  int nhay;

    public TableNumberEntity(String so, double tien, boolean isHit) {
        this.so = so;
        this.tien = tien;
        this.isHit = isHit;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public double getTien() {
        return tien;
    }

    public void setTien(double tien) {
        this.tien = tien;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public int getNhay() {
        return nhay;
    }

    public void setNhay(int nhay) {
        this.nhay = nhay;
    }
}
