package com.smsanalytic.lotto.ui.character;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.ui.smsManagement.adapter.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterFragment extends BaseFragment {
    private View view;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private CharacterDefaultFragment defaultFragment;
    private CharacterCustomFragment customFragment;


    public CharacterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_character, container, false);
                ButterKnife.bind(this, view);
                addControl();
            }
            return view;
    }

    private void addControl() {
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        List<Fragment> list = new ArrayList<>();
        defaultFragment = new CharacterDefaultFragment();
        customFragment = new CharacterCustomFragment();
        list.add(defaultFragment);
        list.add(customFragment);
        PagerAdapter adapter = new PagerAdapter(manager, list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

}
