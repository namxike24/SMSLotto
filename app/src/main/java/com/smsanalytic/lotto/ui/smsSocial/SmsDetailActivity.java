package com.smsanalytic.lotto.ui.smsSocial;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.eventbus.OnProcessMessageSuccess;
import com.smsanalytic.lotto.eventbus.UpdateSMS;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.message.ProcessMessageActivity;
import com.smsanalytic.lotto.ui.smsSocial.adapter.SmsDetailAdapter;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDeleteDebt;
import static com.smsanalytic.lotto.processSms.ProcessSms.replySMS;


public class SmsDetailActivity extends BaseActivity implements IClickListener {
    @BindView(R.id.rvSmsDetail)
    RecyclerView rvSmsDetail;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.tvTypeSms)
    TextView tvTypeSms;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.btnSend)
    TextView btnSend;
    @BindView(R.id.rlSend)
    RelativeLayout rlSend;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.et_content_sms)
    EditText etContentSms;
    @BindView(R.id.btnChon)
    TextView btnChon;
    private SmsObject smsObject;

    public static final String SMS_KEY = "sms_key";
    public static final String LIST_SMS_KEY = "list_sms_key";
    public static final String FROM_SCREEN = "fromCREEN";
    public static final String FROM_SMS_ACCOUNT = "fromSmsAccount";
    public static final String FROM_SMS_SOCIAL = "fromSmsSocial";
    public static final String FROM_SMS_EMPTY = "fromSmsEmpty";


    List<SmsObject> smsObjectList;
    List<SmsObject> smsObjectListRecive;
    SmsDetailAdapter smsDetailAdapter;
    private String startFromScreen;
    private DaoSession daoSession;

    private AccountObject accountObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        EventBus.getDefault().register(this);
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        ButterKnife.bind(this);
        getBundle();
        initAdapter();

        etContentSms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });
        btnChon.setOnClickListener(v -> {
           try{
               Common.disableView(v);
               if (btnChon.getText().equals("CHỌN")) {
                   btnChon.setText("CHỌN TẤT CẢ");
                   smsDetailAdapter.setShowCheckBox(true);

               } else if (btnChon.getText().equals("CHỌN TẤT CẢ")) {
                   for (SmsObject smsObject : smsObjectList) {
                       smsObject.setCheckbox(true);
                   }
                   btnChon.setText("XÓA");

               } else if (btnChon.getText().equals("XÓA")) {

                   if (smsObjectList.size() > 0) {
                       for (int i = smsObjectList.size() - 1; i >= 0; i--) {
                           if (smsObjectList.get(i).getCheckbox()) {
                               // xóa các loto trong bản loto
                               List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObjectList.get(i).getGuid())).list();
                               daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
                               daoSession.getSmsObjectDao().delete(smsObjectList.get(i));
                               smsObjectList.remove(i);
                               smsDetailAdapter.notifyItemRemoved(i);
                               isChange=true;

                               // xóa công nợ
                               int typeTiente = Common.getTypeTienTe(getBaseContext());

                               if (accountObject !=null) {
                                   processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects, accountObject, typeTiente);
                               }

                           }
                       }
                       smsDetailAdapter.setShowCheckBox(false);
                   }
                   btnChon.setText("CHỌN");

               }
               smsDetailAdapter.notifyDataSetChanged();
           }
           catch (Exception e){}

        });

    }

    private void getBundle() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(FROM_SCREEN)) {
                startFromScreen = bundle.getString(FROM_SCREEN);
            }
            if (bundle.containsKey(SMS_KEY)) {
                String data = bundle.getString(SMS_KEY);
                smsObject = new Gson().fromJson(data, SmsObject.class);

                List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
                if (accountList.size()>0){
                    accountObject=accountList.get(0);
                }
                tvGroupName.setText(smsObject.getGroupTitle());
                if (smsObject.getSmsType() == SmsType.SMS_FB) {
                    tvTypeSms.setText("Facebook Message");
                } else if (smsObject.getSmsType() == SmsType.SMS_ZALO) {
                    tvTypeSms.setText("Zalo");
                } else if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                    tvTypeSms.setText("SMS");
                } else {
                    if (smsObject.getIdAccouunt().contains("ONE_TO_ONE")) {
                        tvTypeSms.setText("Facebook Message");
                    } else {
                        tvTypeSms.setText("SMS");
                    }
                }
            }
            if (bundle.containsKey(LIST_SMS_KEY)) {
                String data = bundle.getString(LIST_SMS_KEY);
                TypeToken<List<SmsObject>> token = new TypeToken<List<SmsObject>>() {
                };
                smsObjectListRecive = new Gson().fromJson(data, token.getType());
            }
        } catch (Exception e) {
        }

    }


    private void initAdapter() {
        smsObjectList = new ArrayList<>();
        smsDetailAdapter = new SmsDetailAdapter(smsObjectList, startFromScreen, this);
        smsDetailAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsDetail.setAdapter(smsDetailAdapter);
        rvSmsDetail.setLayoutManager(layoutManager);
        smsObjectList.clear();
        if (smsObjectListRecive != null) {
            smsObjectList.addAll(smsObjectListRecive);
        } else {
            DaoSession daoSession = ((MyApp) this.getApplication()).getDaoSession();
            List<SmsObject> list = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(smsObject.getIdAccouunt())).orderAsc(SmsObjectDao.Properties.Date).build().list();
            smsObjectList.addAll(list);
        }
        rvSmsDetail.scrollToPosition(smsObjectList.size() - 1);
        smsDetailAdapter.notifyDataSetChanged();
    }
    boolean isChange=false;
    @OnClick(R.id.btnBack)
    void backEvent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",isChange);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnClick(R.id.btnSend)
    void sendSmsEvent() {
        if (!etContentSms.getText().toString().isEmpty()) {
            if (smsObjectList.size() > 0) {
                SmsObject result = replySMS(this, smsObjectList.get(0), etContentSms.getText().toString(), daoSession, false);
                if (result != null) {
                    smsObjectList.add(result);
                    smsDetailAdapter.notifyItemChanged(smsObjectList.size() - 1);
                    rvSmsDetail.scrollToPosition(smsObjectList.size() - 1);
                    etContentSms.setText("");
                    isChange=true;
                } else {
                    AccountObject accountObject = null;
                    List<AccountObject> accountObjectList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObjectList.get(0).getIdAccouunt())).list();
                    if (accountObjectList.size() > 0) {
                        accountObject = accountObjectList.get(0);
                    }

                    if (accountObject != null) {
                        if (!accountObject.getPhone().isEmpty()) {
                            SmsObject smsObjectNew = new SmsObject(smsObjectList.get(0).getIdAccouunt(), etContentSms.getText().toString(), etContentSms.getText().toString(), SmsType.SMS_NORMAL, smsObjectList.get(0).getIdAccouunt(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
                            String phone = accountObject.getPhone().split(",")[0];
                            Common.sendSmsAuto(smsObjectNew.getSmsProcessed(), phone);
                            daoSession.getSmsObjectDao().insert(smsObjectNew);
                            smsObjectList.add(smsObjectNew);
                            smsDetailAdapter.notifyItemChanged(smsObjectList.size() - 1);
                            rvSmsDetail.scrollToPosition(smsObjectList.size() - 1);
                            etContentSms.setText("");
                            isChange=true;
}
                        else {
                            Toast.makeText(getBaseContext(), "Phiên làm việc đã kết thúc. Yêu cầu phát sinh một tin nhắn đến của người này", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Phiên làm việc đã kết thúc. Yêu cầu phát sinh một tin nhắn đến của người này", Toast.LENGTH_SHORT).show();
                    }


//
//
                }
            }
        }
    }


    @Override
    public void clickEvent(View view, int p) {
        switch (view.getId()) {
            case R.id.layoutItem:
                if (startFromScreen.equals(FROM_SMS_ACCOUNT)) {
                    showPopupProcessSMS(view, smsObjectList.get(p), R.menu.menu_process_sms);
                } else if (startFromScreen.equals(FROM_SMS_EMPTY)) {
                    showPopupProcessSMS(view, smsObjectList.get(p), R.menu.menu_empty_sms);
                }

                break;
            case R.id.checkbox:
                btnChon.setText("XÓA");
                smsObjectList.get(p).setCheckbox(!smsObjectList.get(p).getCheckbox());
                smsDetailAdapter.notifyItemChanged(p);
                break;
        }
    }

    private void showPopupProcessSMS(View view, SmsObject smsObject, int layout) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(layout, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.processSmsEvent: // xử lý tin nhắn
                    Intent intentProcess = new Intent(SmsDetailActivity.this, ProcessMessageActivity.class);
                    intentProcess.putExtra(ProcessMessageActivity.MESSAGE, new Gson().toJson(smsObject));
                    startActivity(intentProcess);
                    break;
                case R.id.removeSmsEvent: // xóa tin nhắn
                    processRemoveSms(smsObject);
                    break;
                case R.id.copySmsEvent: // copy tin nhắn
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Đã sao chép", smsObject.getSmsRoot());
                    clipboard.setPrimaryClip(clip);
                    break;
                case R.id.restoreSmsEvent: // khôi phục
                    processRestoreSms(smsObject);
                    break;

            }
            return true;
        });
        popup.show(); //showing popup menu
    }

    private void processRestoreSms(SmsObject smsObject) {
        try {
            Common.createDialog(this, "Xác nhận", "Bạn có chắc chắn muốn phục hồi lại tin nhắn gốc không?", new IClickListener() {
                @Override
                public void acceptEvent(boolean accept) {
                    if (accept) {
                        smsObject.setIsSuccess(false);
                        smsObject.setSmsProcessed(smsObject.getSmsRoot());
                        daoSession.getSmsObjectDao().update(smsObject);
                        for (int i = 0; i < smsObjectList.size(); i++) {
                            if (smsObject.getGuid() != null) {
                                if (smsObjectList.get(i).getGuid().equals(smsObject.getGuid())) {
                                    smsObjectList.set(i, smsObject);
                                    smsDetailAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                        }

                        // xóa các loto trong bản loto
                        List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObject.getGuid())).list();
                        daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
                        // xóa công nợ
                        int typeTiente = Common.getTypeTienTe(getBaseContext());
                        if (accountObject!=null) {
                            processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects,accountObject, typeTiente);
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void processRemoveSms(SmsObject smsObject) {
        Common.createDialog(this, "Xác nhận", "Bạn có chắc chắn muốn xóa tin nhắn này?", new IClickListener() {
            @Override
            public void acceptEvent(boolean accept) {
                if (accept) {
                    // xóa tin nhắn đó khỏi bảng tin nhắn
                    daoSession.getSmsObjectDao().delete(smsObject);
                    for (int i = 0; i < smsObjectList.size(); i++) {
                        if (smsObjectList.get(i).getGuid().equals(smsObject.getGuid())) {
                            smsObjectList.remove(i);
                            smsDetailAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
                // xóa các loto trong bản loto
                List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObject.getGuid())).list();
                daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
                // xóa công nợ
                int typeTiente = Common.getTypeTienTe(getBaseContext());
                if (accountObject !=null) {
                    processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects, accountObject, typeTiente);
                }
            }
        });


    }

    @Subscribe
    public void onEvent(OnProcessMessageSuccess event) {
        try {
            if (event.getData() != null) {
                for (int i = 0; i < smsObjectList.size(); i++) {
                    if (smsObjectList.get(i).getGuid().equals(event.getData().getGuid())) {
                        smsObjectList.set(i, event.getData());
                        smsDetailAdapter.notifyItemChanged(i);
                        EventBus.getDefault().post(new UpdateSMS());
                        isChange=true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
