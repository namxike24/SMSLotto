package com.smsanalytic.lotto.common.calculate;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.KyTuThayThe;
import com.smsanalytic.lotto.common.PriceUnitEnum;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class StringCalculate {
    public static ArrayList<KyTuThayThe> listKyTuThayThe = new ArrayList<>(); //Danh sách các ký tự từ khóa
    private static StringCalculate mInstance;
    public static Pattern patternUnit; //kiểm tra đơn vị
    public static Pattern patternType; //Kiểm tra loại
    public static Pattern patternKeyword; //Kiểm tra tất cả từ khóa
    public static Pattern patternPrice; //Kiểm tra giá trị tiền
    public static Pattern patternPriceFull; //Kiểm tra giá trị tiền đầy đủ
    public static Pattern patternNumber; //Kiểm tra số

    private StringCalculate() {

    }

    public synchronized static StringCalculate getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StringCalculate();
            Type type = new TypeToken<ArrayList<KyTuThayThe>>() {
            }.getType();
            listKyTuThayThe = new Gson().fromJson(get(context), type);
            patternKeyword = Pattern.compile(getKeyWordRegex());
            patternUnit = Pattern.compile("d|n|tr|trn|k|y|w");
            patternType = Pattern.compile("de|lo|loa|canggiua|bacang|xien|xien2|xienhai|xien3|xienba|xien4|xienbon|xienquay|xienghep2|xienghephai|xienghep3|xienghepba|xienghep4|xienghepbon|haicua|daunhat|ditnhat|daudb|ditbe|ditlon");
            patternPrice = Pattern.compile("[0-9]+(d|n|tr|trn|k|y|w)?");
            patternPriceFull = Pattern.compile("[0-9]+(d|n|tr|trn|k|y|w)");
            patternNumber = Pattern.compile("\\d+");
        }
        return mInstance;
    }

    public static void updateData(Context context) {
        try {
            Type type = new TypeToken<ArrayList<KyTuThayThe>>() {
            }.getType();
            listKyTuThayThe = new Gson().fromJson(get(context), type);
            patternKeyword = Pattern.compile(getKeyWordRegex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * lấy regex các từ khóa
     *
     * @return
     */
    private static String getKeyWordRegex() {
        StringBuilder sb = new StringBuilder();
        try {
            for (KyTuThayThe kyTuThayThe : listKyTuThayThe) {
                sb.append("|").append(kyTuThayThe.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.substring(1);
    }

    /**
     * đọc file json từ assets
     *
     * @param paramContext
     * @return
     */
    private static String get(Context paramContext) {
        try {
            String fileName = "kytuthaythe.json";
            SettingDefault settingDefault = null;
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
            if (settingDefault != null && settingDefault.getTiente() != TienTe.TIEN_VIETNAM) {
                fileName = "kytuthaythe1.json";
            }
            InputStream param = paramContext.getAssets().open(fileName);
            int i = param.available();
            byte[] bytes = new byte[i];
            param.read(bytes);
            param.close();
            String result = new String(bytes, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void processChildEntity(StringProcessEntity processEntity, StringProcessChildEntity childEntity) {
        try {
            List<String> listWord = Arrays.asList(childEntity.getValue().split("\\s"));
            if (listWord.size() < 2) { //Nếu số từ nhỏ hơn 2 thì báo lỗi
                setError(processEntity, childEntity, "Không đúng định dạng");
            } else {
                if (childEntity.getValue().contains(" bo ")) {
                    setError(processEntity, childEntity, "Không hiểu bo là gì:\n-Nếu là bộ(hệ) thì ghi là bộ hoặc dãy\n-Nếu là bỏ thì ghi là bỏ hoặc loại");
                } else {
                    if (patternType.matcher(listWord.get(0)).matches()) { //Nếu từ đầu tiên là loại chơi (lo, de,....)
                        switch (listWord.get(0)) {
                            case TypeEnum.TYPE_DE_STRING:
                                ProcessStartWithDe.processStartWithTypeDE(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_LO_STRING:
                                ProcessStartWithLo.processStartWithTypeLo(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_3C_STRING:
                                ProcessStartWith3C.processStartWithType3C(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_DITNHAT_STRING:
                                ProcessStartWithDitNhat.processStartWithTypeDitNhat(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_DAUDB_STRING:
                                ProcessStartWithDauDB.processStartWithTypeDauDB(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_DAUNHAT_STRING:
                                ProcessStartWithDauNhat.processStartWithTypeDauNhat(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_CANGGIUA_STRING:
                                ProcessStartWithCangGiua.processStartWithTypeCangGiua(processEntity, childEntity, listWord, null, -1);
                                break;
                            case TypeEnum.TYPE_XIENGHEP2_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghephai", "xienghep2"));
                                ProcessStartWithXienGhep2.processStartWithTypeXienGhep2(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIENGHEP3_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghepba", "xienghep3"));
                                ProcessStartWithXienGhep3.processStartWithTypeXienGhep3(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIENGHEP4_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghepbon", "xienghep4"));
                                ProcessStartWithXienGhep4.processStartWithTypeXienGhep4(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIENQUAY_STRING:
                                ProcessStartWithXienQuay.processStartWithTypeXienQuay(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case "xien":
                                ProcessStartWithXien.processStartWithXien(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIEN2_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienhai", "xien2"));
                                ProcessStartWithXien2.processStartWithXien2(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIEN3_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienba", "xien3"));
                                ProcessStartWithXien3.processStartWithXien3(processEntity, childEntity, listWord, null, null, -1);
                                break;
                            case TypeEnum.TYPE_XIEN4_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienbon", "xien4"));
                                ProcessStartWithXien4.processStartWithXien4(processEntity, childEntity, listWord, null, null, -1);
                                break;
                        }
                    } else { //từ đầu tiên không phải từ khóa loại
                        ProcessNotStartWithType.processNotStartWithType(processEntity, childEntity, listWord, null, null, -1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processChildEntity(StringProcessEntity processEntity, StringProcessChildEntity childEntity, AccountObject customer, StringProcessChildEntity previousEntity, int position) {
        try {
            List<String> listWord = Arrays.asList(childEntity.getValue().split("\\s"));
            if (listWord.size() < 2) { //Nếu số từ nhỏ hơn 2 thì báo lỗi
                setError(processEntity, childEntity, "Không đúng định dạng");
            } else {
                if (childEntity.getValue().contains(" bo ")) {
                    setError(processEntity, childEntity, "Không hiểu bo là gì:\n-Nếu là bộ(hệ) thì ghi là bộ hoặc dãy\n-Nếu là bỏ thì ghi là bỏ hoặc loại");
                } else {
                    if (patternType.matcher(listWord.get(0)).matches()) { //Nếu từ đầu tiên là loại chơi (lo, de,....)
                        switch (listWord.get(0)) {
                            case TypeEnum.TYPE_DE_STRING:
                                ProcessStartWithDe.processStartWithTypeDE(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_LO_STRING:
                                ProcessStartWithLo.processStartWithTypeLo(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_3C_STRING:
                                ProcessStartWith3C.processStartWithType3C(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_DITNHAT_STRING:
                                ProcessStartWithDitNhat.processStartWithTypeDitNhat(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_DAUDB_STRING:
                                ProcessStartWithDauDB.processStartWithTypeDauDB(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_DAUNHAT_STRING:
                                ProcessStartWithDauNhat.processStartWithTypeDauNhat(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_CANGGIUA_STRING:
                                ProcessStartWithCangGiua.processStartWithTypeCangGiua(processEntity, childEntity, listWord, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIENGHEP2_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghephai", "xienghep2"));
                                ProcessStartWithXienGhep2.processStartWithTypeXienGhep2(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIENGHEP3_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghepba", "xienghep3"));
                                ProcessStartWithXienGhep3.processStartWithTypeXienGhep3(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIENGHEP4_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienghepbon", "xienghep4"));
                                ProcessStartWithXienGhep4.processStartWithTypeXienGhep4(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIENQUAY_STRING:
                                ProcessStartWithXienQuay.processStartWithTypeXienQuay(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case "xien":
                                ProcessStartWithXien.processStartWithXien(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIEN2_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienhai", "xien2"));
                                ProcessStartWithXien2.processStartWithXien2(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIEN3_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienba", "xien3"));
                                ProcessStartWithXien3.processStartWithXien3(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                            case TypeEnum.TYPE_XIEN4_STRING:
                                childEntity.setValue(childEntity.getValue().replaceAll("xienbon", "xien4"));
                                ProcessStartWithXien4.processStartWithXien4(processEntity, childEntity, listWord, customer, previousEntity, position);
                                break;
                        }
                    } else { //từ đầu tiên không phải từ khóa loại
                        ProcessNotStartWithType.processNotStartWithType(processEntity, childEntity, listWord, customer, previousEntity, position);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isDuplicateNum(ArrayList<String> listNum, String num, int group) {
        boolean result = false;
        try {
            switch (group) {
                case 2:
                    result = !listNum.isEmpty() && listNum.size() % 2 != 0 && listNum.get(listNum.size() - 1).equalsIgnoreCase(num);
                    break;
                case 3:
                    if (!listNum.isEmpty() && listNum.size() % 3 != 0) {
                        if (listNum.size() % 3 == 1) {
                            result = listNum.get(listNum.size() - 1).equalsIgnoreCase(num);
                        } else {
                            result = listNum.get(listNum.size() - 1).equalsIgnoreCase(num) || listNum.get(listNum.size() - 2).equalsIgnoreCase(num);
                        }
                    }
                    break;
                case 4:
                    if (!listNum.isEmpty() && listNum.size() % 4 != 0) {
                        if (listNum.size() % 4 == 1) {
                            result = listNum.get(listNum.size() - 1).equalsIgnoreCase(num);
                        } else if (listNum.size() % 4 == 2) {
                            result = listNum.get(listNum.size() - 1).equalsIgnoreCase(num) || listNum.get(listNum.size() - 2).equalsIgnoreCase(num);
                        } else {
                            result = listNum.get(listNum.size() - 1).equalsIgnoreCase(num) || listNum.get(listNum.size() - 2).equalsIgnoreCase(num) || listNum.get(listNum.size() - 3).equalsIgnoreCase(num);
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean checkValidateRepeatChar(String wordAfter1) {
        char[] array = wordAfter1.toCharArray();
        for (char c : array) {
            if (wordAfter1.indexOf(c) != wordAfter1.lastIndexOf(c)) {
                return false;
            }
        }
        return true;
    }

    public static int processPriceValue(int priceValue, int priceUnit) {
        try {
            switch (priceUnit) {
                case PriceUnitEnum.PRICE_TRN:
                    return priceValue * 100;
                case PriceUnitEnum.PRICE_TR:
                    return priceValue * 1000;
                default:
                    return priceValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return priceValue;
        }
    }

    public static double processPriceValue(double priceValue, int priceUnit) {
        try {
            switch (priceUnit) {
                case PriceUnitEnum.PRICE_TRN:
                    return priceValue * 100;
                case PriceUnitEnum.PRICE_TR:
                    return priceValue * 1000;
                default:
                    return priceValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return priceValue;
        }
    }

    public static double processPriceValue(double priceValue, int priceUnit, AccountObject customer) {
        try {
            switch (priceUnit) {
                case PriceUnitEnum.PRICE_TRN:
                    if (customer == null) {
                        return Common.round(priceValue * 100);
                    } else {
                        AccountSetting setting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                        return getPriceByChuyenDe78(setting.getChuyende78(), priceValue * 100);
                    }
                case PriceUnitEnum.PRICE_TR:
                    if (customer == null) {
                        return Common.round(priceValue * 1000);
                    } else {
                        AccountSetting setting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                        return getPriceByChuyenDe78(setting.getChuyende78(), priceValue * 1000);
                    }
                default:
                    if (customer == null) {
                        return Common.round(priceValue);
                    } else {
                        AccountSetting setting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                        return getPriceByChuyenDe78(setting.getChuyende78(), priceValue);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return priceValue;
        }
    }

    public static void setError(StringProcessEntity processEntity, StringProcessChildEntity childEntity, String error) {
        childEntity.setError(error);
        processEntity.setHasError(true);
    }

    public static void processAddNumberObject(StringProcessChildEntity childEntity, int type, double priceValue, String word) {
        try {
            LotoNumberObject numberObject = new LotoNumberObject();
            numberObject.setValue1(word);
            numberObject.setMoneyTake(priceValue);
            numberObject.setMoneyKeep(priceValue);
            numberObject.setDateTake(Calendar.getInstance().getTimeInMillis());
            numberObject.setType(type);
            childEntity.getListDataLoto().add(numberObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processAddNumberObject(StringProcessChildEntity childEntity, int type, double priceValue, String word, AccountSetting accountSetting) {
        try {
            switch (type) {
                case TypeEnum.TYPE_DAUDB:
                case TypeEnum.TYPE_DE:
                case TypeEnum.TYPE_DAUNHAT:
                case TypeEnum.TYPE_DITNHAT:
                    priceValue -= priceValue * accountSetting.getCophandacbiet() / 100;
                    break;
                case TypeEnum.TYPE_LO:
                    priceValue -= priceValue * accountSetting.getCophanlo() / 100;
                    break;
                case TypeEnum.TYPE_3C:
                    priceValue -= priceValue * accountSetting.getCophan3c() / 100;
                    break;
                case TypeEnum.TYPE_XIEN2:
                case TypeEnum.TYPE_XIEN3:
                case TypeEnum.TYPE_XIEN4:
                case TypeEnum.TYPE_XIENGHEP2:
                case TypeEnum.TYPE_XIENGHEP3:
                case TypeEnum.TYPE_XIENGHEP4:
                case TypeEnum.TYPE_XIENQUAY:
                    priceValue -= priceValue * accountSetting.getCophanxien() / 100;
                    break;
            }
            LotoNumberObject numberObject = new LotoNumberObject();
            numberObject.setValue1(word);
            numberObject.setMoneyTake(priceValue);
            numberObject.setMoneyKeep(priceValue);
            numberObject.setDateTake(Calendar.getInstance().getTimeInMillis());
            numberObject.setType(type);
            childEntity.getListDataLoto().add(numberObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Xử lý chuỗi
     *
     * @param original chuỗi ban đầu
     * @return
     */
    public static StringProcessEntity processStringOriginal(String original) {
        try {
            StringProcessEntity processEntity = new StringProcessEntity();
            processEntity.setOriginal(original);
            processEntity.setListChild(new ArrayList<>());

            original = replaceKeyWord(original); //Chuyển các từ trong danh sách thành từ khóa tương ứng
            ArrayList<String> listWords = splitToWords(original); //Tách chuỗi ra thành list các từ
            for (String listDatum : listWords) {
                Log.e("List", listDatum + "");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listWords.size(); i++) { //Duyệt list các từ
                String word = listWords.get(i);
                if (patternKeyword.matcher(word).matches()) { //Nếu từ là từ khóa
                    if (patternUnit.matcher(word).matches()) { //Nếu từ là đơn vị thì add từ khóa vào và thêm chuỗi con
                        sb.append(word);
                        addChildToParent(processEntity, sb);
                        sb = new StringBuilder();
                    } else if (patternType.matcher(word).matches()) { //Nếu từ là từ khóa loại
                        if (sb.toString().trim().equalsIgnoreCase("")) { //Nếu từ là từ đầu tiên của chuỗi con thì add từ vào chuỗi
                            sb.append(" ").append(word);
                        } else { //Nếu từ không phải là đầu tiên của chuỗi con thì thêm chuỗi con rồi tạo chuỗi con mới và add từ
                            addChildToParent(processEntity, sb);
                            sb = new StringBuilder();
                            sb.append(" ").append(word);
                        }
                    } else { //Nếu là các từ khóa còn lại thì add vào chuỗi con
                        sb.append(" ").append(word);
                        if (i == listWords.size() - 1) { //Nếu là từ cuối cùng thì thêm chuỗi con
                            addChildToParent(processEntity, sb);
                        }
                    }
                } else { //Nếu không phải từ khóa thì add vào chuỗi con
                    sb.append(" ").append(word);
                    if (word.equalsIgnoreCase("cua") && i < listWords.size() - 1) {
                        sb.append(" ").append(listWords.get(i + 1));
                        i += 1;
                    } else {
                        if (i == listWords.size() - 1) {//Nếu là từ cuối cùng thì thêm chuỗi con
                            addChildToParent(processEntity, sb);
                        } else if ((i > 0 && listWords.get(i - 1).equalsIgnoreCase("x"))
                                && !patternUnit.matcher(listWords.get(i + 1)).matches()) {
                            addChildToParent(processEntity, sb);
                            sb = new StringBuilder();
                        }
                    }
                }
            }
            return processEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addChildToParent(StringProcessEntity processEntity, StringBuilder sb) {
        StringProcessChildEntity childEntity = new StringProcessChildEntity();
        String text = sb.toString().trim();
        if (text.startsWith("haicua")) {
            StringProcessChildEntity child1 = new StringProcessChildEntity();
            StringProcessChildEntity child2 = new StringProcessChildEntity();
            child1.setValue(text.replaceAll("haicua", "de"));
            child1.setListDataLoto(new ArrayList<>());
            processEntity.getListChild().add(child1);
            child2.setValue(text.replaceAll("haicua", "ditnhat"));
            child2.setListDataLoto(new ArrayList<>());
            processEntity.getListChild().add(child2);
        } else {
            childEntity.setValue(sb.toString().trim());
            childEntity.setListDataLoto(new ArrayList<>());
            processEntity.getListChild().add(childEntity);
        }
    }

    /**
     * Chuyển các từ trong danh sách thành từ khóa tương ứng
     *
     * @param original
     * @return
     */
    private static String replaceKeyWord(String original) {
        String spec = ".[{(*+?^$|";
//        String result = Common.stripAcents(original.replaceAll("bỏ", "bor").replaceAll("bỏ", "bor").replaceAll("bộ", "boj").replaceAll("bộ", "boj").replaceAll("\\.", "").replaceAll(",", ""));
        String result = Common.stripAcents(original.replaceAll("bỏ", "bor").replaceAll("bỏ", "bor").replaceAll("bộ", "boj").replaceAll("bộ", "boj").replaceAll("\\.", " ").replaceAll(",", " ")
        .replaceAll("diem","d"));
        result = result.replaceAll("[^a-zA-Z0-9]", " ");
        try {
            for (KyTuThayThe kyTuThayThe : listKyTuThayThe) {
                ArrayList<String> listData = kyTuThayThe.getDatas();
                StringBuilder sbRegex = new StringBuilder(spec.contains(listData.get(0)) ? "\\" + listData.get(0) : listData.get(0));
                if (listData.size() > 1) {
                    for (int i = 1; i < listData.size(); i++) {
                        sbRegex.append("|").append(spec.contains(listData.get(i)) ? "\\" + listData.get(i) : listData.get(i));
                    }
                }
                result = result.replaceAll(sbRegex.toString(), kyTuThayThe.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tách chuỗi ra thành list các từ
     *
     * @param original
     * @return
     */
    private static ArrayList<String> splitToWords(String original) {
        ArrayList<String> result = new ArrayList<>();
        try {
            original = original.replaceAll("\\s{2,}", " "); //Thay thế các khoảng trắng nhiều dấu cách thành 1 dấu cách
            char[] arrChar = original.toCharArray(); //chuyển chuỗi thành mảng ký tự
            String regexText = "[a-zA-Z]+";
            String regexNum = "[0-9]+";
            Pattern patternText = Pattern.compile(regexText); //Kiểm tra chữ cái
            Pattern patternNum = Pattern.compile(regexNum); //Kiểm tra số
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arrChar.length; i++) { //duyệt mảng ký tự
                String s = String.valueOf(arrChar[i]);
                sb.append(s); //Add ký tự vào chuỗi
                if (!patternText.matcher(sb.toString()).matches() && !patternNum.matcher(sb.toString()).matches()) { //Nếu chuỗi không phải là chữ cái và không phải số thì cắt chuỗi
                    String st = sb.substring(0, sb.length() - 1);
                    if (!st.equalsIgnoreCase("") //Nếu chuỗi cắt có dữ liệu và là chữ cái hoặc số thì add vào danh sách từ rồi tạo chuỗi mới
                            && (patternText.matcher(st).matches() || patternNum.matcher(st).matches())) {
                        result.add(st);
                    }
                    sb = new StringBuilder(s);
                    if (!patternText.matcher(sb.toString()).matches() && !patternNum.matcher(sb.toString()).matches()) { //Nếu không phải chữ cái và số thì clear
                        sb = new StringBuilder();
                    } else {
                        if (i == arrChar.length - 1) {
                            result.add(s);
                        }
                    }
                } else {
                    if (i == arrChar.length - 1) {
                        result.add(sb.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double getPriceByChuyenDe78(int type, double originPrice) {
        try {
            switch (type) {
                case 2:
                    return Common.round(originPrice * (77.0d / 70.0d));
                case 3:
                    return Common.round(originPrice * (80.0d / 70.0d));
                case 4:
                    return Common.round(originPrice * (84.0d / 70.0d));
                default:
                    return originPrice;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originPrice;
    }
}
