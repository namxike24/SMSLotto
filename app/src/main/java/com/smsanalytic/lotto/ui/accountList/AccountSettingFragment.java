package com.smsanalytic.lotto.ui.accountList;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.entities.AccountSettingFromJson;
import com.smsanalytic.lotto.entities.AccountSettingItem;
import com.smsanalytic.lotto.ui.document.ChooseCustomerPopup;
import com.smsanalytic.lotto.ui.document.adapter.ChooseCustomerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSettingFragment extends Fragment {
    @BindView(R.id.spinner1)
    Spinner phantichtindiden;
    @BindView(R.id.spinner2)
    Spinner traloitinnhan;
    @BindView(R.id.spinner3)
    Spinner kieubaocongno;
    @BindView(R.id.spinner4)
    Spinner chuyende78;
    @BindView(R.id.spinner5)
    Spinner chapnhandonvilolanghin;
    @BindView(R.id.spinner6)
    Spinner donvitinhde;
    @BindView(R.id.spinner7)
    Spinner tudongtralaikhiquagionhan;
    @BindView(R.id.spinner8)
    Spinner gioihannhanso;
    @BindView(R.id.spinner9)
    Spinner khanhgiulaicophan;
    @BindView(R.id.lnChiTietCoPhan)
    LinearLayout lnChiTietCoPhan;
    @BindView(R.id.edCPDacBiet)
    EditText edCPDacBiet;
    @BindView(R.id.edCPLo)
    EditText edCPLo;
    @BindView(R.id.edCPXien)
    EditText edCPXien;
    @BindView(R.id.edCPBaCang)
    EditText edCPBaCang;
    @BindView(R.id.spinner10)
    Spinner tudongchuyenditinden;
    @BindView(R.id.spinner11)
    Spinner tudongnhantinchodl;
    @BindView(R.id.spinner12)
    Spinner sonhaylotrathuongtoida;
    @BindView(R.id.tvNguoiNhan)
    TextView tvNguoiNhan;

    @BindView(R.id.rlNguoiNhan)
    RelativeLayout rlNguoiNhan;
    @BindView(R.id.tv_PhanTram_Lo)
    TextView tvPhanTramLo;
    @BindView(R.id.sBChuyenDi)
    SeekBar sBChuyenDi;
    @BindView(R.id.tv_PhanTram_Lo1)
    TextView tvPhanTramLo1;
    @BindView(R.id.rlTudongChuyenDi)
    RelativeLayout rlTudongChuyenDi;
    @BindView(R.id.lnPhanTramChuyen)
    LinearLayout lnPhanTramChuyen;
    @BindView(R.id.tvAcount)
    TextView tvAcount;
    @BindView(R.id.ivArrowChonKH)
    ImageView ivArrowChonKH;
    @BindView(R.id.rlAccount)
    RelativeLayout rlAccount;
    @BindView(R.id.tv_chapnhandonvilo_title)
    TextView tvChapnhandonviloTitle;
    private View view;
    private ChooseCustomerPopup customerPopup;

    AccountSettingFromJson accountSettingFromJson;
    AccountSetting accountSetting;
    AddAccountActivity activity;
    private ArrayList<AccountObject> listCustomer;
    private DaoSession daoSession;

    public AccountSettingFragment() {
        // Required empty public constructor
    }

    public AccountSetting getaccountSetting() {
        try {
            if (accountSetting.getKhachgiulaicophan() == 2) {
                String db = edCPDacBiet.getText().toString();
                accountSetting.setCophandacbiet(TextUtils.isEmpty(db) ? 0 : Double.parseDouble(Common.formatPercent(Double.parseDouble(db))));
                String lo = edCPLo.getText().toString();
                accountSetting.setCophanlo(TextUtils.isEmpty(lo) ? 0 : Double.parseDouble(Common.formatPercent(Double.parseDouble(lo))));
                String xien = edCPXien.getText().toString();
                accountSetting.setCophanxien(TextUtils.isEmpty(xien) ? 0 : Double.parseDouble(Common.formatPercent(Double.parseDouble(xien))));
                String bacang = edCPBaCang.getText().toString();
                accountSetting.setCophan3c(TextUtils.isEmpty(bacang) ? 0 : Double.parseDouble(Common.formatPercent(Double.parseDouble(bacang))));
            } else {
                accountSetting.setCophandacbiet(0);
                accountSetting.setCophanlo(0);
                accountSetting.setCophanxien(0);
                accountSetting.setCophan3c(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountSetting;
    }

    public static AccountSettingFragment newInstance() {

        Bundle args = new Bundle();
        AccountSettingFragment fragment = new AccountSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_account_setting, container, false);
            ButterKnife.bind(this, view);
            activity = (AddAccountActivity) getActivity();
            accountSetting = activity.getAccountSetting();
            daoSession = MyApp.getInstance().getDaoSession();
            int donviTiente= Common.getTypeTienTe(getContext());
            String donviString=TienTe.getValueTienTe(donviTiente);
            tvChapnhandonviloTitle.setText(getString(R.string.tv_chapnhandonvilo,donviString));

            // lấy dữ liệu spinner từ file json
            String dataSpinnerList = Common.loadJSONFromAsset(getContext(), "AccountSetting.json");
            int typeTienTe = Common.getTypeTienTe(getContext());
            if (typeTienTe == TienTe.TIEN_JAPAN) {
                dataSpinnerList = Common.loadJSONFromAsset(getContext(), "AccountSettingJapan.json");
            } else if (typeTienTe == TienTe.TIEN_TAIWAN) {
                dataSpinnerList = Common.loadJSONFromAsset(getContext(), "AccountSettingTaiwan.json");
            } else if (typeTienTe == TienTe.TIEN_KOREA) {
                dataSpinnerList = Common.loadJSONFromAsset(getContext(), "AccountSettingKorea.json");
            }
            accountSettingFromJson = new Gson().fromJson(dataSpinnerList, AccountSettingFromJson.class);
            initSpinner();

            // lấy danh sách khách hàng là chủ
            listCustomer = new ArrayList<>();
            List<AccountObject> accountObjects = new ArrayList<>();
            for (AccountObject accountObject : daoSession.getAccountObjectDao().queryBuilder().list()) {
                if (accountObject.getAccountType() == AccountType.STAFFANDBOSS || accountObject.getAccountType() == AccountType.BOSS) {
                    accountObjects.add(accountObject);
                }
            }
            listCustomer.addAll(accountObjects);
            rlAccount.setOnClickListener(v -> {
                try {
                    Common.disableView(view);
                    if (customerPopup == null || !customerPopup.isShowing()) {
                        customerPopup = new ChooseCustomerPopup(getActivity(), listCustomer, chooseCustomerDone);
                        customerPopup.showAsDropDown(rlAccount, -100, 0);
                    } else {
                        customerPopup.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            });
            sBChuyenDi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvPhanTramLo.setText(getString(R.string.tv_format_present, progress));
                    accountSetting.setPhantramChuyendi(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }
        return view;
    }

    private ChooseCustomerAdapter.ItemListener chooseCustomerDone = new ChooseCustomerAdapter.ItemListener() {
        @Override
        public void clickItem(AccountObject entity) {
            try {
                tvAcount.setText(entity.getAccountName());
                accountSetting.setIdAccountNhanChuyen(entity.getIdAccount());
                accountSetting.setAccoutNaemNhanChuyen(entity.getAccountName());
                accountSetting.setAccountStattus(entity.getAccountStatus());
                customerPopup.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initSpinner() {
        try {
            if (accountSettingFromJson != null && accountSetting != null) {
                tvPhanTramLo.setText(getString(R.string.tv_format_present, accountSetting.getPhantramChuyendi()));
                sBChuyenDi.setProgress(accountSetting.getPhantramChuyendi());
                tvAcount.setText(accountSetting.getAccoutNaemNhanChuyen());

                // 1
                ArrayAdapter<AccountSettingItem> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getPhantichtindendi());
                phantichtindiden.setAdapter(adapter1);
                phantichtindiden.setSelection(accountSetting.getPhantichtindendi() - 1);
                phantichtindiden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setPhantichtindendi(accountSettingFromJson.getPhantichtindendi().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 2
                ArrayAdapter<AccountSettingItem> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getTraloitinnhan());
                traloitinnhan.setAdapter(adapter2);
                traloitinnhan.setSelection(accountSetting.getTraloitinnhan() - 1);
                traloitinnhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setTraloitinnhan(accountSettingFromJson.getTraloitinnhan().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 3
                ArrayAdapter<AccountSettingItem> adapter3 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getKieubaocongno());
                kieubaocongno.setAdapter(adapter3);
                kieubaocongno.setSelection(accountSetting.getKieubaocongno() - 1);
                kieubaocongno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setKieubaocongno(accountSettingFromJson.getKieubaocongno().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 4
                ArrayAdapter<AccountSettingItem> adapter4 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getChuyende78());
                chuyende78.setAdapter(adapter4);
                chuyende78.setSelection(accountSetting.getChuyende78() - 1);
                chuyende78.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setChuyende78(accountSettingFromJson.getChuyende78().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 5
                ArrayAdapter<AccountSettingItem> adapter5 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getChapnhandonvilolanghin());
                chapnhandonvilolanghin.setAdapter(adapter5);
                chapnhandonvilolanghin.setSelection(accountSetting.getChapnhandonvilolanghin() - 1);
                chapnhandonvilolanghin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setChapnhandonvilolanghin(accountSettingFromJson.getChapnhandonvilolanghin().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 6
                ArrayAdapter<AccountSettingItem> adapter6 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getDonvitinhde());
                donvitinhde.setAdapter(adapter6);
                donvitinhde.setSelection(accountSetting.getDonvitinhde() - 1);
                donvitinhde.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setDonvitinhde(accountSettingFromJson.getDonvitinhde().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 7
                ArrayAdapter<AccountSettingItem> adapter7 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getTudongtralaitinkhiquagionhan());
                tudongtralaikhiquagionhan.setAdapter(adapter7);
                tudongtralaikhiquagionhan.setSelection(accountSetting.getTudongtralaitinkhiquagionhan() - 1);
                tudongtralaikhiquagionhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setTudongtralaitinkhiquagionhan(accountSettingFromJson.getTudongtralaitinkhiquagionhan().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 8
                ArrayAdapter<AccountSettingItem> adapter8 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getGioihannhan1so());
                gioihannhanso.setAdapter(adapter8);
                gioihannhanso.setSelection(accountSetting.getGioihannhan1so() - 1);
                gioihannhanso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setGioihannhan1so(accountSettingFromJson.getGioihannhan1so().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 9
                ArrayAdapter<AccountSettingItem> adapter9 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getKhachgiulaicophan());
                khanhgiulaicophan.setAdapter(adapter9);
                khanhgiulaicophan.setSelection(accountSetting.getKhachgiulaicophan() - 1);
                khanhgiulaicophan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setKhachgiulaicophan(accountSettingFromJson.getKhachgiulaicophan().get(position).getId());
                        if (accountSettingFromJson.getKhachgiulaicophan().get(position).getId() == 2) {
                            lnChiTietCoPhan.setVisibility(View.VISIBLE);
                            setValueToCoPhan();
                        } else {
                            lnChiTietCoPhan.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 10
                ArrayAdapter<AccountSettingItem> adapter10 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getTudongchuyenditinden());
                tudongchuyenditinden.setAdapter(adapter10);
                tudongchuyenditinden.setSelection(accountSetting.getTudongchuyenditinden() - 1);
                tudongchuyenditinden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setTudongchuyenditinden(accountSettingFromJson.getTudongchuyenditinden().get(position).getId());
                        if (position == 0) {
                            rlTudongChuyenDi.setVisibility(View.GONE);
                        } else if (position == 1) {

                            rlTudongChuyenDi.setVisibility(View.VISIBLE);
                            lnPhanTramChuyen.setVisibility(View.VISIBLE);
                        } else {
                            rlTudongChuyenDi.setVisibility(View.VISIBLE);
                            lnPhanTramChuyen.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 11
                ArrayAdapter<AccountSettingItem> adapter11 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getTudongnhantinchodlkhihetgionhan());
                tudongnhantinchodl.setAdapter(adapter11);
                tudongnhantinchodl.setSelection(accountSetting.getTudongnhantinchodlkhihetgionhan() - 1);
                tudongnhantinchodl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setTudongnhantinchodlkhihetgionhan(accountSettingFromJson.getTudongnhantinchodlkhihetgionhan().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // 12
                ArrayAdapter<AccountSettingItem> adapter12 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountSettingFromJson.getSonhaylotrathuongtoida());
                sonhaylotrathuongtoida.setAdapter(adapter12);
                sonhaylotrathuongtoida.setSelection(accountSetting.getSonhaylotrathuongtoida() - 1);
                sonhaylotrathuongtoida.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        accountSetting.setSonhaylotrathuongtoida(accountSettingFromJson.getSonhaylotrathuongtoida().get(position).getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        } catch (Exception e) {

        }
    }

    private void setValueToCoPhan() {
        try {
            edCPDacBiet.setText(accountSetting.getCophandacbiet() == 0 ? "" : Common.formatPercent(accountSetting.getCophandacbiet()));
            edCPLo.setText(accountSetting.getCophanlo() == 0 ? "" : Common.formatPercent(accountSetting.getCophanlo()));
            edCPBaCang.setText(accountSetting.getCophan3c() == 0 ? "" : Common.formatPercent(accountSetting.getCophan3c()));
            edCPXien.setText(accountSetting.getCophanxien() == 0 ? "" : Common.formatPercent(accountSetting.getCophanxien()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
