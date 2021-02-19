package com.smsanalytic.lotto.ui.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;

public class SettingPasswordActivity extends AppCompatActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.edPass)
    EditText edPass;
    @BindView(R.id.edConfirmPass)
    EditText edConfirmPass;
    @BindView(R.id.edPassDelete)
    EditText edPassDelete;
    @BindView(R.id.edConfirmPassDelete)
    EditText edConfirmPassDelete;
    @BindView(R.id.btnUpdate)
    TextView btnUpdate;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initListener() {
        try {
            btnBack.setOnClickListener(backListener);
            btnUpdate.setOnClickListener(updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                String pass = edPass.getText().toString().trim();
                String confirmPass = edConfirmPass.getText().toString().trim();
                String passDelete = edPassDelete.getText().toString().trim();
                String confirmPassDelete = edConfirmPassDelete.getText().toString().trim();
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(SettingPasswordActivity.this, "Bạn cần phải nhập mật khẩu đăng nhập", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPass)) {
                    Toast.makeText(SettingPasswordActivity.this, "Mật khẩu đăng nhập buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!pass.equalsIgnoreCase(confirmPass)) {
                    Toast.makeText(SettingPasswordActivity.this, "Mật khẩu đăng nhập buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(passDelete)) {
                    Toast.makeText(SettingPasswordActivity.this, "Bạn cần phải nhập mật khẩu xóa dữ liệu", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassDelete)) {
                    Toast.makeText(SettingPasswordActivity.this, "Mật khẩu xóa dữ liệu buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!passDelete.equalsIgnoreCase(confirmPassDelete)) {
                    Toast.makeText(SettingPasswordActivity.this, "Mật khẩu xóa dữ liệu buộc phải giống nhau", Toast.LENGTH_LONG).show();
                    return;
                }

                processUpdateAccount(pass, passDelete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void processUpdateAccount(String pass, String passDelete) {
        try {
            progressDialog = new ProgressDialog(SettingPasswordActivity.this);
            progressDialog.setMessage("Đang cập nhật...");
            progressDialog.show();
//            String id = MyApp.mFirebaseDatabase.child("Lotto").push().getKey();
            MyApp.currentAccount.setPass(pass);
            MyApp.currentAccount.setPassDelete(passDelete);
            MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.currentAccount.getImei()).child("Account").setValue(MyApp.currentAccount);
            MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingPasswordActivity.this, "Thành công", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                    Toast.makeText(SettingPasswordActivity.this, "Có lỗi xảy ra trong quá trình ghi dữ liệu 1", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            edPass.setText(MyApp.currentAccount.getPass());
            edConfirmPass.setText(MyApp.currentAccount.getPass());
            edPassDelete.setText(MyApp.currentAccount.getPassDelete());
            edConfirmPassDelete.setText(MyApp.currentAccount.getPassDelete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
