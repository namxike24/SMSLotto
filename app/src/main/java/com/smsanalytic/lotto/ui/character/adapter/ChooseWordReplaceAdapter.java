package com.smsanalytic.lotto.ui.character.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.model.WordReplaceEntity;

public class ChooseWordReplaceAdapter extends RecyclerView.Adapter<ChooseWordReplaceAdapter.ViewHolder> {

    private ItemListener listener;
    private List<WordReplaceEntity> list;
    private Context context;

    public ChooseWordReplaceAdapter(Context context, List<WordReplaceEntity> list, ItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_choose_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            WordReplaceEntity entity = list.get(position);
            holder.tvName.setText(entity.getWordDisplay());
            holder.tvName.setTag(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
                tvName.setOnClickListener(clickListener);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                listener.clickItem((WordReplaceEntity) view.getTag());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public interface ItemListener {
        void clickItem(WordReplaceEntity entity);
    }
}
