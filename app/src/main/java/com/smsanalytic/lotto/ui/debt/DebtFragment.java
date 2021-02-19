package com.smsanalytic.lotto.ui.debt;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.ui.debt.adapter.DebtPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebtFragment extends BaseFragment {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private View view;

    private DebtDetailFragment debtDetailFragment;
    private AnalyticDebtFragment analyticDebtFragment;
    private MainLoToActivity activity;
    public DebtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_debt, container, false);
            ButterKnife.bind(this,view);
            activity = (MainLoToActivity) getActivity();
            activity.checkGetXSMB();
            addControl();
        }
        return view;

    }
    private void addControl() {
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        List<Fragment> list = new ArrayList<>();
        debtDetailFragment = DebtDetailFragment.newInstance();
        analyticDebtFragment =AnalyticDebtFragment.newInstance();
        list.add(debtDetailFragment);
        list.add(analyticDebtFragment);
        DebtPagerAdapter adapter = new DebtPagerAdapter(manager, list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }
}
