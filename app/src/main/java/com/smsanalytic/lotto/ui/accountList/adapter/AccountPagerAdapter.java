package com.smsanalytic.lotto.ui.accountList.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;


public class AccountPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> list;


    public AccountPagerAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    public AccountPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Số ĐT và Giá";
                break;
            case 1:
                title = "Cấu hình riêng";
                break;
        }
        return title;
    }
}
