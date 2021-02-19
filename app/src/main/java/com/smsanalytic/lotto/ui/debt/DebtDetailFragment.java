package com.smsanalytic.lotto.ui.debt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.DebtObject;
import com.smsanalytic.lotto.database.DebtObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.eventbus.UpdateDebtEvent;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.ui.document.ChooseCustomerPopup;
import com.smsanalytic.lotto.ui.document.adapter.ChooseCustomerAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class DebtDetailFragment extends Fragment {
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.rlDate)
    RelativeLayout rlDate;
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.rlAccount)
    LinearLayout rlAccount;
    @BindView(R.id.btnLayDuLieu)
    Button btnLayDuLieu;
    @BindView(R.id.tbCongNo)
    TableLayout tbCongNo;
    @BindView(R.id.btnCongNoAll)
    Button btnCongNoAll;
    private View view;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private ChooseCustomerPopup customerPopup;
    private ArrayList<AccountObject> listCustomer;
    private DaoSession daoSession;
    private AccountObject currentCustomer;
    private SettingDefault settingDefault;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.debt_detail_fragment, container, false);
            ButterKnife.bind(this, view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            initDataDefault();
            initData();
            setDataDefault();
            Long startcurrentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
            Long endCurrentDate = Common.addHourToDate(startcurrentDate, 24);
            loadData(startcurrentDate, endCurrentDate);
            btnLayDuLieu.setOnClickListener(v -> {
                if (currentCustomer != null) {
                    Intent intent = new Intent(getContext(), DebtOfAccountActivity.class);
                    intent.putExtra(DebtOfAccountActivity.START_DATE_KEY, startDate.getTimeInMillis());
                    intent.putExtra(DebtOfAccountActivity.END_DATE_KEY, endDate.getTimeInMillis());
                    intent.putExtra(DebtOfAccountActivity.ACCOUNT_ID_KEY, currentCustomer.getIdAccount());
                    intent.putExtra(DebtOfAccountActivity.ACCOUNT_NAME_KEY, currentCustomer.getAccountName());
                    startActivity(intent);
                }
            });

            btnCongNoAll.setOnClickListener(v -> {

//                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
//                    //67080000 = 18:38
//                    if (settingDefault.getTimeGuiCongNo() > 0 && Common.convertHHMMtoMilisecond(time) < 67080000) {
//                        Toast.makeText(getContext(), "Thời gian gửi tin nhắn chốt công nợ phải lớn hơn thời gian có kết quả sổ xố!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        getDebtAllAccount();
//                    }

                getDebtAllAccount(daoSession, getActivity(),settingDefault!=null?settingDefault.getTiente(): TienTe.TIEN_VIETNAM);

            });
        }
        return view;
    }
    String donvi;
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
    public static void getDebtAllAccount(DaoSession daoSession, Context context, int tientetype) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        startDate.set(Calendar.MILLISECOND, 0);
        List<AccountObject> accountObjectList = daoSession.getAccountObjectDao().loadAll();
        for (AccountObject accountObject : accountObjectList) {
            int smsType = SmsType.SMS_NORMAL;
            if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_FB) {
                smsType = SmsType.SMS_FB;
            } else if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_ZALO) {
                smsType = SmsType.SMS_ZALO;
            }
            String smsSend = DebtOfAccountActivity.congNo(tientetype,context, daoSession, accountObject, startDate.getTimeInMillis(), endDate.getTimeInMillis(), false).toString();
            SmsObject smsObject = new SmsObject(accountObject.getIdAccount(), smsSend, smsSend, smsType, accountObject.getIdAccount(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
            smsObject.setGuid(UUID.randomUUID().toString());
            SmsObject smsObject1 = ProcessSms.replySMS(context, smsObject, smsObject.getSmsProcessed(), daoSession, true);
            // nếu không gửi đc qua mạng xa hội mà account có số điện thoại thfi gửi qua sms
            if (smsObject1 == null) {
                if (!accountObject.getPhone().isEmpty()) {
                    String phone = accountObject.getPhone().split(",")[0];
                    Common.sendSmsAuto(smsObject.getSmsProcessed(), phone);
                    smsObject.setGuid(UUID.randomUUID().toString());
                    daoSession.getSmsObjectDao().insert(smsObject);
                }
            }
        }

    }

    private void initDataDefault() {
        btnLayDuLieu.setEnabled(false);
        tvDate.setText(Common.getCurrentTime());
        rlDate.setOnClickListener(dateListener);
        rlAccount.setOnClickListener(accountListener);
    }

    public static DebtDetailFragment newInstance() {
        Bundle args = new Bundle();
        DebtDetailFragment fragment = new DebtDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                        , chooseDateDoneListener, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void initData() {
        try {
            listCustomer = new ArrayList<>();
            AccountObject all = new AccountObject();
            all.setIdAccount("All");
            all.setAccountName("Chọn khách hàng");
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
            startDate.set(Calendar.MILLISECOND, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData(long currentDate, long endDate) {
        tbCongNo.removeAllViews();
        List<DebtObject> debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.Date.between(currentDate, endDate)).list();
        View tableRowTitle = LayoutInflater.from(getContext()).inflate(R.layout.view_cong_no_title, null, false);
        TextView tvNoCuT = tableRowTitle.findViewById(R.id.tvTitleNoCu);
        TextView tvPhatSinhT = tableRowTitle.findViewById(R.id.tvTitlePhatSinh);
        TextView tvConLaiT = tableRowTitle.findViewById(R.id.tvTitleConLai);
        tvNoCuT.setText(getString(R.string.nocu_fomart,donvi));
        tvPhatSinhT.setText(getString(R.string.phatsinh_fomart,donvi));
        tvConLaiT.setText(getString(R.string.conlai_fomart,donvi));
        tbCongNo.addView(tableRowTitle);
        int count = 0;
        double tongNoCu = 0;
        double tongPhatSinh = 0;
        double tongConLai = 0;
        for (DebtObject object : debtObjectList) {
            count++;
            tongNoCu += (object.getOldebt() + object.getMoneyReceived() + object.getMoneyPay());
            tongPhatSinh += object.getExpenses();

            View tableRow = LayoutInflater.from(getContext()).inflate(R.layout.view_cong_no_row, null, false);
            TableRow row = tableRow.findViewById(R.id.btnRow);
            TextView tvNo = tableRow.findViewById(R.id.tvNo);
            TextView tvAccountName = tableRow.findViewById(R.id.tvAccountName);
            TextView tvNoCu = tableRow.findViewById(R.id.tvNoCu);
            TextView tvPhatSinh = tableRow.findViewById(R.id.tvPhatSinh);
            TextView tvConLai = tableRow.findViewById(R.id.tvConLai);
            tvNo.setText(String.valueOf(count));
            tvAccountName.setText(object.getAccountName());
            tvNoCu.setText(Common.roundMoney(object.getOldebt() + object.getMoneyReceived() + object.getMoneyPay()));
            tvPhatSinh.setText(Common.roundMoney(object.getExpenses()));
            tvConLai.setText(Common.roundMoney(object.getExpenses() + (object.getOldebt() + object.getMoneyReceived() + object.getMoneyPay())));
            tbCongNo.addView(tableRow);
            row.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DebtOfAccountActivity.class);
                intent.putExtra(DebtOfAccountActivity.START_DATE_KEY, currentDate);
                intent.putExtra(DebtOfAccountActivity.END_DATE_KEY, endDate);
                intent.putExtra(DebtOfAccountActivity.ACCOUNT_ID_KEY, object.getIdAccouunt());
                intent.putExtra(DebtOfAccountActivity.ACCOUNT_NAME_KEY, object.getAccountName());
                startActivity(intent);
            });
        }
        tongConLai = tongNoCu + tongPhatSinh;
        View tableRow = LayoutInflater.from(getContext()).inflate(R.layout.view_cong_no_footer, null, false);
        TextView tvNoCu = tableRow.findViewById(R.id.tvNoCu);
        TextView tvPhatSinh = tableRow.findViewById(R.id.tvPhatSinh);
        TextView tvConLai = tableRow.findViewById(R.id.tvConLai);
        tvNoCu.setText(Common.roundMoney(tongNoCu));
        tvPhatSinh.setText(Common.roundMoney(tongPhatSinh));
        tvConLai.setText(Common.roundMoney(tongConLai));
        tbCongNo.addView(tableRow);

    }


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
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                tvDate.setText(DateTimeUtils.convertDateToString(startDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
                loadData(startDate.getTimeInMillis(), endDate.getTimeInMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


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
                if (entity.getIdAccount().equalsIgnoreCase("All")) {
                    btnLayDuLieu.setEnabled(false);
                } else {
                    btnLayDuLieu.setEnabled(true);
                }
                currentCustomer = entity;
                tvAccount.setText(currentCustomer.getAccountName());
                customerPopup.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDebtEvent(UpdateDebtEvent event) {
        loadData(startDate.getTimeInMillis(), endDate.getTimeInMillis());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
