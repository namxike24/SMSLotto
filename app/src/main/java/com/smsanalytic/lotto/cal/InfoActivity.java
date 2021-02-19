package com.smsanalytic.lotto.cal;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class InfoActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvImei)
    TextView tvImei;
    @BindView(R.id.tvHanSuDung)
    TextView tvHanSuDung;
    @BindView(R.id.btnCopyImei)
    Button btnCopyImei;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        initListener();
        getDataCustomerFromServer();
    }


    private void getDataCustomerFromServer() {
        try {
            progressDialog = new ProgressDialog(InfoActivity.this);
            progressDialog.setMessage("Đang tải dữ liệu..");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Account");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        AccountEntity accountEntity = dataSnapshot.getValue(AccountEntity.class);
                                        tvImei.setText(accountEntity.getImei());
                                        tvHanSuDung.setText(DateTimeUtils.convertDateToString(DateTimeUtils.getDateFromString(accountEntity.getDateExpired()).toDate(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN));

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






    private void initListener() {
        try {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    finish();
                }
            });

            btnCopyImei.setOnClickListener(copyImeiListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener copyImeiListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                String data= tvImei.getText().toString().trim();
                Common.disableView(view);
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null,data);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(InfoActivity.this,"Đã sao chép",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
