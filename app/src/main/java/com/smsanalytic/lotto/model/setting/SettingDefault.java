package com.smsanalytic.lotto.model.setting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SettingDefault implements Serializable {
    @SerializedName("timeKhongNhanLo")
    @Expose
    private long timeKhongNhanLo;
    @SerializedName("timeKhongNhanDe")
    @Expose
    private long timeKhongNhanDe;
    @SerializedName("tuDongCanChuyen")
    @Expose
    private boolean tuDongCanChuyen;
    @SerializedName("timeCanChuyenLoTu")
    @Expose
    private long timeCanChuyenLoTu;
    @SerializedName("timeCanChuyenLoDen")
    @Expose
    private long timeCanChuyenLoDen;
    @SerializedName("timeCanChuyenDeTu")
    @Expose
    private long timeCanChuyenDeTu;
    @SerializedName("timeCanChuyenDeDen")
    @Expose
    private long timeCanChuyenDeDen;
    @SerializedName("nguoiNhanCanChuyen")
    @Expose
    private String nguoiNhanCanChuyen;
    @SerializedName("dBGiuLaiMax")
    @Expose
    private double dBGiuLaiMax;
    @SerializedName("loGiuaLaimax")
    @Expose
    private double loGiuaLaimax;
    @SerializedName("baSoGiuLaiMax")
    @Expose
    private double baSoGiuLaiMax;
    @SerializedName("giaiNhatGiuLaiMax")
    @Expose
    private double giaiNhatGiuLaiMax;
    @SerializedName("x2GiulaiMax")
    @Expose
    private double x2GiulaiMax;
    @SerializedName("x3GiulaiMax")
    @Expose
    private double x3GiulaiMax;
    @SerializedName("x4GiulaiMax")
    @Expose
    private double x4GiulaiMax;
    @SerializedName("tuDongBoTinGiong")
    @Expose
    private boolean tuDongBoTinGiong;
    @SerializedName("tuDongGuiTinCongNo")
    @Expose
    private boolean tuDongGuiTinCongNo;
    @SerializedName("timeGuiCongNo")
    @Expose
    private long timeGuiCongNo;
    @SerializedName("doDaiTinNhan")
    @Expose
    private int doDaiTinNhan;
    @SerializedName("donViXien")
    @Expose
    private int donViXien;
    @SerializedName("donviXienChuyen")
    @Expose
    private int donviXienChuyen;
    @SerializedName("tiente")
    @Expose
    private int tiente;

    public int getTiente() {
        return tiente;
    }

    public void setTiente(int tiente) {
        this.tiente = tiente;
    }

    public long getTimeKhongNhanLo() {
        return timeKhongNhanLo;
    }

    public void setTimeKhongNhanLo(long timeKhongNhanLo) {
        this.timeKhongNhanLo = timeKhongNhanLo;
    }

    public long getTimeKhongNhanDe() {
        return timeKhongNhanDe;
    }

    public void setTimeKhongNhanDe(long timeKhongNhanDe) {
        this.timeKhongNhanDe = timeKhongNhanDe;
    }

    public boolean isTuDongCanChuyen() {
        return tuDongCanChuyen;
    }

    public void setTuDongCanChuyen(boolean tuDongCanChuyen) {
        this.tuDongCanChuyen = tuDongCanChuyen;
    }

    public long getTimeCanChuyenLoTu() {
        return timeCanChuyenLoTu;
    }

    public void setTimeCanChuyenLoTu(long timeCanChuyenLoTu) {
        this.timeCanChuyenLoTu = timeCanChuyenLoTu;
    }

    public long getTimeCanChuyenLoDen() {
        return timeCanChuyenLoDen;
    }

    public void setTimeCanChuyenLoDen(long timeCanChuyenLoDen) {
        this.timeCanChuyenLoDen = timeCanChuyenLoDen;
    }

    public long getTimeCanChuyenDeTu() {
        return timeCanChuyenDeTu;
    }

    public void setTimeCanChuyenDeTu(long timeCanChuyenDeTu) {
        this.timeCanChuyenDeTu = timeCanChuyenDeTu;
    }

    public long getTimeCanChuyenDeDen() {
        return timeCanChuyenDeDen;
    }

    public void setTimeCanChuyenDeDen(long timeCanChuyenDeDen) {
        this.timeCanChuyenDeDen = timeCanChuyenDeDen;
    }

    public String getNguoiNhanCanChuyen() {
        return nguoiNhanCanChuyen;
    }

    public void setNguoiNhanCanChuyen(String nguoiNhanCanChuyen) {
        this.nguoiNhanCanChuyen = nguoiNhanCanChuyen;
    }

    public double getdBGiuLaiMax() {
        return dBGiuLaiMax;
    }

    public void setdBGiuLaiMax(double dBGiuLaiMax) {
        this.dBGiuLaiMax = dBGiuLaiMax;
    }

    public double getLoGiuaLaimax() {
        return loGiuaLaimax;
    }

    public void setLoGiuaLaimax(double loGiuaLaimax) {
        this.loGiuaLaimax = loGiuaLaimax;
    }

    public double getBaSoGiuLaiMax() {
        return baSoGiuLaiMax;
    }

    public void setBaSoGiuLaiMax(double baSoGiuLaiMax) {
        this.baSoGiuLaiMax = baSoGiuLaiMax;
    }

    public double getGiaiNhatGiuLaiMax() {
        return giaiNhatGiuLaiMax;
    }

    public void setGiaiNhatGiuLaiMax(double giaiNhatGiuLaiMax) {
        this.giaiNhatGiuLaiMax = giaiNhatGiuLaiMax;
    }

    public double getX2GiulaiMax() {
        return x2GiulaiMax;
    }

    public void setX2GiulaiMax(double x2GiulaiMax) {
        this.x2GiulaiMax = x2GiulaiMax;
    }

    public double getX3GiulaiMax() {
        return x3GiulaiMax;
    }

    public void setX3GiulaiMax(double x3GiulaiMax) {
        this.x3GiulaiMax = x3GiulaiMax;
    }

    public double getX4GiulaiMax() {
        return x4GiulaiMax;
    }

    public void setX4GiulaiMax(double x4GiulaiMax) {
        this.x4GiulaiMax = x4GiulaiMax;
    }

    public boolean isTuDongBoTinGiong() {
        return tuDongBoTinGiong;
    }

    public void setTuDongBoTinGiong(boolean tuDongBoTinGiong) {
        this.tuDongBoTinGiong = tuDongBoTinGiong;
    }

    public boolean isTuDongGuiTinCongNo() {
        return tuDongGuiTinCongNo;
    }

    public void setTuDongGuiTinCongNo(boolean tuDongGuiTinCongNo) {
        this.tuDongGuiTinCongNo = tuDongGuiTinCongNo;
    }

    public long getTimeGuiCongNo() {
        return timeGuiCongNo;
    }

    public void setTimeGuiCongNo(long timeGuiCongNo) {
        this.timeGuiCongNo = timeGuiCongNo;
    }

    public int getDoDaiTinNhan() {
        return doDaiTinNhan;
    }

    public void setDoDaiTinNhan(int doDaiTinNhan) {
        this.doDaiTinNhan = doDaiTinNhan;
    }

    public int getDonViXien() {
        return donViXien;
    }

    public void setDonViXien(int donViXien) {
        this.donViXien = donViXien;
    }

    public int getDonviXienChuyen() {
        return donviXienChuyen;
    }

    public void setDonviXienChuyen(int donviXienChuyen) {
        this.donviXienChuyen = donviXienChuyen;
    }
}
