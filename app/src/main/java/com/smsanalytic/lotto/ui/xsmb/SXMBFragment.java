package com.smsanalytic.lotto.ui.xsmb;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.GetDataXSMB;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.unit.PreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SXMBFragment extends BaseFragment {
    private View view;
    @BindView(R.id.lnData)
    LinearLayout lnData;
    @BindView(R.id.lnNoData)
    LinearLayout lnNoData;
    @BindView(R.id.tvGiaiDB)
    TextView tvGiaiDB;
    @BindView(R.id.tvGiaiNhat)
    TextView tvGiaiNhat;
    @BindView(R.id.tvGiaiNhi1)
    TextView tvGiaiNhi1;
    @BindView(R.id.tvGiaiNhi2)
    TextView tvGiaiNhi2;
    @BindView(R.id.tvGiaiBa1)
    TextView tvGiaiBa1;
    @BindView(R.id.tvGiaiBa2)
    TextView tvGiaiBa2;
    @BindView(R.id.tvGiaiBa3)
    TextView tvGiaiBa3;
    @BindView(R.id.tvGiaiBa4)
    TextView tvGiaiBa4;
    @BindView(R.id.tvGiaiBa5)
    TextView tvGiaiBa5;
    @BindView(R.id.tvGiaiBa6)
    TextView tvGiaiBa6;
    @BindView(R.id.tvGiaiTu1)
    TextView tvGiaiTu1;
    @BindView(R.id.tvGiaiTu2)
    TextView tvGiaiTu2;
    @BindView(R.id.tvGiaiTu3)
    TextView tvGiaiTu3;
    @BindView(R.id.tvGiaiTu4)
    TextView tvGiaiTu4;
    @BindView(R.id.tvGiaiNam1)
    TextView tvGiaiNam1;
    @BindView(R.id.tvGiaiNam2)
    TextView tvGiaiNam2;
    @BindView(R.id.tvGiaiNam3)
    TextView tvGiaiNam3;
    @BindView(R.id.tvGiaiNam4)
    TextView tvGiaiNam4;
    @BindView(R.id.tvGiaiNam5)
    TextView tvGiaiNam5;
    @BindView(R.id.tvGiaiNam6)
    TextView tvGiaiNam6;
    @BindView(R.id.tvGiaiSau1)
    TextView tvGiaiSau1;
    @BindView(R.id.tvGiaiSau2)
    TextView tvGiaiSau2;
    @BindView(R.id.tvGiaiSau3)
    TextView tvGiaiSau3;
    @BindView(R.id.tvGiaiBay1)
    TextView tvGiaiBay1;
    @BindView(R.id.tvGiaiBay2)
    TextView tvGiaiBay2;
    @BindView(R.id.tvGiaiBay3)
    TextView tvGiaiBay3;
    @BindView(R.id.tvGiaiBay4)
    TextView tvGiaiBay4;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.btnGetData)
    Button btnGetData;

    @BindView(R.id.rlDate)
    RelativeLayout rlDate;
    private Calendar date = Calendar.getInstance();
    private ProgressDialog progressDialog;


    public SXMBFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sxmb, container, false);
            ButterKnife.bind(this, view);
        }
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang lấy dữ liệu...");
            tvDate.setText(DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            processGetData();
            btnGetData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Common.disableView(view);
                        processGetData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            rlDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                            , chooseDateDoneListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                    datePickerDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DatePickerDialog.OnDateSetListener chooseDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, day);
                tvDate.setText(DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
                processGetData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void processGetData() {
        try {
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new GetDataXSMB(getActivity(), getDataListener, DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT)).start();
                }
            }, 250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GetDataXSMB.Listener getDataListener = new GetDataXSMB.Listener() {
        @Override
        public void onSuccess(Map<String, ArrayList<String>> data, Map<String, ArrayList<String>> dataHit) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressDialog.dismiss();
                            if (data == null || data.isEmpty()) {
                                lnData.setVisibility(View.GONE);
                                lnNoData.setVisibility(View.VISIBLE);
//                                Toast.makeText(getActivity(), "Dữ liệu đang cập nhật", Toast.LENGTH_LONG).show();
                            } else {
                                lnData.setVisibility(View.VISIBLE);
                                lnNoData.setVisibility(View.GONE);
                                mapData(data);
                                if (DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT)
                                        .equalsIgnoreCase(DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT))) {
                                    PreferencesManager.getInstance().setValue(Common.LAST_DAY_GET_XSMB, DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
                                    XSMBUtils.TinhKetQuaXS(dataHit, date);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }
    };

    private void mapData(Map<String, ArrayList<String>> data) {
        try {
            tvGiaiDB.setText(data.get("giaidb").get(0));

            tvGiaiNhat.setText(data.get("giai1").get(0));

            tvGiaiNhi1.setText(data.get("giai2").get(0));
            tvGiaiNhi2.setText(data.get("giai2").size() >= 2 ? data.get("giai2").get(1) : "");

            tvGiaiBa1.setText(data.get("giai3").get(0));
            tvGiaiBa2.setText(data.get("giai3").size() >= 2 ? data.get("giai3").get(1) : "");
            tvGiaiBa3.setText(data.get("giai3").size() >= 3 ? data.get("giai3").get(2) : "");
            tvGiaiBa4.setText(data.get("giai3").size() >= 4 ? data.get("giai3").get(3) : "");
            tvGiaiBa5.setText(data.get("giai3").size() >= 5 ? data.get("giai3").get(4) : "");
            tvGiaiBa6.setText(data.get("giai3").size() >= 6 ? data.get("giai3").get(5) : "");

            tvGiaiTu1.setText(data.get("giai4").get(0));
            tvGiaiTu2.setText(data.get("giai4").size() >= 2 ? data.get("giai4").get(1) : "");
            tvGiaiTu3.setText(data.get("giai4").size() >= 3 ? data.get("giai4").get(2) : "");
            tvGiaiTu4.setText(data.get("giai4").size() >= 4 ? data.get("giai4").get(3) : "");

            tvGiaiNam1.setText(data.get("giai5").get(0));
            tvGiaiNam2.setText(data.get("giai5").size() >= 2 ? data.get("giai5").get(1) : "");
            tvGiaiNam3.setText(data.get("giai5").size() >= 3 ? data.get("giai5").get(2) : "");
            tvGiaiNam4.setText(data.get("giai5").size() >= 4 ? data.get("giai5").get(3) : "");
            tvGiaiNam5.setText(data.get("giai5").size() >= 5 ? data.get("giai5").get(4) : "");
            tvGiaiNam6.setText(data.get("giai5").size() >= 6 ? data.get("giai5").get(5) : "");

            tvGiaiSau1.setText(data.get("giai6").get(0));
            tvGiaiSau2.setText(data.get("giai6").size() >= 2 ? data.get("giai6").get(1) : "");
            tvGiaiSau3.setText(data.get("giai6").size() >= 3 ? data.get("giai6").get(2) : "");

            tvGiaiBay1.setText(data.get("giai7").get(0));
            tvGiaiBay2.setText(data.get("giai7").size() >= 2 ? data.get("giai7").get(1) : "");
            tvGiaiBay3.setText(data.get("giai7").size() >= 3 ? data.get("giai7").get(2) : "");
            tvGiaiBay4.setText(data.get("giai7").size() >= 4 ? data.get("giai7").get(3) : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
