package com.smsanalytic.lotto.common;

public class TienTe {
    public  static final int TIEN_VIETNAM=1; // tiền việt
    public  static final int TIEN_JAPAN=2; // tiền nhật
    public  static final int TIEN_TAIWAN=3; // tiền đài
    public  static final int TIEN_KOREA=4; // tiền hàn



    public static String getKeyTienTe(int type){
        switch (type){
            case TIEN_JAPAN:
                return "¥";
            case TIEN_TAIWAN:
                return "k";
            case TIEN_KOREA:
                return "₩";
                default:
                    return "n";
        }

    }
    public static String getValueTienTe(int type){
        switch (type){
            case TIEN_JAPAN:
                return "Yên";
            case TIEN_TAIWAN:
                return "Khoai";
            case TIEN_KOREA:
                return "Won";
            default:
                return "Nghìn";
        }

    }
    public static String getValueTienTe3(int type){
        switch (type){
            case TIEN_JAPAN:
                return "Yên";
            case TIEN_TAIWAN:
                return "Khoai";
            case TIEN_KOREA:
                return "Won";
            default:
                return "Nghìn Đồng";
        }

    }
    public static String getValueTienTe2(int type){
        switch (type){
            case TIEN_JAPAN:
                return "Yên";
            case TIEN_TAIWAN:
                return "Khoai";
            case TIEN_KOREA:
                return "Won";
            default:
                return "Đồng";
        }

    }
    public static String getKeyTienTe2(int type){
        switch (type){
            case TIEN_JAPAN:
                return "¥";
            case TIEN_TAIWAN:
                return "k";
            case TIEN_KOREA:
                return "₩";
            default:
                return "đ";
        }

    }
}
