package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * MoneyBox lets the UI know the amount of the refund, with the description
 * of coins in the detail
 */
public class RefundedCoinsEvent {
    private final static String TAG = RefundedCoinsEvent.class.getSimpleName();
    public final int refundAmount;
    public final String refundCoinsMessage;

    public RefundedCoinsEvent(int refund, String refundMessage) {
        refundAmount = refund;
        refundCoinsMessage = refundMessage;
        Log.i(TAG, "RefundedCoinsEvent created");
    }
}
