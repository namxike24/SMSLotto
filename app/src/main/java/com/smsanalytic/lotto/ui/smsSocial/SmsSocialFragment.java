package com.smsanalytic.lotto.ui.smsSocial;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.eventbus.PushNotificationEvent;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.accountList.AddAccountActivity;
import com.smsanalytic.lotto.ui.smsSocial.adapter.SmsSocialAdapter;

import static com.smsanalytic.lotto.ui.accountList.AddAccountActivity.REQUEST_ADD_ACCOUNT;

public class SmsSocialFragment extends BaseFragment implements IClickListener {
    private View view;
    @BindView(R.id.rvSmsSocial)
    RecyclerView rvSmsSocial;
    List<SmsObject> smsObjectList;
    SmsSocialAdapter smsSocialAdapter;

    private DaoSession daoSession;
    private int fragmentType = 1;

    public static final int FRAGMENT_SOCIAL = 1;// danh sách tin nhắn mạng xã hội
    public static final int FRAGMENT_OTHER = 2;// danh sách tin nhắn khác

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sms_social, container, false);
            ButterKnife.bind(this, view);
            initAdapter();
        }


//        btn1.setText(list.get(0).getAccountName());
//        btn2.setText(list.get(1).getAccountName());
//
//        btn1.setOnClickListener(v -> {
//            List<SmsObject> list1 = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.AccountName.eq(btn1.getText())).orderDesc(SmsObjectDao.Properties.Date).build().list();
//            for (SmsObject smsObject : list1) {
//                    Log.e("==="+smsObject.getAccountName()+"===",smsObject.getSmsRoot());
//            }
//        });
//        btn2.setOnClickListener(v -> {
//            List<SmsObject> list1 = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.AccountName.eq(btn2.getText())).orderDesc(SmsObjectDao.Properties.Date).build().list();
//
//            for (SmsObject smsObject : list1) {
//                Log.e("==="+smsObject.getAccountName()+"===",smsObject.getIdAccouunt());
//            }
//
//
//            shareAppLinkViaFacebook("100003975336850");
//
//        });
        return view;
    }

    public static SmsSocialFragment newInstance(int fragmentType) {
        Bundle args = new Bundle();
        SmsSocialFragment fragment = new SmsSocialFragment();
        fragment.setArguments(args);
        fragment.fragmentType = fragmentType;
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initAdapter() {
        smsObjectList = new ArrayList<>();
        smsSocialAdapter = new SmsSocialAdapter(smsObjectList, getContext());
        smsSocialAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsSocial.setAdapter(smsSocialAdapter);
        rvSmsSocial.setLayoutManager(layoutManager);
        daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
        updateData();
    }

    private void shareAppLinkViaFacebook(String urlToShare) {
        try {
            Intent intent1 = new Intent();
            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
            intent1.setAction("android.intent.action.SEND");
            intent1.setType("text/plain");
            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(intent1);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }

    @Override
    public void clickEvent(View view, int p) {
        SmsObject smsObject = smsObjectList.get(p);
        switch (view.getId()) {
            case R.id.rlItem:
                Intent intent = new Intent(getContext(), SmsDetailActivity.class);
                intent.putExtra(SmsDetailActivity.SMS_KEY, new Gson().toJson(smsObject));
                intent.putExtra(SmsDetailActivity.FROM_SCREEN, SmsDetailActivity.FROM_SMS_SOCIAL);
                getActivity().startActivity(intent);
                break;
            case R.id.ivMenu:
                showPopupMenu(view, smsObject);
                break;
        }
    }

    private void showPopupMenu(View view, SmsObject smsObject) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_add_account, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.addAccountEvent:
                    Intent intent = new Intent(getActivity(), AddAccountActivity.class);
                    intent.putExtra(AddAccountActivity.SMS_INFO_KEY, new Gson().toJson(smsObject));
                    intent.putExtra(AddAccountActivity.STATUS_SCREEN, AddAccountActivity.CREATE_NEW_ACCOUNT_TYPE);
                    startActivityForResult(intent, REQUEST_ADD_ACCOUNT);
                    break;
                case R.id.updateAccountEvent:
                    break;
                case R.id.deleteEvent:
                    List<SmsObject> list = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(smsObject.getIdAccouunt())).list();
                    daoSession.getSmsObjectDao().deleteInTx(list);
                    updateData();
                    break;
            }
            return true;
        });
        popup.show(); //showing popup menu
    }


    private void updateData() {
        List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().list();
        List<SmsObject> list = new ArrayList<>();
        if (fragmentType == FRAGMENT_SOCIAL) {
            list = daoSession.getSmsObjectDao().queryBuilder().where(new WhereCondition.StringCondition("1 GROUP BY id_account HAVING ( sms_type=6  or  sms_type=5)")).orderDesc(SmsObjectDao.Properties.Date).build().list();
        } else if (fragmentType == FRAGMENT_OTHER) {
            list = daoSession.getSmsObjectDao().queryBuilder().where(new WhereCondition.StringCondition("1 GROUP BY id_account HAVING sms_type=4")).orderDesc(SmsObjectDao.Properties.Date).build().list();
        }
        for (AccountObject account : accountList) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).getIdAccouunt().equals(account.getIdAccount())) {
                    list.remove(i);
                }
            }
        }
        smsObjectList.clear();
        smsObjectList.addAll(list);
        smsSocialAdapter.notifyDataSetChanged();
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
        updateData();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                updateData();
            } else {
            }
        }
    }
}