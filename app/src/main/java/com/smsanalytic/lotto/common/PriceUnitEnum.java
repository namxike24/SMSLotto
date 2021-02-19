package com.smsanalytic.lotto.common;

public class PriceUnitEnum {
    public static final int PRICE_N = 0;
    public static final String PRICE_N_STRING = "n"; //Nghìn
    public static final String PRICE_K_STRING = "k"; //Nghìn
    public static final String PRICE_Y_STRING = "y"; //Nghìn
    public static final String PRICE_W_STRING = "w"; //Nghìn
    public static final int PRICE_TRN = 1;
    public static final String PRICE_TRN_STRING = "trn"; //Trăm nghìn
    public static final int PRICE_TR = 2;
    public static final String PRICE_TR_STRING = "tr"; //Triệu
    public static final int PRICE_D = 3;
    public static final String PRICE_D_STRING = "d"; //Điểm

    public static int getPrice(String priceString) {
        switch (priceString) {
            case PRICE_N_STRING:
            case PRICE_K_STRING:
            case PRICE_Y_STRING:
            case PRICE_W_STRING:
                return PRICE_N;
            case PRICE_TRN_STRING:
                return PRICE_TRN;
            case PRICE_TR_STRING:
                return PRICE_TR;
            case PRICE_D_STRING:
                return PRICE_D;
            default:
                return -1;
        }
    }
}
