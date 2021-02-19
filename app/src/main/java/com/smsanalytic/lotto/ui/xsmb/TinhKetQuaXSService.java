package com.smsanalytic.lotto.ui.xsmb;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.util.Calendar;

import com.smsanalytic.lotto.GetDataXSMB;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class TinhKetQuaXSService extends JobService {
    public static final String TAG = TinhKetQuaXSService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "DebugLog onStartJob ");
        new GetDataXSMB(MyApp.getContext(), getDataListener, DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(), DateTimeUtils.DAY_MONTH_YEAR_FORMAT)).start();
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "DebugLog onStopJob ");
        return false;
    }

    private GetDataXSMB.Listener getDataListener = (data, dataHit) -> {
        try {
            if (dataHit != null || !dataHit.isEmpty()) {
                Log.e(TAG,"Success");
                PreferencesManager.getInstance().setValue(Common.LAST_DAY_GET_XSMB, DateTimeUtils.convertDateToString(Calendar.getInstance().getTime(),DateTimeUtils.DAY_MONTH_YEAR_FORMAT));
                XSMBUtils.TinhKetQuaXS(dataHit, Calendar.getInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
