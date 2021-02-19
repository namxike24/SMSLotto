package com.smsanalytic.lotto.interfaces;

import android.view.View;

public interface IClickListener {
    default void clickEvent(View view,int p){};
    default void acceptEvent(boolean accept){};
}
