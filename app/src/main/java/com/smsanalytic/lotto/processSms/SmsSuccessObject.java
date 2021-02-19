package com.smsanalytic.lotto.processSms;

import java.util.ArrayList;

import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.model.StringProcessEntity;

public class SmsSuccessObject {
    private String value = "";
    private ArrayList<LotoNumberObject> listDataLoto;
    private ArrayList<Content> contents;
    private StringProcessEntity processEntity;
    private String smsFormatFailed = "";
    private String mesError = "";
    private boolean isProcessSuccess;

    public SmsSuccessObject() {

    }

    public SmsSuccessObject(String value, ArrayList<LotoNumberObject> listDataLoto, boolean isProcessSuccess ) {
        this.value = value;
        this.listDataLoto = listDataLoto;
        this.isProcessSuccess = isProcessSuccess;
    }

    public ArrayList<Content> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    public SmsSuccessObject(StringProcessEntity processEntity, boolean isProcessSuccess) {
        this.processEntity = processEntity;
        this.isProcessSuccess = isProcessSuccess;

    }

    public boolean isProcessSuccess() {
        return isProcessSuccess;
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

    public void setProcessSuccess(boolean processSuccess) {
        isProcessSuccess = processSuccess;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<LotoNumberObject> getListDataLoto() {
        return listDataLoto;
    }

    public void setListDataLoto(ArrayList<LotoNumberObject> listDataLoto) {
        this.listDataLoto = listDataLoto;
    }

    public StringProcessEntity getProcessEntity() {
        return processEntity;
    }

    public void setProcessEntity(StringProcessEntity processEntity) {
        this.processEntity = processEntity;
    }
}
