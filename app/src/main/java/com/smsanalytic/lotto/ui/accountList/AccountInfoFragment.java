package com.smsanalytic.lotto.ui.accountList;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.CurrencyEditText;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.ui.accountList.adapter.PhoneListAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountInfoFragment extends Fragment {
    @BindView(R.id.edt_account_name)
    EditText edtAccountName;
    @BindView(R.id.edt_you_call_them)
    EditText edtYouCallThem;
    @BindView(R.id.edt_them_call_you)
    EditText edtThemCallYou;
    @BindView(R.id.btnAddContact)
    TextView btnAddContact;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.btnAddPhone)
    TextView btnAddPhone;
    @BindView(R.id.rbStaff)
    RadioButton rbStaff;
    @BindView(R.id.rbBoss)
    RadioButton rbBoss;
    @BindView(R.id.rbStaffAndBoss)
    RadioButton rbStaffAndBoss;
    @BindView(R.id.rg_account_type)
    RadioGroup rgAccountType;
    @BindView(R.id.tableRow1)
    TableRow tableRow1;
    @BindView(R.id.cdt_de_danh)
    CurrencyEditText cdtDeDanh;
    @BindView(R.id.cdt_de_an)
    CurrencyEditText cdtDeAn;
    @BindView(R.id.tableRow2)
    TableRow tableRow2;
    @BindView(R.id.cdt_giainhat_danh)
    CurrencyEditText cdtGiainhatDanh;
    @BindView(R.id.cdt_giainhat_an)
    CurrencyEditText cdtGiainhatAn;
    @BindView(R.id.tableRow3)
    TableRow tableRow3;
    @BindView(R.id.cdt_lo_danh)
    CurrencyEditText cdtLoDanh;
    @BindView(R.id.cdt_lo_an)
    CurrencyEditText cdtLoAn;
    @BindView(R.id.tableRow4)
    TableRow tableRow4;
    @BindView(R.id.cdt_c3_danh)
    CurrencyEditText cdtC3Danh;
    @BindView(R.id.cdt_c3_an)
    CurrencyEditText cdtC3An;
    @BindView(R.id.tableRow5)
    TableRow tableRow5;
    @BindView(R.id.cdt_thuongapman_an)
    CurrencyEditText cdtThuongapmanAn;
    @BindView(R.id.cdt_cangiua_danh)
    CurrencyEditText cdtCangiuaDanh;
    @BindView(R.id.cdt_cangiua_an)
    CurrencyEditText cdtCangiuaAn;
    @BindView(R.id.cdt_x2_danh)
    CurrencyEditText cdtX2Danh;
    @BindView(R.id.cdt_x2_an)
    CurrencyEditText cdtX2An;
    @BindView(R.id.cdt_x3_danh)
    CurrencyEditText cdtX3Danh;
    @BindView(R.id.cdt_x3_an)
    CurrencyEditText cdtX3An;
    @BindView(R.id.cdt_x4_danh)
    CurrencyEditText cdtX4Danh;
    @BindView(R.id.cdt_x4_an)
    CurrencyEditText cdtX4An;
    @BindView(R.id.rlAddPhone)
    RelativeLayout rlAddPhone;
    @BindView(R.id.rvPhoneList)
    RecyclerView rvPhoneList;
    @BindView(R.id.tv_title_caidat)
    TextView tvTitleCaidat;
    private View view;
    AccountRate accountRate;
    AccountObject accountObject;
    private List<String> phones;
    private PhoneListAdapter phoneListAdapter;
    private DaoSession daoSession;
    private AddAccountActivity activity;
    private SmsObject smsObjectInfo;
    private int statusScreen;
    private int accountStatus = AccountStatus.ACCOUNT_SMS;

    private static final int CONTACT_REQUEST = 1111;
    private SettingDefault settingDefault;
    public AccountInfoFragment() {
        // Required empty public constructor
    }

    public static AccountInfoFragment newInstance(SmsObject smsObjectInfo, int statusScreen) {

        Bundle args = new Bundle();
        AccountInfoFragment fragment = new AccountInfoFragment();
        fragment.setArguments(args);
        fragment.smsObjectInfo = smsObjectInfo;
        fragment.statusScreen = statusScreen;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_info_account, container, false);
            ButterKnife.bind(this, view);
            activity = (AddAccountActivity) getActivity();
            accountObject = activity.getAccountObject();
            accountRate = activity.getAccountRate();
            daoSession = activity.getDaoSession();
            initDataDefault();
            createPhoneListAdapter();
            setDataDefault();
            setAccountInfo();

        }
        return view;
    }

    private void setAccountInfo() {
        if (accountObject != null && statusScreen == AddAccountActivity.UPDATE_ACCOUNT_TYPE) {
            edtAccountName.setText(accountObject.getAccountName());
            edtYouCallThem.setText(accountObject.getYouCallThem());
            edtThemCallYou.setText(accountObject.getThemCallYou());
            if (accountObject.getPhone() != null) {
                String[] phone = accountObject.getPhone().split(",");
                for (String s : phone) {
                    if ( PhoneNumberUtils.isGlobalPhoneNumber(s)) {
                        phones.add(s);
                    }
                }
            }
            phoneListAdapter.notifyDataSetChanged();
            if (accountObject.getAccountType() == AccountType.STAFF) {
                rbStaff.setChecked(true);
            } else if (accountObject.getAccountType() == AccountType.BOSS) {
                rbBoss.setChecked(true);
            } else if (accountObject.getAccountType() == AccountType.STAFFANDBOSS) {
                rbStaffAndBoss.setChecked(true);
            }
            accountStatus = accountObject.getAccountStatus();
        }
        if (smsObjectInfo!=null && statusScreen ==  AddAccountActivity.CREATE_NEW_ACCOUNT_TYPE ){
            if (smsObjectInfo.getSmsType() == SmsType.SMS_NORMAL){
                phones.add(smsObjectInfo.getIdAccouunt());
                phoneListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setDataDefault() {
        if (smsObjectInfo != null) {
            String accountName = "";
            if (smsObjectInfo.getSmsType() == SmsType.SMS_FB) {
                accountName = "Facebook - " + smsObjectInfo.getGroupTitle();
                accountStatus = AccountStatus.ACCOUNT_FB;
            } else if (smsObjectInfo.getSmsType() == SmsType.SMS_ZALO) {
                accountName = "Zalo - " + smsObjectInfo.getGroupTitle();
                accountStatus = AccountStatus.ACCOUNT_ZALO;
            } else if (smsObjectInfo.getSmsType() == SmsType.SMS_NORMAL) {
                accountName = smsObjectInfo.getGroupTitle();
                accountStatus = AccountStatus.ACCOUNT_SMS;
            }
            edtAccountName.setText(accountName);
        }
    }

    private void createPhoneListAdapter() {
        phones = new ArrayList<>();
        phoneListAdapter = new PhoneListAdapter(phones, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPhoneList.setAdapter(phoneListAdapter);
        rvPhoneList.setLayoutManager(layoutManager);
    }

    private void initDataDefault() {
        // lấy dữ liệu account default;
        if (accountRate != null) {
            cdtDeDanh.setText(Common.formatMoney(accountRate.getDe_danh()));
            cdtDeAn.setText(Common.formatMoney(accountRate.getDe_an()));
            cdtGiainhatDanh.setText(Common.formatMoney(accountRate.getGiainhat_danh()));
            cdtGiainhatAn.setText(Common.formatMoney(accountRate.getGiainhat_an()));
            cdtLoDanh.setText(Common.formatMoney(accountRate.getLo_danh()));
            cdtLoAn.setText(Common.formatMoney(accountRate.getLo_an()));
            cdtC3Danh.setText(Common.formatMoney(accountRate.getBacang_danh()));
            cdtC3An.setText(Common.formatMoney(accountRate.getBacang_an()));
            cdtThuongapmanAn.setText(Common.formatMoney(accountRate.getThuonganman()));
            cdtCangiuaDanh.setText(Common.formatMoney(accountRate.getCanggiua_danh()));
            cdtCangiuaAn.setText(Common.formatMoney(accountRate.getCanggiua_an()));
            cdtX2Danh.setText(Common.formatMoney(accountRate.getXien2_danh()));
            cdtX2An.setText(Common.formatMoney(accountRate.getXien2_an()));
            cdtX3Danh.setText(Common.formatMoney(accountRate.getXien3_danh()));
            cdtX3An.setText(Common.formatMoney(accountRate.getXien3_an()));
            cdtX4Danh.setText(Common.formatMoney(accountRate.getXien4_danh()));
            cdtX4An.setText(Common.formatMoney(accountRate.getXien4_an()));
            // save
        }

        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(getContext(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        String keyTienTe = TienTe.getKeyTienTe2(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
        String valueTienTe = TienTe.getValueTienTe2(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM).toLowerCase();
        tvTitleCaidat.setText(getString(R.string.caidat_hoahong,keyTienTe,valueTienTe));
    }


    @OnClick({R.id.btnAddPhone, R.id.btnAddContact})
    void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhone:
                if (checkPhoneExist(edtPhone.getText().toString())) {
                    if (phones.contains(edtPhone.getText().toString())) {
                        Toast.makeText(getContext(), "Số điện thoại này đã được thêm", Toast.LENGTH_SHORT).show();
                    } else {
                        phones.add(edtPhone.getText().toString());
                        edtPhone.setText("");
                    }
                    phoneListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btnAddContact:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CONTACT_REQUEST);
                break;
        }

    }


    private boolean checkPhoneExist(String phone) {
        try {
            if (phone.length() > 9) {
                List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().list();
                String phoneString = "";
                for (AccountObject accountObject : list) {
                    phoneString += accountObject.getPhone() + ",";
                }
                String[] splitPhone = phoneString.split(",");
                for (String s : splitPhone) {
                    if (s.equals(phone)) {
                        Toast.makeText(getContext(), "Số điện thoại đã sử dụng cho một khách hàng khác", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return true;
            } else {
                Toast.makeText(getContext(), "Số điện thoại chưa đúng", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public AccountRate getAccountRate() {
        accountRate.setDe_danh(cdtDeDanh.getNumericValue());
        accountRate.setDe_an(cdtDeAn.getNumericValue());
        accountRate.setGiainhat_danh(cdtGiainhatDanh.getNumericValue());
        accountRate.setGiainhat_an(cdtGiainhatAn.getNumericValue());
        accountRate.setLo_danh(cdtLoDanh.getNumericValue());
        accountRate.setLo_an(cdtLoAn.getNumericValue());
        accountRate.setBacang_danh(cdtC3Danh.getNumericValue());
        accountRate.setBacang_an(cdtC3An.getNumericValue());
        accountRate.setThuonganman(cdtThuongapmanAn.getNumericValue());
        accountRate.setCanggiua_danh(cdtCangiuaDanh.getNumericValue());
        accountRate.setCanggiua_an(cdtCangiuaAn.getNumericValue());
        accountRate.setXien2_danh(cdtX2Danh.getNumericValue());
        accountRate.setXien2_an(cdtX2An.getNumericValue());
        accountRate.setXien3_danh(cdtX3Danh.getNumericValue());
        accountRate.setXien3_an(cdtX3An.getNumericValue());
        accountRate.setXien4_danh(cdtX4Danh.getNumericValue());
        accountRate.setXien4_an(cdtX4An.getNumericValue());
        return accountRate;
    }

    public AccountObject getAccountObject() {
        accountObject.setAccountName(edtAccountName.getText().toString());
        accountObject.setYouCallThem(edtYouCallThem.getText().toString());
        accountObject.setThemCallYou(edtThemCallYou.getText().toString());
        accountObject.setAccountStatus(accountStatus);
        accountObject.setDateCreate(Common.getCurrentTimeLong());
        accountObject.setPhone(TextUtils.join(",", phoneListAdapter.getList()));
        if (statusScreen == AddAccountActivity.CREATE_NEW_ACCOUNT_TYPE) {
            // nếu smsObjectInfo==null thêm mới từ nút +
            if (smsObjectInfo == null) {
                accountObject.setIdAccount(TextUtils.join(",", phoneListAdapter.getList()));
            } else { // thêm mới từ màn hình tin nhắn
                accountObject.setIdAccount(smsObjectInfo.getIdAccouunt());
            }
        }

        if (rbStaff.isChecked()) {
            accountObject.setAccountType(AccountType.STAFF);
        }
        if (rbBoss.isChecked()) {
            accountObject.setAccountType(AccountType.BOSS);
        }
        if (rbStaffAndBoss.isChecked()) {
            accountObject.setAccountType(AccountType.STAFFANDBOSS);
        }
        accountObject.setAccountRate(new Gson().toJson(getAccountRate()));
        return accountObject;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (CONTACT_REQUEST):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");
                                if (checkPhoneExist(num)) {
                                    if (phones.contains(num)) {
                                        Toast.makeText(getContext(), "Số điện thoại này đã được thêm", Toast.LENGTH_SHORT).show();
                                    } else {
                                        phones.add(num);
                                    }
                                }
                            }
                        }
                    }
                    phoneListAdapter.notifyDataSetChanged();
                    break;
                }
        }
    }


    public boolean validateEnterInfo() {
        boolean validate = true;
        if (cdtDeDanh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền đề đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtDeAn.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền đề trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtGiainhatDanh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền giải nhất đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtGiainhatAn.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền giải nhất trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtLoDanh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền lô đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtLoAn.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền lô trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtC3Danh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền ba càng đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtC3An.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền ba càng trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtThuongapmanAn.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng ap man trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtCangiuaDanh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng càng giữa đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtCangiuaAn.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng càng giữa trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtX2Danh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 2 đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtX2An.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 2 trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtX3Danh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 3 đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtX3An.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 3 trúng", Toast.LENGTH_SHORT).show();
        } else if (cdtX4Danh.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 4 đánh", Toast.LENGTH_SHORT).show();
        } else if (cdtX4An.getText().toString().isEmpty()) {
            validate = false;
            Toast.makeText(getContext(), "Chưa cài đặt tiền thưởng xiên 4 trúng", Toast.LENGTH_SHORT).show();
        }
        return validate;
    }

}
