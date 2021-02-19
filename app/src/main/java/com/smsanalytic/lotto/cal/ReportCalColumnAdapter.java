package com.smsanalytic.lotto.cal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AbstractListAdapter;


/**
 * created_by nvnam on 30/06/2017
 */
public class ReportCalColumnAdapter extends AbstractListAdapter<ReportCalColumnEntity, ReportCalColumnAdapter.ViewHolder> {


    public ReportCalColumnAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.item_column_report_cal, viewGroup, false));
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
        private RecyclerView rcvData;

        public ViewHolder(final View convertView) {
            super(convertView);
            contentView = convertView;

            rcvData = convertView.findViewById(R.id.rcvData);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rcvData.setLayoutManager(layoutManager);
        }

        /**
         * Xử lý hiển thị dữ liệu
         * created_by nvnam on 30/06/2017
         *
         * @param entity   Đối tượng cần binding data
         * @param position Vị trí
         */
        public void bind(final ReportCalColumnEntity entity, final int position) {
            try {
                ReportCalRowInColumnAdapter adapter = new ReportCalRowInColumnAdapter(context);
                adapter.setData(entity.getListData());
                rcvData.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //region Khai báo sự kiện cho item



        //endregion
    }

}
