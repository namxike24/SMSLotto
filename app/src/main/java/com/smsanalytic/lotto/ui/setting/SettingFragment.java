package com.smsanalytic.lotto.ui.setting;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.CurrencyEditText;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.ui.document.ChooseCustomerPopup;
import com.smsanalytic.lotto.ui.document.adapter.ChooseCustomerAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.tvTime_NhanLo)
    TextView tvTimeNhanLo;
    @BindView(R.id.tvTime_NhanDe)
    TextView tvTimeNhanDe;
    @BindView(R.id.scTuDongCanChuyen)
    SwitchCompat scTuDongCanChuyen;
    @BindView(R.id.btnGioCanChuyenLo_Tu)
    TextView btnGioCanChuyenLoTu;
    @BindView(R.id.btnGioCanChuyenLo_Den)
    TextView btnGioCanChuyenLoDen;
    @BindView(R.id.btnGioCanChuyenDe_Tu)
    TextView btnGioCanChuyenDeTu;
    @BindView(R.id.btnGioCanChuyenDe_Den)
    TextView btnGioCanChuyenDeDen;
    @BindView(R.id.tvNguoiNhanCanChuyen)
    TextView tvNguoiNhanCanChuyen;
    @BindView(R.id.tvDBMax)
    TextView tvDBMax;
    @BindView(R.id.cet_DB_Max)
    CurrencyEditText cetDBMax;
    @BindView(R.id.lnDBMax)
    LinearLayout lnDBMax;
    @BindView(R.id.tvGiaiNhatMax)
    TextView tvGiaiNhatMax;
    @BindView(R.id.cet_GiaiNhat_Max)
    CurrencyEditText cetGiaiNhatMax;
    @BindView(R.id.lnGiaiNhatMax)
    LinearLayout lnGiaiNhatMax;
    @BindView(R.id.tvLotoMax)
    TextView tvLotoMax;
    @BindView(R.id.cet_Loto_Max)
    CurrencyEditText cetLotoMax;
    @BindView(R.id.lnLotoMax)
    LinearLayout lnLotoMax;
    @BindView(R.id.tvBasoMax)
    TextView tvBasoMax;
    @BindView(R.id.cet_Baso_Max)
    CurrencyEditText cetBasoMax;
    @BindView(R.id.lnBasoMax)
    LinearLayout lnBasoMax;
    @BindView(R.id.tvX2Max)
    TextView tvX2Max;
    @BindView(R.id.cet_X2_Max)
    CurrencyEditText cetX2Max;
    @BindView(R.id.lnXien2Max)
    LinearLayout lnXien2Max;
    @BindView(R.id.tvX3Max)
    TextView tvX3Max;
    @BindView(R.id.cet_X3_Max)
    CurrencyEditText cetX3Max;
    @BindView(R.id.lnXien3Max)
    LinearLayout lnXien3Max;
    @BindView(R.id.tvX4Max)
    TextView tvX4Max;
    @BindView(R.id.cet_X4_Max)
    CurrencyEditText cetX4Max;
    @BindView(R.id.lnXien4Max)
    LinearLayout lnXien4Max;
    @BindView(R.id.lnCanChuyenTuDong)
    LinearLayout lnCanChuyenTuDong;
    @BindView(R.id.scBoTinGiongNhau)
    SwitchCompat scBoTinGiongNhau;
    @BindView(R.id.lnBoTinGiong)
    LinearLayout lnBoTinGiong;
    @BindView(R.id.scGuiCongNo)
    SwitchCompat scGuiCongNo;
    @BindView(R.id.tvTime_GuiTinNhanCongNo)
    TextView tvTimeGuiTinNhanCongNo;
    @BindView(R.id.lnTuDongGuiCongNo)
    LinearLayout lnTuDongGuiCongNo;
    @BindView(R.id.tbnSettingKyTuChuyen)
    TextView tbnSettingKyTuChuyen;
    @BindView(R.id.btnSettingPassword)
    TextView btnSettingPassword;
    @BindView(R.id.btnSettingXien)
    TextView btnSettingXien;
    @BindView(R.id.btnUpdateSetting)
    TextView btnUpdateSetting;
    @BindView(R.id.rbVn)
    RadioButton rbVn;
    @BindView(R.id.rbJapan)
    RadioButton rbJapan;
    @BindView(R.id.rbTaiwan)
    RadioButton rbTaiwan;
    @BindView(R.id.rbKorea)
    RadioButton rbKorea;
    @BindView(R.id.rg_account_type)
    RadioGroup rgAccountType;
    @BindView(R.id.tvDeMaxTitle)
    TextView tvDeMaxTitle;
    @BindView(R.id.tvG1MaxTitle)
    TextView tvG1MaxTitle;
    @BindView(R.id.tvBaSoMaxTitle)
    TextView tvBaSoMaxTitle;
    @BindView(R.id.tvX2MaxTitle)
    TextView tvX2MaxTitle;
    @BindView(R.id.tvX3MaxTitle)
    TextView tvX3MaxTitle;
    @BindView(R.id.tvX4MaxTitle)
    TextView tvX4MaxTitle;
    private View view;
    private SettingDefault settingDefault;
    private ChooseCustomerPopup customerPopup;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting, container, false);
            ButterKnife.bind(this, view);
            setDataDefault();
            switchEvent();
            clickEvent();

        }
        return view;
    }

    private void setDataDefault() {
        String dateCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(getContext(), "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        if (settingDefault != null) {
            tvTimeNhanLo.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeKhongNhanLo()));
            tvTimeNhanDe.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeKhongNhanDe()));
            scBoTinGiongNhau.setChecked(settingDefault.isTuDongBoTinGiong());
            lnBoTinGiong.setVisibility((settingDefault.isTuDongBoTinGiong() ? View.VISIBLE : View.GONE));
            scGuiCongNo.setChecked(settingDefault.isTuDongGuiTinCongNo());
            lnTuDongGuiCongNo.setVisibility(settingDefault.isTuDongGuiTinCongNo() ? View.VISIBLE : View.GONE);
            tvTimeGuiTinNhanCongNo.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeGuiCongNo()));
            scTuDongCanChuyen.setChecked(settingDefault.isTuDongCanChuyen());
            lnCanChuyenTuDong.setVisibility(settingDefault.isTuDongCanChuyen() ? View.VISIBLE : View.GONE);
            AccountObject accountObject = new Gson().fromJson(settingDefault.getNguoiNhanCanChuyen(), AccountObject.class);
            if (accountObject != null) {
                tvNguoiNhanCanChuyen.setText(accountObject.getAccountName());
            }
            if (settingDefault.getTimeCanChuyenLoTu() > 0) {
                btnGioCanChuyenLoTu.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeCanChuyenLoTu()));
            }
            if (settingDefault.getTimeCanChuyenLoDen() > 0) {
                btnGioCanChuyenLoDen.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeCanChuyenLoDen()));
            }
            if (settingDefault.getTimeCanChuyenDeTu() > 0) {
                btnGioCanChuyenDeTu.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeCanChuyenDeTu()));
            }
            if (settingDefault.getTimeCanChuyenDeDen() > 0) {
                btnGioCanChuyenDeDen.setText(Common.convertMilisecondToHHmm(settingDefault.getTimeCanChuyenDeDen()));
            }


            cetDBMax.setText(Common.formatMoney(settingDefault.getdBGiuLaiMax()));
            cetGiaiNhatMax.setText(Common.formatMoney(settingDefault.getGiaiNhatGiuLaiMax()));
            cetLotoMax.setText(Common.formatMoney(settingDefault.getdBGiuLaiMax()));
            cetBasoMax.setText(Common.formatMoney(settingDefault.getBaSoGiuLaiMax()));
            cetX2Max.setText(Common.formatMoney(settingDefault.getX2GiulaiMax()));
            cetX3Max.setText(Common.formatMoney(settingDefault.getX3GiulaiMax()));
            cetX4Max.setText(Common.formatMoney(settingDefault.getX4GiulaiMax()));


            if (settingDefault.getTiente() == TienTe.TIEN_VIETNAM) {
                rbVn.setChecked(true);
            } else if (settingDefault.getTiente() == TienTe.TIEN_JAPAN) {
                rbJapan.setChecked(true);
            } else if (settingDefault.getTiente() == TienTe.TIEN_TAIWAN) {
                rbTaiwan.setChecked(true);
            } else if (settingDefault.getTiente() == TienTe.TIEN_KOREA) {
                rbKorea.setChecked(true);
            }




            String valueTienTe= TienTe.getValueTienTe(settingDefault.getTiente());
            String keyTienTe= TienTe.getKeyTienTe(settingDefault.getTiente());
            String tiente_type1=getString(R.string.nghin_con,valueTienTe ,"con");
            String tiente_type2=getString(R.string.nghin_con,valueTienTe,"cặp");
            tvDeMaxTitle.setText(tiente_type1);
            tvBaSoMaxTitle.setText(tiente_type1);
            tvG1MaxTitle.setText(tiente_type1);
            tvX2MaxTitle.setText(tiente_type2);
            tvX3MaxTitle.setText(tiente_type2);
            tvX4MaxTitle.setText(tiente_type2);

        }

    }

    private ChooseCustomerAdapter.ItemListener chooseCustomerDone = new ChooseCustomerAdapter.ItemListener() {
        @Override
        public void clickItem(AccountObject entity) {
            try {
                tvNguoiNhanCanChuyen.setText(entity.getAccountName());
                settingDefault.setNguoiNhanCanChuyen(new Gson().toJson(entity));
                customerPopup.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void clickEvent() {
        // chọn người gửi cân chuyển tự động


        ArrayList<AccountObject> accountObjects = new ArrayList<>();
        for (AccountObject accountObject : MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().list()) {
            if (accountObject.getAccountType() == AccountType.STAFFANDBOSS || accountObject.getAccountType() == AccountType.BOSS) {
                accountObjects.add(accountObject);
            }
        }
        tvNguoiNhanCanChuyen.setOnClickListener(v -> {
            try {
                Common.disableView(view);
                if (customerPopup == null || !customerPopup.isShowing()) {
                    customerPopup = new ChooseCustomerPopup(getActivity(), accountObjects, chooseCustomerDone);
                    customerPopup.showAsDropDown(tvNguoiNhanCanChuyen, -100, 0);
                } else {
                    customerPopup.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


        // không nhận lô
        tvTimeNhanLo.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(tvTimeNhanLo.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeKhongNhanDe() < Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian không nhận Lô, Xiên, G1 phải nhỏ hơn đặc biệt, ba càng ", Toast.LENGTH_SHORT).show();
                    } else {
                        tvTimeNhanLo.setText(time);
                        settingDefault.setTimeKhongNhanLo(Common.convertHHMMtoMilisecond(tvTimeNhanLo.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();


        });

        // không nhận đề
        tvTimeNhanDe.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(tvTimeNhanDe.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeKhongNhanLo() > Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian không nhận Lô, Xiên, G1 phải nhỏ hơn đặc biệt, ba càng ", Toast.LENGTH_SHORT).show();
                    } else {
                        tvTimeNhanDe.setText(time);
                        settingDefault.setTimeKhongNhanDe(Common.convertHHMMtoMilisecond(tvTimeNhanDe.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });

        // Tự động chuyển lô Từ
        btnGioCanChuyenLoTu.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(btnGioCanChuyenLoTu.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeCanChuyenLoDen() > 0 && settingDefault.getTimeCanChuyenLoDen() < Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian đến phải lơn hơn thời gian từ", Toast.LENGTH_SHORT).show();
                    } else {
                        btnGioCanChuyenLoTu.setText(getString(R.string.tv_hh_mm, hourOfDay, minute));
                        settingDefault.setTimeCanChuyenLoTu(Common.convertHHMMtoMilisecond(btnGioCanChuyenLoTu.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });

        // Tự động chuyển lô đến
        btnGioCanChuyenLoDen.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(btnGioCanChuyenLoDen.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeCanChuyenLoTu() > 0 && settingDefault.getTimeCanChuyenLoTu() > Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian đến phải lơn hơn thời gian từ", Toast.LENGTH_SHORT).show();
                    } else {
                        btnGioCanChuyenLoDen.setText(getString(R.string.tv_hh_mm, hourOfDay, minute));
                        settingDefault.setTimeCanChuyenLoDen(Common.convertHHMMtoMilisecond(btnGioCanChuyenLoDen.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });

        // Tự động chuyển de den
        btnGioCanChuyenDeTu.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(btnGioCanChuyenDeTu.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeCanChuyenDeDen() > 0 && settingDefault.getTimeCanChuyenDeDen() < Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian đến phải lơn hơn thời gian từ", Toast.LENGTH_SHORT).show();
                    } else {
                        btnGioCanChuyenDeTu.setText(getString(R.string.tv_hh_mm, hourOfDay, minute));
                        settingDefault.setTimeCanChuyenDeTu(Common.convertHHMMtoMilisecond(btnGioCanChuyenDeTu.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });

        // Tự động chuyển de tu
        btnGioCanChuyenDeDen.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Common.convertDateToCalender(btnGioCanChuyenDeDen.getText().toString());
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    if (settingDefault.getTimeCanChuyenDeTu() > 0 && settingDefault.getTimeCanChuyenDeTu() > Common.convertHHMMtoMilisecond(time)) {
                        Toast.makeText(getContext(), "Thời gian đến phải lơn hơn thời gian từ", Toast.LENGTH_SHORT).show();
                    } else {
                        btnGioCanChuyenDeDen.setText(getString(R.string.tv_hh_mm, hourOfDay, minute));
                        settingDefault.setTimeCanChuyenDeDen(Common.convertHHMMtoMilisecond(btnGioCanChuyenDeDen.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });


        // time tự động gửi công nợ
        tvTimeGuiTinNhanCongNo.setOnClickListener(v -> {
            Common.disableView(view);
            Calendar cal = Calendar.getInstance();
            TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                    , (view1, hourOfDay, minute) -> {
                String time = getString(R.string.tv_hh_mm, hourOfDay, minute);
                if (settingDefault != null) {
                    //67080000 = 18:38
                    if (settingDefault.getTimeGuiCongNo() > 0 && Common.convertHHMMtoMilisecond(time) < 67080000) {
                        Toast.makeText(getContext(), "Thời gian gửi tin nhắn chốt công nợ phải lớn hơn thời gian có kết quả sổ xố!", Toast.LENGTH_SHORT).show();
                    } else {
                        tvTimeGuiTinNhanCongNo.setText(getString(R.string.tv_hh_mm, hourOfDay, minute));
                        settingDefault.setTimeGuiCongNo(Common.convertHHMMtoMilisecond(tvTimeGuiTinNhanCongNo.getText().toString()));
                    }
                }

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            datePickerDialog.show();
        });

        btnUpdateSetting.setOnClickListener(v -> {
            int tiente = TienTe.TIEN_VIETNAM;
            if (rbVn.isChecked()) {
                tiente = TienTe.TIEN_VIETNAM;
            } else if (rbJapan.isChecked()) {
                tiente = TienTe.TIEN_JAPAN;

            } else if (rbTaiwan.isChecked()) {
                tiente = TienTe.TIEN_TAIWAN;

            } else if (rbKorea.isChecked()) {
                tiente = TienTe.TIEN_KOREA;

            }
            settingDefault.setTiente(tiente);
            settingDefault.setdBGiuLaiMax(cetDBMax.getNumericValue());
            settingDefault.setGiaiNhatGiuLaiMax(cetGiaiNhatMax.getNumericValue());
            settingDefault.setLoGiuaLaimax(cetLotoMax.getNumericValue());
            settingDefault.setBaSoGiuLaiMax(cetBasoMax.getNumericValue());
            settingDefault.setX2GiulaiMax(cetX2Max.getNumericValue());
            settingDefault.setX3GiulaiMax(cetX3Max.getNumericValue());
            settingDefault.setX4GiulaiMax(cetX4Max.getNumericValue());
            if (settingDefault.isTuDongCanChuyen()) {
                if (settingDefault.getTimeCanChuyenLoTu() == 0 || settingDefault.getTimeCanChuyenLoDen() == 0
                        || settingDefault.getTimeCanChuyenDeTu() == 0
                        || settingDefault.getTimeCanChuyenDeDen() == 0 || tvNguoiNhanCanChuyen.getText().toString().isEmpty()) {

                    Toast.makeText(getContext(), "Bạn cần điền đủ thông tin thời gian và người nhận để cân chuyển", Toast.LENGTH_SHORT).show();
                } else {
                    PreferencesManager.getInstance().setValue(PreferencesManager.SETTING_DEFAULT, new Gson().toJson(settingDefault));
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    if (settingDefault.isTuDongGuiTinCongNo()) {
                        Common.cancelJob(getActivity());
                        Common.startJob(getActivity());
                        Common.startJobTinNhanCongNo(getActivity());
                    }
                    StringCalculate.updateData(getActivity());
                }
            } else {
                PreferencesManager.getInstance().setValue(PreferencesManager.SETTING_DEFAULT, new Gson().toJson(settingDefault));
                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                if (settingDefault.isTuDongGuiTinCongNo()) {
                    Common.cancelJob(getActivity());
                    Common.startJob(getActivity());
                    Common.startJobTinNhanCongNo(getActivity());
                }
                StringCalculate.updateData(getActivity());
            }


        });
    }

    private void switchEvent() {
        scTuDongCanChuyen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lnCanChuyenTuDong.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            settingDefault.setTuDongCanChuyen(isChecked);
        });
        scBoTinGiongNhau.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lnBoTinGiong.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            settingDefault.setTuDongBoTinGiong(isChecked);
        });
        scGuiCongNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lnTuDongGuiCongNo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            settingDefault.setTuDongGuiTinCongNo(isChecked);
        });
        // sự kiện click
        tbnSettingKyTuChuyen.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingKyTuSMSGuiDiActivity.class);
            intent.putExtra(SettingKyTuSMSGuiDiActivity.KY_TU_KEY, settingDefault);
            startActivity(intent);
        });
        btnSettingPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingPasswordActivity.class);
            startActivity(intent);
        });
        btnSettingXien.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingXienActivity.class);
            startActivity(intent);
        });

    }


}
