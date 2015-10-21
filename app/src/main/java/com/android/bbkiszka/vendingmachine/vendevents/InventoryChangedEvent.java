package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * Notify interested parties that the vending items have changed.
 */
public class InventoryChangedEvent {
    private final static String TAG = InventoryChangedEvent.class.getSimpleName();
    // don't store the entire inventory in the event.
    // interested parties can get what they need from the VendingMachine instance
    public final int itemId;
    public final int oldQuantity;
    public final int newQuantity;

    public InventoryChangedEvent(int id, int oldQuan, int newQuan) {
        itemId = id;
        oldQuantity = oldQuan;
        newQuantity = newQuan;
        Log.i(TAG, "InventoryChangeEvent created");
    }
}
