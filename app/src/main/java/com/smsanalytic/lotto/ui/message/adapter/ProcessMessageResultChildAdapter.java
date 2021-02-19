package com.smsanalytic.lotto.ui.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;

public class ProcessMessageResultChildAdapter extends RecyclerView.Adapter<ProcessMessageResultChildAdapter.ViewHolder> {

    private ItemListener listener;
    private List<String> list;
    private Context context;

    public ProcessMessageResultChildAdapter(Context context, List<String> list, ItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_result_process_message_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String entity = list.get(position);
            holder.tvValue.setText(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvValue)
        TextView tvValue;
        @BindView(R.id.btnCopy)
        Button btnCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
                tvValue.setOnClickListener(view -> {
                    listener.clickItem();
                });
                btnCopy.setOnClickListener(view -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", tvValue.getText());
                    clipboard.setPrimaryClip(clip);

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public interface ItemListener {
        void clickItem();
    }
}
