package com.smsanalytic.lotto.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.model.setting.SettingXienType;

public class SettingXienActivity extends AppCompatActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.groupDonViTinh)
    RadioGroup groupDonViTinh;
    @BindView(R.id.rb10Nghin)
    RadioButton rb10Nghin;
    @BindView(R.id.rb1Nghin)
    RadioButton rb1Nghin;
    @BindView(R.id.groupChuyenXien)
    RadioGroup groupChuyenXien;
    @BindView(R.id.rbChyenXienN)
    RadioButton rbChyenXienN;
    @BindView(R.id.rbChyenXienD)
    RadioButton rbChyenXienD;

    @BindView(R.id.btnUpdate)
    TextView btnUpdate;
    @BindView(R.id.tv_caidatdonvitinh_1)
    TextView tvCaidatdonvitinh1;
    @BindView(R.id.tv_caidatdonvitinh_2)
    TextView tvCaidatdonvitinh2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_xien);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initListener() {
        try {
            btnBack.setOnClickListener(backListener);
            btnUpdate.setOnClickListener(updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener backListener = view -> {
        try {
            Common.disableView(view);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                SettingXienType.setDonViTinh(groupDonViTinh.getCheckedRadioButtonId() == R.id.rb10Nghin ? SettingXienType.MUOI_NGHIN : SettingXienType.MOT_NGHIN);
                SettingXienType.setChuyenXienDi(groupChuyenXien.getCheckedRadioButtonId() == R.id.rbChyenXienN ? SettingXienType.NGHIN : SettingXienType.DIEM);
                Toast.makeText(SettingXienActivity.this, "Thành công", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initData() {
        try {
            int tienTeType = Common.getTypeTienTe(this);
            String keyTienTe = TienTe.getKeyTienTe(tienTeType);
            String valueTiente = TienTe.getValueTienTe(tienTeType);
tvCaidatdonvitinh1.setText(getString(R.string.tv_caidatdonvitinh_1,valueTiente));
            tvCaidatdonvitinh2.setText(getString(R.string.tv_caidatdonvitinh_2,valueTiente));
            rb10Nghin.setText(getString(R.string.tv_1d_10n, valueTiente, keyTienTe));
            rb1Nghin.setText(getString(R.string.tv_1d_1n, valueTiente, keyTienTe));
            rbChyenXienN.setText(getString(R.string.tv_chuyen_xien_n, valueTiente, keyTienTe));
            if (SettingXienType.getDonViTinh() == SettingXienType.MUOI_NGHIN) {
                rb10Nghin.setChecked(true);
            } else {
                rb1Nghin.setChecked(true);
            }

            if (SettingXienType.getChuyenXienDi() == SettingXienType.NGHIN) {
                rbChyenXienN.setChecked(true);
            } else {
                rbChyenXienD.setChecked(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
