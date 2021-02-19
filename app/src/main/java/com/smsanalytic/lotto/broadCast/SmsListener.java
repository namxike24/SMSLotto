package com.smsanalytic.lotto.broadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.chuyenTuDongChuyenTNchoNguoiKhac;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.phanTichTuDong;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDebt;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.traLaiTinQuaGio;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.tuDongCanChuyen;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.xuLyTinQuaGio;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.xulyTinGiong;

public class SmsListener extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.e("Sms", "receive");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = "";
            String msgBody = "";
            long time = 0;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody += msgs[i].getMessageBody();
                        time = msgs[i].getTimestampMillis();
                    }
                    if (!TextUtils.isEmpty(msgBody)) {
                        saveSms(context, msg_from.replace("+84", "0"), msgBody, time);
                    }
                } catch (Exception e) {
                    Log.e("Sms", e.getMessage());
                }
            }
        }
    }

    private void saveSms(Context context, String msg_from, String msgBody, long time) {
        try {
            String groupTitle = Common.contactExists(context, msg_from);
            if (groupTitle != null) {
                groupTitle = groupTitle.trim();
            }
            SmsObject smsObject = new SmsObject(groupTitle != null ? groupTitle : msg_from, msgBody, msgBody, SmsType.SMS_NORMAL, msg_from, time, SmsStatus.SMS_RECEIVE);
            smsObject.setGuid(UUID.randomUUID().toString());

            smsObject.setIsSuccess(false);
            // xử lý tin nhắn

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
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().save(lotoNumberObject);
                                }
                                // tự động gửi đên người nhân
                                chuyenTuDongChuyenTNchoNguoiKhac(customer, smsObject);

                                // xử lý công nợ
                                processDebt(MyApp.getInstance().getDaoSession(), smsSuccessObject.getListDataLoto(), customer, Common.getTypeTienTe(MyApp.getContext()));


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
                        MyApp.getInstance().getDaoSession().getSmsObjectDao().save(smsObject);
                    } else {
                        if (strings.get(0).isEmpty()) {
                            // tin quá giờ
                            SmsObject smsObjectQuaGio = (SmsObject) smsObject.clone();
                            smsObjectQuaGio.setSmsRoot(strings.get(1) + " (Tin quá giờ)");
                            smsObjectQuaGio.setSmsProcessed(strings.get(1) + " (Tin quá giờ)");
                            smsObjectQuaGio.setSuccess(false);
                            smsObjectQuaGio.setGuid(UUID.randomUUID().toString());
                            MyApp.getInstance().getDaoSession().getSmsObjectDao().save(smsObjectQuaGio);
                            traLaiTinQuaGio(smsObjectQuaGio, customer, MyApp.getInstance().getDaoSession());

                        } else {
                            // tin quá giờ
                            SmsObject smsObjectQuaGio = (SmsObject) smsObject.clone();
                            smsObjectQuaGio.setSmsRoot(strings.get(1) + " (Tin quá giờ)");
                            smsObjectQuaGio.setSmsProcessed(strings.get(1) + " (Tin quá giờ)");
                            smsObjectQuaGio.setSuccess(false);
                            smsObjectQuaGio.setGuid(UUID.randomUUID().toString());
                            MyApp.getInstance().getDaoSession().getSmsObjectDao().save(smsObjectQuaGio);
                            MyApp.getInstance().getDaoSession().getSmsObjectDao().save(smsObject);
                            traLaiTinQuaGio(smsObjectQuaGio, customer, MyApp.getInstance().getDaoSession());
                        }
                    }

                } else {
                    MyApp.getInstance().getDaoSession().getSmsObjectDao().save(smsObject);
                }
                if (smsObject.getIsSuccess()) {
                    // gửi lại nhắn nếu xử lý thành công
//                    processReplySMS(smsObject, customer);
                }
                // tự động cân chuyển
                tuDongCanChuyen(MyApp.getInstance().getDaoSession(), smsObject);
//
//
                EventBus.getDefault().post(new PushNotificationEvent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}