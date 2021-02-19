package com.smsanalytic.lotto.ui.smsProcess.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.sms.LotoHitDetail;

public class SmsEmptyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_RECEIVE = 1;
    private static final int TYPE_SEND = 2;
    private IClickListener iClickListenerl;
    private List<SmsObject> smsObjectList;
    private Context context;
    public static final int SMS_DETAIL_TYPE = 1;// màn hình chi tiết tin nhắn
    public static final int SMS_EMPTY_TYPE = 2;// màn hinfht in năhsn
    private int type;

    public SmsEmptyAdapter(List<SmsObject> smsObjectList, Context context, int type) {
        this.smsObjectList = smsObjectList;
        this.context = context;
        this.type = type;
    }

    public void setiClickListenerl(IClickListener iClickListenerl) {
        this.iClickListenerl = iClickListenerl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_RECEIVE:
                View viewSend = inflater.inflate(R.layout.view_sms_empty, parent, false);
                return new ViewHolder(viewSend);
            default:
                View viewdefault = inflater.inflate(R.layout.view_sms_empty_2, parent, false);
                return new ViewHolder1(viewdefault);
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
            ViewHolder1 viewHolder= (ViewHolder1) holder;
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
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.lnItem)
        LinearLayout lnItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            lnItem.setOnClickListener(this);
        }

        private void bind(SmsObject smsObject) {
            tvContent.setText(smsObject.getSmsRoot());
            tvUserName.setText(smsObject.getGroupTitle());
            if (type == SMS_EMPTY_TYPE) {
                tvUserName.setText(smsObject.getGroupTitle());
            } else {
                LotoHitDetail lotoHitDetail = new Gson().fromJson(smsObject.getLotoHitDetail(), LotoHitDetail.class);
                if (smsObject.getSmsType() == SmsType.SMS_EMPTY) {
                    tvUserName.setText("Tin Trống");
                }
                if (smsObject.isSuccess()) {
                    if (lotoHitDetail != null) {
                        tvUserName.setText(Html.fromHtml(lotoHitDetail.getTitle().trim()));
                        tvContent.setText(smsObject.getSmsProcessed());
                    }
                }
            }


            tvTime.setText(Common.convertDateByFormat(smsObject.getDate(), Common.FORMAT_DATE_HH_MM));
            if (smsObject.getSmsType() != SmsType.SMS_EMPTY) {
                if (!smsObject.isSuccess()) {
                    tvContent.setTypeface(null, Typeface.BOLD);
                } else {
                    tvContent.setTypeface(null, Typeface.NORMAL);
                    tvContent.setText(smsObject.getSmsProcessed());
                }
            } else {
                tvContent.setTypeface(null, Typeface.NORMAL);
            }
        }

        @Override
        public void onClick(View v) {
            if (iClickListenerl != null) {
                iClickListenerl.clickEvent(v, getAdapterPosition());
            }
        }
    }

    class ViewHolder1 extends  RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.lnItem)
        LinearLayout lnItem;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            lnItem.setOnClickListener(this);
        }

        private void bind(SmsObject smsObject) {
            tvContent.setText(smsObject.getSmsRoot());
            tvUserName.setText(smsObject.getGroupTitle());
            if (type == SMS_EMPTY_TYPE) {
                tvUserName.setText(smsObject.getGroupTitle());
            } else {
                LotoHitDetail lotoHitDetail = new Gson().fromJson(smsObject.getLotoHitDetail(), LotoHitDetail.class);
                if (smsObject.getSmsType() == SmsType.SMS_EMPTY) {
                    tvUserName.setText("Tin Trống");
                }
                if (smsObject.isSuccess()) {
                    if (lotoHitDetail != null) {
                        tvUserName.setText(Html.fromHtml(lotoHitDetail.getTitle().trim()));
                        tvContent.setText(smsObject.getSmsProcessed());
                    }
                }
            }


            tvTime.setText(Common.convertDateByFormat(smsObject.getDate(),Common.FORMAT_DATE_HH_MM));
            if (smsObject.getSmsType() != SmsType.SMS_EMPTY) {
                if (!smsObject.isSuccess()) {
                    tvContent.setTypeface(null, Typeface.BOLD);
                } else {
                    tvContent.setTypeface(null, Typeface.NORMAL);
                    tvContent.setText(smsObject.getSmsProcessed());
                }
            } else {
                tvContent.setTypeface(null, Typeface.NORMAL);
            }

        }

        @Override
        public void onClick(View v) {
            if (iClickListenerl != null) {
                iClickListenerl.clickEvent(v, getAdapterPosition());
            }
        }
    }

}

