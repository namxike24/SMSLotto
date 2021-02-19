package com.smsanalytic.lotto.ui.balance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.interfaces.IClickListener;

public class AccountBossListAdapter extends RecyclerView.Adapter<AccountBossListAdapter.ViewHolder> {
    List<AccountObject> list;
    private IClickListener iClickListener;
    private int lastCheckedPosition = -1;
    private Context context;

    public AccountBossListAdapter(List<AccountObject> list, Context context) {
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
        View view = inflater.inflate(R.layout.view_account_boss_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountObject accountObject = list.get(position);
        holder.radio.setText(accountObject.getAccountName());
        holder.radio.setChecked(position == lastCheckedPosition);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.radio)
        RadioButton radio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            radio.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (iClickListener != null) {
                iClickListener.clickEvent(v,getAdapterPosition());
                lastCheckedPosition = getAdapterPosition();
                notifyDataSetChanged();
            }
        }
    }

}
