package com.smsanalytic.lotto.ui.balance;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.AccountType;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.SmsStatus;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.AccountObject;
import com.smsanalytic.lotto.database.DaoSession;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.database.LotoNumberObjectDao;
import com.smsanalytic.lotto.entities.SmsSendObject;
import com.smsanalytic.lotto.model.canchuyen.GiuLaiLonNhatDefault;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.unit.PreferencesManager;

public class ChiTietCanChuyenActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.btnChuyen)
    TextView btnChuyen;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.tableDB)
    TableLayout tableDB;
    @BindView(R.id.rlDe)
    LinearLayout rlDe;
    @BindView(R.id.tableDuoiGiaiNhat)
    TableLayout tableDuoiGiaiNhat;
    @BindView(R.id.rlDuoiGiaiNhat)
    LinearLayout rlDuoiGiaiNhat;
    @BindView(R.id.tableDauGiaiNhat)
    TableLayout tableDauGiaiNhat;
    @BindView(R.id.rlDauGiaiNhat)
    LinearLayout rlDauGiaiNhat;
    @BindView(R.id.tableLoto)
    TableLayout tableLoto;
    @BindView(R.id.rlLoto)
    LinearLayout rlLoto;
    @BindView(R.id.tableBaSo)
    TableLayout tableBaSo;
    @BindView(R.id.rlBaSo)
    LinearLayout rlBaSo;
    @BindView(R.id.tableXien)
    TableLayout tableXien;
    @BindView(R.id.rlXien)
    LinearLayout rlXien;
    @BindView(R.id.baoCaoNhanhTable)
    TableLayout baoCaoNhanhTable;
    @BindView(R.id.nestScrollView)
    NestedScrollView nestScrollView;
    @BindView(R.id.tvError)
    TextView tvError;
    @BindView(R.id.tableDauDB)
    TableLayout tableDauDB;
    @BindView(R.id.rlDauDB)
    LinearLayout rlDauDB;
    private DaoSession daoSession;

    public static String GIULAILONNHAT_KEY = "giulailonnhat_key";
    public static String NGOAILE_KEY = "ngoaile_key";
    GiuLaiLonNhatDefault giuLaiLonNhatObject;
    List<LotoNumberObject> baoCaoNhanhList = new ArrayList<>();// danh sách hiển thị báo cáo nhanh
    List<LotoNumberObject> lotoNgoaiLe;
    HashMap<String, Double> lotoNgoaiLeHashMap = new HashMap<>();
    boolean visibleRowBaoCaoNhanh = false;
    boolean visibleXienRowChiTiet = false;
    List<SmsSendObject> smsSend = new ArrayList<>();
    // data setting
    SettingDefault settingDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_can_chuyen);
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        ButterKnife.bind(this);
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        getBundle();
        clickEvent();

    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(NGOAILE_KEY)) {
            String data = bundle.getString(NGOAILE_KEY);
            TypeToken<List<LotoNumberObject>> token = new TypeToken<List<LotoNumberObject>>() {
            };
            lotoNgoaiLe = new Gson().fromJson(data, token.getType());
            if (lotoNgoaiLe != null) {
                for (LotoNumberObject s : lotoNgoaiLe) {
                    if (s.getType() != TypeEnum.TYPE_XIEN2 && s.getType() != TypeEnum.TYPE_XIEN3 && s.getType() != TypeEnum.TYPE_XIEN4) {
                        lotoNgoaiLeHashMap.put(s.getValue1(), s.getMoneyTake());
                    } else {
                        lotoNgoaiLeHashMap.put(s.getXienFormat(), s.getMoneyTake());
                    }

                }
            }
        }
        if (bundle.containsKey(GIULAILONNHAT_KEY)) {
            giuLaiLonNhatObject = (GiuLaiLonNhatDefault) bundle.getSerializable(GIULAILONNHAT_KEY);
            if (giuLaiLonNhatObject != null) {
                if (giuLaiLonNhatObject.getDb()) {
                    initTableLotoByType(TypeEnum.TYPE_DE, rlDe, tableDB);
                    initTableLotoByType(TypeEnum.TYPE_DAUDB, rlDauDB, tableDauDB);
                }
                if (giuLaiLonNhatObject.getGiainhat()) {
                    initTableLotoByType(TypeEnum.TYPE_DAUNHAT, rlDauGiaiNhat, tableDauGiaiNhat);
                }
                if (giuLaiLonNhatObject.getGiainhat()) {
                    initTableLotoByType(TypeEnum.TYPE_DITNHAT, rlDuoiGiaiNhat, tableDuoiGiaiNhat);
                }
                if (giuLaiLonNhatObject.getBacang()) {
                    initTableLotoByType(TypeEnum.TYPE_3C, rlBaSo, tableBaSo);
                }
                if (giuLaiLonNhatObject.getLoto()) {
                    initTableLotoByType(TypeEnum.TYPE_LO, rlLoto, tableLoto);
                }
                if (giuLaiLonNhatObject.getXien()) {
                    initTableXien();
                }

                // khởi tạo dữ liệu báo cáo nhanh
                initBaoCaoNhanh();

//                if (visibleRowBaoCaoNhanh && visibleXienRowChiTiet) {
//                    nestScrollView.setVisibility(View.VISIBLE);
//                    tvError.setVisibility(View.GONE);
//                } else {
//                    nestScrollView.setVisibility(View.GONE);
//                    tvError.setVisibility(View.VISIBLE);
//                    String redString = getResources().getString(R.string.tv_khong_du_lieu_can_chuyen);
//                    tvError.setText(Html.fromHtml(redString));
//                }
            }
        }

    }

    private void initBaoCaoNhanh() {
        String donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
        String type = "";
        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_bao_cao_nhanh_title_table, null, false);
        baoCaoNhanhTable.addView(tableRowTitle);
        for (LotoNumberObject lotoItem : baoCaoNhanhList) {
            if (lotoItem.getType() == TypeEnum.TYPE_DE && giuLaiLonNhatObject.getDb()) {
                type = "Đề";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_LO && giuLaiLonNhatObject.getLoto()) {
                donvi = "d";
                type = "Lô";
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_3C && giuLaiLonNhatObject.getBacang()) {
                type = "Ba càng";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_XIEN2 && giuLaiLonNhatObject.getXien()) {
                type = "Xiên";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_DAUNHAT && giuLaiLonNhatObject.getGiainhat()) {
                type = "Đầu G1";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_DITNHAT && giuLaiLonNhatObject.getGiainhat()) {
                type = "Đuôi G1";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            } else if (lotoItem.getType() == TypeEnum.TYPE_DAUDB && giuLaiLonNhatObject.getDb()) {
                type = "Đầu ĐB";
                donvi = TienTe.getKeyTienTe(settingDefault.getTiente());
                visibleRowBaoCaoNhanh = true;
            }
            if (lotoItem.getMoneyTake() <= 0 || lotoItem.getSeChuyen() <= 0) {
                visibleRowBaoCaoNhanh = false;
            }
            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_bao_cao_nhanh_item_table, null, false);
            TextView tvType = tableRow.findViewById(R.id.tvType);
            TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
            TextView tvDaChuyen = tableRow.findViewById(R.id.tvDaChuyen);
            TextView tvSeChuyen = tableRow.findViewById(R.id.tvSeChuyen);
            TextView tvConLai = tableRow.findViewById(R.id.tvConLai);
            tvType.setText(type);
            tvNhanVe.setText(Common.formatMoney(lotoItem.getMoneyTake()) + donvi);
            tvDaChuyen.setText(Common.formatMoney(lotoItem.getMoneySend()) + donvi);
            tvSeChuyen.setText(Common.formatMoney(lotoItem.getSeChuyen()) + donvi);
            tvConLai.setText(Common.formatMoney(lotoItem.getMoneyKeep()) + donvi);
            if (visibleRowBaoCaoNhanh) {
                baoCaoNhanhTable.addView(tableRow);
            }
        }
    }

    private void initTableXien() {
        String donvi = TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
        List<LotoNumberObject> xienListTotal = new ArrayList<>();
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_RECEIVE));
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_RECEIVE));
        xienListTotal.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_RECEIVE));


        List<LotoNumberObject> xienListDaChuyen = new ArrayList<>();
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN2, SmsStatus.SMS_SENT));
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN3, SmsStatus.SMS_SENT));
        xienListDaChuyen.addAll(getDateXienByType(daoSession, TypeEnum.TYPE_XIEN4, SmsStatus.SMS_SENT));

        for (LotoNumberObject lotoNhanVe : xienListTotal) {
            for (LotoNumberObject lotoDaChuyen : xienListDaChuyen) {
                if (lotoNhanVe.getXienFormat().trim().equals(lotoDaChuyen.getXienFormat().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }
        // báo cáo nhanh
        double tongNhanVe = 0;
        double tongDaChuyen = 0;
        double tongSeChuyen = 0;
        double tongConLai = 0;
        for (LotoNumberObject s : xienListTotal) {
            double giulaimax = 0;
            double giulai = 0;
            double sechuyen = 0;
            Double giualaingoaile = null;
            if (s.getType() == TypeEnum.TYPE_XIEN2) {
                giulaimax = giuLaiLonNhatObject.getX2value();
                s.setValue1(s.getValue1() + "-" + s.getValue2());
            } else if (s.getType() == TypeEnum.TYPE_XIEN3) {
                giulaimax = giuLaiLonNhatObject.getX3value();
                s.setValue1(s.getValue1() + "-" + s.getValue2() + "-" + s.getValue3());
            } else if (s.getType() == TypeEnum.TYPE_XIEN4) {
                giulaimax = giuLaiLonNhatObject.getX4value();
                s.setValue1(s.getValue1() + "-" + s.getValue2() + "-" + s.getValue3() + "-" + s.getValue4());
            }
            if (lotoNgoaiLeHashMap.containsKey(s.getXienFormat())) {
                giualaingoaile = lotoNgoaiLeHashMap.get(s.getXienFormat()) != null ? lotoNgoaiLeHashMap.get(s.getXienFormat()) : 0.0;
            }
            // cân chuyển theo phần trăm
            if (giuLaiLonNhatObject.isCanchuyenTheoPhanTram()) {
                // CHuyển theo tổng nhận về
                if (giuLaiLonNhatObject.isChuyenTheoTongNhanVe()) {
                    giulai = s.getMoneyTake() * (100 - giulaimax) / 100;
                    sechuyen = s.getMoneyTake() * (giulaimax) / 100 - s.getMoneySend();
                } else {   // CHuyển theo tổng tồn lại
                    giulai = (s.getMoneyTake() - s.getMoneySend()) * (100 - giulaimax) / 100;
                    sechuyen = (s.getMoneyTake() - s.getMoneySend()) * giulaimax / 100;
                }
            } else {    // cân chuyển theo giá trị tiền giữ lại
                giulai = giulaimax;
                sechuyen = s.getMoneyTake() - giulai - s.getMoneySend();
            }
            // trường hợp ngoại lệ mà !=null => theo giá trị ngoại lệ
            if (giualaingoaile != null) {
                giulai = giualaingoaile;
                sechuyen = (s.getMoneyTake() - s.getMoneySend()) - giulai;
            }
            // sẽ chyển  >0 => hiển thị dòng đó trong table
            if (sechuyen > 0) {
                s.setSeChuyen(sechuyen);
                s.setMoneyKeep(giulai);
                visibleXienRowChiTiet = true;
            } else {
                s.setSeChuyen(0);
                s.setMoneyKeep(s.getMoneyTake());
            }


            tongNhanVe += s.getMoneyTake();
            tongDaChuyen += s.getMoneySend();
            tongSeChuyen += s.getSeChuyen();
            tongConLai += s.getMoneyKeep();
        }

        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN2,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN2,donvi));
        }
        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN3,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN3,donvi));
        }
        if (!ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN4,donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(xienListTotal, TypeEnum.TYPE_XIEN4,donvi));
        }

        // thêm vào danh sách báo cáo nhanh
        LotoNumberObject baoCaoNhanh = new LotoNumberObject(TypeEnum.TYPE_XIEN2, tongNhanVe, tongDaChuyen, tongSeChuyen, tongConLai);
        baoCaoNhanhList.add(baoCaoNhanh);
        if (visibleXienRowChiTiet) {
            rlXien.setVisibility(View.VISIBLE);
            View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_title_table, null, false);
            tableXien.addView(tableRowTitle);
            int count = 0;
            for (LotoNumberObject lotoItem : xienListTotal) {
                count++;
                View tableRow = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_item_table, null, false);
                TextView tvNo = tableRow.findViewById(R.id.tvNo);
                TextView tvNumber = tableRow.findViewById(R.id.tvNumber);
                TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
                TextView tvDaChuyen = tableRow.findViewById(R.id.tvDaChuyen);
                TextView tvSeChuyen = tableRow.findViewById(R.id.tvSeChuyen);
                TextView tvConLai = tableRow.findViewById(R.id.tvConLai);
                tvNo.setText(String.valueOf(count));
                tvNumber.setText(String.valueOf(lotoItem.getValue1()));
                tvNhanVe.setText(Common.formatMoney(lotoItem.getMoneyTake()) + donvi);
                tvDaChuyen.setText(Common.formatMoney(lotoItem.getMoneySend()) + donvi);
                tvSeChuyen.setText(Common.formatMoney(lotoItem.getSeChuyen()) + donvi);
                tvConLai.setText(Common.formatMoney(lotoItem.getMoneyKeep()) + donvi);
                tableXien.addView(tableRow);
            }
        } else {
            rlXien.setVisibility(View.GONE);
        }
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> finish());
        btnChuyen.setOnClickListener(v -> {
            List<AccountObject> accountObjects = new ArrayList<>();
            for (AccountObject accountObject : daoSession.getAccountObjectDao().queryBuilder().list()) {
                if (accountObject.getAccountType() == AccountType.STAFFANDBOSS || accountObject.getAccountType() == AccountType.BOSS) {
                    accountObjects.add(accountObject);
                }
            }
            if (accountObjects.size() > 0) {
                if (smsSend.size() > 0) {
                    Collections.sort(smsSend, (c1, c2) -> -Double.compare(c1.getMoney(), c2.getMoney()));
                    List<String> data= new ArrayList<>();
                    int typeCompare = -1;
                    for (SmsSendObject smsSendObject : smsSend){
                        if (typeCompare==smsSendObject.getType()){
                            data.add(smsSendObject.getContent().replace(TypeEnum.getStringByType3(typeCompare),"").trim());
                        }
                        else{
                            data.add(smsSendObject.getContent());
                            typeCompare=smsSendObject.getType();
                        }

                    }
                    AccountBossDialogFragment accountBossDialogFragment = AccountBossDialogFragment.newInstance(accountObjects, data);
                    accountBossDialogFragment.show(getSupportFragmentManager().beginTransaction(), accountBossDialogFragment.getTag());
                } else {
                    Toast.makeText(this, "Không có dữ liệu cân chuyển", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không có người nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTableLotoByType(int type, LinearLayout view, TableLayout table) {
        try {
            String donvi =TienTe.getKeyTienTe(settingDefault!=null?settingDefault.getTiente():TienTe.TIEN_VIETNAM);
            double giulaimax = 0;
            switch (type) {
                case TypeEnum.TYPE_DE:
                case TypeEnum.TYPE_DAUDB:
                    giulaimax = giuLaiLonNhatObject.getDbvalue();
                    break;
                case TypeEnum.TYPE_DITNHAT:
                    giulaimax = giuLaiLonNhatObject.getGiainhatvalue();
                    break;
                case TypeEnum.TYPE_3C:
                    giulaimax = giuLaiLonNhatObject.getBacangvalue();
                    break;
                case TypeEnum.TYPE_LO:
                    donvi = "d";
                    giulaimax = giuLaiLonNhatObject.getLotovalue();
                    break;
            }
            // báo cáo nhanh
            double tongNhanVe = 0;
            double tongDaChuyen = 0;
            double tongSeChuyen = 0;
            double tongConLai = 0;
            boolean visibleDBTable = false;
            List<LotoNumberObject> dbList = getDateLotoByType(daoSession, type, SmsStatus.SMS_RECEIVE);
            List<LotoNumberObject> dbDaChuyen = getDateLotoByType(daoSession, type, SmsStatus.SMS_SENT);
            for (LotoNumberObject lotoNhanVe : dbList) {
                for (LotoNumberObject lotoDaChuyen : dbDaChuyen) {
                    if (lotoNhanVe.getValue1().trim().equals(lotoDaChuyen.getValue1().trim())) {
                        lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                    }
                }
            }
            for (LotoNumberObject s : dbList) {
                double giulai = 0;
                double sechuyen = 0;
                Double giualaingoaile = null;
                // trường hợp ngoại lệ
                if (lotoNgoaiLeHashMap.containsKey(s.getValue1())) {
                    giualaingoaile = lotoNgoaiLeHashMap.get(s.getValue1()) != null ? lotoNgoaiLeHashMap.get(s.getValue1()) : 0.0;
                }
                // cân chuyển theo phần trăm
                if (giuLaiLonNhatObject.isCanchuyenTheoPhanTram()) {
                    // CHuyển theo tổng nhận về
                    if (giuLaiLonNhatObject.isChuyenTheoTongNhanVe()) {
                        giulai = s.getMoneyTake() * (100 - giulaimax) / 100;
                        sechuyen = s.getMoneyTake() * (giulaimax) / 100 - s.getMoneySend();
                    } else {   // CHuyển theo tổng tồn lại
                        giulai = (s.getMoneyTake() - s.getMoneySend()) * (100 - giulaimax) / 100;
                        sechuyen = (s.getMoneyTake() - s.getMoneySend()) * giulaimax / 100;
                    }
                } else {    // cân chuyển theo giá trị tiền giữ lại
                    giulai = giulaimax;
                    sechuyen = s.getMoneyTake() - giulai - s.getMoneySend();
                }
                // trường hợp ngoại lệ mà !=null => theo giá trị ngoại lệ
                if (giualaingoaile != null) {
                    giulai = giualaingoaile;
                    sechuyen = (s.getMoneyTake() - s.getMoneySend()) - giulai;
                }
                // sẽ chyển  >0 => hiển thị dòng đó trong table
                if (sechuyen > 0) {
                    s.setSeChuyen(sechuyen);
                    s.setMoneyKeep(giulai);
                    visibleDBTable = true;
                } else {
                    s.setSeChuyen(0);
                    s.setMoneyKeep(s.getMoneyTake());
                }
                tongNhanVe += s.getMoneyTake();
                tongDaChuyen += s.getMoneySend();
                tongSeChuyen += s.getSeChuyen();
                tongConLai += s.getMoneyKeep();
            }
            if (!ProcessSms.groupSmsToSent(dbList, type,donvi).isEmpty()) {
                smsSend.addAll(ProcessSms.groupSmsToSent(dbList, type,donvi));
            }
            // thêm vào danh sách báo cáo nhanh
            LotoNumberObject baoCaoNhanh = new LotoNumberObject(type, tongNhanVe, tongDaChuyen, tongSeChuyen, tongConLai);
            baoCaoNhanhList.add(baoCaoNhanh);
            if (visibleDBTable) {
                view.setVisibility(View.VISIBLE);
                View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_title_table, null, false);
                table.addView(tableRowTitle);
                int count = 0;
                for (LotoNumberObject lotoItem : dbList) {
                    count++;
                    View tableRow = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_item_table, null, false);
                    TextView tvNo = tableRow.findViewById(R.id.tvNo);
                    TextView tvNumber = tableRow.findViewById(R.id.tvNumber);
                    TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
                    TextView tvDaChuyen = tableRow.findViewById(R.id.tvDaChuyen);
                    TextView tvSeChuyen = tableRow.findViewById(R.id.tvSeChuyen);
                    TextView tvConLai = tableRow.findViewById(R.id.tvConLai);

                    tvNo.setText(String.valueOf(count));
                    tvNumber.setText(String.valueOf(lotoItem.getValue1()));
                    tvNhanVe.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyTake()), donvi));
                    tvDaChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneySend()), donvi));
                    tvSeChuyen.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getSeChuyen()), donvi));
                    tvConLai.setText(getString(R.string.tv_format_donvi, Common.formatMoney(lotoItem.getMoneyKeep()), donvi));
                    table.addView(tableRow);
                }
            } else {
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }

    }


    public static List<LotoNumberObject> getDateLotoByType(DaoSession session, int type, int smsStatus) {
        Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currentDate, 24);
        String GET_LOTO_BY_SQL = "SELECT  " + LotoNumberObjectDao.Properties.IdLotoNumber.columnName + ", " + LotoNumberObjectDao.Properties.Value1.columnName +
                ", SUM (" + " " + LotoNumberObjectDao.Properties.MoneyTake.columnName + " ) as sumMoneyTake" +
                ", " + LotoNumberObjectDao.Properties.MoneySend.columnName + "" +
                " FROM " + LotoNumberObjectDao.TABLENAME +
                " WHERE " + LotoNumberObjectDao.Properties.DateTake.columnName + " BETWEEN " + currentDate + " AND " + dateEnd + " " +
                " AND " + LotoNumberObjectDao.Properties.Type.columnName + " =" + type + "  " +
                " AND " + LotoNumberObjectDao.Properties.SmsStatus.columnName + " =" + smsStatus + "  " +
                " GROUP BY " + LotoNumberObjectDao.Properties.Value1.columnName + "  " +
                "ORDER BY sumMoneyTake DESC ";
        ArrayList<LotoNumberObject> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(GET_LOTO_BY_SQL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(new LotoNumberObject(c.getLong(0), type, c.getString(1), c.getDouble(2), c.getDouble(3)));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    public static List<LotoNumberObject> getDateXienByType(DaoSession session, int type, int smsStatus) {
        String contentGroupBy = "";
        if (type == TypeEnum.TYPE_XIEN2) {
            contentGroupBy = "" + LotoNumberObjectDao.Properties.Value1.columnName + ", " + LotoNumberObjectDao.Properties.Value2.columnName + "";
        } else if (type == TypeEnum.TYPE_XIEN3) {
            contentGroupBy = "" + LotoNumberObjectDao.Properties.Value1.columnName + ", " + LotoNumberObjectDao.Properties.Value2.columnName + "," + LotoNumberObjectDao.Properties.Value3.columnName + "";
        } else if (type == TypeEnum.TYPE_XIEN4) {
            contentGroupBy = "" + LotoNumberObjectDao.Properties.Value1.columnName + ", " + LotoNumberObjectDao.Properties.Value2.columnName + "," + LotoNumberObjectDao.Properties.Value3.columnName + "," + LotoNumberObjectDao.Properties.Value4.columnName + "";
        }
        Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currentDate, 24);
        String GET_LOTO_BY_SQL = "SELECT  " + LotoNumberObjectDao.Properties.XienFormat.columnName + "," + contentGroupBy +
                ", " + LotoNumberObjectDao.Properties.Type.columnName + "" +
                ", SUM (" + " " + LotoNumberObjectDao.Properties.MoneyTake.columnName + " ) as sumMoneyTake" +
                ", " + " " + LotoNumberObjectDao.Properties.MoneySend.columnName + "" +
                " FROM " + LotoNumberObjectDao.TABLENAME +
                " WHERE " + LotoNumberObjectDao.Properties.DateTake.columnName + " BETWEEN " + currentDate + " AND " + dateEnd + " " +
                " AND " + LotoNumberObjectDao.Properties.Type.columnName + " =" + type + "  " +
                " AND " + LotoNumberObjectDao.Properties.SmsStatus.columnName + " =" + smsStatus + "  " +
                " GROUP BY " + LotoNumberObjectDao.Properties.XienFormat.columnName + "  " +
                "ORDER BY sumMoneyTake DESC ";
        ArrayList<LotoNumberObject> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(GET_LOTO_BY_SQL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    if (type == TypeEnum.TYPE_XIEN2) {
                        result.add(new LotoNumberObject(c.getString(0), c.getString(1), c.getString(2), c.getInt(3), c.getDouble(4), c.getDouble(5)));
                    }
                    if (type == TypeEnum.TYPE_XIEN3) {
                        result.add(new LotoNumberObject(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getDouble(5), c.getDouble(6)));
                    }
                    if (type == TypeEnum.TYPE_XIEN4) {
                        result.add(new LotoNumberObject(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getInt(5), c.getDouble(6), c.getDouble(7)));
                    }
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }
}
