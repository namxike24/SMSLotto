package com.smsanalytic.lotto.common.calculate;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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

import static com.smsanalytic.lotto.common.calculate.StringCalculate.*;

public class ProcessStartWith3C {
    public static void processStartWithType3C(StringProcessEntity processEntity, StringProcessChildEntity childEntity, List<String> listWord, AccountObject customer, StringProcessChildEntity previousEntity,int position) {
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
            int type = TypeEnum.TYPE_3C;
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
                        priceValue = processPriceValue(priceValue, priceUnit);
                        if (priceUnit == PriceUnitEnum.PRICE_D) {
                            setError(processEntity, childEntity, "Ba càng phải kết thúc bằng n");
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
                                            if (word.length() != 3) {
                                                setError(processEntity, childEntity, "Ba càng phải là các cặp số có 3 chữ số: 001, 012, 123...");
                                                isError = true;
                                                break;
                                            } else {
                                                listNum.add(word);
//
                                            }
                                        } else { //Nếu từ không phải là số
                                            setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                            isError = true;
                                            break;
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

                        priceValue = Integer.parseInt(price.replaceAll("\\D", ""));
                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
                        if (priceUnit == -1) {
                            priceUnit = PriceUnitEnum.PRICE_N;
                        }
                        priceValue = processPriceValue(priceValue, priceUnit);
                        if (priceUnit == PriceUnitEnum.PRICE_D) {
                            setError(processEntity, childEntity, "Ba càng phải kết thúc bằng n");
                            isError = true;
                        } else {
                            if (priceValue < 0) { //Nếu giá trị tiền = 0
                                setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
                                isError = true;
                            } else { //Nếu giá trị tiền lớn hơn 0
                                for (int i = 1; i < listWord.size() - 1; i++) { //Lấy các cặp số
                                    String word = listWord.get(i);
                                    if (patternNumber.matcher(word).matches()) { //Nếu từ là số
                                        if (word.length() != 3) {
                                            setError(processEntity, childEntity, "Ba càng phải là các cặp số có 3 chữ số: 001, 012, 123...");
                                            isError = true;
                                            break;
                                        } else {
                                            listNum.add(word);
//
                                        }
                                    } else { //Nếu từ không phải là số
                                        setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
                                        isError = true;
                                        break;
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
                for (String s : listNum) {
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
