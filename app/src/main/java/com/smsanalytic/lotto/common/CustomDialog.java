package com.smsanalytic.lotto.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.processSms.ProcessSms;

public class CustomDialog extends Dialog {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.edtContent)
    EditText edtContent;
    @BindView(R.id.btnCopy)
    Button btnCopy;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnSend)
    Button btnSend;


    private DaoSession daoSession;
    private SmsObject smsObject;
    private AccountObject accountObject;


    public CustomDialog(@NonNull Context context, SmsObject smsObject, AccountObject accountObject) {
        super(context);
        this.smsObject = smsObject;
        this.accountObject = accountObject;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_custom_dialog);
        daoSession= MyApp.getInstance().getDaoSession();
        ButterKnife.bind(this);
        if (smsObject != null) {
            edtContent.setText(smsObject.getSmsRoot());
            tvTitle.setText(getContext().getString(R.string.sms_to,accountObject.getAccountName()));
        }

    }
    @Override
    public void onStart() {
        super.onStart();

            this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @OnClick({R.id.btnCopy, R.id.btnCancel, R.id.btnSend})
    void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnCopy:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", edtContent.getText());
                clipboard.setPrimaryClip(clip);
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnSend:
                if (smsObject != null) {
                    SmsObject smsObject1 = ProcessSms.replySMS(getContext(), smsObject, smsObject.getSmsProcessed(), daoSession, true);
                    // nếu không gửi đc qua mạng xa hội mà account có số điện thoại thfi gửi qua sms
                    if (smsObject1 == null) {
                        if (!accountObject.getPhone().isEmpty()) {
                            String phone = accountObject.getPhone().split(",")[0];
                            Common.sendSmsAuto(smsObject.getSmsProcessed(), phone);
                            smsObject.setGuid(UUID.randomUUID().toString());
                            daoSession.getSmsObjectDao().insert(smsObject);
                        }
                    }
                }
                dismiss();
                break;
        }
    }

}
