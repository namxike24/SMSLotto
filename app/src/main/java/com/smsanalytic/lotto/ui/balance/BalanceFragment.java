package com.smsanalytic.lotto.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;

public class BalanceFragment extends BaseFragment {

    @BindView(R.id.btnGiuLaiLonNhat)
    LinearLayout btnGiuLaiLonNhat;
    @BindView(R.id.btnChuyenDiGiuaLaiTheoPhanTram)
    LinearLayout btnChuyenDiGiuaLaiTheoPhanTram;
    @BindView(R.id.btnCanChuyen100So)
    LinearLayout btnCanChuyen100So;
    @BindView(R.id.btnGiuLaiLonNhatTheoKhuc)
    LinearLayout btnGiuLaiLonNhatTheoKhuc;
    @BindView(R.id.btnGiuLaiPhanTramTheoKhuc)
    LinearLayout btnGiuLaiPhanTramTheoKhuc;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_balance, container, false);
            ButterKnife.bind(this, view);

        }
        return view;
    }


    @OnClick({R.id.btnGiuLaiLonNhat, R.id.btnChuyenDiGiuaLaiTheoPhanTram, R.id.btnCanChuyen100So, R.id.btnGiuLaiLonNhatTheoKhuc, R.id.btnGiuLaiPhanTramTheoKhuc})
    void clickEvent(View v) {
      switch (v.getId()){
          case R.id.btnGiuLaiLonNhat:
              Intent intent= new Intent(getContext(),GiuLonNhatActivity.class);
              startActivity(intent);
              break;
          case R.id.btnChuyenDiGiuaLaiTheoPhanTram:
              Intent intent1= new Intent(getContext(),CanChuyenTheoPhanTramActivity.class);
              startActivity(intent1);
              break;
          case R.id.btnCanChuyen100So:
              Intent intent4= new Intent(getContext(),CanChuyen100SoActivity.class);
              startActivity(intent4);
              break;
          case R.id.btnGiuLaiLonNhatTheoKhuc:
              Intent intent2= new Intent(getContext(),CanChuyenTheoKhucActivity.class);
              startActivity(intent2);
              break;
          case R.id.btnGiuLaiPhanTramTheoKhuc:
              Intent intent3= new Intent(getContext(),CanChuyenTheoKhucActivity.class);
              intent3.putExtra(CanChuyenTheoKhucActivity.CAN_CHUYEN_THEO_PHAN_TRAM,true);
              startActivity(intent3);
              break;

      }
    }
}