package com.smsanalytic.lotto.ui.updateversion;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.BuildConfig;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateVersionFragment extends Fragment {

    @BindView(R.id.tvNoti)
    TextView tvNoti;
    private View view;
    @BindView(R.id.tvKyThuat)
    TextView tvKyThuat;

    public UpdateVersionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_update_version, container, false);
            ButterKnife.bind(this, view);
            tvNoti.setVisibility(View.GONE);
        }
        tvKyThuat.setOnClickListener(v -> {
            checkUpdateVersion();
        });
        return view;
    }

    private ProgressDialog progressDialog;

    private void checkUpdateVersion() {
        try {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang check phiên bản cập nhật...");
            progressDialog.show();
            final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child("check_update");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    try {
                        if (dataSnapshot.child("version").getValue(Integer.class) != BuildConfig.VERSION_CODE) {
                            tvNoti.setVisibility(View.GONE);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.child("link").getValue(String.class)));
                            startActivity(browserIntent);
                        } else {
                            tvNoti.setVisibility(View.VISIBLE);
                            tvNoti.setText("Không có phiên bản mới cần cập nhật");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        tvNoti.setVisibility(View.VISIBLE);
                        tvNoti.setText("Không có phiên bản mới cần cập nhật");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    tvNoti.setVisibility(View.VISIBLE);
                    tvNoti.setText("Không có phiên bản mới cần cập nhật");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
