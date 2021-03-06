package com.smsanalytic.lotto.common.calculate;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.PriceUnitEnum;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.model.setting.SettingXienType;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.common.calculate.StringCalculate.*;

public class ProcessStartWithXien2 {
    public static void processStartWithXien2(StringProcessEntity processEntity, StringProcessChildEntity childEntity, List<String> listWord, AccountObject customer, StringProcessChildEntity previousEntity,int position) {
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
            int type = TypeEnum.TYPE_XIEN2;
            childEntity.setType(type);
            ArrayList<String> listNum = new ArrayList<>();
            int priceValue = 0;
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
                        priceValue = Integer.parseInt(price.replaceAll("\\D", ""));
                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
                        if (priceUnit == -1) {
                            priceUnit = PriceUnitEnum.PRICE_N;
                        }else {
                            String p = price.replaceAll("\\d", "");
                            if (settingDefault!=null){
                                switch (settingDefault.getTiente()){
                                    case TienTe.TIEN_VIETNAM:
                                        if (!p.equalsIgnoreCase("d")&&!p.equalsIgnoreCase("n")
                                                &&!p.equalsIgnoreCase("trn")&&!p.equalsIgnoreCase("tr")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_TAIWAN:
                                        if (!p.equalsIgnoreCase("k")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_JAPAN:
                                        if (!p.equalsIgnoreCase("y")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_KOREA:
                                        if (!p.equalsIgnoreCase("w")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                }
                            }
                        }
                        priceValue = processPriceValue(priceValue, priceUnit);
                        if (priceUnit == PriceUnitEnum.PRICE_D && SettingXienType.getDonViTinh() == SettingXienType.MUOI_NGHIN) {
                            priceValue = priceValue * 10;
                        }
                        if (priceValue < 0) { //Nếu giá trị tiền = 0
                            setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                            isError = true;
                        } else if (isPriceUnitError){
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else { //Nếu giá trị tiền lớn hơn 0
                            if (listWord.size() < 4) { //Nếu số từ trong câu nhỏ hơn 4
                                setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
                                isError = true;
                            } else {
                                for (int i = 1; i < listWord.size() - 2; i++) { //Lấy các cặp số
                                    String word = listWord.get(i);
                                    if (patternNumber.matcher(word).matches()) { //Nếu từ là số
                                        if (word.length() < 2) {
                                            setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
                                            isError = true;
                                            break;
                                        } else {
                                            if (word.length() == 2) {
                                                if (isDuplicateNum(listNum, word, 2)) {
                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                    isError = true;
                                                    break;
                                                } else {
                                                    listNum.add(word);
                                                }
                                            } else if (word.length() == 3) {
                                                if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
                                                    if (isDuplicateNum(listNum, word.substring(0, 2), 2)) {
                                                        setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                        isError = true;
                                                        break;
                                                    } else {
                                                        listNum.add(word.substring(0, 2));
                                                    }
                                                    if (isDuplicateNum(listNum, word.substring(1), 2)) {
                                                        setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                        isError = true;
                                                        break;
                                                    } else {
                                                        listNum.add(word.substring(1));
                                                    }
                                                } else { //Nếu không phải số đối xứng
                                                    setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                    isError = true;
                                                    break;
                                                }
                                            } else if (word.length() == 4) {
                                                if (isDuplicateNum(listNum, word.substring(0, 2), 2)) {
                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                    isError = true;
                                                    break;
                                                } else {
                                                    listNum.add(word.substring(0, 2));
                                                }
                                                if (isDuplicateNum(listNum, word.substring(2), 2)) {
                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                    isError = true;
                                                    break;
                                                } else {
                                                    listNum.add(word.substring(2));
                                                }
                                            } else {
                                                setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                isError = true;
                                                break;
                                            }

                                        }
                                    } else { //Nếu từ không phải là số
                                        setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                        isError = true;
                                        break;
                                    }
                                }
                            }
                        }

                    } else { //Nếu sai định dạng giá trị tiền
                        setError(processEntity, childEntity, "Không hiểu được giá trị tiền");
                    }
                }
            } else { //Không chứa x
                if (previousEntity!=null && !previousEntity.isError() && childEntity.getType()==type){
                    String moneyAdd = ((int)previousEntity.getListDataLoto().get(0).getMoneyTake())+"n";
                    listWord = new ArrayList<>(listWord);
                    listWord.add(moneyAdd);
                    String newValue = childEntity.getValue() + " "+ moneyAdd;
                    childEntity.setValue(newValue);
                }
                if (listWord.size() < 3) { //Nếu size nhỏ hơn 3
                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
                    if (patternPriceFull.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
                        String newValue = childEntity.getValue().substring(0, childEntity.getValue().length() - price.length()) +
                                "x " + price;
                        childEntity.setValue(newValue);
                        setError(processEntity, childEntity, "Không đúng định dạng. thiếu giá trị tiền");
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

                        priceValue = Integer.parseInt(price.replaceAll("\\D", ""));
                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
                        if (priceUnit == -1) {
                            priceUnit = PriceUnitEnum.PRICE_N;
                        }else {
                            String p = price.replaceAll("\\d", "");
                            if (settingDefault!=null){
                                switch (settingDefault.getTiente()){
                                    case TienTe.TIEN_VIETNAM:
                                        if (!p.equalsIgnoreCase("d")&&!p.equalsIgnoreCase("n")
                                                &&!p.equalsIgnoreCase("trn")&&!p.equalsIgnoreCase("tr")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_TAIWAN:
                                        if (!p.equalsIgnoreCase("k")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_JAPAN:
                                        if (!p.equalsIgnoreCase("y")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                    case TienTe.TIEN_KOREA:
                                        if (!p.equalsIgnoreCase("w")&&!p.equalsIgnoreCase("d")){
                                            isPriceUnitError = true;
                                        }
                                        break;
                                }
                            }
                        }
                        priceValue = processPriceValue(priceValue, priceUnit);
                        if (priceUnit == PriceUnitEnum.PRICE_D && SettingXienType.getDonViTinh() == SettingXienType.MUOI_NGHIN) {
                            priceValue = priceValue * 10;
                        }
                        if (priceValue < 0) { //Nếu giá trị tiền = 0
                            setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                            isError = true;
                        } else if (isPriceUnitError){
                            setError(processEntity, childEntity, "Có lỗi khi xử lý. Không đúng định dạng");
                            isError = true;
                        } else { //Nếu giá trị tiền lớn hơn 0
                            for (int i = 1; i < listWord.size() - 1; i++) { //Lấy các cặp số
                                String word = listWord.get(i);
                                if (patternNumber.matcher(word).matches()) { //Nếu từ là số
                                    if (word.length() < 2) {
                                        setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
                                        isError = true;
                                        break;
                                    } else {
                                        if (word.length() == 2) {
                                            if (isDuplicateNum(listNum, word, 2)) {
                                                setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                isError = true;
                                                break;
                                            } else {
                                                listNum.add(word);
                                            }
                                        } else if (word.length() == 3) {
                                            if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
                                                if (isDuplicateNum(listNum, word.substring(0, 2), 2)) {
                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                    isError = true;
                                                    break;
                                                } else {
                                                    listNum.add(word.substring(0, 2));
                                                }
                                                if (isDuplicateNum(listNum, word.substring(1), 2)) {
                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                    isError = true;
                                                    break;
                                                } else {
                                                    listNum.add(word.substring(1));
                                                }
                                            } else { //Nếu không phải số đối xứng
                                                setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                                isError = true;
                                                break;
                                            }
                                        } else if (word.length() == 4) {
                                            if (isDuplicateNum(listNum, word.substring(0, 2), 2)) {
                                                setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                isError = true;
                                                break;
                                            } else {
                                                listNum.add(word.substring(0, 2));
                                            }
                                            if (isDuplicateNum(listNum, word.substring(2), 2)) {
                                                setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
                                                isError = true;
                                                break;
                                            } else {
                                                listNum.add(word.substring(2));
                                            }
                                        } else {
                                            setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
                                            isError = true;
                                            break;
                                        }

                                    }
                                } else { //Nếu từ không phải là số
                                    setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                    isError = true;
                                    break;
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
                if (listNum.size() < 2 || listNum.size() % 2 != 0) {
                    setError(processEntity, childEntity, "Xiên 2 không đúng số cặp");
                } else {
                    if (accountSetting != null && accountSetting.getKhachgiulaicophan() == 2) {
                        priceValue -= priceValue * accountSetting.getCophanxien() / 100;
                    }
                    for (int i = 0; i < listNum.size(); i += 2) {
                        LotoNumberObject numberObject = new LotoNumberObject();
                        numberObject.setValue1(listNum.get(i));
                        numberObject.setValue2(listNum.get(i + 1));
                        numberObject.setMoneyTake(priceValue);
                        numberObject.setMoneyKeep(priceValue);
                        numberObject.setDateTake(Calendar.getInstance().getTimeInMillis());
                        numberObject.setType(type);
                        // sắp xếp xác số xiên theo thứ tự tăng dần để thực hiện group by
                        List<Integer> xienList = new ArrayList<>();
                        xienList.add(Integer.parseInt(listNum.get(i)));
                        xienList.add(Integer.parseInt(listNum.get(i + 1)));
                        Collections.sort(xienList);
                        numberObject.setXienFormat(TextUtils.join("-", xienList));
                        childEntity.getListDataLoto().add(numberObject);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
