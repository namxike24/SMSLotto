package com.smsanalytic.lotto.model.canchuyen;

public class BaoCaoNhanh100SoObject {
    private int type;
    private int sonhanve;
    private double sotonhat;
    private double sobenhat;
    private boolean trangthai;


    public BaoCaoNhanh100SoObject(int type, int sonhanve, double sotonhat, double sobenhat, boolean trangthai) {
        this.type = type;
        this.sonhanve = sonhanve;
        this.sotonhat = sotonhat;
        this.sobenhat = sobenhat;
        this.trangthai = trangthai;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSonhanve() {
        return sonhanve;
    }

    public void setSonhanve(int sonhanve) {
        this.sonhanve = sonhanve;
    }

    public double getSotonhat() {
        return sotonhat;
    }

    public void setSotonhat(double sotonhat) {
        this.sotonhat = sotonhat;
    }

    public double getSobenhat() {
        return sobenhat;
    }

    public void setSobenhat(double sobenhat) {
        this.sobenhat = sobenhat;
    }

    public boolean isTrangthai() {
        return trangthai;
    }

    public void setTrangthai(boolean trangthai) {
        this.trangthai = trangthai;
    }
}
