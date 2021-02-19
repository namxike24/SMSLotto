package com.smsanalytic.lotto.ui.accountList.adapter;

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
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.interfaces.IClickListener;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {

    private List<AccountObject> list;
    private Context context;
    private IClickListener iClickListener;

    public AccountListAdapter(List<AccountObject> list, Context context) {
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
        View view = inflater.inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountObject accountObject = list.get(position);
        holder.tvNo.setText(String.valueOf(position + 1));
        holder.tvUser.setText(accountObject.getAccountName());
        holder.tvUser.setText(accountObject.getAccountName());
        if (!accountObject.getPhone().isEmpty()) {
            holder.tvContentSms.setText(context.getString(R.string.tv_phone_number, accountObject.getPhone()));
        } else {
            holder.tvContentSms.setText(context.getString(R.string.tv_phone_number, "Đang cập nhật"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvNo)
        TextView tvNo;
        @BindView(R.id.tvUser)
        TextView tvUser;
        @BindView(R.id.tvContentSms)
        TextView tvContentSms;
        @BindView(R.id.lnContent)
        LinearLayout lnContent;
        @BindView(R.id.rlItem)
        RelativeLayout rlItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rlItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (iClickListener!=null){
                iClickListener.clickEvent(v,getAdapterPosition());
            }
        }
    }
}
