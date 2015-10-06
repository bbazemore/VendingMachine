package com.android.bbkiszka.vendingmachine;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic Vending Machine, this holds the inventory (data)
 *
 */
public class VendingMachine {
    ArrayList<VendingItem>  mInventory;
    MoneyBox                mMoneyBox;

    public VendingMachine(List<VendingItem> inventory) {

        // set up money box with default coinage
        mMoneyBox = new MoneyBox(new Coinage());

        // set up inventory of VendingItems
        mInventory = new ArrayList(inventory);
    }

    ArrayList<VendingItem> getInventory() { return mInventory;}

    Boolean vendItem( VendingItem item ) {
        Boolean itWorked = false;
        if (null != item) {
            itWorked = mMoneyBox.purchase(item.getPrice());
        }
        return itWorked;
    }

    public void restock(List<VendingItem> moreInventory) {
        mInventory.addAll(moreInventory);
    }
}
