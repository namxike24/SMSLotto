package com.smsanalytic.lotto.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

// Bảng lưu trữ công nợ
@Entity(nameInDb = "debt_tb")
public class DebtObject  {
    @Id(autoincrement = true)
    private Long idDebt;

    @Property(nameInDb = "sms_root")
    private String smsRoot; // tin nhắn gốc

    @Property(nameInDb = "id_account")
    private String idAccouunt; // id của khánh hàng

    @Property(nameInDb = "account_name")
    private String accountName; // tên của khánh hàng

    @Property(nameInDb = "date")
    private long date; // thời gian gửi sms

    @Property(nameInDb = "expenses")
    private double expenses; // tiền phát sinh hôm ngày hiện tại

    @Property(nameInDb = "old_debt")
    private double oldebt; // Tiền nợ cũ

    @Property(nameInDb = "money_pay")
    private double moneyPay; // Tiền đã chi

    @Property(nameInDb = "money_received")
    private double moneyReceived; // Tiền đã nhận

    public DebtObject(long date,String idAccouunt,String accountName, double expenses, double oldebt, double moneyPay, double moneyReceived) {
        this.date=date;
        this.idAccouunt = idAccouunt;
        this.accountName = accountName;
        this.expenses = expenses;
        this.oldebt = oldebt;
        this.moneyPay = moneyPay;
        this.moneyReceived = moneyReceived;
    }


    @Generated(hash = 789486521)
    public DebtObject() {
    }


    @Generated(hash = 737909791)
    public DebtObject(Long idDebt, String smsRoot, String idAccouunt, String accountName, long date, double expenses, double oldebt,
            double moneyPay, double moneyReceived) {
        this.idDebt = idDebt;
        this.smsRoot = smsRoot;
        this.idAccouunt = idAccouunt;
        this.accountName = accountName;
        this.date = date;
        this.expenses = expenses;
        this.oldebt = oldebt;
        this.moneyPay = moneyPay;
        this.moneyReceived = moneyReceived;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getIdDebt() {
        return this.idDebt;
    }

    public void setIdDebt(Long idDebt) {
        this.idDebt = idDebt;
    }

    public String getSmsRoot() {
        return this.smsRoot;
    }

    public void setSmsRoot(String smsRoot) {
        this.smsRoot = smsRoot;
    }

    public String getIdAccouunt() {
        return this.idAccouunt;
    }

    public void setIdAccouunt(String idAccouunt) {
        this.idAccouunt = idAccouunt;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getExpenses() {
        return this.expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getOldebt() {
        return this.oldebt;
    }

    public void setOldebt(double oldebt) {
        this.oldebt = oldebt;
    }

    public double getMoneyPay() {
        return this.moneyPay;
    }

    public void setMoneyPay(double moneyPay) {
        this.moneyPay = moneyPay;
    }

    public double getMoneyReceived() {
        return this.moneyReceived;
    }

    public void setMoneyReceived(double moneyReceived) {
        this.moneyReceived = moneyReceived;
    }


}
