package com.smsanalytic.lotto.ui.debt.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class DebtPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> list;
    public DebtPagerAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list=list;
    }

    public DebtPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Công nợ";
                break;
            case 1:
                title = "Thống kê lỗ, lãi";
                break;
        }
        return title;
    }
}
