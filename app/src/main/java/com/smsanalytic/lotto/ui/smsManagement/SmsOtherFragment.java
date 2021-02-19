package com.smsanalytic.lotto.ui.smsManagement;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.smsSocial.adapter.SmsSocialAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsOtherFragment extends Fragment implements IClickListener {
    private View view;
    @BindView(R.id.rvSmsOther)
    RecyclerView rvSmsOther;
    SmsSocialAdapter smsSocialAdapter;
    List<SmsObject> smsObjectList;

    private DaoSession daoSession;

    public SmsOtherFragment() {
        // Required empty public constructor
    }

    public static SmsOtherFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SmsOtherFragment fragment = new SmsOtherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_other, container, false);
            ButterKnife.bind(this, view);
            initAdapter();
        }
        updateData();
        return view;
    }

    private void updateData() {
        List<SmsObject> list = daoSession.getSmsObjectDao().queryBuilder().where(new WhereCondition.StringCondition("1 GROUP BY id_account HAVING sms_type=4")).orderDesc(SmsObjectDao.Properties.Date).build().list();
        smsObjectList.clear();
        smsObjectList.addAll(list);
        smsSocialAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        smsObjectList = new ArrayList<>();
        smsSocialAdapter = new SmsSocialAdapter(smsObjectList, getContext());
        smsSocialAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsOther.setAdapter(smsSocialAdapter);
        rvSmsOther.setLayoutManager(layoutManager);
        daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
    }

    @Override
    public void clickEvent(View view, int p) {

    }
    public void updateDateByDate(String date){

    }
}
