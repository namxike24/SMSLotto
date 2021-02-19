package com.smsanalytic.lotto.cal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;

public class CalMainActivity extends BaseActivity {
    @BindView(R.id.tvCustomer)
    TextView tvCustomer;
    @BindView(R.id.tvAddData)
    TextView tvAddData;
    @BindView(R.id.tvReport)
    TextView tvReport;
    @BindView(R.id.tvInfo)
    TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal_main);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        try {
            tvCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    startActivity(new Intent(CalMainActivity.this,CustomerActivity.class));
                }
            });

            tvAddData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    startActivity(new Intent(CalMainActivity.this,AddDataActivity.class));
                }
            });

            tvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    startActivity(new Intent(CalMainActivity.this,ReportActivity.class));
                }
            });

            tvInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    startActivity(new Intent(CalMainActivity.this,InfoActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
