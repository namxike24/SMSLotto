package com.smsanalytic.lotto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.cal.CalMainActivity;
import com.smsanalytic.lotto.cal.InfoActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.login.LoginDialog;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            ButterKnife.bind(this);

            getIMEI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    public void getIMEI() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                initPermission();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MyApp.imei = Settings.Secure.getString(
                            this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);

                } else {
                    final TelephonyManager ts = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                    MyApp.imei = ts.getDeviceId();

                }
                if (!TextUtils.isEmpty(MyApp.imei)) {
                    checkNeedRegister();
                } else {
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                1123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getIMEI();
                } else {
                    initPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void checkNeedRegister() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Account");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    MyApp.currentAccount = dataSnapshot.getValue(AccountEntity.class);
//                                    checkLicense();

                                        LoginDialog loginDialog = LoginDialog.newInstance(new LoginDialog.DialogListener() {
                                            @Override
                                            public void onLoginSuccess() {
                                                startActivity(new Intent(SplashActivity.this, MainLoToActivity.class));
                                                finish();
                                            }
                                        },true);
                                        loginDialog.show(getSupportFragmentManager());
                                } else {
//                                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
//                                    finish();
//                                    processAddAccount();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(SplashActivity.this, "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLicense() {
        try {
            Calendar current = Calendar.getInstance();
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateTimeUtils.getDateFromString(MyApp.currentAccount.getDateExpired()).toDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            double date = 0;
            if (current.before(calendar)) {
                date = (calendar.getTimeInMillis() - current.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            }
            if (date <= 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Phần mềm hết hạn sử dụng");
                builder.setMessage("Vui lòng gia hạn để tiếp tục sử dụng");
                builder.setNegativeButton("Thông tin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(SplashActivity.this, InfoActivity.class));
                        finish();

                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                 builder.create().show();
            }else {
                startActivity(new Intent(SplashActivity.this, CalMainActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;
    private void processAddAccount() {
        try {
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage("Đang tạo tài khoản...");
            progressDialog.show();
//            String id = MyApp.mFirebaseDatabase.child("Lotto").push().getKey();
            String id = MyApp.imei;
            AccountEntity account = new AccountEntity();
            account.setImei(MyApp.imei);
            account.setPass("");
            account.setPassDelete("");
            Calendar expiredTime = Calendar.getInstance();
            expiredTime.add(Calendar.DATE, 5);
            account.setDateExpired(DateTimeUtils.convertDateToString(expiredTime.getTime(), DateTimeUtils.SERVER_DATE_TIME_PATTERN));
            account.setLock(false);
            account.setName("");
            MyApp.mFirebaseDatabase.child(Common.DBFB).child(id).child("Account").setValue(account);
            MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    BillBoard data = dataSnapshot.getValue(BillBoard.class);
//
//                    // Check for null
//                    if (data != null) {
//                        progressHUD.showDoneStatus();
//                    }
                    progressDialog.dismiss();
                    startActivity(new Intent(SplashActivity.this, CalMainActivity.class));
                    finish();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                    Toast.makeText(SplashActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu 1", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
