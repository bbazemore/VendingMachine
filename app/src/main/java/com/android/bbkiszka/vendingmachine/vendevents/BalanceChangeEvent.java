package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * This event is published by the MoneyBox to the event bus when a coin is inserted,
 * a refund is requested, or any other action that changes the balance
 * in the MoneyBox
 */
public class BalanceChangeEvent {
    private final static String TAG = BalanceChangeEvent.class.getSimpleName();

    public int newBalance;

    public BalanceChangeEvent(int balance) {
        Log.i(TAG, "BalanceChangeEvent created");
        newBalance = balance;
    }
}
