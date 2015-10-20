package com.android.bbkiszka.vendingmachine.vendevents;

import com.android.bbkiszka.vendingmachine.Coinage;

/**
 * Alert any interested parties that user tried to buy something without enough money in the moneybox
 */
public class InsufficientFundsEvent {
    public final int itemId;
    public final int purchasePrice;
    public final int additionalAmountRequired;

    public InsufficientFundsEvent(int id, int price, int additionalRequired) {
        itemId = id;
        purchasePrice = price;
        additionalAmountRequired = additionalRequired;
    }

    public String amountRequired() {
        return Coinage.getDisplayValue(additionalAmountRequired);
    }
}
