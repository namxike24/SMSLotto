package com.smsanalytic.lotto.common.calculate;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.PriceUnitEnum;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.common.StringCalculateUtils.getList100So;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListBeBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListBeLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListBo;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListCham;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChamLT;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChanChan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChanLe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChia3Du1;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChia3Du2;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListChiaHet3;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauBeDitBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauBeDitLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauChan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauLe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauLonDitBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDauLonDitLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDitBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDitChan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDitLe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListDitLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListGhepTrong;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListKepAm;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListKepBang;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListKepLech;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListKhongChiaHet3;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListLeChan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListLeLe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListLonBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListLonLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListSatKep;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListSatKepLech;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTong;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongBe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongChan;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongDuoi10;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongLe;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongLon;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTongTren10;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTronTron;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTronVuong;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListTuDen;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListVuongTron;
import static com.smsanalytic.lotto.common.StringCalculateUtils.getListVuongVuong;
import static com.smsanalytic.lotto.common.calculate.StringCalculate.*;

public class ProcessStartWithDe {

    public static void processStartWithTypeDE(StringProcessEntity processEntity, StringProcessChildEntity childEntity, List<String> listWord, AccountObject customer, StringProcessChildEntity previousEntity, int position) {
        try {
            SettingDefault settingDefault = null;
            boolean isPriceUnitError = false;
            try {
                String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
                if (!dateCache.isEmpty()) {
                    settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
                } else {
                    String dateDefault = Common.loadJSONFromAsset(MyApp.getInstance(), "SettingDefault.json");
                    settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int type = TypeEnum.TYPE_DE;
            childEntity.setType(type);
            boolean isRemoveDuplicate = false;
            ArrayList<String> listNotAdd = new ArrayList<>();
            ArrayList<String> listNum = new ArrayList<>();
            double priceValue = 0;
            boolean isError = false;
            AccountSetting accountSetting = null;
            if (customer != null) {
                accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
            }
            if (listWord.contains("x")) { //Nếu chứa x
                if (listWord.lastIndexOf("x") == listWord.size() - 1) { //Nếu x ở cuối cùng
                    setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                    isError = true;
                } else if (listWord.lastIndexOf("x") < listWord.size() - 2) { //Nếu sau x có nhiều từ
                    setError(processEntity, childEntity, "Không đúng định dạng");
                    isError = true;
                } else {//Nếu x nằm trước giá trị tiền
                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
                    if (patternPrice.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
                        priceValue = Double.parseDouble(price.replaceAll("\\D", ""));
                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
                        if (priceUnit == -1) {
                            priceUnit = PriceUnitEnum.PRICE_N;
                        } else {
                            String p = price.replaceAll("\\d", "");
                            if (settingDefault != null) {
                                switch (settingDefault.getTiente()) {
                                    case TienTe.TIEN_VIETNAM:
                                        if (!p.equalsIgnoreCase("d") && !p.equalsIgnoreCase("n")
                                                && !p.equalsIgnoreCase("trn") && !p.equalsIgnoreCase("tr")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_TAIWAN:
                                        if (!p.equalsIgnoreCase("k") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_JAPAN:
                                        if (!p.equalsIgnoreCase("y") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_KOREA:
                                        if (!p.equalsIgnoreCase("w") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                }
                            }
                        }
                        priceValue = processPriceValue(priceValue, priceUnit, customer);
                        if (priceUnit == PriceUnitEnum.PRICE_D && (accountSetting != null && accountSetting.getDonvitinhde() == 1)) {
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else if (isPriceUnitError) {
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else {
                            if (priceValue < 0) { //Nếu giá trị tiền = 0
                                setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                                isError = true;
                            } else { //Nếu giá trị tiền lớn hơn 0
                                if (listWord.size() < 4) { //Nếu số từ trong câu nhỏ hơn 4
                                    setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
                                    isError = true;
                                } else {
                                    for (int i = 1; i < listWord.size() - 2; i++) { //Lấy các cặp số
                                        String word = listWord.get(i);
                                        if (patternNumber.matcher(word).matches()) { //Nếu từ là số
                                            if (word.length() == 1 || word.length() > 4) {
                                                setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                isError = true;
                                                break;
                                            } else if (word.length() == 3) {
                                                if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
                                                    listNum.add(word.substring(0, 2));
                                                    listNum.add(word.substring(1));
                                                } else { //Nếu không phải số đối xứng
                                                    if (word.equalsIgnoreCase("100")) {
                                                        if (listWord.get(i + 1).equalsIgnoreCase("so")) {//Xử lý với trường hợp 100 số
                                                            listNum.addAll(getList100So());
                                                            i = i + 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                            isError = true;
                                                            break;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                        isError = true;
                                                        break;
                                                    }
                                                }
                                            } else if (word.length() == 4) {
                                                listNum.add(word.substring(0, 2));
                                                listNum.add(word.substring(2));
                                            } else {
                                                listNum.add(word);
//                                                processAddNumberObject(childEntity, type, priceValue, word);
                                            }
                                        } else { //Nếu từ không phải là số
                                            switch (word) {
                                                case "boj":
                                                    ArrayList<String> listNumBo = new ArrayList<>();
                                                    String wordBo1 = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordBo1).matches()) { //Nếu từ là số
                                                        if (wordBo1.length() == 1) {
                                                            setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                            isError = true;
                                                        } else if (wordBo1.length() > 2) {
                                                            setError(processEntity, childEntity, "Không hiểu \"bộ " + wordBo1 + "\"");
                                                            isError = true;
                                                        } else {
                                                            listNumBo.add(wordBo1);
                                                            if (i + 2 >= listWord.size() - 2) {
                                                                i += 1;
                                                            } else {
                                                                for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                    i = j;
                                                                    String wordAfter2 = listWord.get(j);
                                                                    if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                        if (wordAfter2.length() == 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                            setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                            isError = true;
                                                                        } else if (wordAfter2.length() > 2) {
                                                                            i--;
                                                                            break;
                                                                        } else {
                                                                            listNumBo.add(wordAfter2);
                                                                        }
                                                                    } else { // nếu là chữ
                                                                        i--;
                                                                        break;
                                                                    }

                                                                }
                                                            }
                                                        }
                                                        if (!isError) {
                                                            for (String s : listNumBo) {
                                                                listNum.addAll(getListBo(s));
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu \"bộ\"\n Không hiểu" + wordBo1);
                                                        isError = true;
                                                    }
                                                    break;
                                                case "daudit":
                                                    String wordAfter1 = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfter1).matches()) { //Nếu từ là số
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAfter1 = wordAfter1 + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAfter1)) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            char[] array = wordAfter1.toCharArray();

                                                            for (char c : array) {
                                                                for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                    String num = c + String.valueOf(dit);
                                                                    listNum.add(num);
                                                                }
                                                                for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                    String num = String.valueOf(dau) + c;
                                                                    listNum.add(num);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                        isError = true;
                                                    }
                                                    break;

                                                case "dau":
                                                    String wordGhepDit = "";
                                                    String wordAfterDau = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterDau).matches()) { //Nếu từ là số
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau đầu không thể là Đề nữa\n-đầu1 2 3 hoặc đầu1.2.3 hoặc đầu123 được hiểu là đầu1 đầu2 đầu3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAfterDau = wordAfterDau + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAfterDau)) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    if (wordAfter2.equalsIgnoreCase("ghepdit")) { //Nếu là ghepdit
                                                                        for (int k = j + 1; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            i = k;
                                                                            wordGhepDit = listWord.get(k);
                                                                            if (patternNumber.matcher(wordGhepDit).matches()) { //Nếu là số
                                                                                if (k + 1 >= listWord.size() - 2) {
                                                                                    k += 1;
                                                                                } else {
                                                                                    for (int l = k + 1; l < listWord.size() - 2; l++) {
                                                                                        k = l;
                                                                                        i = k;
                                                                                        String wordAfterDit = listWord.get(l);
                                                                                        if (patternNumber.matcher(wordAfterDit).matches()) { //Nếu là số
                                                                                            if (wordAfterDit.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                                isError = true;
                                                                                                setError(processEntity, childEntity, "Sau đít không thể là Đề nữa");
                                                                                                break;
                                                                                            } else { //Nếu là số có 1 chữ số
                                                                                                wordGhepDit = wordGhepDit + wordAfterDit;
                                                                                                if (!checkValidateRepeatChar(wordGhepDit)) {
                                                                                                    isError = true;
                                                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                        } else {
                                                                                            i--;
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                    break;
                                                                                }
                                                                            } else {
                                                                                setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                                isError = true;
                                                                                break;
                                                                            }
                                                                        }
                                                                        break;
                                                                    } else {
                                                                        i--;
                                                                        break;
                                                                    }
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            if (!wordGhepDit.equalsIgnoreCase("")) {
                                                                char[] arrayDau = wordAfterDau.toCharArray();
                                                                char[] arrayDit = wordGhepDit.toCharArray();
                                                                for (char c : arrayDau) {
                                                                    for (char c1 : arrayDit) {
                                                                        String num = c + String.valueOf(c1);
                                                                        listNum.add(num);
                                                                    }
                                                                }
                                                            } else {
                                                                char[] array = wordAfterDau.toCharArray();
                                                                for (char c : array) {
                                                                    for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                        String num = c + String.valueOf(dit);
                                                                        listNum.add(num);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "dit":
                                                    String wordAfterDit = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterDit).matches()) { //Nếu từ là số
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAfterDit = wordAfterDit + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAfterDit)) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            char[] array = wordAfterDit.toCharArray();
                                                            for (char c : array) {
                                                                for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                    String num = String.valueOf(dau) + c;
                                                                    listNum.add(num);


                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "tong":
                                                    String wordAfterTong = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterTong).matches()) { //Nếu từ là số
                                                        if (!checkValidateRepeatChar(wordAfterTong)) {
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                            break;
                                                        }
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau tổng không thể là Đề nữa\n-tổng1 2 3 hoặc tổng1.2.3 hoặc tổng123 được hiểu là tổng1 tổng2 tổng3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAfterTong = wordAfterTong + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAfterTong)) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            char[] array = wordAfterTong.toCharArray();
                                                            for (char c : array) {
                                                                listNum.addAll(getListTong(Integer.parseInt(String.valueOf(c))));
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Tổng phải là số");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "chamlt":
                                                    String wordAfterchamlt = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterchamlt).matches()) { //Nếu từ là số
                                                        if (!checkValidateRepeatChar(wordAfterchamlt) || wordAfterchamlt.length() > 5) {
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                            break;
                                                        }
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAfterchamlt = wordAfterchamlt + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAfterchamlt) || wordAfterchamlt.length() > 5) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            char[] array = wordAfterchamlt.toCharArray();
                                                            for (char c : array) {
                                                                listNum.addAll(getListChamLT(Integer.parseInt(String.valueOf(c))));
                                                            }
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordAfterchamlt);
                                                        isError = true;
                                                    }
                                                    break;
                                                case "cham":
                                                    String wordAftercham = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAftercham).matches()) { //Nếu từ là số
                                                        if (!checkValidateRepeatChar(wordAftercham) || wordAftercham.length() > 5) {
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                            break;
                                                        }
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                        break;
                                                                    } else { //Nếu là số có 1 chữ số
                                                                        wordAftercham = wordAftercham + wordAfter2;
                                                                        if (!checkValidateRepeatChar(wordAftercham) || wordAftercham.length() > 5) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                            break;
                                                                        }
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                        if (!isError) {
                                                            listNum.addAll(getListCham(wordAftercham));
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordAftercham);
                                                        isError = true;
                                                    }
                                                    break;
                                                case "gheptrong":
                                                    String wordAfterGhepTrong = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterGhepTrong).matches()) { //Nếu từ là số
                                                        if (!checkValidateRepeatChar(wordAfterGhepTrong)) {
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Các số không được trùng nhau. Ví dụ: Đề ghép trong 2345 x 10n");
                                                            break;
                                                        } else {
                                                            i += 1;
                                                        }

                                                        if (!isError) {
                                                            listNum.addAll(getListGhepTrong(wordAfterGhepTrong));
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu gheptrong \n Không hiểu " + wordAfterGhepTrong);
                                                        isError = true;
                                                    }
                                                    break;
                                                case "dan":
                                                    String wordAfterDan = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterDan).matches()) { //Nếu từ là số
                                                        if (wordAfterDan.length() != 2) { //Nếu số khác 2 chữ số
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                        } else {
                                                            char[] arr = wordAfterDan.toCharArray();
                                                            if (Integer.parseInt(String.valueOf(arr[0])) > Integer.parseInt(String.valueOf(arr[1]))) {
                                                                isError = true;
                                                                setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                            } else {
                                                                if (i + 2 < listWord.size() - 2) {
                                                                    String wordAfterDan2 = listWord.get(i + 2);
                                                                    if (patternNumber.matcher(wordAfterDan2).matches()) {
                                                                        setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                        isError = true;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        if (!isError) {
                                                            listNum.addAll(getListDan(wordAfterDan));
                                                            i += 1;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "tu":
                                                    String wordAfterTu = listWord.get(i + 1);
                                                    String wordAfterDen = "";
                                                    if (patternNumber.matcher(wordAfterTu).matches()) { //Nếu từ là số
                                                        if (i + 2 >= listWord.size() - 2) {
                                                            setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                            isError = true;
                                                        } else {
                                                            String wordDen = listWord.get(i + 2);
                                                            if (wordDen.equalsIgnoreCase("den")) {
                                                                if (i + 3 >= listWord.size() - 2) {
                                                                    setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99");
                                                                    isError = true;
                                                                } else {
                                                                    wordAfterDen = listWord.get(i + 3);
                                                                    if (patternNumber.matcher(wordAfterDen).matches()) { //Nếu từ là số
                                                                        if (wordAfterTu.length() != 2 || wordAfterDen.length() != 2
                                                                                || Integer.parseInt(wordAfterTu) > Integer.parseInt(wordAfterDen)) {
                                                                            setError(processEntity, childEntity, "Sai định dạng tuABdenCD với điều kiện AB phải nhỏ hơn CD. Ví dụ: tu 00 den 99");
                                                                            isError = true;
                                                                        }
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99\n Không hiểu \"" + wordAfterDen + "\"");
                                                                        isError = true;
                                                                    }
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                                isError = true;
                                                            }
                                                        }

                                                        if (!isError) {
                                                            listNum.addAll(getListTuDen(wordAfterTu, wordAfterDen));
                                                            i += 3;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không đúng định dạng");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "tongtren":
                                                    String wordAfterTongTren = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterTongTren).matches()) {
                                                        if (wordAfterTongTren.equalsIgnoreCase("10")) {
                                                            listNum.addAll(getListTongTren10());
                                                            i += 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordAfterTongTren + "\"");
                                                            isError = true;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordAfterTongTren + "\"");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "tongduoi":
                                                    String wordAfterTongDuoi = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterTongDuoi).matches()) {
                                                        if (wordAfterTongDuoi.equalsIgnoreCase("10")) {
                                                            listNum.addAll(getListTongDuoi10());
                                                            i += 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordAfterTongDuoi + "\"");
                                                            isError = true;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordAfterTongDuoi + "\"");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "khongchiahetcho":
                                                    String wordAfterKhongChiaHet = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterKhongChiaHet).matches()) {
                                                        if (wordAfterKhongChiaHet.equalsIgnoreCase("3")) {
                                                            listNum.addAll(getListKhongChiaHet3());
                                                            i += 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                            isError = true;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "chiahetcho":
                                                    String wordAfterChiaHet = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterChiaHet).matches()) {
                                                        if (wordAfterChiaHet.equalsIgnoreCase("3")) {
                                                            listNum.addAll(getListChiaHet3());
                                                            i += 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                            isError = true;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "chiabadu":
                                                    String wordAfterChiaDu = listWord.get(i + 1);
                                                    if (patternNumber.matcher(wordAfterChiaDu).matches()) {
                                                        if (wordAfterChiaDu.equalsIgnoreCase("1")) {
                                                            listNum.addAll(getListChia3Du1());
                                                            i += 1;
                                                        } else if (wordAfterChiaDu.equalsIgnoreCase("2")) {
                                                            listNum.addAll(getListChia3Du2());
                                                            i += 1;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                            isError = true;
                                                        }
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                        isError = true;
                                                    }
                                                    break;
                                                case "daube":
                                                    if ((i + 1 < listWord.size() - 2 && listWord.get(i + 1).equalsIgnoreCase("cua"))) {
                                                        if (i + 2 < listWord.size() - 2) {
                                                            if (listWord.get(i + 2).equalsIgnoreCase("ditlon")) {
                                                                listNum.addAll(getListDauBeDitLon());
                                                                i += 2;
                                                            } else if (listWord.get(i + 2).equalsIgnoreCase("ditbe")) {
                                                                listNum.addAll(getListDauBeDitBe());
                                                                i += 2;
                                                            }
                                                        }
                                                    } else {
                                                        listNum.addAll(getListDauBe());
                                                    }
                                                    break;
                                                case "daulon":
                                                    if ((i + 1 < listWord.size() - 2 && listWord.get(i + 1).equalsIgnoreCase("cua"))) {
                                                        if (i + 2 < listWord.size() - 2) {
                                                            if (listWord.get(i + 2).equalsIgnoreCase("ditlon")) {
                                                                listNum.addAll(getListDauLonDitLon());
                                                                i += 2;
                                                            } else if (listWord.get(i + 2).equalsIgnoreCase("ditbe")) {
                                                                listNum.addAll(getListDauLonDitBe());
                                                                i += 2;
                                                            }
                                                        }
                                                    } else {
                                                        listNum.addAll(getListDauLon());
                                                    }
                                                    break;
                                                case "dauchan":
                                                    listNum.addAll(getListDauChan());
                                                    break;
                                                case "daule":
                                                    listNum.addAll(getListDauLe());
                                                    break;
                                                case "ditbe":
                                                    listNum.addAll(getListDitBe());
                                                    break;
                                                case "ditlon":
                                                    listNum.addAll(getListDitLon());
                                                    break;
                                                case "ditchan":
                                                    listNum.addAll(getListDitChan());
                                                    break;
                                                case "ditle":
                                                    listNum.addAll(getListDitLe());
                                                    break;
                                                case "chanchan":
                                                    listNum.addAll(getListChanChan());
                                                    break;
                                                case "lele":
                                                    listNum.addAll(getListLeLe());
                                                    break;
                                                case "chanle":
                                                    listNum.addAll(getListChanLe());
                                                    break;
                                                case "lechan":
                                                    listNum.addAll(getListLeChan());
                                                    break;
                                                case "bebe":
                                                    listNum.addAll(getListBeBe());
                                                    break;
                                                case "belon":
                                                    listNum.addAll(getListBeLon());
                                                    break;
                                                case "lonlon":
                                                    listNum.addAll(getListLonLon());
                                                    break;
                                                case "lonbe":
                                                    listNum.addAll(getListLonBe());
                                                    break;
                                                case "tonglon":
                                                    listNum.addAll(getListTongLon());
                                                    break;
                                                case "tongbe":
                                                    listNum.addAll(getListTongBe());
                                                    break;
                                                case "tongchan":
                                                    listNum.addAll(getListTongChan());
                                                    break;
                                                case "tongle":
                                                    listNum.addAll(getListTongLe());
                                                    break;
                                                case "kepbang":
                                                    listNum.addAll(getListKepBang());
                                                    break;
                                                case "keplech":
                                                    listNum.addAll(getListKepLech());
                                                    break;
                                                case "kepam":
                                                    listNum.addAll(getListKepAm());
                                                    break;
                                                case "satkep":
                                                    listNum.addAll(getListSatKep());
                                                    break;
                                                case "satkeplech":
                                                    listNum.addAll(getListSatKepLech());
                                                    break;
                                                case "vuongvuong":
                                                    listNum.addAll(getListVuongVuong());
                                                    break;
                                                case "vuongtron":
                                                    listNum.addAll(getListVuongTron());
                                                    break;
                                                case "trontron":
                                                    listNum.addAll(getListTronTron());
                                                    break;
                                                case "tronvuong":
                                                    listNum.addAll(getListTronVuong());
                                                    break;
                                                case "bor":
                                                    for (int remove = i + 1; remove < listWord.size() - 2; remove++) {
                                                        String removeNum = listWord.get(remove);
                                                        if (patternNumber.matcher(removeNum).matches()) {
                                                            if (removeNum.length() == 1 || removeNum.length() > 4) {
                                                                setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                                isError = true;
                                                            } else if (removeNum.length() == 3) {
                                                                if (Pattern.compile("(\\d)\\d*\\1").matcher(removeNum).matches()) { //Nếu là số đối xứng
                                                                    listNotAdd.add(removeNum.substring(0, 2));
                                                                    listNotAdd.add(removeNum.substring(1));
                                                                } else { //Nếu không phải số đối xứng
                                                                    setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                                    isError = true;
                                                                }
                                                            } else {
                                                                listNotAdd.add(removeNum);
                                                            }
                                                        } else {
                                                            if (removeNum.equalsIgnoreCase("boj")) {
                                                                ArrayList<String> listRemoveBo = new ArrayList<>();
                                                                String wordBo = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordBo).matches()) { //Nếu từ là số
                                                                    if (wordBo.length() == 1) {
                                                                        setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                        isError = true;
                                                                    } else if (wordBo.length() > 2) {
                                                                        setError(processEntity, childEntity, "Không hiểu \"bộ " + wordBo + "\"");
                                                                        isError = true;
                                                                    } else {
                                                                        listRemoveBo.add(wordBo);
                                                                        if (remove + 2 >= listWord.size() - 2) {
                                                                            remove += 1;
                                                                        } else {
                                                                            for (int j = remove + 2; j < listWord.size() - 2; j++) { //duyệt các từ sau
                                                                                remove = j;
                                                                                String wordAfter2 = listWord.get(j);
                                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                                    if (wordAfter2.length() == 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                        setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                                        isError = true;
                                                                                    } else if (wordAfter2.length() > 2) {
                                                                                        remove--;
                                                                                        break;
                                                                                    } else {
                                                                                        listRemoveBo.add(wordAfter2);
                                                                                    }
                                                                                } else { // nếu là chữ
                                                                                    remove--;
                                                                                    break;
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        for (String s : listRemoveBo) {
                                                                            listNotAdd.addAll(getListBo(s));
                                                                        }
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"bộ\"\n Không hiểu" + wordBo);
                                                                    isError = true;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("daudit")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        char[] array = wordNum.toCharArray();
                                                                        for (char c : array) {
                                                                            for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                                String num = c + String.valueOf(dit);
                                                                                if (!listNotAdd.contains(num)) {
                                                                                    listNotAdd.add(num);
                                                                                }
                                                                            }
                                                                            for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                                String num = String.valueOf(dau) + c;
                                                                                if (!listNotAdd.contains(num)) {
                                                                                    listNotAdd.add(num);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("dit")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        char[] array = wordNum.toCharArray();
                                                                        for (char c : array) {
                                                                            for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                                String num = String.valueOf(dau) + c;
                                                                                if (!listNotAdd.contains(num)) {
                                                                                    listNotAdd.add(num);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("dau")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau đầu không thể là Đề nữa\n-đầu1 2 3 hoặc đầu1.2.3 hoặc đầu123 được hiểu là đầu1 đầu2 đầu3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        char[] array = wordNum.toCharArray();
                                                                        for (char c : array) {
                                                                            for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                                String num = c + String.valueOf(dit);
                                                                                if (!listNotAdd.contains(num)) {
                                                                                    listNotAdd.add(num);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("tong")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                        break;
                                                                    }
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau tổng không thể là Đề nữa\n-tổng1 2 3 hoặc tổng1.2.3 hoặc tổng123 được hiểu là tổng1 tổng2 tổng3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        char[] array = wordNum.toCharArray();
                                                                        for (char c : array) {
                                                                            listNotAdd.addAll(getListTong(Integer.parseInt(String.valueOf(c))));
                                                                        }
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Tổng phải là số");
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("chamlt")) {

                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                        break;
                                                                    }
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        char[] array = wordNum.toCharArray();
                                                                        for (char c : array) {
                                                                            listNotAdd.addAll(getListChamLT(Integer.parseInt(String.valueOf(c))));
                                                                        }
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordNum);
                                                                    isError = true;
                                                                    break;
                                                                }


                                                            } else if (removeNum.equalsIgnoreCase("cham")) {

                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                        break;
                                                                    }
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int k = remove + 2; k < listWord.size() - 2; k++) { //duyệt các từ sau
                                                                            String wordNum2 = listWord.get(k);
                                                                            if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                                remove = k;
                                                                                if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                                    break;
                                                                                } else { //Nếu là số có 1 chữ số
                                                                                    wordNum = wordNum + wordNum2;
                                                                                    if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                                        isError = true;
                                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                remove += 1;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (!isError) {
                                                                        listNotAdd.addAll(getListCham(wordNum));
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordNum);
                                                                    isError = true;
                                                                    break;
                                                                }


                                                            } else if (removeNum.equalsIgnoreCase("gheptrong")) {

                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (!checkValidateRepeatChar(wordNum)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số không được trùng nhau. Ví dụ: Đề ghép trong 2345 x 10n");
                                                                        break;
                                                                    } else {
                                                                        remove += 1;
                                                                    }

                                                                    if (!isError) {
                                                                        listNotAdd.addAll(getListGhepTrong(wordNum));
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"gheptrong\" \n Không hiểu " + wordNum);
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("dan")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (wordNum.length() != 2) { //Nếu số khác 2 chữ số
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                                    } else {
                                                                        char[] arr = wordNum.toCharArray();
                                                                        if (Integer.parseInt(String.valueOf(arr[0])) > Integer.parseInt(String.valueOf(arr[1]))) {
                                                                            isError = true;
                                                                            setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                                        } else {
                                                                            if (remove + 2 < listWord.size() - 2) {
                                                                                String wordAfterDan2 = listWord.get(remove + 2);
                                                                                if (patternNumber.matcher(wordAfterDan2).matches()) {
                                                                                    setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                                    isError = true;
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    if (!isError) {
                                                                        listNotAdd.addAll(getListDan(wordNum));
                                                                        remove += 1;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                    isError = true;
                                                                }

                                                            } else if (removeNum.equalsIgnoreCase("tu")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                String wordNumDen = "";
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (remove + 2 >= listWord.size() - 2) {
                                                                        setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                                        isError = true;
                                                                    } else {
                                                                        String wordDen = listWord.get(remove + 2);
                                                                        if (wordDen.equalsIgnoreCase("den")) {
                                                                            if (remove + 3 >= listWord.size() - 2) {
                                                                                setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99");
                                                                                isError = true;
                                                                            } else {
                                                                                wordNumDen = listWord.get(remove + 3);
                                                                                if (patternNumber.matcher(wordNumDen).matches()) { //Nếu từ là số
                                                                                    if (wordNum.length() != 2 || wordNumDen.length() != 2
                                                                                            || Integer.parseInt(wordNum) > Integer.parseInt(wordNumDen)) {
                                                                                        setError(processEntity, childEntity, "Sai định dạng tuABdenCD với điều kiện AB phải nhỏ hơn CD. Ví dụ: tu 00 den 99");
                                                                                        isError = true;
                                                                                    }
                                                                                } else {
                                                                                    setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99\n Không hiểu \"" + wordNumDen + "\"");
                                                                                    isError = true;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                                            isError = true;
                                                                        }
                                                                    }

                                                                    if (!isError) {
                                                                        listNotAdd.addAll(getListTuDen(wordNum, wordNumDen));
                                                                        remove += 3;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không đúng định dạng");
                                                                    isError = true;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("tongtren")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (wordNum.equalsIgnoreCase("10")) {
                                                                        listNotAdd.addAll(getListTongTren10());
                                                                        remove += 1;
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordNum + "\"");
                                                                        isError = true;
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"tongtren\" \n Không hiểu " + wordNum);
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("tongduoi")) {


                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                    if (wordNum.equalsIgnoreCase("10")) {
                                                                        listNotAdd.addAll(getListTongDuoi10());
                                                                        remove += 1;
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordNum + "\"");
                                                                        isError = true;
                                                                    }

                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu \"tongduoi\" \n Không hiểu " + wordNum);
                                                                    isError = true;
                                                                    break;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("khongchiahetcho")) {
                                                                String workNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(workNum).matches()) {
                                                                    if (workNum.equalsIgnoreCase("3")) {
                                                                        listNotAdd.addAll(getListKhongChiaHet3());
                                                                        remove += 1;
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                                        isError = true;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                                    isError = true;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("chiahetcho")) {
                                                                String workNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(workNum).matches()) {
                                                                    if (workNum.equalsIgnoreCase("3")) {
                                                                        listNotAdd.addAll(getListChiaHet3());
                                                                        remove += 1;
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                                        isError = true;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                                    isError = true;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("chiabadu")) {
                                                                String wordNum = listWord.get(remove + 1);
                                                                if (patternNumber.matcher(wordNum).matches()) {
                                                                    if (wordNum.equalsIgnoreCase("1")) {
                                                                        listNotAdd.addAll(getListChia3Du1());
                                                                        remove += 1;
                                                                    } else if (wordNum.equalsIgnoreCase("2")) {
                                                                        listNotAdd.addAll(getListChia3Du2());
                                                                        remove += 1;
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                                        isError = true;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                                    isError = true;
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("daube")) {
                                                                if ((remove + 1 < listWord.size() - 2 && listWord.get(remove + 1).equalsIgnoreCase("cua"))) {
                                                                    if (remove + 2 < listWord.size() - 2) {
                                                                        if (listWord.get(remove + 2).equalsIgnoreCase("ditlon")) {
                                                                            listNotAdd.addAll(getListDauBeDitLon());
                                                                            remove += 2;
                                                                        } else if (listWord.get(remove + 2).equalsIgnoreCase("ditbe")) {
                                                                            listNotAdd.addAll(getListDauBeDitBe());
                                                                            remove += 2;
                                                                        }
                                                                    }
                                                                } else {
                                                                    listNotAdd.addAll(getListDauBe());
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("daulon")) {
                                                                if ((remove + 1 < listWord.size() - 2 && listWord.get(remove + 1).equalsIgnoreCase("cua"))) {
                                                                    if (remove + 2 < listWord.size() - 2) {
                                                                        if (listWord.get(remove + 2).equalsIgnoreCase("ditlon")) {
                                                                            listNotAdd.addAll(getListDauLonDitLon());
                                                                            remove += 2;
                                                                        } else if (listWord.get(remove + 2).equalsIgnoreCase("ditbe")) {
                                                                            listNotAdd.addAll(getListDauLonDitBe());
                                                                            remove += 2;
                                                                        }
                                                                    }
                                                                } else {
                                                                    listNotAdd.addAll(getListDauLon());
                                                                }
                                                            } else if (removeNum.equalsIgnoreCase("dauchan")) {
                                                                listNotAdd.addAll(getListDauChan());
                                                            } else if (removeNum.equalsIgnoreCase("daule")) {
                                                                listNotAdd.addAll(getListDauLe());
                                                            } else if (removeNum.equalsIgnoreCase("ditbe")) {
                                                                listNotAdd.addAll(getListDitBe());
                                                            } else if (removeNum.equalsIgnoreCase("ditlon")) {
                                                                listNotAdd.addAll(getListDitLon());
                                                            } else if (removeNum.equalsIgnoreCase("ditchan")) {
                                                                listNotAdd.addAll(getListDitChan());
                                                            } else if (removeNum.equalsIgnoreCase("ditle")) {
                                                                listNotAdd.addAll(getListDitLe());
                                                            } else if (removeNum.equalsIgnoreCase("chanchan")) {
                                                                listNotAdd.addAll(getListChanChan());
                                                            } else if (removeNum.equalsIgnoreCase("lele")) {
                                                                listNotAdd.addAll(getListLeLe());
                                                            } else if (removeNum.equalsIgnoreCase("chanle")) {
                                                                listNotAdd.addAll(getListChanLe());
                                                            } else if (removeNum.equalsIgnoreCase("lechan")) {
                                                                listNotAdd.addAll(getListLeChan());
                                                            } else if (removeNum.equalsIgnoreCase("bebe")) {
                                                                listNotAdd.addAll(getListBeBe());
                                                            } else if (removeNum.equalsIgnoreCase("belon")) {
                                                                listNotAdd.addAll(getListBeLon());
                                                            } else if (removeNum.equalsIgnoreCase("lonlon")) {
                                                                listNotAdd.addAll(getListLonLon());
                                                            } else if (removeNum.equalsIgnoreCase("lonbe")) {
                                                                listNotAdd.addAll(getListLonBe());
                                                            } else if (removeNum.equalsIgnoreCase("tonglon")) {
                                                                listNotAdd.addAll(getListTongLon());
                                                            } else if (removeNum.equalsIgnoreCase("tongbe")) {
                                                                listNotAdd.addAll(getListTongBe());
                                                            } else if (removeNum.equalsIgnoreCase("tongchan")) {
                                                                listNotAdd.addAll(getListTongChan());
                                                            } else if (removeNum.equalsIgnoreCase("tongle")) {
                                                                listNotAdd.addAll(getListTongLe());
                                                            } else if (removeNum.equalsIgnoreCase("kepbang")) {
                                                                listNotAdd.addAll(getListKepBang());
                                                            } else if (removeNum.equalsIgnoreCase("keplech")) {
                                                                listNotAdd.addAll(getListKepLech());
                                                            } else if (removeNum.equalsIgnoreCase("kepam")) {
                                                                listNotAdd.addAll(getListKepAm());
                                                            } else if (removeNum.equalsIgnoreCase("satkep")) {
                                                                listNotAdd.addAll(getListSatKep());
                                                            } else if (removeNum.equalsIgnoreCase("satkeplech")) {
                                                                listNotAdd.addAll(getListSatKepLech());
                                                            } else if (removeNum.equalsIgnoreCase("vuongvuong")) {
                                                                listNotAdd.addAll(getListVuongVuong());
                                                            } else if (removeNum.equalsIgnoreCase("vuongtron")) {
                                                                listNotAdd.addAll(getListVuongTron());
                                                            } else if (removeNum.equalsIgnoreCase("trontron")) {
                                                                listNotAdd.addAll(getListTronTron());
                                                            } else if (removeNum.equalsIgnoreCase("tronvuong")) {
                                                                listNotAdd.addAll(getListTronVuong());
                                                            } else if (removeNum.equalsIgnoreCase("bortrung")) {
                                                                isRemoveDuplicate = true;
                                                            } else if (removeNum.equalsIgnoreCase("bor")) {
                                                                break;
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"" + removeNum + "\"");
                                                                isError = true;
                                                                break;
                                                            }
                                                        }
                                                        i = remove;
                                                    }
                                                    break;
                                                case "bortrung":
                                                    isRemoveDuplicate = true;
                                                    break;
                                                default:
                                                    setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                                    isError = true;

                                            }
                                            if (isError) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else { //Nếu sai định dạng giá trị tiền
                        setError(processEntity, childEntity, "Không hiểu được giá trị tiền");
                    }
                }
            } else { //Không chứa x
                if (previousEntity != null && !previousEntity.isError() && childEntity.getType() == type) {
                    String moneyAdd = ((int) previousEntity.getListDataLoto().get(0).getMoneyTake()) + "n";
                    listWord = new ArrayList<>(listWord);
                    listWord.add(moneyAdd);
                    String newValue = childEntity.getValue() + " " + moneyAdd;
                    childEntity.setValue(newValue);
                }
                if (listWord.size() < 3) { //Nếu size nhỏ hơn 3
                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
                    if (patternPriceFull.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
                        String newValue = childEntity.getValue().substring(0, childEntity.getValue().length() - price.length()) +
                                "x " + price;
                        childEntity.setValue(newValue);
                        setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
                        isError = true;
                    } else {
                        setError(processEntity, childEntity, "Không đúng định dạng");
                        isError = true;
                    }
                } else { //Nếu size lớn hơn 3
                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
                    if (patternPriceFull.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
                        String newValue = childEntity.getValue().substring(0, childEntity.getValue().length() - price.length()) +
                                "x " + price;
                        childEntity.setValue(newValue);

                        priceValue = Double.parseDouble(price.replaceAll("\\D", ""));
                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
                        if (priceUnit == -1) {
                            priceUnit = PriceUnitEnum.PRICE_N;
                        } else {
                            String p = price.replaceAll("\\d", "");
                            if (settingDefault != null) {
                                switch (settingDefault.getTiente()) {
                                    case TienTe.TIEN_VIETNAM:
                                        if (!p.equalsIgnoreCase("d") && !p.equalsIgnoreCase("n")
                                                && !p.equalsIgnoreCase("trn") && !p.equalsIgnoreCase("tr")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_TAIWAN:
                                        if (!p.equalsIgnoreCase("k") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_JAPAN:
                                        if (!p.equalsIgnoreCase("y") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_KOREA:
                                        if (!p.equalsIgnoreCase("w") && !p.equalsIgnoreCase("d")) {
                                            isPriceUnitError = true;
                                        }
                                        break;
                                }
                            }
                        }
                        priceValue = processPriceValue(priceValue, priceUnit, customer);
                        if (priceUnit == PriceUnitEnum.PRICE_D && (accountSetting != null && accountSetting.getDonvitinhde() == 1)) {
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else if (isPriceUnitError) {
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else {
                            if (priceValue < 0) { //Nếu giá trị tiền = 0
                                setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                                isError = true;
                            } else { //Nếu giá trị tiền lớn hơn 0
                                for (int i = 1; i < listWord.size() - 1; i++) { //Lấy các cặp số
                                    String word = listWord.get(i);
                                    if (patternNumber.matcher(word).matches()) { //Nếu từ là số
                                        if (word.length() == 1 || word.length() > 4) {
                                            setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                            isError = true;
                                            break;
                                        } else if (word.length() == 3) {
                                            if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
                                                listNum.add(word.substring(0, 2));
                                                listNum.add(word.substring(1));
                                            } else { //Nếu không phải số đối xứng
                                                if (word.equalsIgnoreCase("100")) {
                                                    if (listWord.get(i + 1).equalsIgnoreCase("so")) {//Xử lý với trường hợp 100 số
                                                        listNum.addAll(getList100So());
                                                        i = i + 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                        isError = true;
                                                        break;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                    isError = true;
                                                    break;
                                                }
                                            }
                                        } else if (word.length() == 4) {
                                            listNum.add(word.substring(0, 2));
                                            listNum.add(word.substring(2));
                                        } else {
                                            listNum.add(word);
//                                                processAddNumberObject(childEntity, type, priceValue, word);
                                        }
                                    } else { //Nếu từ không phải là số
                                        switch (word) {
                                            case "boj":
                                                ArrayList<String> listNumBo = new ArrayList<>();
                                                String wordBo1 = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordBo1).matches()) { //Nếu từ là số
                                                    if (wordBo1.length() == 1) {
                                                        setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                        isError = true;
                                                    } else if (wordBo1.length() > 2) {
                                                        setError(processEntity, childEntity, "Không hiểu \"bộ " + wordBo1 + "\"");
                                                        isError = true;
                                                    } else {
                                                        listNumBo.add(wordBo1);
                                                        if (i + 2 >= listWord.size() - 1) {
                                                            i += 1;
                                                        } else {
                                                            for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                                i = j;
                                                                String wordAfter2 = listWord.get(j);
                                                                if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                    if (wordAfter2.length() == 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                        setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                        isError = true;
                                                                    } else if (wordAfter2.length() > 2) {
                                                                        i--;
                                                                        break;
                                                                    } else {
                                                                        listNumBo.add(wordAfter2);
                                                                    }
                                                                } else { // nếu là chữ
                                                                    i--;
                                                                    break;
                                                                }

                                                            }
                                                        }
                                                    }
                                                    if (!isError) {
                                                        for (String s : listNumBo) {
                                                            listNum.addAll(getListBo(s));
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu \"bộ\"\n Không hiểu" + wordBo1);
                                                    isError = true;
                                                }
                                                break;
                                            case "daudit":
                                                String wordAfter1 = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfter1).matches()) { //Nếu từ là số
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAfter1 = wordAfter1 + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAfter1)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                i--;
                                                                break;
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        char[] array = wordAfter1.toCharArray();

                                                        for (char c : array) {
                                                            for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                String num = c + String.valueOf(dit);
                                                                listNum.add(num);
                                                            }
                                                            for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                String num = String.valueOf(dau) + c;
                                                                listNum.add(num);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                    isError = true;
                                                }
                                                break;

                                            case "dau":
                                                String wordGhepDit = "";
                                                String wordAfterDau = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterDau).matches()) { //Nếu từ là số
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau đầu không thể là Đề nữa\n-đầu1 2 3 hoặc đầu1.2.3 hoặc đầu123 được hiểu là đầu1 đầu2 đầu3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAfterDau = wordAfterDau + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAfterDau)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                if (wordAfter2.equalsIgnoreCase("ghepdit")) { //Nếu là ghepdit
                                                                    for (int k = j + 1; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        i = k;
                                                                        wordGhepDit = listWord.get(k);
                                                                        if (patternNumber.matcher(wordGhepDit).matches()) { //Nếu là số
                                                                            if (k + 1 >= listWord.size() - 1) {
                                                                                k += 1;
                                                                            } else {
                                                                                for (int l = k + 1; l < listWord.size() - 1; l++) {
                                                                                    k = l;
                                                                                    i = k;
                                                                                    String wordAfterDit = listWord.get(l);
                                                                                    if (patternNumber.matcher(wordAfterDit).matches()) { //Nếu là số
                                                                                        if (wordAfterDit.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                            isError = true;
                                                                                            setError(processEntity, childEntity, "Sau đít không thể là Đề nữa");
                                                                                            break;
                                                                                        } else { //Nếu là số có 1 chữ số
                                                                                            wordGhepDit = wordGhepDit + wordAfterDit;
                                                                                            if (!checkValidateRepeatChar(wordGhepDit)) {
                                                                                                isError = true;
                                                                                                setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        i--;
                                                                                        break;
                                                                                    }
                                                                                }
                                                                                break;
                                                                            }
                                                                        } else {
                                                                            setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                            isError = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                } else {
                                                                    i--;
                                                                    break;
                                                                }
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        if (!wordGhepDit.equalsIgnoreCase("")) {
                                                            char[] arrayDau = wordAfterDau.toCharArray();
                                                            char[] arrayDit = wordGhepDit.toCharArray();
                                                            for (char c : arrayDau) {
                                                                for (char c1 : arrayDit) {
                                                                    String num = c + String.valueOf(c1);
                                                                    listNum.add(num);
                                                                }
                                                            }
                                                        } else {
                                                            char[] array = wordAfterDau.toCharArray();
                                                            for (char c : array) {
                                                                for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                    String num = c + String.valueOf(dit);
                                                                    listNum.add(num);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                    isError = true;
                                                }
                                                break;
                                            case "dit":
                                                String wordAfterDit = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterDit).matches()) { //Nếu từ là số
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAfterDit = wordAfterDit + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAfterDit)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                i--;
                                                                break;
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        char[] array = wordAfterDit.toCharArray();
                                                        for (char c : array) {
                                                            for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                String num = String.valueOf(dau) + c;
                                                                listNum.add(num);


                                                            }
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                    isError = true;
                                                }
                                                break;
                                            case "tong":
                                                String wordAfterTong = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterTong).matches()) { //Nếu từ là số
                                                    if (!checkValidateRepeatChar(wordAfterTong)) {
                                                        isError = true;
                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                        break;
                                                    }
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau tổng không thể là Đề nữa\n-tổng1 2 3 hoặc tổng1.2.3 hoặc tổng123 được hiểu là tổng1 tổng2 tổng3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAfterTong = wordAfterTong + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAfterTong)) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                i--;
                                                                break;
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        char[] array = wordAfterTong.toCharArray();
                                                        for (char c : array) {
                                                            listNum.addAll(getListTong(Integer.parseInt(String.valueOf(c))));
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Tổng phải là số");
                                                    isError = true;
                                                }
                                                break;
                                            case "chamlt":
                                                String wordAfterchamlt = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterchamlt).matches()) { //Nếu từ là số
                                                    if (!checkValidateRepeatChar(wordAfterchamlt) || wordAfterchamlt.length() > 5) {
                                                        isError = true;
                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                        break;
                                                    }
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAfterchamlt = wordAfterchamlt + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAfterchamlt) || wordAfterchamlt.length() > 5) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                i--;
                                                                break;
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        char[] array = wordAfterchamlt.toCharArray();
                                                        for (char c : array) {
                                                            listNum.addAll(getListChamLT(Integer.parseInt(String.valueOf(c))));
                                                        }
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordAfterchamlt);
                                                    isError = true;
                                                }
                                                break;
                                            case "cham":
                                                String wordAftercham = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAftercham).matches()) { //Nếu từ là số
                                                    if (!checkValidateRepeatChar(wordAftercham) || wordAftercham.length() > 5) {
                                                        isError = true;
                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                        break;
                                                    }
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        i += 1;
                                                    } else {
                                                        for (int j = i + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                            i = j;
                                                            String wordAfter2 = listWord.get(j);
                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                if (wordAfter2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                    break;
                                                                } else { //Nếu là số có 1 chữ số
                                                                    wordAftercham = wordAftercham + wordAfter2;
                                                                    if (!checkValidateRepeatChar(wordAftercham) || wordAftercham.length() > 5) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                        break;
                                                                    }
                                                                }
                                                            } else { // nếu là chữ
                                                                i--;
                                                                break;
                                                            }

                                                        }
                                                    }
                                                    if (!isError) {
                                                        listNum.addAll(getListCham(wordAftercham));
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordAftercham);
                                                    isError = true;
                                                }
                                                break;
                                            case "gheptrong":
                                                String wordAfterGhepTrong = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterGhepTrong).matches()) { //Nếu từ là số
                                                    if (!checkValidateRepeatChar(wordAfterGhepTrong)) {
                                                        isError = true;
                                                        setError(processEntity, childEntity, "Các số không được trùng nhau. Ví dụ: Đề ghép trong 2345 x 10n");
                                                        break;
                                                    } else {
                                                        i += 1;
                                                    }

                                                    if (!isError) {
                                                        listNum.addAll(getListGhepTrong(wordAfterGhepTrong));
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu gheptrong \n Không hiểu " + wordAfterGhepTrong);
                                                    isError = true;
                                                }
                                                break;
                                            case "dan":
                                                String wordAfterDan = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterDan).matches()) { //Nếu từ là số
                                                    if (wordAfterDan.length() != 2) { //Nếu số khác 2 chữ số
                                                        isError = true;
                                                        setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                    } else {
                                                        char[] arr = wordAfterDan.toCharArray();
                                                        if (Integer.parseInt(String.valueOf(arr[0])) > Integer.parseInt(String.valueOf(arr[1]))) {
                                                            isError = true;
                                                            setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                        } else {
                                                            if (i + 2 < listWord.size() - 1) {
                                                                String wordAfterDan2 = listWord.get(i + 2);
                                                                if (patternNumber.matcher(wordAfterDan2).matches()) {
                                                                    setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                    isError = true;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (!isError) {
                                                        listNum.addAll(getListDan(wordAfterDan));
                                                        i += 1;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                    isError = true;
                                                }
                                                break;
                                            case "tu":
                                                String wordAfterTu = listWord.get(i + 1);
                                                String wordAfterDen = "";
                                                if (patternNumber.matcher(wordAfterTu).matches()) { //Nếu từ là số
                                                    if (i + 2 >= listWord.size() - 1) {
                                                        setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                        isError = true;
                                                    } else {
                                                        String wordDen = listWord.get(i + 2);
                                                        if (wordDen.equalsIgnoreCase("den")) {
                                                            if (i + 3 >= listWord.size() - 1) {
                                                                setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99");
                                                                isError = true;
                                                            } else {
                                                                wordAfterDen = listWord.get(i + 3);
                                                                if (patternNumber.matcher(wordAfterDen).matches()) { //Nếu từ là số
                                                                    if (wordAfterTu.length() != 2 || wordAfterDen.length() != 2
                                                                            || Integer.parseInt(wordAfterTu) > Integer.parseInt(wordAfterDen)) {
                                                                        setError(processEntity, childEntity, "Sai định dạng tuABdenCD với điều kiện AB phải nhỏ hơn CD. Ví dụ: tu 00 den 99");
                                                                        isError = true;
                                                                    }
                                                                } else {
                                                                    setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99\n Không hiểu \"" + wordAfterDen + "\"");
                                                                    isError = true;
                                                                }
                                                            }
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                            isError = true;
                                                        }
                                                    }

                                                    if (!isError) {
                                                        listNum.addAll(getListTuDen(wordAfterTu, wordAfterDen));
                                                        i += 3;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không đúng định dạng");
                                                    isError = true;
                                                }
                                                break;
                                            case "tongtren":
                                                String wordAfterTongTren = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterTongTren).matches()) {
                                                    if (wordAfterTongTren.equalsIgnoreCase("10")) {
                                                        listNum.addAll(getListTongTren10());
                                                        i += 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordAfterTongTren + "\"");
                                                        isError = true;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordAfterTongTren + "\"");
                                                    isError = true;
                                                }
                                                break;
                                            case "tongduoi":
                                                String wordAfterTongDuoi = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterTongDuoi).matches()) {
                                                    if (wordAfterTongDuoi.equalsIgnoreCase("10")) {
                                                        listNum.addAll(getListTongDuoi10());
                                                        i += 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordAfterTongDuoi + "\"");
                                                        isError = true;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordAfterTongDuoi + "\"");
                                                    isError = true;
                                                }
                                                break;
                                            case "khongchiahetcho":
                                                String wordAfterKhongChiaHet = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterKhongChiaHet).matches()) {
                                                    if (wordAfterKhongChiaHet.equalsIgnoreCase("3")) {
                                                        listNum.addAll(getListKhongChiaHet3());
                                                        i += 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                        isError = true;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                    isError = true;
                                                }
                                                break;
                                            case "chiahetcho":
                                                String wordAfterChiaHet = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterChiaHet).matches()) {
                                                    if (wordAfterChiaHet.equalsIgnoreCase("3")) {
                                                        listNum.addAll(getListChiaHet3());
                                                        i += 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                        isError = true;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                    isError = true;
                                                }
                                                break;
                                            case "chiabadu":
                                                String wordAfterChiaDu = listWord.get(i + 1);
                                                if (patternNumber.matcher(wordAfterChiaDu).matches()) {
                                                    if (wordAfterChiaDu.equalsIgnoreCase("1")) {
                                                        listNum.addAll(getListChia3Du1());
                                                        i += 1;
                                                    } else if (wordAfterChiaDu.equalsIgnoreCase("2")) {
                                                        listNum.addAll(getListChia3Du2());
                                                        i += 1;
                                                    } else {
                                                        setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                        isError = true;
                                                    }
                                                } else {
                                                    setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                    isError = true;
                                                }
                                                break;
                                            case "daube":
                                                if ((i + 1 < listWord.size() - 1 && listWord.get(i + 1).equalsIgnoreCase("cua"))) {
                                                    if (i + 2 < listWord.size() - 1) {
                                                        if (listWord.get(i + 2).equalsIgnoreCase("ditlon")) {
                                                            listNum.addAll(getListDauBeDitLon());
                                                            i += 2;
                                                        } else if (listWord.get(i + 2).equalsIgnoreCase("ditbe")) {
                                                            listNum.addAll(getListDauBeDitBe());
                                                            i += 2;
                                                        }
                                                    }
                                                } else {
                                                    listNum.addAll(getListDauBe());
                                                }
                                                break;
                                            case "daulon":
                                                if ((i + 1 < listWord.size() - 1 && listWord.get(i + 1).equalsIgnoreCase("cua"))) {
                                                    if (i + 2 < listWord.size() - 1) {
                                                        if (listWord.get(i + 2).equalsIgnoreCase("ditlon")) {
                                                            listNum.addAll(getListDauLonDitLon());
                                                            i += 2;
                                                        } else if (listWord.get(i + 2).equalsIgnoreCase("ditbe")) {
                                                            listNum.addAll(getListDauLonDitBe());
                                                            i += 2;
                                                        }
                                                    }
                                                } else {
                                                    listNum.addAll(getListDauLon());
                                                }
                                                break;
                                            case "dauchan":
                                                listNum.addAll(getListDauChan());
                                                break;
                                            case "daule":
                                                listNum.addAll(getListDauLe());
                                                break;
                                            case "ditbe":
                                                listNum.addAll(getListDitBe());
                                                break;
                                            case "ditlon":
                                                listNum.addAll(getListDitLon());
                                                break;
                                            case "ditchan":
                                                listNum.addAll(getListDitChan());
                                                break;
                                            case "ditle":
                                                listNum.addAll(getListDitLe());
                                                break;
                                            case "chanchan":
                                                listNum.addAll(getListChanChan());
                                                break;
                                            case "lele":
                                                listNum.addAll(getListLeLe());
                                                break;
                                            case "chanle":
                                                listNum.addAll(getListChanLe());
                                                break;
                                            case "lechan":
                                                listNum.addAll(getListLeChan());
                                                break;
                                            case "bebe":
                                                listNum.addAll(getListBeBe());
                                                break;
                                            case "belon":
                                                listNum.addAll(getListBeLon());
                                                break;
                                            case "lonlon":
                                                listNum.addAll(getListLonLon());
                                                break;
                                            case "lonbe":
                                                listNum.addAll(getListLonBe());
                                                break;
                                            case "tonglon":
                                                listNum.addAll(getListTongLon());
                                                break;
                                            case "tongbe":
                                                listNum.addAll(getListTongBe());
                                                break;
                                            case "tongchan":
                                                listNum.addAll(getListTongChan());
                                                break;
                                            case "tongle":
                                                listNum.addAll(getListTongLe());
                                                break;
                                            case "kepbang":
                                                listNum.addAll(getListKepBang());
                                                break;
                                            case "keplech":
                                                listNum.addAll(getListKepLech());
                                                break;
                                            case "kepam":
                                                listNum.addAll(getListKepAm());
                                                break;
                                            case "satkep":
                                                listNum.addAll(getListSatKep());
                                                break;
                                            case "satkeplech":
                                                listNum.addAll(getListSatKepLech());
                                                break;
                                            case "vuongvuong":
                                                listNum.addAll(getListVuongVuong());
                                                break;
                                            case "vuongtron":
                                                listNum.addAll(getListVuongTron());
                                                break;
                                            case "trontron":
                                                listNum.addAll(getListTronTron());
                                                break;
                                            case "tronvuong":
                                                listNum.addAll(getListTronVuong());
                                                break;
                                            case "bor":
                                                for (int remove = i + 1; remove < listWord.size() - 1; remove++) {
                                                    String removeNum = listWord.get(remove);
                                                    if (patternNumber.matcher(removeNum).matches()) {
                                                        if (removeNum.length() == 1 || removeNum.length() > 4) {
                                                            setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                            isError = true;
                                                        } else if (removeNum.length() == 3) {
                                                            if (Pattern.compile("(\\d)\\d*\\1").matcher(removeNum).matches()) { //Nếu là số đối xứng
                                                                listNotAdd.add(removeNum.substring(0, 2));
                                                                listNotAdd.add(removeNum.substring(1));
                                                            } else { //Nếu không phải số đối xứng
                                                                setError(processEntity, childEntity, "Đề phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                                isError = true;
                                                            }
                                                        } else {
                                                            listNotAdd.add(removeNum);
                                                        }
                                                    } else {
                                                        if (removeNum.equalsIgnoreCase("boj")) {
                                                            ArrayList<String> listRemoveBo = new ArrayList<>();
                                                            String wordBo = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordBo).matches()) { //Nếu từ là số
                                                                if (wordBo.length() == 1) {
                                                                    setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                    isError = true;
                                                                } else if (wordBo.length() > 2) {
                                                                    setError(processEntity, childEntity, "Không hiểu \"bộ " + wordBo + "\"");
                                                                    isError = true;
                                                                } else {
                                                                    listRemoveBo.add(wordBo);
                                                                    if (remove + 2 >= listWord.size() - 1) {
                                                                        remove += 1;
                                                                    } else {
                                                                        for (int j = remove + 2; j < listWord.size() - 1; j++) { //duyệt các từ sau
                                                                            remove = j;
                                                                            String wordAfter2 = listWord.get(j);
                                                                            if (patternNumber.matcher(wordAfter2).matches()) { //Nếu là số
                                                                                if (wordAfter2.length() == 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                    setError(processEntity, childEntity, "Bộ phải là 2 số");
                                                                                    isError = true;
                                                                                } else if (wordAfter2.length() > 2) {
                                                                                    remove--;
                                                                                    break;
                                                                                } else {
                                                                                    listRemoveBo.add(wordAfter2);
                                                                                }
                                                                            } else { // nếu là chữ
                                                                                remove--;
                                                                                break;
                                                                            }

                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    for (String s : listRemoveBo) {
                                                                        listNotAdd.addAll(getListBo(s));
                                                                    }
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"bộ\"\n Không hiểu" + wordBo);
                                                                isError = true;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("daudit")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    char[] array = wordNum.toCharArray();
                                                                    for (char c : array) {
                                                                        for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                            String num = c + String.valueOf(dit);
                                                                            if (!listNotAdd.contains(num)) {
                                                                                listNotAdd.add(num);
                                                                            }
                                                                        }
                                                                        for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                            String num = String.valueOf(dau) + c;
                                                                            if (!listNotAdd.contains(num)) {
                                                                                listNotAdd.add(num);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("dit")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau đít không thể là Đề nữa\n-đít1 2 3 hoặc đít1.2.3 hoặc đít123 được hiểu là đít1 đít2 đít3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    char[] array = wordNum.toCharArray();
                                                                    for (char c : array) {
                                                                        for (int dau = 0; dau <= 9; dau++) { //xử lý đít
                                                                            String num = String.valueOf(dau) + c;
                                                                            if (!listNotAdd.contains(num)) {
                                                                                listNotAdd.add(num);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("dau")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau đầu không thể là Đề nữa\n-đầu1 2 3 hoặc đầu1.2.3 hoặc đầu123 được hiểu là đầu1 đầu2 đầu3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    char[] array = wordNum.toCharArray();
                                                                    for (char c : array) {
                                                                        for (int dit = 0; dit <= 9; dit++) { //xử lý đầu
                                                                            String num = c + String.valueOf(dit);
                                                                            if (!listNotAdd.contains(num)) {
                                                                                listNotAdd.add(num);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Đầu đuôi phải là số");
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("tong")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                    break;
                                                                }
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau tổng không thể là Đề nữa\n-tổng1 2 3 hoặc tổng1.2.3 hoặc tổng123 được hiểu là tổng1 tổng2 tổng3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Các số trong Đầu, Đuôi, Tổng không được trùng nhau. Ví dụ: Đề đầu 2345 x 10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    char[] array = wordNum.toCharArray();
                                                                    for (char c : array) {
                                                                        listNotAdd.addAll(getListTong(Integer.parseInt(String.valueOf(c))));
                                                                    }
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Tổng phải là số");
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("chamlt")) {

                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                    break;
                                                                }
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    char[] array = wordNum.toCharArray();
                                                                    for (char c : array) {
                                                                        listNotAdd.addAll(getListChamLT(Integer.parseInt(String.valueOf(c))));
                                                                    }
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordNum);
                                                                isError = true;
                                                                break;
                                                            }


                                                        } else if (removeNum.equalsIgnoreCase("cham")) {

                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                    break;
                                                                }
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    remove += 1;
                                                                } else {
                                                                    for (int k = remove + 2; k < listWord.size() - 1; k++) { //duyệt các từ sau
                                                                        String wordNum2 = listWord.get(k);
                                                                        if (patternNumber.matcher(wordNum2).matches()) { //Nếu là số
                                                                            remove = k;
                                                                            if (wordNum2.length() > 1) { //Nếu là số có từ 2 chữ số trở lên
                                                                                isError = true;
                                                                                setError(processEntity, childEntity, "Sau chạm không thể là Đề nữa\n-chạm1 2 3 hoặc chạm1.2.3 hoặc chạm123 được hiểu là chạm1 chạm2 chạm3");
                                                                                break;
                                                                            } else { //Nếu là số có 1 chữ số
                                                                                wordNum = wordNum + wordNum2;
                                                                                if (!checkValidateRepeatChar(wordNum) || wordNum.length() > 5) {
                                                                                    isError = true;
                                                                                    setError(processEntity, childEntity, "Không đúng định dạng. Sau chạm chỉ chấp nhận từ 1 đến 5 số liền nhau và khác nhau. Ví dụ: de cham12345 x10n");
                                                                                    break;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            remove += 1;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                if (!isError) {
                                                                    listNotAdd.addAll(getListCham(wordNum));
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"chamlt\" \n Không hiểu " + wordNum);
                                                                isError = true;
                                                                break;
                                                            }


                                                        } else if (removeNum.equalsIgnoreCase("gheptrong")) {

                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (!checkValidateRepeatChar(wordNum)) {
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Các số không được trùng nhau. Ví dụ: Đề ghép trong 2345 x 10n");
                                                                    break;
                                                                } else {
                                                                    remove += 1;
                                                                }

                                                                if (!isError) {
                                                                    listNotAdd.addAll(getListGhepTrong(wordNum));
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"gheptrong\" \n Không hiểu " + wordNum);
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("dan")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (wordNum.length() != 2) { //Nếu số khác 2 chữ số
                                                                    isError = true;
                                                                    setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                                } else {
                                                                    char[] arr = wordNum.toCharArray();
                                                                    if (Integer.parseInt(String.valueOf(arr[0])) > Integer.parseInt(String.valueOf(arr[1]))) {
                                                                        isError = true;
                                                                        setError(processEntity, childEntity, "Dàn phải có định dạng: dan AB với điều kiện A phải nhỏ hơn B. Ví dụ: dan 28");
                                                                    } else {
                                                                        if (remove + 2 < listWord.size() - 1) {
                                                                            String wordAfterDan2 = listWord.get(remove + 2);
                                                                            if (patternNumber.matcher(wordAfterDan2).matches()) {
                                                                                setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                                isError = true;
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                if (!isError) {
                                                                    listNotAdd.addAll(getListDan(wordNum));
                                                                    remove += 1;
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không ra được số. VD: dan 28 78 x10n thì có 2 cách hiểu\n-dan28 dan78 x10n\n-dan 28x10n de 78x10n\nBạn hiểu theo cách nào thì ghi rõ như vậy");
                                                                isError = true;
                                                            }

                                                        } else if (removeNum.equalsIgnoreCase("tu")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            String wordNumDen = "";
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (remove + 2 >= listWord.size() - 1) {
                                                                    setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                                    isError = true;
                                                                } else {
                                                                    String wordDen = listWord.get(remove + 2);
                                                                    if (wordDen.equalsIgnoreCase("den")) {
                                                                        if (remove + 3 >= listWord.size() - 1) {
                                                                            setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99");
                                                                            isError = true;
                                                                        } else {
                                                                            wordNumDen = listWord.get(remove + 3);
                                                                            if (patternNumber.matcher(wordNumDen).matches()) { //Nếu từ là số
                                                                                if (wordNum.length() != 2 || wordNumDen.length() != 2
                                                                                        || Integer.parseInt(wordNum) > Integer.parseInt(wordNumDen)) {
                                                                                    setError(processEntity, childEntity, "Sai định dạng tuABdenCD với điều kiện AB phải nhỏ hơn CD. Ví dụ: tu 00 den 99");
                                                                                    isError = true;
                                                                                }
                                                                            } else {
                                                                                setError(processEntity, childEntity, "Sai định dạng tuABdenCD. Ví dụ: tu 00 den 99\n Không hiểu \"" + wordNumDen + "\"");
                                                                                isError = true;
                                                                            }
                                                                        }
                                                                    } else {
                                                                        setError(processEntity, childEntity, "Không hiểu \"tu\"");
                                                                        isError = true;
                                                                    }
                                                                }

                                                                if (!isError) {
                                                                    listNotAdd.addAll(getListTuDen(wordNum, wordNumDen));
                                                                    remove += 3;
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không đúng định dạng");
                                                                isError = true;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("tongtren")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (wordNum.equalsIgnoreCase("10")) {
                                                                    listNotAdd.addAll(getListTongTren10());
                                                                    remove += 1;
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu tongtren \"" + wordNum + "\"");
                                                                    isError = true;
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"tongtren\" \n Không hiểu " + wordNum);
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("tongduoi")) {


                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) { //Nếu từ là số
                                                                if (wordNum.equalsIgnoreCase("10")) {
                                                                    listNotAdd.addAll(getListTongDuoi10());
                                                                    remove += 1;
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu tongduoi \"" + wordNum + "\"");
                                                                    isError = true;
                                                                }

                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu \"tongduoi\" \n Không hiểu " + wordNum);
                                                                isError = true;
                                                                break;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("khongchiahetcho")) {
                                                            String workNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(workNum).matches()) {
                                                                if (workNum.equalsIgnoreCase("3")) {
                                                                    listNotAdd.addAll(getListKhongChiaHet3());
                                                                    remove += 1;
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                                    isError = true;
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu khongchiahetcho");
                                                                isError = true;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("chiahetcho")) {
                                                            String workNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(workNum).matches()) {
                                                                if (workNum.equalsIgnoreCase("3")) {
                                                                    listNotAdd.addAll(getListChiaHet3());
                                                                    remove += 1;
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                                    isError = true;
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu chiahetcho");
                                                                isError = true;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("chiabadu")) {
                                                            String wordNum = listWord.get(remove + 1);
                                                            if (patternNumber.matcher(wordNum).matches()) {
                                                                if (wordNum.equalsIgnoreCase("1")) {
                                                                    listNotAdd.addAll(getListChia3Du1());
                                                                    remove += 1;
                                                                } else if (wordNum.equalsIgnoreCase("2")) {
                                                                    listNotAdd.addAll(getListChia3Du2());
                                                                    remove += 1;
                                                                } else {
                                                                    setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                                    isError = true;
                                                                }
                                                            } else {
                                                                setError(processEntity, childEntity, "Không hiểu chiabadu");
                                                                isError = true;
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("daube")) {
                                                            if ((remove + 1 < listWord.size() - 1 && listWord.get(remove + 1).equalsIgnoreCase("cua"))) {
                                                                if (remove + 2 < listWord.size() - 1) {
                                                                    if (listWord.get(remove + 2).equalsIgnoreCase("ditlon")) {
                                                                        listNotAdd.addAll(getListDauBeDitLon());
                                                                        remove += 2;
                                                                    } else if (listWord.get(remove + 2).equalsIgnoreCase("ditbe")) {
                                                                        listNotAdd.addAll(getListDauBeDitBe());
                                                                        remove += 2;
                                                                    }
                                                                }
                                                            } else {
                                                                listNotAdd.addAll(getListDauBe());
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("daulon")) {
                                                            if ((remove + 1 < listWord.size() - 1 && listWord.get(remove + 1).equalsIgnoreCase("cua"))) {
                                                                if (remove + 2 < listWord.size() - 1) {
                                                                    if (listWord.get(remove + 2).equalsIgnoreCase("ditlon")) {
                                                                        listNotAdd.addAll(getListDauLonDitLon());
                                                                        remove += 2;
                                                                    } else if (listWord.get(remove + 2).equalsIgnoreCase("ditbe")) {
                                                                        listNotAdd.addAll(getListDauLonDitBe());
                                                                        remove += 2;
                                                                    }
                                                                }
                                                            } else {
                                                                listNotAdd.addAll(getListDauLon());
                                                            }
                                                        } else if (removeNum.equalsIgnoreCase("dauchan")) {
                                                            listNotAdd.addAll(getListDauChan());
                                                        } else if (removeNum.equalsIgnoreCase("daule")) {
                                                            listNotAdd.addAll(getListDauLe());
                                                        } else if (removeNum.equalsIgnoreCase("ditbe")) {
                                                            listNotAdd.addAll(getListDitBe());
                                                        } else if (removeNum.equalsIgnoreCase("ditlon")) {
                                                            listNotAdd.addAll(getListDitLon());
                                                        } else if (removeNum.equalsIgnoreCase("ditchan")) {
                                                            listNotAdd.addAll(getListDitChan());
                                                        } else if (removeNum.equalsIgnoreCase("ditle")) {
                                                            listNotAdd.addAll(getListDitLe());
                                                        } else if (removeNum.equalsIgnoreCase("chanchan")) {
                                                            listNotAdd.addAll(getListChanChan());
                                                        } else if (removeNum.equalsIgnoreCase("lele")) {
                                                            listNotAdd.addAll(getListLeLe());
                                                        } else if (removeNum.equalsIgnoreCase("chanle")) {
                                                            listNotAdd.addAll(getListChanLe());
                                                        } else if (removeNum.equalsIgnoreCase("lechan")) {
                                                            listNotAdd.addAll(getListLeChan());
                                                        } else if (removeNum.equalsIgnoreCase("bebe")) {
                                                            listNotAdd.addAll(getListBeBe());
                                                        } else if (removeNum.equalsIgnoreCase("belon")) {
                                                            listNotAdd.addAll(getListBeLon());
                                                        } else if (removeNum.equalsIgnoreCase("lonlon")) {
                                                            listNotAdd.addAll(getListLonLon());
                                                        } else if (removeNum.equalsIgnoreCase("lonbe")) {
                                                            listNotAdd.addAll(getListLonBe());
                                                        } else if (removeNum.equalsIgnoreCase("tonglon")) {
                                                            listNotAdd.addAll(getListTongLon());
                                                        } else if (removeNum.equalsIgnoreCase("tongbe")) {
                                                            listNotAdd.addAll(getListTongBe());
                                                        } else if (removeNum.equalsIgnoreCase("tongchan")) {
                                                            listNotAdd.addAll(getListTongChan());
                                                        } else if (removeNum.equalsIgnoreCase("tongle")) {
                                                            listNotAdd.addAll(getListTongLe());
                                                        } else if (removeNum.equalsIgnoreCase("kepbang")) {
                                                            listNotAdd.addAll(getListKepBang());
                                                        } else if (removeNum.equalsIgnoreCase("keplech")) {
                                                            listNotAdd.addAll(getListKepLech());
                                                        } else if (removeNum.equalsIgnoreCase("kepam")) {
                                                            listNotAdd.addAll(getListKepAm());
                                                        } else if (removeNum.equalsIgnoreCase("satkep")) {
                                                            listNotAdd.addAll(getListSatKep());
                                                        } else if (removeNum.equalsIgnoreCase("satkeplech")) {
                                                            listNotAdd.addAll(getListSatKepLech());
                                                        } else if (removeNum.equalsIgnoreCase("vuongvuong")) {
                                                            listNotAdd.addAll(getListVuongVuong());
                                                        } else if (removeNum.equalsIgnoreCase("vuongtron")) {
                                                            listNotAdd.addAll(getListVuongTron());
                                                        } else if (removeNum.equalsIgnoreCase("trontron")) {
                                                            listNotAdd.addAll(getListTronTron());
                                                        } else if (removeNum.equalsIgnoreCase("tronvuong")) {
                                                            listNotAdd.addAll(getListTronVuong());
                                                        } else if (removeNum.equalsIgnoreCase("bortrung")) {
                                                            isRemoveDuplicate = true;
                                                        } else {
                                                            setError(processEntity, childEntity, "Không hiểu \"" + removeNum + "\"");
                                                            isError = true;
                                                            break;
                                                        }
                                                    }
                                                    i = remove;
                                                }
                                                break;
                                            case "bortrung":
                                                isRemoveDuplicate = true;
                                                break;
                                            default:
                                                setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                                isError = true;

                                        }
                                        if (isError) {
                                            break;
                                        }
                                    }
                                }


                            }
                        }
                    } else {
                        setError(processEntity, childEntity, "Không đúng định dạng");
                        isError = true;
                    }
                }
            }

            if (!isError) {
                ArrayList<String> listNumAdd = new ArrayList<>();
                for (String s : listNum) {
                    if (!listNotAdd.contains(s)) {
                        if (!(isRemoveDuplicate && listNumAdd.contains(s))) {
                            listNumAdd.add(s);
                        }
                    }
                }
                for (String s : listNumAdd) {
                    if (accountSetting != null && accountSetting.getKhachgiulaicophan() == 2) {
                        processAddNumberObject(childEntity, type, priceValue, s, accountSetting);
                    } else {
                        processAddNumberObject(childEntity, type, priceValue, s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
