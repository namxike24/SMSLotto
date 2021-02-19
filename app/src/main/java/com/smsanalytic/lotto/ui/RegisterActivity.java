package com.smsanalytic.lotto.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.lnStep1)
    LinearLayout lnStep1;
    @BindView(R.id.tvImei)
    TextView tvImei;
    @BindView(R.id.tvQuydinh)
    TextView tvQuydinh;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.chkAgree)
    CheckBox chkAgree;

    @BindView(R.id.lnStep2)
    LinearLayout lnStep2;
    @BindView(R.id.edPass)
    EditText edPass;
    @BindView(R.id.edConfirmPass)
    EditText edConfirmPass;
    @BindView(R.id.edPassDelete)
    EditText edPassDelete;
    @BindView(R.id.edConfirmPassDelete)
    EditText edConfirmPassDelete;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.ivShowPass)
    ImageView ivShowPass;
    @BindView(R.id.ivShowPassConfirm)
    ImageView ivShowPassConfirm;

    @BindView(R.id.ivShowPassDelete)
    ImageView ivShowPassDelete;
    @BindView(R.id.ivShowPassDeleteConfirm)
    ImageView ivShowPassDeleteConfirm;

    private ProgressDialog progressDialog;
    private boolean isShowPass;
    private boolean isShowPassConfirm;
    private boolean isShowPassDelete;
    private boolean isShowPassDeleteConfirm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getIMEI();
        tvQuydinh.setText(Html.fromHtml(getString(R.string.sms_quydinh)));
        tvQuydinh.setMovementMethod(new ScrollingMovementMethod());
        initListener();
    }

    private void initListener() {
        try {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chkAgree.isChecked()) {
                        lnStep1.setVisibility(View.GONE);
                        lnStep2.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Bạn chưa đồng ý điều khoản sử dụng ứng dụng", Toast.LENGTH_LONG).show();

                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btnConfirm.setOnClickListener(confirmListener);

            ivShowPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    isShowPass = !isShowPass;
                    if (isShowPass) {
                        ivShowPass.setImageResource(R.drawable.ic_visible);
                        edPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        ivShowPass.setImageResource(R.drawable.ic_invisible);
                        edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edPass.setSelection(edPass.length());
                }
            });
            ivShowPassConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    isShowPassConfirm = !isShowPassConfirm;
                    if (isShowPassConfirm) {
                        ivShowPassConfirm.setImageResource(R.drawable.ic_visible);
                        edConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        ivShowPassConfirm.setImageResource(R.drawable.ic_invisible);
                        edConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edConfirmPass.setSelection(edConfirmPass.length());
                }
            });
            ivShowPassDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    isShowPassDelete = !isShowPassDelete;
                    if (isShowPassDelete) {
                        ivShowPassDelete.setImageResource(R.drawable.ic_visible);
                        edPassDelete.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        ivShowPassDelete.setImageResource(R.drawable.ic_invisible);
                        edPassDelete.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edPassDelete.setSelection(edPassDelete.length());
                }
            });
            ivShowPassDeleteConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    isShowPassDeleteConfirm = !isShowPassDeleteConfirm;
                    if (isShowPassDeleteConfirm) {
                        ivShowPassDeleteConfirm.setImageResource(R.drawable.ic_visible);
                        edConfirmPassDelete.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        ivShowPassDeleteConfirm.setImageResource(R.drawable.ic_invisible);
                        edConfirmPassDelete.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edConfirmPassDelete.setSelection(edConfirmPassDelete.length());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener confirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                String pass = edPass.getText().toString().trim();
                String confirmPass = edConfirmPass.getText().toString().trim();
                String passDelete = edPassDelete.getText().toString().trim();
                String confirmPassDelete = edConfirmPassDelete.getText().toString().trim();
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(RegisterActivity.this, "Bạn cần phải nhập mật khẩu đăng nhập", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu đăng nhập buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!pass.equalsIgnoreCase(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu đăng nhập buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(passDelete)) {
                    Toast.makeText(RegisterActivity.this, "Bạn cần phải nhập mật khẩu xóa dữ liệu", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassDelete)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu xóa dữ liệu buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!passDelete.equalsIgnoreCase(confirmPassDelete)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu xóa dữ liệu buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }

                processAddAccount(pass, passDelete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void processAddAccount(String pass, String passDelete) {
        try {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Đang khởi tạo...");
            progressDialog.show();
//            String id = MyApp.mFirebaseDatabase.child("Lotto").push().getKey();
            String id = tvImei.getText().toString().trim();
            AccountEntity account = new AccountEntity();
            account.setImei(tvImei.getText().toString().trim());
            account.setPass(pass);
            account.setPassDelete(passDelete);
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
                    startActivity(new Intent(RegisterActivity.this, MainLoToActivity.class));
                    finish();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu 1", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getIMEI() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            initPermission();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tvImei.setText(Settings.Secure.getString(
                        this.getContentResolver(),
                        Settings.Secure.ANDROID_ID));

            } else {
                final TelephonyManager ts = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                tvImei.setText(ts.getDeviceId());

            }

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
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
