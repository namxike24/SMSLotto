package com.smsanalytic.lotto.ui.smsDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.database.SmsObject;
import com.smsanalytic.lotto.database.SmsObjectDao;
import com.smsanalytic.lotto.entities.AccountRate;
import com.smsanalytic.lotto.entities.AccountSetting;
import com.smsanalytic.lotto.interfaces.IClickListener;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.model.sms.LotoHitDetail;
import com.smsanalytic.lotto.ui.document.DocumentFragment;
import com.smsanalytic.lotto.ui.smsProcess.adapter.SmsEmptyAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class SmsDetailOfAccountActivity extends AppCompatActivity implements IClickListener {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.rvSmsDetailOfAccount)
    RecyclerView rvSmsDetailOfAccount;
    private DaoSession daoSession;
    private List<SmsObject> smsObjectList;
    private SmsEmptyAdapter emptyAdapter;
    private AccountObject accountObject;
    private AccountRate accountRate;
    private AccountSetting accountSetting;
    private long dateStart;
    private long dateEnd;
    public static final String ACCOUNT_ID_KEY = "accountidkey";
    public static final String DATE_START_KEY = "dateStartKey";
    public static final String DATE_END_KEY = "dateEndKey";
    private SettingDefault settingDefault;
    private String donvi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail_of_account);
        ButterKnife.bind(this);
        getSettingData();
        getBundle();
        initAdapter();
        btnBack.setOnClickListener(v -> finish());
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            accountObject = new Gson().fromJson(bundle.getString(ACCOUNT_ID_KEY), AccountObject.class);
            accountRate = new Gson().fromJson(accountObject.getAccountRate(), AccountRate.class);
            accountSetting= new Gson().fromJson(accountObject.getAccountSetting(),AccountSetting.class);
            dateStart = bundle.getLong(DATE_START_KEY);
            dateEnd = bundle.getLong(DATE_END_KEY);
            tvGroupName.setText(accountObject.getAccountName());
        }
    }

    private void getSettingData(){
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi= TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
    }

    private void initAdapter() {
        daoSession = ((MyApp) getApplication()).getDaoSession();
        smsObjectList = new ArrayList<>();
        emptyAdapter = new SmsEmptyAdapter(smsObjectList, this, SmsEmptyAdapter.SMS_DETAIL_TYPE);
        emptyAdapter.setiClickListenerl(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSmsDetailOfAccount.setAdapter(emptyAdapter);
        rvSmsDetailOfAccount.setLayoutManager(layoutManager);
        initData();
    }

    private void initData() {
        List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where( LotoNumberObjectDao.Properties.DateTake.between(dateStart, dateEnd)).list();
        smsObjectList.clear();
        List<SmsObject> list = daoSession.getSmsObjectDao().queryBuilder().where(SmsObjectDao.Properties.IdAccouunt.eq(accountObject.getIdAccount()),
                SmsObjectDao.Properties.Date.between(dateStart, dateEnd)).list();
        for (SmsObject smsObject : list) {
            if (smsObject.isSuccess()) {
                processLotoDetail(smsObject);
            }
        }
        smsObjectList.addAll(list);
        emptyAdapter.notifyDataSetChanged();
    }

    private void processLotoDetail(SmsObject smsObject) {
        double doiTienTe=1;
        if (settingDefault!=null){
            if (settingDefault.getTiente()==TienTe.TIEN_VIETNAM){
                doiTienTe=0.001;
            }
        }
        List<LotoNumberObject> lotoNumberObjects = daoSession.getLotoNumberObjectDao().queryBuilder().where(LotoNumberObjectDao.Properties.Guid.eq(smsObject.getGuid()), LotoNumberObjectDao.Properties.DateTake.between(dateStart, dateEnd)).list();
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
        StringBuilder stringBuilderThanhTien = new StringBuilder();
        // Tiền trúng
        StringBuilder stringBuilderTrung = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        double tongThanhTien=0;
        if (deTongDanh > 0) {
            stringBuilder.append(getString(R.string.tv_de_detail, "De",Common.formatMoney(deTongTrung), String.valueOf(Common.formatMoney(deTongDanh)), donvi));
            double thanhTien = deTongDanh * DocumentFragment.formatRate(accountRate.getDe_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "De", Common.formatMoney(deTongDanh),donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
        }
        if (loTongDanh > 0) {
            stringBuilder.append(getString(R.string.tv_de_detail, "Lo", Common.formatMoney(loTongTrung), String.valueOf(Common.formatMoney(loTongDanh)), "d"));
            double thanhTien = loTongDanh * accountRate.getLo_danh();
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Lo", Common.formatMoney(loTongDanh), "d",Common.roundMoney(thanhTien), donvi)).append("\n");
        }
        if (giaiNhatTongDanh > 0) {
            double thanhTien = giaiNhatTongDanh * DocumentFragment.formatRate(accountRate.getGiainhat_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Duoi G1", Common.formatMoney(giaiNhatTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "Duoi G1", Common.formatMoney(giaiNhatTongTrung), Common.formatMoney(giaiNhatTongDanh), donvi));
        }
        if (dauG1TongDanh > 0) {
            double thanhTien =dauG1TongDanh * DocumentFragment.formatRate(accountRate.getGiainhat_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Dau G1", Common.formatMoney(dauG1TongDanh), donvi, Common.roundMoney(thanhTien),donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "Dau G1", Common.formatMoney(dauG1Trung), Common.formatMoney(dauG1TongDanh),donvi));
        }
        if (dauDBTongDanh > 0) {
            double thanhTien = dauDBTongDanh * DocumentFragment.formatRate(accountRate.getDe_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Dau DB",Common.formatMoney(dauDBTongDanh),donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "Dau DB",Common.formatMoney(dauDBTrung), String.valueOf(Common.formatMoney(dauDBTongDanh)), donvi));
        }
        if (baCTongDanh > 0) {
            double thanhTien = baCTongDanh * DocumentFragment.formatRate(accountRate.getBacang_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "3Cang", Common.formatMoney(baCTongDanh), donvi, Common.roundMoney(thanhTien), donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "3Cang", Common.formatMoney(baCTongTrung), String.valueOf(Common.formatMoney(baCTongDanh)), donvi));
        }
        if (cangGiuaTongDanh > 0) {
            double thanhTien = cangGiuaTongDanh * DocumentFragment.formatRate(accountRate.getBacang_danh());
            tongThanhTien+=thanhTien;
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Cang", Common.formatMoney(cangGiuaTongDanh), donvi,Common.roundMoney(thanhTien),donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "Cang",Common.formatMoney(cangGiuaTongTrung), String.valueOf(Common.formatMoney(cangGiuaTongDanh)),donvi));
        }

        double tongXienDanh = 0;
        double tongXienThanhTien = 0;
        double tongXienTrung = x2TongTrung + x3TongTrung + x4TongTrung;
        if (x2TongDanh > 0) {
            tongXienDanh += x2TongDanh;
            double thanhTienX2 = x2TongDanh * DocumentFragment.formatRate(accountRate.getXien2_danh());
            tongThanhTien+=thanhTienX2;
            tongXienThanhTien += thanhTienX2;
        }
        if (x3TongDanh > 0) {
            tongXienDanh += x3TongDanh;
            double thanhTienX3 = x3TongDanh * DocumentFragment.formatRate(accountRate.getXien3_danh());
            tongThanhTien+=thanhTienX3;
            tongXienThanhTien += thanhTienX3;
        }
        if (x4TongDanh > 0) {
            tongXienDanh += x4TongDanh;
            double thanhTienX4 = x3TongDanh * DocumentFragment.formatRate(accountRate.getXien4_danh());
            tongThanhTien+=thanhTienX4;
            tongXienThanhTien += thanhTienX4;
        }
        if (tongXienDanh > 0) {
            stringBuilderThanhTien.append(getString(R.string.tv_de_thanhTien, "Xien",Common.roundMoney(tongXienDanh), donvi,Common.roundMoney(tongXienThanhTien),donvi)).append("\n");
            stringBuilder.append(getString(R.string.tv_de_detail, "Xien",Common.roundMoney(tongXienTrung),Common.roundMoney(tongXienDanh),donvi));
        }


//-------------------------TRUNG-------------------------------------
        if (deTongTrung > 0) {
            tienDeTrung = deTongTrung * accountRate.getDe_an()*doiTienTe;;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "de", Common.formatMoney(deTongDanh),donvi, Common.roundMoney(tienDeTrung),donvi)).append("\n");
        }
        if (loTongTrung > 0) {
            tienLoTrung = loTongTrung * accountRate.getLo_an()*doiTienTe;;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "lo", Common.formatMoney(loTongTrung), "d", Common.roundMoney(tienLoTrung), donvi)).append("\n");
        }
        if (giaiNhatTongDanh > 0) {
            tienGiaiNhatTrung = giaiNhatTongDanh * accountRate.getGiainhat_an()*doiTienTe;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "duoi G1", Common.formatMoney(giaiNhatTongDanh),donvi, Common.roundMoney(tienGiaiNhatTrung), donvi)).append("\n");
        }
        if (dauG1Trung > 0) {
            tienDauG1Trung = dauG1Trung * accountRate.getGiainhat_an()*doiTienTe;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "dau G1", Common.formatMoney(dauG1Trung), donvi, Common.roundMoney(tienDauG1Trung),donvi)).append("\n");
        }
        if (dauDBTrung > 0) {
            tienDauDBTrung = dauDBTrung * accountRate.getDe_an()*doiTienTe;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "dau BD", Common.formatMoney(dauDBTrung), donvi, Common.roundMoney(tienDauDBTrung), donvi)).append("\n");
        }
        if (baCTongTrung > 0) {
            tienBaCTrung = baCTongTrung * accountRate.getBacang_an()*doiTienTe;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "3C", Common.formatMoney(baCTongTrung),donvi, Common.roundMoney(tienBaCTrung),donvi)).append("\n");
        }
        if (cangGiuaTongTrung > 0) {
            tienCangGiuaTrung = cangGiuaTongTrung * accountRate.getBacang_an()*doiTienTe;
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "cang", Common.formatMoney(cangGiuaTongTrung),donvi, Common.roundMoney(tienCangGiuaTrung),donvi)).append("\n");
        }

        double tienXienTrungThanhTien = 0;
        if (x2TongTrung > 0) {
            double tienTrung = x2TongTrung * accountRate.getXien2_an()*doiTienTe;
            tienXienTrungThanhTien += tienTrung;
        }
        if (x3TongTrung > 0) {
            double tienTrung = x3TongTrung * accountRate.getXien3_an()*doiTienTe;
            tienXienTrungThanhTien += tienTrung;
        }
        if (x4TongTrung > 0) {
            double tienTrung = x4TongTrung * accountRate.getXien4_an()*doiTienTe;
            tienXienTrungThanhTien += tienTrung;
        }
        if (tienXienTrungThanhTien > 0) {
            stringBuilderTrung.append(getString(R.string.tv_de_trung, "xien",Common.formatMoney(tongXienTrung),donvi, Common.formatMoney(tienXienTrungThanhTien), donvi)).append("\n");
        }

        StringBuilder stringBuilderTong = new StringBuilder();
        double tong =tongThanhTien - (tienDeTrung + tienLoTrung + tienBaCTrung + tienXienTrungThanhTien + tienGiaiNhatTrung + tienDauDBTrung + tienDauG1Trung + tienCangGiuaTrung);

        stringBuilderTong.append("Tổng: ").append(Common.roundMoney(tong)).append(donvi);
        LotoHitDetail lotoHitDetail = new LotoHitDetail();
        lotoHitDetail.setTitle(stringBuilder.toString());
        lotoHitDetail.setChiTiet(stringBuilderThanhTien.append(stringBuilderTrung).append(stringBuilderTong).toString());
        smsObject.setLotoHitDetail(new Gson().toJson(lotoHitDetail));
    }

    @Override
    public void clickEvent(View view, int p) {
        if (view.getId() == R.id.lnItem) {
            Intent intent = new Intent(this, HitDetailActivity.class);
            intent.putExtra(HitDetailActivity.SMS_KEY, new Gson().toJson(smsObjectList.get(p)));
            intent.putExtra(SmsDetailOfAccountActivity.DATE_START_KEY, dateStart);
            intent.putExtra(SmsDetailOfAccountActivity.DATE_END_KEY, dateEnd);
            startActivity(intent);
        }
    }
}
