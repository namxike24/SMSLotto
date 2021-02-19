package com.smsanalytic.lotto.ui.debt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.model.debt.AnalyticDebt;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateAn;
import static com.smsanalytic.lotto.ui.document.DocumentFragment.getRateDanh;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyticDebtFragment extends Fragment {
    @BindView(R.id.tvDateFrom)
    TextView tvDateFrom;
    @BindView(R.id.rlDateFrom)
    RelativeLayout rlDateFrom;
    @BindView(R.id.tvDateTo)
    TextView tvDateTo;
    @BindView(R.id.rlDateTo)
    RelativeLayout rlDateTo;
    @BindView(R.id.btnLayDuLieu)
    Button btnLayDuLieu;
    @BindView(R.id.tbPhanTich)
    TableLayout tbPhanTich;
    private View view;
    private DaoSession daoSession;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    public AnalyticDebtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_analytic_debt, container, false);
            ButterKnife.bind(this, view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            setDateDefault();
            setDataDefault();
            getData(startDate.getTimeInMillis(), endDate.getTimeInMillis());
            clickEvent();

        }

        return view;
    }
    String donvi;
    SettingDefault settingDefault;
    private void setDataDefault() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(getActivity(), "SettingDefault.json");

            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getValueTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
    }
    private void setDateDefault() {
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        startDate.add(Calendar.YEAR,-1);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        tvDateFrom.setText(Common.convertDateByFormat(startDate.getTimeInMillis(), Common.FORMAT_DATE_DD_MM_YYY_2));
        tvDateTo.setText(Common.convertDateByFormat(endDate.getTimeInMillis(), Common.FORMAT_DATE_DD_MM_YYY_2));
    }

    private void clickEvent() {
        rlDateFrom.setOnClickListener(v -> {
            Common.disableView(view);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, year, month, dayOfMonth) -> {
                startDate.set(Calendar.YEAR, year);
                startDate.set(Calendar.MONTH, month);
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                tvDateFrom.setText(Common.convertDateByFormat(startDate.getTimeInMillis(),Common.FORMAT_DATE_DD_MM_YYY_2));
            }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        rlDateTo.setOnClickListener(v -> {
            Common.disableView(view);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, year, month, dayOfMonth) -> {
                endDate.set(Calendar.YEAR, year);
                endDate.set(Calendar.MONTH, month);
                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                tvDateTo.setText(Common.convertDateByFormat(endDate.getTimeInMillis(),Common.FORMAT_DATE_DD_MM_YYY_2));
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        btnLayDuLieu.setOnClickListener(v -> {
            getData(startDate.getTimeInMillis(), endDate.getTimeInMillis());
        });
    }

    public static AnalyticDebtFragment newInstance() {
        Bundle args = new Bundle();
        AnalyticDebtFragment fragment = new AnalyticDebtFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getData(long start, long end) {
        int typeTiente=Common.getTypeTienTe(getContext());
        tbPhanTich.removeAllViews();
        List<AnalyticDebt> analyticDebtList = new ArrayList<>();
        List<AccountObject> accountObjects = daoSession.getAccountObjectDao().loadAll();
        for (AccountObject accountObject : accountObjects) {
            ArrayList<Integer> deType = new ArrayList<>();
            deType.add(TypeEnum.TYPE_DE);
            deType.add(TypeEnum.TYPE_DAUDB);
            deType.add(TypeEnum.TYPE_DAUNHAT);
            deType.add(TypeEnum.TYPE_DITNHAT);
            double de = analyticByType(daoSession,start, end, accountObject, deType,typeTiente);
            ArrayList<Integer> loType = new ArrayList<>();
            loType.add(TypeEnum.TYPE_LO);
            double lo = analyticByType(daoSession,start, end, accountObject, loType,typeTiente);
            ArrayList<Integer> xienType = new ArrayList<>();
            xienType.add(TypeEnum.TYPE_XIEN2);
            xienType.add(TypeEnum.TYPE_XIEN3);
            xienType.add(TypeEnum.TYPE_XIEN4);
            xienType.add(TypeEnum.TYPE_XIENGHEP2);
            xienType.add(TypeEnum.TYPE_XIENGHEP3);
            xienType.add(TypeEnum.TYPE_XIENGHEP4);
            xienType.add(TypeEnum.TYPE_XIENQUAY);
            double xien = analyticByType(daoSession,start, end, accountObject, xienType,typeTiente);
            ArrayList<Integer> cangType = new ArrayList<>();
            cangType.add(TypeEnum.TYPE_3C);
            cangType.add(TypeEnum.TYPE_CANGGIUA);
            double baCang = analyticByType(daoSession,start, end, accountObject, cangType,typeTiente);
            AnalyticDebt analyticDebt = new AnalyticDebt(accountObject.getIdAccount(), accountObject.getAccountName(), de, lo, xien, baCang);
            analyticDebt.setTatCa(de + lo + xien + baCang);
            analyticDebtList.add(analyticDebt);
        }
        View tableRowTitle = LayoutInflater.from(getContext()).inflate(R.layout.view_analytic_title, null, false);
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
        tbPhanTich.addView(tableRowTitle);

        int count = 0;
        double tongDe = 0;
        double tongLo = 0;
        double tongXien = 0;
        double tongBaCang = 0;
        double tongTatCa = 0;
        for (AnalyticDebt analyticDebt : analyticDebtList) {
            count++;
            tongDe += analyticDebt.getTienDe();
            tongLo += analyticDebt.getTienLo();
            tongXien += analyticDebt.getTienXien();
            tongBaCang += analyticDebt.getTienBaCang();
            tongTatCa += analyticDebt.getTatCa();

            View tableRow = LayoutInflater.from(getContext()).inflate(R.layout.view_analytic_row, null, false);
            TableRow btnRow = tableRow.findViewById(R.id.btnRow);
            TextView tvNo = tableRow.findViewById(R.id.tvNo);
            TextView tvAccountName = tableRow.findViewById(R.id.tvAccountName);
            TextView tvDe = tableRow.findViewById(R.id.tvDe);
            TextView tvLo = tableRow.findViewById(R.id.tvLo);
            TextView tvXien = tableRow.findViewById(R.id.tvXien);
            TextView tvBaCang = tableRow.findViewById(R.id.tvBaCang);
            TextView tvTatCa = tableRow.findViewById(R.id.tvTatCa);
            tvNo.setText(String.valueOf(count));
            tvAccountName.setText(analyticDebt.getAccountName());
            tvDe.setText(Common.roundMoney(analyticDebt.getTienDe()));
            tvLo.setText(Common.roundMoney(analyticDebt.getTienLo()));
            tvXien.setText(Common.roundMoney(analyticDebt.getTienXien()));
            tvBaCang.setText(Common.roundMoney(analyticDebt.getTienBaCang()));
            tvTatCa.setText(Common.roundMoney(analyticDebt.getTatCa()));
            tbPhanTich.addView(tableRow);
            btnRow.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AnalyticDetailActivity.class);
                intent.putExtra(AnalyticDetailActivity.START_DATE_KEY, startDate.getTimeInMillis());
                intent.putExtra(AnalyticDetailActivity.END_DATE_KEY, endDate.getTimeInMillis());
                intent.putExtra(AnalyticDetailActivity.ACCOUNT_ID_KEY, analyticDebt.getIdAccount());
                intent.putExtra(AnalyticDetailActivity.ACCOUNT_NAME_KEY, analyticDebt.getAccountName());
                startActivity(intent);
            });
        }
        View tableRow = LayoutInflater.from(getContext()).inflate(R.layout.view_analytic_footer, null, false);
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
        tbPhanTich.addView(tableRow);
    }

    public static double analyticByType(DaoSession daoSession,long start, long end, AccountObject accountObject, ArrayList<Integer> types, int typeTienTe) {
        List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount()),
                LotoNumberObjectDao.Properties.DateTake.between(start, end), LotoNumberObjectDao.Properties.Type.in(types)).list();
        double ketqua = 0;
        AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
        for (LotoNumberObject numberObject : lotoNumberObjects) {
            double money = numberObject.getMoneyTake();
            double danh = money * getRateDanh(numberObject, accountRate,typeTienTe);
            double an = 0;
            if (numberObject.isHit()) {
                an = money * getRateAn(numberObject, accountRate,typeTienTe);
            }
            ketqua += numberObject.getSmsStatus() == SmsStatus.SMS_RECEIVE ? an - danh : danh - an;
        }
        return ketqua;
    }
}
