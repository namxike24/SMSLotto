package com.smsanalytic.lotto;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.database.XSMBObject;
import com.smsanalytic.lotto.database.XSMBObjectDao;
import com.smsanalytic.lotto.ui.xsmb.XSMBUtils;


/**
 * Check cập nhật phiên bản mới
 * created_by nvnam on 19/06/2017
 * modified_by nvnam on 27/06/2017: cập nhật thêm what's new
 */
public class GetDataXSMB implements Runnable {


    private final Context context;

    private final Handler handler;


    private boolean update_available = false;
    private String whatNew = "";

    private Map<String, ArrayList<String>> data = new HashMap<>();
    private Map<String, ArrayList<String>> dataHit = new HashMap<>();
    private ArrayList<String> hitDe = new ArrayList<>();
    private ArrayList<String> hitDauDB = new ArrayList<>();
    private ArrayList<String> hitDauG1 = new ArrayList<>();
    private ArrayList<String> hitDuoiG1 = new ArrayList<>();
    private ArrayList<String> hitLo = new ArrayList<>();
    private ArrayList<String> hit3C = new ArrayList<>();
    private ArrayList<String> hitCangGiua = new ArrayList<>();
    private Listener listener;
    private String date;


    /**
     * Updater Runnable constructor
     *
     * @param handler              the handler to manage the UI in the specified
     * @param time_retry_to_update time in millis which represents the time after which the runnable called with force = false have to retry to check if an update exists
     */
    public GetDataXSMB(Context context, Handler handler, long time_retry_to_update) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * Updater Runnable constructor, when called in automatic way it runs once a day
     */
    public GetDataXSMB(Context context, Listener listener, String date) {
        this(context, null, 0);
        this.listener = listener;
        this.date = date;
    }


    public void start() {
        new Thread(this).start();
    }

    /**
     * Runs the asynchronous web  call and shows on the UI the Dialogs if required
     */
    @Override
    public void run() {
        // Extract from the Internet if an update is needed or not
        getData();
    }

    /**
     * Check cập nhật phiên bản mới
     *
     * @return true nếu app chưa phải là bản mới nhất trên store
     */
    private void getData() {
        try {
//            Element element = Jsoup.connect("https://www.xoso.net/getkqxs/mien-bac/" + "05-05-2020" + ".js")

            DateTime dt = new DateTime();
            DateTime dtVietNam = dt.withZone(DateTimeZone.forID("Asia/Ho_Chi_Minh"));
            //&& (dtVietNam.getHourOfDay() == 18 && dtVietNam.getMinuteOfDay() > 15 && dtVietNam.getMinuteOfDay() < 45))
            if (DateTimeUtils.convertDateToString(dtVietNam.toDate(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT).equalsIgnoreCase(date)
                    && (dtVietNam.getHourOfDay() == 18 && dtVietNam.getMinuteOfHour() > 15 && dtVietNam.getMinuteOfHour() < 45)) {
                Element element = Jsoup.connect("https://data.ketqua.net/pre_loads/kq-mb.raw")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("ttps://data.ketqua.net")
                        .get()
                        .body();
                if (element != null && !TextUtils.isEmpty(element.ownText())) {
                    String result = element.ownText() + ".";
                    String[] arrResult = result.split(";");
                    if (arrResult.length == 10) {
                        processAddData(arrResult);
                    } else {
                        listener.onSuccess(new HashMap<>(), new HashMap<>());
                    }
                } else {
                    listener.onSuccess(new HashMap<>(), new HashMap<>());
                }
            } else {
                Element element = Jsoup.connect("https://www.xoso.net/getkqxs/mien-bac/" + date + ".js")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("https://www.xoso.net")
                        .get()
                        .body();
                if (element != null && (element.getElementsByClass("giaidb") != null && element.getElementsByClass("giaidb").size() > 0
                        || element.getElementsByClass("giai1") != null && element.getElementsByClass("giai1").size() > 0
                        || element.getElementsByClass("giai2") != null && element.getElementsByClass("giai2").size() > 0
                        || element.getElementsByClass("giai3") != null && element.getElementsByClass("giai3").size() > 0
                        || element.getElementsByClass("giai4") != null && element.getElementsByClass("giai4").size() > 0
                        || element.getElementsByClass("giai5") != null && element.getElementsByClass("giai5").size() > 0
                        || element.getElementsByClass("giai6") != null && element.getElementsByClass("giai6").size() > 0
                        || element.getElementsByClass("giai7") != null && element.getElementsByClass("giai7").size() > 0)
                        && element.getElementsByClass("ngay") != null && element.getElementsByClass("ngay").get(0).ownText().contains(date.replaceAll("-", "/"))) {
                    processAddData(element);
                } else {
                    listener.onSuccess(new HashMap<>(), new HashMap<>());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            listener.onSuccess(new HashMap<>(), new HashMap<>());
        }
    }

    private void processAddData(Element element) {
        try {
            data.clear();
            dataHit.clear();
            hitDe.clear();
            hitDauDB.clear();
            hitDauG1.clear();
            hitDuoiG1.clear();
            hitLo.clear();
            hit3C.clear();
            hitCangGiua.clear();
            //Giải đặc biệt
            processDataGiaiDB(element, data, "giaidb");
            processDataGiai1(element, data, "giai1");
            processDataGiai2To7(element, data, "giai2");
            processDataGiai2To7(element, data, "giai3");
            processDataGiai2To7(element, data, "giai4");
            processDataGiai2To7(element, data, "giai5");
            processDataGiai2To7(element, data, "giai6");
            processDataGiai2To7(element, data, "giai7");

            dataHit.put(XSMBUtils.KEY_DE, hitDe);
            dataHit.put(XSMBUtils.KEY_DAUDB, hitDauDB);
            dataHit.put(XSMBUtils.KEY_DAUG1, hitDauG1);
            dataHit.put(XSMBUtils.KEY_DUOIG1, hitDuoiG1);
            dataHit.put(XSMBUtils.KEY_LO, hitLo);
            dataHit.put(XSMBUtils.KEY_3C, hit3C);
            dataHit.put(XSMBUtils.KEY_CANGGIUA, hitCangGiua);

//             lưu thông tin xổ số khi chưa được lưu lần nào
            long count = MyApp.getInstance().getDaoSession().getXSMBObjectDao().queryBuilder().where(XSMBObjectDao.Properties.Date.eq(date)).count();
            if (count == 0) {
                XSMBObject xsmbObject = new XSMBObject();
                xsmbObject.setDate(date);
                xsmbObject.setResult(new Gson().toJson(dataHit));
                MyApp.getInstance().getDaoSession().getXSMBObjectDao().save(xsmbObject);
            }

            listener.onSuccess(data, dataHit);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSuccess(new HashMap<>(), new HashMap<>());
        }
    }

    private void processAddData(String[] arrResult) {
        try {
            data.clear();
            dataHit.clear();
            hitDe.clear();
            hitDauDB.clear();
            hitDauG1.clear();
            hitDuoiG1.clear();
            hitLo.clear();
            hit3C.clear();
            hitCangGiua.clear();
            //Giải đặc biệt
            processDataGiaiDB(arrResult[9].replaceAll("\\.", ""), data, "giaidb");
            processDataGiai1(arrResult[8], data, "giai1");
            processDataGiai2To7(arrResult[7], data, "giai2");
            processDataGiai2To7(arrResult[6], data, "giai3");
            processDataGiai2To7(arrResult[5], data, "giai4");
            processDataGiai2To7(arrResult[4], data, "giai5");
            processDataGiai2To7(arrResult[3], data, "giai6");
            processDataGiai2To7(arrResult[2], data, "giai7");

            dataHit.put(XSMBUtils.KEY_DE, hitDe);
            dataHit.put(XSMBUtils.KEY_DAUDB, hitDauDB);
            dataHit.put(XSMBUtils.KEY_DAUG1, hitDauG1);
            dataHit.put(XSMBUtils.KEY_DUOIG1, hitDuoiG1);
            dataHit.put(XSMBUtils.KEY_LO, hitLo);
            dataHit.put(XSMBUtils.KEY_3C, hit3C);
            dataHit.put(XSMBUtils.KEY_CANGGIUA, hitCangGiua);

//             lưu thông tin xổ số khi chưa được lưu lần nào
//            long count = MyApp.getInstance().getDaoSession().getXSMBObjectDao().queryBuilder().where(XSMBObjectDao.Properties.Date.eq(date)).count();
//            if (count == 0) {
//                XSMBObject xsmbObject = new XSMBObject();
//                xsmbObject.setDate(date);
//                xsmbObject.setResult(new Gson().toJson(dataHit));
//                MyApp.getInstance().getDaoSession().getXSMBObjectDao().save(xsmbObject);
//            }

            listener.onSuccess(data, dataHit);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onSuccess(new HashMap<>(), new HashMap<>());
        }
    }

    private void processDataGiaiDB(Element element, Map<String, ArrayList<String>> data, String giai) {
        try {
            ArrayList<String> giaidb = new ArrayList<>();
            giaidb.add(element.getElementsByClass(giai).get(0).ownText().trim());
            data.put(giai, giaidb);

            hitDe.add(giaidb.get(0).substring(3));
            hitLo.add(giaidb.get(0).substring(3));
            hit3C.add(giaidb.get(0).substring(2));
            hitDauDB.add(giaidb.get(0).substring(0, 2));
            hitCangGiua.add(giaidb.get(0).substring(2, 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataGiaiDB(String result, Map<String, ArrayList<String>> data, String giai) {
        try {
            ArrayList<String> giaidb = new ArrayList<>();
            giaidb.add(result);
            data.put(giai, giaidb);
            if (!TextUtils.isEmpty(result)) {
                hitDe.add(giaidb.get(0).substring(3));
                hitLo.add(giaidb.get(0).substring(3));
                hit3C.add(giaidb.get(0).substring(2));
                hitDauDB.add(giaidb.get(0).substring(0, 2));
                hitCangGiua.add(giaidb.get(0).substring(2, 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataGiai1(Element element, Map<String, ArrayList<String>> data, String giai) {
        try {
            ArrayList<String> giai1 = new ArrayList<>();
            giai1.add(element.getElementsByClass(giai).get(0).ownText().trim());
            data.put(giai, giai1);

            hitDuoiG1.add(giai1.get(0).substring(3));
            hitLo.add(giai1.get(0).substring(3));
            hitDauG1.add(giai1.get(0).substring(0, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataGiai1(String result, Map<String, ArrayList<String>> data, String giai) {
        try {
            ArrayList<String> giai1 = new ArrayList<>();
            giai1.add(result);
            data.put(giai, giai1);

            if (!TextUtils.isEmpty(result)) {
                hitDuoiG1.add(giai1.get(0).substring(3));
                hitLo.add(giai1.get(0).substring(3));
                hitDauG1.add(giai1.get(0).substring(0, 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataGiai2To7(Element element, Map<String, ArrayList<String>> data, String giai) {
        try {
            String s = element.getElementsByClass(giai).get(0).ownText();
            String[] arr = s.split("-");
            ArrayList<String> listNum = new ArrayList<>();
            for (String s1 : arr) {
                listNum.add(s1.trim());
                if (!TextUtils.isEmpty(s1)) {
                    hitLo.add(s1.trim().substring(s1.trim().length() - 2));
                }
            }
            data.put(giai, listNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataGiai2To7(String result, Map<String, ArrayList<String>> data, String giai) {
        try {
            String[] arr = result.split("-");
            ArrayList<String> listNum = new ArrayList<>();
            for (String s1 : arr) {
                listNum.add(s1.trim().replace("*", ""));
                if (!TextUtils.isEmpty(s1.trim().replace("*", ""))) {
                    hitLo.add(s1.trim().substring(s1.trim().length() - 2));
                }
            }
            data.put(giai, listNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Listener {
        void onSuccess(Map<String, ArrayList<String>> data, Map<String, ArrayList<String>> dataHit);
    }

}