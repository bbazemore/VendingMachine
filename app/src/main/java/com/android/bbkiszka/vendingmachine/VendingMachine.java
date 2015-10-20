package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.bbkiszka.vendingmachine.vendevents.InsufficientFundsEvent;
import com.android.bbkiszka.vendingmachine.vendevents.SoldOutEvent;
import com.android.bbkiszka.vendingmachine.vendevents.VendItemEvent;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic Vending Machine, this holds the inventory (data)
 * and is responsible for keeping the inventory quantities up to date.
 * It uses MoneyBox to handle money matters such as insertCoin and refund.
 * <p/>
 * Interacts with the UI by sending notifications on the event bus when
 * interesting things happen: item vended, sold out, restock needed.
 */
public class VendingMachine extends Observable implements Parcelable {
    // Keep stock in a concurrent hash map, indexed by the item id
    // Concurrent hashmap has decent performance and is thread safe.
    ConcurrentHashMap<Integer, VendingItem> mInventory;
    final MoneyBox mMoneyBox;

    public VendingMachine(VendingItem[] inventory) {

        // set up money box with default coinage
        mMoneyBox = new MoneyBox(new Coinage());

        // set up inventory of VendingItems so that we can look them
        // up by unique id, which is the image resource id.
        setInventory(inventory);
    }

    public static VendingItem[] getDefaultInventory() {
        return new VendingItem[]{
                new VendingItem(R.mipmap.ic_candy_cat, 100, 3),  //0
                new VendingItem(R.mipmap.ic_candy_honeycomb, 20, 5), // 1
                new VendingItem(R.mipmap.ic_candy_ice_cream_sandwich, 30, 10), // 2
                new VendingItem(R.mipmap.ic_candy_jellybean, 4, 100), // 3
                new VendingItem(R.mipmap.ic_candy_kitkat, 55, 5), // 4
                new VendingItem(R.mipmap.ic_candy_lollipop, 0, 50), // 5   Free!
                new VendingItem(R.mipmap.ic_car_crosstek, 20, 2), // 6
                new VendingItem(R.mipmap.ic_car_elantra, 30, 1), // 7
                new VendingItem(R.mipmap.ic_car_fit_electric, 80, 3), // 8
                new VendingItem(R.mipmap.ic_car_prius, 60, 4)}; // 9
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
                    VendItemEvent vendItem = new VendItemEvent(item.getId());
                    VendingApplication.shareBus().post(vendItem);
                } else {
                    // don't cheat the customer, refund the amount to the money box
                    mMoneyBox.insertAmount(item.getPrice());
                    if (item.getQuantity() == 0) {
                        // notify sold out
                        SoldOutEvent soldOut = new SoldOutEvent(item.getId());
                        VendingApplication.shareBus().post(soldOut);
                    }
                }
            } else {
                int fundsNeeded = item.getPrice() - mMoneyBox.getCurrentBalance();
                InsufficientFundsEvent fundsEvent = new InsufficientFundsEvent(item.getId(), item.getPrice(), fundsNeeded);
                VendingApplication.shareBus().post(fundsEvent);
            }
        } else  // doesn't exist or sold out
        {
            int itemId = (null == item) ? 0 : item.getId();
            // notify sold out
            SoldOutEvent soldOut = new SoldOutEvent(itemId);
            VendingApplication.shareBus().post(soldOut);
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
    public void setInventory(VendingItem[] inventory) {
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
     * @return a bitmask indicating the set of special object types marshaled
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
        dest.writeInt(mInventory.size());
        Object[] valuesToWrite = mInventory.values().toArray();
        dest.writeArray(valuesToWrite);
    }

    private VendingMachine(Parcel in) {
        mMoneyBox = in.readParcelable(MoneyBox.class.getClassLoader());
        VendingItem[] inventory = (VendingItem[]) in.readArray(VendingItem.class.getClassLoader());
        setInventory(inventory);
    }

    public VendingItem[] getInventory() {
        VendingItem[] inventoryItems = new VendingItem[mInventory.size()];
        mInventory.values().toArray(inventoryItems);
        return inventoryItems;
    }

    public Coinage getCoinage() {
        if (null != mMoneyBox)
            return mMoneyBox.getCoinage();
        return null;
    }
}
