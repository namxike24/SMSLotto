package com.smsanalytic.lotto.ui.smsSocial.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.interfaces.IClickListener;

public class SmsSocialAdapter extends RecyclerView.Adapter<SmsSocialAdapter.ViewHolder> {
    private List<SmsObject> smsObjectList;
    private Context context;

    private IClickListener iClickListener;

    public void setiClickListener(IClickListener iClickListener) {
        this.iClickListener = iClickListener;
    }

    public SmsSocialAdapter(List<SmsObject> smsObjectList, Context context) {
        this.smsObjectList = smsObjectList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_sms_social, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsObject smsObject = smsObjectList.get(position);
        holder.bind(smsObject);

    }

    @Override
    public int getItemCount() {
        return smsObjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivTypeSms)
        ImageView ivTypeSms;
        @BindView(R.id.tvUser)
        TextView tvUser;
        @BindView(R.id.tvContentSms)
        TextView tvContentSms;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.ivMenu)
        ImageView ivMenu;
        @BindView(R.id.rlItem)
        RelativeLayout rlItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rlItem.setOnClickListener(this);
            ivMenu.setOnClickListener(this);
        }


        public void bind(SmsObject smsObject) {
            tvUser.setText(smsObject.getGroupTitle());
            tvContentSms.setText(smsObject.getSmsRoot());
            tvTime.setVisibility(View.VISIBLE);
            if (smsObject.getSmsType() == SmsType.SMS_FB) {
                ivTypeSms.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_facebook));
            } else if (smsObject.getSmsType() == SmsType.SMS_ZALO) {
                ivTypeSms.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_zalo));
            } else if (smsObject.getSmsType() == SmsType.SMS_NORMAL) {
                tvTime.setVisibility(View.GONE);
                ivTypeSms.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sms_normal));
            } else {
                ivTypeSms.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sms));
                tvTime.setVisibility(View.GONE);
            }
            tvTime.setText(DateUtils.getRelativeTimeSpanString(smsObject.getDate(), Calendar.getInstance().getTime().getTime(), DateUtils.MINUTE_IN_MILLIS));
        }


        @Override
        public void onClick(View v) {
            if (iClickListener != null) {
                iClickListener.clickEvent(v, getAdapterPosition());
            }
        }
    }
}
