package com.smsanalytic.lotto.ui.smsDetail;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.model.sms.LotoHitDetail;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class HitDetailActivity extends AppCompatActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.tvTinGoc)
    TextView tvTinGoc;
    @BindView(R.id.tvTinSauXuLy)
    TextView tvTinSauXuLy;
    @BindView(R.id.tvChiTiet)
    TextView tvChiTiet;

    private SmsObject smsObject;
    public final static String SMS_KEY = "sms_key";
    public static final String DATE_START_KEY = "dateStartKey";
    public static final String DATE_END_KEY = "dateEndKey";
    private DaoSession daoSession;
    private long dateStart;
    private long dateEnd;
    private SettingDefault settingDefault;
    private String donvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_detail);
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        ButterKnife.bind(this);
        getDataSetting();
        getBundle();

    }

    private void getDataSetting() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(SMS_KEY)) {
            smsObject = new Gson().fromJson(bundle.getString(SMS_KEY), SmsObject.class);
            dateStart = bundle.getLong(DATE_START_KEY);
            dateEnd = bundle.getLong(DATE_END_KEY);
            processSms(smsObject);
            tvTinGoc.setText(smsObject.getSmsRoot());
            LotoHitDetail lotoHitDetail = new Gson().fromJson(smsObject.getLotoHitDetail(), LotoHitDetail.class);
            if (lotoHitDetail != null) {
                tvChiTiet.setText(lotoHitDetail.getChiTiet());
            }
        }
        btnBack.setOnClickListener(v -> finish());
    }

    private void processSms(SmsObject smsObject) {
        if (smsObject.isSuccess()) {
            StringProcessEntity processEntity = StringCalculate.processStringOriginal(smsObject.getSmsProcessed().trim().toLowerCase());
            if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
                for (int i = 0; i < processEntity.getListChild().size(); i++) {
                    StringProcessChildEntity childEntity = processEntity.getListChild().get(i);
                    AccountObject customer = null;
                    List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
                    if (accountList.size() > 0) {
                        customer = accountList.get(0);
                    }
                    StringCalculate.processChildEntity(processEntity, childEntity, customer
                            , i > 1 && !processEntity.getListChild().get(i - 1).isError() ? processEntity.getListChild().get(i - 1) : null,i);
                }
                List<String> sms = new ArrayList<>();
                HashMap<Integer,List<String>> hitDataList= new HashMap<>();
                if (!processEntity.isHasError()) {
                    List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Hit.eq(true), LotoNumberObjectDao.Properties.DateTake.between(dateStart, dateEnd), LotoNumberObjectDao.Properties.AccountSend.eq(smsObject.getIdAccouunt())).list();

                    for (LotoNumberObject object : lotoNumberObjects){
                            if (hitDataList.containsKey(object.getType())){
                                if (object.getXienFormat()!=null){
                                    hitDataList.get(object.getType()).add(object.getXienFormat());
                                }
                                else{
                                    hitDataList.get(object.getType()).add(object.getValue1());
                                }
                            }
                            else{
                               if (object.getXienFormat()!=null){
                                   List<String> nums=new ArrayList<>();
                                   nums.add(object.getXienFormat());
                                   hitDataList.put(object.getType(),nums);
                               }
                               else {
                                   List<String> nums=new ArrayList<>();
                                   nums.add(object.getValue1());
                                   hitDataList.put(object.getType(),nums);
                               }
                            }
                        }

                    }


                    for (StringProcessChildEntity processChildEntity : processEntity.getListChild()) {
                        sms.addAll(ProcessSms.getListChild(processChildEntity.getListDataLoto(), processChildEntity.getType(), hitDataList,donvi));
                    }
                    tvTinSauXuLy.setText(Html.fromHtml(TextUtils.join(" ", sms)));
                }
            }
        }
    }

