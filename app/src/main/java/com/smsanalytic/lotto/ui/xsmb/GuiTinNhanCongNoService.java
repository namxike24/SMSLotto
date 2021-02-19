package com.smsanalytic.lotto.ui.xsmb;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.gson.Gson;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.ui.debt.DebtDetailFragment.getDebtAllAccount;

public class GuiTinNhanCongNoService extends JobService {
    public static final String TAG = GuiTinNhanCongNoService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "DebugLog onStartJob ");
        SettingDefault settingDefault;
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        getDebtAllAccount(MyApp.getInstance().getDaoSession(), getApplicationContext(),settingDefault!=null?settingDefault.getTiente(): TienTe.TIEN_VIETNAM);

        Common.cancelJob(getApplicationContext());
        Common.startJob(getApplicationContext());
        Common.startJobTinNhanCongNo(getApplicationContext());
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "DebugLog onStopJob ");
        return false;
    }

}
