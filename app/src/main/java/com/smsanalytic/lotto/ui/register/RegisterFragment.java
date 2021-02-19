package com.smsanalytic.lotto.ui.register;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private View view;
    @BindView(R.id.tvImei)
    TextView tvImei;
    @BindView(R.id.tvHanSuDung)
    TextView tvHanSuDung;
    @BindView(R.id.btnCopyImei)
    Button btnCopyImei;
    @BindView(R.id.btnCapNhatHanSuDung)
    Button btnCapNhatHanSuDung;

    @BindView(R.id.tvGiaHan)
    TextView tvGiaHan;
    @BindView(R.id.tvKyThuat)
    TextView tvKyThuat;

    private ProgressDialog progressDialog;
    private String sdt="";

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_register, container, false);
            ButterKnife.bind(this, view);
            displayData();
            initListener();
            final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child("lien_he");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        tvGiaHan.setText(dataSnapshot.child("gia_han").getValue().toString());
                        tvGiaHan.setTag(dataSnapshot.child("gia_han").getValue().toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        return view;
    }

    private void initListener() {
        try {
            btnCopyImei.setOnClickListener(copyImeiListener);
            btnCapNhatHanSuDung.setOnClickListener(capNhatHSDListener);
          //  btnSendSms.setOnClickListener(smsGiaHanListener);
        //    tvGiaHan.setOnClickListener(giaHanListener);
            tvKyThuat.setOnClickListener(kyThuatListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener copyImeiListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                String data= getString(R.string.format_copy_imei,tvGiaHan.getText().toString().trim(),tvImei.getText().toString().trim());
                Common.disableView(view);
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null,data);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(),"Đã sao chép",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener capNhatHSDListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Đang cập nhật lại thông tin sử dụng...");
                progressDialog.show();
                final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(tvImei.getText().toString().trim()).child("Account");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.getValue() != null) {
                            MyApp.currentAccount = dataSnapshot.getValue(AccountEntity.class);
                            displayData();
                            Toast.makeText(getActivity(), "Trạng thái: Đang hoạt động", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

//    private View.OnClickListener smsGiaHanListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            try {
//                Common.disableView(view);
//                String phone = edSDT.getText().toString().trim();
//                if (TextUtils.isEmpty(phone)) {
//                    Toast.makeText(getActivity(), "Vui lòng nhập số điện thoại", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Gửi tin nhắn gia hạn");
//                builder.setMessage("Tin nhắn gia hạn sẽ được gửi đến số điện thoại");
//                builder.setNegativeButton("GỬI", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        Common.sendSmsAuto("Gia hạn " + tvImei.getText().toString().trim(), phone);
//                        Toast.makeText(getActivity(), "Gửi tin nhắn gia hạn thành công", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setPositiveButton("HỦY", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.create().show();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    private View.OnClickListener giaHanListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
//                Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                callIntent.setData(Uri.parse("tel:0985616843"));
//                startActivity(callIntent);
                if (isPackageInstalled("com.zing.zalo", getActivity().getPackageManager())) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://zalo.me/" + tvGiaHan.getText().toString().replaceAll("Zalo: ", ""))));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.zing.zalo")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener kyThuatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
//                Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                callIntent.setData(Uri.parse("tel:0985616843"));
//                startActivity(callIntent);
                if (isPackageInstalled("com.zing.zalo", getActivity().getPackageManager())) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://zalo.me/" + tvGiaHan.getText().toString().replaceAll("Zalo: ", ""))));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.zing.zalo")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }

        return found;
    }

    private void displayData() {
        try {
            tvImei.setText(MyApp.currentAccount.getImei());
            Calendar current = Calendar.getInstance();
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateTimeUtils.getDateFromString(MyApp.currentAccount.getDateExpired()).toDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (current.after(calendar)) {
                tvHanSuDung.setText(DateTimeUtils.convertDateToString(calendar.getTime(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                        + " (Hết hạn)");
                tvHanSuDung.setTextColor(getResources().getColor(R.color.red_light));
            } else {
                long date = (calendar.getTimeInMillis() - current.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                if (date == 0) {
                    tvHanSuDung.setText(DateTimeUtils.convertDateToString(calendar.getTime(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                            + " (" + date + " ngày)");
                    tvHanSuDung.setTextColor(getResources().getColor(R.color.red_light));
                } else {
                    tvHanSuDung.setText(DateTimeUtils.convertDateToString(calendar.getTime(), DateTimeUtils.DAY_MONTH_YEAR_PATTERN)
                            + " (" + date + " ngày)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
