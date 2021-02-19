package com.smsanalytic.lotto.common;

import android.content.DialogInterface;

import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.smsanalytic.lotto.MainLoToActivity;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;

public class BaseFragment extends Fragment {
    private AlertDialog dialog;

    @Override
    public void onResume() {
        super.onResume();
        try {
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
            double date = 0;
            if (current.before(calendar)) {
                date = (calendar.getTimeInMillis() - current.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
            if (date <= 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Phần mềm hết hạn sử dụng");
                builder.setMessage("Bạn còn vui lòng gia hạn để tiếp tục sử dụng sản phẩm");
                builder.setNegativeButton("LIÊN HỆ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (getActivity() instanceof MainLoToActivity) {
                            ((MainLoToActivity) getActivity()).selectMenu(R.id.nav_register);
                        }

                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
                dialog = builder.create();
                dialog.show();
            } else if (date <= 3) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Gia hạn phần mềm");
                builder.setMessage("Bạn còn " + Math.round(date) + " ngày sử dụng. Liên hệ để gia hạn trực tiếp");
                builder.setNegativeButton("LIÊN HỆ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getActivity() instanceof MainLoToActivity) {
                            ((MainLoToActivity) getActivity()).selectMenu(R.id.nav_register);
                        }
                        dialogInterface.dismiss();

                    }
                });
                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
