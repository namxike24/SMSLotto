package com.smsanalytic.lotto;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.entities.LienHeEntity;

public class LienHeDialog extends BaseDialogFragment {
    @BindView(R.id.edGiaHan)
    EditText edGiaHan;
    @BindView(R.id.edKyThuat)
    EditText edKyThuat;
    @BindView(R.id.btnUpdate)
    TextView btnUpdate;

    private String giahan;
    private String kythuat;
    private DialogListener listener;

    public static LienHeDialog newInstance(String giahan, String kythuat, DialogListener listener) {
        LienHeDialog fragment = new LienHeDialog();
        fragment.giahan = giahan;
        fragment.kythuat = kythuat;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        try {
            ButterKnife.bind(this, rootView);
            displayData();
            initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {

            btnUpdate.setOnClickListener(updateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                giahan = edGiaHan.getText().toString().trim();
                kythuat = edKyThuat.getText().toString().trim();
                if (TextUtils.isEmpty(giahan) || TextUtils.isEmpty(kythuat)) {
                    Toast.makeText(getActivity(), "Dữ liệu không được để trống", Toast.LENGTH_LONG).show();
                } else {
                    LienHeEntity entity  = new LienHeEntity(giahan,kythuat);
                    MyApp.mFirebaseDatabase.child(Common.DBFB).child("lien_he").setValue(entity);
                    MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listener.onUpdateSuccess(giahan,kythuat);
                            dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void displayData() {
        try {
            edGiaHan.setText(giahan);
            edKyThuat.setText(kythuat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getDialogWidth() {
        return Common.getScreenWitch(getActivity()) - Common.convertDpToPx(50f, getActivity());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_cap_nhat_lien_he;
    }

    @Override
    public String getTAG() {
        return LienHeDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onUpdateSuccess(String giahan,String kythuat);
    }
}
