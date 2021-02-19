package com.smsanalytic.lotto.ui.smsManagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.ui.smsManagement.adapter.PagerAdapter;
import com.smsanalytic.lotto.ui.smsSocial.SmsSocialFragment;

public class SmsManagementFragment extends BaseFragment {
    private View view;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    MainLoToActivity activity;
    SmsAccountFragment smsAccountFragment;
    SmsSocialFragment smsOtherFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_management, container, false);

            ButterKnife.bind(this, view);
            activity = (MainLoToActivity) getActivity();
            addControl();
            initDateTime();
        }
        return view;
    }

    /**
     * Hàm khởi tạo thời gian
     */
    private void initDateTime() {
        activity.getTvDate().setText(Common.getCurrentTime());
        activity.getLnDate().setOnClickListener(v -> {
            int mYear, mMonth, mDay, mHour, mMinute;
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String dateSelected=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        activity.getTvDate().setText(dateSelected);
                        if (smsAccountFragment!=null && smsOtherFragment!=null){
                            smsAccountFragment.updateDateByDate(dateSelected);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

    }

    private void addControl() {
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        List<Fragment> list = new ArrayList<>();
        smsAccountFragment = SmsAccountFragment.newInstance();
        smsOtherFragment = SmsSocialFragment.newInstance(SmsSocialFragment.FRAGMENT_OTHER);
        list.add(smsAccountFragment);
        list.add(smsOtherFragment);
        PagerAdapter adapter = new PagerAdapter(manager, list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }


}