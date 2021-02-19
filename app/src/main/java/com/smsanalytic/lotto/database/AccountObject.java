package com.smsanalytic.lotto.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

// Bảng lưu trữ khách hàng
@Entity(nameInDb = "account_tb")
public class AccountObject {
    @Id(autoincrement = true)
    private Long noAccount;

    @Property(nameInDb = "id_account")
    private String idAccount; // id của khách hàng

    @Property(nameInDb = "account_name")
    private String accountName; // tên của khách hàng

    @Property(nameInDb = "them_call_you")
    private String themCallYou; // họ gọi bạn là
    @Property(nameInDb = "you_call_them")
    private String youCallThem; // bạn gọi họ


    @Property(nameInDb = "phone")
    private String phone; // số điện thoại

    @Property(nameInDb = "date_create")
    private long dateCreate; // Ngày Tạo

    @Property(nameInDb = "account_type")
    private int accountType; // loại khách hàng( 1: đại lý; 2:chủ , 3: đại lý và chủ)

    @Property(nameInDb = "account_status")
    private int accountStatus; //  khách hàng từ( 1: FB; 2:ZALO , 3: SDT)

    @Property(nameInDb = "account_rate")
    private String accountRate; // ti lệ ăn chia cho khách hàng

    @Property(nameInDb = "account_setting")
    private String accountSetting; // cài đặt cho khách hàng

















    

    @Generated(hash = 1864946651)
    public AccountObject() {
    }

















    @Generated(hash = 834131094)
    public AccountObject(Long noAccount, String idAccount, String accountName,
            String themCallYou, String youCallThem, String phone, long dateCreate,
            int accountType, int accountStatus, String accountRate, String accountSetting) {
        this.noAccount = noAccount;
        this.idAccount = idAccount;
        this.accountName = accountName;
        this.themCallYou = themCallYou;
        this.youCallThem = youCallThem;
        this.phone = phone;
        this.dateCreate = dateCreate;
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.accountRate = accountRate;
        this.accountSetting = accountSetting;
    }


    











    


    public String getAccountRate() {
        return accountRate;
    }

    public void setAccountRate(String accountRate) {
        this.accountRate = accountRate;
    }

    public Long getNoAccount() {
        return this.noAccount;
    }


    public void setNoAccount(Long noAccount) {
        this.noAccount = noAccount;
    }


    public String getIdAccount() {
        return this.idAccount;
    }


    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }


    public String getAccountName() {
        return this.accountName;
    }


    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public String getThemCallYou() {
        return this.themCallYou;
    }


    public void setThemCallYou(String themCallYou) {
        this.themCallYou = themCallYou;
    }


    public String getYouCallThem() {
        return this.youCallThem;
    }


    public void setYouCallThem(String youCallThem) {
        this.youCallThem = youCallThem;
    }


    public String getPhone() {
        return this.phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getAccountType() {
        return this.accountType;
    }


    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }


    public String getAccountSetting() {
        return this.accountSetting;
    }


    public void setAccountSetting(String accountSetting) {
        this.accountSetting = accountSetting;
    }

    public long getDateCreate() {
        return this.dateCreate;
    }

    public void setDateCreate(long dateCreate) {
        this.dateCreate = dateCreate;
    }

















    public int getAccountStatus() {
        return this.accountStatus;
    }

















    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }


}
