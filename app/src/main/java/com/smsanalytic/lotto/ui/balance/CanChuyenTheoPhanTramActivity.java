package com.smsanalytic.lotto.ui.balance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.model.canchuyen.GiuLaiLonNhatDefault;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.processSms.SmsSuccessObject;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.balance.GiuLonNhatActivity.validateNgoaiLe;

public class CanChuyenTheoPhanTramActivity extends AppCompatActivity {


    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.rbChuyenTheoTongNhanVe)
    RadioButton rbChuyenTheoTongNhanVe;
    @BindView(R.id.rbChuyenTheoSoTon)
    RadioButton rbChuyenTheoSoTon;
    @BindView(R.id.rg_account_type)
    RadioGroup rgAccountType;
    @BindView(R.id.tv_PhanTram_DB)
    TextView tvPhanTramDB;
    @BindView(R.id.sBDB)
    SeekBar sBDB;
    @BindView(R.id.tv_PhanTram_Lo)
    TextView tvPhanTramLo;
    @BindView(R.id.sBLo)
    SeekBar sBLo;
    @BindView(R.id.tv_PhanTram_Xien)
    TextView tvPhanTramXien;
    @BindView(R.id.sBXien)
    SeekBar sBXien;
    @BindView(R.id.tv_PhanTram_BaCang)
    TextView tvPhanTramBaCang;
    @BindView(R.id.sBBaCang)
    SeekBar sBBaCang;
    @BindView(R.id.tv_PhanTram_GiaiNhat)
    TextView tvPhanTramGiaiNhat;
    @BindView(R.id.sBGiaiNhat)
    SeekBar sBGiaiNhat;
    @BindView(R.id.edtNgoaiLe)
    EditText edtNgoaiLe;
    @BindView(R.id.btnLayDuLieuChuyen)
    Button btnLayDuLieuChuyen;
    GiuLaiLonNhatDefault giuLaiLonNhatDefault = new GiuLaiLonNhatDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_chuyen_theo_phan_tram);
        ButterKnife.bind(this);
        setUpDataDefault();
        setupSeekBar();
        enableButtonChuyen();
        clickEvent();
        setChangeSeekBar();
    }

    private void setUpDataDefault() {
        SettingDefault settingDefault;
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if(settingDefault!=null){
            String keyTienTe= TienTe.getKeyTienTe(settingDefault.getTiente());
            edtNgoaiLe.setHint(getString(R.string.hint_NL_GiuLonNhat,keyTienTe,keyTienTe));
        }
    }

    private void setupSeekBar() {
    }

    private void setChangeSeekBar() {

        sBDB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPhanTramDB.setText(getString(R.string.tv_format_present, progress));
                if (progress > 0) {
                    giuLaiLonNhatDefault.setDb(true);
                    giuLaiLonNhatDefault.setDbvalue(progress);
                } else {
                    giuLaiLonNhatDefault.setDb(false);
                }
                enableButtonChuyen();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sBBaCang.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPhanTramBaCang.setText(getString(R.string.tv_format_present, progress));
                if (progress > 0) {
                    giuLaiLonNhatDefault.setBacang(true);
                    giuLaiLonNhatDefault.setBacangvalue(progress);
                } else {
                    giuLaiLonNhatDefault.setBacang(false);
                }
                enableButtonChuyen();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sBGiaiNhat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPhanTramGiaiNhat.setText(getString(R.string.tv_format_present, progress));
                if (progress > 0) {
                    giuLaiLonNhatDefault.setGiainhat(true);
                    giuLaiLonNhatDefault.setGiainhatvalue(progress);
                } else {
                    giuLaiLonNhatDefault.setGiainhat(false);
                }
                enableButtonChuyen();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sBLo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPhanTramLo.setText(getString(R.string.tv_format_present, progress));
                if (progress > 0) {
                    giuLaiLonNhatDefault.setLoto(true);
                    giuLaiLonNhatDefault.setLotovalue(progress);
                } else {
                    giuLaiLonNhatDefault.setLoto(false);
                }
                enableButtonChuyen();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sBXien.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPhanTramXien.setText(getString(R.string.tv_format_present, progress));
                if (progress > 0) {
                    giuLaiLonNhatDefault.setXien(true);
                    giuLaiLonNhatDefault.setX2value(progress);
                    giuLaiLonNhatDefault.setX3value(progress);
                    giuLaiLonNhatDefault.setX4value(progress);
                } else {
                    giuLaiLonNhatDefault.setXien(false);
                }
                enableButtonChuyen();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void enableButtonChuyen() {
        if (giuLaiLonNhatDefault.getDb() || giuLaiLonNhatDefault.getGiainhat() || giuLaiLonNhatDefault.getXien()
                || giuLaiLonNhatDefault.getBacang()
                || giuLaiLonNhatDefault.getLoto()) {
            btnLayDuLieuChuyen.setEnabled(true);
        } else {
            btnLayDuLieuChuyen.setEnabled(false);
        }
    }


    private void clickEvent() {
        btnBack.setOnClickListener(v -> finish());
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

    /**
     * Chuyển sang màn hình chi tiết cân chuyển
     */
    private void openChiTietCanChuyen(List<LotoNumberObject> lotoNumberObjects) {
        giuLaiLonNhatDefault.setCanchuyenTheoPhanTram(true);
        giuLaiLonNhatDefault.setChuyenTheoTongNhanVe(rbChuyenTheoTongNhanVe.isChecked());
        Intent intent = new Intent(this, ChiTietCanChuyenActivity.class);
        intent.putExtra(ChiTietCanChuyenActivity.GIULAILONNHAT_KEY, giuLaiLonNhatDefault);
        intent.putExtra(ChiTietCanChuyenActivity.NGOAILE_KEY, new Gson().toJson(lotoNumberObjects));
        startActivity(intent);
    }
}
