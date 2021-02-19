package com.smsanalytic.lotto.model.setting;

import com.smsanalytic.lotto.unit.PreferencesManager;

public class SettingXienType {
    public static final int MUOI_NGHIN = 0;
    public static final int MOT_NGHIN = 1;
    public static final int NGHIN = 0;
    public static final int DIEM = 1;

    public static int getDonViTinh() {
        return PreferencesManager.getInstance().getValueInt("DON_VI_TINH", MUOI_NGHIN);
    }

    public static void setDonViTinh(int type) {
        PreferencesManager.getInstance().setValueInt("DON_VI_TINH", type);
    }

    public static int getChuyenXienDi() {
        return PreferencesManager.getInstance().getValueInt("CHUYEN_XIEN_DI", NGHIN);
    }

    public static void setChuyenXienDi(int type) {
        PreferencesManager.getInstance().setValueInt("CHUYEN_XIEN_DI", type);
    }

}
