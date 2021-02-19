package com.smsanalytic.lotto.model.canchuyen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GiuLaiLonNhatDefault implements Serializable {
    @SerializedName("db")
    @Expose
    private boolean db;
    @SerializedName("dbvalue")
    @Expose
    private double dbvalue;
    @SerializedName("giainhat")
    @Expose
    private boolean giainhat;
    @SerializedName("giainhatvalue")
    @Expose
    private double giainhatvalue;
    @SerializedName("loto")
    @Expose
    private boolean loto;
    @SerializedName("lotovalue")
    @Expose
    private double lotovalue;
    @SerializedName("bacang")
    @Expose
    private boolean bacang;
    @SerializedName("bacangvalue")
    @Expose
    private double bacangvalue;
    @SerializedName("xien")
    @Expose
    private boolean xien;
    @SerializedName("x2value")
    @Expose
    private double x2value;
    @SerializedName("x3value")
    @Expose
    private double x3value;
    @SerializedName("x4value")
    @Expose
    private double x4value;

    private boolean isCanchuyenTheoPhanTram;
    private boolean isChuyenTheoTongNhanVe;

    public boolean getDb() {
        return db;
    }

    public void setDb(boolean db) {
        this.db = db;
    }

    public double getDbvalue() {
        return dbvalue;
    }

    public void setDbvalue(double dbvalue) {
        this.dbvalue = dbvalue;
    }

    public boolean getGiainhat() {
        return giainhat;
    }

    public void setGiainhat(boolean giainhat) {
        this.giainhat = giainhat;
    }

    public double getGiainhatvalue() {
        return giainhatvalue;
    }

    public void setGiainhatvalue(double giainhatvalue) {
        this.giainhatvalue = giainhatvalue;
    }

    public boolean getLoto() {
        return loto;
    }

    public void setLoto(boolean loto) {
        this.loto = loto;
    }

    public double getLotovalue() {
        return lotovalue;
    }

    public void setLotovalue(double lotovalue) {
        this.lotovalue = lotovalue;
    }

    public boolean getBacang() {
        return bacang;
    }

    public void setBacang(boolean bacang) {
        this.bacang = bacang;
    }

    public double getBacangvalue() {
        return bacangvalue;
    }

    public void setBacangvalue(double bacangvalue) {
        this.bacangvalue = bacangvalue;
    }

    public boolean getXien() {
        return xien;
    }

    public void setXien(boolean xien) {
        this.xien = xien;
    }

    public double getX2value() {
        return x2value;
    }

    public void setX2value(double x2value) {
        this.x2value = x2value;
    }

    public double getX3value() {
        return x3value;
    }

    public void setX3value(double x3value) {
        this.x3value = x3value;
    }

    public double getX4value() {
        return x4value;
    }

    public void setX4value(double x4value) {
        this.x4value = x4value;
    }

    public boolean isCanchuyenTheoPhanTram() {
        return isCanchuyenTheoPhanTram;
    }

    public void setCanchuyenTheoPhanTram(boolean canchuyenTheoPhanTram) {
        isCanchuyenTheoPhanTram = canchuyenTheoPhanTram;
    }

    public boolean isChuyenTheoTongNhanVe() {
        return isChuyenTheoTongNhanVe;
    }

    public void setChuyenTheoTongNhanVe(boolean chuyenTheoTongNhanVe) {
        isChuyenTheoTongNhanVe = chuyenTheoTongNhanVe;
    }
}
