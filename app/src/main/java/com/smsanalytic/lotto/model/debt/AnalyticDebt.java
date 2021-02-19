package com.smsanalytic.lotto.model.debt;

public class AnalyticDebt {
    private String idAccount;
    private String accountName;
    private double tienDe;
    private double tienLo;
    private double tienXien;
    private double tienBaCang;
    private double tatCa;
    private String date;

    public AnalyticDebt(String idAccount, String accountName, double tienDe, double tienLo, double tienXien, double tienBaCang) {
        this.idAccount = idAccount;
        this.accountName = accountName;
        this.tienDe = tienDe;
        this.tienLo = tienLo;
        this.tienXien = tienXien;
        this.tienBaCang = tienBaCang;
    }
    public AnalyticDebt(String date, double tienDe, double tienLo, double tienXien, double tienBaCang) {
        this.date = date;
        this.tienDe = tienDe;
        this.tienLo = tienLo;
        this.tienXien = tienXien;
        this.tienBaCang = tienBaCang;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public double getTatCa() {
        return tatCa;
    }

    public void setTatCa(double tatCa) {
        this.tatCa = tatCa;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getTienDe() {
        return tienDe;
    }

    public void setTienDe(double tienDe) {
        this.tienDe = tienDe;
    }

    public double getTienLo() {
        return tienLo;
    }

    public void setTienLo(double tienLo) {
        this.tienLo = tienLo;
    }

    public double getTienXien() {
        return tienXien;
    }

    public void setTienXien(double tienXien) {
        this.tienXien = tienXien;
    }

    public double getTienBaCang() {
        return tienBaCang;
    }

    public void setTienBaCang(double tienBaCang) {
        this.tienBaCang = tienBaCang;
    }
}
