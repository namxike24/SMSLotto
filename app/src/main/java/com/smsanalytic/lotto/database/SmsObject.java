package com.smsanalytic.lotto.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

// Bảng lưu trữ tin nhắn
@Entity(nameInDb = "sms_tb")
public class SmsObject implements Cloneable {
    @Id(autoincrement = true)
    private Long idSms;

    @Property(nameInDb = "guid")
    private String guid; // id random khi nhận push

    @Property(nameInDb = "sms_root")
    private String smsRoot; // tin nhắn gốc

    @Property(nameInDb = "sms_processed")
    private String smsProcessed; // tin nhắn đã xử lý

    @Property(nameInDb = "sms_type")
    private int smsType; // kiểu tin nhắn (1:tin chờ,2: tin đã xủ lý,3: tin trống,4: tin nhắn mạng xã hội)

    @Property(nameInDb = "id_account")
    private String idAccouunt; // id của khánh hàng

    @Property(nameInDb = "group_title")
    private String groupTitle; // Tên nhóm

    @Property(nameInDb = "date")
    private long date; // thời gian gửi sms

    @Property(nameInDb = "sms_status")
    private int smsStatus; // trạng thái tin nhắn(1 : tin đến, 2 tin đi)

    @Property(nameInDb = "is_success")
    private boolean isSuccess; // trạng thái tin nhắn đã xử lý thành công

    @Property(nameInDb = "sms_format_failed")
    private String smsFormatFailed; // định dạng lỗi
    @Property(nameInDb = "mes_error")
    private String mesError; // nội dung lỗi

    @Property(nameInDb = "lotoHitDetail")
    private String lotoHitDetail; // đối tượng chứa các thuộc tính sau khi xử lý tin nhắn, tính tiền và số trúng

    @Property(nameInDb = "checkbox")
    private boolean checkbox;

    public SmsObject(String groupTitle, String smsRoot, String smsProcessed, int smsType, String idAccouunt, long date, int smsStatus) {
        this.smsRoot = smsRoot;
        this.smsProcessed = smsProcessed;
        this.smsType = smsType;
        this.idAccouunt = idAccouunt;
        this.groupTitle = groupTitle;
        this.date = date;
        this.smsStatus=smsStatus;
    }










    @Generated(hash = 1900041154)
    public SmsObject() {
    }










    @Generated(hash = 541150770)
    public SmsObject(Long idSms, String guid, String smsRoot, String smsProcessed, int smsType, String idAccouunt, String groupTitle, long date,
            int smsStatus, boolean isSuccess, String smsFormatFailed, String mesError, String lotoHitDetail, boolean checkbox) {
        this.idSms = idSms;
        this.guid = guid;
        this.smsRoot = smsRoot;
        this.smsProcessed = smsProcessed;
        this.smsType = smsType;
        this.idAccouunt = idAccouunt;
        this.groupTitle = groupTitle;
        this.date = date;
        this.smsStatus = smsStatus;
        this.isSuccess = isSuccess;
        this.smsFormatFailed = smsFormatFailed;
        this.mesError = mesError;
        this.lotoHitDetail = lotoHitDetail;
        this.checkbox = checkbox;
    }










    

   






    public String getLotoHitDetail() {
        return lotoHitDetail;
    }

    public void setLotoHitDetail(String lotoHitDetail) {
        this.lotoHitDetail = lotoHitDetail;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSmsFormatFailed() {
        return smsFormatFailed;
    }

    public void setSmsFormatFailed(String smsFormatFailed) {
        this.smsFormatFailed = smsFormatFailed;
    }

    public String getMesError() {
        return mesError;
    }

    public void setMesError(String mesError) {
        this.mesError = mesError;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Long getIdSms() {
        return idSms;
    }

    public void setIdSms(Long idSms) {
        this.idSms = idSms;
    }

    public String getSmsRoot() {
        return smsRoot;
    }

    public void setSmsRoot(String smsRoot) {
        this.smsRoot = smsRoot;
    }

    public String getSmsProcessed() {
        return smsProcessed;
    }

    public void setSmsProcessed(String smsProcessed) {
        this.smsProcessed = smsProcessed;
    }

    public int getSmsType() {
        return smsType;
    }

    public void setSmsType(int smsType) {
        this.smsType = smsType;
    }

    public String getIdAccouunt() {
        return idAccouunt;
    }

    public void setIdAccouunt(String idAccouunt) {
        this.idAccouunt = idAccouunt;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(int smsStatus) {
        this.smsStatus = smsStatus;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean getIsSuccess() {
        return this.isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }










    public boolean getCheckbox() {
        return this.checkbox;
    }










    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

}
