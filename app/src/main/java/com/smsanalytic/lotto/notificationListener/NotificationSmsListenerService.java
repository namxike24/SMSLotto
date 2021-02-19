package com.smsanalytic.lotto.notificationListener;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TraLoiTinNhan;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.DebtObject;
import com.smsanalytic.lotto.database.DebtObjectDao;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.entities.CountSmsSuccess;
import com.smsanalytic.lotto.entities.SmsSendObject;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.Content;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.balance.ChiTietCanChuyenActivity.getDateLotoByType;
import static com.smsanalytic.lotto.ui.balance.ChiTietCanChuyenActivity.getDateXienByType;
import static com.smsanalytic.lotto.ui.balance.GuiTinCanChuyenActivity.udpateTienGiuLai;
import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateAn;
import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateDanh;

public class NotificationSmsListenerService extends android.service.notification.NotificationListenerService {
    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */


    public static final String PUSH_NOTIFICAITON_KEY = "push_notificaiton_key";
    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        //    public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        //   public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
        public static final String ZALO_PACK_NAME = "com.zing.zalo";
        public static final String LINE_PACK_NAME = "com.linecorp.linelite";

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);

    }

    //List<String> smsList=new ArrayList<>();
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
        //    smsList.add(sbn.getNotification().tickerText.toString());
        //    new Handler().postDelayed(() -> {Toast.makeText(getApplicationContext(),TextUtils.join("====",smsList),Toast.LENGTH_LONG).show();},5000);
            SmsObject smsObject = matchNotificationCode(sbn);
            if (smsObject.getSmsType() != SmsType.SMS_OTHER) {
                if (sbn.getNotification().tickerText != null) {
                    if (sbn.getNotification().tickerText.toString().contains(":")) {
                        if (MyApp.replyHaspMap == null) {
                            MyApp.replyHaspMap = new HashMap<>();
                            MyApp.replyHaspMap.put(smsObject.getIdAccouunt(), sbn);
                        } else {
                            MyApp.replyHaspMap.put(smsObject.getIdAccouunt(), sbn);
                        }
                        if (xulyTinGiong(smsObject)) {
                            // xử lý tin nhắn
                            AccountObject customer = null;
                            List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
                            if (accountList.size() > 0) {
                                customer = accountList.get(0);
                            }
                            List<String> strings = new ArrayList<>();
                            if (phanTichTuDong(customer)) {
                                SmsSuccessObject smsSuccessObject;
                                smsSuccessObject = ProcessSms.processSms(smsObject.getSmsRoot(), customer);
                                // nếu tin nhắn đó của khách hàng thì mới nhận xử lý
                                if (customer != null) {
                                    if (smsSuccessObject.isProcessSuccess()) {
                                        strings = xuLyTinQuaGio(customer, smsSuccessObject.getContents());
                                        smsSuccessObject = ProcessSms.processSms(strings.get(0), customer);
                                        if (smsSuccessObject.isProcessSuccess()) {
                                            smsObject.setSuccess(true);
                                            smsObject.setSmsProcessed(smsSuccessObject.getValue());
                                            for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                                                lotoNumberObject.setGuid(smsObject.getGuid());
                                                lotoNumberObject.setAccountSend(smsObject.getIdAccouunt());
                                                lotoNumberObject.setDateTake(smsObject.getDate());
                                                lotoNumberObject.setDateString(Common.convertDateByFormat(smsObject.getDate(), Common.FORMAT_DATE_DD_MM_YYY_2));
                                                lotoNumberObject.setSmsStatus(smsObject.getSmsStatus());
                                                ((MyApp) getApplication()).getDaoSession().getLotoNumberObjectDao().save(lotoNumberObject);
                                            }
                                            // tự động gửi đên người nhân
                                            chuyenTuDongChuyenTNchoNguoiKhac(customer, smsObject);

                                            // xử lý công nợ
                                            processDebt(((MyApp) getApplication()).getDaoSession(), smsSuccessObject.getListDataLoto(), customer,Common.getTypeTienTe(getApplicationContext()));


                                        } else {
                                            smsObject.setSuccess(false);
                                        }
                                    } else {
                                        smsObject.setSuccess(false);
                                    }
                                }
                            }
                            if (strings.size() > 1) {
                                if (strings.get(1).isEmpty()) {
                                    ((MyApp) getApplication()).getDaoSession().getSmsObjectDao().save(smsObject);
                                } else {
                                    if (strings.get(0).isEmpty()) {
                                        // tin quá giờ
                                        SmsObject smsObjectQuaGio = (SmsObject) smsObject.clone();
                                        smsObjectQuaGio.setSmsRoot(strings.get(1) + " (Tin quá giờ)");
                                        smsObjectQuaGio.setSmsProcessed(strings.get(1) + " (Tin quá giờ)");
                                        smsObjectQuaGio.setSuccess(false);
                                        smsObjectQuaGio.setGuid(UUID.randomUUID().toString());
                                        ((MyApp) getApplication()).getDaoSession().getSmsObjectDao().save(smsObjectQuaGio);
                                        traLaiTinQuaGio(smsObjectQuaGio, customer, MyApp.getInstance().getDaoSession());

                                    } else {
                                        // tin quá giờ
                                        SmsObject smsObjectQuaGio = (SmsObject) smsObject.clone();
                                        smsObjectQuaGio.setSmsRoot(strings.get(1) + " (Tin quá giờ)");
                                        smsObjectQuaGio.setSmsProcessed(strings.get(1) + " (Tin quá giờ)");
                                        smsObjectQuaGio.setSuccess(false);
                                        smsObjectQuaGio.setGuid(UUID.randomUUID().toString());
                                        ((MyApp) getApplication()).getDaoSession().getSmsObjectDao().save(smsObjectQuaGio);
                                        ((MyApp) getApplication()).getDaoSession().getSmsObjectDao().save(smsObject);
                                        traLaiTinQuaGio(smsObjectQuaGio, customer, MyApp.getInstance().getDaoSession());
                                    }
                                }

                            } else {
                                ((MyApp) getApplication()).getDaoSession().getSmsObjectDao().save(smsObject);
                            }
                            if (smsObject.getIsSuccess()) {
                                // gửi lại nhắn nếu xử lý thành công
//                                processReplySMS(smsObject, customer);
                            }
                            // tự động cân chuyển
                            tuDongCanChuyen(MyApp.getInstance().getDaoSession(), smsObject);
//
//
                            EventBus.getDefault().post(new PushNotificationEvent());
                        }
                    }
                }
            }


        } catch (
                Exception e) {
            Common.sendMailLogData(MyApp.getContext(), e);
        }

    }


    public static void traLaiTinQuaGio(SmsObject smsObjects, AccountObject accountObject, DaoSession daoSession) {

        try {
            AccountSetting accountSetting = new Gson().fromJson(accountObject.getAccountSetting(), AccountSetting.class);
            if (accountSetting.getTudongtralaitinkhiquagionhan() == 2) {
                SmsObject smsObjectClone = (SmsObject) smsObjects.clone();
                smsObjectClone.setSmsStatus(SmsStatus.SMS_SENT);
                SmsObject result = ProcessSms.replySMS(MyApp.getContext(), smsObjectClone, smsObjectClone.getSmsRoot(), daoSession, true);
                if (result == null) {
                    if (!accountObject.getPhone().isEmpty()) {
                        String phone = accountObject.getPhone().split(",")[0];
                        Common.sendSmsAuto(smsObjectClone.getSmsProcessed(), phone);
                        smsObjectClone.setGuid(UUID.randomUUID().toString());
                        daoSession.getSmsObjectDao().insert(smsObjectClone);
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    public static void processReplySMS(SmsObject smsObject, AccountObject customer) {
        try {
            if (PreferencesManager.getInstance().getValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), "").isEmpty()) {
                CountSmsSuccess countSmsSuccess = new CountSmsSuccess(Common.getCurrentTime(), 1);
                PreferencesManager.getInstance().setValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), new Gson().toJson(countSmsSuccess));
            } else {
                String dataCache = PreferencesManager.getInstance().getValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), "");
                CountSmsSuccess countSmsSuccess = new Gson().fromJson(dataCache, CountSmsSuccess.class);
                if (countSmsSuccess.getDate().equals(Common.getCurrentTime())) {
                    countSmsSuccess.setCount(countSmsSuccess.getCount() + 1);
                    PreferencesManager.getInstance().setValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), new Gson().toJson(countSmsSuccess));
                } else {
                    CountSmsSuccess countSmsSuccessNew = new CountSmsSuccess(Common.getCurrentTime(), 1);
                    PreferencesManager.getInstance().setValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), new Gson().toJson(countSmsSuccessNew));
                }
            }
            AccountSetting accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
            switch (accountSetting.getTraloitinnhan()) {
                case TraLoiTinNhan.DEM_TIN_XU_LY_THANH_CONG:
                    String dataCache = PreferencesManager.getInstance().getValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), "");
                    CountSmsSuccess countSmsSuccess = new Gson().fromJson(dataCache, CountSmsSuccess.class);
                    if (countSmsSuccess != null) {
                        String content = "Đã nhận " + countSmsSuccess.getCount() + " tin ";
                        if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                            guiDenKHSms(customer, smsObject, content);
                        } else {
                            ProcessSms.replySMS(MyApp.getContext(), smsObject, content, MyApp.getInstance().getDaoSession(), false);
                        }

                    }
                    break;
                case TraLoiTinNhan.TRICH_SAU_XU_LY:
                    String content = smsObject.getSmsProcessed() + "\n OK";
                    if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                        guiDenKHSms(customer, smsObject, content);
                    } else {
                        ProcessSms.replySMS(MyApp.getContext(), smsObject, content, MyApp.getInstance().getDaoSession(), false);
                    }
                    break;
                 case TraLoiTinNhan.OK:
                     String content3 ="OK";
                     if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                         guiDenKHSms(customer, smsObject, content3);
                     } else {
                         ProcessSms.replySMS(MyApp.getContext(), smsObject, content3, MyApp.getInstance().getDaoSession(), false);
                     }
                     break;
                case TraLoiTinNhan.OK_SO_TIN:
                    String dataCache1 = PreferencesManager.getInstance().getValue(PreferencesManager.COUNT_SMS_SUCCES + smsObject.getIdAccouunt(), "");
                    CountSmsSuccess countSmsSuccess1 = new Gson().fromJson(dataCache1, CountSmsSuccess.class);
                    if (countSmsSuccess1!=null){
                        String content4 ="OK "+ countSmsSuccess1.getCount() +" tin" ;
                        if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                            guiDenKHSms(customer, smsObject, content4);
                        } else {
                            ProcessSms.replySMS(MyApp.getContext(), smsObject, content4, MyApp.getInstance().getDaoSession(), false);
                        }
                    }
                    break;
                case TraLoiTinNhan.TRICH_NOI_DUNG_GUI:
                    String content1 = smsObject.getSmsRoot() + "\n OK";
                    if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                        guiDenKHSms(customer, smsObject, content1);
                    } else {
                        ProcessSms.replySMS(MyApp.getContext(), smsObject, content1, MyApp.getInstance().getDaoSession(), false);
                    }

                    break;
            }

        } catch (Exception e) {
        }
    }

    public static void guiDenKHSms(AccountObject customer, SmsObject smsObject, String content) {
//        smsObject.setSmsRoot(content);
//        smsObject.setSmsProcessed(content);
//        smsObject.setIsSuccess(false);
//        String phone = customer.getIdAccount();
//        Common.sendSmsAuto(smsObject.getSmsProcessed(), phone);
//        smsObject.setGuid(UUID.randomUUID().toString());
//        MyApp.getInstance().getDaoSession().getSmsObjectDao().insert(smsObject);
    }


    public static List<String> xuLyTinQuaGio(AccountObject customer, ArrayList<Content> valueList) {
        List<String> list = new ArrayList<>();
        SettingDefault settingDefault;
        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(MyApp.getContext(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault != null) {
            List<String> smsDungGio = new ArrayList<>();
            List<String> smsQuaGio = new ArrayList<>();
            for (Content content : valueList) {
                long timeSMS = Common.convertHHMMtoMilisecond(Common.convertDateByFormat(content.getDate(), Common.FORMAT_DATE_HH_MM));
                if (TypeEnum.isTypeLoXienG1(content.getType())) {
                    if (timeSMS > settingDefault.getTimeKhongNhanLo()) {
                        content.setQuaGio(true);
                        smsQuaGio.add(content.getValue());
                    } else {
                        content.setQuaGio(false);
                        smsDungGio.add(content.getValue());
                    }
                } else if (TypeEnum.isTypeDeCang(content.getType())) {
                    if (timeSMS > settingDefault.getTimeKhongNhanDe()) {
                        content.setQuaGio(true);
                        smsQuaGio.add(content.getValue());
                    } else {
                        content.setQuaGio(false);
                        smsDungGio.add(content.getValue());
                    }
                }
            }
            list.add(0, TextUtils.join(".", smsDungGio).trim());
            list.add(1, TextUtils.join(".", smsQuaGio).trim());

        }
        return list;
    }

    /**
     * hàm udpate công nợ sau khi xử lý tin thành công
     *
     * @param daoSession
     * @param listDataLoto
     * @param accountObject
     */
    public static void processDebt(DaoSession daoSession, ArrayList<LotoNumberObject> listDataLoto, AccountObject accountObject, int typeTienTe) {
        if (listDataLoto != null && !listDataLoto.isEmpty()) {
            double tiendanh = 0;
            double tienan = 0;
            double ketqua = 0;
            AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
            AccountSetting accountSetting= new Gson().fromJson(accountObject.getAccountSetting(),AccountSetting.class);
            for (LotoNumberObject numberObject : listDataLoto) {
                double money = numberObject.getMoneyTake();
                double danh = money * getRateDanh(numberObject, accountRate, typeTienTe);
                double an = 0;
                if (numberObject.isHit()) {
                    if (numberObject.getType() == TypeEnum.TYPE_LO) {
                        an = money * getRateAn(numberObject, accountRate, typeTienTe) * numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida());
                    } else {
                        an = money * getRateAn(numberObject, accountRate, typeTienTe);
                    }

                }
                tiendanh += danh;
                tienan += an;
                ketqua += numberObject.getSmsStatus() == SmsStatus.SMS_RECEIVE ? an - danh : danh - an;
            }
            long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
            double oldDebtMoney = 0;
            List<DebtObject> oldDebtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()),
                    DebtObjectDao.Properties.Date.between(0, currentDate)).orderDesc(DebtObjectDao.Properties.Date).limit(1).list();
            if (oldDebtObjectList.size() > 0) {
                oldDebtMoney = oldDebtObjectList.get(0).getExpenses() + oldDebtObjectList.get(0).getOldebt() + oldDebtObjectList.get(0).getMoneyReceived() + oldDebtObjectList.get(0).getMoneyPay();
            }

            Long endDate = Common.addHourToDate(currentDate, 24);
            List<DebtObject> debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), DebtObjectDao.Properties.Date.between(currentDate, endDate)).list();
            if (debtObjectList.size() > 0) {
                double expenses = debtObjectList.get(0).getExpenses() + ketqua;
                debtObjectList.get(0).setExpenses(expenses);
                debtObjectList.get(0).setOldebt(oldDebtMoney);
                daoSession.getDebtObjectDao().update(debtObjectList.get(0));
            } else {
                DebtObject debtObject = new DebtObject(Common.getCurrentTimeLong(), accountObject.getIdAccount(), accountObject.getAccountName(), ketqua, 0, 0, 0);
                debtObject.setOldebt(oldDebtMoney);
                daoSession.getDebtObjectDao().insert(debtObject);
            }

        }

    }

    /**
     * hàm udpate công nợ sau khi xử lý tin thành công
     *
     * @param daoSession
     * @param listDataLoto
     * @param accountObject
     */
    public static void processDeleteDebt(DaoSession daoSession, ArrayList<LotoNumberObject> listDataLoto, AccountObject accountObject, int typeTienTe) {
        if (listDataLoto != null && !listDataLoto.isEmpty()) {
            double tiendanh = 0;
            double tienan = 0;
            double ketqua = 0;
            AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
            for (LotoNumberObject numberObject : listDataLoto) {
                double money = numberObject.getMoneyTake();
                double danh = money * getRateDanh(numberObject, accountRate,typeTienTe);
                double an = 0;
                if (numberObject.isHit()) {
                    an = money * getRateAn(numberObject, accountRate,typeTienTe);
                }
                tiendanh += danh;
                tienan += an;
                ketqua += numberObject.getSmsStatus() == SmsStatus.SMS_RECEIVE ? an - danh : danh - an;
            }
            long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
            double oldDebtMoney = 0;
            List<DebtObject> oldDebtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()),
                    DebtObjectDao.Properties.Date.between(0, currentDate)).orderDesc(DebtObjectDao.Properties.Date).limit(1).list();
            if (oldDebtObjectList.size() > 0) {
                oldDebtMoney = oldDebtObjectList.get(0).getExpenses() + oldDebtObjectList.get(0).getOldebt() + oldDebtObjectList.get(0).getMoneyReceived() + oldDebtObjectList.get(0).getMoneyPay();
            }

            Long endDate = Common.addHourToDate(currentDate, 24);
            List<DebtObject> debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), DebtObjectDao.Properties.Date.between(currentDate, endDate)).list();
            if (debtObjectList.size() > 0) {
                double expenses = debtObjectList.get(0).getExpenses() - ketqua;
                debtObjectList.get(0).setExpenses(expenses);
                debtObjectList.get(0).setOldebt(oldDebtMoney);
                daoSession.getDebtObjectDao().update(debtObjectList.get(0));
            }
        }

    }


    private SmsObject matchNotificationCode(StatusBarNotification sbn) {
        try {
            String packageName = sbn.getPackageName();
            String title = "";
            String sms = "";
            String key = "";
            if (packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)) {
                if (sbn.getNotification().extras.get(Notification.EXTRA_TITLE) != null) {
                    title = sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString();
                }
                if (sbn.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                    sms = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
                }
                if (sbn.getTag() != null) {
                    key = sbn.getTag();
                }
                SmsObject smsObject = new SmsObject(title, sms, sms, SmsType.SMS_FB, key, sbn.getPostTime(), SmsStatus.SMS_RECEIVE);
                smsObject.setGuid(UUID.randomUUID().toString());
                return smsObject;
            } else if (packageName.equals(ApplicationPackageNames.ZALO_PACK_NAME)) {
              //  Log.e("==bbb==",sbn.getNotification().extras.toString());
             //   smsList.add(sbn.getNotification().extras.toString());
              //  new Handler().postDelayed(() ->Common.sendMailLogDatas(getBaseContext(),TextUtils.join("=====\n",smsList)) ,3000);
//                String conent=sbn.getGroupKey()+"==Noi dung=="+ sbn.getNotification().tickerText+" EXTRAR"+sbn.getNotification().extras.toString();
//
                if (sbn.getNotification().extras.get(Notification.EXTRA_TITLE) != null) {
                    title = sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString().split("\\(")[0].trim();
                }
                if (sbn.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                    sms = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
                }
                if (sbn.getGroupKey() != null) {
                    key = sbn.getGroupKey();

                }
                SmsObject smsObject = new SmsObject(title, sms, sms, SmsType.SMS_ZALO, key, sbn.getPostTime(), SmsStatus.SMS_RECEIVE);

                smsObject.setIsSuccess(false);
                smsObject.setGuid(UUID.randomUUID().toString());
                return smsObject;
            } else {
                return new SmsObject();
            }

        } catch (Exception e) {
        }
        return new SmsObject();
    }

    private String TAG = this.getClass().getSimpleName();
    List<String> smsList= new ArrayList<>();
    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onListenerConnected() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getActiveNotifications();
            }
        } catch (Exception e) {
            Common.sendMailLogData(MyApp.getContext(), e);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onListenerDisconnected() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Notification listener disconnected - requesting rebind
                requestRebind(new ComponentName(this, NotificationListenerService.class));
            }
        } catch (Exception e) {
            Common.sendMailLogData(MyApp.getContext(), e);
        }
    }

    public void tryReconnectService() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ComponentName componentName =
                        new ComponentName(getApplicationContext(), NotificationSmsListenerService.class);

                //It say to Notification Manager RE-BIND your service to listen notifications again inmediatelly!
                requestRebind(componentName);
            }
        } catch (Exception e) {
            Common.sendMailLogData(MyApp.getContext(), e);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d(TAG, "Notification service onStartCommandCalled");
            if (intent != null) {
                tryReconnectService();//switch on/off component and rebind
            }
            //START_STICKY  to order the system to restart your service as soon as possible when it was killed.
            return START_STICKY;
        } catch (Exception e) {
            Common.sendMailLogData(MyApp.getContext(), e);
        }
        return START_STICKY;
    }

    public static boolean xulyTinGiong(SmsObject smsObject) {
        boolean result = true;
        SettingDefault settingDefault;
        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(MyApp.getInstance(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }

        if (settingDefault != null) {
            if (settingDefault.isTuDongBoTinGiong()) {
                long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
                long endDate = Common.addHourToDate(currentDate, 24);
                List<SmsObject> smsObjectList = MyApp.getInstance().getDaoSession().getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(smsObject.getIdAccouunt()),
                        SmsObjectDao.Properties.Date.between(currentDate, endDate), SmsObjectDao.Properties.SmsRoot.eq(smsObject.getSmsRoot())
                ).list();
                if (smsObjectList.size() > 0) {
                    result = false;
                }
            }
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static NotificationWear getNotificationWear(StatusBarNotification statusBarNotification) {
        NotificationWear notificationWear = new NotificationWear();
        Bundle bundle = statusBarNotification.getNotification().extras;
        notificationWear.setPackageName(statusBarNotification.getPackageName());
        notificationWear.setTag(statusBarNotification.getTag());
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if ("android.wearable.EXTENSIONS".equals(key)) {
                Bundle wearBundle = ((Bundle) value);
                for (String keyInner : wearBundle.keySet()) {
                    Object valueInner = wearBundle.get(keyInner);
                    if (keyInner != null && valueInner != null) {
                        if ("actions".equals(keyInner) && valueInner instanceof ArrayList) {
                            ArrayList<Notification.Action> actions = new ArrayList<>();
                            actions.addAll((ArrayList) valueInner);
                            for (Notification.Action act : actions) {
                                if (act.getRemoteInputs() != null) {
                                    //API > 20 needed
                                    notificationWear.setPendingIntent(act.actionIntent);
//
                                    notificationWear.getRemoteInputs().addAll(Arrays.asList(act.getRemoteInputs()));
                                    notificationWear.setBundle(wearBundle);
                                    return notificationWear;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }


    public static void chuyenTuDongChuyenTNchoNguoiKhac(AccountObject accountObject, SmsObject smsObjects) {
        try {
            SmsObject smsObjectClone = (SmsObject) smsObjects.clone();
            smsObjectClone.setSmsStatus(SmsStatus.SMS_SENT);
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            startDate.set(Calendar.HOUR_OF_DAY, 15);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 45);
            endDate.set(Calendar.SECOND, 0);
            endDate.set(Calendar.MILLISECOND, 0);
            if (smsObjectClone.getDate() > startDate.getTimeInMillis() && smsObjectClone.getDate() < endDate.getTimeInMillis()) {
                if (accountObject != null) {
                    AccountSetting accountSetting = new Gson().fromJson(accountObject.getAccountSetting(), AccountSetting.class);
                    if (accountSetting.getTudongchuyenditinden() == 2 || accountSetting.getTudongchuyenditinden() == 3) {
                        if (!accountSetting.getIdAccountNhanChuyen().isEmpty()) {
                            smsObjectClone.setIdAccouunt(accountSetting.getIdAccountNhanChuyen());
                            SmsObject smsObject1 = ProcessSms.replySMS(MyApp.getContext(), smsObjectClone, smsObjectClone.getSmsRoot(), MyApp.getInstance().getDaoSession(), false);
                            // nếu không gửi đc qua mạng xa hội mà account có số điện thoại thfi gửi qua sms
                            if (smsObject1 == null) {
                                if (!accountObject.getPhone().isEmpty()) {
                                    String phone = accountObject.getPhone().split(",")[0];
                                    Common.sendSmsAuto(smsObjectClone.getSmsProcessed(), phone);
                                    smsObjectClone.setGuid(UUID.randomUUID().toString());
                                    MyApp.getInstance().getDaoSession().getSmsObjectDao().insert(smsObjectClone);
                                    saveLotoWithSMSTypeSent(smsObjectClone);
                                }
                            } else {
                                saveLotoWithSMSTypeSent(smsObjectClone);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    public static void saveLotoWithSMSTypeSent(SmsObject smsObject) {
        AccountObject customer = null;
        List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
        if (accountList.size() > 0) {
            customer = accountList.get(0);
        }
        SmsSuccessObject smsSuccessObject = ProcessSms.processSms(smsObject.getSmsRoot(), customer);
        if (smsSuccessObject.isProcessSuccess()) {
            smsObject.setSuccess(true);
            smsObject.setSmsProcessed(smsSuccessObject.getValue());
            for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                lotoNumberObject.setGuid(smsObject.getGuid());
                lotoNumberObject.setAccountSend(smsObject.getIdAccouunt());
                lotoNumberObject.setDateTake(smsObject.getDate());
                lotoNumberObject.setDateString(Common.convertDateByFormat(smsObject.getDate(), Common.FORMAT_DATE_DD_MM_YYY_2));
                lotoNumberObject.setSmsStatus(smsObject.getSmsStatus());
                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().save(lotoNumberObject);
            }
        }
    }


    public static boolean phanTichTuDong(AccountObject customer) {
        if (customer != null) {
            AccountSetting accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
            if (accountSetting.getPhantichtindendi() == 1) {
                return true;
            }
        }
        return false;
    }


    public static void tuDongCanChuyen(DaoSession daoSession, SmsObject smsObject) {
        List<SmsSendObject> smsSend = new ArrayList<>();
        SettingDefault settingDefault;
        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(MyApp.getContext(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault != null) {
            String donvi =TienTe.getKeyTienTe(settingDefault.getTiente());
            if (settingDefault.isTuDongCanChuyen()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(smsObject.getDate());
                String dateFormat = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                long dateOfSms = Common.convertHHMMtoMilisecond(dateFormat);
                if (settingDefault.getTimeCanChuyenDeTu() < dateOfSms && settingDefault.getTimeCanChuyenDeDen() > dateOfSms) {
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DAUDB,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DAUDB,donvi));
                    }
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DE,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DE,donvi));
                    }
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_CANGGIUA,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_CANGGIUA,donvi));
                    }
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_3C,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_3C,donvi));
                    }
                }
                if (settingDefault.getTimeCanChuyenLoTu() < dateOfSms && settingDefault.getTimeCanChuyenLoDen() > dateOfSms) {
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DITNHAT,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DITNHAT,donvi));
                    }
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DAUNHAT,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_DAUNHAT,donvi));
                    }
                    if (tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_LO,donvi).size() > 0) {
                        smsSend.addAll(tuDongCanChuyenDe(settingDefault, daoSession, TypeEnum.TYPE_LO,donvi));
                    }

                    if (tuCanChuyenXien(settingDefault, daoSession).size() > 0) {
                        smsSend.addAll(tuCanChuyenXien(settingDefault, daoSession));
                    }
                }

                boolean sendSuccess = false;
                SmsObject smsObjectNew = null;
                if (settingDefault.getNguoiNhanCanChuyen() != null) {
                    AccountObject accountObject = new Gson().fromJson(settingDefault.getNguoiNhanCanChuyen(), AccountObject.class);
                    int smsType = SmsType.SMS_NORMAL;
                    if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_FB) {
                        smsType = SmsType.SMS_FB;
                    } else if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_ZALO) {
                        smsType = SmsType.SMS_ZALO;
                    }
                    Collections.sort(smsSend, (c1, c2) -> -Double.compare(c1.getMoney(), c2.getMoney()));
                    List<String> data= new ArrayList<>();
                    int typeCompare = -1;
                    for (SmsSendObject smsSendObject : smsSend){
                        if (typeCompare==smsSendObject.getType()){
                            data.add(smsSendObject.getContent().replace(TypeEnum.getStringByType3(typeCompare),"").trim());
                        }
                        else{
                            data.add(smsSendObject.getContent());
                            typeCompare=smsSendObject.getType();
                        }

                    }
                    smsObjectNew = new SmsObject(accountObject.getIdAccount(), TextUtils.join(".", data), TextUtils.join(".", data), smsType, accountObject.getIdAccount(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
                    SmsObject smsObject1 = ProcessSms.replySMS(MyApp.getContext(), smsObjectNew, smsObjectNew.getSmsProcessed(), daoSession, true);
                    // nếu không gửi đc qua mạng xa hội mà account có số điện thoại thfi gửi qua sms
                    if (smsObject1 == null) {
                        if (!accountObject.getPhone().isEmpty()) {
                            String phone = accountObject.getPhone().split(",")[0];
                            Common.sendSmsAuto(smsObject.getSmsProcessed(), phone);
                            daoSession.getSmsObjectDao().insert(smsObject);
                            sendSuccess = true;
                        }
                    } else {
                        sendSuccess = true;
                    }
                }
                if (sendSuccess) {
                    SmsSuccessObject smsSuccessObject = ProcessSms.processSms(smsObjectNew.getSmsRoot());
                    if (smsSuccessObject.isProcessSuccess()) {
                        smsObjectNew.setSuccess(true);
                        smsObjectNew.setSmsProcessed(smsSuccessObject.getValue());
                        for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                            lotoNumberObject.setGuid(smsObjectNew.getGuid());
                            lotoNumberObject.setAccountSend(smsObjectNew.getIdAccouunt());
                            lotoNumberObject.setSmsStatus(smsObjectNew.getSmsStatus());
                            daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                            udpateTienGiuLai(daoSession, lotoNumberObject);
                        }
                    } else {
                        smsObject.setSuccess(false);
                    }

                }
            }
        }

    }


    public static List<SmsSendObject> tuDongCanChuyenDe(SettingDefault settingDefault, DaoSession daoSession, int type,String donvi) {
        List<SmsSendObject> smsSend = new ArrayList<>();

        double giulaimax = 0;
        switch (type) {
            case TypeEnum.TYPE_DE:
            case TypeEnum.TYPE_DAUDB:
                giulaimax = settingDefault.getdBGiuLaiMax();
                break;
            case TypeEnum.TYPE_DITNHAT:
                giulaimax = settingDefault.getGiaiNhatGiuLaiMax();
                break;
            case TypeEnum.TYPE_3C:
                giulaimax = settingDefault.getBaSoGiuLaiMax();
                break;
            case TypeEnum.TYPE_LO:
                giulaimax = settingDefault.getLoGiuaLaimax();
                break;
        }

        List<LotoNumberObject> dbList = getDateLotoByType(daoSession, type, SmsStatus.SMS_RECEIVE);
        List<LotoNumberObject> dbDaChuyen = getDateLotoByType(daoSession, type, SmsStatus.SMS_SENT);
        for (LotoNumberObject lotoNhanVe : dbList) {
            for (LotoNumberObject lotoDaChuyen : dbDaChuyen) {
                if (lotoNhanVe.getValue1().trim().equals(lotoDaChuyen.getValue1().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }
        for (LotoNumberObject s : dbList) {
            double giulai = 0;
            double sechuyen = 0;
            giulai = giulaimax;
            sechuyen = s.getMoneyTake() - giulai - s.getMoneySend();

            // sẽ chyển  >0 => hiển thị dòng đó trong table
            if (sechuyen > 0) {
                s.setSeChuyen(sechuyen);
                s.setMoneyKeep(giulai);
            } else {
                s.setSeChuyen(0);
                s.setMoneyKeep(s.getMoneyTake());
            }

        }
        if (!ProcessSms.groupSmsToSent(dbList, type,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(dbList, type,donvi));
        }

        return smsSend;
    }


    public static List<SmsSendObject> tuCanChuyenXien(SettingDefault settingDefault, DaoSession daoSession) {
        List<SmsSendObject> smsSend = new ArrayList<>();
        String donvi= TienTe.getKeyTienTe(settingDefault.getTiente());
        List<LotoNumberObject> xienListTotal = new ArrayList<>();
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_RECEIVE));
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_RECEIVE));
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_RECEIVE));

        List<LotoNumberObject> xienListDaChuyen = new ArrayList<>();
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_SENT));
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_SENT));
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_SENT));

        for (LotoNumberObject lotoNhanVe : xienListTotal) {
            for (LotoNumberObject lotoDaChuyen : xienListDaChuyen) {
                if (lotoNhanVe.getXienFormat().trim().equals(lotoDaChuyen.getXienFormat().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }
        for (LotoNumberObject s : xienListTotal) {
            double giulaimax = 0;
            double giulai = 0;
            double sechuyen = 0;
            Double giualaingoaile = null;
            if (s.getType() == TypeEnum.TYPE_XIEN2) {
                giulaimax = settingDefault.getX2GiulaiMax();
                s.setValue1(s.getValue1() + "-" + s.getValue2());
            } else if (s.getType() == TypeEnum.TYPE_XIEN3) {
                giulaimax = settingDefault.getX3GiulaiMax();
                s.setValue1(s.getValue1() + "-" + s.getValue2() + "-" + s.getValue3());
            } else if (s.getType() == TypeEnum.TYPE_XIEN4) {
                giulaimax = settingDefault.getX4GiulaiMax();
                s.setValue1(s.getValue1() + "-" + s.getValue2() + "-" + s.getValue3() + "-" + s.getValue4());
            }

            giulai = giulaimax;
            sechuyen = s.getMoneyTake() - giulai - s.getMoneySend();
            // trường hợp ngoại lệ mà !=null => theo giá trị ngoại lệ
            if (giualaingoaile != null) {
                giulai = giualaingoaile;
                sechuyen = (s.getMoneyTake() - s.getMoneySend()) - giulai;
            }
            // sẽ chyển  >0 => hiển thị dòng đó trong table
            if (sechuyen > 0) {
                s.setSeChuyen(sechuyen);
                s.setMoneyKeep(giulai);
            } else {
                s.setSeChuyen(0);
                s.setMoneyKeep(s.getMoneyTake());
            }

        }

        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN2,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN2,donvi));
        }
        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN3,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN3,donvi));
        }
        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN4,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN4,donvi));
        }
        return smsSend;

    }
}
