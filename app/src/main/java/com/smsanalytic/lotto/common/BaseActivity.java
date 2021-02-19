package com.smsanalytic.lotto.common;

import androidx.appcompat.app.AppCompatActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.login.LoginDialog;

public class BaseActivity extends AppCompatActivity {

    private LoginDialog loginDialog;

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApp.isApprunning) {
            MyApp.isApprunning = true;
//            if (loginDialog != null) {
//                loginDialog.dismiss();
//            }
//            loginDialog = LoginDialog.newInstance(new LoginDialog.DialogListener() {
//                @Override
//                public void onLoginSuccess() {
//
//                }
//            }, false);
//            loginDialog.show(getSupportFragmentManager());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Common.isAppOpen()) {
            MyApp.isApprunning = false;
        }
    }
}
