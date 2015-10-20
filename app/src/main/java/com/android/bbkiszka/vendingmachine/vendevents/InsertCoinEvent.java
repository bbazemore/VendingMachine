package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * Notify interested parties that a coin was inserted
 */
public class InsertCoinEvent {
    private final static String TAG = InsertCoinEvent.class.getSimpleName();

    public final int coinValue;

    public InsertCoinEvent(int value) {
        Log.i(TAG, "InsertCoinEvent created");
        coinValue = value;
    }
}
