package com.smsanalytic.lotto.common;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;


public class CurrencyEditText extends AppCompatEditText {
    public interface NumericValueWatcher {
        void onChanged(double newValue);
        void onCleared();
    }



    private String unit="đ";
    public Locale locale = new Locale("vn","VN");
    public int decimalDigits = 2;

    private char GROUPING_SEPARATOR;
    private char DECIMAL_SEPARATOR;
    private String LEADING_ZERO_FILTER_REGEX;

    private String mDefaultText = null;
    private String mPreviousText = "";
    private String mNumberFilterRegex;
    private List<NumericValueWatcher> mNumericListeners = new ArrayList<>();

    //region TextWatcher

    private final TextWatcher mTextWatcher = new TextWatcher() {
        private boolean validateLock = false;

        @Override
        public void afterTextChanged(Editable s) {
            if (isCurrency){

                String text = s.toString();

                if (validateLock) {
                    return;
                }

                // If user presses GROUPING_SEPARATOR, convert it to DECIMAL_SEPARATOR
                if (text.endsWith(GROUPING_SEPARATOR+"")){
                    text = text.substring(0,text.length()-1) + DECIMAL_SEPARATOR;
                }

                // Limit decimal digits
                if (decimalDigitLimitReached(text)){
                    validateLock = true;
                    setText(mPreviousText); // cancel change and revert to previous input
                    setSelection(mPreviousText.length());
                    validateLock = false;
                    return;
                }

                // valid decimal number should not have thousand separators after a decimal separators
                if (hasGroupingSeperatorAfterDecimalSeperator(text)){
                    validateLock = true;
                    setText(mPreviousText); // cancel change and revert to previous input
                    setSelection(mPreviousText.length());
                    validateLock = false;
                    return;
                }


                // valid decimal number should not have more than 2 decimal separators
                if (countMatches(text, String.valueOf(DECIMAL_SEPARATOR)) > 1) {
                    validateLock = true;
                    setText(mPreviousText); // cancel change and revert to previous input
                    setSelection(mPreviousText.length());
                    validateLock = false;
                    return;
                }

                if (text.length() == 0) {
                    handleNumericValueCleared();
                    return;
                }

                // If only decimal separator is inputted, add a zero to the left of it
                if (text.equals(String.valueOf(DECIMAL_SEPARATOR))) {
                    text = '0' + text;
                }

                setTextInternal(format(text));
                setSelection(getText().length());
                handleNumericValueChanged();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing
        }
    };

    private boolean isCurrency;

    public boolean isCurrency() {
        return isCurrency;
    }

    public void setCurrency(boolean currency) {
        isCurrency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    //endregion
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        SettingDefault settingDefault;
            String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
        settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
    } else {
        String dateDefault = Common.loadJSONFromAsset(getContext(), "SettingDefault.json");
        settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
    }
    String keyTienTe = TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM).toLowerCase();

        this.unit=keyTienTe;


        setInputType(InputType.TYPE_CLASS_PHONE);

        reload();

        addTextChangedListener(mTextWatcher);
        setOnClickListener(v -> {
            // disable moving cursor
            setSelection(getText().length());
        });
        isCurrency=true;
        setOnFocusChangeListener((view, b) -> {
            if (!b){
                setCurrency(false);
                if (!getText().toString().equals("")){
                    setText(formatUnit(getText().toString(),unit));

                }                }
            else{
                setCurrency(true);
                setTextInternal(format(getText().toString()));
            }

        });
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        reload();
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
        reload();
    }

    private void reload(){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        GROUPING_SEPARATOR = symbols.getGroupingSeparator();
        DECIMAL_SEPARATOR = symbols.getDecimalSeparator();
        LEADING_ZERO_FILTER_REGEX = "^0+(?!$)";
        mNumberFilterRegex = "[^\\d\\" + this.DECIMAL_SEPARATOR + "]";
    }

    //region Utils

    private void handleNumericValueCleared() {
        mPreviousText = "";
        for (CurrencyEditText.NumericValueWatcher listener : mNumericListeners) {
            listener.onCleared();
        }
    }

    private void handleNumericValueChanged() {
        mPreviousText = getText().toString();
        for (CurrencyEditText.NumericValueWatcher listener : mNumericListeners) {
            listener.onChanged(getNumericValue());
        }
    }

    public void addNumericValueChangedListener(CurrencyEditText.NumericValueWatcher watcher) {
        mNumericListeners.add(watcher);
    }

    public void removeAllNumericValueChangedListeners() {
        while (!mNumericListeners.isEmpty()) {
            mNumericListeners.remove(0);
        }
    }

    public void setDefaultNumericValue(double defaultNumericValue, final String defaultNumericFormat) {
        mDefaultText = String.format(defaultNumericFormat, defaultNumericValue);
        setTextInternal(mDefaultText);
    }

    public void clear() {
        setTextInternal(mDefaultText != null ? mDefaultText : "");
        if (mDefaultText != null) {
            handleNumericValueChanged();
        }
    }

    public double getNumericValue() {
        String original = getText().toString().replaceAll(mNumberFilterRegex, "");
        try {
            return NumberFormat.getInstance().parse(original).doubleValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    private String format(final String original) {
        String[] parts = splitOriginalText(original);
        String number = parts[0].replaceAll(mNumberFilterRegex, "").replaceFirst(LEADING_ZERO_FILTER_REGEX, "");

        number = reverse(reverse(number).replaceAll("(.{3})", "$1" + GROUPING_SEPARATOR));
        number = removeStart(number, String.valueOf(GROUPING_SEPARATOR));

        if (parts.length > 1) {
            parts[1] = parts[1].replaceAll(mNumberFilterRegex, "");
            number += DECIMAL_SEPARATOR + parts[1];
        }

        return number;
    }

    private String formatUnit(final String original,String unit) {
        String[] parts = splitOriginalText(original);
        String number = parts[0].replaceAll(mNumberFilterRegex, "").replaceFirst(LEADING_ZERO_FILTER_REGEX, "");

        number = reverse(reverse(number).replaceAll("(.{3})", "$1" + GROUPING_SEPARATOR));
        number = removeStart(number, String.valueOf(GROUPING_SEPARATOR));

        if (parts.length > 1) {
            parts[1] = parts[1].replaceAll(mNumberFilterRegex, "");
            number += DECIMAL_SEPARATOR + parts[1];
        }

        return number+ unit;
    }

    private String[] splitOriginalText(String original) {
        //Dot is special character in regex, so we have to treat it specially.
        final String[] parts;
        if (DECIMAL_SEPARATOR == '.') {
            parts = original.split("\\.", -1);
        } else {
            parts = original.split(DECIMAL_SEPARATOR + "", -1);
        }
        return parts;
    }

    private void setTextInternal(String text) {
        removeTextChangedListener(mTextWatcher);
        setText(text);
        addTextChangedListener(mTextWatcher);
    }

    private String reverse(String original) {
        if (original == null || original.length() <= 1) {
            return original;
        }
        return TextUtils.getReverse(original, 0, original.length()).toString();
    }

    private String removeStart(String str, String remove) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }

    private int countMatches(String str, String sub) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int lastIndex = str.lastIndexOf(sub);
        if (lastIndex < 0) {
            return 0;
        } else {
            return 1 + countMatches(str.substring(0, lastIndex), sub);
        }
    }

    private boolean hasGroupingSeperatorAfterDecimalSeperator(String text){
        //Return true if thousand seperator (.) comes after a decimal seperator. (,)

        if (text.contains(GROUPING_SEPARATOR+"") && text.contains(DECIMAL_SEPARATOR+"")){
            int firstIndexOfDecimal = text.indexOf(DECIMAL_SEPARATOR);
            int lastIndexOfGrouping = text.lastIndexOf(GROUPING_SEPARATOR);

            if (firstIndexOfDecimal < lastIndexOfGrouping){
                return true;
            }
        }

        return false;
    }

    private boolean decimalDigitLimitReached(String text){
        //Return true if decimal digit limit is reached
        if (text.contains(DECIMAL_SEPARATOR+"")){

            if (DECIMAL_SEPARATOR == '.'){
                //Dot is special character in regex, so we have to treat it specially.
                String[] parts = text.split("\\.");
                if (parts.length>0){
                    String lastPart = parts[parts.length-1];

                    if (lastPart.length() == decimalDigits + 1){
                        return true;
                    }
                }
            }else{
                //If decimal seperator is not a dot, we can safely split.
                String[] parts = text.split(DECIMAL_SEPARATOR+"");
                if (parts.length>1){
                    String lastPart = parts[parts.length-1];

                    if (lastPart.length() == decimalDigits + 1){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}