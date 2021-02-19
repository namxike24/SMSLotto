package com.smsanalytic.lotto.ui.asyncData;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseFragment;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.DebtObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AsyncDataFragment extends BaseFragment {
    private View view;
    private DaoSession daoSession;

    @BindView(R.id.tvDeleteData)
    TextView tvDeleteData;
    @BindView(R.id.tvAsyncData)
    TextView tvAsyncData;
    @BindView(R.id.tvSaveData)
    TextView tvSaveData;

    private ProgressDialog progressDialog;

    private List<AccountObject> accountObjects;
    private List<DebtObject> debtObjects;


    public AsyncDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_async_data, container, false);
            ButterKnife.bind(this, view);
            daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 250);
    }

    private void getData() {
        try {
            accountObjects = daoSession.getAccountObjectDao().loadAll();
            debtObjects = daoSession.getDebtObjectDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {
            tvDeleteData.setOnClickListener(deleteDataListener);
            tvAsyncData.setOnClickListener(asyncDataListener);
            tvSaveData.setOnClickListener(saveDataListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener deleteDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Xóa tất cả dữ liệu");
                builder.setMessage("Bạn có chắc là muốn xóa tất cả các dữ liệu bao gồm Tin nhắn và công nợ của tất cả khách hàng???");
                builder.setNegativeButton("XÓA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showDialogEnterPassDelete();
                    }
                });
                builder.setPositiveButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void showDialogEnterPassDelete() {
        try {
            EnterPassDialog enterPassDialog = EnterPassDialog.newInstance(dialogListener);
            enterPassDialog.show(getActivity().getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EnterPassDialog.DialogListener dialogListener = new EnterPassDialog.DialogListener() {
        @Override
        public void onEnterPassSuccess() {
            try {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Đang xóa dữ liệu...");
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteData();
                    }
                }, 250);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void deleteData() {
        try {
            daoSession.getLotoNumberObjectDao().deleteAll();
            daoSession.getDebtObjectDao().deleteAll();
            daoSession.getSmsObjectDao().deleteAll();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Xóa dữ liệu thành công!", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener asyncDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Phục hồi số liệu");
                builder.setMessage("LOTO SMART sẽ phục hồi Danh sách khách hàng và Công nợ của bạn từ file sao lưu. Việc này sẽ dẫn đến danh sách khách hàng và công nợ của bạn bị thay thế bởi file đã sao lưu trước đấy. Hãy cân nhắc trước khi thực hiện");
                builder.setNegativeButton("PHỤC HỒI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        processAsyncData();
                    }
                });
                builder.setPositiveButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void processAsyncData() {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang phục hồi dữ liệu...");
            progressDialog.show();
            final Query query = MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.currentAccount.getImei());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        List<AccountObject> listCustomer = new ArrayList<>();
                        List<DebtObject> listDebt = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals("Customer") && snapshot.getChildrenCount() > 0) {

                                GenericTypeIndicator<List<AccountObject>> type = new GenericTypeIndicator<List<AccountObject>>() {
                                };
                                listCustomer = snapshot.getValue(type);
                            } else if (snapshot.getKey().equals("Debt") && snapshot.getChildrenCount() > 0) {
                                GenericTypeIndicator<List<DebtObject>> type = new GenericTypeIndicator<List<DebtObject>>() {
                                };
                                listDebt = snapshot.getValue(type);
                            }
                        }
                        if ((listCustomer != null && !listCustomer.isEmpty())
                                || listDebt != null && !listDebt.isEmpty()) {
                            processDataFromServer(listCustomer, listDebt);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MyApp.getInstance(), "Phục hồi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MyApp.getInstance(), "Phục hồi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(MyApp.getInstance(), "Có lỗi xảy ra trong quá trình tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDataFromServer(List<AccountObject> listCustomer, List<DebtObject> listDebt) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (listCustomer != null && !listCustomer.isEmpty()){
                            daoSession.getAccountObjectDao().deleteAll();
                            daoSession.getAccountObjectDao().insertInTx(listCustomer);
                            accountObjects.clear();
                            accountObjects.addAll(listCustomer);
                        }
                        if (listDebt != null && !listDebt.isEmpty()){
                            daoSession.getDebtObjectDao().deleteAll();
                            daoSession.getDebtObjectDao().insertInTx(listDebt);
                            debtObjects.clear();
                            debtObjects.addAll(listDebt);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(MyApp.getInstance(), "Phục hồi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(MyApp.getInstance(), "Phục hồi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(MyApp.getInstance(), "Phục hồi dữ liệu thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener saveDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.disableView(view);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Sao lưu số liệu");
                builder.setMessage("LOTO SMART sẽ sao lưu Danh sách khách hàng và Công nợ của bạn. File sao lưu này sẽ thay thế file sao lưu trước đấy. Hãy cân nhắc trước khi thực hiện");
                builder.setNegativeButton("SAO LƯU", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        processSaveDataCustomer();
                    }
                });
                builder.setPositiveButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void processSaveDataCustomer() {
        try {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Đang sao lưu dữ liệu...");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (accountObjects != null && !accountObjects.isEmpty()) {
                        MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.currentAccount.getImei()).child("Customer").setValue(accountObjects);
                        MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                processSaveDataDebt();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu 1", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        processSaveDataDebt();
                    }
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processSaveDataDebt() {
        try {
            if (debtObjects != null && !debtObjects.isEmpty()) {
                MyApp.mFirebaseDatabase.child(Common.DBFB).child(MyApp.currentAccount.getImei()).child("Debt").setValue(debtObjects);
                MyApp.mFirebaseDatabase.child(Common.DBFB).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(MyApp.getInstance(), "Sao lưu dữ liệu thành công", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình ghi dữ liệu 1", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Sao lưu dữ liệu thành công", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
