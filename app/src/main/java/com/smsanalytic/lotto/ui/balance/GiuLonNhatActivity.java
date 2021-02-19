package com.smsanalytic.lotto.ui.balance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.CurrencyEditText;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.model.canchuyen.GiuLaiLonNhatDefault;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class GiuLonNhatActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.cbAll)
    CheckBox cbAll;
    @BindView(R.id.cbDB)
    CheckBox cbDB;
    @BindView(R.id.cbLo)
    CheckBox cbLo;
    @BindView(R.id.cbXien)
    CheckBox cbXien;
    @BindView(R.id.cbBaso)
    CheckBox cbBaso;
    @BindView(R.id.tvDBMax)
    TextView tvDBMax;
    @BindView(R.id.cet_DB_Max)
    CurrencyEditText cetDBMax;
    @BindView(R.id.tvGiaiNhatMax)
    TextView tvGiaiNhatMax;
    @BindView(R.id.cet_GiaiNhat_Max)
    CurrencyEditText cetGiaiNhatMax;
    @BindView(R.id.tvLotoMax)
    TextView tvLotoMax;
    @BindView(R.id.cet_Loto_Max)
    CurrencyEditText cetLotoMax;
    @BindView(R.id.tvBasoMax)
    TextView tvBasoMax;
    @BindView(R.id.cet_Baso_Max)
    CurrencyEditText cetBasoMax;
    @BindView(R.id.tvX2Max)
    TextView tvX2Max;
    @BindView(R.id.cet_X2_Max)
    CurrencyEditText cetX2Max;
    @BindView(R.id.tvX3Max)
    TextView tvX3Max;
    @BindView(R.id.cet_X3_Max)
    CurrencyEditText cetX3Max;
    @BindView(R.id.tvX4Max)
    TextView tvX4Max;
    @BindView(R.id.cet_X4_Max)
    CurrencyEditText cetX4Max;
    @BindView(R.id.edtNgoaiLe)
    EditText edtNgoaiLe;
    @BindView(R.id.btnHuongDan)
    TextView btnHuongDan;
    @BindView(R.id.tvNoiDungHD)
    TextView tvNoiDungHD;
    @BindView(R.id.btnLayDuLieuChuyen)
    Button btnLayDuLieuChuyen;
    @BindView(R.id.lnDBMax)
    LinearLayout lnDBMax;
    @BindView(R.id.lnGiaiNhatMax)
    LinearLayout lnGiaiNhatMax;
    @BindView(R.id.lnLotoMax)
    LinearLayout lnLotoMax;
    @BindView(R.id.lnBasoMax)
    LinearLayout lnBasoMax;
    @BindView(R.id.lnXien2Max)
    LinearLayout lnXien2Max;
    @BindView(R.id.lnXien3Max)
    LinearLayout lnXien3Max;
    @BindView(R.id.lnXien4Max)
    LinearLayout lnXien4Max;

    GiuLaiLonNhatDefault giuLaiLonNhatDefault = new GiuLaiLonNhatDefault();
    @BindView(R.id.tv_donvi_DBMax)
    TextView tvDonviDBMax;
    @BindView(R.id.tv_donvi_GiaiNhatMax)
    TextView tvDonviGiaiNhatMax;
    @BindView(R.id.tv_donvi_BaSoMax)
    TextView tvDonviBaSoMax;
    @BindView(R.id.tv_donvi_x2Max)
    TextView tvDonviX2Max;
    @BindView(R.id.tv_donvi_x3Max)
    TextView tvDonviX3Max;
    @BindView(R.id.tv_donvi_x4Max)
    TextView tvDonviX4Max;
    // data setting
    SettingDefault settingDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giu_lon_nhat);
        ButterKnife.bind(this);
        clickEvent();
        setDataCache();
        edtNgoaiLe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                edtNgoaiLe.setTextColor(getResources().getColor(R.color.back_1));
            }

        });

    }

    private void setDataCache() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault!=null){
            String valueTienTe= TienTe.getValueTienTe(settingDefault.getTiente());
            String keyTienTe= TienTe.getKeyTienTe(settingDefault.getTiente());
            String tiente_type1=getString(R.string.nghin_con,valueTienTe ,"con");
            String tiente_type2=getString(R.string.nghin_con,valueTienTe,"cặp");
            tvDonviDBMax.setText(tiente_type1);
            tvDonviBaSoMax.setText(tiente_type1);
            tvDonviGiaiNhatMax.setText(tiente_type1);
            tvDonviBaSoMax.setText(tiente_type1);
            tvDonviX2Max.setText(tiente_type2);
            tvDonviX3Max.setText(tiente_type2);
            tvDonviX4Max.setText(tiente_type2);
            edtNgoaiLe.setHint(getString(R.string.hint_NL_GiuLonNhat,keyTienTe,keyTienTe));
        }

        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.GIULAILONNHAT_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            giuLaiLonNhatDefault = new Gson().fromJson(dateCache, GiuLaiLonNhatDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "giulailonnhatdefault.json");
            giuLaiLonNhatDefault = new Gson().fromJson(dateDefault, GiuLaiLonNhatDefault.class);
        }
        if (giuLaiLonNhatDefault != null) {
            cbDB.setChecked(giuLaiLonNhatDefault.getDb());
            cbLo.setChecked(giuLaiLonNhatDefault.getLoto());
            cbBaso.setChecked(giuLaiLonNhatDefault.getBacang());
            cbXien.setChecked(giuLaiLonNhatDefault.getXien());
            if (cbDB.isChecked() && cbLo.isChecked() && cbBaso.isChecked() && cbXien.isChecked()) {
                cbAll.setChecked(true);
            } else {
                cbAll.setChecked(false);
            }
            cetDBMax.setText(Common.formatMoney(giuLaiLonNhatDefault.getDbvalue()));
            cetGiaiNhatMax.setText(Common.formatMoney(giuLaiLonNhatDefault.getGiainhatvalue()));
            cetLotoMax.setText(Common.formatMoney(giuLaiLonNhatDefault.getLotovalue()));
            cetBasoMax.setText(Common.formatMoney(giuLaiLonNhatDefault.getBacangvalue()));
            cetX2Max.setText(Common.formatMoney(giuLaiLonNhatDefault.getX2value()));
            cetX3Max.setText(Common.formatMoney(giuLaiLonNhatDefault.getX3value()));
            cetX4Max.setText(Common.formatMoney(giuLaiLonNhatDefault.getX4value()));


            lnBasoMax.setVisibility(cbBaso.isChecked() ? View.VISIBLE : View.GONE);
            lnXien2Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            lnXien3Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            lnXien4Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            lnLotoMax.setVisibility(cbLo.isChecked() ? View.VISIBLE : View.GONE);
            lnDBMax.setVisibility(cbDB.isChecked() ? View.VISIBLE : View.GONE);
            lnGiaiNhatMax.setVisibility(cbDB.isChecked() ? View.VISIBLE : View.GONE);
        }
    }

    private void clickEvent() {
        // thoái màn hình
        btnBack.setOnClickListener(v -> finish());
        // Hiển thị hướng dẫn
        btnHuongDan.setOnClickListener(v -> {
            if (tvNoiDungHD.getVisibility() == View.VISIBLE) tvNoiDungHD.setVisibility(View.GONE);
            else tvNoiDungHD.setVisibility(View.VISIBLE);
        });
        // hiển  ô nhập ba số
        cbBaso.setOnClickListener(v -> {
            lnBasoMax.setVisibility(cbBaso.isChecked() ? View.VISIBLE : View.GONE);
            if (!cbBaso.isChecked()) cbAll.setChecked(false);
            giuLaiLonNhatDefault.setBacang(cbBaso.isChecked());
            showAllAndSaveCache(giuLaiLonNhatDefault);
        });
        // hiển  ô nhập Xiên
        cbXien.setOnClickListener(v -> {
            lnXien2Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            lnXien3Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            lnXien4Max.setVisibility(cbXien.isChecked() ? View.VISIBLE : View.GONE);
            if (!cbXien.isChecked()) cbAll.setChecked(false);
            giuLaiLonNhatDefault.setXien(cbXien.isChecked());
            showAllAndSaveCache(giuLaiLonNhatDefault);
        });
        // hiển  ô nhập Lô
        cbLo.setOnClickListener(v -> {
            lnLotoMax.setVisibility(cbLo.isChecked() ? View.VISIBLE : View.GONE);
            if (!cbLo.isChecked()) cbAll.setChecked(false);
            giuLaiLonNhatDefault.setLoto(cbLo.isChecked());
            showAllAndSaveCache(giuLaiLonNhatDefault);
        });
        // hiển  ô đặc biệt
        cbDB.setOnClickListener(v -> {
            lnDBMax.setVisibility(cbDB.isChecked() ? View.VISIBLE : View.GONE);
            lnGiaiNhatMax.setVisibility(cbDB.isChecked() ? View.VISIBLE : View.GONE);
            if (!cbDB.isChecked()) cbAll.setChecked(false);
            giuLaiLonNhatDefault.setDb(cbDB.isChecked());
            showAllAndSaveCache(giuLaiLonNhatDefault);
        });
        // hiển thị ô tất cả
        cbAll.setOnClickListener(v -> {
            if (cbAll.isChecked()) {
                cbDB.setChecked(true);
                cbLo.setChecked(true);
                cbXien.setChecked(true);
                cbBaso.setChecked(true);
                giuLaiLonNhatDefault.setDb(true);
                giuLaiLonNhatDefault.setGiainhat(true);
                giuLaiLonNhatDefault.setLoto(true);
                giuLaiLonNhatDefault.setXien(true);
                giuLaiLonNhatDefault.setBacang(true);
                showAllAndSaveCache(giuLaiLonNhatDefault);
            }
        });


        //sang màn hình chi tiết dữ liệu chuyển
        btnLayDuLieuChuyen.setOnClickListener(v -> {
            if (edtNgoaiLe.getText().toString().isEmpty()) {
                openChiTietCanChuyen(new ArrayList<>());
            } else {
                processSms();
            }
        });

    }

    private void processSms() {
        SmsSuccessObject smsSuccessObject = ProcessSms.processSms(edtNgoaiLe.getText().toString());
        if (smsSuccessObject.isProcessSuccess()) {
            if (validateNgoaiLe(smsSuccessObject.getListDataLoto())) {
                openChiTietCanChuyen(smsSuccessObject.getListDataLoto());
            } else {
                Toast.makeText(this, R.string.tv_ngoai_le_error, Toast.LENGTH_SHORT).show();
            }

        } else {
            edtNgoaiLe.setTextColor(Color.RED);
            Toast.makeText(this, R.string.tv_ngoai_le_error, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean validateNgoaiLe(ArrayList<LotoNumberObject> listDataLoto) {
        for (LotoNumberObject s : listDataLoto) {
            if (s.getType() == TypeEnum.TYPE_XIEN2 || s.getType() == TypeEnum.TYPE_XIEN3 || s.getType() == TypeEnum.TYPE_XIEN4
                    || s.getType() == TypeEnum.TYPE_3C
                    || s.getType() == TypeEnum.TYPE_XIENGHEP2
                    || s.getType() == TypeEnum.TYPE_XIENGHEP3
                    || s.getType() == TypeEnum.TYPE_XIENGHEP4
                    || s.getType() == TypeEnum.TYPE_XIENQUAY


            ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Chuyển sang màn hình chi tiết cân chuyển
     */
    private void openChiTietCanChuyen(List<LotoNumberObject> lotoNumberObjects) {
        giuLaiLonNhatDefault.setDbvalue(cetDBMax.getNumericValue());
        giuLaiLonNhatDefault.setGiainhatvalue(cetGiaiNhatMax.getNumericValue());
        giuLaiLonNhatDefault.setLotovalue(cetLotoMax.getNumericValue());
        giuLaiLonNhatDefault.setBacangvalue(cetBasoMax.getNumericValue());
        giuLaiLonNhatDefault.setX2value(cetX2Max.getNumericValue());
        giuLaiLonNhatDefault.setX3value(cetX3Max.getNumericValue());
        giuLaiLonNhatDefault.setX4value(cetX4Max.getNumericValue());
        PreferencesManager.getInstance().setValue(PreferencesManager.GIULAILONNHAT_DEFAULT, new Gson().toJson(giuLaiLonNhatDefault));
        Intent intent = new Intent(this, ChiTietCanChuyenActivity.class);
        intent.putExtra(ChiTietCanChuyenActivity.GIULAILONNHAT_KEY, giuLaiLonNhatDefault);
        intent.putExtra(ChiTietCanChuyenActivity.NGOAILE_KEY, new Gson().toJson(lotoNumberObjects));
        startActivity(intent);
    }

    private void showAllAndSaveCache(GiuLaiLonNhatDefault giuLaiLonNhatDefault) {
        if (cbDB.isChecked() && cbLo.isChecked() && cbBaso.isChecked() && cbXien.isChecked()) {
            cbAll.setChecked(true);
        } else {
            cbAll.setChecked(false);
        }
        PreferencesManager.getInstance().setValue(PreferencesManager.GIULAILONNHAT_DEFAULT, new Gson().toJson(giuLaiLonNhatDefault));
    }


}
