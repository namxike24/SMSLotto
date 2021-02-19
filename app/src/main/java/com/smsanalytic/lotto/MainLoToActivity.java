package com.smsanalytic.lotto;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.ui.xsmb.XSMBUtils;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class MainLoToActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @BindView(R.id.lnDate)
    LinearLayout lnDate;
    @BindView(R.id.tvDate)
    TextView tvDate;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Common.isNotificationServiceEnabled(this)) {
            Common.buildNotificationServiceAlertDialog(this).show();
        }

        initPermission();
        setContentView(R.layout.activity_main_lo_to);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigation();
        Common.cancelJob(MainLoToActivity.this);
        Common.startJob(MainLoToActivity.this);
        Common.startJobTinNhanCongNo(MainLoToActivity.this);
        checkGetXSMB();
        Bundle params = new Bundle();
        params.putString("message", "This is a test message");


    }

    public void checkGetXSMB() {
        try {
            DateTime dt = new DateTime();
            dtVietNam = dt.withZone(DateTimeZone.forID("Asia/Ho_Chi_Minh"));
            if ((dtVietNam.getHourOfDay() >= 18 && dtVietNam.getMinuteOfDay() >= 10)) {
                new Handler().postDelayed(() -> new GetDataXSMB(MainLoToActivity.this, getDataListener, DateTimeUtils.convertDateToString(dtVietNam.toDate(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT)).start(), 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        // check quyền danh bạ
    }


    private NavController navController;

    /**
     * Hàm khởi tạo navigation
     */
    private void initNavigation() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_sms_process, R.id.nav_sms_management, R.id.nav_sms_social,
                R.id.nav_document, R.id.nav_account_list, R.id.nav_balance
                , R.id.nav_balance
                , R.id.nav_xsmb
                , R.id.nav_debt
                , R.id.nav_setting
                , R.id.nav_character
                , R.id.nav_sms_detail
                , R.id.nav_register
                , R.id.nav_async
                , R.id.nav_update
//                , R.id.nav_logout
        )
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_sms_management || destination.getId() == R.id.nav_sms_detail) {
                lnDate.setVisibility(View.VISIBLE);
            } else {
                lnDate.setVisibility(View.GONE);
            }
        });


    }

    public void selectMenu(int id) {
        try {
            navController.navigate(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        // Handle navigation view item clicks here.
//        switch (menuItem.getItemId()) {
//            case R.id.nav_update:
//                Toast.makeText(getBaseContext(), "Không có bản cập nhật mới nào", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_logout:
//                Toast.makeText(getBaseContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        //close navigation drawer
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
        }
    }

    public DateTime dtVietNam;


    public GetDataXSMB.Listener getDataListener = new GetDataXSMB.Listener() {
        @Override
        public void onSuccess(Map<String, ArrayList<String>> data, Map<String, ArrayList<String>> dataHit) {
            try {
                Log.e(MainLoToActivity.class.getSimpleName(), "Lay ket qua");
                if (data != null && !data.isEmpty()) {
                    PreferencesManager.getInstance().setValue(Common.LAST_DAY_GET_XSMB, DateTimeUtils.convertDateToString(dtVietNam.toDate(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dtVietNam.toDate());
                    XSMBUtils.TinhKetQuaXS(dataHit, calendar);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public LinearLayout getLnDate() {
        return lnDate;
    }

    public void setLnDate(LinearLayout lnDate) {
        this.lnDate = lnDate;
    }

    public TextView getTvDate() {
        return tvDate;
    }

    public void setTvDate(TextView tvDate) {
        this.tvDate = tvDate;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    private ProgressDialog progressDialog;





}
