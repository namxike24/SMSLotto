package com.smsanalytic.lotto.common;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * We follow ISO 8601 for date time;
 *
 * @Created_by nvchuong on 19/12/2016
 */
public class DateTimeUtils {
    // format hien thi tren man hinh thiet bi
    public static final String SERVER_DATE_TIME_PATTERN_MISA = "dd/MM/yyyy hh:mm:ss";
    public static final String SERVER_DATE_TIME_PATTERN_MISA_24H_FULL = "dd/MM/yyyy HH:mm:ss";
    public static final String MONTH_YEAR_FORMAT = "MM/yyyy";
    public static final String DAY_MONTH_YEAR_FORMAT = "dd-MM-yyyy";

    public static final String SERVER_DATE_TIME_PATTERN_MISA_24H = "dd/MM/yyyy HH:mm";

    public static final String SERVER_DATE_TIME_PATTERN_12H = "yyyy-MM-dd hh:mm:ss aa";
    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy hh:mm a";
    public static final String DATE_TIME_PATTERN_24 = "dd/MM/yyyy HH:mm";

    // format du lieu tu server tra v
    public static final String SERVER_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"; // 2016-12-24T09:08:10.11+07:00
    // format du lieu dang AM/ PM
    public static final String TIME_12_PATTERN = "hh:mm aa";
    public static final String TIME_24_PATTERN = "HH:mm";

    // format du lieu thoi gian bao gom ngay thang nam
    public static final String DAY_MONTH_YEAR_PATTERN = "dd/MM/yyyy";
    public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    public static final String ONLY_YEAR_PATTERN = "yyyy";
    public static final String IMAGE_DATE_FORMAT = "yyyyMMdd";
    public static final String DATA_FORMAT = "MMddyyyy";


    public static String convertServerTime(Date dateTime, String convertPattern) {
        if (dateTime == null)
            return "";
        SimpleDateFormat sdf2 = new SimpleDateFormat(convertPattern,
                Locale.getDefault());
        return sdf2.format(dateTime);
    }

    /**
     * convert dinh dạng giờ 24 thành 12
     *
     * @param time
     * @return
     * @Created_by nvchuong on 16/02/2017
     */
    public static String convertTime24ToTime12(String time) {
        SimpleDateFormat _24Hoursdf = new SimpleDateFormat(TIME_24_PATTERN);
        SimpleDateFormat _12Hoursdf = new SimpleDateFormat(TIME_12_PATTERN);

        try {
            Date dateObj = _24Hoursdf.parse(time);
            return _12Hoursdf.format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * Convert định dạng 12-> 24
     *
     * @param time
     * @return
     * @Created_by nvchuong on 16/02/2017
     */
    public static String convertTime12ToTime24(String time) {
        SimpleDateFormat _24Hoursdf = new SimpleDateFormat(TIME_24_PATTERN);
        SimpleDateFormat _12Hoursdf = new SimpleDateFormat(TIME_12_PATTERN);

        try {
            Date dateObj = _12Hoursdf.parse(time);
            return _24Hoursdf.format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }


    public static DateTime getDateFromString(String dateTime) {

        return ISODateTimeFormat.dateTimeParser().withZone(DateTimeZone.getDefault()).parseDateTime(dateTime);
//        SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_PATTERN,
//                Locale.getDefault());
//        try {
//            return sdf.parse(dateTime);
//        } catch (ParseException e) {
//            return new Date();
//        }
    }

    public static Date getDateFromString(String dateTime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format,
                Locale.getDefault());
        try {
            return sdf.parse(dateTime);
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * @return thời gian hiện tại
     * @Created_by nvchuong on 29/06/2017
     */
    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance();
    }

    /**
     * Hàm trả về ngày đầu tiên của tuần
     *
     * @return
     */

    public static Calendar getFirstDayOfWeek() {
        Calendar cal = (Calendar) Calendar.getInstance().clone();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.set(Calendar.DAY_OF_YEAR, --day);
        }
        return cal;
    }

    /**
     * Hàm lấy ngày cuối cùng của tuần
     *
     * @return
     * @Created_by nvchuong on 06/02/2017
     */
    public static Calendar getLastDayOfWeek() {

        Calendar cal = (Calendar) Calendar.getInstance().clone();
        cal.getFirstDayOfWeek();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.set(Calendar.DAY_OF_YEAR, ++day);
        }
        return cal;
    }


    /**
     * Hàm lấy ngày cuối cùng trong tháng
     *
     * @return
     * @Created_by nvchuong on 06/02/2017
     */
    public static int getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

	/*
     * Lấy ngày đầu tiên của tháng nào? năn nào? Chú ý month từ 0 - 11
	 * @Created_by nvchuong on 06/02/2017
	 */

    public static String getFirstDateOfMonth(String format, int m, int y) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);

        cal.set(Calendar.DATE, 1);
        Date currentDate = cal.getTime();
        SimpleDateFormat fm = new SimpleDateFormat(format);

        return fm.format(currentDate);
    }

	/*
     * Lấy ngày cuối cùng của tháng nào? năm nào? Chú ý month từ 0 - 11
	 * @Created_by nvchuong on 06/02/2017
	 */

    public static String getLastDateOfMonth(String format, int m, int y) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);

        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date currentDate = cal.getTime();
        SimpleDateFormat fm = new SimpleDateFormat(format);

        return fm.format(currentDate);
    }

    /**
     * Ham covert dinh dang date trong ISMAC ve dinh dang datetime chuan
     *
     * @param date
     * @return
     * @Created_by nvchuong on 04/05/2017
     */
    public static String convertDateTimeToDefaultFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeUtils.SERVER_DATE_TIME_PATTERN_12H,
                Locale.ENGLISH);
        try {
            Date dateTime = sdf.parse(date);
            dateTime.getTime();
            SimpleDateFormat sdf2 = new SimpleDateFormat(DateTimeUtils.SERVER_DATE_TIME_PATTERN,
                    Locale.ENGLISH);
            return sdf2.format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Hàm convert DateToString
     *
     * @param date
     * @param format
     * @return
     * @Created_by nvchuong on 12/04/2017
     */
    public static String convertDateToString(Date date, String format) {
        if (date == null) {
            return null;
        }

        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return getDateTimeString(date, dateFormat);

    }

    public static String convertDateTime(String fromDate, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        DateTime dateTime = DateTimeUtils.getDateFromString(fromDate);
        long timeInMillis = dateTime.getMillis();
        calendar.setTimeInMillis(timeInMillis);
        return DateTimeUtils.convertServerTime(calendar.getTime(), dateFormat);
    }

    private static String getDateTimeString(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }
}
