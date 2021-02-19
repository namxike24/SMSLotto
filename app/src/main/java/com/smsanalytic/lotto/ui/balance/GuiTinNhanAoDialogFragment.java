package com.smsanalytic.lotto.ui.balance;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.ui.balance.adapter.AccountBossListAdapter;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDebt;

public class GuiTinNhanAoDialogFragment extends DialogFragment implements IClickListener {

    @BindView(R.id.rvAccount)
    RecyclerView rvAccount;
    @BindView(R.id.btnNext)
    TextView btnNext;
    private View view;
    private List<AccountObject> accountObjects;
    private AccountBossListAdapter accountBossListAdapter;
    private AccountObject accountObjectSelected;
    private DaoSession daoSession;
    private String sms;




    public static GuiTinNhanAoDialogFragment newInstance(String sms) {
        Bundle args = new Bundle();
        GuiTinNhanAoDialogFragment fragment = new GuiTinNhanAoDialogFragment();
        fragment.setArguments(args);
        fragment.sms= sms;
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
            view = inflater.inflate(R.layout.dialog_gui_tin_nhan_ao, container, false);
            ButterKnife.bind(this, view);
            daoSession= MyApp.getInstance().getDaoSession();
            initAdapter();

            btnNext.setOnClickListener(v -> {
                if (accountObjectSelected != null) {
                    int smsType = SmsType.SMS_NORMAL;
                    if (accountObjectSelected.getAccountStatus() == AccountStatus.ACCOUNT_FB) {
                        smsType = SmsType.SMS_FB;
                    } else if (accountObjectSelected.getAccountStatus() == AccountStatus.ACCOUNT_ZALO) {
                        smsType = SmsType.SMS_ZALO;
                    }
                    int statusSMS= SmsStatus.SMS_RECEIVE;
                    if (accountObjectSelected.getAccountType() == AccountType.BOSS){
                        statusSMS=SmsStatus.SMS_SENT;
                    }
                    SmsObject  smsObjectNew = new SmsObject(accountObjectSelected.getAccountName(), sms, sms, smsType, accountObjectSelected.getIdAccount(), Common.getCurrentTimeLong(), statusSMS);
                    smsObjectNew.setGuid(UUID.randomUUID().toString());


                    SmsSuccessObject smsSuccessObject;
                    smsSuccessObject = ProcessSms.processSms(smsObjectNew.getSmsRoot(), accountObjectSelected);
                        if (smsSuccessObject.isProcessSuccess()) {
                            smsObjectNew.setSuccess(true);
                            smsObjectNew.setSmsProcessed(smsSuccessObject.getValue());
                            for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                                lotoNumberObject.setGuid(smsObjectNew.getGuid());
                                lotoNumberObject.setAccountSend(smsObjectNew.getIdAccouunt());
                                lotoNumberObject.setDateTake(smsObjectNew.getDate());
                                lotoNumberObject.setDateString(Common.convertDateByFormat(smsObjectNew.getDate(), Common.FORMAT_DATE_DD_MM_YYY_2));
                                lotoNumberObject.setSmsStatus(smsObjectNew.getSmsStatus());
                                daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                            }
                            // xử lý công nợ
                            processDebt(daoSession, smsSuccessObject.getListDataLoto(), accountObjectSelected, Common.getTypeTienTe(getContext()));
                            daoSession.getSmsObjectDao().insert(smsObjectNew);

                        }
                    Toast.makeText(getContext(), "Gửi tin thành công", Toast.LENGTH_SHORT).show();
                        dismiss();
                } else {
                    Toast.makeText(getContext(), "Bạn phải chọn người gửi tin nhắn", Toast.LENGTH_SHORT).show();
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
        accountObjects =daoSession.getAccountObjectDao().loadAll();
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
