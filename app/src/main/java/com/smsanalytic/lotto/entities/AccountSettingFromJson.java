package com.smsanalytic.lotto.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountSettingFromJson {
    @SerializedName("phantichtindendi")
    @Expose
    public  List<AccountSettingItem> phantichtindendi = null;
    @SerializedName("traloitinnhan")
    @Expose
    public  List<AccountSettingItem> traloitinnhan = null;
    @SerializedName("kieubaocongno")
    @Expose
    public  List<AccountSettingItem> kieubaocongno = null;
    @SerializedName("chuyende78")
    @Expose
    public  List<AccountSettingItem> chuyende78 = null;
    @SerializedName("chapnhandonvilolanghin")
    @Expose
    public  List<AccountSettingItem> chapnhandonvilolanghin = null;
    @SerializedName("donvitinhde")
    @Expose
    public  List<AccountSettingItem> donvitinhde = null;
    @SerializedName("tudongtralaitinkhiquagionhan")
    @Expose
    public  List<AccountSettingItem> tudongtralaitinkhiquagionhan = null;
    @SerializedName("gioihannhan1so")
    @Expose
    public  List<AccountSettingItem> gioihannhan1so = null;
    @SerializedName("khachgiulaicophan")
    @Expose
    public  List<AccountSettingItem> khachgiulaicophan = null;
    @SerializedName("tudongchuyenditinden")
    @Expose
    public  List<AccountSettingItem> tudongchuyenditinden = null;
    @SerializedName("tudongnhantinchodlkhihetgionhan")
    @Expose
    public  List<AccountSettingItem> tudongnhantinchodlkhihetgionhan = null;
    @SerializedName("sonhaylotrathuongtoida")
    @Expose
    public  List<AccountSettingItem> sonhaylotrathuongtoida = null;

    public AccountSettingFromJson() {
    }

    public List<AccountSettingItem> getPhantichtindendi() {
        return phantichtindendi;
    }

    public void setPhantichtindendi(List<AccountSettingItem> phantichtindendi) {
        this.phantichtindendi = phantichtindendi;
    }

    public List<AccountSettingItem> getTraloitinnhan() {
        return traloitinnhan;
    }

    public void setTraloitinnhan(List<AccountSettingItem> traloitinnhan) {
        this.traloitinnhan = traloitinnhan;
    }

    public List<AccountSettingItem> getKieubaocongno() {
        return kieubaocongno;
    }

    public void setKieubaocongno(List<AccountSettingItem> kieubaocongno) {
        this.kieubaocongno = kieubaocongno;
    }

    public List<AccountSettingItem> getChuyende78() {
        return chuyende78;
    }

    public void setChuyende78(List<AccountSettingItem> chuyende78) {
        this.chuyende78 = chuyende78;
    }

    public List<AccountSettingItem> getChapnhandonvilolanghin() {
        return chapnhandonvilolanghin;
    }

    public void setChapnhandonvilolanghin(List<AccountSettingItem> chapnhandonvilolanghin) {
        this.chapnhandonvilolanghin = chapnhandonvilolanghin;
    }

    public List<AccountSettingItem> getDonvitinhde() {
        return donvitinhde;
    }

    public void setDonvitinhde(List<AccountSettingItem> donvitinhde) {
        this.donvitinhde = donvitinhde;
    }

    public List<AccountSettingItem> getTudongtralaitinkhiquagionhan() {
        return tudongtralaitinkhiquagionhan;
    }

    public void setTudongtralaitinkhiquagionhan(List<AccountSettingItem> tudongtralaitinkhiquagionhan) {
        this.tudongtralaitinkhiquagionhan = tudongtralaitinkhiquagionhan;
    }

    public List<AccountSettingItem> getGioihannhan1so() {
        return gioihannhan1so;
    }

    public void setGioihannhan1so(List<AccountSettingItem> gioihannhan1so) {
        this.gioihannhan1so = gioihannhan1so;
    }

    public List<AccountSettingItem> getKhachgiulaicophan() {
        return khachgiulaicophan;
    }

    public void setKhachgiulaicophan(List<AccountSettingItem> khachgiulaicophan) {
        this.khachgiulaicophan = khachgiulaicophan;
    }

    public List<AccountSettingItem> getTudongchuyenditinden() {
        return tudongchuyenditinden;
    }

    public void setTudongchuyenditinden(List<AccountSettingItem> tudongchuyenditinden) {
        this.tudongchuyenditinden = tudongchuyenditinden;
    }

    public List<AccountSettingItem> getTudongnhantinchodlkhihetgionhan() {
        return tudongnhantinchodlkhihetgionhan;
    }

    public void setTudongnhantinchodlkhihetgionhan(List<AccountSettingItem> tudongnhantinchodlkhihetgionhan) {
        this.tudongnhantinchodlkhihetgionhan = tudongnhantinchodlkhihetgionhan;
    }

    public List<AccountSettingItem> getSonhaylotrathuongtoida() {
        return sonhaylotrathuongtoida;
    }

    public void setSonhaylotrathuongtoida(List<AccountSettingItem> sonhaylotrathuongtoida) {
        this.sonhaylotrathuongtoida = sonhaylotrathuongtoida;
    }
}
