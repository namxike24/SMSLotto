package com.smsanalytic.lotto;

import java.util.List;

import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;

public class Test {

    private static void processStartWithTypeDE(StringProcessEntity processEntity, StringProcessChildEntity childEntity, List<String> listWord) {
//        try {
//            int type = TypeEnum.TYPE_XIEN4;
//            childEntity.setType(type);
//            ArrayList<String> listNum = new ArrayList<>();
//            int priceValue = 0;
//            boolean isError = false;
//
//            if (listWord.contains("x")) { //Nếu chứa x
//                if (listWord.lastIndexOf("x") == listWord.size() - 1) { //Nếu x ở cuối cùng
//                    setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
//                    isError = true;
//                } else if (listWord.lastIndexOf("x") < listWord.size() - 2) { //Nếu sau x có nhiều từ
//                    setError(processEntity, childEntity, "Không đúng định dạng");
//                    isError = true;
//                } else {//Nếu x nằm trước giá trị tiền
//                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
//                    if (patternPrice.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
//                        priceValue = Integer.parseInt(price.replaceAll("\\D", ""));
//                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
//                        if (priceUnit == -1) {
//                            priceUnit = PriceUnitEnum.PRICE_N;
//                        }
//                        priceValue = processPriceValue(priceValue, priceUnit);
//                        if (priceValue < 0) { //Nếu giá trị tiền = 0
//                            setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
//                            isError = true;
//                        } else { //Nếu giá trị tiền lớn hơn 0
//                            if (listWord.size() < 4) { //Nếu số từ trong câu nhỏ hơn 4
//                                setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
//                                isError = true;
//                            } else {
//                                for (int i = 1; i < listWord.size() - 2; i++) { //Lấy các cặp số
//                                    String word = listWord.get(i);
//                                    if (patternNumber.matcher(word).matches()) { //Nếu từ là số
//                                        if (word.length() < 2) {
//                                            setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
//                                            isError = true;
//                                            break;
//                                        } else {
//                                            if (word.length() == 2) {
//                                                if (listNum.contains(word)) {
//                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                    isError = true;
//                                                    break;
//                                                } else {
//                                                    listNum.add(word);
//                                                }
//                                            } else if (word.length() == 3) {
//                                                if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
//                                                    if (listNum.contains(word.substring(0, 2)) || listNum.contains(word.substring(1))) {
//                                                        setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                        isError = true;
//                                                        break;
//                                                    } else {
//                                                        listNum.add(word.substring(0, 2));
//                                                        listNum.add(word.substring(1));
//                                                    }
//                                                } else { //Nếu không phải số đối xứng
//                                                    setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
//                                                    isError = true;
//                                                    break;
//                                                }
//                                            } else if (word.length() == 4) {
//                                                if (listNum.contains(word.substring(0, 2)) || listNum.contains(word.substring(2)) || word.substring(0, 2).equalsIgnoreCase(word.substring(2))) {
//                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                    isError = true;
//                                                    break;
//                                                } else {
//                                                    listNum.add(word.substring(0, 2));
//                                                    listNum.add(word.substring(2));
//                                                }
//                                            } else {
//                                                setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
//                                                isError = true;
//                                                break;
//                                            }
//
//                                        }
//                                    } else { //Nếu từ không phải là số
//                                        setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
//                                        isError = true;
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//
//                    } else { //Nếu sai định dạng giá trị tiền
//                        setError(processEntity, childEntity, "Không hiểu được giá trị tiền");
//                    }
//                }
//            } else { //Không chứa x
//                if (listWord.size() < 3) { //Nếu size nhỏ hơn 3
//                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
//                    if (patternPriceFull.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
//                        String newValue = childEntity.getValue().substring(0, childEntity.getValue().length() - price.length()) +
//                                "x " + price;
//                        childEntity.setValue(newValue);
//                        setError(processEntity, childEntity, "Không đúng định dạng. thiếu giá trị tiền");
//                        isError = true;
//                    } else {
//                        setError(processEntity, childEntity, "Không đúng định dạng");
//                        isError = true;
//                    }
//                } else { //Nếu size lớn hơn 3
//                    String price = listWord.get(listWord.size() - 1); //Lấy giá trị tiền
//                    if (patternPriceFull.matcher(price).matches()) { //Nếu đúng định dạng giá trị tiền
//                        String newValue = childEntity.getValue().substring(0, childEntity.getValue().length() - price.length()) +
//                                "x " + price;
//                        childEntity.setValue(newValue);
//
//                        priceValue = Integer.parseInt(price.replaceAll("\\D", ""));
//                        int priceUnit = PriceUnitEnum.getPrice(price.replaceAll("\\d", ""));
//                        if (priceUnit == -1) {
//                            priceUnit = PriceUnitEnum.PRICE_N;
//                        }
//                        priceValue = processPriceValue(priceValue, priceUnit);
//                        if (priceValue < 0) { //Nếu giá trị tiền = 0
//                            setError(processEntity, childEntity, "Không đúng định dạng. Thiếu giá trị tiền");
//                            isError = true;
//                        } else { //Nếu giá trị tiền lớn hơn 0
//                            for (int i = 1; i < listWord.size() - 1; i++) { //Lấy các cặp số
//                                String word = listWord.get(i);
//                                if (patternNumber.matcher(word).matches()) { //Nếu từ là số
//                                    if (word.length() < 2) {
//                                        setError(processEntity, childEntity, "Không đúng định dạng. Cần ghi rõ các cặp số");
//                                        isError = true;
//                                        break;
//                                    } else {
//                                        if (word.length() == 2) {
//                                            if (listNum.contains(word)) {
//                                                setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                isError = true;
//                                                break;
//                                            } else {
//                                                listNum.add(word);
//                                            }
//                                        } else if (word.length() == 3) {
//                                            if (Pattern.compile("(\\d)\\d*\\1").matcher(word).matches()) { //Nếu là số đối xứng
//                                                if (listNum.contains(word.substring(0, 2)) || listNum.contains(word.substring(1))) {
//                                                    setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                    isError = true;
//                                                    break;
//                                                } else {
//                                                    listNum.add(word.substring(0, 2));
//                                                    listNum.add(word.substring(1));
//                                                }
//                                            } else { //Nếu không phải số đối xứng
//                                                setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
//                                                isError = true;
//                                                break;
//                                            }
//                                        } else if (word.length() == 4) {
//                                            if (listNum.contains(word.substring(0, 2)) || listNum.contains(word.substring(2)) || word.substring(0, 2).equalsIgnoreCase(word.substring(2))) {
//                                                setError(processEntity, childEntity, "Các cặp số của xiên không được trùng nhau");
//                                                isError = true;
//                                                break;
//                                            } else {
//                                                listNum.add(word.substring(0, 2));
//                                                listNum.add(word.substring(2));
//                                            }
//                                        } else {
//                                            setError(processEntity, childEntity, "Xiên phải là các cặp số:01 02 03...232 sẽ hiểu là 23 và 32. 2345 sẽ hiểu là 23 và 45");
//                                            isError = true;
//                                            break;
//                                        }
//
//                                    }
//                                } else { //Nếu từ không phải là số
//                                    setError(processEntity, childEntity, "Không hiểu \"" + word + "\"");
//                                    isError = true;
//                                    break;
//                                }
//                            }
//                        }
//                    } else {
//                        setError(processEntity, childEntity, "Không đúng định dạng");
//                        isError = true;
//                    }
//                }
//            }
//
//            if (!isError) {
//                if (listNum.size() < 2 || listNum.size() % 2 != 0) {
//                    setError(processEntity, childEntity, "Xiên 4 không đúng số cặp");
//                } else {
//                    for (int i = 0; i < listNum.size(); i += 3) {
//                        LotoNumberObject numberObject = new LotoNumberObject();
//                        numberObject.setValue1(listNum.get(i));
//                        numberObject.setValue2(listNum.get(i + 1));
//                        numberObject.setValue3(listNum.get(i + 2));
//                        numberObject.setValue4(listNum.get(i + 3));
//                        numberObject.setMoneyTake(priceValue);
//                        numberObject.setMoneyKeep(priceValue);
//                        numberObject.setDateTake(Calendar.getInstance().getTimeInMillis());
//                        numberObject.setType(type);
//                        childEntity.getListDataLoto().add(numberObject);
//                    }
//
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
