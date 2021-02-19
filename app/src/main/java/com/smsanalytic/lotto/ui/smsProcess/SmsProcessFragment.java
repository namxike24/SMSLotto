package com.smsanalytic.lotto.ui.smsProcess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.eventbus.PushCountSmsWaitEvent;
import com.smsanalytic.lotto.ui.smsProcess.adapter.SmsProcessPagerAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class SmsProcessFragment extends BaseFragment {
    private View view;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.snSmsFrom)
    Spinner snSmsFrom;

    private SmsEmptyFragment smsProcessedFragment;
    private SmsEmptyFragment smsEmptyFragment;
    private SmsWaitProcessFragment smsWaitProcessFragment;
    //Tạo một mảng dữ liệu giả
    String arr[] = {
            "CHỌN LOẠI HÌNH ĐỂ PHÂN TÍCH",
            "TIN NHẮN ĐẾN CỦA ĐẠI LÝ",
            "TIN NHẮN ĐI CHO CÔNG TY"};
    String tab_name[] = {
            "TIN CHỜ",
            "TIN XỬ LÝ XONG",
            "TIN TRỐNG"};

    private List<Fragment> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_process, container, false);
            ButterKnife.bind(this, view);
            addControl();
            createSpinnerAdapter();
        }
        return view;

    }

    /**
     * Hàm khởi tạo spinner
     */
    private void createSpinnerAdapter() {
        //Gán Data source (arr) vào Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item, arr
                );
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_1);
        snSmsFrom.setAdapter(adapter);
        int selected = PreferencesManager.getInstance().getValueInt(PreferencesManager.SMS_FROM, 1);
        snSmsFrom.setSelection(selected);
        snSmsFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = position;
                if (position == 0) {
                    selected = 1;
                    snSmsFrom.setSelection(selected);

                }
                PreferencesManager.getInstance().setValueInt(PreferencesManager.SMS_FROM, selected);
                if (smsEmptyFragment!=null){
                    smsEmptyFragment.selectedAccount();
                }
                if (smsProcessedFragment!=null){
                    smsProcessedFragment.selectedAccount();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Hàm khởi tạo PagerAdapter
     */
    private void addControl() {
        list = new ArrayList<>();
        smsWaitProcessFragment = SmsWaitProcessFragment.newInstance();
        smsEmptyFragment = SmsEmptyFragment.newInstance(SmsType.SMS_EMPTY);
        smsProcessedFragment = SmsEmptyFragment.newInstance(SmsType.SMS_PROCESSED);
        list.add(smsWaitProcessFragment);
        list.add(smsProcessedFragment);
        list.add(smsEmptyFragment);
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        SmsProcessPagerAdapter adapter = new SmsProcessPagerAdapter(manager, list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        setupTabView();
    }

    TextView countWait;

    public void setupTabView() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(R.layout.tab_view);
            TextView tabName = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tvTabName);
            if (i == 0) {
                countWait = tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tvCountWait);
                countWait.setVisibility(View.VISIBLE);
            }
            tabName.setText("" + tab_name[i]);

        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushCountSmsWaitEvent(PushCountSmsWaitEvent event) {
        if (event.getCountSmsWait() > 0) {
            if (countWait != null) {
                countWait.setVisibility(View.VISIBLE);
                countWait.setText(String.valueOf(event.getCountSmsWait()));
            }
        } else {
            if (countWait != null) {
                countWait.setVisibility(View.GONE);
            }
        }
    }

}