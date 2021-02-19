package com.smsanalytic.lotto;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class GiaHanDialog extends BaseDialogFragment {
    @BindView(R.id.edName)
    EditText edName;
    @BindView(R.id.tvMa)
    TextView tvMa;
    @BindView(R.id.tvNgayHetHan)
    TextView tvNgayHetHan;
    @BindView(R.id.tvGiaHanDen)
    TextView tvGiaHanDen;
    @BindView(R.id.btnUpdate)
    TextView btnUpdate;

    private AccountEntity accountEntity;
    private ProgressDialog progressDialog;
    private DialogListener listener;

    public static GiaHanDialog newInstance(AccountEntity accountEntity, DialogListener listener) {
        GiaHanDialog fragment = new GiaHanDialog();
        fragment.accountEntity = accountEntity;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        try {
            ButterKnife.bind(this, rootView);
            displayData();
            initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {
            tvGiaHanDen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    Calendar calendar = (Calendar) tvGiaHanDen.getTag();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                            , chooseDateDoneListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                    datePickerDialog.show();
                }
            });

            btnUpdate.setOnClickListener(updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isGiaHanSuccess;
    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                accountEntity.setName(edName.getText().toString().trim());
                Calendar calendar = (Calendar) tvGiaHanDen.getTag();
                accountEntity.setDateExpired(DateTimeUtils.convertDateToString(calendar.getTime(), DateTimeUtils.SERVER_DATE_TIME_PATTERN));

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Đang cập nhật...");
                progressDialog.show();

                MyApp.mFirebaseDatabase.child(Common.DBFB).child(accountEntity.getImei()).child("Account").setValue(accountEntity);
                MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (!isGiaHanSuccess) {
                            isGiaHanSuccess = true;
                            listener.onUpdateSuccess();
                            dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        progressDialog.dismiss();
                        isGiaHanSuccess = false;
                        Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private DatePickerDialog.OnDateSetListener chooseDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                Calendar calendar = (Calendar) tvGiaHanDen.getTag();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                tvGiaHanDen.setText(DateTimeUtils.convertDateToString(calendar.getTime(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN));
                tvGiaHanDen.setTag(calendar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void displayData() {
        try {
            if (!TextUtils.isEmpty(accountEntity.getName())) {
                edName.setText(accountEntity.getName());
            }
            tvMa.setText(accountEntity.getImei());

            Calendar current = Calendar.getInstance();
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (current.after(calendar)) {
                tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                        + " (Hết hạn)");
                tvNgayHetHan.setTextColor(getResources().getColor(R.color.red_light));
            } else {
                long date = (calendar.getTimeInMillis() - current.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                if (date == 0) {
                    tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                            + " (" + date + " ngày)");
                    tvNgayHetHan.setTextColor(getResources().getColor(R.color.red_light));
                } else {
                    tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                            + " (" + date + " ngày)");
                }
            }

            tvGiaHanDen.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN));
            tvGiaHanDen.setTag(calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getDialogWidth() {
        return Common.getScreenWitch(getActivity()) - Common.convertDpToPx(50f, getActivity());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_gia_han;
    }

    @Override
    public String getTAG() {
        return GiaHanDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onUpdateSuccess();
    }
}
