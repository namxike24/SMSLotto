package com.smsanalytic.lotto.cal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;

public class ReportActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.lnFromDate)
    LinearLayout lnFromDate;
    @BindView(R.id.tvFromDate)
    TextView tvFromDate;
    @BindView(R.id.lnToDate)
    LinearLayout lnToDate;
    @BindView(R.id.tvToDate)
    TextView tvToDate;
    @BindView(R.id.btnGetData)
    TextView btnGetData;
    @BindView(R.id.rcvData)
    RecyclerView rcvData;
    @BindView(R.id.tvTotalAll)
    TextView tvTotalAll;

    private ProgressDialog progressDialog;
    private ArrayList<CustomerEntity> listCustomer = new ArrayList<>();
    private Calendar fromDate;
    private Calendar toDate;
    private ReportCalColumnAdapter adapter;
    private ArrayList<ReportCalColumnEntity> listData = new ArrayList<>();
    private ArrayList<String> listDay = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        fromDate = DateTimeUtils.getFirstDayOfWeek();
        fromDate.set(Calendar.HOUR_OF_DAY, 0);
        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        toDate = DateTimeUtils.getLastDayOfWeek();
        toDate.set(Calendar.HOUR_OF_DAY, 0);
        toDate.set(Calendar.MINUTE, 0);
        toDate.set(Calendar.SECOND, 0);
        toDate.set(Calendar.MILLISECOND, 0);
        tvFromDate.setText(DateTimeUtils.convertDateToString(fromDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
        tvToDate.setText(DateTimeUtils.convertDateToString(toDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
        initListener();
        getDataCustomerFromServer();
        initRecyclerView();
    }

    private void initRecyclerView() {
        try {
            adapter = new ReportCalColumnAdapter(ReportActivity.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ReportActivity.this, RecyclerView.HORIZONTAL, false);
            rcvData.setLayoutManager(layoutManager);
            adapter.setData(new ArrayList<>());
            rcvData.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataCustomerFromServer() {
        try {
            progressDialog = new ProgressDialog(ReportActivity.this);
            progressDialog.setMessage("Đang tải dữ liệu khách hàng...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Customer");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        listCustomer.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            listCustomer.add(snapshot.getValue(CustomerEntity.class));
                                        }

                                    }
                                    progressDialog.dismiss();
                                }
                            }, 250);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }, 150);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromServer() {
        try {
            progressDialog = new ProgressDialog(ReportActivity.this);
            progressDialog.setMessage("Đang tải dữ liệu...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Data").orderByKey()
                            .startAt(DateTimeUtils.convertDateToString(fromDate.getTime(), DateTimeUtils.DATA_FORMAT))
                            .endAt(DateTimeUtils.convertDateToString(toDate.getTime(), DateTimeUtils.DATA_FORMAT));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listData.clear();
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        processData(dataSnapshot);
                                    } else {
                                        Toast.makeText(ReportActivity.this, "Không có dữ liệu" + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                        tvTotalAll.setVisibility(View.GONE);
                                    }
                                    adapter.setData(listData);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            }, 250);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(ReportActivity.this, "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 150);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double totalAll;

    private void processData(@NonNull DataSnapshot dataSnapshot) {
        try {
            totalAll = 0;
            //Add cột ngày
            listData.add(getColumnDay());
            //Add cột khách hàng
            for (CustomerEntity customerEntity : listCustomer) {
                listData.add(getColumnCustomer(dataSnapshot, customerEntity));
            }
            if (totalAll >= 0) {
                tvTotalAll.setText(HtmlCompat.fromHtml(String.format(getString(R.string.html_total_all_black)
                        , Common.formatMoney(totalAll)), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                tvTotalAll.setText(HtmlCompat.fromHtml(String.format(getString(R.string.html_total_all_red)
                        , Common.formatMoney(totalAll)), HtmlCompat.FROM_HTML_MODE_LEGACY));
            }
            tvTotalAll.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReportCalColumnEntity getColumnCustomer(DataSnapshot dataSnapshot, CustomerEntity customerEntity) {
        ReportCalColumnEntity columnEntity = new ReportCalColumnEntity();
        ArrayList<ReportCalEntity> dataColumn = new ArrayList<>();
        ReportCalEntity title = new ReportCalEntity();
        title.setType(ReportCalEntity.TYPE_NAME);
        title.setCustomerID(customerEntity.getId());
        title.setCustomerName(customerEntity.getName());
        dataColumn.add(title);
        double total = 0;
        for (int i = 0; i < listDay.size(); i++) {
            String day = listDay.get(i);
            ReportCalEntity entity = new ReportCalEntity();
            entity.setCustomerID(customerEntity.getId());
            entity.setType(ReportCalEntity.TYPE_VALUE);
            entity.setValue(0);
            entity.setDate(day);
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                if (snapshot.getKey().equalsIgnoreCase(day)) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getKey().equalsIgnoreCase(customerEntity.getId())) {
                            double value = 0;
                            for (DataSnapshot childValue : child.getChildren()) {
                                value += childValue.getValue(Double.class);
                            }
                            Log.e("Value", "" + value);
                            entity.setValue(value);
                            total += value;
                        }
                    }
                }

            }
            dataColumn.add(entity);
        }
        totalAll += total;
        ReportCalEntity entityTotal = new ReportCalEntity();
        entityTotal.setCustomerID(customerEntity.getId());
        entityTotal.setType(ReportCalEntity.TYPE_VALUE);
        entityTotal.setValue(total);
        entityTotal.setDate("");
        dataColumn.add(entityTotal);
        columnEntity.setListData(dataColumn);
        return columnEntity;
    }

    private ReportCalColumnEntity getColumnDay() {
        ReportCalColumnEntity columnEntity = new ReportCalColumnEntity();
        ArrayList<ReportCalEntity> dataColumn = new ArrayList<>();
        dataColumn.add(new ReportCalEntity(ReportCalEntity.TYPE_DATE, ""));
        for (String day : listDay) {
            Date date = DateTimeUtils.getDateFromString(day, DateTimeUtils.DATA_FORMAT);
            dataColumn.add(new ReportCalEntity(ReportCalEntity.TYPE_DATE, DateTimeUtils.convertDateToString(date, DateTimeUtils.DAY_MONTH_YEAR_PATTERN)));
        }
        ReportCalEntity total = new ReportCalEntity();
        total.setType(ReportCalEntity.TYPE_NAME);
        total.setCustomerID("");
        total.setCustomerName("Tổng");
        dataColumn.add(total);
        columnEntity.setListData(dataColumn);
        return columnEntity;
    }


    private void initListener() {
        try {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    finish();
                }
            });

            lnFromDate.setOnClickListener(fromDateListener);
            lnToDate.setOnClickListener(toDateListener);
//            lnCustomer.setOnClickListener(customerListener);
//
            btnGetData.setOnClickListener(getDataListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener getDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                if (validate()) {
                    listDay = getListDay();
                    getDataFromServer();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private ArrayList<String> getListDay() {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate.getTime());
        Calendar to = Calendar.getInstance();
        to.setTime(toDate.getTime());
        ArrayList<String> result = new ArrayList<>();
        if (from.get(Calendar.DATE) == to.get(Calendar.DATE)) {
            result.add(DateTimeUtils.convertDateToString(from.getTime(), DateTimeUtils.DATA_FORMAT));
        } else {
            while (from.get(Calendar.DATE) != to.get(Calendar.DATE)) {
                result.add(DateTimeUtils.convertDateToString(from.getTime(), DateTimeUtils.DATA_FORMAT));
                from.add(Calendar.DATE, 1);
            }
            result.add(DateTimeUtils.convertDateToString(from.getTime(), DateTimeUtils.DATA_FORMAT));
        }
        return result;
    }

    private boolean validate() {
        try {
            if (fromDate.after(toDate)) {
                Toast.makeText(ReportActivity.this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (toDate.getTimeInMillis() - fromDate.getTimeInMillis() > 6 * 24 * 60 * 60 * 1000) {
                Toast.makeText(ReportActivity.this, "Chỉ lấy dữ liệu trong vòng 7 ngày", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private View.OnClickListener fromDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT
                        , chooseFromDateDoneListener, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener chooseFromDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                fromDate.set(Calendar.YEAR, year);
                fromDate.set(Calendar.MONTH, month);
                fromDate.set(Calendar.DAY_OF_MONTH, day);
                tvFromDate.setText(DateTimeUtils.convertDateToString(fromDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private View.OnClickListener toDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT
                        , chooseToDateDoneListener, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener chooseToDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                toDate.set(Calendar.YEAR, year);
                toDate.set(Calendar.MONTH, month);
                toDate.set(Calendar.DAY_OF_MONTH, day);
                tvToDate.setText(DateTimeUtils.convertDateToString(toDate.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
