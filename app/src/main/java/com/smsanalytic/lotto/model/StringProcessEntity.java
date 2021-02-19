package com.smsanalytic.lotto.model;

import java.util.ArrayList;

/**
 * object xử lý tin nhắn
 */
public class StringProcessEntity {
    private String original; //Tin nhắn  gốc
    private ArrayList<StringProcessChildEntity> listChild; //Danh sách các thành phần con sau khi xử lý
    private boolean hasError;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public ArrayList<StringProcessChildEntity> getListChild() {
        return listChild;
    }

    public void setListChild(ArrayList<StringProcessChildEntity> listChild) {
        this.listChild = listChild;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
