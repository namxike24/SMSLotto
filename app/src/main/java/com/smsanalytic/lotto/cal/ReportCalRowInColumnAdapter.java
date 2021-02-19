package com.smsanalytic.lotto.cal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AbstractListAdapter;
import com.smsanalytic.lotto.common.Common;


/**
 * created_by nvnam on 30/06/2017
 */
public class ReportCalRowInColumnAdapter extends AbstractListAdapter<ReportCalEntity, ReportCalRowInColumnAdapter.ViewHolder> {


    public ReportCalRowInColumnAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.item_row_in_column_report_cal, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(mData.get(i), i);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View contentView;
        private TextView tvName;
        private TextView tvValue;
        private TextView tvDate;

        public ViewHolder(final View convertView) {
            super(convertView);
            contentView = convertView;

            tvName = convertView.findViewById(R.id.tvName);
            tvValue = convertView.findViewById(R.id.tvValue);
            tvDate = convertView.findViewById(R.id.tvDate);
        }

        /**
         * Xử lý hiển thị dữ liệu
         * created_by nvnam on 30/06/2017
         *
         * @param entity   Đối tượng cần binding data
         * @param position Vị trí
         */
        public void bind(final ReportCalEntity entity, final int position) {
            try {
                switch (entity.getType()){
                    case ReportCalEntity.TYPE_DATE:
                        tvName.setVisibility(View.GONE);
                        tvDate.setVisibility(View.VISIBLE);
                        tvValue.setVisibility(View.GONE);
                        tvDate.setText(entity.getDate());
                        break;
                    case ReportCalEntity.TYPE_NAME:
                        tvName.setVisibility(View.VISIBLE);
                        tvDate.setVisibility(View.GONE);
                        tvValue.setVisibility(View.GONE);
                        tvName.setText(entity.getCustomerName());
                        break;
                    case ReportCalEntity.TYPE_VALUE:
                        tvName.setVisibility(View.GONE);
                        tvDate.setVisibility(View.GONE);
                        tvValue.setVisibility(View.VISIBLE);
                        tvValue.setText(String.valueOf(Common.formatMoney(entity.getValue(),2)));
                        if (entity.getValue()<0){
                            tvValue.setTextColor(ContextCompat.getColor(context,R.color.red));
                        }else {
                            tvValue.setTextColor(ContextCompat.getColor(context,R.color.back_1));
                        }
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //region Khai báo sự kiện cho item



        //endregion
    }

    public interface ISubjectManagerListener {

    }
}
