package com.smsanalytic.lotto.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeEnum {
    public static final int TYPE_DE = 0;
    public static final String TYPE_DE_STRING = "de";
    public static final int TYPE_LO = 1;
    public static final String TYPE_LO_STRING = "lo";
    public static final int TYPE_3C = 2;
    public static final String TYPE_3C_STRING = "bacang";
    public static final int TYPE_DITNHAT = 3;
    public static final String TYPE_DITNHAT_STRING = "ditnhat";
    public static final int TYPE_DAUDB = 4;
    public static final String TYPE_DAUDB_STRING = "daudb";
    public static final int TYPE_DAUNHAT = 5;
    public static final String TYPE_DAUNHAT_STRING = "daunhat";
    public static final int TYPE_CANGGIUA = 6;
    public static final String TYPE_CANGGIUA_STRING = "canggiua";
    public static final int TYPE_XIENGHEP2 = 7;
    public static final String TYPE_XIENGHEP2_STRING = "xienghephai";
    public static final int TYPE_XIENGHEP3 = 8;
    public static final String TYPE_XIENGHEP3_STRING = "xienghepba";
    public static final int TYPE_XIENGHEP4 = 9;
    public static final String TYPE_XIENGHEP4_STRING = "xienghepbon";
    public static final int TYPE_XIENQUAY = 10;
    public static final String TYPE_XIENQUAY_STRING = "xienquay";
    public static final int TYPE_XIEN2 = 11;
    public static final String TYPE_XIEN2_STRING = "xienhai";
    public static final int TYPE_XIEN3 = 12;
    public static final String TYPE_XIEN3_STRING = "xienba";
    public static final int TYPE_XIEN4 = 13;
    public static final String TYPE_XIEN4_STRING = "xienbon";

    public static String getStringByType(int type) {
        switch (type) {
            case TYPE_LO:
                return TYPE_LO_STRING;
            case TYPE_3C:
                return TYPE_3C_STRING;
            case TYPE_DITNHAT:
                return TYPE_DITNHAT_STRING;
            case TYPE_DAUDB:
                return TYPE_DAUDB_STRING;
            case TYPE_DAUNHAT:
                return TYPE_DAUNHAT_STRING;
            case TYPE_CANGGIUA:
                return TYPE_CANGGIUA_STRING;
            case TYPE_XIENGHEP2:
                return TYPE_XIENGHEP2_STRING;
            case TYPE_XIENGHEP3:
                return TYPE_XIENGHEP3_STRING;
            case TYPE_XIENGHEP4:
                return TYPE_XIENGHEP4_STRING;
            case TYPE_XIENQUAY:
                return TYPE_XIENQUAY_STRING;
            case TYPE_XIEN2:
                return TYPE_XIEN2_STRING;
            case TYPE_XIEN3:
                return TYPE_XIEN3_STRING;
            case TYPE_XIEN4:
                return TYPE_XIEN4_STRING;
            default:
                return TYPE_DE_STRING;
        }
    }

    public static String getStringByTypeFull(int type) {
        switch (type) {
            case TYPE_LO:
                return "Lô";
            case TYPE_3C:
                return "Ba càng";
            case TYPE_DITNHAT:
                return "Đuôi G1";
            case TYPE_DAUDB:
                return "Đầu ĐB";
            case TYPE_DAUNHAT:
                return "Đầu G1";
            case TYPE_CANGGIUA:
                return "Càng giữa";
            case TYPE_XIENGHEP2:
            case TYPE_XIENGHEP3:
            case TYPE_XIENGHEP4:
                return "Xiên ghép";
            case TYPE_XIENQUAY:
                return "Xiên quay";
            case TYPE_XIEN2:
            case TYPE_XIEN3:
            case TYPE_XIEN4:
                return "Lô xiên";
            default:
                return "Đề";
        }
    }

    public static String getStringByType3(int type) {
        switch (type) {
            case TYPE_LO:
                return "Lo";
            case TYPE_3C:
                return "Ba càng";
            case TYPE_DITNHAT:
                return "Đuôi G1";
            case TYPE_DAUDB:
                return "Đầu ĐB";
            case TYPE_DAUNHAT:
                return "Đầu G1";
            case TYPE_CANGGIUA:
                return "Càng giữa";
            case TYPE_XIENGHEP2:
            case TYPE_XIENGHEP3:
            case TYPE_XIENGHEP4:
                return "Xiên ghép";
            case TYPE_XIENQUAY:
                return "Xiên quay";
            case TYPE_XIEN2:
                return "Xien2";
            case TYPE_XIEN3:
                return "Xien3";
            case TYPE_XIEN4:
                return "Xien4";
            default:
                return "De";
        }
    }


    public static String getStringByType2(int type) {
        switch (type) {
            case TYPE_LO:
                return "Lô";
            case TYPE_3C:
                return "Ba càng";
            case TYPE_DITNHAT:
                return "Đuôi G1";
            case TYPE_DAUDB:
                return "Đầu ĐB";
            case TYPE_DAUNHAT:
                return "Đầu G1";
            case TYPE_CANGGIUA:
                return "Càng giữa";
            case TYPE_XIENGHEP2:
            case TYPE_XIENGHEP3:
            case TYPE_XIENGHEP4:
            case TYPE_XIENQUAY:
            case TYPE_XIEN2:
            case TYPE_XIEN3:
            case TYPE_XIEN4:
                return "Xiên";
            case TYPE_DE:
                return "Đề";
            default:
                return "Đề";
        }
    }


    public static boolean isTypeLoXienG1(int type) {
        List<Integer> arrayIntegers = new ArrayList<>(Arrays.asList(TYPE_LO, TYPE_DITNHAT, TYPE_DAUNHAT, TYPE_XIENQUAY, TYPE_XIENGHEP2, TYPE_XIENGHEP3, TYPE_XIENGHEP4, TYPE_XIEN2, TYPE_XIEN3, TYPE_XIEN4));
        if (arrayIntegers.contains(type)) {
            return true;
        }
        return false;
    }


    public static boolean isTypeLoXien(int type) {
        List<Integer> arrayIntegers = new ArrayList<>(Arrays.asList( TYPE_XIENQUAY, TYPE_XIENGHEP2, TYPE_XIENGHEP3, TYPE_XIENGHEP4, TYPE_XIEN2, TYPE_XIEN3, TYPE_XIEN4));
        if (arrayIntegers.contains(type)) {
            return true;
        }
        return false;
    }
    public static boolean isTypeDeCang(int type) {
        List<Integer> arrayIntegers = new ArrayList<>(Arrays.asList(TYPE_DAUDB, TYPE_DE, TYPE_CANGGIUA, TYPE_3C));
        if (arrayIntegers.contains(type)) {
            return true;
        }
        return false;
    }
}
