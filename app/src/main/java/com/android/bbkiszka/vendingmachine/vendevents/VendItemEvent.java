package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * This event is created when an item is successfully purchased
 */
public class VendItemEvent {
    private final static String TAG = VendItemEvent.class.getSimpleName();

    public final int itemId;
    //public int price;

    public VendItemEvent(int id) {
        Log.i(TAG, "VendItemEvent created");
        itemId = id;
    }
}

