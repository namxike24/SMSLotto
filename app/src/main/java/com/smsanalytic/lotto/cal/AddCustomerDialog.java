package com.smsanalytic.lotto.cal;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;

public class AddCustomerDialog extends BaseDialogFragment {
    @BindView(R.id.edName)
    EditText edName;
    @BindView(R.id.btnAdd)
    TextView btnAdd;

    private ProgressDialog progressDialog;
    private DialogListener listener;

    public static AddCustomerDialog newInstance(DialogListener listener) {
        AddCustomerDialog fragment = new AddCustomerDialog();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        try {
            ButterKnife.bind(this, rootView);
            initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {
            btnAdd.setOnClickListener(addListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isGiaHanSuccess;
    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                CustomerEntity customer = new CustomerEntity(UUID.randomUUID().toString(),edName.getText().toString().trim());
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Đang thêm khách hàng...");
                progressDialog.show();

                MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.imei).child("Customer").child(customer.getId()).setValue(customer);
                MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (!isGiaHanSuccess) {
                            isGiaHanSuccess = true;
                            listener.onAddSuccess();
                            dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        progressDialog.dismiss();
                        isGiaHanSuccess = false;
                        Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected int getDialogWidth() {
        return Common.getScreenWitch(getActivity()) - Common.convertDpToPx(50f, getActivity());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_them_khach_hang;
    }

    @Override
    public String getTAG() {
        return AddCustomerDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onAddSuccess();
    }
}
