package com.smsanalytic.lotto.ui.smsProcess;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.eventbus.OnProcessMessageSuccess;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.eventbus.SelectAccountTypeEvent;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.message.ProcessMessageActivity;
import com.smsanalytic.lotto.ui.smsProcess.adapter.SmsEmptyAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDeleteDebt;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsEmptyFragment extends Fragment implements IClickListener {

    @BindView(R.id.rvSmsEmpty)
    RecyclerView rvSmsEmpty;
    private View view;
    private List<SmsObject> smsObjectList;
    private SmsEmptyAdapter emptyAdapter;
    private int smsType;
    DaoSession daoSession;
    @BindView(R.id.mes_empty)
    TextView mesEmpty;

    public static SmsEmptyFragment newInstance(int smsType) {
        Bundle args = new Bundle();
        SmsEmptyFragment fragment = new SmsEmptyFragment();
        fragment.setArguments(args);
        fragment.smsType = smsType;
        return fragment;
    }

    public SmsEmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_empty, container, false);
            ButterKnife.bind(this, view);
            initAdapter();
        }
        return view;
    }

    private void initAdapter() {
        daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
        smsObjectList = new ArrayList<>();
        emptyAdapter = new SmsEmptyAdapter(smsObjectList, getContext(),SmsEmptyAdapter.SMS_EMPTY_TYPE);
        emptyAdapter.setiClickListenerl(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsEmpty.setAdapter(emptyAdapter);
        rvSmsEmpty.setLayoutManager(layoutManager);
        smsObjectList.clear();
        initData();
    }

    private void initData() {
       try {
           int loaiKhachHang = PreferencesManager.getInstance().getValueInt(PreferencesManager.SMS_FROM, 1);
           int smsStatus = loaiKhachHang;
           Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(),Common.FORMAT_DATE_DD_MM_YYY);
           Long dateEnd = Common.addHourToDate(currentDate, 24);
           smsObjectList.clear();
           List<SmsObject> list = new ArrayList<>();
           List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().list();
           if (smsType == SmsType.SMS_EMPTY) {
               for (AccountObject accountObject : accountList) {
                   daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.SmsStatus.eq(smsStatus), SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), SmsObjectDao.Properties.Date.between(currentDate, dateEnd)).orderAsc(SmsObjectDao.Properties.Date).build().list();
                   list.addAll(daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.SmsStatus.eq(smsStatus),SmsObjectDao.Properties.SmsType.eq(SmsType.SMS_EMPTY), SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), SmsObjectDao.Properties.Date.between(currentDate, dateEnd)).orderAsc(SmsObjectDao.Properties.Date).build().list());
               }
           } else {
               for (AccountObject accountObject : accountList) {
                   list.addAll(daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.SmsStatus.eq(smsStatus),SmsObjectDao.Properties.IsSuccess.eq(true), SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), SmsObjectDao.Properties.Date.between(currentDate, dateEnd)).orderAsc(SmsObjectDao.Properties.Date).build().list());
               }
           }
           if (list != null) {
               smsObjectList.addAll(list);
           }
           if (smsObjectList.size() > 1) {
               rvSmsEmpty.scrollToPosition(smsObjectList.size() - 1);
           }
           if (smsObjectList.size()==0){
               mesEmpty.setVisibility(View.VISIBLE);
               if (smsType == SmsType.SMS_EMPTY){
                   mesEmpty.setText(getString(R.string.sms_tin_trong_empty));
               }
               else
               {
                   mesEmpty.setText(getString(R.string.sms_tin_da_xu_ly_empty));
               }
           }
           else{
               mesEmpty.setVisibility(View.GONE);
           }


           emptyAdapter.notifyDataSetChanged();
       }
       catch (Exception e){

       }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushNotification(PushNotificationEvent event) {
        if (event.getSmsObject() != null && emptyAdapter != null) {
            if (event.getSmsObject().isSuccess()) {
                initData();
            }
        }
    }

    @Override
    public void clickEvent(View view, int p) {
        switch (view.getId()) {
            case R.id.lnItem:
                showPopupProcessSMS(view, smsObjectList.get(p), R.menu.menu_empty_sms);
                break;
        }
    }

    private void showPopupProcessSMS(View view, SmsObject smsObject, int layout) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(layout, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.processSmsEvent: // xử lý tin nhắn
                    Intent intentProcess = new Intent(getActivity(), ProcessMessageActivity.class);
                    intentProcess.putExtra(ProcessMessageActivity.MESSAGE, new Gson().toJson(smsObject));
                    startActivity(intentProcess);
                    break;
                case R.id.removeSmsEvent: // xóa tin nhắn
                    processRemoveSms(smsObject);
                    break;
            }
            return true;
        });
        popup.show(); //showing popup menu
    }



    private void processRemoveSms(SmsObject smsObject) {
        Common.createDialog(getContext(), "Xác nhận", "Bạn có chắc chắn muốn xóa tin nhắn này?", new IClickListener() {
            @Override
            public void acceptEvent(boolean accept) {
                if (accept) {
                    // xóa tin nhắn đó khỏi bảng tin nhắn
                    daoSession.getSmsObjectDao().delete(smsObject);
                    for (int i = 0; i < smsObjectList.size(); i++) {
                        if (smsObjectList.get(i).getGuid().equals(smsObject.getGuid())) {
                            smsObjectList.remove(i);
                            emptyAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
                // xóa các loto trong bản loto
                List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObject.getGuid())).list();
                daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);

                // xóa công nợ
                int typeTiente = Common.getTypeTienTe(getContext());
                List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(smsObject.getIdAccouunt())).list();
                if (accountList.size() > 0) {
                    processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects, accountList.get(0), typeTiente);
                }
            }
        });
    }

    @Subscribe
    public void onEvent(OnProcessMessageSuccess event) {
        try {
            if (event.getData() != null) {
                for (int i = 0; i < smsObjectList.size(); i++) {
                    if (smsObjectList.get(i).getGuid().equals(event.getData().getGuid())) {
                        if (smsType == SmsType.SMS_EMPTY) {
                            smsObjectList.remove(i);
                            emptyAdapter.notifyItemRemoved(i);
                        } else {
                            smsObjectList.set(i, event.getData());
                            emptyAdapter.notifyItemChanged(i);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onSelectAccountTypeEvent(SelectAccountTypeEvent event) {
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectedAccount(){
        if (daoSession!=null){
            initData();
        }
    }
}
