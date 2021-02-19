package com.smsanalytic.lotto.ui.xsmb;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DebtObject;
import com.smsanalytic.lotto.database.DebtObjectDao;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService;

public class XSMBUtils {
    public static final String KEY_DE = "de";
    public static final String KEY_DAUDB = "daudb";
    public static final String KEY_DAUG1 = "daug1";
    public static final String KEY_DUOIG1 = "duoig1";
    public static final String KEY_LO = "lo";
    public static final String KEY_3C = "3c";
    public static final String KEY_CANGGIUA = "canggiua";

    public static void TinhKetQuaXS(Map<String, ArrayList<String>> dataHit, Calendar date) {
        try {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(date.getTime());
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(date.getTime());
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 0);

            ArrayList<LotoNumberObject> listLotto = (ArrayList<LotoNumberObject>) MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().queryBuilder().where(
                    LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
            for (LotoNumberObject lotto : listLotto) {
                switch (lotto.getType()) {
                    case TypeEnum.TYPE_DE:
                        if (dataHit.get(KEY_DE).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_DAUDB:
                        if (dataHit.get(KEY_DAUDB).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_LO:
                        if (dataHit.get(KEY_LO).contains(lotto.getValue1())) {
                            int nhay = 0;
                            AccountObject customer = null;
                            try {
                                List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(lotto.getAccountSend())).list();
                                if (accountList.size() > 0) {
                                    customer = accountList.get(0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            AccountSetting accountSetting = null;
                            try {
                                if (customer != null) {
                                    accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for (String s : dataHit.get(KEY_LO)) {
                                if (s.equalsIgnoreCase(lotto.getValue1())) {
                                    if (accountSetting != null && accountSetting.getSonhaylotrathuongtoida() <= nhay) {
                                        break;
                                    } else {
                                        nhay++;
                                    }
                                }
                            }
                            lotto.setHit(true);
                            lotto.setNhay(nhay);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_3C:
                        if (dataHit.get(KEY_3C).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_DITNHAT:
                        if (dataHit.get(KEY_DUOIG1).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_DAUNHAT:
                        if (dataHit.get(KEY_DAUG1).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_CANGGIUA:
                        if (dataHit.get(KEY_CANGGIUA).contains(lotto.getValue1())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_XIENGHEP2:
                    case TypeEnum.TYPE_XIEN2:
                        if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                && dataHit.get(KEY_LO).contains(lotto.getValue2())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_XIENGHEP3:
                    case TypeEnum.TYPE_XIEN3:
                        if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_XIENGHEP4:
                    case TypeEnum.TYPE_XIEN4:
                        if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                            lotto.setHit(true);
                            MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                        }
                        break;
                    case TypeEnum.TYPE_XIENQUAY:
                        if (!TextUtils.isEmpty(lotto.getValue3()) && !TextUtils.isEmpty(lotto.getValue4())) {
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                        } else if (!TextUtils.isEmpty(lotto.getValue3())) {
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                        } else {
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                        }
                        break;
                }
            }

            // công nợ
            List<AccountObject> accountObjectList = MyApp.getInstance().getDaoSession().getAccountObjectDao().loadAll();
            int typeTienTe= Common.getTypeTienTe(MyApp.getContext());
            for (AccountObject accountObject : accountObjectList) {
                // lấy các con số của khách hàng hôm đó
                List<LotoNumberObject> lotoNumberObjects = MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount()),
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                // update lại công nợ của khách hàng hôm đó
                List<DebtObject> debtObjectList = MyApp.getInstance().getDaoSession().getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), DebtObjectDao.Properties.Date.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                if (debtObjectList.size() > 0) {
                    debtObjectList.get(0).setExpenses(0);
                    MyApp.getInstance().getDaoSession().getDebtObjectDao().update(debtObjectList.get(0));
                }
                NotificationSmsListenerService.processDebt(MyApp.getInstance().getDaoSession(), (ArrayList<LotoNumberObject>) lotoNumberObjects, accountObject,typeTienTe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
