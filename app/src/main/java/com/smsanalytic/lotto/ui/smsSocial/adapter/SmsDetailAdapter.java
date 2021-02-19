package com.smsanalytic.lotto.ui.smsSocial.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.smsSocial.SmsDetailActivity;

public class SmsDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_RECEIVE = 1;
    private static final int TYPE_SEND = 2;

    private List<SmsObject> smsObjectList;
    private Context context;
    private IClickListener iClickListener;
    private String startFromScreen;
    private boolean showCheckBox;


    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }



    public void setiClickListener(IClickListener iClickListener) {
        this.iClickListener = iClickListener;
    }

    public SmsDetailAdapter(List<SmsObject> smsObjectList, String startFromScreen, Context context) {
        this.smsObjectList = smsObjectList;
        this.context = context;
        this.startFromScreen = startFromScreen;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SEND:
                View viewSend = inflater.inflate(R.layout.view_sms_detail_send, parent, false);
                return new ViewHolderSend(viewSend);
            default:
                View viewdefault = inflater.inflate(R.layout.view_sms_detail, parent, false);
                return new ViewHolder(viewdefault);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SmsObject smsObject = smsObjectList.get(position);
        if (smsObject.getSmsStatus()==SmsStatus.SMS_RECEIVE){
                ViewHolder viewHolder= (ViewHolder) holder;
                viewHolder.bind(smsObject);
        }
        else {
            ViewHolderSend viewHolder= (ViewHolderSend) holder;
            viewHolder.bind(smsObject);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (smsObjectList.get(position).getSmsStatus() == SmsStatus.SMS_RECEIVE) {
            return TYPE_RECEIVE;
        } else {
            return TYPE_SEND;
        }

    }

    @Override
    public int getItemCount() {
        return smsObjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.layoutItem)
        RelativeLayout layoutItem;
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutItem.setOnClickListener(this);
            checkbox.setOnClickListener(this);
        }

        private void bind(SmsObject smsObject) {
            if (!smsObject.isSuccess()) {
                tvContent.setText(smsObject.getSmsRoot());
            } else {
                tvContent.setText(smsObject.getSmsProcessed());
            }
            tvTime.setText(Common.convertDateByFormat(smsObject.getDate(), Common.FORMAT_DATE_HH_MM));
            if (startFromScreen.equals(SmsDetailActivity.FROM_SMS_SOCIAL)) {
                tvContent.setTypeface(null, Typeface.NORMAL);
            } else {
                if (!smsObject.isSuccess()) {
                    tvContent.setTypeface(null, Typeface.BOLD);
                } else {
                    tvContent.setTypeface(null, Typeface.NORMAL);
                }
            }
            checkbox.setVisibility(showCheckBox? View.VISIBLE:View.GONE);
            checkbox.setChecked(smsObject.getCheckbox());
        }

        @Override
        public void onClick(View v) {
            if (iClickListener != null) {
                iClickListener.clickEvent(v, getAdapterPosition());
            }
        }
    }

    class ViewHolderSend extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.layoutItem)
        RelativeLayout layoutItem;
        @BindView(R.id.checkbox)
        CheckBox checkbox;

        public ViewHolderSend(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutItem.setOnClickListener(this);
            checkbox.setOnClickListener(this);
        }

        private void bind(SmsObject smsObject) {
            if (!smsObject.isSuccess()) {
                tvContent.setText(smsObject.getSmsRoot());
            } else {
                tvContent.setText(smsObject.getSmsProcessed());
            }
            tvTime.setText(Common.convertDateByFormat(smsObject.getDate(), Common.FORMAT_DATE_HH_MM));
            if (startFromScreen.equals(SmsDetailActivity.FROM_SMS_SOCIAL)) {
                tvContent.setTypeface(null, Typeface.NORMAL);
            } else {
                if (!smsObject.isSuccess()) {
                    tvContent.setTypeface(null, Typeface.BOLD);
                } else {
                    tvContent.setTypeface(null, Typeface.NORMAL);
                }
            }
            checkbox.setVisibility(showCheckBox? View.VISIBLE:View.GONE);
            checkbox.setChecked(smsObject.getCheckbox());
        }

        @Override
        public void onClick(View v) {
            if (iClickListener != null) {
                iClickListener.clickEvent(v, getAdapterPosition());
            }
        }
    }
}
