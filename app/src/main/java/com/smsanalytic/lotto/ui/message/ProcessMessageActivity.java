package com.smsanalytic.lotto.ui.message;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.BaseActivity;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.DateTimeUtils;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.XSMBObject;
import com.smsanalytic.lotto.database.XSMBObjectDao;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.eventbus.OnProcessMessageSuccess;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;
import com.smsanalytic.lotto.ui.balance.GuiTinCanChuyenActivity;
import com.smsanalytic.lotto.ui.balance.GuiTinNhanAoDialogFragment;
import com.smsanalytic.lotto.ui.message.adapter.ProcessMessageResultAdapter;

import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDebt;
import static com.smsanalytic.lotto.notificationListener.NotificationSmsListenerService.processDeleteDebt;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_3C;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_CANGGIUA;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DAUDB;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DAUG1;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DE;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_DUOIG1;
import static com.smsanalytic.lotto.ui.xsmb.XSMBUtils.KEY_LO;

public class ProcessMessageActivity extends BaseActivity {
    public static final String MESSAGE = "MESSAGE";

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.edMessage)
    EditText edMessage;
    @BindView(R.id.btnToOriginal)
    Button btnToOriginal;
    @BindView(R.id.btnProcessAndSave)
    Button btnProcessAndSave;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.lnResult)
    LinearLayout lnResult;
    @BindView(R.id.rcvResult)
    RecyclerView rcvResult;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    LinearLayout toolbar;

    private SmsObject message;
    private ProcessMessageResultAdapter adapter;
    private ArrayList<StringProcessChildEntity> listData = new ArrayList<>();
    private DaoSession daoSession;
    private String donvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_process_message);
            ButterKnife.bind(this);
            daoSession = ((MyApp) getApplication()).getDaoSession();
            int tienteType = Common.getTypeTienTe(this);
            donvi=TienTe.getValueTienTe(tienteType);
            getBundle();
            initView();
            initListener();
            displayData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        try {
            rcvResult.setLayoutManager(new LinearLayoutManager(ProcessMessageActivity.this));
            adapter = new ProcessMessageResultAdapter(ProcessMessageActivity.this, listData, () -> {

            });
            rcvResult.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        try {
            edMessage.setText(message.getSmsProcessed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        try {
            btnBack.setOnClickListener(backListener);
            btnToOriginal.setOnClickListener(toOriginalListener);
            if (message!=null){
                btnProcessAndSave.setOnClickListener(processListener);
            }
            else {
                btnProcessAndSave.setOnClickListener(processListenerNew);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener backListener = view -> {
        try {
            Common.disableView(view);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    private View.OnClickListener toOriginalListener = view -> {
        try {
            Common.disableView(view);
            Common.hideKeyBoard(ProcessMessageActivity.this);
            if (btnToOriginal.getText().equals(getString(R.string.process_message_to_original))){
                edMessage.setText(message.getSmsRoot());
            }
            else{
                GuiTinNhanAoDialogFragment guiTinNhanAoDialogFragment = GuiTinNhanAoDialogFragment.newInstance(edMessage.getText().toString());
                guiTinNhanAoDialogFragment.show(getSupportFragmentManager().beginTransaction(), guiTinNhanAoDialogFragment.getTag());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private View.OnClickListener processListener = view -> {
        try {
            Common.disableView(view);
            Common.hideKeyBoard(ProcessMessageActivity.this);
            StringProcessEntity processEntity = StringCalculate.processStringOriginal(edMessage.getText().toString().trim().toLowerCase());
            if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
                for (int i = 0; i < processEntity.getListChild().size(); i++) {
                    StringProcessChildEntity childEntity = processEntity.getListChild().get(i);
                    AccountObject customer = null;
                    List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(message.getIdAccouunt())).list();
                    if (accountList.size() > 0) {
                        customer = accountList.get(0);
                    }
                    StringCalculate.processChildEntity(processEntity, childEntity, customer
                            , i > 0 && !processEntity.getListChild().get(i - 1).isError() ? processEntity.getListChild().get(i - 1) : null, i);
                }

                if (processEntity.isHasError()) {
                    tvDescription.setVisibility(View.VISIBLE);
                    lnResult.setVisibility(View.GONE);
                    processError(processEntity);
                } else {
                    tvDescription.setVisibility(View.GONE);
                    lnResult.setVisibility(View.VISIBLE);
                    processShowResult(processEntity,donvi);
                    if (message!=null){
                        saveToDB(processEntity);
                    }
                }
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                lnResult.setVisibility(View.GONE);
                createDialogError("Không đúng định dạng");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    private View.OnClickListener processListenerNew = view -> {
        try {
            Common.disableView(view);
            Common.hideKeyBoard(ProcessMessageActivity.this);
            StringProcessEntity processEntity = StringCalculate.processStringOriginal(edMessage.getText().toString().trim().toLowerCase());
            if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
                for (int i = 0; i < processEntity.getListChild().size(); i++) {
                    StringProcessChildEntity childEntity = processEntity.getListChild().get(i);
                    StringCalculate.processChildEntity(processEntity, childEntity, null
                            , i > 0 && !processEntity.getListChild().get(i - 1).isError() ? processEntity.getListChild().get(i - 1) : null, i);
                }
                if (processEntity.isHasError()) {
                    tvDescription.setVisibility(View.VISIBLE);
                    lnResult.setVisibility(View.GONE);
                    processError(processEntity);
                } else {
                    tvDescription.setVisibility(View.GONE);
                    lnResult.setVisibility(View.VISIBLE);
                    processShowResult(processEntity,donvi);

                }
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                lnResult.setVisibility(View.GONE);
                createDialogError("Không đúng định dạng");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    /**
     * Hàm lưu vào cơ sở dữ liệu
     */
    private void saveToDB(StringProcessEntity processEntity) {
        int typeTiente = Common.getTypeTienTe(this);
        List<AccountObject> accountList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(message.getIdAccouunt())).list();
        if (accountList.size() > 0) {
            //update tin nhắn
            message.setSmsProcessed(edMessage.getText().toString());
            message.setSmsType(SmsType.SMS_PROCESSED);
            daoSession.getSmsObjectDao().update(message);

            // xóa các loto trong bản loto
            List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(message.getGuid())).list();
            daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
            // xóa công nợ tin cũ
            processDeleteDebt(daoSession, (ArrayList<LotoNumberObject>) lotoNumberObjects, accountList.get(0), typeTiente);


            ArrayList<LotoNumberObject> objectList = new ArrayList<>();
            for (StringProcessChildEntity stringProcessChildEntity : processEntity.getListChild()) {
                objectList.addAll(stringProcessChildEntity.getListDataLoto());
            }
            // lưu lại số loto
            String date = Common.convertDateByFormat((message.getDate()), DateTimeUtils.DAY_MONTH_YEAR_FORMAT);
            List<XSMBObject> listDataXSMB = new ArrayList<>();
            try {
                listDataXSMB = MyApp.getInstance().getDaoSession().getXSMBObjectDao().queryBuilder().where(XSMBObjectDao.Properties.Date.eq(date)).list();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (LotoNumberObject lotoNumberObject : objectList) {
                lotoNumberObject.setGuid(message.getGuid());
                lotoNumberObject.setAccountSend(message.getIdAccouunt());
                lotoNumberObject.setDateTake(message.getDate());
                lotoNumberObject.setDateString(Common.convertDateByFormat((message.getDate()), Common.FORMAT_DATE_DD_MM_YYY_2));
                lotoNumberObject.setSmsStatus(message.getSmsStatus());
                /**
                 * xử lý kết quả trúng
                 */
                processCheckHitLotto(listDataXSMB, lotoNumberObject);
                daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
                if (message.getSmsStatus() == SmsStatus.SMS_SENT) {
                    GuiTinCanChuyenActivity.udpateTienGiuLai(daoSession, lotoNumberObject);
                }
            }
            // hàm xử lý công nợ
            processDebt(daoSession, objectList, accountList.get(0), typeTiente);
        }
    }

    private void processCheckHitLotto(List<XSMBObject> listDataXSMB, LotoNumberObject lotto) {
        try {
            if (listDataXSMB != null && !listDataXSMB.isEmpty()) {
                String hit = listDataXSMB.get(0).getResult();
                TypeToken<Map<String, ArrayList<String>>> typeToken = new TypeToken<Map<String, ArrayList<String>>>() {
                };
                Map<String, ArrayList<String>> dataHit = new Gson().fromJson(hit, typeToken.getType());
                if (dataHit != null && !dataHit.isEmpty()) {

                    switch (lotto.getType()) {
                        case TypeEnum.TYPE_DE:
                            if (dataHit.get(KEY_DE).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DAUDB:
                            if (dataHit.get(KEY_DAUDB).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_LO:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())) {
                                int nhay = 0;
                                AccountObject customer = null;
                                try {
                                    List<AccountObject> accountList = MyApp.getInstance().getDaoSession().getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(lotto.getAccountSend())).list();
                                    if (accountList.size() > 0) {
                                        customer = accountList.get(0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                AccountSetting accountSetting = null;
                                try {
                                    if (customer != null) {
                                        accountSetting = new Gson().fromJson(customer.getAccountSetting(), AccountSetting.class);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                for (String s : dataHit.get(KEY_LO)) {
                                    if (s.equalsIgnoreCase(lotto.getValue1())) {
                                        if (accountSetting != null && accountSetting.getSonhaylotrathuongtoida() <= nhay) {
                                            break;
                                        } else {
                                            nhay++;
                                        }
                                    }
                                }
                                lotto.setHit(true);
                                lotto.setNhay(nhay);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_3C:
                            if (dataHit.get(KEY_3C).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DITNHAT:
                            if (dataHit.get(KEY_DUOIG1).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_DAUNHAT:
                            if (dataHit.get(KEY_DAUG1).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_CANGGIUA:
                            if (dataHit.get(KEY_CANGGIUA).contains(lotto.getValue1())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP2:
                        case TypeEnum.TYPE_XIEN2:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP3:
                        case TypeEnum.TYPE_XIEN3:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENGHEP4:
                        case TypeEnum.TYPE_XIEN4:
                            if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                    && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                                lotto.setHit(true);
                                MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                            }
                            break;
                        case TypeEnum.TYPE_XIENQUAY:
                            if (!TextUtils.isEmpty(lotto.getValue3()) && !TextUtils.isEmpty(lotto.getValue4())) {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue4())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            } else if (!TextUtils.isEmpty(lotto.getValue3())) {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            } else {
                                if (dataHit.get(KEY_LO).contains(lotto.getValue1())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue2())
                                        && dataHit.get(KEY_LO).contains(lotto.getValue3())) {
                                    lotto.setHit(true);
                                    MyApp.getInstance().getDaoSession().getLotoNumberObjectDao().update(lotto);
                                }
                            }
                            break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processShowResult(StringProcessEntity processEntity,String donvi) {
        try {
            listData.clear();
            listData.addAll(processEntity.getListChild());
            adapter.notifyDataSetChanged();

            StringBuilder sbProcess = new StringBuilder();
            StringBuilder sbTotal = new StringBuilder();

            StringBuilder sbTotalDe = new StringBuilder();
            StringBuilder sbTotalLo = new StringBuilder();
            StringBuilder sbTotal3C = new StringBuilder();
            StringBuilder sbTotalDitNhat = new StringBuilder();
            StringBuilder sbTotalDauDB = new StringBuilder();
            StringBuilder sbTotalDauNhat = new StringBuilder();
            StringBuilder sbTotalCangGiua = new StringBuilder();
            StringBuilder sbTotalXienghep2 = new StringBuilder();
            StringBuilder sbTotalXienghep3 = new StringBuilder();
            StringBuilder sbTotalXienghep4 = new StringBuilder();
            StringBuilder sbTotalXienquay = new StringBuilder();
            StringBuilder sbTotalXien2 = new StringBuilder();
            StringBuilder sbTotalXien3 = new StringBuilder();
            StringBuilder sbTotalXien4 = new StringBuilder();
            ArrayList<StringBuilder> listSbTotal = new ArrayList<>();
            listSbTotal.add(sbTotalDe);
            listSbTotal.add(sbTotalLo);
            listSbTotal.add(sbTotal3C);
            listSbTotal.add(sbTotalDitNhat);
            listSbTotal.add(sbTotalDauDB);
            listSbTotal.add(sbTotalDauNhat);
            listSbTotal.add(sbTotalCangGiua);
            listSbTotal.add(sbTotalXienghep3);
            listSbTotal.add(sbTotalXienghep4);
            listSbTotal.add(sbTotalXienquay);
            listSbTotal.add(sbTotalXien2);
            listSbTotal.add(sbTotalXien3);
            listSbTotal.add(sbTotalXien4);

            int totalDeNum = 0;
            double totalDePrice = 0;
            int totalLoNum = 0;
            double totalLoPrice = 0;
            int total3CNum = 0;
            double total3CPrice = 0;
            int totalDitNhatNum = 0;
            double totalDitNhatPrice = 0;
            int totalDauDBNum = 0;
            double totalDauDBPrice = 0;
            int totalDauNhatNum = 0;
            double totalDauNhatPrice = 0;
            int totalCangGiuaNum = 0;
            double totalCangGiuaPrice = 0;
            int totalXienghep2Num = 0;
            double totalXienghep2Price = 0;
            int totalXienghep3Num = 0;
            double totalXienghep3Price = 0;
            int totalXienghep4Num = 0;
            double totalXienghep4Price = 0;
            int totalXienquayNum = 0;
            double totalXienquayPrice = 0;
            int totalXien2Num = 0;
            double totalXien2Price = 0;
            int totalXien3Num = 0;
            double totalXien3Price = 0;
            int totalXien4Num = 0;
            double totalXien4Price = 0;

            for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                String value = Common.getValueReplaceKey(childEntity.getValue());
                if (childEntity.isError()) {
                    sbProcess.append(value).append(". ");
                } else {
                    sbProcess.append(value.substring(0, value.lastIndexOf(" "))).append("<b>").append(value.substring(value.lastIndexOf(" "))).append("</b>").append(". ");
                }

                double price = 0;
                for (LotoNumberObject numberObject : childEntity.getListDataLoto()) {
                    price += numberObject.getMoneyTake();
                }
                switch (childEntity.getType()) {
                    case TypeEnum.TYPE_LO:
                        totalLoNum += childEntity.getListDataLoto().size();
                        totalLoPrice += price;
                        break;
                    case TypeEnum.TYPE_3C:
                        total3CNum += childEntity.getListDataLoto().size();
                        total3CPrice += price;
                        break;
                    case TypeEnum.TYPE_DITNHAT:
                        totalDitNhatNum += childEntity.getListDataLoto().size();
                        totalDitNhatPrice += price;
                        break;
                    case TypeEnum.TYPE_DAUDB:
                        totalDauDBNum += childEntity.getListDataLoto().size();
                        totalDauDBPrice += price;
                        break;
                    case TypeEnum.TYPE_DAUNHAT:
                        totalDauNhatNum += childEntity.getListDataLoto().size();
                        totalDauNhatPrice += price;
                        break;
                    case TypeEnum.TYPE_CANGGIUA:
                        totalCangGiuaNum += childEntity.getListDataLoto().size();
                        totalCangGiuaPrice += price;
                        break;
                    case TypeEnum.TYPE_XIENGHEP2:
                        totalXienghep2Num += childEntity.getListDataLoto().size();
                        totalXienghep2Price += price;
                        break;
                    case TypeEnum.TYPE_XIENGHEP3:
                        totalXienghep3Num += childEntity.getListDataLoto().size();
                        totalXienghep3Price += price;
                        break;
                    case TypeEnum.TYPE_XIENGHEP4:
                        totalXienghep4Num += childEntity.getListDataLoto().size();
                        totalXienghep4Price += price;
                        break;
                    case TypeEnum.TYPE_XIENQUAY:
                        totalXienquayNum += childEntity.getListDataLoto().size();
                        totalXienquayPrice += price;
                        break;
                    case TypeEnum.TYPE_XIEN2:
                        totalXien2Num += childEntity.getListDataLoto().size();
                        totalXien2Price += price;
                        break;
                    case TypeEnum.TYPE_XIEN3:
                        totalXien3Num += childEntity.getListDataLoto().size();
                        totalXien3Price += price;
                        break;
                    case TypeEnum.TYPE_XIEN4:
                        totalXien4Num += childEntity.getListDataLoto().size();
                        totalXien4Price += price;
                        break;
                    default:
                        totalDeNum += childEntity.getListDataLoto().size();
                        totalDePrice += price;
                }
            }


            if (totalDeNum > 0 && totalDePrice > 0) {
                sbTotalDe.append(String.format("- Đề %s cặp: %s "+donvi+"", String.valueOf(totalDeNum), String.valueOf(Common.formatMoney(totalDePrice, 1))));
            }

            if (totalLoNum > 0 && totalLoPrice > 0) {
                sbTotalLo.append(String.format("- Lô %s cặp: %s Điểm", String.valueOf(totalLoNum), String.valueOf(Common.formatMoney(totalLoPrice, 1))));
            }

            if (total3CNum > 0 && total3CPrice > 0) {
                sbTotalLo.append(String.format("- Ba càng %s cặp: %s "+donvi+"", String.valueOf(total3CNum), String.valueOf(Common.formatMoney(total3CPrice, 1))));
            }

            if (totalDitNhatNum > 0 && totalDitNhatPrice > 0) {
                sbTotalDitNhat.append(String.format("- Đuôi G1 %s cặp: %s "+donvi+"", String.valueOf(totalDitNhatNum), String.valueOf(Common.formatMoney(totalDitNhatPrice, 1))));
            }

            if (totalDauDBNum > 0 && totalDauDBPrice > 0) {
                sbTotalDauDB.append(String.format("- Đầu ĐB %s cặp: %s "+donvi+"", String.valueOf(totalDauDBNum), String.valueOf(Common.formatMoney(totalDauDBPrice, 1))));
            }

            if (totalDauNhatNum > 0 && totalDauNhatPrice > 0) {
                sbTotalDauNhat.append(String.format("- Đầu G1 %s cặp: %s "+donvi+"", String.valueOf(totalDauNhatNum), String.valueOf(Common.formatMoney(totalDauNhatPrice, 1))));
            }

            if (totalCangGiuaNum > 0 && totalCangGiuaPrice > 0) {
                sbTotalDauNhat.append(String.format("- Càng giữa %s cặp: %s "+donvi+"", String.valueOf(totalCangGiuaNum), String.valueOf(Common.formatMoney(totalCangGiuaPrice, 1))));
            }

            if (totalXienghep2Num > 0 && totalXienghep2Price > 0) {
                sbTotalXienghep2.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep2Num), String.valueOf(Common.formatMoney(totalXienghep2Price, 1))));
            }

            if (totalXienghep3Num > 0 && totalXienghep3Price > 0) {
                sbTotalXienghep3.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep3Num), String.valueOf(Common.formatMoney(totalXienghep3Price, 1))));
            }

            if (totalXienghep4Num > 0 && totalXienghep4Price > 0) {
                sbTotalXienghep4.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep4Num), String.valueOf(Common.formatMoney(totalXienghep4Price, 1))));
            }

            if (totalXienquayNum > 0 && totalXienquayPrice > 0) {
                sbTotalXienquay.append(String.format("- Xiên quay %s cặp: %s "+donvi+"", String.valueOf(totalXienquayNum), String.valueOf(Common.formatMoney(totalXienquayPrice, 1))));
            }

            if (totalXien2Num > 0 && totalXien2Price > 0) {
                sbTotalXien2.append(String.format("- Xiên 2 %s cặp: %s "+donvi+"", String.valueOf(totalXien2Num), String.valueOf(Common.formatMoney(totalXien2Price, 1))));
            }
            if (totalXien3Num > 0 && totalXien3Price > 0) {
                sbTotalXien3.append(String.format("- Xiên 3 %s cặp: %s "+donvi+"", String.valueOf(totalXien3Num), String.valueOf(Common.formatMoney(totalXien3Price, 1))));
            }
            if (totalXien4Num > 0 && totalXien4Price > 0) {
                sbTotalXien4.append(String.format("- Xiên 4 %s cặp: %s "+donvi+"", String.valueOf(totalXien4Num), String.valueOf(Common.formatMoney(totalXien4Price, 1))));
            }

            edMessage.setText(Html.fromHtml(sbProcess.toString()));
            sbTotal.append("Toàn bộ:");
            for (StringBuilder stringBuilder : listSbTotal) {
                if (!TextUtils.isEmpty(stringBuilder.toString())) {
                    sbTotal.append("\n").append(stringBuilder.toString());
                }
            }
            tvTotal.setText(sbTotal.toString());


            //TODO xử lý lưu thông tin số vào database
//            for (StringProcessChildEntity entity : processEntity.getListChild()) {
//                // xóa các loto cũ trong bản loto của tin nhắn
//                List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.IdSms.eq(message.getIdSms())).list();
//                daoSession.getLotoNumberObjectDao().deleteInTx(lotoNumberObjects);
//
//                //Lưu danh sách số mới vào database
//                for (LotoNumberObject lotoNumberObject : entity.getListDataLoto()) {
//                    lotoNumberObject.setIdSms(message.getIdSms());
//                    lotoNumberObject.setAccountSend(message.getIdAccouunt());
//                    daoSession.getLotoNumberObjectDao().save(lotoNumberObject);
//                }
//            }
//
            message.setSmsProcessed(edMessage.getText().toString().trim());
            message.setSuccess(true);
            EventBus.getDefault().post(new OnProcessMessageSuccess(message));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processError(StringProcessEntity processEntity) {
        try {
            int no = 1;
            StringBuilder sbError = new StringBuilder();
            StringBuilder sbProcess = new StringBuilder();
            for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                if (childEntity.isError()) {
                    sbError.append(no).append(". ").append(childEntity.getError()).append("\n");
                    sbProcess.append(childEntity.getValue()).append(". ");
                    no++;
                }else {
                    sbProcess.append(childEntity.getValue().substring(0, childEntity.getValue().lastIndexOf(" "))).append("<b>").append(childEntity.getValue().substring(childEntity.getValue().lastIndexOf(" ")))
                            .append("</b>").append(". ");
                }
            }
            edMessage.setText(Html.fromHtml(sbProcess.toString()));
            createDialogError(sbError.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDialogError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProcessMessageActivity.this);
        builder.setTitle("Tin nhắn sai cú pháp");
        builder.setMessage(error);
        builder.setNegativeButton("Đồng ý", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void getBundle() {
        try {
            String data = getIntent().getStringExtra(MESSAGE);
            message = new Gson().fromJson(data, SmsObject.class);
            if (message == null) {
                tvGroupName.setText("Soạn thảo tin nhắn");
                btnToOriginal.setText("Chuyển tin này đi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
