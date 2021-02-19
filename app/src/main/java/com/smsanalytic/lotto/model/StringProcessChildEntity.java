package com.smsanalytic.lotto.model;

import java.util.ArrayList;

import com.smsanalytic.lotto.database.LotoNumberObject;

/**
 * object chứa các thành phần con của tin nhắn sau khi xử lý
 */
public class StringProcessChildEntity {
    private String value; //Text hiển thị sau khi xử lý
    private boolean isError; //có bị lỗi hay không
    private String error; //Lỗi gì
    private int type;
    private ArrayList<LotoNumberObject> listDataLoto; //Danh sách các con số sau khi xử lý xong

    private boolean showChild;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        isError = true;
        value = "<font color='#ff0000'>" + value + "</font>";
    }

    public ArrayList<LotoNumberObject> getListDataLoto() {
        if (listDataLoto == null) {
            listDataLoto = new ArrayList<>();
        }
        return listDataLoto;
    }

    public void setListDataLoto(ArrayList<LotoNumberObject> listDataLoto) {
        this.listDataLoto = listDataLoto;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isShowChild() {
        return showChild;
    }

    public void setShowChild(boolean showChild) {
        this.showChild = showChild;
    }
}
