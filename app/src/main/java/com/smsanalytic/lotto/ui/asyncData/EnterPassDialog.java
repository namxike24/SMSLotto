package com.smsanalytic.lotto.ui.asyncData;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;

public class EnterPassDialog extends BaseDialogFragment {
    @BindView(R.id.edPass)
    EditText edPass;
    @BindView(R.id.tvOK)
    TextView tvOK;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    private DialogListener listener;

    public static EnterPassDialog newInstance(DialogListener listener) {
        EnterPassDialog fragment = new EnterPassDialog();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        try {
            ButterKnife.bind(this, rootView);
            tvOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Common.disableView(view);
                        String pass = edPass.getText().toString();
                        if (MyApp.currentAccount.getPassDelete().equals(pass)) {
                            dismiss();
                            listener.onEnterPassSuccess();
                        } else {
                            Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Common.disableView(view);
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
    }

    @Override
    protected int getDialogWidth() {
        return Common.getScreenWitch(getActivity())-Common.convertDpToPx(50f,getActivity());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_enter_pass;
    }

    @Override
    public String getTAG() {
        return EnterPassDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onEnterPassSuccess();
    }
}
