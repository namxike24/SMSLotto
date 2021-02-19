package com.smsanalytic.lotto.ui.smsProcess;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.eventbus.PushCountSmsWaitEvent;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDebt;
import static com.smsanalytic.lotto.ui.balance.GuiTinCanChuyenActivity.udpateTienGiuLai;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsWaitProcessFragment extends Fragment {
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvSms)
    TextView tvSms;
    @BindView(R.id.tvSmsRoot)
    TextView tvSmsRoot;
    @BindView(R.id.imageArrowDown)
    ImageView imageArrowDown;
    @BindView(R.id.edtSmsProcessed)
    EditText edtSmsProcessed;
    @BindView(R.id.btnRestore)
    Button btnRestore;
    @BindView(R.id.btnProcessAndSave)
    Button btnProcessAndSave;
    @BindView(R.id.btnSaveSmsEmpty)
    Button btnSaveSmsEmpty;
    @BindView(R.id.tvError)
    TextView tvError;
    @BindView(R.id.lnMainLayout)
    LinearLayout lnMainLayout;
    @BindView(R.id.mes_empty)
    TextView mesEmpty;


    private View view;
    private DaoSession daoSession;
    List<SmsObject> smsWaitProcess;
    private SmsObject smsObjectSelected;

    public SmsWaitProcessFragment() {
        // Required empty public constructor
    }


    public static SmsWaitProcessFragment newInstance() {
        Bundle args = new Bundle();
        SmsWaitProcessFragment fragment = new SmsWaitProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_wait_process, container, false);
            ButterKnife.bind(this, view);
            smsWaitProcess = new ArrayList<>();
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            initData();
            clickEvent();
        }
        return view;
    }

    private void clickEvent() {
        // sự kiện khôi phục tin gốc
        btnRestore.setOnClickListener(v -> {
            Common.disableView(v);
            if (smsObjectSelected != null) {
                Common.createDialog(getContext(), "Xác nhận", "Bạn có chắc chắn muốn phục hồi lại tin nhắn gốc không?", new IClickListener() {
                    @Override
                    public void acceptEvent(boolean accept) {
                        if (accept) {
                            edtSmsProcessed.setText(smsObjectSelected.getSmsRoot());
                            edtSmsProcessed.setSelection(edtSmsProcessed.getText().toString().trim().length());
                        }
                    }
                });
            }
        });
        // sự kiện xử lý vào lưu tin nhắn
        btnProcessAndSave.setOnClickListener(v -> {
            Common.disableView(v);
            AccountObject customer = null;
            List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObjectSelected.getIdAccouunt())).list();
            if (accountList.size() > 0) {
                customer = accountList.get(0);
            }
            SmsSuccessObject smsSuccessObject = ProcessSms.processSms(edtSmsProcessed.getText().toString(),customer);
            if (smsSuccessObject.isProcessSuccess()) {
                // lưu số đã xử lý vào bản loto
                for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                    lotoNumberObject.setGuid(smsObjectSelected.getGuid());
                    lotoNumberObject.setAccountSend(smsObjectSelected.getIdAccouunt());
                    lotoNumberObject.setDateTake(smsObjectSelected.getDate());
                    lotoNumberObject.setDateString(Common.convertDateByFormat((smsObjectSelected.getDate()),Common.FORMAT_DATE_DD_MM_YYY_2));
                    lotoNumberObject.setSmsStatus(smsObjectSelected.getSmsStatus());
                    daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                   if (lotoNumberObject.getSmsStatus()==SmsStatus.SMS_SENT){
                       udpateTienGiuLai(daoSession, lotoNumberObject);
                   }
                }
                smsObjectSelected.setSuccess(true);
                smsObjectSelected.setSmsProcessed(smsSuccessObject.getValue());
                daoSession.getSmsObjectDao().update(smsObjectSelected);
                for (int i = 0; i < smsWaitProcess.size(); i++) {
                    if (smsWaitProcess.get(i).getGuid().equals(smsObjectSelected.getGuid())) {
                        smsWaitProcess.remove(i);
                        processGetSmsWait();
                    }
                }
                Toast.makeText(getContext(), "Xử lý thành công", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new PushNotificationEvent(smsObjectSelected));


                // hàm xử lý công nợ
                if (accountList.size() > 0) {
                   int typeTiente=Common.getTypeTienTe(getContext());
                    processDebt(daoSession, smsSuccessObject.getListDataLoto(), accountList.get(0),typeTiente);
                }
            } else {
                edtSmsProcessed.setText(Html.fromHtml(smsSuccessObject.getSmsFormatFailed()));
                edtSmsProcessed.setSelection(edtSmsProcessed.getText().length());
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.tv_error_detail, smsSuccessObject.getMesError()));
            }
        });
        // sự kiện lưu vào tin trống
        btnSaveSmsEmpty.setOnClickListener(v -> {
            Common.disableView(v);
            if (smsObjectSelected != null) {
                Common.createDialog(getContext(), "Xác nhận", "Bạn có chắc chắn muốn lưu tin này vào tin trống?", new IClickListener() {
                    @Override
                    public void acceptEvent(boolean accept) {
                        if (accept) {
                            processUpdateSMSType(SmsType.SMS_EMPTY);
                            EventBus.getDefault().post(new PushNotificationEvent(smsObjectSelected));
                        }
                    }
                });
            }
        });

    }

    private void processUpdateSMSType(int smsType) {
        try {
            smsObjectSelected.setSmsType(smsType);
            daoSession.getSmsObjectDao().update(smsObjectSelected);
            for (int i = 0; i < smsWaitProcess.size(); i++) {
                if (smsWaitProcess.get(i).getGuid().equals(smsObjectSelected.getGuid())) {
                    smsWaitProcess.remove(i);
                    processGetSmsWait();
                }
            }
        } catch (Exception e) {

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
    public void onPushNotification(PushNotificationEvent event) {
        initData();
    }

    private void initData() {
        smsWaitProcess.clear();
        int loaiKhachHang = PreferencesManager.getInstance().getValueInt(PreferencesManager.SMS_FROM, 1);
        int smsStatus = loaiKhachHang;
        Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(),Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currentDate, 24);
        List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().list();
        for (AccountObject accountObject : accountList) {
            smsWaitProcess.addAll(daoSession.getSmsObjectDao().queryBuilder().where(
                    SmsObjectDao.Properties.SmsStatus.eq(smsStatus),
                    SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()),
                    SmsObjectDao.Properties.Date.between(currentDate, dateEnd), SmsObjectDao.Properties.IsSuccess.eq(false)
                    , SmsObjectDao.Properties.SmsType.notEq(SmsType.SMS_EMPTY),SmsObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE)).list());
        }
        processGetSmsWait();
    }

    private void processGetSmsWait() {
        try {
            EventBus.getDefault().post(new PushCountSmsWaitEvent(smsWaitProcess.size()));
            if (smsWaitProcess.size() > 0) {
                lnMainLayout.setVisibility(View.VISIBLE);
                smsObjectSelected = smsWaitProcess.get(0);
                tvUserName.setText(smsWaitProcess.get(0).getGroupTitle());
                tvSmsRoot.setText(smsWaitProcess.get(0).getSmsRoot());
                AccountObject customer = null;
                List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsWaitProcess.get(0).getIdAccouunt())).list();
                if (accountList.size() > 0) {
                    customer = accountList.get(0);
                }
                if (NotificationSmsListenerService.phanTichTuDong(customer)){
                    SmsSuccessObject smsSuccessObject = ProcessSms.processSms(smsWaitProcess.get(0).getSmsRoot(),customer);
                    if (!smsSuccessObject.isProcessSuccess()) {
                        if (smsSuccessObject.getSmsFormatFailed() != null) {
                            edtSmsProcessed.setText(Html.fromHtml(smsSuccessObject.getSmsFormatFailed()));
                        }
                        edtSmsProcessed.setSelection(edtSmsProcessed.getText().toString().trim().length());
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(getString(R.string.tv_error_detail, smsSuccessObject.getMesError()));
                    }
                }
                else{
                    edtSmsProcessed.setText(smsWaitProcess.get(0).getSmsRoot());
                    edtSmsProcessed.setSelection(edtSmsProcessed.getText().toString().trim().length());
                }
                mesEmpty.setVisibility(View.GONE);
            } else {
                mesEmpty.setVisibility(View.VISIBLE);
                mesEmpty.setText(getString(R.string.sms_tin_cho_empty));
                lnMainLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }


}
