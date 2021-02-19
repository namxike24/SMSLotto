package com.smsanalytic.lotto.cal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;

public class CustomerActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tbCustomer)
    TableLayout tbCustomer;
    @BindView(R.id.btnAdd)
    TextView btnAdd;

    private ProgressDialog progressDialog;
    private ArrayList<CustomerEntity> listData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);
        initListener();
        getDataFromServer();
    }

    private void getDataFromServer() {
        try {
            progressDialog = new ProgressDialog(CustomerActivity.this);
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
                                        listData.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                listData.add(snapshot.getValue(CustomerEntity.class));
                                        }
                                        displayData();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(CustomerActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                        tbCustomer.removeAllViews();
                                    }
                                }
                            },250);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(CustomerActivity.this, "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            },150);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        try {
            tbCustomer.removeAllViews();
            for (CustomerEntity customerEntity : listData) {
                View tableRow = LayoutInflater.from(CustomerActivity.this).inflate(R.layout.table_customer_row, null, false);
                TextView tvName = tableRow.findViewById(R.id.tvName);
                TextView tvDelete = tableRow.findViewById(R.id.tvDelete);

                tvName.setText(customerEntity.getName());
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Common.disableView(view);
                        confirmDelete(customerEntity);
                    }
                });
                tbCustomer.addView(tableRow);
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(CustomerEntity customerEntity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
            builder.setTitle("Xóa khách hàng");
            builder.setMessage(String.format("Bạn có chắc chắn muốn xóa khách hàng %s không?",customerEntity.getName()));
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
            progressDialog = new ProgressDialog(CustomerActivity.this);
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
           btnAdd.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AddCustomerDialog addCustomerDialog = AddCustomerDialog.newInstance(addListener);
                   addCustomerDialog.show(getSupportFragmentManager());
               }
           });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AddCustomerDialog.DialogListener addListener = new AddCustomerDialog.DialogListener() {
        @Override
        public void onAddSuccess() {
            getDataFromServer();
        }
    };
}
