package com.smsanalytic.lotto.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DemoObject;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.balance.adapter.AccountBossListAdapter;

public class AccountBossDialogFragment extends DialogFragment implements IClickListener {

    @BindView(R.id.rvAccount)
    RecyclerView rvAccount;
    @BindView(R.id.btnNext)
    TextView btnNext;
    private View view;
    private List<AccountObject> accountObjects;
    private AccountBossListAdapter accountBossListAdapter;
    private AccountObject accountObjectSelected;
    private List<String> smsSend;

    private List<DemoObject> smsSendObject;

    public static AccountBossDialogFragment newInstance(List<AccountObject> accountObjects, List<String> smsSend) {
        Bundle args = new Bundle();
        AccountBossDialogFragment fragment = new AccountBossDialogFragment();
        fragment.setArguments(args);
        fragment.accountObjects = accountObjects;
        fragment.smsSend = smsSend;
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.account_boss_dialog_fragment, container, false);
            ButterKnife.bind(this, view);
            initAdapter();
            btnNext.setOnClickListener(v -> {
                if (accountObjectSelected != null) {
                    Intent intent = new Intent(getContext(), GuiTinCanChuyenActivity.class);
                    intent.putExtra(GuiTinCanChuyenActivity.ACCOUNT_SELECTED_KEY, new Gson().toJson(accountObjectSelected));
                    intent.putExtra(GuiTinCanChuyenActivity.SMS_SEND_KEY, new Gson().toJson(smsSend));
                    startActivity(intent);
                    this.dismiss();
                } else {
                    Toast.makeText(getContext(), "Bạn phải chọn người chuyển", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(Common.getScreenWitch(getContext()) - 20, WindowManager.LayoutParams.WRAP_CONTENT);

        }
    }

    private void initAdapter() {
        if (accountObjects == null) {
            accountObjects = new ArrayList<>();
        }
        accountBossListAdapter = new AccountBossListAdapter(accountObjects, getContext());
        accountBossListAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAccount.setAdapter(accountBossListAdapter);
        rvAccount.setLayoutManager(layoutManager);

    }

    @Override
    public void clickEvent(View view, int p) {
        accountObjectSelected = accountObjects.get(p);
    }
}
