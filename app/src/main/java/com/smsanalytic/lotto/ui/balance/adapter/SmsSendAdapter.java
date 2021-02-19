package com.smsanalytic.lotto.ui.balance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.SmsSend;
import com.smsanalytic.lotto.interfaces.IClickListener;

public class SmsSendAdapter extends RecyclerView.Adapter<SmsSendAdapter.ViewHolder> {
    List<SmsSend> list;

    private IClickListener iClickListener;
    private Context context;

    public SmsSendAdapter(List<SmsSend> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setiClickListener(IClickListener iClickListener) {
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_sms_send, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.etSms.setText(list.get(position).getSms());
        if (list.get(position).isSend()){
            holder.btnSend.setText(context.getString(R.string.sent));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.btnSend)
        Button btnSend;
        @BindView(R.id.etSms)
        EditText etSms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnSend.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (iClickListener != null) {
                if (getAdapterPosition()>=0){
                    if (!list.get(getAdapterPosition()).isSend()){
                        iClickListener.clickEvent(v, getAdapterPosition());
                    }
                }
            }
        }
    }

}

