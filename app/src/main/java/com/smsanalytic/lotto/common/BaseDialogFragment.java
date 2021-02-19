package com.smsanalytic.lotto.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * @author hvthuyen
 *         <p/>
 *         base cho các dialog fragment
 */
public abstract class BaseDialogFragment extends DialogFragment implements
        View.OnClickListener {

    protected String TAG;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getTAG();

    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //View rootView = inflater.inflate(getLayout(), container, false);
        //PvThuc: Chỉnh sửa cho dialog ăn theo style Checkbox và RadioButton
        View rootView = getActivity().getLayoutInflater().inflate(getLayout(), container, false);
        try {
            initView(rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (getDialog() != null && getDialog().getWindow() != null) {
                getDialog().getWindow().setLayout(getDialogWidth(), LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCancelable(true);
    }

    /**
     * khởi tạo view cho dialog fragment hiện tại
     *
     * @param rootView view hiện tại cho dialog
     */
    protected abstract void initView(View rootView);

    /**
     * @return chiều rộng dialog
     */
    protected abstract int getDialogWidth();

    /**
     * id của layout hiển thị cho dialog
     *
     * @return
     * @context dùng trong onCreateView
     * @mean
     * @author hvthuyen
     * @createdDate Apr 28, 2016
     */
    protected abstract int getLayout();

    /**
     * @return
     * @context
     * @mean
     * @author hvthuyen
     * @createdDate Apr 28, 2016
     */
    public abstract String getTAG();

    public void show(FragmentManager manager) {
        super.show(manager, getTAG());
    }



}
