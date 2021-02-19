package com.smsanalytic.lotto.ui.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class ProcessMessageResultAdapter extends RecyclerView.Adapter<ProcessMessageResultAdapter.ViewHolder> {

    private ItemListener listener;
    private List<StringProcessChildEntity> list;
    private Context context;
    private SettingDefault settingDefault;
    private String donvi;

    public ProcessMessageResultAdapter(Context context, List<StringProcessChildEntity> list, ItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        getDataSetting();
    }
    private void getDataSetting(){
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(context, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_result_process_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            StringProcessChildEntity entity = list.get(position);
            holder.tvValue.setText(getValueString(entity));
            if (entity.isShowChild()) {
                holder.rcvChild.setVisibility(View.VISIBLE);
            } else {
                holder.rcvChild.setVisibility(View.GONE);
            }

            ArrayList<String> listChild = getListChild(entity.getListDataLoto(), entity.getType());
            ProcessMessageResultChildAdapter adapter = new ProcessMessageResultChildAdapter(context, listChild, new ProcessMessageResultChildAdapter.ItemListener() {
                @Override
                public void clickItem() {
                    entity.setShowChild(false);
                    notifyItemChanged(position);
                }
            });
            holder.rcvChild.setAdapter(adapter);
            holder.lnRoot.setTag(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getListChild(ArrayList<LotoNumberObject> lotoNumberObjects, int type) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {
            switch (type) {
                case TypeEnum.TYPE_LO:
                    sb.append(TypeEnum.getStringByTypeFull(type)).append(": ")
                            .append(getListNum(lotoNumberObjects)).append(" x ")
                            .append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake()))
                            .append("d");
                    result.add(sb.toString());
                    break;
                case TypeEnum.TYPE_3C:
                case TypeEnum.TYPE_DITNHAT:
                case TypeEnum.TYPE_DAUDB:
                case TypeEnum.TYPE_DAUNHAT:
                case TypeEnum.TYPE_CANGGIUA:
                    sb.append(TypeEnum.getStringByTypeFull(type)).append(": ")
                            .append(getListNum(lotoNumberObjects)).append(" x ")
                            .append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake()))
                            .append(donvi);
                    result.add(sb.toString());
                    break;
                case TypeEnum.TYPE_XIENGHEP2:
                case TypeEnum.TYPE_XIENGHEP3:
                case TypeEnum.TYPE_XIENGHEP4:
                case TypeEnum.TYPE_XIENQUAY:
                case TypeEnum.TYPE_XIEN2:
                case TypeEnum.TYPE_XIEN3:
                case TypeEnum.TYPE_XIEN4:
                    for (LotoNumberObject numberObject : lotoNumberObjects) {
                        sb = new StringBuilder();
                        sb.append(TypeEnum.getStringByTypeFull(type)).append(": ")
                                .append(numberObject.getValue1()).append(",").append(numberObject.getValue2());

                        if (numberObject.getValue3() != null) {
                            sb.append(",").append(numberObject.getValue3());
                        }
                        if (numberObject.getValue4() != null) {
                            sb.append(",").append(numberObject.getValue4());
                        }
                        sb.append(" x ").append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake())).append(donvi);
                        result.add(sb.toString());
                    }
                    break;

                default:
                    sb.append(TypeEnum.getStringByTypeFull(TypeEnum.TYPE_DE)).append(": ")
                            .append(getListNum(lotoNumberObjects)).append(" x ")
                            .append(Common.roundMoney(lotoNumberObjects.get(0).getMoneyTake()))
                            .append(donvi);
                    result.add(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getListNum(ArrayList<LotoNumberObject> entity) {
        StringBuilder sb = new StringBuilder();
        try {
            for (LotoNumberObject numberObject : entity) {
                sb.append(numberObject.getValue1()).append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    private String getValueString(StringProcessChildEntity entity) {
        try {
            StringBuilder sbResult = new StringBuilder();
            double price = 0;
            for (LotoNumberObject numberObject : entity.getListDataLoto()) {
                price += numberObject.getMoneyTake();
            }
            sbResult.append(Common.getValueReplaceKey(entity.getValue()))
                    .append(String.format(" [" + TypeEnum.getStringByTypeFull(entity.getType()) + " %s cặp: %s điểm]"
                            , String.valueOf(entity.getListDataLoto().size()), String.valueOf(Common.formatMoney(price, 1))));
            return sbResult.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lnRoot)
        LinearLayout lnRoot;
        @BindView(R.id.tvValue)
        TextView tvValue;
        @BindView(R.id.rcvChild)
        RecyclerView rcvChild;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
                lnRoot.setOnClickListener(clickListener);
                rcvChild.setLayoutManager(new LinearLayoutManager(context));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private View.OnClickListener clickListener = view -> {
        try {
            Common.disableView(view);
            int position = (int) view.getTag();
            StringProcessChildEntity entity = list.get(position);
            entity.setShowChild(!entity.isShowChild());
            notifyItemChanged(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public interface ItemListener {
        void clickItem();
    }
}
