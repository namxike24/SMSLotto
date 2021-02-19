package com.smsanalytic.lotto.ui.character;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.model.WordReplaceEntity;
import com.smsanalytic.lotto.ui.character.adapter.ChooseWordReplaceAdapter;

public class ChooseWordReplacePopup extends PopupWindow {
    private Context context;
    private LayoutInflater inflater;
    private View rootView;

    @BindView(R.id.rcvData)
    RecyclerView rcvData;

    private LinearLayoutManager linearLayoutManager;
    private ArrayList<WordReplaceEntity> listData;
    private ChooseWordReplaceAdapter adapter;
    private ChooseWordReplaceAdapter.ItemListener itemListener;

    public ChooseWordReplacePopup(Context context, ArrayList<WordReplaceEntity> listData, ChooseWordReplaceAdapter.ItemListener itemListener) {
        this.context = context;
        this.listData = listData;
        this.itemListener = itemListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(getLayout(), null, false);
        setContentView(rootView);
        onCreate();
        initView(rootView);
        onViewCreated(rootView);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    private void onViewCreated(View rootView) {

    }

    private void initView(View rootView) {
        try {
            ButterKnife.bind(rootView);
            rcvData = rootView.findViewById(R.id.rcvData);
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            linearLayoutManager = new LinearLayoutManager(context);
            rcvData.setLayoutManager(linearLayoutManager);
            adapter = new ChooseWordReplaceAdapter(context, listData, itemListener);
            rcvData.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCreate() {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            setWidth(width / 3 * 2);
            setHeight(height/3*2);
            setBackgroundDrawable(new ColorDrawable(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected int getLayout() {
        return R.layout.popup_choose_customer;
    }
}
