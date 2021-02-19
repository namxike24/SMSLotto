package com.smsanalytic.lotto.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringCalculateUtils {
    public static ArrayList<String> getListCangGiua(ArrayList<String> listNum) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (String s : listNum) {
                for (char c : s.toCharArray()) {
                    result.add(String.valueOf(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getList100So() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauBeDitLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i < 5; i++) {
                for (int j = 5; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauBeDitBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j <= 4; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongTren10() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (i + j > 10) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongDuoi10() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (i + j < 10) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListKhongChiaHet3() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (Integer.parseInt(i + String.valueOf(j)) % 3 != 0) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListChiaHet3() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (Integer.parseInt(i + String.valueOf(j)) % 3 == 0) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListChia3Du1() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (Integer.parseInt(i + String.valueOf(j)) % 3 == 1) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListChia3Du2() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (Integer.parseInt(i + String.valueOf(j)) % 3 == 2) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 5; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauLonDitLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 5; i <= 9; i++) {
                for (int j = 5; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauLonDitBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 5; i <= 9; i++) {
                for (int j = 0; j <= 4; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauChan() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 8; i += 2) {
                for (int j = 0; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDauLe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 1; i <= 9; i += 2) {
                for (int j = 0; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDitBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 4; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDitLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 5; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDitChan() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 8; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDitLe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 1; j <= 9; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListChanChan() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 8; i += 2) {
                for (int j = 0; j <= 8; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListLeLe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 1; i <= 9; i += 2) {
                for (int j = 1; j <= 9; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListChanLe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 8; i += 2) {
                for (int j = 1; j <= 9; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListLeChan() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 1; i <= 9; i += 2) {
                for (int j = 0; j <= 8; j += 2) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListBeBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListBeLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 4; i++) {
                for (int j = 5; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListLonLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 5; i <= 9; i++) {
                for (int j = 5; j <= 9; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListLonBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 5; i <= 9; i++) {
                for (int j = 0; j <= 4; j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongBe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (i + j < 5 || i + j > 9) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongLon() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if (i + j >= 5 && i + j <= 9) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongChan() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if ((i + j) % 2 == 0) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTongLe() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if ((i + j) % 2 == 1) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListKepBang() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                result.add(String.valueOf(i) + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListKepLech() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("05");
            result.add("50");
            result.add("16");
            result.add("61");
            result.add("27");
            result.add("72");
            result.add("38");
            result.add("83");
            result.add("49");
            result.add("94");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static ArrayList<String> getListChamLT(int num) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                result.add(String.valueOf(i) + num);
                if (num != i) {
                    result.add(num + String.valueOf(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListCham(String wordAftercham) {
        ArrayList<String> result = new ArrayList<>();
        try {
            char[] array = wordAftercham.toCharArray();
            for (char c : array) {
                int num = Integer.parseInt(String.valueOf(c));
                for (int i = 0; i <= 9; i++) {
                    String so = String.valueOf(i) + num;
                    if (!result.contains(so)) {
                        result.add(so);
                    }
                    if (num != i) {
                        String so1 = num + String.valueOf(i);
                        if (!result.contains(so1)) {
                            result.add(so1);
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTong(int num) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 9; i++) {
                for (int j = 0; j <= 9; j++) {
                    if ((i + j) % 10 == num) {
                        result.add(String.valueOf(i) + j);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListGhepTrong(String num) {
        ArrayList<String> result = new ArrayList<>();
        char[] arrChar = num.toCharArray();
        try {
            for (char c : arrChar) {
                for (char c1 : arrChar) {
                    result.add(String.valueOf(c) + c1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListDan(String num) {
        ArrayList<String> result = new ArrayList<>();
        char[] arrChar = num.toCharArray();
        try {
            for (int i = Integer.parseInt(String.valueOf(arrChar[0])); i <= Integer.parseInt(String.valueOf(arrChar[1])); i++) {
                for (int j = Integer.parseInt(String.valueOf(arrChar[0])); j <= Integer.parseInt(String.valueOf(arrChar[1])); j++) {
                    result.add(String.valueOf(i) + j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTuDen(String tu, String den) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = Integer.parseInt(tu); i <= Integer.parseInt(den); i++) {
                if (i <= 9) {
                    result.add("0" + i);
                } else {
                    result.add(String.valueOf(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListKepAm() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("07");
            result.add("70");
            result.add("14");
            result.add("41");
            result.add("29");
            result.add("92");
            result.add("36");
            result.add("63");
            result.add("58");
            result.add("85");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static ArrayList<String> getListSatKep() {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (int i = 0; i <= 8; i++) {
                result.add(String.valueOf(i) + (i + 1));
                result.add((i + 1) + String.valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static ArrayList<String> getListSatKepLech() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("48");
            result.add("84");
            result.add("26");
            result.add("62");
            result.add("15");
            result.add("51");
            result.add("37");
            result.add("73");
            result.add("28");
            result.add("82");
            result.add("06");
            result.add("60");
            result.add("39");
            result.add("93");
            result.add("17");
            result.add("71");
            result.add("04");
            result.add("95");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListVuongVuong() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("12");
            result.add("21");
            result.add("14");
            result.add("41");
            result.add("15");
            result.add("51");
            result.add("17");
            result.add("71");
            result.add("24");
            result.add("42");
            result.add("25");
            result.add("52");
            result.add("27");
            result.add("72");
            result.add("45");
            result.add("54");
            result.add("47");
            result.add("74");
            result.add("57");
            result.add("75");
            result.add("11");
            result.add("22");
            result.add("44");
            result.add("55");
            result.add("77");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListVuongTron() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("10");
            result.add("13");
            result.add("16");
            result.add("18");
            result.add("19");
            result.add("20");
            result.add("23");
            result.add("26");
            result.add("28");
            result.add("29");
            result.add("40");
            result.add("43");
            result.add("46");
            result.add("48");
            result.add("49");
            result.add("50");
            result.add("53");
            result.add("56");
            result.add("58");
            result.add("59");
            result.add("70");
            result.add("73");
            result.add("76");
            result.add("78");
            result.add("79");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTronTron() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("03");
            result.add("30");
            result.add("06");
            result.add("60");
            result.add("08");
            result.add("80");
            result.add("09");
            result.add("90");
            result.add("36");
            result.add("63");
            result.add("38");
            result.add("83");
            result.add("39");
            result.add("93");
            result.add("68");
            result.add("86");
            result.add("69");
            result.add("96");
            result.add("89");
            result.add("98");
            result.add("00");
            result.add("33");
            result.add("66");
            result.add("88");
            result.add("99");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListTronVuong() {
        ArrayList<String> result = new ArrayList<>();
        try {
            result.add("01");
            result.add("02");
            result.add("04");
            result.add("05");
            result.add("07");
            result.add("31");
            result.add("32");
            result.add("34");
            result.add("35");
            result.add("37");
            result.add("61");
            result.add("62");
            result.add("64");
            result.add("65");
            result.add("67");
            result.add("81");
            result.add("82");
            result.add("84");
            result.add("85");
            result.add("87");
            result.add("91");
            result.add("92");
            result.add("94");
            result.add("95");
            result.add("97");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> getListBo(String num) {
        ArrayList<String> result = new ArrayList<>();
        try {
            List<String> bo01 = Arrays.asList("01", "10", "06", "60", "51", "15", "56", "65");
            List<String> bo02 = Arrays.asList("02", "20", "07", "70", "52", "25", "57", "75");
            List<String> bo03 = Arrays.asList("03", "30", "08", "80", "53", "35", "58", "85");
            List<String> bo04 = Arrays.asList("04", "40", "09", "90", "54", "45", "59", "95");
            List<String> bo12 = Arrays.asList("12", "21", "17", "71", "62", "26", "67", "76");
            List<String> bo13 = Arrays.asList("13", "31", "18", "81", "63", "36", "68", "86");
            List<String> bo14 = Arrays.asList("14", "41", "19", "91", "64", "46", "69", "96");
            List<String> bo23 = Arrays.asList("23", "32", "28", "82", "73", "37", "78", "87");
            List<String> bo24 = Arrays.asList("24", "42", "29", "92", "74", "47", "79", "97");
            List<String> bo34 = Arrays.asList("34", "43", "39", "93", "84", "48", "89", "98");
            List<String> bo00 = Arrays.asList("00", "55", "05", "50");
            List<String> bo11 = Arrays.asList("11", "66", "16", "61");
            List<String> bo22 = Arrays.asList("22", "77", "27", "72");
            List<String> bo33 = Arrays.asList("33", "88", "38", "83");
            List<String> bo44 = Arrays.asList("44", "99", "49", "94");
            List<List<String>> listData = new ArrayList<>();
            listData.add(bo01);
            listData.add(bo02);
            listData.add(bo03);
            listData.add(bo04);
            listData.add(bo12);
            listData.add(bo13);
            listData.add(bo14);
            listData.add(bo23);
            listData.add(bo24);
            listData.add(bo34);
            listData.add(bo00);
            listData.add(bo11);
            listData.add(bo22);
            listData.add(bo33);
            listData.add(bo44);

            for (List<String> listDatum : listData) {
                if (listDatum.contains(num)) {
                    result.addAll(listDatum);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
