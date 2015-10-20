package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * This event is published when the requested item becomes sold out
 */
public class SoldOutEvent {
    private final static String TAG = SoldOutEvent.class.getSimpleName();

    public final int itemId;

    public SoldOutEvent(int id) {
        Log.i(TAG, "SoldOutEvent created");
        itemId = id;
    }
}
