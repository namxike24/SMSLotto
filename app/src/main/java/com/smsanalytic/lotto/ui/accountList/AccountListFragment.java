package com.smsanalytic.lotto.ui.accountList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.DebtObject;
import com.smsanalytic.lotto.database.DebtObjectDao;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.ui.accountList.adapter.AccountListAdapter;

import static android.app.Activity.RESULT_OK;
import static com.smsanalytic.lotto.ui.accountList.AddAccountActivity.REQUEST_ADD_ACCOUNT;

public class AccountListFragment extends BaseFragment implements IClickListener {

    private View view;

    @BindView(R.id.btnAddAccount)
    FloatingActionButton btnAddAccount;
    @BindView(R.id.rvAccountList)
    RecyclerView rvAccountList;
    AccountListAdapter accountListAdapter;
    DaoSession daoSession;
    private List<AccountObject> accountObjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_account_list, container, false);
            ButterKnife.bind(this, view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
            initAdapter();
        }
        btnAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddAccountActivity.class);
            intent.putExtra(AddAccountActivity.STATUS_SCREEN, AddAccountActivity.CREATE_NEW_ACCOUNT_TYPE);
            startActivityForResult(intent, AddAccountActivity.REQUEST_ADD_ACCOUNT);
        });
        return view;
    }

    private void initAdapter() {
        accountObjects = new ArrayList<>();
        accountListAdapter = new AccountListAdapter(accountObjects, getContext());
        accountListAdapter.setiClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAccountList.setAdapter(accountListAdapter);
        rvAccountList.setLayoutManager(layoutManager);
        List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().list();
        accountObjects.addAll(list);
        accountListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent intent) {
        if (requestCode == AddAccountActivity.REQUEST_ADD_ACCOUNT &&
                resultCode == RESULT_OK) {
            accountObjects.clear();
            List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().orderAsc(AccountObjectDao.Properties.DateCreate).build().list();
            accountObjects.addAll(list);
            accountListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void clickEvent(View view, int p) {
        switch (view.getId()) {
            case R.id.rlItem:
                showPopupMenu(view, accountObjects.get(p));
                break;
        }
    }

    private void showPopupMenu(View view, AccountObject accountObject) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_account_detail, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.deleteAccount:
                    String mes = "Bạn có muốn xóa khách hàng \n'" + accountObject.getAccountName() + "'\nLưu ý: tất cả các dữ liệu tin nhắn và công nợ của '" + accountObject.getAccountName() + " bị mất";
                    Common.createDialog(getContext(), "Xóa Khách hàng", mes, new IClickListener() {
                        @Override
                        public void acceptEvent(boolean accept) {
                            if (accept) {
                                // xóa Tin nhắn
                                List<SmsObject> smsObjectList = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount())).list();
                                daoSession.getSmsObjectDao().deleteInTx(smsObjectList);
                                // xóa loto
                                List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount())).list();
                                daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
                                // xóa công nợ của khách hàng
                                List<DebtObject> debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount())).list();
                                daoSession.getDebtObjectDao().deleteInTx(debtObjectList);

                                daoSession.getAccountObjectDao().delete(accountObject);
                                accountObjects.clear();
                                List<AccountObject> list = daoSession.getAccountObjectDao().queryBuilder().orderAsc(AccountObjectDao.Properties.DateCreate).build().list();
                                accountObjects.addAll(list);
                                accountListAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Xóa Khách hàng '" + accountObject.getAccountName() + "' thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case R.id.updateAccountEvent: // update khách hàng
                    Intent intent = new Intent(getActivity(), AddAccountActivity.class);
                    intent.putExtra(AddAccountActivity.STATUS_SCREEN, AddAccountActivity.UPDATE_ACCOUNT_TYPE);
                    intent.putExtra(AddAccountActivity.ACCOUNT_UPDATE_INFO_KEY, new Gson().toJson(accountObject));
                    startActivityForResult(intent, REQUEST_ADD_ACCOUNT);
                    break;

            }
            return true;
        });
        popup.show(); //showing popup menu
    }
}