package com.smsanalytic.lotto.ui.character;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.smsanalytic.lotto.MyApp;
import com.smsanalytic.lotto.R;
import com.smsanalytic.lotto.common.Common;
import com.smsanalytic.lotto.common.KyTuThayThe;
import com.smsanalytic.lotto.common.calculate.StringCalculate;
import com.smsanalytic.lotto.model.WordReplaceEntity;
import com.smsanalytic.lotto.unit.PreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterCustomFragment extends Fragment {
    private View view;
    @BindView(R.id.edWordReplace)
    EditText edWordReplace;
    @BindView(R.id.edWord)
    EditText edWord;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.tbWord)
    TableLayout tbWord;

    private ArrayList<WordReplaceEntity> listWordReplace;


    public CharacterCustomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_character_custom, container, false);
                ButterKnife.bind(this, view);
            }
            return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            btnSave.setOnClickListener(saveListener);
            displayListWord();
        } catch (Exception e) {
            e.printStackTrace();
        }}

        private View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Common.hideKeyBoard(getActivity());
                    if (TextUtils.isEmpty(edWord.getText().toString().trim())) {
                        showAlert("Cụm từ muốn thay thế không được bỏ trống");
                        return;
                    }

                    if (TextUtils.isEmpty(edWordReplace.getText().toString().trim())) {
                        showAlert("Cụm từ được muốn thay thế không được bỏ trống");
                        return;
                    }

                    String word = edWord.getText().toString().trim();
                    String wordReplace = edWordReplace.getText().toString().trim();

                    if (!isExistWord(word)) {
                        MyApp.listWordCustom.add(new WordReplaceEntity(wordReplace, word));
                        PreferencesManager.getInstance().setValue(Common.LIST_WORD_CUSTOM, new Gson().toJson(MyApp.listWordCustom));
                        displayListWord();
                        for (KyTuThayThe kyTuThayThe : StringCalculate.listKyTuThayThe) {
                            if (kyTuThayThe.getType().trim().equalsIgnoreCase(wordReplace)){
                                kyTuThayThe.getDatas().add(word);
                                break;
                            }
                        }
                        edWord.setText("");
                        edWordReplace.setText("");
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
                if (!MyApp.listWordCustom.isEmpty()) {
                    View tableRow = LayoutInflater.from(getActivity()).inflate(R.layout.table_word_replace, null, false);
                    TextView tvXoa = tableRow.findViewById(R.id.tvXoa);
                    tvXoa.setText("");
                    tbWord.addView(tableRow);

                    for (int i = 0; i < MyApp.listWordCustom.size(); i++) {
                        WordReplaceEntity entity = MyApp.listWordCustom.get(i);
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
                                MyApp.listWordCustom.remove(entity);
                                PreferencesManager.getInstance().setValue(Common.LIST_WORD_CUSTOM, new Gson().toJson(MyApp.listWordCustom));
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
}
