package com.smsanalytic.lotto.ui.smsManagement;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.eventbus.UpdateSMS;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.sms.SmsManagementObject;
import com.smsanalytic.lotto.ui.message.ProcessMessageActivity;
import com.smsanalytic.lotto.ui.smsManagement.adapter.SmsManagementAdapter;
import com.smsanalytic.lotto.ui.smsSocial.SmsDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsAccountFragment extends Fragment implements IClickListener {
    @BindView(R.id.btnAddSMS)
    FloatingActionButton btnAddSMS;
    private View view;

    @BindView(R.id.rvSmsManagemnt)
    RecyclerView rvSmsManagemnt;
    List<SmsManagementObject> smsObjectList;
    SmsManagementAdapter smsManagementAdapter;
    private DaoSession daoSession;
    @BindView(R.id.mes_empty)
    TextView mesEmpty;

    public SmsAccountFragment() {
        // Required empty public constructor
    }

    public static SmsAccountFragment newInstance() {
        Bundle args = new Bundle();
        SmsAccountFragment fragment = new SmsAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_account, container, false);
            ButterKnife.bind(this, view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            initAdapter();
            Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
            Long dateEnd = Common.addHourToDate(currentDate, 24);
            if (currentDate != null && dateEnd != null) {
                initDataSms(currentDate, dateEnd);
            }

        }
        return view;
    }


    /**
     * Hàm khởi tạo dữ liệu
     */
    private void initDataSms(Long dateSelected, Long dateEnd) {
        try {
            smsObjectList.clear();
            List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().list();
            for (AccountObject accountObject : accountList) {
                long countSuccess = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), SmsObjectDao.Properties.IsSuccess.eq(true),
                        SmsObjectDao.Properties.Date.between(dateSelected, dateEnd)
                ).count();
                List<SmsObject> smsAll = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), SmsObjectDao.Properties.Date.between(dateSelected, dateEnd)).list();
                if (smsAll.size() > 0) {
                    SmsManagementObject smsManagementObject = new SmsManagementObject(accountObject.getAccountName(), getString(R.string.tv_sms_success, countSuccess, smsAll.size()), smsAll.size() - (int) countSuccess, smsAll);
                    smsObjectList.add(smsManagementObject);
                }
            }
            if (smsObjectList.size() > 0) {
                mesEmpty.setVisibility(View.GONE);
            } else {
                mesEmpty.setVisibility(View.VISIBLE);
            }
            smsManagementAdapter.notifyDataSetChanged();


            btnAddSMS.setOnClickListener(v -> {
                Intent intentProcess = new Intent(getContext(), ProcessMessageActivity.class);
                startActivity(intentProcess);
            });
        } catch (Exception e) {
        }
    }

    private void initAdapter() {
        smsObjectList = new ArrayList<>();
        smsManagementAdapter = new SmsManagementAdapter(smsObjectList, getContext());
        smsManagementAdapter.setiClickListenerl(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsManagemnt.setAdapter(smsManagementAdapter);
        rvSmsManagemnt.setLayoutManager(layoutManager);

    }

    @Override
    public void clickEvent(View view, int p) {
        switch (view.getId()) {
            case R.id.rlItem:
                SmsManagementObject smsManagementObject = smsObjectList.get(p);
                Intent intent = new Intent(getContext(), SmsDetailActivity.class);
                intent.putExtra(SmsDetailActivity.SMS_KEY, new Gson().toJson(smsManagementObject.getSmsObject().get(0)));
                intent.putExtra(SmsDetailActivity.LIST_SMS_KEY, new Gson().toJson(smsManagementObject.getSmsObject()));
                intent.putExtra(SmsDetailActivity.FROM_SCREEN, SmsDetailActivity.FROM_SMS_ACCOUNT);
                startActivityForResult(intent, 1111);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1111) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("result", false);
                if (result) {
                    Long currcenrdate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
                    Long dateEnd = Common.addHourToDate(currcenrdate, 24);
                    if (currcenrdate != null && dateEnd != null) {
                        initDataSms(currcenrdate, dateEnd);
                    }
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushNotification(PushNotificationEvent event) {
        Long currcenrdate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currcenrdate, 24);
        if (currcenrdate != null && dateEnd != null) {
            initDataSms(currcenrdate, dateEnd);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSMS(UpdateSMS event) {
        Long currcenrdate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currcenrdate, 24);
        if (currcenrdate != null && dateEnd != null) {
            initDataSms(currcenrdate, dateEnd);
        }
    }

    public void updateDateByDate(String date) {
        Long dateSelected = Common.convertDateToMiniSeconds(date, Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = null;
        if (dateSelected != null) {
            dateEnd = Common.addHourToDate(dateSelected, 24);
        }
        if (dateSelected != null && dateEnd != null) {
            initDataSms(dateSelected, dateEnd);
        }
    }
}
