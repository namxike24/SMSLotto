package com.smsanalytic.lotto.ui.smsDetail;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.accountList.adapter.AccountListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsOfAccountDetailFragment extends BaseFragment implements IClickListener {

    MainLoToActivity activity;
    @BindView(R.id.rvSmsDetail)
    RecyclerView rvSmsDetail;
    @BindView(R.id.btnLayNgayHienTai)
    TextView btnLayNgayHienTai;
    private View view;
    AccountListAdapter accountListAdapter;
    private List<AccountObject> accountObjects;
    DaoSession daoSession;
    private long dateStart;
    private long dateEnd;

    public SmsOfAccountDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_detail, container, false);
            ButterKnife.bind(this,view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            activity = (MainLoToActivity) getActivity();
            activity.checkGetXSMB();
            initAdapter();
            initDateTime();
        }
        return view;
    }

    private void initAdapter() {
        accountObjects = new ArrayList<>();
        accountListAdapter = new AccountListAdapter(accountObjects, getContext());
        accountListAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsDetail.setAdapter(accountListAdapter);
        rvSmsDetail.setLayoutManager(layoutManager);
        List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().list();
        accountObjects.addAll(list);
        accountListAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickEvent(View view, int p) {
        if (view.getId()==R.id.rlItem){
            Intent intent= new Intent(getContext(),SmsDetailOfAccountActivity.class);
            intent.putExtra(SmsDetailOfAccountActivity.ACCOUNT_ID_KEY,new Gson().toJson(accountObjects.get(p)));
            intent.putExtra(SmsDetailOfAccountActivity.DATE_START_KEY,dateStart);
            intent.putExtra(SmsDetailOfAccountActivity.DATE_END_KEY,dateEnd);
            startActivity(intent);
        }
    }

    /**
     * Hàm khởi tạo thời gian
     */
    private void initDateTime() {
       try {
           activity.getTvDate().setText(Common.getCurrentTime());
           dateStart = Common.convertDateToMiniSeconds(Common.getCurrentTime(),Common.FORMAT_DATE_DD_MM_YYY);
           dateEnd = Common.addHourToDate(dateStart, 24);
           getData(dateStart,dateEnd);
           activity.getLnDate().setOnClickListener(v -> {
               int mYear, mMonth, mDay, mHour, mMinute;
               // Get Current Date
               final Calendar c = Calendar.getInstance();
               mYear = c.get(Calendar.YEAR);
               mMonth = c.get(Calendar.MONTH);
               mDay = c.get(Calendar.DAY_OF_MONTH);
               DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                       (view, year, monthOfYear, dayOfMonth) -> {
                           String dateSelected = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                           activity.getTvDate().setText(dateSelected);
                           dateStart = Common.convertDateToMiniSeconds(dateSelected,Common.FORMAT_DATE_DD_MM_YYY);
                           dateEnd = Common.addHourToDate(dateStart, 24);
                           if (dateStart<Common.convertDateToMiniSeconds(Common.getCurrentTime(),Common.FORMAT_DATE_DD_MM_YYY)){
                                btnLayNgayHienTai.setVisibility(View.VISIBLE);
                           }
                           else {
                               btnLayNgayHienTai.setVisibility(View.GONE);
                           }
                           getData(dateStart,dateEnd);
                       }, mYear, mMonth, mDay);
               datePickerDialog.show();
           });

           btnLayNgayHienTai.setOnClickListener(v -> {
               dateStart = Common.convertDateToMiniSeconds(Common.getCurrentTime(),Common.FORMAT_DATE_DD_MM_YYY);
               dateEnd = Common.addHourToDate(dateStart, 24);
               getData(dateStart,dateEnd);
               activity.getTvDate().setText(Common.getCurrentTime());
               btnLayNgayHienTai.setVisibility(View.GONE);
           });
       }
       catch (Exception e){}
    }

    private void getData(long dateStart, long dateEnd) {
        List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().list();
        if (accountList.size() > 0) {
            for (int i = accountList.size() - 1; i >= 0; i--) {
                long countSms = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountList.get(i).getIdAccount()),
                        SmsObjectDao.Properties.Date.between(dateStart, dateEnd)
                ).count();
                if (countSms <= 0) {
                    accountList.remove(i);
                }
            }
        }
        accountObjects.clear();
        accountObjects.addAll(accountList);
        accountListAdapter.notifyDataSetChanged();
    }
}
