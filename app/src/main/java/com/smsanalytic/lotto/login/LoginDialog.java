package com.smsanalytic.lotto.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.AccountManagerActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseDialogFragment;
import com.smsanalytic.lotto.common.Common;

public class LoginDialog extends BaseDialogFragment {
    @BindView(R.id.edUser)
    EditText edUser;
    @BindView(R.id.edPass)
    EditText edPass;
    @BindView(R.id.ivShowPass)
    ImageView ivShowPass;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.ivManager)
    ImageView ivManager;
    private DialogListener listener;
    private boolean hasManager;
    private boolean isModeManager;
    private boolean isShowPass;

    public static LoginDialog newInstance(DialogListener listener, boolean hasManager) {
        LoginDialog fragment = new LoginDialog();
        fragment.listener = listener;
        fragment.hasManager = hasManager;
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        try {
            ButterKnife.bind(this, rootView);
            if (hasManager) {
                ivManager.setVisibility(View.VISIBLE);
                MyApp.isApprunning = true;
            } else {
                ivManager.setVisibility(View.GONE);
            }
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Common.disableView(view);
                        Common.hideKeyBoard(getActivity());
                        if (isModeManager) {
                            String user = edUser.getText().toString().trim();
                            String pass = edPass.getText().toString();

                            final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child("manager");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        if (dataSnapshot.child("user").getValue().toString().equalsIgnoreCase(user)
                                                && dataSnapshot.child("pass").getValue().toString().equalsIgnoreCase(pass)) {
                                            startActivity(new Intent(getActivity(), AccountManagerActivity.class));
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getActivity(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Đăng nhập quản lý không thành công", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            String pass = edPass.getText().toString();
//                            if (pass.equals("123456")) {
//                                dismiss();
//                                listener.onLoginSuccess();
//                            } else {
//                                Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_LONG).show();
//                            }
                            Log.e("Object",new Gson().toJson(MyApp.currentAccount));
                            if (MyApp.currentAccount.isLock()) {
                                showDialogAccountLocked();
                            } else {
                                if (MyApp.currentAccount.getPass().equals(pass)) {
                                    dismiss();
                                    listener.onLoginSuccess();
                                } else {
                                    Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ivManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Common.disableView(view);
                        isModeManager = !isModeManager;
                        if (isModeManager) {
                            edUser.setVisibility(View.VISIBLE);
                        } else {
                            edUser.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ivShowPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.disableView(view);
                    isShowPass = !isShowPass;
                    if (isShowPass) {
                        ivShowPass.setImageResource(R.drawable.ic_visible);
                        edPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        ivShowPass.setImageResource(R.drawable.ic_invisible);
                        edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    edPass.setSelection(edPass.length());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialogAccountLocked() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Thông báo");
            builder.setMessage("Tài khoản đã bị khóa");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    getActivity().finish();
                }
            });
            builder.create().show();
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
        return Common.getScreenWitch(getActivity());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_login;
    }

    @Override
    public String getTAG() {
        return LoginDialog.class.getSimpleName();
    }

    @Override
    public void onClick(View view) {

    }

    public interface DialogListener {
        void onLoginSuccess();
    }
}
