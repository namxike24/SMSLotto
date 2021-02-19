package com.smsanalytic.lotto.common;

import android.content.Context;;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @create by hvthuyen
 *@create date 6/29/2016

 * @param <V>
 * @param <K>
 */
public abstract class AbstractListAdapter<V, K extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<K> implements Filterable {
    public Context context;
    public LayoutInflater mInflater;
    public IOnChangedData onChangedData;

    public AbstractListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    protected List<V> mData = new ArrayList<V>();

    @Override
    public abstract K onCreateViewHolder(ViewGroup viewGroup, int i);

    @Override
    public abstract void onBindViewHolder(K k, int i);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(final List<V> data) {
        // Remove all deleted items.
        for (int i = mData.size() - 1; i >= 0; --i) {
            if (getLocation(data, mData.get(i)) < 0) {
                delete(i);
            }
        }

        // Add and move items.
        for (int i = 0; i < data.size(); ++i) {
            V entity = data.get(i);
            int loc = getLocation(mData, entity);
            if (loc < 0) {
                add(i, entity);
            } else if (loc != i) {
                moveEntity(i, loc);

            }
        }
        if (onChangedData != null)
            onChangedData.onChangedData(data.size());
    }

    /**
     * @param data
     * @param entity
     * @return
     */
    private int getLocation(List<V> data, V entity) {
        for (int j = 0; j < data.size(); ++j) {
            V newEntity = data.get(j);
            if (entity.equals(newEntity)) {
                return j;
            }
        }

        return -1;
    }

    /**
     * @param position vị trí cần lấy ra trong adapter
     * @return
     * @author hvthuyen
     */
    public V getItem(int position) {
        return mData.get(position);
    }

    /**
     * @param i
     * @param entity
     * @author hvthuyen
     */
    public void add(int i, V entity) {
        mData.add(i, entity);

        if (onChangedData != null)
            onChangedData.onChangedData(mData.size());
        notifyItemInserted(i);
    }



    /**
     * @param entity
     * @author hvthuyen
     */
    public void add(V entity) {
        mData.add(entity);
        if (onChangedData != null)
            onChangedData.onChangedData(mData.size());
    }

    /**
     * @param i
     * @author hvthuyen
     */
    public void delete(int i) {
        mData.remove(i);
        if (onChangedData != null)
            onChangedData.onChangedData(mData.size());
        notifyItemRemoved(i);
        // notifyItemRangeChanged(i, getItemCount());
    }

    public void moveEntity(int i, int loc) {
        move(mData, i, loc);
        notifyItemMoved(i, loc);
    }

    private void move(List<V> data, int a, int b) {
        V temp = data.remove(a);
        data.add(b, temp);
    }

    /**
     * @author hvthuyen
     */
    public void clear() {
        mData.clear();
        if (onChangedData != null)
            onChangedData.onChangedData(mData.size());
    }

    public List<V> getData() {
        return mData;
    }

    /**
     * @param data
     * @author hvthuyen
     */
    public void addAll(List<V> data) {
        mData.addAll(data);
        if (onChangedData != null)
            onChangedData.onChangedData(mData.size());

    }

    /**
     * kiểm tra tra xem hóa đơn
     *
     * @create by hvthuyen
     * @create date 6/6/2016
     */
    public interface IOnChangedData {
        /**
         * @param count số phần tử trong adapter
         * @create by hvthuyen
         * @create date 6/6/2016
         */
        void onChangedData(int count);
    }

    public void setOnChangedData(IOnChangedData onChangedData) {
        this.onChangedData = onChangedData;
    }
}
