package com.smsanalytic.lotto.ui.balance;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
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
import com.smsanalytic.lotto.model.canchuyen.BaoCaoNhanh100SoObject;
import com.smsanalytic.lotto.model.setting.SettingDefault;
import com.smsanalytic.lotto.processSms.ProcessSms;
import com.smsanalytic.lotto.unit.PreferencesManager;


public class CanChuyen100SoActivity extends AppCompatActivity {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.btnChuyen)
    TextView btnChuyen;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.baoCaoNhanhTable)
    TableLayout baoCaoNhanhTable;
    @BindView(R.id.tableChiTiet)
    TableLayout tableChiTiet;
    @BindView(R.id.rlChiTiet)
    LinearLayout rlChiTiet;
    @BindView(R.id.lnMain)
    LinearLayout lnMain;
    @BindView(R.id.nestScrollView)
    NestedScrollView nestScrollView;
    @BindView(R.id.tvTitleType)
    TextView tvTitleType;
    @BindView(R.id.rlLoai)
    RelativeLayout rlLoai;
    @BindView(R.id.btnLayGiuLieu)
    Button btnLayGiuLieu;
    @BindView(R.id.titleChiTiet)
    TextView titleChiTiet;
    @BindView(R.id.tv_huongdan)
    TextView tvHuongdan;
    private DaoSession daoSession;
    private int typeDefault = TypeEnum.TYPE_DE;
    private String tableTitle = "Đuôi Đặc Biệt(Đề)";
    List<BaoCaoNhanh100SoObject> baocaoList = new ArrayList<>();
    List<SmsSendObject> smsSend = new ArrayList<>();
    SettingDefault settingDefault;
    private String donvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_chuyen100_so);
        daoSession = ((MyApp) this.getApplication()).getDaoSession();
        String dateSettingCache = PreferencesManager.getInstance().getValue(PreferencesManager.SETTING_DEFAULT, "");
        if (!dateSettingCache.isEmpty()) {
            settingDefault = new Gson().fromJson(dateSettingCache, SettingDefault.class);
        } else {
            String dateDefault = Common.loadJSONFromAsset(this, "SettingDefault.json");
            settingDefault = new Gson().fromJson(dateDefault, SettingDefault.class);
        }
        donvi = TienTe.getKeyTienTe(settingDefault != null ? settingDefault.getTiente() : TienTe.TIEN_VIETNAM);
        ButterKnife.bind(this);
        clickEvent();
        initBaoCaoNhanh();
        initChiTiet();
    }

    private void initChiTiet() {

        titleChiTiet.setText(tableTitle);
        tableChiTiet.removeAllViews();
        BaoCaoNhanh100SoObject canChuyenObject = null;
        for (BaoCaoNhanh100SoObject object : baocaoList) {
            if (object.getType() == typeDefault) {
                canChuyenObject = object;
                break;
            }
        }

        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_chi_tiet_title_table, null, false);
        tableChiTiet.addView(tableRowTitle);

        String donvi = TienTe.getKeyTienTe(settingDefault != null ? settingDefault.getTiente() : TienTe.TIEN_VIETNAM);
        if (typeDefault == TypeEnum.TYPE_LO) {
            donvi = "d";
        }
        List<LotoNumberObject> dbList = getDataChiTietByType(typeDefault, SmsStatus.SMS_RECEIVE);
        List<LotoNumberObject> dbListSeChuyen = getDataChiTietByType(typeDefault, SmsStatus.SMS_SENT);

        for (LotoNumberObject lotoNhanVe : dbList) {
            for (LotoNumberObject lotoDaChuyen : dbListSeChuyen) {
                if (lotoNhanVe.getValue1().trim().equals(lotoDaChuyen.getValue1().trim())) {
                    lotoNhanVe.setMoneySend(lotoDaChuyen.getMoneyTake());
                }
            }
        }

        for (LotoNumberObject lotoNumberObject : dbList) {
            double giulai = 0;
            double sechuyen = 0;
            if (canChuyenObject != null) {
                if (canChuyenObject.isTrangthai()) {
                    giulai = canChuyenObject.getSobenhat();
                } else {
                    giulai = 0;
                }
                sechuyen = lotoNumberObject.getMoneyTake() - lotoNumberObject.getMoneySend() - giulai;
                lotoNumberObject.setMoneyKeep(giulai);
                lotoNumberObject.setSeChuyen(sechuyen);
            }
        }
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
            tableChiTiet.addView(tableRow);
        }
        smsSend.clear();
        if (!ProcessSms.groupSmsToSent(dbList, typeDefault, donvi).isEmpty()) {
            smsSend.addAll(ProcessSms.groupSmsToSent(dbList, typeDefault, donvi));
        }
    }

    private void initBaoCaoNhanh() {
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_DE));
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_LO));
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_3C));
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_DAUDB));
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_DAUNHAT));
        baocaoList.add(getDataBaoCaoByType(TypeEnum.TYPE_DITNHAT));

        String donvi;
        String donviRoot = TienTe.getKeyTienTe(settingDefault != null ? settingDefault.getTiente() : TienTe.TIEN_VIETNAM);
        String type = "";
        View tableRowTitle = LayoutInflater.from(this).inflate(R.layout.view_bao_cao_nhanh_100so_title_table, null, false);
        baoCaoNhanhTable.addView(tableRowTitle);
        for (BaoCaoNhanh100SoObject object : baocaoList) {
            if (object.getType() == TypeEnum.TYPE_LO) {
                donvi = "d";
            } else {
                donvi = donviRoot;
            }

            View tableRow = LayoutInflater.from(this).inflate(R.layout.view_bao_cao_nhanh_100so_item_table, null, false);
            TextView tvLoai = tableRow.findViewById(R.id.tvLoai);
            TextView tvNhanVe = tableRow.findViewById(R.id.tvNhanVe);
            TextView tvSoLonNhat = tableRow.findViewById(R.id.tvSoLonNhat);
            TextView tvSoBeNhat = tableRow.findViewById(R.id.tvSoNhoNhat);
            TextView tvTinhTrang = tableRow.findViewById(R.id.tvTinhTrang);
            type = TypeEnum.getStringByType2(object.getType());
            tvLoai.setText(type);
            tvNhanVe.setText(String.valueOf(object.getSonhanve()));
            tvSoLonNhat.setText(getString(R.string.tv_format_donvi, Common.formatMoney(object.getSotonhat()), donvi));
            tvSoBeNhat.setText(getString(R.string.tv_format_donvi, Common.formatMoney(object.getSobenhat()), donvi));
            if (object.isTrangthai()) {
                tvTinhTrang.setText("Đủ số để cân");
                tvTinhTrang.setTextColor(getResources().getColor(R.color.red));
            } else {
                tvTinhTrang.setText("Không đủ số để cân");
                tvTinhTrang.setTextColor(getResources().getColor(R.color.back_1));
            }
            baoCaoNhanhTable.addView(tableRow);
        }
    }

    private void clickEvent() {
        btnBack.setOnClickListener(v -> finish());

        rlLoai.setOnClickListener(v -> {
            showPopupLotoType();
        });
        btnLayGiuLieu.setOnClickListener(v -> {
            initChiTiet();
        });
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

    public BaoCaoNhanh100SoObject getDataBaoCaoByType(int type) {
        Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currentDate, 24);
        String GET_LOTO_BY_SQL = "SELECT " +
                " SUM ( " + LotoNumberObjectDao.Properties.MoneyTake.columnName + ")" +
                " FROM " + LotoNumberObjectDao.TABLENAME +
                " WHERE " + LotoNumberObjectDao.Properties.DateTake.columnName + " BETWEEN " + currentDate + " AND " + dateEnd + " " +
                " AND " + LotoNumberObjectDao.Properties.Type.columnName + " =" + type + "  " +
                " GROUP BY " + LotoNumberObjectDao.Properties.Value1.columnName + " ";
        ArrayList<Double> solonnhatArray = new ArrayList<>();
        Cursor c = daoSession.getDatabase().rawQuery(GET_LOTO_BY_SQL, null);
        try {
            if (c.moveToFirst()) {
                do {
                    double solonnhat1 = c.getDouble(0);
                    solonnhatArray.add(solonnhat1);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        int count = solonnhatArray.size();
        double solonnhat = solonnhatArray.size() > 0 ? Collections.max(solonnhatArray) : 0;
        double sobenhat = solonnhatArray.size() > 0 ? Collections.min(solonnhatArray) : 0;
        return new BaoCaoNhanh100SoObject(type, count, solonnhat, sobenhat, count == 100);
    }

    public List<LotoNumberObject> getDataChiTietByType(int type, int smsStatus) {
        Long currentDate = Common.convertDateToMiniSeconds(Common.getCurrentTime(), Common.FORMAT_DATE_DD_MM_YYY);
        Long dateEnd = Common.addHourToDate(currentDate, 24);
        String GET_LOTO_BY_SQL = "SELECT " + LotoNumberObjectDao.Properties.IdLotoNumber.columnName + "," + LotoNumberObjectDao.Properties.Value1.columnName +
                ", SUM (" + " " + LotoNumberObjectDao.Properties.MoneyTake.columnName + " ) as sumMoneyTake" +
                ", " + LotoNumberObjectDao.Properties.MoneySend.columnName + "" +
                " FROM " + LotoNumberObjectDao.TABLENAME +
                " WHERE " + LotoNumberObjectDao.Properties.DateTake.columnName + " BETWEEN " + currentDate + " AND " + dateEnd + " " +
                " AND " + LotoNumberObjectDao.Properties.Type.columnName + " =" + type + "  " +
                " AND " + LotoNumberObjectDao.Properties.SmsStatus.columnName + " =" + smsStatus + "  " +
                " GROUP BY " + LotoNumberObjectDao.Properties.Value1.columnName + "  " +
                "ORDER BY sumMoneyTake DESC ";
        ArrayList<LotoNumberObject> result = new ArrayList<>();
        Cursor c = daoSession.getDatabase().rawQuery(GET_LOTO_BY_SQL, null);
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


    private void showPopupLotoType() {
        PopupMenu popup = new PopupMenu(this, rlLoai);
        popup.getMenuInflater()
                .inflate(R.menu.menu_loto_type, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            tvTitleType.setText(item.getTitle());
            switch (item.getItemId()) {
                case R.id.itemDe:
                    typeDefault = TypeEnum.TYPE_DE;
                    tableTitle = "Đặc Biệt(Đề)";

                    break;
                case R.id.itemLo:
                    typeDefault = TypeEnum.TYPE_LO;
                    tableTitle = "Lô";
                    break;
                case R.id.itemLoDau:
                    tableTitle = "Lô Đầu";
                    break;
                case R.id.itemXien:
                    tableTitle = "Xiên";
                    typeDefault = TypeEnum.TYPE_XIEN2;
                    break;
                case R.id.itemBaCang:
                    tableTitle = "Ba Càng";
                    typeDefault = TypeEnum.TYPE_3C;
                    break;
                case R.id.itemDitNhat:
                    tableTitle = "Đuôi Giải Nhất";
                    typeDefault = TypeEnum.TYPE_DITNHAT;
                    break;
                case R.id.itemDauNhat:
                    tableTitle = "Đầu Giải Nhất";
                    typeDefault = TypeEnum.TYPE_DAUNHAT;
                    break;
                case R.id.itemDauDB:
                    tableTitle = "Đầu Đặc Biệt";
                    typeDefault = TypeEnum.TYPE_DAUDB;
                    break;

            }
            return true;
        });
        popup.show(); //showing popup menu
    }
}
