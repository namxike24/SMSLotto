package com.smsanalytic.lotto.ui.balance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.CurrencyEditText;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.entities.SmsSendObject;
import com.smsanalytic.lotto.model.canchuyen.GiuLaiTheoKhuc;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.balance.ChiTietCanChuyenActivity.getDateLotoByType;
import static com.smsanalytic.lotto.ui.balance.ChiTietCanChuyenActivity.getDateXienByType;
import static com.smsanalytic.lotto.ui.balance.GiuLonNhatActivity.validateNgoaiLe;

public class CanChuyenTheoKhucActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.etKhuc1Tu)
    EditText etKhuc1Tu;
    @BindView(R.id.etKhuc1Den)
    EditText etKhuc1Den;
    @BindView(R.id.cetKhuc1GiuaMax)
    CurrencyEditText cetKhuc1GiuaMax;
    @BindView(R.id.etKhuc2Tu)
    EditText etKhuc2Tu;
    @BindView(R.id.etKhuc2Den)
    EditText etKhuc2Den;
    @BindView(R.id.cetKhuc2GiuaMax)
    CurrencyEditText cetKhuc2GiuaMax;
    @BindView(R.id.etKhuc3Tu)
    EditText etKhuc3Tu;
    @BindView(R.id.etKhuc3Den)
    EditText etKhuc3Den;
    @BindView(R.id.cetKhuc3GiuaMax)
    CurrencyEditText cetKhuc3GiuaMax;
    @BindView(R.id.etKhuc4Tu)
    EditText etKhuc4Tu;
    @BindView(R.id.etKhuc4Den)
    EditText etKhuc4Den;
    @BindView(R.id.cetKhuc4GiuaMax)
    CurrencyEditText cetKhuc4GiuaMax;
    @BindView(R.id.etKhuc5Tu)
    EditText etKhuc5Tu;
    @BindView(R.id.etKhuc5Den)
    EditText etKhuc5Den;
    @BindView(R.id.cetKhuc5GiuaMax)
    CurrencyEditText cetKhuc5GiuaMax;
    @BindView(R.id.etNgoaiLe)
    EditText etNgoaiLe;
    @BindView(R.id.rlLoai)
    RelativeLayout rlLoai;
    @BindView(R.id.btnLayGiuLieu)
    Button btnLayGiuLieu;
    @BindView(R.id.tableChiTiet)
    TableLayout tableChiTiet;
    @BindView(R.id.rlDe)
    LinearLayout rlDe;
    @BindView(R.id.tvTitleType)
    TextView tvTitleType;
    @BindView(R.id.titleTable)
    TextView titleTable;
    @BindView(R.id.rbChuyenTheoTongNhanVe)
    RadioButton rbChuyenTheoTongNhanVe;
    @BindView(R.id.rbChuyenTheoSoTon)
    RadioButton rbChuyenTheoSoTon;
    @BindView(R.id.tvPercent1)
    TextView tvPercent1;
    @BindView(R.id.tvPercent2)
    TextView tvPercent2;
    @BindView(R.id.tvPercent3)
    TextView tvPercent3;
    @BindView(R.id.tvPercent4)
    TextView tvPercent4;
    @BindView(R.id.tvPercent5)
    TextView tvPercent5;
    @BindView(R.id.lnHinhThucCanChuyen)
    LinearLayout lnHinhThucCanChuyen;
    @BindView(R.id.btnChuyen)
    TextView btnChuyen;
    @BindView(R.id.rg_account_type)
    RadioGroup rgAccountType;
    @BindView(R.id.tv_huongdan)
    TextView tvHuongdan;
    private DaoSession daoSession;
    private GiuLaiTheoKhuc giuLaiTheoKhuc;
    private int typeDefault = TypeEnum.TYPE_DE;
    HashMap<String, Double> lotoNgoaiLeHashMap = new HashMap<>();
    List<LotoNumberObject> lotoNgoaiLe;
    private String tableTitle = "Đuôi Đặc Biệt(Đề)";

    public static final String CAN_CHUYEN_THEO_PHAN_TRAM = "CanchuyenTheoPhanTram";
    SettingDefault settingDefault;
    private boolean isCanchuyenTheoPhanTram;
    private boolean isChuyenTheoTongNhanVe;
    List<SmsSendObject> smsSend = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_chuyen_theo_khuc);
        ButterKnife.bind(this);
        setUpDataDefault();
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        getBundle();
        setDataCache();
        clickEvent();
    }

    private void setUpDataDefault() {

        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault != null) {
            String keyTienTe = TienTe.getKeyTienTe(settingDefault.getTiente());
            String valueTienTe = TienTe.getValueTienTe(settingDefault.getTiente());
            etNgoaiLe.setHint(getString(R.string.hint_NL_GiuLonNhat, keyTienTe, keyTienTe));
            tvHuongdan.setText(getString(R.string.tv_duongdan_theokhuc,valueTienTe));
        }

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(CAN_CHUYEN_THEO_PHAN_TRAM)) {
                isCanchuyenTheoPhanTram = bundle.getBoolean(CAN_CHUYEN_THEO_PHAN_TRAM);
                if (isCanchuyenTheoPhanTram) {
                    isChuyenTheoTongNhanVe = rbChuyenTheoTongNhanVe.isChecked();
                    lnHinhThucCanChuyen.setVisibility(View.VISIBLE);
                    tvPercent1.setVisibility(View.VISIBLE);
                    tvPercent2.setVisibility(View.VISIBLE);
                    tvPercent3.setVisibility(View.VISIBLE);
                    tvPercent4.setVisibility(View.VISIBLE);
                    tvPercent5.setVisibility(View.VISIBLE);
                }

            }
        }

    }

    private void setDataCache() {
        String dateCache = "";
        if (isCanchuyenTheoPhanTram) {
            dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.GIULAITHEOKHUCPHANTRAM_DEFAULT, "");
        } else {
            dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.GIULAITHEOKHUC_DEFAULT, "");
        }

        if (!dateCache.isEmpty()) {
            giuLaiTheoKhuc = new Gson().fromJson(dateCache, GiuLaiTheoKhuc.class);
        } else {
            String dateDefault = "";
            if (isCanchuyenTheoPhanTram) {
                dateDefault = Common.loadJSONFromAsset(this, "giulaitheokhucphantram.json");
            } else {
                dateDefault = Common.loadJSONFromAsset(this, "giulaitheokhuc.json");
            }
            giuLaiTheoKhuc = new Gson().fromJson(dateDefault, GiuLaiTheoKhuc.class);
        }
        if (giuLaiTheoKhuc != null) {
            etKhuc1Tu.setText(String.valueOf(giuLaiTheoKhuc.getKhuc1tu()));
            etKhuc1Den.setText(String.valueOf(giuLaiTheoKhuc.getKhuc1den()));
            cetKhuc1GiuaMax.setText(Common.formatMoney(giuLaiTheoKhuc.getKhuc1giulaimax()));
            etKhuc2Tu.setText(String.valueOf(giuLaiTheoKhuc.getKhuc2tu()));
            etKhuc2Den.setText(String.valueOf(giuLaiTheoKhuc.getKhuc2den()));
            cetKhuc2GiuaMax.setText((Common.formatMoney(giuLaiTheoKhuc.getKhuc2giulaimax())));
            etKhuc3Tu.setText(String.valueOf(giuLaiTheoKhuc.getKhuc3tu()));
            etKhuc3Den.setText(String.valueOf(giuLaiTheoKhuc.getKhuc3den()));
            cetKhuc3GiuaMax.setText((Common.formatMoney(giuLaiTheoKhuc.getKhuc3giulaimax())));
            etKhuc4Tu.setText(String.valueOf(giuLaiTheoKhuc.getKhuc4tu()));
            etKhuc4Den.setText(String.valueOf(giuLaiTheoKhuc.getKhuc4den()));
            cetKhuc4GiuaMax.setText((Common.formatMoney(giuLaiTheoKhuc.getKhuc4giulaimax())));
            etKhuc5Tu.setText(String.valueOf(giuLaiTheoKhuc.getKhuc5tu()));
            etKhuc5Den.setText(String.valueOf(giuLaiTheoKhuc.getKhuc5den()));
            cetKhuc5GiuaMax.setText((Common.formatMoney(giuLaiTheoKhuc.getKhuc5giulaimax())));
            layDulieuLotoHoacXien();
        }
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> {
            finish();
        });
        rlLoai.setOnClickListener(v -> {
            showPopupLotoType();
        });

        btnLayGiuLieu.setOnClickListener(v -> {
            saveGiuLaiTheoKhuc();
            if (validateKhuc5()) {
                if (etNgoaiLe.getText().toString().isEmpty()) {
                    layDulieuLotoHoacXien();
                } else {
                    processSms();
                }
            }
        });
        btnChuyen.setOnClickListener(v -> {
            List<AccountObject> accountObjects = new ArrayList<>();
            for (AccountObject accountObject : daoSession.getAccountObjectDao().queryBuilder().list()) {
                if (accountObject.getAccountType() == AccountType.STAFFANDBOSS || accountObject.getAccountType() == AccountType.BOSS) {
                    accountObjects.add(accountObject);
                }
            }
            if (accountObjects.size() > 0) {
                if (smsSend.size() > 0) {
                    Collections.sort(smsSend, (c1, c2) -> -Double.compare(c1.getMoney(), c2.getMoney()));
                    List<String> data= new ArrayList<>();
                    int typeCompare = -1;
                    for (SmsSendObject smsSendObject : smsSend){
                        if (typeCompare==smsSendObject.getType()){
                            data.add(smsSendObject.getContent().replace(TypeEnum.getStringByType3(typeCompare),"").trim());
                        }
                        else{
                            data.add(smsSendObject.getContent());
                            typeCompare=smsSendObject.getType();
                        }

                    }
                    AccountBossDialogFragment accountBossDialogFragment = AccountBossDialogFragment.newInstance(accountObjects, data);
                    accountBossDialogFragment.show(getSupportFragmentManager().beginTransaction(), accountBossDialogFragment.getTag());
                } else {
                    Toast.makeText(this, "Không có dữ liệu cân chuyển", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Không có người nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processSms() {
        SmsSuccessObject smsSuccessObject = ProcessSms.processSms(etNgoaiLe.getText().toString());
        if (smsSuccessObject.isProcessSuccess()) {
            etNgoaiLe.setTextColor(getResources().getColor(R.color.back_1));
            if (validateNgoaiLe(smsSuccessObject.getListDataLoto())) {

                lotoNgoaiLe = smsSuccessObject.getListDataLoto();
                if (lotoNgoaiLe != null) {
                    for (LotoNumberObject s : lotoNgoaiLe) {
                        if (s.getType() != TypeEnum.TYPE_XIEN2 && s.getType() != TypeEnum.TYPE_XIEN3 && s.getType() != TypeEnum.TYPE_XIEN4) {
                            lotoNgoaiLeHashMap.put(s.getValue1(), s.getMoneyTake());
                        } else {
                            lotoNgoaiLeHashMap.put(s.getXienFormat(), s.getMoneyTake());
                        }
                    }
                }
                layDulieuLotoHoacXien();
            } else {
                Toast.makeText(this, R.string.tv_ngoai_le_error, Toast.LENGTH_SHORT).show();
            }

        } else {
            etNgoaiLe.setTextColor(Color.RED);
            Toast.makeText(this, R.string.tv_ngoai_le_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void layDulieuLotoHoacXien() {
        if (typeDefault == TypeEnum.TYPE_XIEN2) {
            initTableDetailXien();
        } else {
            initTableDetailLoto(typeDefault);
        }
    }

    private void initTableDetailXien() {
        tableChiTiet.removeAllViews();
        titleTable.setText(tableTitle);
        String donvi = TienTe.getKeyTienTe(settingDefault != null ? settingDefault.getTiente() : TienTe.TIEN_VIETNAM);
        List<LotoNumberObject> dbList = new ArrayList<>();
        dbList.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_RECEIVE));
        dbList.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_RECEIVE));
        dbList.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_RECEIVE));

        List<LotoNumberObject> dbListDaChuyen = new ArrayList<>();
        dbListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_SENT));
        dbListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_SENT));
        dbListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_SENT));
        for (LotoNumberObject lotoNhanVe : dbList) {
            for (LotoNumberObject lotoDaChuyen : dbListDaChuyen) {
                if (lotoNhanVe.getXienFormat().trim().equals(lotoDaChuyen.getXienFormat().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }

        for (int i = 0; i < dbList.size(); i++) {
            LotoNumberObject lotoNumber = dbList.get(i);
            double giulai = 0;
            double sechuyen = 0;
            double giulaiMax = 0;
            Double giualaingoaile = null;
            // trường hợp ngoại lệ
            if (lotoNgoaiLeHashMap.containsKey(lotoNumber.getValue1())) {
                giualaingoaile = lotoNgoaiLeHashMap.get(lotoNumber.getValue1()) != null ? lotoNgoaiLeHashMap.get(lotoNumber.getValue1()) : 0.0;
            }
            if (lotoNumber.getType() == TypeEnum.TYPE_XIEN2) {
                lotoNumber.setValue1(lotoNumber.getValue1() + "-" + lotoNumber.getValue2());
            } else if (lotoNumber.getType() == TypeEnum.TYPE_XIEN3) {
                lotoNumber.setValue1(lotoNumber.getValue1() + "-" + lotoNumber.getValue2() + "-" + lotoNumber.getValue3());
            } else if (lotoNumber.getType() == TypeEnum.TYPE_XIEN4) {
                lotoNumber.setValue1(lotoNumber.getValue1() + "-" + lotoNumber.getValue2() + "-" + lotoNumber.getValue3() + "-" + lotoNumber.getValue4());
            }
            if (i >= giuLaiTheoKhuc.getKhuc1tu() - 1 && i <= giuLaiTheoKhuc.getKhuc1den() - 1) {
                giulaiMax = giuLaiTheoKhuc.getKhuc1giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc2tu() - 1 && i <= giuLaiTheoKhuc.getKhuc2den() - 1) {
                giulaiMax = giuLaiTheoKhuc.getKhuc2giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc3tu() - 1 && i <= giuLaiTheoKhuc.getKhuc3den() - 1) {
                giulaiMax = giuLaiTheoKhuc.getKhuc3giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc4tu() - 1 && i <= giuLaiTheoKhuc.getKhuc4den() - 1) {
                giulaiMax = giuLaiTheoKhuc.getKhuc4giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc5tu() - 1 && i <= giuLaiTheoKhuc.getKhuc5den() - 1) {
                giulaiMax = giuLaiTheoKhuc.getKhuc5giulaimax();
            }
            // cân chuyển theo phần trăm
            if (isCanchuyenTheoPhanTram) {
                // CHuyển theo tổng nhận về
                if (isChuyenTheoTongNhanVe) {
                    giulai = lotoNumber.getMoneyTake() * (giulaiMax) / 100;
                    sechuyen = lotoNumber.getMoneyTake() * (100 - giulaiMax) / 100 - lotoNumber.getMoneySend();
                } else {   // CHuyển theo tổng tồn lại
                    giulai = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) * giulaiMax / 100;
                    sechuyen = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) * (100 - giulaiMax) / 100;
                }
            } else {    // cân chuyển theo giá trị tiền giữ lại
                giulai = giulaiMax;
                sechuyen = lotoNumber.getMoneyTake() - giulaiMax - lotoNumber.getMoneySend();
            }

            // trường hợp ngoại lệ mà !=null => theo giá trị ngoại lệ
            if (giualaingoaile != null) {
                giulai = giualaingoaile;
                sechuyen = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) - giulai;
            }
            // sẽ chyển  >0 => hiển thị dòng đó trong table
            if (sechuyen > 0) {
                lotoNumber.setSeChuyen(sechuyen);
                lotoNumber.setMoneyKeep(giulai);
            } else {
                lotoNumber.setSeChuyen(0);
                lotoNumber.setMoneyKeep(lotoNumber.getMoneyTake());
            }
        }

        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_title_table, null, false);
        tableChiTiet.addView(tableRowTitle);
        int count = 0;
        for (LotoNumberObject lotoItem : dbList) {
            count++;
            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_item_table, null, false);
            TextView tvNo = tableRow.findViewById(R.id.tvNo);
            TextView tvNumber = tableRow.findViewById(R.id.tvNumber);
            TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
            TextView tvDaChuyen = tableRow.findViewById(R.id.tvDaChuyen);
            TextView tvSeChuyen = tableRow.findViewById(R.id.tvSeChuyen);
            TextView tvConLai = tableRow.findViewById(R.id.tvConLai);

            tvNo.setText(String.valueOf(count));
            tvNumber.setText(String.valueOf(lotoItem.getValue1()));
            tvNhanVe.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyTake()), donvi));
            tvDaChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneySend()), donvi));
            tvSeChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getSeChuyen()), donvi));
            tvConLai.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyKeep()), donvi));
            tableChiTiet.addView(tableRow);
        }
    }


    private boolean validateKhuc1() {
        boolean validate = true;
        if (giuLaiTheoKhuc.getKhuc1tu() == 0) {
            Toast.makeText(this, "Khúc 1: Từ phải lớn hơn không", Toast.LENGTH_SHORT).show();
            validate = false;
        } else if (giuLaiTheoKhuc.getKhuc1tu() >= giuLaiTheoKhuc.getKhuc1den()) {
            Toast.makeText(this, "Khúc 1: Đến phải lớn hơn Từ", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        return validate;
    }

    private boolean validateKhuc2() {
        boolean validate = validateKhuc1();
        if (giuLaiTheoKhuc.getKhuc2tu() != 0 || giuLaiTheoKhuc.getKhuc2den() != 0) {
            if (giuLaiTheoKhuc.getKhuc2tu() >= giuLaiTheoKhuc.getKhuc2den()) {
                Toast.makeText(this, "Khúc 2: Đến phải lớn hơn Từ", Toast.LENGTH_SHORT).show();
                validate = false;
            } else if (giuLaiTheoKhuc.getKhuc2tu() <= giuLaiTheoKhuc.getKhuc1den()) {
                Toast.makeText(this, "Khúc 2 không được trùng với khúc 1!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        return validate;
    }

    private boolean validateKhuc3() {
        boolean validate = validateKhuc2();
        if (giuLaiTheoKhuc.getKhuc3tu() != 0 || giuLaiTheoKhuc.getKhuc3den() != 0) {
            if (giuLaiTheoKhuc.getKhuc3tu() >= giuLaiTheoKhuc.getKhuc3den()) {
                Toast.makeText(this, "Khúc 3: Đến phải lớn hơn Từ", Toast.LENGTH_SHORT).show();
                validate = false;
            } else if (giuLaiTheoKhuc.getKhuc3tu() <= giuLaiTheoKhuc.getKhuc2den()) {
                Toast.makeText(this, "Khúc 3 không được trùng với khúc 2!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        return validate;
    }

    private boolean validateKhuc4() {
        boolean validate = validateKhuc3();
        if (giuLaiTheoKhuc.getKhuc4tu() != 0 || giuLaiTheoKhuc.getKhuc4den() != 0) {
            if (giuLaiTheoKhuc.getKhuc4tu() >= giuLaiTheoKhuc.getKhuc4den()) {
                Toast.makeText(this, "Khúc 4: Đến phải lớn hơn Từ", Toast.LENGTH_SHORT).show();
                validate = false;
            } else if (giuLaiTheoKhuc.getKhuc4tu() <= giuLaiTheoKhuc.getKhuc3den()) {
                Toast.makeText(this, "Khúc 4 không được trùng với khúc 3!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        return validate;
    }

    private boolean validateKhuc5() {
        boolean validate = validateKhuc4();
        if (giuLaiTheoKhuc.getKhuc5tu() != 0 || giuLaiTheoKhuc.getKhuc5den() != 0) {
            if (giuLaiTheoKhuc.getKhuc5tu() >= giuLaiTheoKhuc.getKhuc5den()) {
                Toast.makeText(this, "Khúc 5: Đến phải lớn hơn Từ", Toast.LENGTH_SHORT).show();
                validate = false;
            } else if (giuLaiTheoKhuc.getKhuc5tu() <= giuLaiTheoKhuc.getKhuc4den()) {
                Toast.makeText(this, "Khúc 5 không được trùng với khúc 4!", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }
        return validate;
    }


    private void saveGiuLaiTheoKhuc() {
        giuLaiTheoKhuc.setKhuc1tu(Integer.parseInt(etKhuc1Tu.getText().toString().isEmpty() ? "0" : etKhuc1Tu.getText().toString()));
        giuLaiTheoKhuc.setKhuc1den(Integer.parseInt(etKhuc1Den.getText().toString().isEmpty() ? "0" : etKhuc1Den.getText().toString()));
        giuLaiTheoKhuc.setKhuc1giulaimax(cetKhuc1GiuaMax.getNumericValue());

        giuLaiTheoKhuc.setKhuc2tu(Integer.parseInt(etKhuc2Tu.getText().toString().isEmpty() ? "0" : etKhuc2Tu.getText().toString()));
        giuLaiTheoKhuc.setKhuc2den(Integer.parseInt(etKhuc2Den.getText().toString().isEmpty() ? "0" : etKhuc2Den.getText().toString()));
        giuLaiTheoKhuc.setKhuc2giulaimax(cetKhuc2GiuaMax.getNumericValue());

        giuLaiTheoKhuc.setKhuc3tu(Integer.parseInt(etKhuc3Tu.getText().toString().isEmpty() ? "0" : etKhuc3Tu.getText().toString()));
        giuLaiTheoKhuc.setKhuc3den(Integer.parseInt(etKhuc3Den.getText().toString().isEmpty() ? "0" : etKhuc3Den.getText().toString()));
        giuLaiTheoKhuc.setKhuc3giulaimax(cetKhuc3GiuaMax.getNumericValue());

        giuLaiTheoKhuc.setKhuc4tu(Integer.parseInt(etKhuc4Tu.getText().toString().isEmpty() ? "0" : etKhuc4Tu.getText().toString()));
        giuLaiTheoKhuc.setKhuc4den(Integer.parseInt(etKhuc4Den.getText().toString().isEmpty() ? "0" : etKhuc4Den.getText().toString()));
        giuLaiTheoKhuc.setKhuc4giulaimax(cetKhuc4GiuaMax.getNumericValue());

        giuLaiTheoKhuc.setKhuc5tu(Integer.parseInt(etKhuc5Tu.getText().toString().isEmpty() ? "0" : etKhuc5Tu.getText().toString()));
        giuLaiTheoKhuc.setKhuc5den(Integer.parseInt(etKhuc5Den.getText().toString().isEmpty() ? "0" : etKhuc5Den.getText().toString()));
        giuLaiTheoKhuc.setKhuc5giulaimax(cetKhuc5GiuaMax.getNumericValue());
        if (isCanchuyenTheoPhanTram) {
            PreferencesManager.getInstance().setValue(PreferencesManager.GIULAITHEOKHUCPHANTRAM_DEFAULT, new Gson().toJson(giuLaiTheoKhuc));
        } else {
            PreferencesManager.getInstance().setValue(PreferencesManager.GIULAITHEOKHUC_DEFAULT, new Gson().toJson(giuLaiTheoKhuc));
        }

    }

    private void showPopupLotoType() {
        PopupMenu popup = new PopupMenu(this, rlLoai);
        popup.getMenuInflater()
                .inflate(R.menu.menu_loto_type, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            tvTitleType.setText(item.getTitle());
            switch (item.getItemId()) {
                case R.id.itemDe:
                    typeDefault = TypeEnum.TYPE_DE;
                    tableTitle = "Đuôi Đặc Biệt(Đề)";
                    break;
                case R.id.itemLo:
                    typeDefault = TypeEnum.TYPE_LO;
                    tableTitle = "Lô";
                    break;
                case R.id.itemLoDau:
                    tableTitle = "Lô Đầu";
                    break;
                case R.id.itemXien:
                    tableTitle = "Xiên";
                    typeDefault = TypeEnum.TYPE_XIEN2;
                    break;
                case R.id.itemBaCang:
                    tableTitle = "Ba Càng";
                    typeDefault = TypeEnum.TYPE_3C;
                    break;
                case R.id.itemDitNhat:
                    tableTitle = "Đuôi Giải Nhất";
                    typeDefault = TypeEnum.TYPE_DITNHAT;
                    break;
                case R.id.itemDauNhat:
                    tableTitle = "Đầu Giải Nhất";
                    typeDefault = TypeEnum.TYPE_DAUNHAT;
                    break;
                case R.id.itemDauDB:
                    tableTitle = "Đầu Đặc Biệt";
                    typeDefault = TypeEnum.TYPE_DAUDB;
                    break;

            }
            return true;
        });
        popup.show(); //showing popup menu
    }

    private void initTableDetailLoto(int type) {
        tableChiTiet.removeAllViews();
        titleTable.setText(tableTitle);
        String donvi = TienTe.getKeyTienTe(settingDefault != null ? settingDefault.getTiente() : TienTe.TIEN_VIETNAM);
        if (type == TypeEnum.TYPE_LO) {
            donvi = "d";
        }
        List<LotoNumberObject> dbList = getDateLotoByType(daoSession, type, SmsStatus.SMS_RECEIVE);
        List<LotoNumberObject> dbDaChuyen = getDateLotoByType(daoSession, type, SmsStatus.SMS_SENT);
        for (LotoNumberObject lotoNhanVe : dbList) {
            for (LotoNumberObject lotoDaChuyen : dbDaChuyen) {
                if (lotoNhanVe.getValue1().trim().equals(lotoDaChuyen.getValue1().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }
        for (int i = 0; i < dbList.size(); i++) {
            LotoNumberObject lotoNumber = dbList.get(i);
            double giulaimax = 0;
            double giulai = 0;
            double sechuyen = 0;
            Double giualaingoaile = null;
            // trường hợp ngoại lệ
            if (lotoNgoaiLeHashMap.containsKey(lotoNumber.getValue1())) {
                giualaingoaile = lotoNgoaiLeHashMap.get(lotoNumber.getValue1()) != null ? lotoNgoaiLeHashMap.get(lotoNumber.getValue1()) : 0.0;
            }
            if (i >= giuLaiTheoKhuc.getKhuc1tu() - 1 && i <= giuLaiTheoKhuc.getKhuc1den() - 1) {
                giulaimax = giuLaiTheoKhuc.getKhuc1giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc2tu() - 1 && i <= giuLaiTheoKhuc.getKhuc2den() - 1) {
                giulaimax = giuLaiTheoKhuc.getKhuc2giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc3tu() - 1 && i <= giuLaiTheoKhuc.getKhuc3den() - 1) {
                giulaimax = giuLaiTheoKhuc.getKhuc3giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc4tu() - 1 && i <= giuLaiTheoKhuc.getKhuc4den() - 1) {
                giulaimax = giuLaiTheoKhuc.getKhuc4giulaimax();
            }
            if (i >= giuLaiTheoKhuc.getKhuc5tu() - 1 && i <= giuLaiTheoKhuc.getKhuc5den() - 1) {
                giulaimax = giuLaiTheoKhuc.getKhuc5giulaimax();
            }

            // cân chuyển theo phần trăm
            if (isCanchuyenTheoPhanTram) {
                // CHuyển theo tổng nhận về
                if (isChuyenTheoTongNhanVe) {
                    giulai = lotoNumber.getMoneyTake() * (giulaimax) / 100;
                    sechuyen = lotoNumber.getMoneyTake() * (100 - giulaimax) / 100 - lotoNumber.getMoneySend();
                } else {   // CHuyển theo tổng tồn lại
                    giulai = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) * giulaimax / 100;
                    sechuyen = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) * (100 - giulaimax) / 100;
                }
            } else {    // cân chuyển theo giá trị tiền giữ lại
                giulai = giulaimax;
                sechuyen = lotoNumber.getMoneyTake() - giulaimax - lotoNumber.getMoneySend();

            }

            // trường hợp ngoại lệ mà !=null => theo giá trị ngoại lệ
            if (giualaingoaile != null) {
                giulai = giualaingoaile;
                sechuyen = (lotoNumber.getMoneyTake() - lotoNumber.getMoneySend()) - giulai;
            }
            // sẽ chyển  >0 => hiển thị dòng đó trong table
            if (sechuyen > 0) {
                lotoNumber.setSeChuyen(sechuyen);
                lotoNumber.setMoneyKeep(giulai);
            } else {
                lotoNumber.setSeChuyen(0);
                lotoNumber.setMoneyKeep(lotoNumber.getMoneyTake());
            }
        }

        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_title_table, null, false);
        tableChiTiet.addView(tableRowTitle);
        int count = 0;
        for (LotoNumberObject lotoItem : dbList) {
            count++;
            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_item_table, null, false);
            TextView tvNo = tableRow.findViewById(R.id.tvNo);
            TextView tvNumber = tableRow.findViewById(R.id.tvNumber);
            TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
            TextView tvDaChuyen = tableRow.findViewById(R.id.tvDaChuyen);
            TextView tvSeChuyen = tableRow.findViewById(R.id.tvSeChuyen);
            TextView tvConLai = tableRow.findViewById(R.id.tvConLai);

            tvNo.setText(String.valueOf(count));
            tvNumber.setText(String.valueOf(lotoItem.getValue1()));
            tvNhanVe.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyTake()), donvi));
            tvDaChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneySend()), donvi));
            tvSeChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getSeChuyen()), donvi));
            tvConLai.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyKeep()), donvi));
            tableChiTiet.addView(tableRow);
        }
        smsSend.clear();

        if (!ProcessSms.groupSmsToSent(dbList, typeDefault, donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(dbList, typeDefault, donvi));
        }
    }
}
