package com.smsanalytic.lotto.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.ui.xsmb.GuiTinNhanCongNoService;
import com.smsanalytic.lotto.ui.xsmb.TinhKetQuaXSService;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class Common {

    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    public static final String FORMAT_DATE_DD_MM_YYY = "dd/MM/yyy";
    public static final String FORMAT_DATE_DD_MM_YYY_2 = "dd-MM-yyy";
    public static final String FORMAT_DATE_HH_MM = "HH:mm";

    public static final String LAST_DAY_GET_XSMB = "LAST_DAY_GET_XSMB";
    public static final String XSMB_YESTERDAY = "XSMB_YESTERDAY";
    public static final String LIST_WORD_DEFAULT = "LIST_WORD_DEFAULT";
    public static final String LIST_WORD_CUSTOM = "LIST_WORD_CUSTOM";

//    public static final String DBFB = "Lotto";
    public static final String DBFB = "TestLotto";
//    public static final String DBFB = "CalData";

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     *
     * @return An alert dialog which leads to the notification enabling screen
     */
    public static AlertDialog buildNotificationServiceAlertDialog(Context activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                (dialog, id) -> activity.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialog, id) -> {
                    // If you choose to not enable the notification listener
                    // the app. will not work as expected
                });
        return (alertDialogBuilder.create());
    }


    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     *
     * @return True if enabled, false otherwise.
     */
    public static boolean isNotificationServiceEnabled(Activity activity) {
        String pkgName = activity.getPackageName();
        final String flat = Settings.Secure.getString(activity.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String stripAcents(String value) {
        try {
            if (value == null) return null;
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isInternetAccessible(Context context) {

//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                e.printStackTrace();
////                Log.e(LOG_TAG, "Couldn't check internet connection", e);
//            }
//        return false;

        Runtime runtime = Runtime.getRuntime();
        Process proc;
        try {
            proc = runtime.exec("ping -n 1" + "google.com.vn");
            if (proc.waitFor() == 0) { // normal exit
                return true;

            } else { // abnormal exit, so decide that the server is not
                // reachable
                return false;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // other servers, for example
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public static String contactExists(Context context, String number) {
        try {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static String formatMoney(Double money) {
        String currency = "0";
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        if (money != null) {
            currency = numberFormat.format(money);
        }
        return currency;
    }

    public static String formatPercent(Double percent) {
        String currency = "0";
        DecimalFormat numberFormat = new DecimalFormat("#,###.##");
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
        DecimalFormatSymbols symbols = numberFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numberFormat.setDecimalFormatSymbols(symbols);
        if (percent != null) {
            currency = numberFormat.format(percent);
        }
        return currency;
    }

    public static String formatMoney(Double money, int maxPoint) {
        String currency = "0";
        DecimalFormat numberFormat = new DecimalFormat("#,###.##");
        numberFormat.setMaximumFractionDigits(maxPoint);
        DecimalFormatSymbols symbols = numberFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        numberFormat.setDecimalFormatSymbols(symbols);
        if (money != null) {
            currency = numberFormat.format(money);
        }
        return currency;
    }

    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static void sendMailLogData(Context context, Exception ex) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+"taigc00760@gmail.com"));
//            intent.putExtra(Intent.EXTRA_SUBJECT,"Dữ liệu lỗi");
//            StringBuilder sb= new StringBuilder();
//            sb.append("Error:\n" +ex.getMessage()).append("\n\n");
//            for (StackTraceElement stackTraceElement: ex.getStackTrace()){
//                sb.append("\n").append(stackTraceElement.toString());
//
//            }
//            intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
//            context.startActivity(Intent.createChooser(intent,"222"));
//        }
//        catch (Exception e){
//
//        }

    }

    public static void sendMailLogDatas(Context context, String data) {
        try {
//            Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+"taigc00760@gmail.com"));
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_SUBJECT,"Dữ liệu lỗi");
////            StringBuilder sb= new StringBuilder();
////            sb.append("Error:\n" +ex.getMessage()).append("\n\n");
////            for (StackTraceElement stackTraceElement: ex.getStackTrace()){
////                sb.append("\n").append(stackTraceElement.toString());
////
////            }
//            intent.putExtra(Intent.EXTRA_TEXT,data);
//            context.startActivity(Intent.createChooser(intent,"222"));
            Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
            selectorIntent.setData(Uri.parse("mailto:"));

            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"taigc00760@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, data);
            emailIntent.setSelector( selectorIntent );

            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
        catch (Exception e){

        }

    }

    public static long getCurrentTimeLong() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static Calendar convertDateToCalender(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date d = dateFormat.parse(date);
            cal.setTime(d);
        } catch (Exception e) {

        }
        return cal;
    }

    public static String convertDateByFormat(long date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }

    public static long addHourToDate(long date, int hour) {
        try {
            if (hour > 0) {
                return date + (hour * 60 * 60 * 1000) - 1;
            } else {
                return date + (hour * 60 * 60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long convertDateToMiniSeconds(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dateFormat = sdf.parse(date);
            return dateFormat.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String convertMilisecondToHHmm(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return MyApp.getInstance().getString(R.string.tv_hh_mm, hours, minutes);
    }


    public static long convertHHMMtoMilisecond(String date) {
        try {
            String[] data = date.split(":");
            int hours = Integer.parseInt(data[0]);
            int minutes = Integer.parseInt(data[1]);

            long time = (minutes * 60 + 3600 * hours) * 1000;
            return time;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void createDialog(Context context, String title, String content, IClickListener iClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("Hủy", (dialog, which) -> {
                    if (iClickListener != null) {
                        iClickListener.acceptEvent(false);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Đồng ý", (dialog, which) -> {
                    if (iClickListener != null) {
                        iClickListener.acceptEvent(true);
                    }
                    dialog.dismiss();
                })
                .create();

        alertDialog.show();
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void disableView(final View v) {
        v.postDelayed(() -> v.setClickable(true), 500);
        v.setClickable(false);

    }

    /**
     * ẩn bàn phím
     * created_by nvnam on 14/11/2018
     *
     * @param context context truyền vào
     */
    public static void hideKeyBoard(Context context) {
        try {
            View v = ((Activity) context).getCurrentFocus();
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ẩn bàn phím với edit text truyền vào
     * created_by nvnam on 14/11/2018
     *
     * @param context  context truyền vào
     * @param editText edittext đang được focus
     */
    public static void hideKeyBoard(Context context, EditText editText) {
        try {
            if (editText != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getValueReplaceKey(String original) {
        return original.replaceAll("bortrung", "bỏ trùng")
                .replaceAll("bor", "bỏ").replaceAll("boj", "bộ")
                .replaceAll("xienghephai", "xienghep2")
                .replaceAll("xienghepba", "xienghep3").replaceAll("xienghepbon", "xienghep4")
                .replaceAll("xienhai", "xien2").replaceAll("xienba", "xien3").replaceAll("xienbon", "xien4");
    }

    public static int convertDpToPx(float width_dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        float width = width_px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(width_dp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static int getScreenWitch(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float width = metrics.widthPixels;
        return (int) width;

    }

    public static String roundMoney(double value) {
        long factor = (long) Math.pow(10, 1);
        value = value * factor;
        long tmp = Math.round(value);
        return formatMoney((double) tmp / factor);
    }
    public static String formatDouble(double value) {
        DecimalFormat format = new DecimalFormat("0.#");
      return  format.format(value);
    }


    public static void startJob(Context context) {
        try {
            DateTime dt = new DateTime();
            DateTime dtVietNam = dt.withZone(DateTimeZone.forID("Asia/Ho_Chi_Minh"));
            DateTime dtVietNamEnd = dt.withZone(DateTimeZone.forID("Asia/Ho_Chi_Minh"));
            dtVietNamEnd = dtVietNamEnd.withHourOfDay(18).withMinuteOfHour(35).withSecondOfMinute(0).withMillisOfSecond(0);
            if (dtVietNamEnd.isBefore(dtVietNam)) {
                dtVietNamEnd = dtVietNamEnd.plusDays(1);
            }

            Log.e("Time Zone", dtVietNam.toString() + "   " + dtVietNamEnd.toString());
            ComponentName serviceComponent = new ComponentName(context, TinhKetQuaXSService.class);
            JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);
            builder.setMinimumLatency(dtVietNamEnd.getMillis() - dtVietNam.getMillis()); // wait at least
//            builder.setOverrideDeadline(2000); // maximum delay
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("abc", "123");
            builder.setExtras(bundle);
            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startJobTinNhanCongNo(Context context) {
        try {

            SettingDefault settingDefault;
            String cache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
            if (!cache.isEmpty()) {
                settingDefault = new Gson().fromJson(cache, SettingDefault.class);
            } else {
                String defaultCache = Common.loadJSONFromAsset(context, "SettingDefault.json");
                settingDefault = new Gson().fromJson(defaultCache, SettingDefault.class);
            }
            if (settingDefault != null && settingDefault.isTuDongGuiTinCongNo()) {
                String[] time = Common.convertMilisecondToHHmm(settingDefault.getTimeGuiCongNo()).split(":");
                int hour = Integer.parseInt(time[0]);
                int minutes = Integer.parseInt(time[1]);
                Calendar current = Calendar.getInstance();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < current.getTimeInMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }

                ComponentName serviceComponent = new ComponentName(context, GuiTinNhanCongNoService.class);
                JobInfo.Builder builder = new JobInfo.Builder(2, serviceComponent);
                builder.setMinimumLatency(calendar.getTimeInMillis() - current.getTimeInMillis()); // wait at least
//            builder.setOverrideDeadline(2000); // maximum delay
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
                PersistableBundle bundle = new PersistableBundle();
                builder.setExtras(bundle);
                JobScheduler jobScheduler =
                        (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelJob(Context context) {
        try {
            JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            tm.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi tin nhắn sms tự động
     *
     * @param textSend
     * @param phone
     */
    public static void sendSmsAuto(String textSend, String phone) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> listMessage = smsManager.divideMessage(textSend);
        smsManager.sendMultipartTextMessage(phone, null, listMessage, null, null);
    }


    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static boolean isAppOpen() {
        boolean result = false;
        try {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            result = appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static int getTypeTienTe(Context context){
        SettingDefault settingDefault;
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(context, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        return settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM;
    }

}
