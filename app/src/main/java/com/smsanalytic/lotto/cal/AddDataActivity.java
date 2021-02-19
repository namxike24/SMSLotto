package com.smsanalytic.lotto.cal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;

public class AddDataActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.lnDate)
    LinearLayout lnDate;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.lnCustomer)
    LinearLayout lnCustomer;
    @BindView(R.id.tvCustomer)
    TextView tvCustomer;
    @BindView(R.id.edValue)
    EditText edValue;
    @BindView(R.id.btnAdd)
    TextView btnAdd;

    private ProgressDialog progressDialog;
    private ArrayList<CustomerEntity> listCustomer = new ArrayList<>();
    private Calendar date;
    private CustomerEntity currentCustomer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        ButterKnife.bind(this);
        date = Calendar.getInstance();
        tvDate.setText(DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
        initListener();
        getDataFromServer();
    }

    private void getDataFromServer() {
        try {
            progressDialog = new ProgressDialog(AddDataActivity.this);
            progressDialog.setMessage("Đang tải dữ liệu...");
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
                                        tvCustomer.setText(listCustomer.get(0).getName());
                                        currentCustomer = listCustomer.get(0);

                                    } else {
                                        Toast.makeText(AddDataActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            }, 250);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(AddDataActivity.this, "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 150);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(CustomerEntity customerEntity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddDataActivity.this);
            builder.setTitle("Xóa khách hàng");
            builder.setMessage(String.format("Bạn có chắc chắn muốn xóa khách hàng %s không?", customerEntity.getName()));
            builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    processDelete(customerEntity);

                }
            });
            builder.setCancelable(false);
            builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDelete(CustomerEntity customerEntity) {
        try {
            progressDialog = new ProgressDialog(AddDataActivity.this);
            progressDialog.setMessage("Đang xóa dữ liệu...");
            progressDialog.show();

            MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Customer").child(customerEntity.getId()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    progressDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDataFromServer();
                        }
                    }, 250);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            lnDate.setOnClickListener(dateListener);
            lnCustomer.setOnClickListener(customerListener);

            btnAdd.setOnClickListener(addListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                if (validate()) {
                    progressDialog = new ProgressDialog(AddDataActivity.this);
                    progressDialog.setMessage("Đang thêm dữ liệu...");
                    progressDialog.show();

                    MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Data")
                            .child(DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DATA_FORMAT)).child(currentCustomer.getId())
                            .child(String.valueOf(Calendar.getInstance().getTimeInMillis())).setValue(Double.parseDouble(edValue.getText().toString().trim()));
                    MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            edValue.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            progressDialog.dismiss();
                            Toast.makeText(AddDataActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean validate() {
        try {
            if (currentCustomer == null) {
                Toast.makeText(AddDataActivity.this, "Khách hàng không được trống", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                if (Double.parseDouble(edValue.getText().toString().trim()) == 0 || TextUtils.isEmpty(edValue.getText().toString().trim())) {
                    Toast.makeText(AddDataActivity.this, "Vui lòng nhập giá trị", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddDataActivity.this, "Vui lòng nhập giá trị", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ChooseCustomerPopup customerPopup;
    private View.OnClickListener customerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                if (customerPopup == null || !customerPopup.isShowing()) {
                    customerPopup = new ChooseCustomerPopup(AddDataActivity.this, listCustomer, chooseCustomerDone);
                    customerPopup.showAsDropDown(view, -100, 0);
                } else {
                    customerPopup.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private ChooseCustomerAdapter.ItemListener chooseCustomerDone = new ChooseCustomerAdapter.ItemListener() {
        @Override
        public void clickItem(CustomerEntity entity) {
            try {
                customerPopup.dismiss();
                currentCustomer = entity;
                tvCustomer.setText(currentCustomer.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddDataActivity.this, android.app.AlertDialog.THEME_HOLO_LIGHT
                        , chooseDateDoneListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener chooseDateDoneListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            try {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, day);
                tvDate.setText(DateTimeUtils.convertDateToString(date.getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
