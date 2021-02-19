package com.smsanalytic.lotto.ui.setting;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.KyTuTinGuiDi;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class SettingKyTuSMSGuiDiActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.rbTinDai)
    RadioButton rbTinDai;
    @BindView(R.id.rb160)
    RadioButton rb160;
    @BindView(R.id.rb320)
    RadioButton rb320;
    @BindView(R.id.rb480)
    RadioButton rb480;
    @BindView(R.id.rg_account_type)
    RadioGroup rgAccountType;
    @BindView(R.id.btnAdd)
    TextView btnAdd;

    public static final String KY_TU_KEY = "kytu";

    private SettingDefault settingDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ky_tu_s_m_s_gui_di);
        ButterKnife.bind(this);
        getBundle();
        clickEvent();
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(KY_TU_KEY)) {
            settingDefault = (SettingDefault) bundle.getSerializable(KY_TU_KEY);
            if (settingDefault!=null){
                switch (settingDefault.getDoDaiTinNhan()) {
                    case KyTuTinGuiDi
                            .TIN_DAI:
                        rbTinDai.setChecked(true);
                        break;
                    case KyTuTinGuiDi
                            .KY_TU_160:
                        rb160.setChecked(true);
                        break;
                    case KyTuTinGuiDi
                            .KY_TU_320:
                        rb320.setChecked(true);
                        break;
                    case KyTuTinGuiDi
                            .KY_TU_480:
                        rb480.setChecked(true);
                        break;

                }
            }
        }
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnAdd.setOnClickListener(v -> {
            int idKytu=1;
            if (rbTinDai.isChecked()){
                idKytu=KyTuTinGuiDi.TIN_DAI;
            }
            else if (rb160.isChecked()){
                idKytu=KyTuTinGuiDi.KY_TU_160;
            }
            else if (rb320.isChecked()){
                idKytu=KyTuTinGuiDi.KY_TU_320;
            }
            else if (rb480.isChecked()){
                idKytu=KyTuTinGuiDi.KY_TU_480;
            }
            if (settingDefault!=null){
                settingDefault.setDoDaiTinNhan(idKytu);
            }
            PreferencesManager.getInstance().setValue(PreferencesManager.SETTING_DEFAULT, new Gson().toJson(settingDefault));
            Toast.makeText(getBaseContext(),"Thành công!",Toast.LENGTH_SHORT).show();

        });
    }
}
