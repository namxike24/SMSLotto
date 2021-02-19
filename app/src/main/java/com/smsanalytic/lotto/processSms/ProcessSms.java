package com.smsanalytic.lotto.processSms;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.entities.SmsSendObject;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.model.setting.SettingXienType;
import com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService;
import com.smsanalytic.lotto.notificationListener.NotificationWear;


public class ProcessSms {


    /**
     * Hàm xử lý tin nhắn
     *
     * @param content
     * @return
     */
    public static SmsSuccessObject processSms(String content) {

        SmsSuccessObject smsSuccessObject = new SmsSuccessObject();
        StringProcessEntity processEntity = StringCalculate.processStringOriginal(content.toLowerCase());
        if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
            for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                StringCalculate.processChildEntity(processEntity, childEntity);
            }
            if (!processEntity.isHasError()) {
                String value = "";
                ArrayList<LotoNumberObject> listDataLoto = new ArrayList<>();
                ArrayList<Content> contents = new ArrayList<>();
                for (StringProcessChildEntity s : processEntity.getListChild()) {
                    value += s.getValue() + ". ";
                    contents.add(new Content(s.getType(), s.getValue()));
                    listDataLoto.addAll(s.getListDataLoto());
                }
                smsSuccessObject = new SmsSuccessObject(value, listDataLoto, true);

            } else {
                smsSuccessObject = new SmsSuccessObject(processEntity, false);
                processError(processEntity, smsSuccessObject);
            }
        }
        return smsSuccessObject;
    }

    public static SmsSuccessObject processSms(String content, AccountObject customer) {

        SmsSuccessObject smsSuccessObject = new SmsSuccessObject();
        StringProcessEntity processEntity = StringCalculate.processStringOriginal(content.toLowerCase());
        if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
            for (int i = 0; i < processEntity.getListChild().size(); i++) {
                StringProcessChildEntity childEntity = processEntity.getListChild().get(i);
                StringCalculate.processChildEntity(processEntity, childEntity, customer
                        , i > 1 && !processEntity.getListChild().get(i - 1).isError() ? processEntity.getListChild().get(i - 1) : null,i);
            }
            if (!processEntity.isHasError()) {
                String value = "";
                ArrayList<LotoNumberObject> listDataLoto = new ArrayList<>();
                ArrayList<Content> contents = new ArrayList<>();
                for (StringProcessChildEntity s : processEntity.getListChild()) {
                    value += s.getValue() + ". ";
                    listDataLoto.addAll(s.getListDataLoto());
                    long time = 0;
                    if (s.getListDataLoto().size() > 0) {
                        time = s.getListDataLoto().get(0).getDateTake();
                    }
                    contents.add(new Content(s.getType(), s.getValue(), time));
                }
                smsSuccessObject = new SmsSuccessObject(value, listDataLoto, true);
                smsSuccessObject.setContents(contents);

            } else {
                smsSuccessObject = new SmsSuccessObject(processEntity, false);
                processError(processEntity, smsSuccessObject);
            }
        }
        return smsSuccessObject;
    }


    public static void processError(StringProcessEntity processEntity, SmsSuccessObject smsSuccessObject) {
        try {
            int no = 1;
            StringBuilder sbError = new StringBuilder();
            StringBuilder sbProcess = new StringBuilder();
            for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                sbProcess.append(childEntity.getValue()).append(". ");
                if (childEntity.isError()) {
                    sbError.append(no).append(". ").append(childEntity.getError()).append("\n");
                    no++;
                }
            }
            smsSuccessObject.setMesError(sbError.toString());
            smsSuccessObject.setSmsFormatFailed(sbProcess.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<SmsSendObject> groupSmsToSent(List<LotoNumberObject> lotoNumberObjects, int type, String donvi) {
        try {
            List<SmsSendObject> result = new ArrayList<>();
            if (type == TypeEnum.TYPE_LO) {
                donvi = "d";
            }
            if (SettingXienType.getChuyenXienDi()==1 && TypeEnum.isTypeLoXien(type)){
                donvi = "d";
            }

            double money = 0;
            HashMap<Double, List<String>> hashMap = new HashMap<>();
            for (LotoNumberObject lotoNumberObject : lotoNumberObjects) {
                if (lotoNumberObject.getType() == type) {
                    if (lotoNumberObject.getSeChuyen() > 0) {
                        if (money != lotoNumberObject.getSeChuyen()) {
                            if (lotoNumberObject.getSeChuyen() > 0) {
                                List<String> loto = new ArrayList<>();
                                loto.add(lotoNumberObject.getValue1().replaceAll("-", ","));
                                double key=lotoNumberObject.getSeChuyen();
                                if (SettingXienType.getChuyenXienDi()==1 && TypeEnum.isTypeLoXien(type)){
                                    donvi = "d";
                                    key=lotoNumberObject.getSeChuyen()/10;
                                }
                                hashMap.put(key, loto);
                                money = lotoNumberObject.getSeChuyen();
                            }
                        } else {
                            if (money > 0) {
                                double key=lotoNumberObject.getSeChuyen();
                                if (SettingXienType.getChuyenXienDi()==1 && TypeEnum.isTypeLoXien(type)){
                                    donvi = "d";
                                    key=lotoNumberObject.getSeChuyen()/10;
                                }
                                hashMap.get(key).add(lotoNumberObject.getValue1().replaceAll("-", ","));
                            }
                        }
                    }
                }
            }
            if (hashMap.size() > 0) {
                for (Map.Entry<Double, List<String>> entry : hashMap.entrySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TypeEnum.getStringByType3(type)).append(" ");
                    sb.append(TextUtils.join(",", entry.getValue())).append(" x ").append(Common.formatDouble(entry.getKey())).append(donvi).append(". ");
                    SmsSendObject smsSendObject= new SmsSendObject(entry.getKey(),sb.toString(),type);
                    result.add(smsSendObject);
                }
                return result;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }


    public static List<SmsSendObject> groupSmsToSent(LotoNumberObject lotoNumberObject, int type, String donvi) {
        try {
            if (type == TypeEnum.TYPE_LO) {
                donvi = "d";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(TypeEnum.getStringByType3(type)).append(" ");
            double money = 0;
            HashMap<String, List<String>> hashMap = new HashMap<>();
           List<SmsSendObject> result = new ArrayList<>();

            if (lotoNumberObject.getType() == type) {
                if (lotoNumberObject.getSeChuyen() > 0) {
                    if (money != lotoNumberObject.getSeChuyen()) {
                        if (lotoNumberObject.getSeChuyen() > 0) {
                            List<String> loto = new ArrayList<>();
                            loto.add(lotoNumberObject.getValue1().replaceAll("-", ","));
                            hashMap.put(String.valueOf(lotoNumberObject.getSeChuyen()), loto);
                            money = lotoNumberObject.getSeChuyen();
                        }
                    } else {
                        if (money > 0) {
                            hashMap.get(String.valueOf(lotoNumberObject.getSeChuyen())).add(lotoNumberObject.getValue1().replaceAll("-", ","));
                        }
                    }
                }
            }

            if (hashMap.size() > 0) {
                for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
                    sb.append(TextUtils.join(",", entry.getValue())).append(" x ").append(entry.getKey()).append(donvi).append(". ");
                    SmsSendObject smsSendObject= new SmsSendObject(Double.parseDouble(entry.getKey()),sb.toString());
                    result.add(smsSendObject);
                }

                return result;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public static ArrayList<String> getListChild(ArrayList<LotoNumberObject> lotoNumberObjects, int type, HashMap<Integer,List<String>> hitList,String donvi) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {
            switch (type) {
                case TypeEnum.TYPE_LO:
                    sb.append(TypeEnum.getStringByTypeFull(type)).append(": ")
                            .append(getListNum(lotoNumberObjects, type, hitList)).append(" x ")
                            .append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake()))
                            .append("d");
                    result.add(sb.toString());
                    break;
                case TypeEnum.TYPE_3C:
                case TypeEnum.TYPE_DITNHAT:
                case TypeEnum.TYPE_DAUDB:
                case TypeEnum.TYPE_DAUNHAT:
                case TypeEnum.TYPE_CANGGIUA:
                case TypeEnum.TYPE_DE:
                    sb.append(TypeEnum.getStringByTypeFull(type)).append(": ")
                            .append(getListNum(lotoNumberObjects, type, hitList)).append(" x ")
                            .append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake()))
                            .append(donvi);
                    result.add(sb.toString());
                    break;
                case TypeEnum.TYPE_XIENGHEP2:
                case TypeEnum.TYPE_XIENGHEP3:
                case TypeEnum.TYPE_XIENGHEP4:
                case TypeEnum.TYPE_XIENQUAY:
                case TypeEnum.TYPE_XIEN2:
                case TypeEnum.TYPE_XIEN3:
                case TypeEnum.TYPE_XIEN4:
                    sb = new StringBuilder();
                    sb.append(TypeEnum.getStringByTypeFull(type)).append(": ");

                    for (LotoNumberObject numberObject : lotoNumberObjects) {
                        if (hitList.size() > 0) {
                                if (hitList.containsKey(numberObject.getType()) && hitList.get(numberObject.getType()).contains(numberObject.getXienFormat())) {
                                    String value= MyApp.getContext().getString(R.string.html_loto_hit,numberObject.getXienFormat()!=null ? numberObject.getXienFormat().replaceAll("-",","):"" );
                                    sb.append(value).append(" ; ");
                                } else {
                                    sb.append(numberObject.getXienFormat()!=null ?numberObject.getXienFormat().replaceAll("-",","):"" ).append(" ; ");
                                }

                        } else {
                                    sb.append(numberObject.getXienFormat()!=null ?numberObject.getXienFormat().replaceAll("-",","):"" ).append(" ; ");
                        }

                    }
                    sb.append(" x ").append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake())).append(donvi);
                    result.add(sb.toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getListNum(ArrayList<LotoNumberObject> entity, int type, HashMap<Integer,List<String>> hitList) {
        StringBuilder sb = new StringBuilder();
        try {
            for (LotoNumberObject numberObject : entity) {
                if (hitList.size()>0){
                    if (hitList.containsKey(numberObject.getType()) && hitList.get(numberObject.getType()).contains(numberObject.getValue1())){
                        sb.append(MyApp.getContext().getString(R.string.html_loto_hit, numberObject.getValue1())).append(",");
                    }
                    else{
                        sb.append(numberObject.getValue1()).append(",");
                    }
                }
                else{
                    sb.append(numberObject.getValue1()).append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().substring(0, sb.length() - 1);
    }


    public static SmsObject replySMS(Context context, SmsObject smsObjectLast, String content, DaoSession daoSession, boolean isCanChuyen) {
        SmsObject smsObjectResult = null;
        if (smsObjectLast.getSmsType()!= SmsType.SMS_NORMAL){
            if (MyApp.replyHaspMap != null) {
                if (MyApp.replyHaspMap.containsKey(smsObjectLast.getIdAccouunt())) {
                    NotificationWear notificationWear = NotificationSmsListenerService.getNotificationWear(MyApp.replyHaspMap.get(smsObjectLast.getIdAccouunt()));
                    if (notificationWear != null) {
                        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.getRemoteInputs().size()];
                        Intent localIntent = new Intent();
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle localBundle = notificationWear.getBundle();
                        int i = 0;
                        for (RemoteInput remoteIn : notificationWear.getRemoteInputs()) {
                            getDetailsOfNotification(remoteIn);
                            remoteInputs[i] = remoteIn;
                            localBundle.putCharSequence(remoteInputs[i].getResultKey(), content);//This work, apart from Hangouts as probably they need additional parameter (notification_tag?)
                            i++;
                        }
                        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
                        try {
                            notificationWear.getPendingIntent().send(context, 0, localIntent);
                            SmsObject smsObject = new SmsObject(smsObjectLast.getGroupTitle(), content, content, smsObjectLast.getSmsType(), smsObjectLast.getIdAccouunt(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
                            smsObject.setGuid(UUID.randomUUID().toString());
                            daoSession.getSmsObjectDao().insert(smsObject);
                            smsObjectResult = smsObject;
                        } catch (PendingIntent.CanceledException e) {
                            notiError(context, isCanChuyen);
                        }
                    } else {
                        notiError(context, isCanChuyen);
                    }
                } else {
                    notiError(context, isCanChuyen);
                }
            }
        }

        return smsObjectResult;
    }

    public static void notiError(Context context, boolean isCanChuyen
    ) {
        if (!isCanChuyen) {
            Toast.makeText(context, "Phiên làm việc đã kết thúc. Yêu cầu phát sinh một tin nhắn đến của người này", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getDetailsOfNotification(RemoteInput remoteInput) {
        //Some more details of RemoteInput... no idea what for but maybe it will be useful at some point
        String resultKey = remoteInput.getResultKey();
        String label = remoteInput.getLabel().toString();
        Boolean canFreeForm = remoteInput.getAllowFreeFormInput();
        if (remoteInput.getChoices() != null && remoteInput.getChoices().length > 0) {
            String[] possibleChoices = new String[remoteInput.getChoices().length];
            for (int i = 0; i < remoteInput.getChoices().length; i++) {
                possibleChoices[i] = remoteInput.getChoices()[i].toString();
            }
        }
    }
}
