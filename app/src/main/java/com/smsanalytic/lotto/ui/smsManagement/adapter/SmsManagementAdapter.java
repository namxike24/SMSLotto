package com.smsanalytic.lotto.ui.smsManagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.sms.SmsManagementObject;

public class SmsManagementAdapter extends RecyclerView.Adapter<SmsManagementAdapter.ViewHolder> {

    private IClickListener iClickListenerl;
    private List<SmsManagementObject> list;
    private Context context;

    public void setiClickListenerl(IClickListener iClickListenerl) {
        this.iClickListenerl = iClickListenerl;
    }

    public SmsManagementAdapter(List<SmsManagementObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_sms_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsManagementObject smsManagementObject = list.get(position);
        holder.tvUser.setText(smsManagementObject.getAccountName());
        holder.tvContentSms.setText(smsManagementObject.getDesciption());
        if (smsManagementObject.getWaitprocessNumber()>0){
            holder.tvCount.setText(String.valueOf(smsManagementObject.getWaitprocessNumber()));
            holder.tvCount.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvCount.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvUser)
        TextView tvUser;
        @BindView(R.id.tvContentSms)
        TextView tvContentSms;
        @BindView(R.id.lnContent)
        LinearLayout lnContent;
        @BindView(R.id.tvCount)
        TextView tvCount;
        @BindView(R.id.rlItem)
        RelativeLayout rlItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rlItem.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (iClickListenerl != null) {
                iClickListenerl.clickEvent(v, getAdapterPosition());
            }
        }
    }
}
