package com.smsanalytic.lotto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.common.VNCharacterUtils;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class AccountManagerActivity extends AppCompatActivity {
    @BindView(R.id.tbAccount)
    TableLayout tbAccount;
    @BindView(R.id.edSearch)
    EditText edSearch;
    @BindView(R.id.btnCapNhatSoDT)
    Button btnCapNhatSoDT;
    private ArrayList<AccountEntity> listData;
    private ArrayList<AccountEntity> listDataOrigin;
    private ProgressDialog progressDialog;

    private Timer timer;

    private String giahan = "", kythuat = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        ButterKnife.bind(this);
        listData = new ArrayList<>();
        listDataOrigin = new ArrayList<>();
        getDataFromServer();
        initListener();
    }

    private void initListener() {
        try {
            edSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            displayData();
                                        }
                                    });
                                }
                            }, 250);
                }
            });

            btnCapNhatSoDT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    LienHeDialog dialog = LienHeDialog.newInstance(giahan, kythuat, new LienHeDialog.DialogListener() {
                        @Override
                        public void onUpdateSuccess(String gh, String kt) {
                            giahan = gh;
                            kythuat = kt;
                        }
                    });
                    dialog.show(getSupportFragmentManager());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromServer() {
        try {
            progressDialog = new ProgressDialog(AccountManagerActivity.this);
            progressDialog.setMessage("Đang tải dữ liệu...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        listDataOrigin.clear();
                                        listData.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (snapshot.hasChild("Account")) {
                                                listDataOrigin.add(snapshot.child("Account").getValue(AccountEntity.class));
                                            }
                                        }
                                        displayData();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(AccountManagerActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },150);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(AccountManagerActivity.this, "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            },150);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Query query1 = MyApp.mFirebaseDatabase.child(Common.DBFB).child("lien_he");
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    giahan = dataSnapshot.child("gia_han").getValue().toString();
                                    kythuat = dataSnapshot.child("ky_thuat").getValue().toString();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            },1500);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        try {
            listData.clear();
            String keySearch = VNCharacterUtils.removeAccent(edSearch.getText().toString().trim().toLowerCase());
            if (TextUtils.isEmpty(keySearch)) {
                listData.addAll(listDataOrigin);
            } else {
                for (AccountEntity accountEntity : listDataOrigin) {
                    if (VNCharacterUtils.removeAccent(accountEntity.getImei().toLowerCase()).contains(keySearch)
                            || (!TextUtils.isEmpty(accountEntity.getName()) && VNCharacterUtils.removeAccent(accountEntity.getName().toLowerCase()).contains(keySearch))) {
                        listData.add(accountEntity);
                    }
                }
            }
            tbAccount.removeAllViews();
            View tableRowTitle = LayoutInflater.from(AccountManagerActivity.this).inflate(R.layout.table_account_header, null, false);
            tbAccount.addView(tableRowTitle);
            for (int i = 0; i < listData.size(); i++) {
                AccountEntity entity = listData.get(i);
                View tableRow = LayoutInflater.from(AccountManagerActivity.this).inflate(R.layout.table_account_row_child, null, false);
                TextView tvStt = tableRow.findViewById(R.id.tvSTT);
                TextView tvName = tableRow.findViewById(R.id.tvName);
                TextView tvAccount = tableRow.findViewById(R.id.tvAccount);
                TextView tvNgayHetHan = tableRow.findViewById(R.id.tvNgayHetHan);

                tvStt.setText(String.valueOf(i + 1));
                if (entity.getName() == null) {
                    tvName.setText("");
                } else {
                    tvName.setText(entity.getName());
                }
                tvAccount.setText(entity.getImei());

                Calendar current = Calendar.getInstance();
                current.set(Calendar.HOUR_OF_DAY, 0);
                current.set(Calendar.MINUTE, 0);
                current.set(Calendar.SECOND, 0);
                current.set(Calendar.MILLISECOND, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateTimeUtils.getDateFromString(entity.getDateExpired()).toDate());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (current.after(calendar)) {
                    tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(entity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                            + " (Hết hạn)");
                    tvNgayHetHan.setTextColor(getResources().getColor(R.color.red_light));
                } else {
                    long date = (calendar.getTimeInMillis() - current.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                    if (date == 0) {
                        tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(entity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                                + " (" + date + " ngày)");
                        tvNgayHetHan.setTextColor(getResources().getColor(R.color.red_light));
                    } else {
                        tvNgayHetHan.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(entity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                                + " (" + date + " ngày)");
                    }
                }


                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Common.disableView(view);
                        ActionManagerDialog actionManagerDialog = ActionManagerDialog.newInstance(entity, actionDialogListener);
                        actionManagerDialog.show(getSupportFragmentManager());
                    }
                });


                tbAccount.addView(tableRow);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateData(AccountEntity entity, Switch view) {
        try {
            progressDialog = new ProgressDialog(AccountManagerActivity.this);
            progressDialog.setMessage("Đang cập nhật...");
            progressDialog.show();

            MyApp.mFirebaseDatabase.child(Common.DBFB).child(entity.getImei()).child("Account").setValue(entity);
            MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    view.setChecked(entity.isLock());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                    Toast.makeText(AccountManagerActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteData(AccountEntity entity) {
        try {
            progressDialog = new ProgressDialog(AccountManagerActivity.this);
            progressDialog.setMessage("Đang xóa dữ liệu...");
            progressDialog.show();

            MyApp.mFirebaseDatabase.child(Common.DBFB).child(entity.getImei()).removeValue(new DatabaseReference.CompletionListener() {
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

//            MyApp.mFirebaseDatabase.child("Lotto").child(entity.getImei()).removeValue();
//
//            MyApp.mFirebaseDatabase.child("Lotto").removeEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    progressDialog.dismiss();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    progressDialog.dismiss();
//                    Toast.makeText(AccountManagerActivity.this, "Có lỗi xảy ra trong quá trình xóa dữ liệu", Toast.LENGTH_LONG).show();
//                }
//            });
//            MyApp.mFirebaseDatabase.child("Lotto").child(entity.getImei()).child("Account").setValue(entity);
//            MyApp.mFirebaseDatabase.child("Lotto").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    progressDialog.dismiss();
//                    view.setChecked(entity.isLock());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    progressDialog.dismiss();
//                    Toast.makeText(AccountManagerActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GiaHanDialog.DialogListener updateSuccess = () -> getDataFromServer();

    private ActionManagerDialog.DialogListener actionDialogListener = new ActionManagerDialog.DialogListener() {
        @Override
        public void onGiaHan(AccountEntity entity) {
            try {
                GiaHanDialog dialog = GiaHanDialog.newInstance(entity, updateSuccess);
                dialog.show(getSupportFragmentManager());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onKhoaSuccess() {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDataFromServer();
                    }
                }, 150);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onXoaSuccess() {
            getDataFromServer();
        }
    };
}
