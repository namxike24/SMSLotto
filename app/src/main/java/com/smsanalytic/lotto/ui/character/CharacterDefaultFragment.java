package com.smsanalytic.lotto.ui.character;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.KyTuThayThe;
import com.smsanalytic.lotto.common.TienTe;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.model.WordReplaceEntity;
import com.smsanalytic.lotto.ui.character.adapter.ChooseWordReplaceAdapter;
import com.smsanalytic.lotto.unit.PreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterDefaultFragment extends Fragment {
    @BindView(R.id.tvNote)
    TextView tvNote;
    private View view;
    @BindView(R.id.lnWordReplace)
    LinearLayout lnWordReplace;
    @BindView(R.id.tvWordReplace)
    TextView tvWordReplace;
    @BindView(R.id.edWord)
    EditText edWord;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.tbWord)
    TableLayout tbWord;

    private ArrayList<WordReplaceEntity> listWordReplace;


    public CharacterDefaultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_character_default, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            initListWordReplace();
            lnWordReplace.setOnClickListener(wordReplaceListener);
            btnSave.setOnClickListener(saveListener);
            displayListWord();
            int tienTeType = Common.getTypeTienTe(getContext());
            String keyTienTe= TienTe.getKeyTienTe(tienTeType);
            tvNote.setText(getString(R.string.character_note_default,keyTienTe,keyTienTe,keyTienTe));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.hideKeyBoard(getActivity());
                if (TextUtils.isEmpty(edWord.getText().toString().trim())) {
                    showAlert("Cụm từ muốn thay thế không được bỏ trống");
                    return;
                }

                if (TextUtils.isEmpty(tvWordReplace.getText().toString().trim())) {
                    showAlert("Cụm từ được muốn thay thế không được bỏ trống");
                    return;
                }

                String word = edWord.getText().toString().trim();

                if (!isExistWord(word)) {
                    MyApp.listWordDefault.add(new WordReplaceEntity(wordReplace.getWord(), word));
                    PreferencesManager.getInstance().setValue(Common.LIST_WORD_DEFAULT, new Gson().toJson(MyApp.listWordDefault));
                    displayListWord();
                    for (KyTuThayThe kyTuThayThe : StringCalculate.listKyTuThayThe) {
                        if (kyTuThayThe.getType().trim().equalsIgnoreCase(wordReplace.getWord())) {
                            kyTuThayThe.getDatas().add(word);
                            break;
                        }
                    }
                    edWord.setText("");
                    tvWordReplace.setText("");
                    wordReplace = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private boolean isExistWord(String word) {
        try {
            for (KyTuThayThe kyTuThayThe : StringCalculate.listKyTuThayThe) {
                if (kyTuThayThe.getDatas().contains(word)) {
                    showAlert(word + " hiện được mặc định hiểu là " + kyTuThayThe.getType() + " nên không thể thay thế từ này!");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void displayListWord() {
        try {
            tbWord.removeAllViews();
            if (!MyApp.listWordDefault.isEmpty()) {
                View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_word_replace, null, false);
                TextView tvXoa = tableRow.findViewById(R.id.tvXoa);
                tvXoa.setText("");
                tbWord.addView(tableRow);

                for (int i = 0; i < MyApp.listWordDefault.size(); i++) {
                    WordReplaceEntity entity = MyApp.listWordDefault.get(i);
                    View row = LayoutInflater.from(getActivity()).inflate(R.layout.table_word_replace, null, false);
                    TextView tvSTT = row.findViewById(R.id.tvSTT);
                    tvSTT.setTypeface(Typeface.DEFAULT);
                    TextView tvCumtu = row.findViewById(R.id.tvCumTu);
                    tvCumtu.setTypeface(Typeface.DEFAULT);
                    TextView tvDuocThayBang = row.findViewById(R.id.tvDuocThayBang);
                    tvDuocThayBang.setTypeface(Typeface.DEFAULT);
                    TextView tvXoaChild = row.findViewById(R.id.tvXoa);
                    tvXoaChild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyApp.listWordDefault.remove(entity);
                            PreferencesManager.getInstance().setValue(Common.LIST_WORD_DEFAULT, new Gson().toJson(MyApp.listWordDefault));
                            displayListWord();
                        }
                    });

                    tvSTT.setText(String.valueOf(i + 1));
                    tvCumtu.setText(entity.getWordDisplay());
                    tvDuocThayBang.setText(entity.getWord());
                    tbWord.addView(row);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Cảnh báo");
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton("OK",
                    (dialog, id) -> dialog.dismiss());
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChooseWordReplacePopup wordReplacePopup;
    private View.OnClickListener wordReplaceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Common.hideKeyBoard(getActivity());
                if (wordReplacePopup == null || !wordReplacePopup.isShowing()) {
                    wordReplacePopup = new ChooseWordReplacePopup(getActivity(), listWordReplace, chooseWordReplaceDone);
                    wordReplacePopup.showAsDropDown(view, -100, 0);
                } else {
                    wordReplacePopup.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private WordReplaceEntity wordReplace;
    private ChooseWordReplaceAdapter.ItemListener chooseWordReplaceDone = new ChooseWordReplaceAdapter.ItemListener() {
        @Override
        public void clickItem(WordReplaceEntity entity) {
            try {
                wordReplacePopup.dismiss();
                if (TextUtils.isEmpty(entity.getWord())) {
                    tvWordReplace.setText("");
                    wordReplace = null;
                } else {
                    tvWordReplace.setText(entity.getWordDisplay());
                    wordReplace = entity;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initListWordReplace() {
        try {
            listWordReplace = new ArrayList<>();
            listWordReplace.add(new WordReplaceEntity("", "Chọn cụm từ thay thế"));
            listWordReplace.add(new WordReplaceEntity("de", "Đề"));
            listWordReplace.add(new WordReplaceEntity("lo", "Lô"));
            listWordReplace.add(new WordReplaceEntity("xien", "Xiên"));
            listWordReplace.add(new WordReplaceEntity("bacang", "Ba càng"));
            listWordReplace.add(new WordReplaceEntity("ditnhat", "Đuôi giải nhất"));
            listWordReplace.add(new WordReplaceEntity("cua", "Của"));
            listWordReplace.add(new WordReplaceEntity("daunhat", "Đầu giải nhất"));
            listWordReplace.add(new WordReplaceEntity("daudb", "Đầu giải ĐB"));
            listWordReplace.add(new WordReplaceEntity("botrung", "Bỏ trùng"));
            listWordReplace.add(new WordReplaceEntity("dit", "Đuôi"));
            listWordReplace.add(new WordReplaceEntity("100so", "100 số"));
            listWordReplace.add(new WordReplaceEntity("bebe", "Bé bé"));
            listWordReplace.add(new WordReplaceEntity("belon", "Bé lớn"));
            listWordReplace.add(new WordReplaceEntity("boj", "Bộ"));
            listWordReplace.add(new WordReplaceEntity("bor", "Bỏ"));
            listWordReplace.add(new WordReplaceEntity("canggiua", "canggiua"));
            listWordReplace.add(new WordReplaceEntity("cham", "Chạm"));
            listWordReplace.add(new WordReplaceEntity("chamlt", "Chạm lấy trùng"));
            listWordReplace.add(new WordReplaceEntity("chanchan", "Chẵn Chẵn"));
            listWordReplace.add(new WordReplaceEntity("chanle", "Chẵn lẻ"));
            listWordReplace.add(new WordReplaceEntity("chia3du1", "Chia 3 dư 1"));
            listWordReplace.add(new WordReplaceEntity("chia3du2", "Chia 3 dư 2"));
            listWordReplace.add(new WordReplaceEntity("d", "Điểm"));
            listWordReplace.add(new WordReplaceEntity("dau", "Đầu"));
            listWordReplace.add(new WordReplaceEntity("daube", "Đầu bé"));
            listWordReplace.add(new WordReplaceEntity("dauchan", "Đầu chẵn"));
            listWordReplace.add(new WordReplaceEntity("daudit", "Đầu đuôi"));
            listWordReplace.add(new WordReplaceEntity("daule", "Đầu lẻ"));
            listWordReplace.add(new WordReplaceEntity("daulon", "Đầu lớn"));
            listWordReplace.add(new WordReplaceEntity("ditbe", "Đuôi bé"));
            listWordReplace.add(new WordReplaceEntity("ditchan", "Đuôi chẵn"));
            listWordReplace.add(new WordReplaceEntity("ditle", "Đuôi lẻ"));
            listWordReplace.add(new WordReplaceEntity("gheptrong", "Ghép trong"));
            listWordReplace.add(new WordReplaceEntity("hangcho", "Dây chó"));
            listWordReplace.add(new WordReplaceEntity("hangchuot", "Dây chuột"));
            listWordReplace.add(new WordReplaceEntity("hangdan", "Dây hổ"));
            listWordReplace.add(new WordReplaceEntity("hangga", "Dây gà"));
            listWordReplace.add(new WordReplaceEntity("hangkhi", "Dây khỉ"));
            listWordReplace.add(new WordReplaceEntity("hanglon", "Dây lợn"));
            listWordReplace.add(new WordReplaceEntity("hangmeo", "Dây mèo"));
            listWordReplace.add(new WordReplaceEntity("hangmui", "Dây mùi"));
            listWordReplace.add(new WordReplaceEntity("hangngua", "Dây ngựa"));
            listWordReplace.add(new WordReplaceEntity("hangran", "Dây rắn"));
            listWordReplace.add(new WordReplaceEntity("hangrong", "Dây rồng"));
            listWordReplace.add(new WordReplaceEntity("hangtrau", "Dây trâu"));
            listWordReplace.add(new WordReplaceEntity("kepam", "Kép âm"));
            listWordReplace.add(new WordReplaceEntity("kepbangkeplech", "Kép bằng-kép lệch"));
            listWordReplace.add(new WordReplaceEntity("keplech", "Kép lệch"));
            listWordReplace.add(new WordReplaceEntity("khongchiahetcho3", "Không chia hết cho 3"));
            listWordReplace.add(new WordReplaceEntity("le", "Lẻ"));
            listWordReplace.add(new WordReplaceEntity("lechan", "Lẻ chẵn"));
            listWordReplace.add(new WordReplaceEntity("lele", "Lẻ lẻ"));
            listWordReplace.add(new WordReplaceEntity("loa", "Loa"));
            listWordReplace.add(new WordReplaceEntity("lonbe", "Lớn bé"));
            listWordReplace.add(new WordReplaceEntity("lonlon", "Lớn lớn"));
            listWordReplace.add(new WordReplaceEntity("n", "Nghìn"));
            listWordReplace.add(new WordReplaceEntity("satkep", "Sát kép"));
            listWordReplace.add(new WordReplaceEntity("satkeplech", "Sát kép lệch"));
            listWordReplace.add(new WordReplaceEntity("tc=", "Tất cả bằng"));
            listWordReplace.add(new WordReplaceEntity("tongbe", "Tổng bé"));
            listWordReplace.add(new WordReplaceEntity("tongchan", "Tổng chẵn"));
            listWordReplace.add(new WordReplaceEntity("tongduoi10", "Tổng dưới 10"));
            listWordReplace.add(new WordReplaceEntity("tongtren10", "Tổng trên 10"));
            listWordReplace.add(new WordReplaceEntity("tr", "Triệu"));
            listWordReplace.add(new WordReplaceEntity("trn", "Tram nghìn"));
            listWordReplace.add(new WordReplaceEntity("trontron", "Tròn tròn"));
            listWordReplace.add(new WordReplaceEntity("tronvuong", "Tròn vuông"));
            listWordReplace.add(new WordReplaceEntity("vuongtron", "Vuông tròn"));
            listWordReplace.add(new WordReplaceEntity("vuongvuong", "Vuông vuông"));
            listWordReplace.add(new WordReplaceEntity("x", "Nhân"));
            listWordReplace.add(new WordReplaceEntity("xienghep2", "Xiên ghép 2"));
            listWordReplace.add(new WordReplaceEntity("xienghep3", "Xiên ghép 3"));
            listWordReplace.add(new WordReplaceEntity("xienghep4", "Xiên ghép 4"));
            listWordReplace.add(new WordReplaceEntity("xienquay", "Xiên quay"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
