package com.smsanalytic.lotto.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountSettingItem {
    @SerializedName("id")
    @Expose
    public  Integer id;
    @SerializedName("text")
    @Expose
    public  String text;
    @SerializedName("selected")
    @Expose
    public  Boolean selected;

    public AccountSettingItem(Integer id, String text, Boolean selected) {
        this.id = id;
        this.text = text;
        this.selected = selected;
    }

    public AccountSettingItem() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return text;
    }
}
