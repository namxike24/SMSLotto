package com.smsanalytic.lotto.ui.smsProcess.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class SmsProcessPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> list;
    public SmsProcessPagerAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list=list;
    }

    public SmsProcessPagerAdapter(@NonNull FragmentManager fm, int behavior) {
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
                title = "Tin chờ";
                break;
            case 1:
                title = "Tin xử lý xong";
                break;
            case 2:
                title = "Tin trống";
                break;
        }
        return title;
    }
}
