package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic Vending Machine, this holds the inventory (data)
 * and is responsible for keeping the inventory quantities up to date.
 * It uses MoneyBox to handle money matters such as insertCoin and refund.
 *
 * Interacts with the UI by sending notifications on the event bus when
 * interesting things happen: item vended, sold out, restock needed.
 *
 */
public class VendingMachine extends Observable implements Parcelable {
    // Keep stock in a concurrent hash map, indexed by the item id
    // Concurrent hashmap has decent performance and is thread safe.
    ConcurrentHashMap<Integer, VendingItem> mInventory;
    MoneyBox                mMoneyBox;

    public VendingMachine(List<VendingItem> inventory) {

        // set up money box with default coinage
        mMoneyBox = new MoneyBox(new Coinage());

        // set up inventory of VendingItems so that we can look them
        // up by unique id, which is the image resource id.
        setInventory(inventory);
    }

    public static final Creator<VendingMachine> CREATOR = new Creator<VendingMachine>() {
        @Override
        public VendingMachine createFromParcel(Parcel in) {
            return new VendingMachine(in);
        }

        @Override
        public VendingMachine[] newArray(int size) {
            return new VendingMachine[size];
        }
    };


    public Boolean insertCoin(int value) {
        return mMoneyBox.insertCoin(value);
    }

    public int refund() {
        return mMoneyBox.refund();
    }

    public int getCurrentBalance() {
        return mMoneyBox.getCurrentBalance();
    }

    public boolean vendItem(int itemImageId) {
        boolean itWorked = false;
        VendingItem item = mInventory.get(itemImageId);

        if (null != item && item.getQuantity() > 0) {
            // Step 1 - make sure there is enough money to cover the purchase
            itWorked = mMoneyBox.purchase(item.getPrice());
            if (itWorked) {
                // Step 2 remove it from inventory
                itWorked = item.sellOne();
                if (itWorked) {
                    // notify UI to vend item
                } else {
                    // don't cheat the customer, refund the amount to the money box
                    mMoneyBox.insertAmount(item.getPrice());
                    if (item.getQuantity() == 0) {
                        // notify sold out
                    }
                }
            }
        }
        return itWorked;
    }

    // Add or update an item in stock.
    public void updateInventoryItem(VendingItem inventoryItem) {
        mInventory.put(inventoryItem.getId(), inventoryItem);

        // Notify UI there is an updated item
    }

    // Restocking allows us to clear out old (expired) items.
    // The inventory passed in is the complete list of what the machine now has.
    public void setInventory(List<VendingItem> inventory) {
        mInventory = new ConcurrentHashMap<Integer, VendingItem>();
        for (VendingItem item : inventory) {
            // Store item in inventory so it can be fetched out by item id
            mInventory.put(item.getId(), item);
        }

        // Notify UI there are new items
    }

    // Provide number of items we have in inventory for a particular item
    public int itemCount(int itemId) {
        int itemCount = 0;
        VendingItem item = mInventory.get(itemId);
        if (null != item)
            itemCount = item.getQuantity();
        return itemCount;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshaled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        // no special processing due to child classes or whatnot
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mMoneyBox.writeToParcel(dest, flags);
        dest.writeList((ArrayList<VendingItem>) mInventory.values());
    }

    private VendingMachine(Parcel in) {
        mMoneyBox = in.readParcelable(MoneyBox.class.getClassLoader());
        ArrayList<VendingItem> inventory = new ArrayList<VendingItem>();
        in.readList(inventory, ArrayList.class.getClassLoader());
        setInventory(inventory);
    }

    private ArrayList<VendingItem> getInventory() {
        return (ArrayList<VendingItem>) mInventory.values();
    }
}
