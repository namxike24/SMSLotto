package com.smsanalytic.lotto.ui.document;

import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.entities.TableNumberEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.ui.document.adapter.ChooseCustomerAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class DocumentFragment extends BaseFragment {
    private View view;
    @BindView(R.id.rlDate)
    RelativeLayout rlDate;
    @BindView(R.id.rlAccount)
    LinearLayout rlAccount;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvAcount)
    TextView tvAcount;
    @BindView(R.id.btnGetData)
    Button btnGetData;
    @BindView(R.id.rg_account_type)
    RadioGroup groupType;

    @BindView(R.id.lnData)
    LinearLayout lnData;
    @BindView(R.id.tbTotalDetail)
    TableLayout tbTotalDetail;
    @BindView(R.id.lnDetail)
    LinearLayout lnDetail;
    @BindView(R.id.lnDe)
    LinearLayout lnDe;
    @BindView(R.id.tbDe)
    TableLayout tbDe;
    @BindView(R.id.lnLo)
    LinearLayout lnLo;
    @BindView(R.id.tbLo)
    TableLayout tbLo;
    @BindView(R.id.lnXien)
    LinearLayout lnXien;
    @BindView(R.id.tbXien)
    TableLayout tbXien;
    @BindView(R.id.lnBaCang)
    LinearLayout lnBaCang;
    @BindView(R.id.tbBaCang)
    TableLayout tbBaCang;
    @BindView(R.id.lnDuoiG1)
    LinearLayout lnDuoiG1;
    @BindView(R.id.tbDuoiG1)
    TableLayout tbDuoiG1;
    @BindView(R.id.lnCangGiua)
    LinearLayout lnCangGiua;
    @BindView(R.id.tbCangGiua)
    TableLayout tbCangGiua;
    @BindView(R.id.lnDauG1)
    LinearLayout lnDauG1;
    @BindView(R.id.tbDauG1)
    TableLayout tbDauG1;
    @BindView(R.id.lnDauDB)
    LinearLayout lnDauDB;
    @BindView(R.id.tbDauDB)
    TableLayout tbDauDB;

    @BindView(R.id.lnDataAll)
    LinearLayout lnDataAll;
    @BindView(R.id.tbTotalDetailAll)
    TableLayout tbTotalDetailAll;

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private int type;
    private ChooseCustomerPopup customerPopup;
    private DaoSession daoSession;
    private ArrayList<AccountObject> listCustomer;
    private ArrayList<LotoNumberObject> listNumDe;
    private ArrayList<LotoNumberObject> listNumLo;
    private ArrayList<LotoNumberObject> listNumXien;
    private ArrayList<LotoNumberObject> listNumBaCang;
    private ArrayList<LotoNumberObject> listNumDauDB;
    private ArrayList<LotoNumberObject> listNumDauG1;
    private ArrayList<LotoNumberObject> listNumDuoiG1;
    private ArrayList<LotoNumberObject> listNumCangGiua;
    private ArrayList<LotoNumberObject> listNumDeThang;
    private ArrayList<LotoNumberObject> listNumLoThang;
    private ArrayList<LotoNumberObject> listNumXienThang;
    private ArrayList<LotoNumberObject> listNumBaCangThang;
    private ArrayList<LotoNumberObject> listNumDauDBThang;
    private ArrayList<LotoNumberObject> listNumDauG1Thang;
    private ArrayList<LotoNumberObject> listNumDuoiG1Thang;
    private ArrayList<LotoNumberObject> listNumCangGiuaThang;
    private AccountObject currentCustomer;
    private ProgressDialog progressDialog;
    private MainLoToActivity activity;
    private SettingDefault settingDefault;
    private String donvi;
    private int donviInt;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_document, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            activity = (MainLoToActivity) getActivity();
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            activity.checkGetXSMB();
            tvDate.setText(DateTimeUtils.convertDateToString(startDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            initListener();
            initData();
            getDataSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataSetting() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(getContext(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donviInt=settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM;
        donvi= TienTe.getKeyTienTe(donviInt);

    }

    private void initData() {
        try {
            type = 3;
            listCustomer = new ArrayList<>();
            AccountObject all = new AccountObject();
            all.setIdAccount("All");
            all.setAccountName("Tất cả");
            listCustomer.add(all);
            List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().orderAsc(AccountObjectDao.Properties.DateCreate).build().list();
            listCustomer.addAll(list);
            currentCustomer = all;
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 0);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang lấy dữ liệu...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 250);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (type) {
                            case 0:
                                getDataNhanVe();
                                break;
                            case 1:
                                getDataChuyenDi();
                                break;
                            case 2:
                                getDataGiuLai();
                                break;
                            default:
                                getDataTatCa();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataTatCa() {
        try {
            if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDeThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumLoThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumXienThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumBaCangThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDauDBThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDauG1Thang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDuoiG1Thang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumCangGiuaThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
            } else {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDeThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumLoThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumXienThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumBaCangThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDauDBThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDauG1Thang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumDuoiG1Thang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();

                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
                listNumCangGiuaThang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())
                        , LotoNumberObjectDao.Properties.Hit.eq(true)).whereOr(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())).list();
            }


            displayDataType4();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataType4() {
        getActivity().runOnUiThread(() -> {
            try {
                lnData.setVisibility(View.GONE);
                lnDataAll.setVisibility(View.VISIBLE);
                tbTotalDetailAll.removeAllViews();
                View tableRowTitle = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_header, null, false);
                tbTotalDetailAll.addView(tableRowTitle);
                totalAllNhanVe = 0;
                totalAllChuyenDi = 0;
                if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                    displayDataAllCustomer();
                } else {
                    AccountSetting accountSetting=new Gson().fromJson(currentCustomer.getAccountSetting(),AccountSetting.class);
                    displayDataAllOneCustomer(accountSetting.getSonhaylotrathuongtoida());
                }
                View tableRowEnd = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total_all, null, false);
                TextView tvTotalAll = tableRowEnd.findViewById(R.id.tvTotalAll);

                double sum = totalAllChuyenDi + totalAllNhanVe;
                if (sum == 0) {
                    tvTotalAll.setText("TỔNG HÒA");
                } else if (sum > 0) {
                    tvTotalAll.setText(sum == 0 ? "0" : String.format("TỔNG LÃI: %s"+donvi+"", Common.roundMoney(sum)));
                } else {
                    tvTotalAll.setText(sum == 0 ? "0" : String.format("TỔNG LỖ: %s"+donvi+"", Common.roundMoney(Math.abs(sum))));
                    tvTotalAll.setTextColor(getResources().getColor(R.color.red_dark));
                }
                tbTotalDetailAll.addView(tableRowEnd);
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });

    }

    private double totalAllNhanVe;
    private double totalAllChuyenDi;

    private void displayDataAllCustomer() {
        try {
            displayDataAllCustomerChild(listNumDe, "Đề", false,donviInt);
            displayDataAllCustomerChild(listNumLo, "Lô", false,donviInt);
            displayDataAllCustomerChild(listNumXien, "Xiên", false,donviInt);
            displayDataAllCustomerChild(listNumBaCang, "Ba Càng", false,donviInt);
            displayDataAllCustomerChild(listNumDauDB, "Đầu DB", false,donviInt);
            displayDataAllCustomerChild(listNumDauG1, "Đầu G1", false,donviInt);
            displayDataAllCustomerChild(listNumDuoiG1, "Đuôi DB", false,donviInt);
            displayDataAllCustomerChild(listNumCangGiua, "Càng", false,donviInt);
            displayDataAllCustomerChild(listNumDeThang, "th Đề", true,donviInt);
            displayDataAllCustomerChild(listNumLoThang, "th Lô", true,donviInt);
            displayDataAllCustomerChild(listNumXienThang, "th Xiên", true,donviInt);
            displayDataAllCustomerChild(listNumBaCangThang, "th Ba Càng", true,donviInt);
            displayDataAllCustomerChild(listNumDauDBThang, "th Đầu DB", true,donviInt);
            displayDataAllCustomerChild(listNumDauG1Thang, "th Đầu G1", true,donviInt);
            displayDataAllCustomerChild(listNumDuoiG1Thang, "th Đuôi DB", true,donviInt);
            displayDataAllCustomerChild(listNumCangGiuaThang, "th Càng", true,donviInt);
            View tableRowTotalAll = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data_total, null, false);
            TextView tvNhanVe = tableRowTotalAll.findViewById(R.id.tvNhanVe);
            TextView tvChuyenDi = tableRowTotalAll.findViewById(R.id.tvChuyenDi);
            tvNhanVe.setText(totalAllNhanVe == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllNhanVe)));
            tvNhanVe.setTextColor(totalAllNhanVe > 0 ? getResources().getColor(R.color.blue_dark)
                    : getResources().getColor(R.color.red_dark));
            tvChuyenDi.setText(totalAllChuyenDi == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllChuyenDi)));
            tvChuyenDi.setTextColor(totalAllChuyenDi > 0 ? getResources().getColor(R.color.blue_dark)
                    : getResources().getColor(R.color.red_dark));
            tbTotalDetailAll.addView(tableRowTotalAll);

            if (listCustomer.size() > 1) {
                for (int i = 1; i < listCustomer.size(); i++) {
                    displayDataCustomer(listCustomer.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataAllOneCustomer(int nhayMax) {
        try {
            AccountRate accountRate = new Gson().fromJson(currentCustomer.getAccountRate(), AccountRate.class);
            displayDataAllOneCustomerChild(accountRate, listNumDe, "Đề", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumLo, "Lô", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumXien, "Xiên", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumBaCang, "Ba Càng", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDauDB, "Đầu DB", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDauG1, "Đầu G1", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDuoiG1, "Đuôi DB", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumCangGiua, "Càng", false,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDeThang, "th Đề", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumLoThang, "th Lô", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumXienThang, "th Xiên", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumBaCangThang, "th Ba Càng", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDauDBThang, "th Đầu DB", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDauG1Thang, "th Đầu G1", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumDuoiG1Thang, "th Đuôi DB", true,donviInt,nhayMax);
            displayDataAllOneCustomerChild(accountRate, listNumCangGiuaThang, "th Càng", true,donviInt,nhayMax);
            View tableRowTotalAll = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data_total, null, false);
            TextView tvNhanVe = tableRowTotalAll.findViewById(R.id.tvNhanVe);
            TextView tvChuyenDi = tableRowTotalAll.findViewById(R.id.tvChuyenDi);
            tvNhanVe.setText(totalAllNhanVe == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllNhanVe)));
            tvNhanVe.setTextColor(totalAllNhanVe > 0 ? getResources().getColor(R.color.blue_dark)
                    : getResources().getColor(R.color.red_dark));
            tvChuyenDi.setText(totalAllChuyenDi == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllChuyenDi)));
            tvChuyenDi.setTextColor(totalAllChuyenDi > 0 ? getResources().getColor(R.color.blue_dark)
                    : getResources().getColor(R.color.red_dark));
            tbTotalDetailAll.addView(tableRowTotalAll);


            displayDataCustomer(currentCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int totalItemAdd;
    double totalAllNhanVeChild;
    double totalAllChuyenDiChild;

    private void displayDataCustomer(AccountObject customer) {
        try {
            totalItemAdd = 0;
            totalAllNhanVeChild = 0;
            totalAllChuyenDiChild = 0;
            AccountRate accountRate = new Gson().fromJson(customer.getAccountRate(), AccountRate.class);
            AccountSetting accountSetting=new Gson().fromJson(customer.getAccountSetting(),AccountSetting.class);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(),listNumDe, "Đề", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumLo, "Lô", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumXien, "Xiên", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumBaCang, "Ba Càng", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDauDB, "Đầu DB", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDauG1, "Đầu G1", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDuoiG1, "Đuôi DB", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumCangGiua, "Càng", false,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDeThang, "th Đề", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumLoThang, "th Lô", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumXienThang, "th Xiên", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumBaCangThang, "th Ba Càng", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDauDBThang, "th Đầu DB", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDauG1Thang, "th Đầu G1", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumDuoiG1Thang, "th Đuôi DB", true,donviInt);
            displayDataCustomerChild(customer, accountRate,accountSetting.getSonhaylotrathuongtoida(), listNumCangGiuaThang, "th Càng", true,donviInt);

            if (totalItemAdd > 0) {
                View tableRowTotalAll = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data_total, null, false);
                TextView tvNhanVe = tableRowTotalAll.findViewById(R.id.tvNhanVe);
                TextView tvChuyenDi = tableRowTotalAll.findViewById(R.id.tvChuyenDi);
                tvNhanVe.setText(totalAllNhanVeChild == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllNhanVeChild)));
                tvNhanVe.setTextColor(totalAllNhanVeChild > 0 ? getResources().getColor(R.color.blue_dark)
                        : getResources().getColor(R.color.red_dark));
                tvChuyenDi.setText(totalAllChuyenDiChild == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(totalAllChuyenDiChild)));
                tvChuyenDi.setTextColor(totalAllChuyenDiChild > 0 ? getResources().getColor(R.color.blue_dark)
                        : getResources().getColor(R.color.red_dark));
                tbTotalDetailAll.addView(tableRowTotalAll);
                totalItemAdd++;

                View tableRowHeader = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_child_header, null, false);
                TextView tvKhachHang = tableRowHeader.findViewById(R.id.tvKhachHang);

                StringBuilder sb = new StringBuilder(String.format(getString(R.string.html_color_format), "#007600", customer.getAccountName()));
                if (!TextUtils.isEmpty(customer.getPhone())) {
                    sb.append("-").append(customer.getPhone());
                }
                double sum = totalAllChuyenDiChild + totalAllNhanVeChild;
                if (sum == 0) {
                    sb.append(String.format(getString(R.string.html_color_format), "#0100fe", " Hòa"));
                } else if (sum > 0) {
                    sb.append(String.format(getString(R.string.html_color_format), "#0100fe", String.format(" Lãi: %s"+donvi+"", Common.roundMoney(sum))));
                } else {
                    sb.append(String.format(getString(R.string.html_color_format), "#cc0202", String.format(" Bù: %s"+donvi+"", Common.roundMoney(sum))));
                }
                tvKhachHang.setText(Html.fromHtml(sb.toString()));
                tbTotalDetailAll.addView(tableRowHeader, tbTotalDetailAll.getChildCount() - totalItemAdd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataCustomerChild(AccountObject customer, AccountRate accountRate,int nhayMax, ArrayList<LotoNumberObject> listNum, String loai, boolean isHit,int typeTienTe) {
        if (listNum != null && !listNum.isEmpty()) {

            double diemNhanVe = 0;
            double diemChuyenDi = 0;
            double thanhtienNhanVe = 0;
            double thanhtienChuyenDi = 0;
            for (LotoNumberObject numberObject : listNum) {
                if (numberObject.getAccountSend().equalsIgnoreCase(customer.getIdAccount())) {
                    double diemNV = 0;
                    double diemCD = 0;
                    double tienNV = 0;
                    double tienCD = 0;
                    if (numberObject.getSmsStatus() == SmsStatus.SMS_SENT) {
                        diemCD = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) : numberObject.getMoneyTake();
                        if (isHit) {
                            tienCD = numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) * getRateAn(numberObject, accountRate,typeTienTe);
                        } else {
                            tienCD = numberObject.getMoneyTake() * getRateDanh(numberObject, accountRate,typeTienTe);
                        }
                    } else {
                        diemNV = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) : numberObject.getMoneyTake();
                        if (isHit) {
                            tienNV = diemNV * getRateAn(numberObject, accountRate,typeTienTe);
                        } else {
                            tienNV = diemNV * getRateDanh(numberObject, accountRate,typeTienTe);
                        }

                    }


                    diemNhanVe += diemNV;
                    diemChuyenDi += diemCD;
                    thanhtienNhanVe += tienNV;
                    thanhtienChuyenDi += tienCD;
                }
            }
            if (thanhtienChuyenDi != 0 || thanhtienNhanVe != 0) {
//            totalAll += ketqua;
                if (isHit) {
                    totalAllNhanVeChild -= thanhtienNhanVe;
                    totalAllChuyenDiChild += thanhtienChuyenDi;
                } else {
                    totalAllNhanVeChild += thanhtienNhanVe;
                    totalAllChuyenDiChild -= thanhtienChuyenDi;
                }

                View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data, null, false);
                TextView tvLoai = tableRow.findViewById(R.id.tvType);
                TextView tvNhanVeDiem = tableRow.findViewById(R.id.tvNhanVeDiem);
                TextView tvNhanVeThanhTien = tableRow.findViewById(R.id.tvNhanVeThanhTien);
                TextView tvChuyenDiDiem = tableRow.findViewById(R.id.tvChuyenDiDiem);
                TextView tvChuyenDiThanhTien = tableRow.findViewById(R.id.tvChuyenDiThanhTien);

                tvLoai.setText(loai);
                if (isHit) {
                    tvLoai.setTextColor(getResources().getColor(R.color.blue_dark));
                } else {
                    tvLoai.setTextColor(getResources().getColor(R.color.back_1));
                }
                tvNhanVeDiem.setText(diemNhanVe == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemNhanVe)));
                tvNhanVeThanhTien.setText(thanhtienNhanVe == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienNhanVe)));
                tvChuyenDiDiem.setText(diemChuyenDi == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemChuyenDi)));
                tvChuyenDiThanhTien.setText(thanhtienChuyenDi == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienChuyenDi)));
                tbTotalDetailAll.addView(tableRow);
                totalItemAdd++;
            }
        }
    }

    private void displayDataAllCustomerChild(ArrayList<LotoNumberObject> listNum, String loai, boolean isHit,int typeTienTe) {
        if (listNum != null && !listNum.isEmpty()) {
            double diemNhanVe = 0;
            double diemChuyenDi = 0;
            double thanhtienNhanVe = 0;
            double thanhtienChuyenDi = 0;
            for (LotoNumberObject numberObject : listNum) {
                String idAccount;
                double diemNV = 0;
                double diemCD = 0;
                double tienNV = 0;
                double tienCD = 0;
                idAccount = numberObject.getAccountSend();
                AccountRate accountRate = getAccountRate(idAccount);
                AccountSetting accountSetting =getAccountSetting(idAccount);
                if (numberObject.getSmsStatus() == SmsStatus.SMS_SENT) {
                    diemCD = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) : numberObject.getMoneyTake();
                    if (isHit) {
                        tienCD = numberObject.getMoneyTake() * numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) * getRateAn(numberObject, accountRate,typeTienTe);
                    } else {
                        tienCD = numberObject.getMoneyTake() * getRateDanh(numberObject, accountRate,typeTienTe);
                    }
                } else {
                    diemNV = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) : numberObject.getMoneyTake();
                    if (isHit) {
                        tienNV = diemNV * getRateAn(numberObject, accountRate,typeTienTe);
                    } else {
                        tienNV = diemNV * getRateDanh(numberObject, accountRate,typeTienTe);
                    }

                }


                diemNhanVe += diemNV;
                diemChuyenDi += diemCD;
                thanhtienNhanVe += tienNV;
                thanhtienChuyenDi += tienCD;
            }
//            totalAll += ketqua;
            if (isHit) {
                totalAllNhanVe -= thanhtienNhanVe;
                totalAllChuyenDi += thanhtienChuyenDi;
            } else {
                totalAllNhanVe += thanhtienNhanVe;
                totalAllChuyenDi -= thanhtienChuyenDi;
            }

            View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data, null, false);
            TextView tvLoai = tableRow.findViewById(R.id.tvType);
            tvLoai.setGravity(Gravity.LEFT);
            TextView tvNhanVeDiem = tableRow.findViewById(R.id.tvNhanVeDiem);
            tvNhanVeDiem.setGravity(Gravity.RIGHT);
            TextView tvNhanVeThanhTien = tableRow.findViewById(R.id.tvNhanVeThanhTien);
            tvNhanVeThanhTien.setGravity(Gravity.RIGHT);
            TextView tvChuyenDiDiem = tableRow.findViewById(R.id.tvChuyenDiDiem);
            tvChuyenDiDiem.setGravity(Gravity.RIGHT);
            TextView tvChuyenDiThanhTien = tableRow.findViewById(R.id.tvChuyenDiThanhTien);
            tvChuyenDiThanhTien.setGravity(Gravity.RIGHT);

            tvLoai.setText(loai);
            if (isHit) {
                tvLoai.setTextColor(getResources().getColor(R.color.blue_dark));
            } else {
                tvLoai.setTextColor(getResources().getColor(R.color.back_1));
            }
            tvNhanVeDiem.setText(diemNhanVe == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemNhanVe)));
            tvNhanVeThanhTien.setText(thanhtienNhanVe == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienNhanVe)));
            tvChuyenDiDiem.setText(diemChuyenDi == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemChuyenDi)));
            tvChuyenDiThanhTien.setText(thanhtienChuyenDi == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienChuyenDi)));
            tbTotalDetailAll.addView(tableRow);
        }
    }

    private void displayDataAllOneCustomerChild(AccountRate accountRate, ArrayList<LotoNumberObject> listNum, String loai, boolean isHit, int typeTienTe, int nhayMax) {
        if (listNum != null && !listNum.isEmpty()) {
            double diemNhanVe = 0;
            double diemChuyenDi = 0;
            double thanhtienNhanVe = 0;
            double thanhtienChuyenDi = 0;
            for (LotoNumberObject numberObject : listNum) {
                double diemNV = 0;
                double diemCD = 0;
                double tienNV = 0;
                double tienCD = 0;
                if (numberObject.getSmsStatus() == SmsStatus.SMS_SENT) {
                    diemCD = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) : numberObject.getMoneyTake();
                    if (isHit) {
                        tienCD = numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) * getRateAn(numberObject, accountRate,typeTienTe);
                    } else {
                        tienCD = numberObject.getMoneyTake() * getRateDanh(numberObject, accountRate,typeTienTe);
                    }
                } else {
                    diemNV = isHit ? numberObject.getMoneyTake() * numberObject.getNhay(nhayMax) : numberObject.getMoneyTake();
                    if (isHit) {
                        tienNV = diemNV * getRateAn(numberObject, accountRate,typeTienTe);
                    } else {
                        tienNV = diemNV * getRateDanh(numberObject, accountRate,typeTienTe);
                    }

                }


                diemNhanVe += diemNV;
                diemChuyenDi += diemCD;
                thanhtienNhanVe += tienNV;
                thanhtienChuyenDi += tienCD;
            }
//            totalAll += ketqua;
            if (isHit) {
                totalAllNhanVe -= thanhtienNhanVe;
                totalAllChuyenDi += thanhtienChuyenDi;
            } else {
                totalAllNhanVe += thanhtienNhanVe;
                totalAllChuyenDi -= thanhtienChuyenDi;
            }

            View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_all_row_data, null, false);
            TextView tvLoai = tableRow.findViewById(R.id.tvType);
            tvLoai.setGravity(Gravity.LEFT);
            TextView tvNhanVeDiem = tableRow.findViewById(R.id.tvNhanVeDiem);
            tvNhanVeDiem.setGravity(Gravity.RIGHT);
            TextView tvNhanVeThanhTien = tableRow.findViewById(R.id.tvNhanVeThanhTien);
            tvNhanVeThanhTien.setGravity(Gravity.RIGHT);
            TextView tvChuyenDiDiem = tableRow.findViewById(R.id.tvChuyenDiDiem);
            tvChuyenDiDiem.setGravity(Gravity.RIGHT);
            TextView tvChuyenDiThanhTien = tableRow.findViewById(R.id.tvChuyenDiThanhTien);
            tvChuyenDiThanhTien.setGravity(Gravity.RIGHT);

            tvLoai.setText(loai);
            if (isHit) {
                tvLoai.setTextColor(getResources().getColor(R.color.blue_dark));
            } else {
                tvLoai.setTextColor(getResources().getColor(R.color.back_1));
            }
            tvNhanVeDiem.setText(diemNhanVe == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemNhanVe)));
            tvNhanVeThanhTien.setText(thanhtienNhanVe == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienNhanVe)));
            tvChuyenDiDiem.setText(diemChuyenDi == 0 ? "0" : String.format(loai.equalsIgnoreCase("lô") || loai.equalsIgnoreCase("th Lô") ? "%sd" : "%s"+donvi+"", Common.roundMoney(diemChuyenDi)));
            tvChuyenDiThanhTien.setText(thanhtienChuyenDi == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(thanhtienChuyenDi)));
            tbTotalDetailAll.addView(tableRow);
        }
    }

    private void getDataNhanVe() {
        try {
            if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)).list();
            } else {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
            }
            displayDataType0To2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataChuyenDi() {
        try {
            if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)).list();
            } else {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_SENT)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
            }
            displayDataType0To2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataGiuLai() {
        try {
            if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)).list();
            } else {
                listNumDe = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DE)
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumLo = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_LO)
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumXien = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.in(TypeEnum.TYPE_XIEN2
                                , TypeEnum.TYPE_XIEN3, TypeEnum.TYPE_XIEN4, TypeEnum.TYPE_XIENGHEP2, TypeEnum.TYPE_XIENGHEP3
                                , TypeEnum.TYPE_XIENGHEP4, TypeEnum.TYPE_XIENQUAY)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumBaCang = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_3C)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauDB = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUDB)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDauG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DAUNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumDuoiG1 = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_DITNHAT)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
                listNumCangGiua = (ArrayList<LotoNumberObject>) daoSession.getLotoNumberObjectDao().queryBuilder().where(
                        LotoNumberObjectDao.Properties.DateTake.between(startDate.getTimeInMillis(), endDate.getTimeInMillis())
                        , LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)
                        , LotoNumberObjectDao.Properties.Type.eq(TypeEnum.TYPE_CANGGIUA)
                        , LotoNumberObjectDao.Properties.AccountSend.eq(currentCustomer.getIdAccount())).list();
            }

            displayDataType0To2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataType0To2() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    lnData.setVisibility(View.VISIBLE);
                    lnDataAll.setVisibility(View.GONE);

                    if (currentCustomer.getIdAccount().equalsIgnoreCase("all")) {
                        displayDataTotalAllCustomer();
                    } else {
                        AccountSetting accountSetting=new Gson().fromJson(currentCustomer.getAccountSetting(),AccountSetting.class);
                        displayDataTotalOneCustomer(accountSetting.getSonhaylotrathuongtoida());
                    }

                    displayDataDetail(listNumDe, lnDe, tbDe, donvi);
                    displayDataDetail(listNumLo, lnLo, tbLo, "d");
                    displayDataDetail(listNumXien, lnXien, tbXien, donvi);
                    displayDataDetail(listNumBaCang, lnBaCang, tbBaCang, donvi);
                    displayDataDetail(listNumDuoiG1, lnDuoiG1, tbDuoiG1, donvi);
                    displayDataDetail(listNumCangGiua, lnCangGiua, tbCangGiua, donvi);
                    displayDataDetail(listNumDauG1, lnDauG1, tbDauG1, donvi);
                    displayDataDetail(listNumDauDB, lnDauDB, tbDauDB, donvi);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        });

    }


    private double totalAll;

    private void displayDataTotalAllCustomer() {
        try {
            tbTotalDetail.removeAllViews();
            totalAll = 0;
            View tableRowTitle = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total, null, false);
            tbTotalDetail.addView(tableRowTitle);
            displayDataTotalAllCustomerChild(listNumDe, "Đề",donviInt);
            displayDataTotalAllCustomerChild(listNumLo, "Lô",donviInt);
            displayDataTotalAllCustomerChild(listNumXien, "Xiên",donviInt);
            displayDataTotalAllCustomerChild(listNumBaCang, "Ba Càng",donviInt);
            displayDataTotalAllCustomerChild(listNumDauDB, "Đầu DB",donviInt);
            displayDataTotalAllCustomerChild(listNumDauG1, "Đầu G1",donviInt);
            displayDataTotalAllCustomerChild(listNumDuoiG1, "Đuôi DB",donviInt);
            displayDataTotalAllCustomerChild(listNumCangGiua, "Càng",donviInt);

            if (tbTotalDetail.getChildCount() > 1) {
                View tableRowTotalAll = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total_all, null, false);
                TextView tvTotalAll = tableRowTotalAll.findViewById(R.id.tvTotalAll);
                if (totalAll >= 0) {
                    tvTotalAll.setText(String.format("TỔNG LÃI: %s"+donvi+"", Common.roundMoney(totalAll)));
                    tvTotalAll.setTextColor(getResources().getColor(R.color.blue_dark));
                } else {
                    tvTotalAll.setText(String.format("TỔNG LỖ: %s"+donvi+"", Common.roundMoney(Math.abs(totalAll))));
                    tvTotalAll.setTextColor(getResources().getColor(R.color.red_dark));
                }
                tbTotalDetail.addView(tableRowTotalAll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataTotalOneCustomer(int nhayMax) {
        try {
            tbTotalDetail.removeAllViews();
            totalAll = 0;
            AccountRate accountRate = new Gson().fromJson(currentCustomer.getAccountRate(), AccountRate.class);
            View tableRowTitle = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total, null, false);
            tbTotalDetail.addView(tableRowTitle);
            displayDataTotalOneCustomerChild(accountRate, listNumDe, "Đề",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumLo, "Lô",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumXien, "Xiên",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumBaCang, "Ba Càng",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumDauDB, "Đầu DB",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumDauG1, "Đầu G1",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumDuoiG1, "Đuôi DB",donviInt,nhayMax);
            displayDataTotalOneCustomerChild(accountRate, listNumCangGiua, "Càng",donviInt,nhayMax);

            if (tbTotalDetail.getChildCount() > 1) {
                View tableRowTotalAll = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total_all, null, false);
                TextView tvTotalAll = tableRowTotalAll.findViewById(R.id.tvTotalAll);
                if (totalAll >= 0) {
                    tvTotalAll.setText(String.format("TỔNG LÃI: %s"+donvi+"", Common.roundMoney(totalAll)));
                    tvTotalAll.setTextColor(getResources().getColor(R.color.blue_dark));
                } else {
                    tvTotalAll.setText(String.format("TỔNG LỖ: %s"+donvi+"", Common.roundMoney(Math.abs(totalAll))));
                    tvTotalAll.setTextColor(getResources().getColor(R.color.red_dark));
                }
                tbTotalDetail.addView(tableRowTotalAll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayDataTotalAllCustomerChild(ArrayList<LotoNumberObject> listNum, String loai,int typeTienTe) {
        if (listNum != null && !listNum.isEmpty()) {
            double tiendanh = 0;
            double tienan = 0;
            double ketqua = 0;
            for (LotoNumberObject numberObject : listNum) {
                String idAccount = numberObject.getAccountSend();
                AccountRate accountRate = getAccountRate(idAccount);
                double money;
                if (type == 2) {
                    money = numberObject.getMoneyKeep();
                } else {
                    money = numberObject.getMoneyTake();
                }
                double danh = money * getRateDanh(numberObject, accountRate,typeTienTe);
                double an = 0;
                if (numberObject.isHit()) {
                    AccountSetting accountSetting=getAccountSetting(numberObject.getAccountSend());
                    an = money * numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) * getRateAn(numberObject, accountRate,typeTienTe);
                }
                tiendanh += danh;
                tienan += an;
                ketqua += type == 1 ? an - danh : danh - an;
            }
            totalAll += ketqua;
            View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total, null, false);
            TextView tvLoai = tableRow.findViewById(R.id.tvType);
            TextView tvTienDanh = tableRow.findViewById(R.id.tvTienDanh);
            tvTienDanh.setGravity(Gravity.RIGHT);
            TextView tvTienAn = tableRow.findViewById(R.id.tvTienAn);
            tvTienAn.setGravity(Gravity.RIGHT);
            TextView tvKetQua = tableRow.findViewById(R.id.tvKetQua);
            tvKetQua.setGravity(Gravity.LEFT);

            tvLoai.setText(loai);
            tvTienDanh.setText(tiendanh == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(tiendanh)));
            tvTienAn.setText(tienan == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(tienan)));
            tvTienAn.setTextColor(getResources().getColor(R.color.red_dark));
            if (tienan > 0) {
                tvTienAn.setTextColor(getResources().getColor(R.color.red_dark));
            } else {
                tvTienAn.setTextColor(getResources().getColor(R.color.back_1));
            }
            if (ketqua >= 0) {
                tvKetQua.setText(String.format("Lãi: %s"+donvi+"", Common.roundMoney(ketqua)));
                tvKetQua.setTextColor(getResources().getColor(R.color.blue_dark));
            } else {
                tvKetQua.setText(String.format("Lỗ: %s"+donvi+"", Common.roundMoney(ketqua)));
                tvKetQua.setTextColor(getResources().getColor(R.color.red_dark));
            }
            tbTotalDetail.addView(tableRow);
        }
    }

    private AccountRate getAccountRate(String id) {
        AccountRate accountRate = null;
        try {
            if (listCustomer.size() > 1) {
                for (int i = 1; i < listCustomer.size(); i++) {
                    if (listCustomer.get(i).getIdAccount().equalsIgnoreCase(id)) {
                        accountRate = new Gson().fromJson(listCustomer.get(i).getAccountRate(), AccountRate.class);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountRate;
    }
    private AccountSetting getAccountSetting(String id) {
        AccountSetting accountSetting = null;
        try {
            if (listCustomer.size() > 1) {
                for (int i = 1; i < listCustomer.size(); i++) {
                    if (listCustomer.get(i).getIdAccount().equalsIgnoreCase(id)) {
                        accountSetting = new Gson().fromJson(listCustomer.get(i).getAccountSetting(), AccountSetting.class);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountSetting;
    }

    private void displayDataTotalOneCustomerChild(AccountRate accountRate, ArrayList<LotoNumberObject> listNum, String loai,int typeTienTe, int nhayMax) {
        if (listNum != null && !listNum.isEmpty()) {
            double tiendanh = 0;
            double tienan = 0;
            double ketqua = 0;
            for (LotoNumberObject numberObject : listNum) {
                double money;
                if (type == 2) {
                    money = numberObject.getMoneyKeep() * numberObject.getNhay(nhayMax);
                } else {
                    money = numberObject.getMoneyTake() * numberObject.getNhay(nhayMax);
                }
                double danh = money * getRateDanh(numberObject, accountRate,typeTienTe);
                double an = 0;
                if (numberObject.isHit()) {
                    an = money * getRateAn(numberObject, accountRate,typeTienTe);
                }
                tiendanh += danh;
                tienan += an;
                ketqua += type == 1 ? an - danh : danh - an;
            }
            totalAll += ketqua;
            View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_total, null, false);
            TextView tvLoai = tableRow.findViewById(R.id.tvType);
            TextView tvTienDanh = tableRow.findViewById(R.id.tvTienDanh);
            tvTienDanh.setGravity(Gravity.RIGHT);
            TextView tvTienAn = tableRow.findViewById(R.id.tvTienAn);
            tvTienAn.setGravity(Gravity.RIGHT);
            TextView tvKetQua = tableRow.findViewById(R.id.tvKetQua);
            tvKetQua.setGravity(Gravity.LEFT);

            tvLoai.setText(loai);
            tvTienDanh.setText(tiendanh == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(tiendanh)));
            tvTienAn.setText(tienan == 0 ? "0" : String.format("%s"+donvi+"", Common.roundMoney(tienan)));
            tvTienAn.setTextColor(getResources().getColor(R.color.red_dark));
            if (ketqua >= 0) {
                tvKetQua.setText(String.format("Lãi: %s"+donvi+"", Common.roundMoney(ketqua)));
                tvKetQua.setTextColor(getResources().getColor(R.color.blue_dark));
            } else {
                tvKetQua.setText(String.format("Lỗ: %s"+donvi+"", Common.roundMoney(ketqua)));
                tvKetQua.setTextColor(getResources().getColor(R.color.red_dark));
            }
            tbTotalDetail.addView(tableRow);
        }
    }

    public static double getRateDanh(LotoNumberObject numberObject, AccountRate accountRate,int typeTienTe) {
        try {
            double donvi;
            if (typeTienTe==TienTe.TIEN_VIETNAM){
                donvi=0.001;
            }
            else {
                donvi=1;
            }
            switch (numberObject.getType()) {
                case TypeEnum.TYPE_DE:
                case TypeEnum.TYPE_DAUDB:
                    return formatRate(accountRate.getDe_danh());
                case TypeEnum.TYPE_LO:
                    return accountRate.getLo_danh()*donvi;
                case TypeEnum.TYPE_3C:
                    return formatRate(accountRate.getBacang_danh()) ;
                case TypeEnum.TYPE_DITNHAT:
                case TypeEnum.TYPE_DAUNHAT:
                    return formatRate(accountRate.getGiainhat_danh()) ;
                case TypeEnum.TYPE_CANGGIUA:
                    return formatRate(accountRate.getCanggiua_danh()) ;
                case TypeEnum.TYPE_XIENGHEP2:
                case TypeEnum.TYPE_XIEN2:
                    return formatRate(accountRate.getXien2_danh());
                case TypeEnum.TYPE_XIENGHEP3:
                case TypeEnum.TYPE_XIEN3:
                    return formatRate(accountRate.getXien3_danh()) ;
                case TypeEnum.TYPE_XIENGHEP4:
                case TypeEnum.TYPE_XIEN4:
                    return formatRate(accountRate.getXien4_danh());
                case TypeEnum.TYPE_XIENQUAY:
                    if (!TextUtils.isEmpty(numberObject.getValue3()) && !TextUtils.isEmpty(numberObject.getValue4())) {
                        return formatRate(accountRate.getXien4_danh()) ;
                    } else if (!TextUtils.isEmpty(numberObject.getValue3())) {
                        return formatRate(accountRate.getXien3_danh()) ;
                    } else {
                        return formatRate(accountRate.getXien2_danh()) ;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1d;
    }

    public static double getRateAn(LotoNumberObject numberObject, AccountRate accountRate,int typeTienTe) {
        try {
            double donvi;
            if (typeTienTe==TienTe.TIEN_VIETNAM){
                donvi=0.001;
            }
            else {
                donvi=1;
            }
            switch (numberObject.getType()) {
                case TypeEnum.TYPE_DE:
                case TypeEnum.TYPE_DAUDB:
                    return accountRate.getDe_an()*donvi;
                case TypeEnum.TYPE_LO:
                    return accountRate.getLo_an() *donvi;
                case TypeEnum.TYPE_3C:
                    return accountRate.getBacang_an() *donvi;
                case TypeEnum.TYPE_DITNHAT:
                case TypeEnum.TYPE_DAUNHAT:
                    return accountRate.getGiainhat_an() *donvi;
                case TypeEnum.TYPE_CANGGIUA:
                    return accountRate.getCanggiua_an() *donvi;
                case TypeEnum.TYPE_XIENGHEP2:
                case TypeEnum.TYPE_XIEN2:
                    return accountRate.getXien2_an() *donvi;
                case TypeEnum.TYPE_XIENGHEP3:
                case TypeEnum.TYPE_XIEN3:
                    return accountRate.getXien3_an() *donvi;
                case TypeEnum.TYPE_XIENGHEP4:
                case TypeEnum.TYPE_XIEN4:
                    return accountRate.getXien4_an() *donvi;
                case TypeEnum.TYPE_XIENQUAY:
                    if (!TextUtils.isEmpty(numberObject.getValue3()) && !TextUtils.isEmpty(numberObject.getValue4())) {
                        return accountRate.getXien4_an() *donvi;
                    } else if (!TextUtils.isEmpty(numberObject.getValue3())) {
                        return accountRate.getXien3_an() *donvi;
                    } else {
                        return accountRate.getXien2_an()*donvi;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1d;
    }

    public static double formatRate(double value){
        double result ;
        if (value<1){
            result=value;
        }else {
            double num =1.0/value;
            int zeros = 0;
            while (num < 1) {
                num *= 10;
                zeros++;
            }
            zeros -= 1;
            String number ="1";
            for (int i= 0;i<=zeros;i++){
                number+="0";
            }
            result=value/Double.parseDouble(number);
        }
        return  result;
    }


    private void displayDataDetail(ArrayList<LotoNumberObject> listNum, LinearLayout layout, TableLayout table, String unit) {
        try {
            if (listNum == null || listNum.isEmpty()) {
                layout.setVisibility(View.GONE);
            } else {
                ArrayList<TableNumberEntity> listTableNum = new ArrayList<>();
                table.removeAllViews();
                layout.setVisibility(View.VISIBLE);
                View tableRowTitle = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_detail, null, false);
                TextView tvTienTitle = tableRowTitle.findViewById(R.id.tvTien);
                if (unit.equalsIgnoreCase(donvi)) {
                    tvTienTitle.setText(TienTe.getValueTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM));
                } else {
                    tvTienTitle.setText("Điểm");
                }
                table.addView(tableRowTitle);
                int stt = 0;
                for (int i = 0; i < listNum.size(); i++) {
                    LotoNumberObject numberObject = listNum.get(i);
                    AccountSetting accountSetting=getAccountSetting(numberObject.getAccountSend());
                    StringBuilder sbSo = new StringBuilder(numberObject.getValue1());
                    if (!TextUtils.isEmpty(numberObject.getValue2())) {
                        sbSo.append(",").append(numberObject.getValue2());
                    }
                    if (!TextUtils.isEmpty(numberObject.getValue3())) {
                        sbSo.append(",").append(numberObject.getValue3());
                    }
                    if (!TextUtils.isEmpty(numberObject.getValue4())) {
                        sbSo.append(",").append(numberObject.getValue4());
                    }

                    double money = 0;
                    int nhay = 0;
                    if (type == 2) {
                        money += numberObject.getMoneyKeep();
                    } else {
                        money += numberObject.getMoneyTake();
                    }
                    nhay += numberObject.getNhay(accountSetting.getSonhaylotrathuongtoida());
                    for (int j = listNum.size() - 1; j > i; j--) {
                        LotoNumberObject numberCheckExist = listNum.get(j);
                        StringBuilder sbSoCheckExist = new StringBuilder(numberCheckExist.getValue1());
                        if (!TextUtils.isEmpty(numberCheckExist.getValue2())) {
                            sbSoCheckExist.append(",").append(numberCheckExist.getValue2());
                        }
                        if (!TextUtils.isEmpty(numberCheckExist.getValue3())) {
                            sbSoCheckExist.append(",").append(numberCheckExist.getValue3());
                        }
                        if (!TextUtils.isEmpty(numberCheckExist.getValue4())) {
                            sbSoCheckExist.append(",").append(numberCheckExist.getValue4());
                        }
                        if (sbSo.toString().equalsIgnoreCase(sbSoCheckExist.toString())) {
                            if (type == 2) {
                                money += numberCheckExist.getMoneyKeep();
                            } else {
                                money += numberCheckExist.getMoneyTake();
                            }
                            listNum.remove(j);
                        }
                    }
                    TableNumberEntity entity = new TableNumberEntity(sbSo.toString(), money, numberObject.isHit());
                    entity.setNhay(nhay);
                    listTableNum.add(entity);
                }

                Collections.sort(listTableNum, new Comparator<TableNumberEntity>() {
                    @Override
                    public int compare(TableNumberEntity t1, TableNumberEntity t2) {
                        return Double.compare(t2.getTien(), t1.getTien());
                    }
                });

                for (TableNumberEntity tableNumberEntity : listTableNum) {
                    stt++;
                    View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_detail_row_detail, null, false);
                    TextView tvSTT = tableRow.findViewById(R.id.tvSTT);
                    tvSTT.setTypeface(Typeface.DEFAULT);
                    tvSTT.setTextColor(getResources().getColor(R.color.blue_dark));
                    TextView tvSo = tableRow.findViewById(R.id.tvSo);
                    TextView tvNhay = tableRow.findViewById(R.id.tvNhay);
                    TextView tvTien = tableRow.findViewById(R.id.tvTien);
                    tvTien.setTypeface(Typeface.DEFAULT);
                    tvTien.setGravity(Gravity.RIGHT);

                    tvSTT.setText(String.valueOf(stt));
                    tvSo.setText(tableNumberEntity.getSo());
                    if (tableNumberEntity.isHit()) {
                        tvSo.setTextColor(getResources().getColor(R.color.red_dark));
                        if (tableNumberEntity.getNhay() > 1) {
                            tvNhay.setVisibility(View.VISIBLE);
                            tvNhay.setText(String.valueOf(tableNumberEntity.getNhay()));
                        } else {
                            tvNhay.setVisibility(View.GONE);
                        }
                    } else {
                        tvSo.setTextColor(getResources().getColor(R.color.back_1));
                        tvNhay.setVisibility(View.GONE);
                    }
                    tvTien.setText(String.format("%s%s", Common.roundMoney(tableNumberEntity.getTien()), unit));
                    table.addView(tableRow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {
            rlDate.setOnClickListener(dateListener);
            btnGetData.setOnClickListener(getDataListener);
            groupType.setOnCheckedChangeListener(onChangeTypeListener);
            rlAccount.setOnClickListener(accountListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener accountListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                if (customerPopup == null || !customerPopup.isShowing()) {
                    customerPopup = new ChooseCustomerPopup(getActivity(), listCustomer, chooseCustomerDone);
                    customerPopup.showAsDropDown(view, -100, 0);
                } else {
                    customerPopup.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private ChooseCustomerAdapter.ItemListener chooseCustomerDone = new ChooseCustomerAdapter.ItemListener() {
        @Override
        public void clickItem(AccountObject entity) {
            try {
                currentCustomer = entity;
                tvAcount.setText(currentCustomer.getAccountName());
                customerPopup.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                        , chooseDateDoneListener, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener chooseDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, day);
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, day);
                tvDate.setText(DateTimeUtils.convertDateToString(startDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener getDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener onChangeTypeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            try {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbNhanVe:
                        type = 0;
                        break;
                    case R.id.rbChuyenDi:
                        type = 1;
                        break;
                    case R.id.rbGiuLai:
                        type = 2;
                        break;
                    case R.id.rbTatCa:
                        type = 3;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}