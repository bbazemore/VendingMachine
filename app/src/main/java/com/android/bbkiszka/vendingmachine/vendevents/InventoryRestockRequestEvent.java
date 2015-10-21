package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

/**
 * Event sent from Settings to notify Vending Machine to restock inventory as specified
 */
public class InventoryRestockRequestEvent {
    private final static String TAG = InventoryRestockRequestEvent.class.getSimpleName();
    // TODO: pass a new set of VendingItems through the event. If it was a bigger set of
    // data we might pass a content provider uri or cursor.
    public final int itemId;
    public final int oldQuantity;
    public final int newQuantity;

    public InventoryRestockRequestEvent(int id, int oldQuan, int newQuan) {
        itemId = id;
        oldQuantity = oldQuan;
        newQuantity = newQuan;
        Log.i(TAG, "InventoryRestockRequestEvent created");
    }
}
