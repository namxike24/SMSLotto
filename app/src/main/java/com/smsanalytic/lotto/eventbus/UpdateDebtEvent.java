package com.smsanalytic.lotto.eventbus;

import com.smsanalytic.lotto.database.DebtObject;

public class UpdateDebtEvent {
    private DebtObject debtObject;

    public UpdateDebtEvent(DebtObject debtObject) {
        this.debtObject = debtObject;
    }

    public DebtObject getDebtObject() {
        return debtObject;
    }

    public void setDebtObject(DebtObject debtObject) {
        this.debtObject = debtObject;
    }
}
