package com.smsanalytic.lotto.ui.accountList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.database.XSMBObject;
import com.smsanalytic.lotto.database.XSMBObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.ui.accountList.adapter.AccountPagerAdapter;
import com.smsanalytic.lotto.ui.balance.GuiTinCanChuyenActivity;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDebt;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDeleteDebt;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_3C;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_CANGGIUA;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DAUDB;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DAUG1;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DE;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DUOIG1;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_LO;

public class AddAccountActivity extends BaseActivity {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    public AccountRate accountRate;
    public AccountSetting accountSetting;
    public AccountObject accountObject;
    @BindView(R.id.btnAdd)
    TextView btnAdd;
    private DaoSession daoSession;
    public final static int REQUEST_ADD_ACCOUNT = 1000;
    public final static int UPDATE_ACCOUNT_TYPE = 1; // type update khách hàng
    public final static int CREATE_NEW_ACCOUNT_TYPE = 2;// type add khách hàng
    public final static String STATUS_SCREEN = "statusScreen";
    public final static String SMS_INFO_KEY = "smsinfor"; // thông tin sms truyền khi thêm mới khách hàng từ tin nhắn mạng xã hội
    public final static String ACCOUNT_UPDATE_INFO_KEY = "accountUpdateInfoKey";
    AccountSettingFragment accountSettingFragment;
    AccountInfoFragment accountInfoFragment;
    List<Fragment> list;
    private SmsObject smsObjectInfo;
    private int statusScreen;//statusScreen=2 thêm , statusScreen=1 sửa khách hàng

    private View view;
    private int[] tabIcons = {
            R.drawable.ic_account,
            R.drawable.ic_setting_black,
    };

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        ButterKnife.bind(this);

        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        // lấy dữ liệu account default;
        String dataAccountRateDefault = Common.loadJSONFromAsset(this, "AccountRateDefault.json");
        accountRate = new Gson().fromJson(dataAccountRateDefault, AccountRate.class);
        String dataAccountSettingDefault = Common.loadJSONFromAsset(this, "AccountSettingDefault.json");
        accountSetting = new Gson().fromJson(dataAccountSettingDefault, AccountSetting.class);
        getBundle();
        addControl();
        setupTabIcons();

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(SMS_INFO_KEY)) {
            smsObjectInfo = new Gson().fromJson(bundle.getString(SMS_INFO_KEY), SmsObject.class);


        }
        if (bundle.containsKey(STATUS_SCREEN)) {
            statusScreen = bundle.getInt(STATUS_SCREEN);
            if (statusScreen == UPDATE_ACCOUNT_TYPE) {
                tvGroupName.setText(getString(R.string.tv_update_account));
                btnAdd.setText("Lưu danh bạ");
            } else {
                tvGroupName.setText(getString(R.string.tv_add_account));
                btnAdd.setText("Thêm");
            }
        }
        if (bundle.containsKey(ACCOUNT_UPDATE_INFO_KEY)) {
            accountObject = new Gson().fromJson(bundle.getString(ACCOUNT_UPDATE_INFO_KEY), AccountObject.class);
            if (statusScreen == UPDATE_ACCOUNT_TYPE) {
                accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
                accountSetting = new Gson().fromJson(accountObject.getAccountSetting(), AccountSetting.class);
            }
            setAccountObject(accountObject);
        }
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void addControl() {
        list = new ArrayList<>();
        if (accountObject == null) {
            accountObject = new AccountObject();
        }
        accountInfoFragment = AccountInfoFragment.newInstance(smsObjectInfo, statusScreen);
        accountSettingFragment = AccountSettingFragment.newInstance();
        list.add(accountInfoFragment);
        list.add(accountSettingFragment);
        FragmentManager manager = Objects.requireNonNull(this).getSupportFragmentManager();
        AccountPagerAdapter adapter = new AccountPagerAdapter(manager, list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }


    @OnClick({R.id.btnBack, R.id.btnAdd})
    void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnAdd:
                accountObject = accountInfoFragment.getAccountObject();
                accountObject.setAccountSetting(new Gson().toJson(accountSettingFragment.getaccountSetting()));
                if (validateBeforeAdd() && accountInfoFragment.validateEnterInfo()) {
                    if (statusScreen == UPDATE_ACCOUNT_TYPE) {
                        daoSession.getAccountObjectDao().update(accountObject);
                        Toast.makeText(getBaseContext(), "Cập nhật khách hàng thành công", Toast.LENGTH_SHORT).show();
                        processMessageForAccount();
                    } else {
                        daoSession.getAccountObjectDao().save(accountObject);
                        Toast.makeText(getBaseContext(), "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void processMessageForAccount() {
        try {
            List<SmsObject> smsObjectList = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount())).list();
            for (SmsObject smsObject : smsObjectList) {
                if (smsObject.getSmsType() == SmsType.SMS_PROCESSED) {
                    StringProcessEntity processEntity = StringCalculate.processStringOriginal(smsObject.getSmsProcessed());
                    if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
                        for (int i = 0; i < processEntity.getListChild().size(); i++) {
                            StringProcessChildEntity childEntity = processEntity.getListChild().get(i);
                            AccountObject customer = null;
                            List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
                            if (accountList.size() > 0) {
                                customer = accountList.get(0);
                            }
                            StringCalculate.processChildEntity(processEntity, childEntity, customer
                                    , i > 0 && !processEntity.getListChild().get(i - 1).isError() ? processEntity.getListChild().get(i - 1) : null, i);
                        }

                        if (!processEntity.isHasError()) {
                            saveToDB(processEntity, smsObject);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hàm lưu vào cơ sở dữ liệu
     */
    private void saveToDB(StringProcessEntity processEntity, SmsObject smsObject) {
        int typeTiente = Common.getTypeTienTe(this);

            //update tin nhắn
            daoSession.getSmsObjectDao().update(smsObject);

            // xóa các loto trong bản loto
            List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObject.getGuid())).list();
            daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
            // xóa công nợ tin cũ
            processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects,accountObject , typeTiente);


            ArrayList<LotoNumberObject> objectList = new ArrayList<>();
            for (StringProcessChildEntity stringProcessChildEntity : processEntity.getListChild()) {
                objectList.addAll(stringProcessChildEntity.getListDataLoto());
            }
            // lưu lại số loto
            String date = Common.convertDateByFormat((smsObject.getDate()), DateTimeUtils.DAY_MONTH_YEAR_FORMAT);
            List<XSMBObject> listDataXSMB = new ArrayList<>();
            try {
                listDataXSMB = MyApp.getInstance().getDaoSession().getXSMBObjectDao().queryBuilder().where(XSMBObjectDao.Properties.Date.eq(date)).list();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (LotoNumberObject lotoNumberObject : objectList) {
                lotoNumberObject.setGuid(smsObject.getGuid());
                lotoNumberObject.setAccountSend(smsObject.getIdAccouunt());
                lotoNumberObject.setDateTake(smsObject.getDate());
                lotoNumberObject.setDateString(Common.convertDateByFormat((smsObject.getDate()), Common.FORMAT_DATE_DD_MM_YYY_2));
                lotoNumberObject.setSmsStatus(smsObject.getSmsStatus());
                /**
                 * xử lý kết quả trúng
                 */
                processCheckHitLotto(listDataXSMB, lotoNumberObject);
                daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                if (smsObject.getSmsStatus() == SmsStatus.SMS_SENT) {
                    GuiTinCanChuyenActivity.udpateTienGiuLai(daoSession, lotoNumberObject);
                }
            }
            // hàm xử lý công nợ
            processDebt(daoSession, objectList, accountObject, typeTiente);

    }


    private void processCheckHitLotto(List<XSMBObject> listDataXSMB, LotoNumberObject lotto) {
        try {
            if (listDataXSMB != null && !listDataXSMB.isEmpty()) {
                String hit = listDataXSMB.get(0).getResult();
                TypeToken<Map<String, ArrayList<String>>> typeToken = new TypeToken<Map<String, ArrayList<String>>>() {
                };
                Map<String, ArrayList<String>> dataHit = new Gson().fromJson(hit, typeToken.getType());
                if (dataHit != null && !dataHit.isEmpty()) {

                    switch (lotto.getType()) {
                        case TypeEnum.TYPE_DE:
                            if (dataHit.get(KEY_DE).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DAUDB:
                            if (dataHit.get(KEY_DAUDB).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_LO:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())) {
                                int nhay = 0;
                                AccountObject customer = null;
                                try {
                                    List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(lotto.getAccountSend())).list();
                                    if (accountList.size() > 0) {
                                        customer = accountList.get(0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                AccountSetting accountSetting = null;
                                try {
                                    if (customer != null) {
                                        accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                for (String s : dataHit.get(KEY_LO)) {
                                    if (s.equalsIgnoreCase(lotto.getValue1())) {
                                        if (accountSetting != null && accountSetting.getSonhaylotrathuongtoida() <= nhay) {
                                            break;
                                        } else {
                                            nhay++;
                                        }
                                    }
                                }
                                lotto.setHit(true);
                                lotto.setNhay(nhay);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_3C:
                            if (dataHit.get(KEY_3C).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DITNHAT:
                            if (dataHit.get(KEY_DUOIG1).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DAUNHAT:
                            if (dataHit.get(KEY_DAUG1).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_CANGGIUA:
                            if (dataHit.get(KEY_CANGGIUA).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP2:
                        case TypeEnum.TYPE_XIEN2:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP3:
                        case TypeEnum.TYPE_XIEN3:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP4:
                        case TypeEnum.TYPE_XIEN4:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENQUAY:
                            if (!TextUtils.isEmpty(lotto.getValue3()) && !TextUtils.isEmpty(lotto.getValue4())) {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            } else if (!TextUtils.isEmpty(lotto.getValue3())) {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            } else {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            }
                            break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean validateBeforeAdd() {
        boolean validate = true;
        if (accountObject.getAccountName().isEmpty()) {
            validate = false;
            Toast.makeText(getBaseContext(), "Nhập tên khách hàng", Toast.LENGTH_SHORT).show();
        } else if (accountObject.getYouCallThem().isEmpty()) {
            validate = false;
            Toast.makeText(getBaseContext(), "Chưa nhập tên xưng hô bạn gọi người này", Toast.LENGTH_SHORT).show();
        } else if (accountObject.getThemCallYou().isEmpty()) {
            validate = false;
            Toast.makeText(getBaseContext(), "Chưa nhập tên xưng hô người ngày gọi bạn", Toast.LENGTH_SHORT).show();
        } else if (accountObject.getPhone().isEmpty() && smsObjectInfo == null && statusScreen == CREATE_NEW_ACCOUNT_TYPE) {
            validate = false;
            Toast.makeText(getBaseContext(), "Chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
        }
        return validate;
    }


    public AccountRate getAccountRate() {
        return accountRate;
    }

    public void setAccountRate(AccountRate accountRate) {
        this.accountRate = accountRate;
    }

    public AccountObject getAccountObject() {
        return accountObject;
    }

    public void setAccountObject(AccountObject accountObject) {
        this.accountObject = accountObject;
    }

    public AccountSetting getAccountSetting() {
        return accountSetting;
    }

    public void setAccountSetting(AccountSetting accountSetting) {
        this.accountSetting = accountSetting;
    }
}
