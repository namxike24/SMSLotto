package com.smsanalytic.lotto;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.ui.register.AccountEntity;

public class ActionManagerDialog extends BaseDialogFragment {
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.tvGiaHan)
    TextView tvGiaHan;
    @BindView(R.id.tvKhoa)
    TextView tvKhoa;
    @BindView(R.id.tvXoa)
    TextView tvXoa;


    private AccountEntity accountEntity;
    private ProgressDialog progressDialog;
    private DialogListener listener;

    public static ActionManagerDialog newInstance(AccountEntity accountEntity, DialogListener listener) {
        ActionManagerDialog fragment = new ActionManagerDialog();
        fragment.accountEntity = accountEntity;
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
            tvGiaHan.setOnClickListener(giaHanListener);
            tvKhoa.setOnClickListener(khoaListener);
            tvXoa.setOnClickListener(xoaListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener giaHanListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                listener.onGiaHan(accountEntity);
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener khoaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                String message = accountEntity.isLock() ? "Bạn có chắc chắn muốn mở khóa tài khoản này?" : "Bạn có chắc chắn muốn khóa tài khoản này?";
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Thông báo");
                builder.setMessage(message);
                builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        accountEntity.setLock(!accountEntity.isLock());
                        updateData(accountEntity);
                    }
                });
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Boolean isSuccessKhoa = false;

    private void updateData(AccountEntity entity) {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang cập nhật...");
            progressDialog.show();
            MyApp.mFirebaseDatabase.child(Common.DBFB).child(entity.getImei()).child("Account").setValue(entity);
            MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if (!isSuccessKhoa) {
                        isSuccessKhoa = true;
                        listener.onKhoaSuccess();
                        dismiss();
                    }
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener xoaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                String message = String.format("Bạn có chắc chắn muốn xóa tài khoản này không?");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Thông báo");
                builder.setMessage(message);
                builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteData(accountEntity);
                    }
                });
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean isXoaSuccess;

    private void deleteData(AccountEntity entity) {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang xóa dữ liệu...");
            progressDialog.show();

            MyApp.mFirebaseDatabase.child(Common.DBFB).child(entity.getImei()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    progressDialog.dismiss();
                    if (!isXoaSuccess) {
                        isXoaSuccess = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listener.onXoaSuccess();
                                dismiss();
                            }
                        }, 150);
                    }

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayData() {
        try {
            if (!TextUtils.isEmpty(accountEntity.getName())) {
                tvAccount.setText(String.format("%s - %s", accountEntity.getImei(), accountEntity.getName()));
            } else {
                tvAccount.setText(accountEntity.getImei());
            }
            if (accountEntity.isLock()) {
                tvKhoa.setText("Mở khóa tài khoản");
            } else {
                tvKhoa.setText("Khóa tài khoản");
            }
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
        return R.layout.dialog_action_manager;
    }

    @Override
    public String getTAG() {
        return ActionManagerDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onGiaHan(AccountEntity accountEntity);

        void onKhoaSuccess();

        void onXoaSuccess();
    }
}
