package com.smsanalytic.lotto;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.common.TypeEnum;
import com.smsanalytic.lotto.database.LotoNumberObject;
import com.smsanalytic.lotto.model.StringProcessChildEntity;
import com.smsanalytic.lotto.model.StringProcessEntity;

public class NamTestActivity extends AppCompatActivity {

    private EditText edText;
    private TextView tvResult;
    private TextView tvTotal;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nam_test);
        edText = findViewById(R.id.edText);
        btnTest = findViewById(R.id.btnTest);
        tvResult = findViewById(R.id.tvResult);
        tvTotal = findViewById(R.id.tvTotal);

        btnTest.setOnClickListener(view -> {
            try {
//                    new InternetCheck(new InternetCheck.Consumer() {
//                        @Override
//                        public void accept(Boolean internet) {
//                            Log.e("check internet", internet ? "Have internet" : "no have");
//                        }
//                    });
                StringProcessEntity processEntity = StringCalculate.processStringOriginal(edText.getText().toString().trim().toLowerCase());
                if (processEntity != null && processEntity.getListChild() != null && !processEntity.getListChild().isEmpty()) {
                    for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                        Log.e("String", childEntity.getValue());
                        StringCalculate.processChildEntity(processEntity, childEntity);
                    }

                    if (processEntity.isHasError()) {
                        processError(processEntity);
                    } else {
                        processShowResult(processEntity,"Nghin");
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NamTestActivity.this);
                    builder.setTitle("Tin nhắn sai cú pháp");
                    builder.setMessage("Không đúng định dạng");
                    builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void
    processShowResult(StringProcessEntity processEntity,String donvi) {
        try {
            StringBuilder sbProcess = new StringBuilder();
            StringBuilder sbResult = new StringBuilder();

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
            int totalDePrice = 0;
            int totalLoNum = 0;
            int totalLoPrice = 0;
            int total3CNum = 0;
            int total3CPrice = 0;
            int totalDitNhatNum = 0;
            int totalDitNhatPrice = 0;
            int totalDauDBNum = 0;
            int totalDauDBPrice = 0;
            int totalDauNhatNum = 0;
            int totalDauNhatPrice = 0;
            int totalCangGiuaNum = 0;
            int totalCangGiuaPrice = 0;
            int totalXienghep2Num = 0;
            int totalXienghep2Price = 0;
            int totalXienghep3Num = 0;
            int totalXienghep3Price = 0;
            int totalXienghep4Num = 0;
            int totalXienghep4Price = 0;
            int totalXienquayNum = 0;
            int totalXienquayPrice = 0;
            int totalXien2Num = 0;
            int totalXien2Price = 0;
            int totalXien3Num = 0;
            int totalXien3Price = 0;
            int totalXien4Num = 0;
            int totalXien4Price = 0;

            for (StringProcessChildEntity childEntity : processEntity.getListChild()) {
                String value = childEntity.getValue().replaceAll("bortrung", "bỏ trùng")
                        .replaceAll("bor", "bỏ").replaceAll("boj", "bộ")
                        .replaceAll("xienghephai", "xienghep2")
                        .replaceAll("xienghepba", "xienghep3").replaceAll("xienghepbon", "xienghep4")
                        .replaceAll("xienhai","xien2").replaceAll("xienba","xien3").replaceAll("xienbon","xien4");
                if (childEntity.isError()) {
                    sbProcess.append(value).append(". ");
                } else {
                    sbProcess.append(value.substring(0, value.lastIndexOf(" "))).append("<b>").append(value.substring(value.lastIndexOf(" "))).append("</b>").append(". ");
                }
                int price = 0;
                for (LotoNumberObject numberObject : childEntity.getListDataLoto()) {
                    price += numberObject.getMoneyTake();
                }
                sbResult.append(childEntity.getValue())
                        .append(String.format(" [" + TypeEnum.getStringByTypeFull(childEntity.getType()) + " %s cặp: %s điểm]"
                                , String.valueOf(childEntity.getListDataLoto().size()), String.valueOf(price)))
                        .append("\n");
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
                sbTotalDe.append(String.format("- Đề %s cặp: %s "+donvi+"", String.valueOf(totalDeNum), String.valueOf(totalDePrice)));
            }

            if (totalLoNum > 0 && totalLoPrice > 0) {
                sbTotalLo.append(String.format("- Lô %s cặp: %s Điểm", String.valueOf(totalLoNum), String.valueOf(totalLoPrice)));
            }

            if (total3CNum > 0 && total3CPrice > 0) {
                sbTotalLo.append(String.format("- Ba càng %s cặp: %s "+donvi+"", String.valueOf(total3CNum), String.valueOf(total3CPrice)));
            }

            if (totalDitNhatNum > 0 && totalDitNhatPrice > 0) {
                sbTotalDitNhat.append(String.format("- Đuôi G1 %s cặp: %s "+donvi+"", String.valueOf(totalDitNhatNum), String.valueOf(totalDitNhatPrice)));
            }

            if (totalDauDBNum > 0 && totalDauDBPrice > 0) {
                sbTotalDauDB.append(String.format("- Đầu ĐB %s cặp: %s "+donvi+"", String.valueOf(totalDauDBNum), String.valueOf(totalDauDBPrice)));
            }

            if (totalDauNhatNum > 0 && totalDauNhatPrice > 0) {
                sbTotalDauNhat.append(String.format("- Đầu G1 %s cặp: %s "+donvi+"", String.valueOf(totalDauNhatNum), String.valueOf(totalDauNhatPrice)));
            }

            if (totalCangGiuaNum > 0 && totalCangGiuaPrice > 0) {
                sbTotalDauNhat.append(String.format("- Càng giữa %s cặp: %s "+donvi+"", String.valueOf(totalCangGiuaNum), String.valueOf(totalCangGiuaPrice)));
            }

            if (totalXienghep2Num > 0 && totalXienghep2Price > 0) {
                sbTotalXienghep2.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep2Num), String.valueOf(totalXienghep2Price)));
            }

            if (totalXienghep3Num > 0 && totalXienghep3Price > 0) {
                sbTotalXienghep3.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep3Num), String.valueOf(totalXienghep3Price)));
            }

            if (totalXienghep4Num > 0 && totalXienghep4Price > 0) {
                sbTotalXienghep4.append(String.format("- Xiên ghép %s cặp: %s "+donvi+"", String.valueOf(totalXienghep4Num), String.valueOf(totalXienghep4Price)));
            }

            if (totalXienquayNum > 0 && totalXienquayPrice > 0) {
                sbTotalXienquay.append(String.format("- Xiên quay %s cặp: %s "+donvi+"", String.valueOf(totalXienquayNum), String.valueOf(totalXienquayPrice)));
            }

            if (totalXien2Num > 0 && totalXien2Price > 0) {
                sbTotalXien2.append(String.format("- Xiên 2 %s cặp: %s "+donvi+"", String.valueOf(totalXien2Num), String.valueOf(totalXien2Price)));
            }
            if (totalXien3Num > 0 && totalXien3Price > 0) {
                sbTotalXien3.append(String.format("- Xiên 3 %s cặp: %s "+donvi+"", String.valueOf(totalXien3Num), String.valueOf(totalXien3Price)));
            }
            if (totalXien4Num > 0 && totalXien4Price > 0) {
                sbTotalXien4.append(String.format("- Xiên 4 %s cặp: %s "+donvi+"", String.valueOf(totalXien4Num), String.valueOf(totalXien4Price)));
            }

            edText.setText(Html.fromHtml(sbProcess.toString()));
            tvResult.setText(sbResult.substring(0, sbResult.length() - 1));
            sbTotal.append("Toàn bộ:");
            for (StringBuilder stringBuilder : listSbTotal) {
                if (!TextUtils.isEmpty(stringBuilder.toString())) {
                    sbTotal.append("\n").append(stringBuilder.toString());
                }
            }
            tvTotal.setText(sbTotal.toString());
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
                sbProcess.append(childEntity.getValue()).append(". ");
                if (childEntity.isError()) {
                    sbError.append(no).append(". ").append(childEntity.getError()).append("\n");
                    no++;
                }
            }
            edText.setText(Html.fromHtml(sbProcess.toString()));
            tvResult.setText("");
            tvTotal.setText("");
            AlertDialog.Builder builder = new AlertDialog.Builder(NamTestActivity.this);
            builder.setTitle("Tin nhắn sai cú pháp");
            builder.setMessage(sbError.toString());
            builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
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
}
