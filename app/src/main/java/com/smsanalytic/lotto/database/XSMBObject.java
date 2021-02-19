package com.smsanalytic.lotto.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "xsmb_tb")
public class XSMBObject {
    @Id(autoincrement = true)
    private Long xsmbID;
    @Property(nameInDb = "date")
    private String date;
    @Property(nameInDb = "result")
    private String result;
    @Generated(hash = 1827523905)
    public XSMBObject(Long xsmbID, String date, String result) {
        this.xsmbID = xsmbID;
        this.date = date;
        this.result = result;
    }
    @Generated(hash = 1333048880)
    public XSMBObject() {
    }
    public Long getXsmbID() {
        return this.xsmbID;
    }
    public void setXsmbID(Long xsmbID) {
        this.xsmbID = xsmbID;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getResult() {
        return this.result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}
