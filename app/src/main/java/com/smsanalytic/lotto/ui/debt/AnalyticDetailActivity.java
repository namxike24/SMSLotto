package com.smsanalytic.lotto.ui.debt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.model.debt.AnalyticDebt;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateAn;
import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateDanh;


public class AnalyticDetailActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.tvAccountName)
    TextView tvAccountName;
    @BindView(R.id.tvDes)
    TextView tvDes;
    @BindView(R.id.tbPhanTichChiTiet)
    TableLayout tbPhanTichChiTiet;
    private long startDate;
    private long endDate;
    private String idAccount;
    private String accountName;
    private DaoSession daoSession;

    public static final String START_DATE_KEY = "start_date_key";
    public static final String END_DATE_KEY = "end_date_key";
    public static final String ACCOUNT_ID_KEY = "account_id_key";
    public static final String ACCOUNT_NAME_KEY = "account_name_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic_detail);
        ButterKnife.bind(this);
        daoSession = MyApp.getInstance().getDaoSession();
        btnBack.setOnClickListener(v -> finish());
        setDataDefault();
        getBundle();

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        startDate = bundle.getLong(START_DATE_KEY);
        endDate = bundle.getLong(END_DATE_KEY);
        idAccount = bundle.getString(ACCOUNT_ID_KEY);
        accountName = bundle.getString(ACCOUNT_NAME_KEY);
        tvAccountName.setText(getString(R.string.tv_account_format, accountName));
        tvDes.setText(getString(R.string.tv_deb_des, Common.convertDateByFormat(startDate, Common.FORMAT_DATE_DD_MM_YYY_2), Common.convertDateByFormat(endDate, Common.FORMAT_DATE_DD_MM_YYY_2)));
        getData();
    }
    String donvi;
    SettingDefault settingDefault;
    private void setDataDefault() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(getBaseContext(), "SettingDefault.json");

            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getValueTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
    }
    private void getData() {
        List<AccountObject> accountObjectList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(idAccount)).list();
        if (accountObjectList.size() > 0) {

            View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_analytic_detail_title, null, false);
            TextView tvDeT = tableRowTitle.findViewById(R.id.tvDe);
            TextView tvLoT = tableRowTitle.findViewById(R.id.tvLo);
            TextView tvXienT = tableRowTitle.findViewById(R.id.tvXien);
            TextView tvBaCangT = tableRowTitle.findViewById(R.id.tvBaCang);
            TextView tvTatCaT = tableRowTitle.findViewById(R.id.tvTatCa);

            tvDeT.setText(getString(R.string.de_fomart,donvi));
            tvLoT.setText(getString(R.string.lo_fomart,donvi));
            tvXienT.setText(getString(R.string.xien_fomart,donvi));
            tvBaCangT.setText(getString(R.string.bacang_fomart,donvi));
            tvTatCaT.setText(getString(R.string.tatca_fomart,donvi));


            tbPhanTichChiTiet.addView(tableRowTitle);
            double tongDe = 0;
            double tongLo = 0;
            double tongXien = 0;
            double tongBaCang = 0;
            double tongTatCa = 0;
            for (AnalyticDebt analyticDebt : analyticByType(daoSession, accountObjectList.get(0))) {

                tongDe += analyticDebt.getTienDe();
                tongLo += analyticDebt.getTienLo();
                tongXien += analyticDebt.getTienXien();
                tongBaCang += analyticDebt.getTienBaCang();
                tongTatCa += analyticDebt.getTatCa();

                View tableRow = LayoutInflater.from(this).inflate(R.layout.view_analytic_detail_row, null, false);

                TextView tvDate = tableRow.findViewById(R.id.tvDate);
                TextView tvDe = tableRow.findViewById(R.id.tvDe);
                TextView tvLo = tableRow.findViewById(R.id.tvLo);
                TextView tvXien = tableRow.findViewById(R.id.tvXien);
                TextView tvBaCang = tableRow.findViewById(R.id.tvBaCang);
                TextView tvTatCa = tableRow.findViewById(R.id.tvTatCa);
                tvDate.setText(analyticDebt.getDate());
                tvDe.setText(Common.roundMoney(analyticDebt.getTienDe()));
                tvLo.setText(Common.roundMoney(analyticDebt.getTienLo()));
                tvXien.setText(Common.roundMoney(analyticDebt.getTienXien()));
                tvBaCang.setText(Common.roundMoney(analyticDebt.getTienBaCang()));
                tvTatCa.setText(Common.roundMoney(analyticDebt.getTatCa()));
                tbPhanTichChiTiet.addView(tableRow);

            }
            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_analytic_detail_footer, null, false);
            TextView tvDe = tableRow.findViewById(R.id.tvDe);
            TextView tvLo = tableRow.findViewById(R.id.tvLo);
            TextView tvXien = tableRow.findViewById(R.id.tvXien);
            TextView tvBaCang = tableRow.findViewById(R.id.tvBaCang);
            TextView tvTatCa = tableRow.findViewById(R.id.tvTatCa);
            tvDe.setText(Common.roundMoney(tongDe));
            tvLo.setText(Common.roundMoney(tongLo));
            tvXien.setText(Common.roundMoney(tongXien));
            tvBaCang.setText(Common.roundMoney(tongBaCang));
            tvTatCa.setText(Common.roundMoney(tongTatCa));
            tbPhanTichChiTiet.addView(tableRow);


        }
    }

    public List<AnalyticDebt> analyticByType(DaoSession daoSession, AccountObject accountObject) {
        int typeTiente=Common.getTypeTienTe(this);
        HashMap<String, List<LotoNumberObject>> hashMap = new HashMap<>();
        List<AnalyticDebt> analyticDebts = new ArrayList<>();
        List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount())).orderDesc(LotoNumberObjectDao.Properties.DateTake).list();
        for (LotoNumberObject numberObject : lotoNumberObjects) {
            if (hashMap.containsKey(numberObject.getDateString())) {
                hashMap.get(numberObject.getDateString()).add(numberObject);
            } else {
                List<LotoNumberObject> list = new ArrayList<>();
                list.add(numberObject);
                hashMap.put(numberObject.getDateString(), list);
            }
        }
        AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
        ArrayList<Integer> deType = new ArrayList<>();
        deType.add(TypeEnum.TYPE_DE);
        deType.add(TypeEnum.TYPE_DAUDB);
        deType.add(TypeEnum.TYPE_DAUNHAT);
        deType.add(TypeEnum.TYPE_DITNHAT);
        ArrayList<Integer> loType = new ArrayList<>();
        loType.add(TypeEnum.TYPE_LO);
        ArrayList<Integer> xienType = new ArrayList<>();
        xienType.add(TypeEnum.TYPE_XIEN2);
        xienType.add(TypeEnum.TYPE_XIEN3);
        xienType.add(TypeEnum.TYPE_XIEN4);
        xienType.add(TypeEnum.TYPE_XIENGHEP2);
        xienType.add(TypeEnum.TYPE_XIENGHEP3);
        xienType.add(TypeEnum.TYPE_XIENGHEP4);
        xienType.add(TypeEnum.TYPE_XIENQUAY);
        ArrayList<Integer> cangType = new ArrayList<>();
        cangType.add(TypeEnum.TYPE_3C);
        cangType.add(TypeEnum.TYPE_CANGGIUA);
        for (Map.Entry<String, List<LotoNumberObject>> entry : hashMap.entrySet()) {
            double de = 0;
            double lo = 0;
            double xien = 0;
            double bacang = 0;
            for (LotoNumberObject numberObject : entry.getValue()) {
                if (deType.contains(numberObject.getType())) {
                    de += processMoney(numberObject, accountRate,typeTiente);
                }
                if (loType.contains(numberObject.getType())) {
                    lo += processMoney(numberObject, accountRate,typeTiente);
                }
                if (xienType.contains(numberObject.getType())) {
                    xien += processMoney(numberObject, accountRate,typeTiente);
                }
                if (cangType.contains(numberObject.getType())) {
                    bacang += processMoney(numberObject, accountRate,typeTiente);
                }
            }
            AnalyticDebt analyticDebt = new AnalyticDebt(entry.getKey(), de, lo, xien, bacang);
            analyticDebt.setTatCa(de + lo + xien + bacang);
            analyticDebts.add(analyticDebt);
        }

        return analyticDebts;
    }

    private double processMoney(LotoNumberObject numberObject, AccountRate accountRate, int typeTienTe) {
        double money = numberObject.getMoneyTake();
        double danh = money * getRateDanh(numberObject, accountRate,typeTienTe);
        double an = 0;
        if (numberObject.isHit()) {
            an = money * getRateAn(numberObject, accountRate,typeTienTe);
        }
        return numberObject.getSmsStatus() == SmsStatus.SMS_RECEIVE ? an - danh : danh - an;
    }
}
