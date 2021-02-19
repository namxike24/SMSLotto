package com.smsanalytic.lotto.entities;

public class LienHeEntity {
    public  String gia_han;
    public  String ky_thuat;

    public LienHeEntity(String gia_han, String ky_thuat) {
        this.gia_han = gia_han;
        this.ky_thuat = ky_thuat;
    }

    public String getGia_han() {
        return gia_han;
    }

    public void setGia_han(String gia_han) {
        this.gia_han = gia_han;
    }

    public String getKy_thuat() {
        return ky_thuat;
    }

    public void setKy_thuat(String ky_thuat) {
        this.ky_thuat = ky_thuat;
    }
}
