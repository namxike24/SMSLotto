package com.smsanalytic.lotto.ui.debt;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountStatus;
import com.smsanalytic.lotto.common.BaoCongNo;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.CurrencyEditText;
import com.smsanalytic.lotto.common.CustomDialog;
import com.smsanalytic.lotto.common.KieuBaoCongNo;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.SmsType;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.AccountObjectDao;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.DebtObject;
import com.smsanalytic.lotto.database.DebtObjectDao;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.eventbus.UpdateDebtEvent;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.ui.document.DocumentFragment;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class DebtOfAccountActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.btnNhanTinChotCN)
    Button btnNhanTinChotCN;
    @BindView(R.id.tvkhoanthu)
    TextView tvkhoanthu;
    @BindView(R.id.cetTienThu)
    CurrencyEditText cetTienThu;
    @BindView(R.id.btnLuuKhoanThu)
    Button btnLuuKhoanThu;
    @BindView(R.id.rlKhoanThu)
    RelativeLayout rlKhoanThu;
    @BindView(R.id.tvkhoanchi)
    TextView tvkhoanchi;
    @BindView(R.id.cetTienChi)
    CurrencyEditText cetTienChi;
    @BindView(R.id.btnLuuKhoanChi)
    Button btnLuuKhoanChi;
    @BindView(R.id.tbChiTietCN)
    TableLayout tbChiTietCN;

    public static final String START_DATE_KEY = "start_date_key";
    public static final String END_DATE_KEY = "end_date_key";
    public static final String ACCOUNT_ID_KEY = "account_id_key";
    public static final String ACCOUNT_NAME_KEY = "account_name_key";
    @BindView(R.id.tvAccountName)
    TextView tvAccountName;
    @BindView(R.id.tv_donvi)
    TextView tvDonvi;
    @BindView(R.id.tvNote)
    TextView tvNote;

    private long startDate;
    private long endDate;
    private String idAccount;
    private String accountName;
    private DaoSession daoSession;
    private List<DebtObject> debtObjectList = new ArrayList<>();
    private AccountObject accountObject = null;
    private SettingDefault settingDefault;
    private String donvi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_of_account);
        ButterKnife.bind(this);
        daoSession = MyApp.getInstance().getDaoSession();
        setDataDefault();
        getBundle();
        clickEvent();
    }

    private void setDataDefault() {
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
        tvDonvi.setText("ĐVT:" +TienTe.getValueTienTe3(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM));
        if (settingDefault!=null){
            if (settingDefault.getTiente()==TienTe.TIEN_VIETNAM){
                tvNote.setText(Html.fromHtml(getString(R.string.note_debt)));
            }
            else {
                String donvi=TienTe.getValueTienTe3(settingDefault.getTiente()).toLowerCase();
                String donvi1=TienTe.getValueTienTe2(settingDefault.getTiente()).toLowerCase();
                tvNote.setText(Html.fromHtml(getString(R.string.note_debt_tiente,donvi,donvi1)));
            }
        }
        else{
            tvNote.setText(Html.fromHtml(getString(R.string.note_debt)));
        }
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> finish());
        btnLuuKhoanChi.setOnClickListener(v -> {
            if (!cetTienChi.getText().toString().isEmpty()) {
                if (debtObjectList.size() > 0) {
                    Common.createDialog(this, "Thêm thu chi", "Hãy xem lại số liệu bạn vừa nhập đã đúng chưa? Nếu Đóng thì chọn ĐỒNG Ý còn không thì chọn HỦY để thực hiện lại", new IClickListener() {
                        @Override
                        public void acceptEvent(boolean accept) {
                            if (accept) {
                                try {
                                    debtObjectList.get(0).setMoneyPay(debtObjectList.get(0).getMoneyPay() + (-cetTienChi.getNumericValue()));
                                    daoSession.getDebtObjectDao().update(debtObjectList.get(0));
                                    cetTienChi.setText("");
                                    TableRow row = (TableRow) tbChiTietCN.getChildAt(1);
                                    TextView tvChi = row.findViewById(R.id.tvChi);
                                    TextView tvConLai = row.findViewById(R.id.tvConLai);
                                    tvChi.setText(Common.roundMoney(debtObjectList.get(0).getMoneyPay()));
                                    tvConLai.setText(Common.roundMoney(debtObjectList.get(0).getExpenses() + debtObjectList.get(0).getOldebt() + debtObjectList.get(0).getMoneyPay() + debtObjectList.get(0).getMoneyReceived()));
                                    EventBus.getDefault().post(new UpdateDebtEvent(debtObjectList.get(0)));
                                } catch (Exception e) {
                                }
                            }
                        }
                    });
                }
            }
        });
        btnLuuKhoanThu.setOnClickListener(v -> {
            if (!cetTienThu.getText().toString().isEmpty()) {
                if (debtObjectList.size() > 0) {
                    Common.createDialog(this, "Thêm thu chi", "Hãy xem lại số liệu bạn vừa nhập đã đúng chưa? Nếu Đóng thì chọn ĐỒNG Ý còn không thì chọn HỦY để thực hiện lại", new IClickListener() {
                        @Override
                        public void acceptEvent(boolean accept) {
                            if (accept) {
                                try {
                                    debtObjectList.get(0).setMoneyReceived(debtObjectList.get(0).getMoneyReceived() + cetTienThu.getNumericValue());
                                    daoSession.getDebtObjectDao().update(debtObjectList.get(0));
                                    cetTienThu.setText("");
                                    TableRow row = (TableRow) tbChiTietCN.getChildAt(1);
                                    TextView tvThu = row.findViewById(R.id.tvThu);
                                    TextView tvConLai = row.findViewById(R.id.tvConLai);
                                    tvThu.setText(Common.roundMoney(debtObjectList.get(0).getMoneyReceived()));
                                    tvConLai.setText(Common.roundMoney(debtObjectList.get(0).getExpenses() + debtObjectList.get(0).getOldebt() + debtObjectList.get(0).getMoneyPay() + debtObjectList.get(0).getMoneyReceived()));
                                    EventBus.getDefault().post(new UpdateDebtEvent(debtObjectList.get(0)));
                                } catch (Exception e) {
                                }
                            }
                        }
                    });
                }
            }
        });
        btnNhanTinChotCN.setOnClickListener(v -> {
            congNo(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM,this,daoSession,accountObject,startDate,endDate,true);
        });
    }


    public static StringBuilder congNo(int tienteType,Context context, DaoSession daoSession, AccountObject accountObject, long startDate, long endDate, boolean showDialog) {
        AccountSetting accountSetting = new Gson().fromJson(accountObject.getAccountSetting(), AccountSetting.class);

        BaoCongNo baoCongNoNhan = null;
        BaoCongNo baoCongNoChuyen = null;
        String donvi = TienTe.getKeyTienTe(tienteType);
        if (accountSetting.getKieubaocongno() == KieuBaoCongNo.BAO_NO_CU_DON_GIAN) {
            baoCongNoNhan = processLotoDetailDonGian(context, accountObject, daoSession, SmsStatus.SMS_RECEIVE,startDate,endDate,donvi);
            baoCongNoChuyen = processLotoDetailDonGian(context, accountObject, daoSession, SmsStatus.SMS_SENT,startDate,endDate,donvi);
        } else {
            baoCongNoNhan = processLotoDetail(context, SmsStatus.SMS_RECEIVE, accountObject, daoSession,startDate,endDate,donvi);
            baoCongNoChuyen = processLotoDetail(context, SmsStatus.SMS_SENT, accountObject, daoSession,startDate,endDate,donvi);
        }
        double tong = 0;

        String date = "Ngay: " + Common.convertDateByFormat(startDate, Common.FORMAT_DATE_DD_MM_YYY_2) + "";
        StringBuilder stringBuilderSend = new StringBuilder(date).append("\n");
        if (baoCongNoNhan != null) {
            if (baoCongNoNhan.isHasNumber()) {
                stringBuilderSend.append(baoCongNoNhan.getContent()).append("\n");
                tong += baoCongNoNhan.getTong() * (-1);
            }

        }
        if (baoCongNoChuyen != null) {
            if (baoCongNoChuyen.isHasNumber()) {
                stringBuilderSend.append(baoCongNoChuyen.getContent()).append("\n");
                tong += baoCongNoChuyen.getTong();
            }

        }


        if (accountSetting.getKieubaocongno() == KieuBaoCongNo.BAO_CONG_NO_HOM_NAY) {
            stringBuilderSend.append("Tong: " + Common.formatMoney(tong) + TienTe.getKeyTienTe(tienteType));
        } else if (accountSetting.getKieubaocongno() == KieuBaoCongNo.BAO_NO_CU_CHI_TIET || accountSetting.getKieubaocongno() == KieuBaoCongNo.BAO_NO_CU_DON_GIAN) {
            if (tong > 0) {
                stringBuilderSend.append("Hom nay " + Common.capitalize(accountObject.getYouCallThem()) + "(duong) +" + Common.formatMoney(tong) + "" +
                        donvi);
            } else {
                stringBuilderSend.append("Hom nay " + Common.capitalize(accountObject.getYouCallThem()) + "(am) " + Common.formatMoney(tong) + donvi);
            }
            List<DebtObject> debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()), DebtObjectDao.Properties.Date.between(startDate, endDate)).list();
            if (debtObjectList.size() > 0) {
                double cu =debtObjectList.get(0).getOldebt()+debtObjectList.get(0).getMoneyReceived()+debtObjectList.get(0).getMoneyPay();
                stringBuilderSend.append("\n").append("No cu: " + Common.formatMoney(cu) + donvi).append("\n");
                double total = tong + debtObjectList.get(0).getOldebt()+ debtObjectList.get(0).getMoneyPay()+debtObjectList.get(0).getMoneyReceived();

                if (cu>0){
                    stringBuilderSend.append("Tong: " + Common.formatMoney(tong) +  " +(" + Common.formatMoney(cu) + ")= " + Common.formatMoney(total) + donvi);
                }
                else{
                    stringBuilderSend.append("Tong: " + Common.formatMoney(tong) +  " +(" + Common.formatMoney(cu) + ")= " + Common.formatMoney(total) + donvi);
                }
            }
        }

        if (showDialog) {
            int smsType = SmsType.SMS_NORMAL;
            if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_FB) {
                smsType = SmsType.SMS_FB;
            } else if (accountObject.getAccountStatus() == AccountStatus.ACCOUNT_ZALO) {
                smsType = SmsType.SMS_ZALO;
            }
            SmsObject smsObject = new SmsObject(accountObject.getIdAccount(), stringBuilderSend.toString().trim(), stringBuilderSend.toString().trim(), smsType, accountObject.getIdAccount(), Common.getCurrentTimeLong(), SmsStatus.SMS_SENT);
            CustomDialog customDialog = new CustomDialog(context, smsObject, accountObject);
            customDialog.show();
        }
        return stringBuilderSend;
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        startDate = bundle.getLong(START_DATE_KEY);
        endDate = bundle.getLong(END_DATE_KEY);
        idAccount = bundle.getString(ACCOUNT_ID_KEY);
        accountName = bundle.getString(ACCOUNT_NAME_KEY);
        tvAccountName.setText(getString(R.string.tv_account_format, accountName));
        List<AccountObject> accountObjectList = daoSession.getAccountObjectDao().queryBuilder().where(AccountObjectDao.Properties.IdAccount.eq(idAccount)).list();
        if (accountObjectList.size() > 0) {
            accountObject = accountObjectList.get(0);
        }
        getData();
    }

    public static BaoCongNo processLotoDetail(Context context, int smsStatus, AccountObject accountObject, DaoSession daoSession,long startDate,long endDate,String donvi) {
        int typeTienTe = Common.getTypeTienTe(context);
        double donviTien;
        if (typeTienTe==TienTe.TIEN_VIETNAM){
            donviTien=0.001;
        }
        else{
            donviTien=1;
        }
        if (accountObject != null) {
            AccountSetting accountSetting= new Gson().fromJson(accountObject.getAccountSetting(),AccountSetting.class);
            AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
            String title = "";
            if (smsStatus == SmsStatus.SMS_RECEIVE) {
                title = "- " + Common.capitalize(accountObject.getThemCallYou()) + " nhan cua " + Common.capitalize(accountObject.getYouCallThem()) + "";
            } else {
                title = "- " + Common.capitalize(accountObject.getThemCallYou()) + " chuyen cho " + Common.capitalize(accountObject.getYouCallThem()) + "";
            }


            List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount()), LotoNumberObjectDao.Properties.SmsStatus.eq(smsStatus), LotoNumberObjectDao.Properties.DateTake.between(startDate, endDate)).list();
            //-- Đề
            double deTongDanh = 0;
            double deTongTrung = 0;
            double tienDeTrung = 0;

            //--Lô
            double loTongDanh = 0;
            double loTongTrung = 0;
            double tienLoTrung = 0;
            // 3C
            double baCTongDanh = 0;
            double baCTongTrung = 0;
            double tienBaCTrung = 0;
            // Giải nhất
            double giaiNhatTongDanh = 0;
            double giaiNhatTongTrung = 0;
            double tienGiaiNhatTrung = 0;
            // Càng giữa
            double cangGiuaTongDanh = 0;
            double cangGiuaTongTrung = 0;
            double tienCangGiuaTrung = 0;
            // Xien 2
            double x2TongDanh = 0;
            double x2TongTrung = 0;
            double tienX2Trung = 0;
            // Xien 3
            double x3TongDanh = 0;
            double x3TongTrung = 0;
            double tienX3Trung = 0;
            // Xien 4
            double x4TongDanh = 0;
            double x4TongTrung = 0;
            double tienX4Trung = 0;
            // dau db
            double dauDBTongDanh = 0;
            double dauDBTrung = 0;
            double tienDauDBTrung = 0;
            // dau G1
            double dauG1TongDanh = 0;
            double dauG1Trung = 0;
            double tienDauG1Trung = 0;

            for (LotoNumberObject lotoNumberObject : lotoNumberObjects) {


                switch (lotoNumberObject.getType()) {
                    case TypeEnum.TYPE_DE:
                        deTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            deTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DAUDB:
                        dauDBTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            dauDBTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_LO:
                        loTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            loTongTrung += lotoNumberObject.getMoneyTake() * (lotoNumberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) != 0 ? lotoNumberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) : 1);
                        }
                        break;
                    case TypeEnum.TYPE_3C:
                        baCTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            baCTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DITNHAT:
                        giaiNhatTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            giaiNhatTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DAUNHAT:
                        dauG1TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            dauG1TongDanh += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_CANGGIUA:
                        cangGiuaTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            cangGiuaTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN2:
                    case TypeEnum.TYPE_XIENGHEP2:
                        x2TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x2TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN3:
                    case TypeEnum.TYPE_XIENGHEP3:
                        x3TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x3TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN4:
                    case TypeEnum.TYPE_XIENGHEP4:
                        x4TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x4TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                }
            }

            // Thành tiền
            StringBuilder stringBuilderThanhTien = new StringBuilder(title).append("\n");
            // Tiền trúng
            StringBuilder stringBuilderTrung = new StringBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            double tongThanhTien = 0;
            if (deTongDanh > 0) {
                stringBuilder.append(context.getString(R.string.tv_de_detail, "De", Common.formatMoney(deTongTrung), Common.formatMoney(deTongDanh), donvi));
                double thanhTien = deTongDanh *  DocumentFragment.formatRate(accountRate.getDe_danh()) *donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "De", Common.formatMoney(deTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
            }
            if (loTongDanh > 0) {
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Lo", Common.formatMoney(loTongTrung), String.valueOf(Common.formatMoney(loTongDanh)), "d"));
                double thanhTien = loTongDanh * accountRate.getLo_danh() *donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Lo", Common.formatMoney(loTongDanh), "d", Common.roundMoney(thanhTien), donvi)).append("\n");
            }
            if (giaiNhatTongDanh > 0) {
                double thanhTien = giaiNhatTongDanh *  DocumentFragment.formatRate(accountRate.getGiainhat_danh()) *donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Duoi G1", String.valueOf(giaiNhatTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Duoi G1", String.valueOf(Common.formatMoney(giaiNhatTongTrung)), String.valueOf(Common.formatMoney(giaiNhatTongDanh)), donvi));
            }
            if (dauG1TongDanh > 0) {
                double thanhTien = dauG1TongDanh *  DocumentFragment.formatRate(accountRate.getGiainhat_danh()) *donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Dau G1", String.valueOf(dauG1TongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Dau G1", String.valueOf(Common.formatMoney(dauG1Trung)), String.valueOf(Common.formatMoney(dauG1TongDanh)), donvi));
            }
            if (dauDBTongDanh > 0) {
                double thanhTien = dauDBTongDanh *  DocumentFragment.formatRate(accountRate.getDe_danh()) *donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Dau DB", String.valueOf(dauDBTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Dau DB", String.valueOf(Common.formatMoney(dauDBTrung)), String.valueOf(Common.formatMoney(dauDBTongDanh)), donvi));
            }
            if (baCTongDanh > 0) {
                double thanhTien = baCTongDanh *  DocumentFragment.formatRate(accountRate.getBacang_danh()) *donviTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "3Cang", Common.roundMoney(baCTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "3Cang", Common.formatMoney(baCTongTrung), String.valueOf(Common.formatMoney(baCTongDanh)), donvi));
            }
            if (cangGiuaTongDanh > 0) {
                double thanhTien = cangGiuaTongDanh *  DocumentFragment.formatRate(accountRate.getBacang_danh())*donviTien;
                tongThanhTien += thanhTien;
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Cang", String.valueOf(cangGiuaTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Cang", String.valueOf(Common.formatMoney(cangGiuaTongTrung)), String.valueOf(Common.formatMoney(cangGiuaTongDanh)), donvi));
            }

            double tongXienDanh = 0;
            double tongXienThanhTien = 0;
            double tongXienTrung = x2TongTrung + x3TongTrung + x4TongTrung;
            if (x2TongDanh > 0) {
                tongXienDanh += x2TongDanh;
                double thanhTienX2 = x2TongDanh *  DocumentFragment.formatRate(accountRate.getXien2_danh()) *donviTien;
                tongThanhTien += thanhTienX2;
                tongXienThanhTien += thanhTienX2;
            }
            if (x3TongDanh > 0) {
                tongXienDanh += x3TongDanh;
                double thanhTienX3 = x3TongDanh *  DocumentFragment.formatRate(accountRate.getXien3_danh())*donviTien;
                tongThanhTien += thanhTienX3;
                tongXienThanhTien += thanhTienX3;
            }
            if (x4TongDanh > 0) {
                tongXienDanh += x4TongDanh;
                double thanhTienX4 = x3TongDanh *  DocumentFragment.formatRate(accountRate.getXien4_danh()) *donviTien;
                tongThanhTien += thanhTienX4;
                tongXienThanhTien += thanhTienX4;
            }
            if (tongXienDanh > 0) {
                stringBuilderThanhTien.append(context.getString(R.string.tv_de_thanhTien, "Xien", String.valueOf(Common.roundMoney(tongXienDanh)), donvi, String.valueOf(Common.roundMoney(tongXienThanhTien)), donvi)).append("\n");
                stringBuilder.append(context.getString(R.string.tv_de_detail, "Xien", String.valueOf(Common.roundMoney(tongXienTrung)), String.valueOf(Common.roundMoney(tongXienDanh)), donvi));
            }


//-------------------------TRUNG-------------------------------------
            if (deTongTrung > 0) {
                tienDeTrung = deTongTrung * accountRate.getDe_an() *donviTien;
                ;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "de",Common.roundMoney(deTongTrung), donvi, Common.roundMoney(tienDeTrung), donvi)).append("\n");
            }
            if (loTongTrung > 0) {
                tienLoTrung = loTongTrung * accountRate.getLo_an() *donviTien;
                ;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "lo", Common.roundMoney(loTongTrung), "d", Common.roundMoney(tienLoTrung), donvi)).append("\n");
            }
            if (giaiNhatTongDanh > 0) {
                tienGiaiNhatTrung = giaiNhatTongDanh * accountRate.getGiainhat_an()*donviTien;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "duoi G1", Common.roundMoney(giaiNhatTongDanh), donvi, Common.roundMoney(tienGiaiNhatTrung), donvi)).append("\n");
            }
            if (dauG1Trung > 0) {
                tienDauG1Trung = dauG1Trung * accountRate.getGiainhat_an() *donviTien;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "dau G1",Common.roundMoney(dauG1Trung), donvi, Common.roundMoney(tienDauG1Trung), donvi)).append("\n");
            }
            if (dauDBTrung > 0) {
                tienDauDBTrung = dauDBTrung * accountRate.getDe_an() *donviTien;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "dau BD", Common.roundMoney(dauDBTrung), donvi, Common.roundMoney(tienDauDBTrung), donvi)).append("\n");
            }
            if (baCTongTrung > 0) {
                tienBaCTrung = baCTongTrung * accountRate.getBacang_an() *donviTien;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "3C", Common.roundMoney(baCTongTrung), donvi, Common.roundMoney(tienBaCTrung), donvi)).append("\n");
            }
            if (cangGiuaTongTrung > 0) {
                tienCangGiuaTrung = cangGiuaTongTrung * accountRate.getBacang_an() *donviTien;
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "cang",Common.roundMoney(cangGiuaTongTrung), donvi, Common.roundMoney(tienCangGiuaTrung), donvi)).append("\n");
            }

            double tienXienTrungThanhTien = 0;
            if (x2TongTrung > 0) {
                double tienTrung = x2TongTrung * accountRate.getXien2_an() *donviTien;
                tienXienTrungThanhTien += tienTrung;
            }
            if (x3TongTrung > 0) {
                double tienTrung = x3TongTrung * accountRate.getXien3_an() *donviTien;
                tienXienTrungThanhTien += tienTrung;
            }
            if (x4TongTrung > 0) {
                double tienTrung = x4TongTrung * accountRate.getXien4_an() *donviTien;
                tienXienTrungThanhTien += tienTrung;
            }
            if (tienXienTrungThanhTien > 0) {
                stringBuilderTrung.append(context.getString(R.string.tv_de_trung, "xien",Common.roundMoney(tongXienTrung), donvi, Common.roundMoney(tienXienTrungThanhTien), donvi)).append("\n");
            }

            StringBuilder stringBuilderTong = new StringBuilder();
            double tong = tongThanhTien - (tienDeTrung + tienLoTrung + tienBaCTrung + tienXienTrungThanhTien + tienGiaiNhatTrung + tienDauDBTrung + tienDauG1Trung + tienCangGiuaTrung);
            stringBuilderTong.append("Tổng: ").append(Common.roundMoney(tong)).append(donvi);
            BaoCongNo baoCongNo = new BaoCongNo(tong, new StringBuilder(stringBuilderThanhTien.append(stringBuilderTrung)).toString(), lotoNumberObjects.size() > 0);

            return baoCongNo;

        }
        return null;
    }

    public static BaoCongNo processLotoDetailDonGian(Context context, AccountObject accountObject, DaoSession daoSession, int smsStatus,long startDate,long endDate,String donvi) {
        int typeTienTe = Common.getTypeTienTe(context);
        double donviTien;
        if (typeTienTe==TienTe.TIEN_VIETNAM){
            donviTien=0.001;
        }
        else{
            donviTien=1;
        }
        if (accountObject != null) {
            AccountSetting accountSetting= new Gson().fromJson(accountObject.getAccountSetting(),AccountSetting.class);
            AccountRate accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
            String title = "";
            if (smsStatus == SmsStatus.SMS_RECEIVE) {
                title = "- " + Common.capitalize(accountObject.getThemCallYou()) + " nhan cua " + Common.capitalize(accountObject.getYouCallThem()) + "";
            } else {
                title = "- " + Common.capitalize(accountObject.getThemCallYou()) + " chuyen cho " + Common.capitalize(accountObject.getYouCallThem()) + "";
            }


            List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.AccountSend.eq(accountObject.getIdAccount()), LotoNumberObjectDao.Properties.SmsStatus.eq(smsStatus), LotoNumberObjectDao.Properties.DateTake.between(startDate, endDate)).list();
            //-- Đề
            double deTongDanh = 0;
            double deTongTrung = 0;
            double tienDeTrung = 0;

            //--Lô
            double loTongDanh = 0;
            double loTongTrung = 0;
            double tienLoTrung = 0;
            // 3C
            double baCTongDanh = 0;
            double baCTongTrung = 0;
            double tienBaCTrung = 0;
            // Giải nhất
            double giaiNhatTongDanh = 0;
            double giaiNhatTongTrung = 0;
            double tienGiaiNhatTrung = 0;
            // Càng giữa
            double cangGiuaTongDanh = 0;
            double cangGiuaTongTrung = 0;
            double tienCangGiuaTrung = 0;
            // Xien 2
            double x2TongDanh = 0;
            double x2TongTrung = 0;
            double tienX2Trung = 0;
            // Xien 3
            double x3TongDanh = 0;
            double x3TongTrung = 0;
            double tienX3Trung = 0;
            // Xien 4
            double x4TongDanh = 0;
            double x4TongTrung = 0;
            double tienX4Trung = 0;
            // dau db
            double dauDBTongDanh = 0;
            double dauDBTrung = 0;
            double tienDauDBTrung = 0;
            // dau G1
            double dauG1TongDanh = 0;
            double dauG1Trung = 0;
            double tienDauG1Trung = 0;

            for (LotoNumberObject lotoNumberObject : lotoNumberObjects) {


                switch (lotoNumberObject.getType()) {
                    case TypeEnum.TYPE_DE:
                        deTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            deTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DAUDB:
                        dauDBTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            dauDBTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_LO:
                        loTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            loTongTrung += lotoNumberObject.getMoneyTake() * (lotoNumberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) != 0 ? lotoNumberObject.getNhay(accountSetting.getSonhaylotrathuongtoida()) : 1);
                        }
                        break;
                    case TypeEnum.TYPE_3C:
                        baCTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            baCTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DITNHAT:
                        giaiNhatTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            giaiNhatTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_DAUNHAT:
                        dauG1TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            dauG1TongDanh += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_CANGGIUA:
                        cangGiuaTongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            cangGiuaTongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN2:
                    case TypeEnum.TYPE_XIENGHEP2:
                        x2TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x2TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN3:
                    case TypeEnum.TYPE_XIENGHEP3:
                        x3TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x3TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                    case TypeEnum.TYPE_XIEN4:
                    case TypeEnum.TYPE_XIENGHEP4:
                        x4TongDanh += lotoNumberObject.getMoneyTake();
                        if (lotoNumberObject.isHit()) {
                            x4TongTrung += lotoNumberObject.getMoneyTake();
                        }
                        break;
                }
            }


            StringBuilder stringBuilder = new StringBuilder(title).append("\n");
            double tongThanhTien = 0;
            if (deTongDanh > 0) {
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "De", Common.formatMoney(deTongDanh), donvi, Common.formatMoney(deTongTrung), donvi)).append("\n");
                double thanhTien = deTongDanh * DocumentFragment.formatRate(accountRate.getDe_danh()) * donviTien;
                tongThanhTien += thanhTien;
            }
            if (loTongDanh > 0) {
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Lo", Common.formatMoney(loTongDanh), "d", Common.formatMoney(loTongTrung), "d")).append("\n");
                double thanhTien = loTongDanh * accountRate.getLo_danh() * donviTien;
                tongThanhTien += thanhTien;

            }
            if (giaiNhatTongDanh > 0) {
                double thanhTien = giaiNhatTongDanh *  DocumentFragment.formatRate(accountRate.getGiainhat_danh())  * donviTien;
                tongThanhTien += thanhTien;

                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Duoi G1", Common.formatMoney(giaiNhatTongDanh), donvi, Common.formatMoney(giaiNhatTongTrung), donvi)).append("\n");
            }
            if (dauG1TongDanh > 0) {
                double thanhTien = dauG1TongDanh * DocumentFragment.formatRate(accountRate.getGiainhat_danh())  * donviTien;
                tongThanhTien += thanhTien;
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Dau G1", Common.formatMoney(dauG1TongDanh), donvi, Common.formatMoney(dauG1Trung), donvi)).append("\n");
            }
            if (dauDBTongDanh > 0) {
                double thanhTien = dauDBTongDanh * DocumentFragment.formatRate(accountRate.getDe_danh()) * donviTien;
                tongThanhTien += thanhTien;
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Dau DB", Common.formatMoney(dauDBTongDanh), donvi, Common.formatMoney(dauDBTrung), donvi)).append("\n");
            }
            if (baCTongDanh > 0) {
                double thanhTien = baCTongDanh * DocumentFragment.formatRate(accountRate.getBacang_danh())  * donviTien;
                tongThanhTien += thanhTien;
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "3Cang", Common.formatMoney(baCTongDanh), donvi, Common.formatMoney(baCTongTrung), donvi)).append("\n");
            }
            if (cangGiuaTongDanh > 0) {
                double thanhTien = cangGiuaTongDanh * DocumentFragment.formatRate(accountRate.getBacang_danh()) * donviTien;
                tongThanhTien += thanhTien;
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Cang", Common.formatMoney(cangGiuaTongDanh), donvi, Common.formatMoney(cangGiuaTongTrung), donvi)).append("\n");
            }

            double tongXienDanh = 0;
            double tongXienThanhTien = 0;
            double tongXienTrung = x2TongTrung + x3TongTrung + x4TongTrung;
            if (x2TongDanh > 0) {
                tongXienDanh += x2TongDanh;
                double thanhTienX2 = x2TongDanh *  DocumentFragment.formatRate(accountRate.getXien2_danh())  * donviTien;
                tongThanhTien += thanhTienX2;
                tongXienThanhTien += thanhTienX2;
            }
            if (x3TongDanh > 0) {
                tongXienDanh += x3TongDanh;
                double thanhTienX3 = x3TongDanh *  DocumentFragment.formatRate(accountRate.getXien3_danh())  * donviTien;
                tongThanhTien += thanhTienX3;
                tongXienThanhTien += thanhTienX3;
            }
            if (x4TongDanh > 0) {
                tongXienDanh += x4TongDanh;
                double thanhTienX4 = x3TongDanh *  DocumentFragment.formatRate(accountRate.getXien4_danh())  * donviTien;
                tongThanhTien += thanhTienX4;
                tongXienThanhTien += thanhTienX4;
            }
            if (tongXienDanh > 0) {
                stringBuilder.append(context.getString(R.string.tv_de_don_gian, "Xien", Common.formatMoney(tongXienDanh), donvi, Common.formatMoney(tongXienTrung), donvi)).append("\n");
            }


//-------------------------TRUNG-------------------------------------
            if (deTongTrung > 0) {
                tienDeTrung = deTongTrung * accountRate.getDe_an() * donviTien;

            }
            if (loTongTrung > 0) {
                tienLoTrung = loTongTrung * accountRate.getLo_an()  * donviTien;
                ;

            }
            if (giaiNhatTongDanh > 0) {
                tienGiaiNhatTrung = giaiNhatTongDanh * accountRate.getGiainhat_an()  * donviTien;

            }
            if (dauG1Trung > 0) {
                tienDauG1Trung = dauG1Trung * accountRate.getGiainhat_an()  * donviTien;

            }
            if (dauDBTrung > 0) {
                tienDauDBTrung = dauDBTrung * accountRate.getDe_an() * donviTien;

            }
            if (baCTongTrung > 0) {
                tienBaCTrung = baCTongTrung * accountRate.getBacang_an()  * donviTien;

            }
            if (cangGiuaTongTrung > 0) {
                tienCangGiuaTrung = cangGiuaTongTrung * accountRate.getBacang_an()  * donviTien;

            }

            double tienXienTrungThanhTien = 0;
            if (x2TongTrung > 0) {
                double tienTrung = x2TongTrung * accountRate.getXien2_an()  * donviTien;
                tienXienTrungThanhTien += tienTrung;
            }
            if (x3TongTrung > 0) {
                double tienTrung = x3TongTrung * accountRate.getXien3_an()  * donviTien;
                tienXienTrungThanhTien += tienTrung;
            }
            if (x4TongTrung > 0) {
                double tienTrung = x4TongTrung * accountRate.getXien4_an()  * donviTien;
                tienXienTrungThanhTien += tienTrung;
            }


            StringBuilder stringBuilderTong = new StringBuilder();
            double tong = tongThanhTien - (tienDeTrung + tienLoTrung + tienBaCTrung + tienXienTrungThanhTien + tienGiaiNhatTrung + tienDauDBTrung + tienDauG1Trung + tienCangGiuaTrung);
            stringBuilderTong.append("Tổng: ").append(Common.roundMoney(tong)).append(donvi);
            BaoCongNo baoCongNo = new BaoCongNo(tong, stringBuilder.toString(), lotoNumberObjects.size() > 0);

            return baoCongNo;

        }
        return null;
    }

    private void getData() {
        debtObjectList = daoSession.getDebtObjectDao().queryBuilder().where(DebtObjectDao.Properties.IdAccouunt.eq(idAccount), DebtObjectDao.Properties.Date.between(0, endDate)).orderDesc(DebtObjectDao.Properties.Date).list();
        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_cong_no_cua_khach_hang_title, null, false);
        tbChiTietCN.addView(tableRowTitle);
        for (DebtObject object : debtObjectList) {
            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_cong_no_cua_khach_hang_row, null, false);
            TextView tvDate = tableRow.findViewById(R.id.tvDate);
            TextView tvNoCu = tableRow.findViewById(R.id.tvNoCu);
            TextView tvThu = tableRow.findViewById(R.id.tvThu);
            TextView tvChi = tableRow.findViewById(R.id.tvChi);
            TextView tvPhatSinh = tableRow.findViewById(R.id.tvPhatSinh);
            TextView tvConLai = tableRow.findViewById(R.id.tvConLai);
            tvDate.setText(Common.convertDateByFormat(object.getDate(), Common.FORMAT_DATE_DD_MM_YYY_2));
            tvNoCu.setText(Common.roundMoney(object.getOldebt()));
            tvChi.setText(Common.roundMoney(object.getMoneyPay()));
            tvThu.setText(Common.roundMoney(object.getMoneyReceived()));
            tvPhatSinh.setText(Common.roundMoney(object.getExpenses()));
            tvConLai.setText(Common.roundMoney(object.getExpenses() + object.getOldebt() + object.getMoneyPay() + object.getMoneyReceived()));
            tbChiTietCN.addView(tableRow);
        }
    }
}
