package com.smsanalytic.lotto.ui.balance;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.KyTuTinGuiDi;
import com.smsanalytic.lotto.common.SmsSend;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.ui.balance.adapter.SmsSendAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class GuiTinCanChuyenActivity extends AppCompatActivity implements IClickListener {

    public static String ACCOUNT_SELECTED_KEY = "account_select_key";
    public static String SMS_SEND_KEY = "sms_send_key";
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.tvTypeSms)
    TextView tvTypeSms;
    @BindView(R.id.btnMenu)
    ImageView btnMenu;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.rvSmsDetail)
    RecyclerView rvSmsDetail;
    private AccountObject accountObject;
    private List<SmsSend> smsSendList = new ArrayList<>();
    private String smsFormat = "";
    SmsSendAdapter smsSendAdapter;
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui_tin_can_chuyen);
        ButterKnife.bind(this);
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        getBundle();
        initAdapter();
        clickEvent();
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            showPopupProcessSMS(v,smsSendList,R.menu.menu_gui_tat_ca);
        });
    }
    private void showPopupProcessSMS(View view, List<SmsSend> smsObject, int layout) {
        String sms="";

        for (SmsSend smsSend : smsObject){
            sms+=smsSend.getSms()+"\n";
        }

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(layout, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        String finalSms = sms;
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.copyAllSmS: // xử lý tin nhắn
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Đã sao chép", finalSms);
                    clipboard.setPrimaryClip(clip);
                    break;
                case R.id.sendAllSms: // xóa tin nhắn
                    for (int i= 0; i< smsObject.size();i++ ){
                        if (!smsObject.get(i).isSend()){
                            processChuyenTin(i);
                        }
                    }
                    break;
            }
            return true;
        });
        popup.show(); //showing popup menu
    }
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(ACCOUNT_SELECTED_KEY)) {
                accountObject = new Gson().fromJson(bundle.getString(ACCOUNT_SELECTED_KEY), AccountObject.class);
                tvTypeSms.setText(getString(R.string.tv_gui_den, accountObject.getAccountName()));
            }
            if (bundle.containsKey(SMS_SEND_KEY)) {
                TypeToken<List<String>> typeToken = new TypeToken<List<String>>() {
                };
                List<String> smsList = new Gson().fromJson(bundle.getString(SMS_SEND_KEY), typeToken.getType());
                xuLyDoDaiTin(smsList);

            }

        }
    }


    private void xuLyDoDaiTin(List<String> smsList) {
        SettingDefault settingDefault;
        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault != null) {
            if (settingDefault.getDoDaiTinNhan() == KyTuTinGuiDi.TIN_DAI) {

            } else {
                int kytu = 320;
                if (settingDefault.getDoDaiTinNhan() == KyTuTinGuiDi.KY_TU_160) {
                    kytu = 160;
                } else if (settingDefault.getDoDaiTinNhan() == KyTuTinGuiDi.KY_TU_320) {
                    kytu = 320;
                } else if (settingDefault.getDoDaiTinNhan() == KyTuTinGuiDi.KY_TU_480) {
                    kytu = 480;
                }
                String sms = "";
                for (int i = 0; i < smsList.size(); i++) {
                    sms += smsList.get(i);
                    if (sms.length() >= kytu) {
                        String[] strings = sms.split(".");
                        if (strings.length > 0) {
                            smsSendList.add(new SmsSend(strings[0], false));
                            smsSendList.add(new SmsSend(strings[1], false));
                        } else {
                            smsSendList.add(new SmsSend(sms, false));
                        }

                        sms = "";
                    }
                    if (i == smsList.size() - 1 && sms.length() < kytu) {
                        smsSendList.add(new SmsSend(sms, false));
                        sms = "";
                    }
                }
                if (smsSendList.size() == 0) {
                    smsSendList.add(new SmsSend(TextUtils.join(" ", smsList), false));
                }

            }
        }

    }

    private void initAdapter() {
        smsSendAdapter = new SmsSendAdapter(smsSendList, this);
        smsSendAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsDetail.setAdapter(smsSendAdapter);
        rvSmsDetail.setLayoutManager(layoutManager);
    }

    @Override
    public void clickEvent(View view, int p) {
        if (view.getId() == R.id.btnSend) {
            processChuyenTin(p);

        }
    }

    private void processChuyenTin(int p) {
        int smsType;
        if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_FB) {
            smsType = SmsType.SMS_FB;
        } else if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_ZALO) {
            smsType = SmsType.SMS_ZALO;
        } else {
            smsType = SmsType.SMS_NORMAL;
        }
        SmsObject smsObject = new SmsObject(accountObject.getAccountName(), smsSendList.get(p).getSms(), smsSendList.get(p).getSms(), smsType, accountObject.getIdAccount(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
        smsObject.setGuid(UUID.randomUUID().toString());
        // xử lý tin nhắn
        AccountObject customer = null;
        List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
        if (accountList.size() > 0) {
            customer = accountList.get(0);
        }


        boolean sendSuccess = false;
        SmsObject smsObject1 = null;
        if (customer != null) {
            smsObject1 = ProcessSms.replySMS(this, smsObject, smsObject.getSmsProcessed(), daoSession, true);
            // nếu không gửi đc qua mạng xa hội mà account có số điện thoại thfi gửi qua sms
            if (smsObject1 == null) {
                if (!accountObject.getPhone().isEmpty()) {
                    smsObject1 = smsObject;
                    String phone = accountObject.getPhone().split(",")[0];
                    Common.sendSmsAuto(smsObject1.getSmsProcessed(), phone);
                    daoSession.getSmsObjectDao().insert(smsObject1);
                    sendSuccess = true;
                }
            } else {
                sendSuccess = true;
            }
        }
        if (sendSuccess) {
            SmsSuccessObject smsSuccessObject = ProcessSms.processSms(smsObject1.getSmsRoot());
            if (smsSuccessObject.isProcessSuccess()) {
                smsObject1.setSuccess(true);
                smsObject1.setSmsProcessed(smsSuccessObject.getValue());
                for (LotoNumberObject lotoNumberObject : smsSuccessObject.getListDataLoto()) {
                    lotoNumberObject.setGuid(smsObject1.getGuid());
                    lotoNumberObject.setAccountSend(smsObject1.getIdAccouunt());
                    lotoNumberObject.setSmsStatus(smsObject1.getSmsStatus());
                    daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                    udpateTienGiuLai(daoSession, lotoNumberObject);
                }
            } else {
                smsObject1.setSuccess(false);
            }

            smsSendList.get(p).setSend(true);
            smsSendAdapter.notifyItemChanged(p);
            Toast.makeText(this, "Đã chuyển tin nhắn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thê gửi đi, Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
        }

    }


    public static void udpateTienGiuLai(DaoSession daoSession, LotoNumberObject lotoNumberObject) {
        Long currcenrdate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currcenrdate, 24);
        List<LotoNumberObject> lotoNumberObjects;
        if (lotoNumberObject.getType() == TypeEnum.TYPE_XIEN2 ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIEN3 ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIEN4 ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIENQUAY ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIENGHEP4 ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIENGHEP3 ||
                lotoNumberObject.getType() == TypeEnum.TYPE_XIENGHEP2
        ) {

            lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Type.eq(lotoNumberObject.getType()), LotoNumberObjectDao.Properties.XienFormat.eq(lotoNumberObject.getXienFormat()), LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE), LotoNumberObjectDao.Properties.DateTake.between(currcenrdate, dateEnd)).list();
        } else {
            lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Type.eq(lotoNumberObject.getType()), LotoNumberObjectDao.Properties.Value1.eq(lotoNumberObject.getValue1()), LotoNumberObjectDao.Properties.SmsStatus.eq(SmsStatus.SMS_RECEIVE), LotoNumberObjectDao.Properties.DateTake.between(currcenrdate, dateEnd)).list();
        }
        if (lotoNumberObjects != null) {
            Collections.sort(lotoNumberObjects, (s1, s2) -> Long.compare(s2.getDateTake(), s1.getDateTake()));
            double sechuyen = lotoNumberObject.getMoneyTake();
            for (LotoNumberObject s : lotoNumberObjects) {
                double giulai = s.getMoneyTake() - (s.getMoneyTake() - s.getMoneyKeep()) - sechuyen;
                if (giulai >= 0) {
                    s.setMoneyKeep(giulai);
                    sechuyen = 0;
                } else {
                    s.setMoneyKeep(0);
                    sechuyen = giulai * (-1);
                }
                daoSession.getLotoNumberObjectDao().update(s);
            }
        }
    }


}
