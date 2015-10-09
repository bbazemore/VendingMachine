package com.android.bbkiszka.vendingmachine.general;

import android.test.AndroidTestCase;

import com.android.bbkiszka.vendingmachine.R;
import com.android.bbkiszka.vendingmachine.VendingItem;
import com.android.bbkiszka.vendingmachine.VendingMachine;

/**
 * Give the VendingMachine a run for the money
 */
public class TestVendingMachine extends AndroidTestCase {
    public static final String TAG = TestMoneyBox.class.getSimpleName();
    private VendingMachine vendingMachine;
    private VendingItem[] inventory;

    public void setUp() {
        // set the vending machine up with some inventory
        // item initialization specifies image resource id, price, quantity
        inventory = new VendingItem[]{
                new VendingItem(R.mipmap.ic_candy_cat, 100, 3),  //0
                new VendingItem(R.mipmap.ic_candy_honeycomb, 20, 5), // 1
                new VendingItem(R.mipmap.ic_candy_ice_cream_sandwich, 30, 10), // 2
                new VendingItem(R.mipmap.ic_candy_jellybean, 4, 100), // 3
                new VendingItem(R.mipmap.ic_candy_kitkat, 55, 5), // 4
                new VendingItem(R.mipmap.ic_candy_lollipop, 0, 50), // 5   Free!
                new VendingItem(R.mipmap.ic_car_crosstek, 20, 2), // 6
                new VendingItem(R.mipmap.ic_car_elantra, 30, 1), // 7
                new VendingItem(R.mipmap.ic_car_fit_electric, 80, 3), // 8
                new VendingItem(R.mipmap.ic_car_prius, 60, 4)  // 9
        };
        vendingMachine = new VendingMachine(inventory);
    }

    // try vending some things with no money in the box
    public void testNoMoneyInBox() {
        assertFalse("Purchasing with no money returns false",
                vendingMachine.vendItem(R.mipmap.ic_car_fit_electric));
        assertTrue("Purchasing free item with no money returns true",
                vendingMachine.vendItem(R.mipmap.ic_candy_lollipop));
    }

    // try vending some things with some, but not enough money
    public void testVendWithNotEnoughMoney() {
        vendingMachine.insertCoin(25);
        vendingMachine.insertCoin(10);
        assertEquals("Current balance should be 35 cents", 35, vendingMachine.getCurrentBalance());

        assertFalse("Purchasing with not enough money returns false",
                vendingMachine.vendItem(R.mipmap.ic_car_fit_electric));
        assertEquals("Balance after failed purchase should be 35 cents", 35, vendingMachine.getCurrentBalance());

        assertTrue("Purchasing free item with no money returns true",
                vendingMachine.vendItem(R.mipmap.ic_candy_lollipop));
        assertEquals("Balance after no-cost purchase should be 35 cents", 35, vendingMachine.getCurrentBalance());

        assertFalse("Purchasing with not enough money returns false #2",
                vendingMachine.vendItem(R.mipmap.ic_car_prius));
        assertEquals("Balance after failed purchase should be 35 cents", 35, vendingMachine.getCurrentBalance());

    }


    // try vending some things with more than enough money
    public void testVendWithMoreThanEnoughMoney() {
        vendingMachine.insertCoin(100);
        vendingMachine.insertCoin(100);
        vendingMachine.insertCoin(100);
        vendingMachine.insertCoin(100);
        assertEquals("Current balance should be 400 cents", 400, vendingMachine.getCurrentBalance());

        assertTrue("Purchasing with more than enough money returns true",
                vendingMachine.vendItem(R.mipmap.ic_car_fit_electric));
        assertEquals("Balance after Fit purchase should be 320 cents", 320, vendingMachine.getCurrentBalance());

        assertTrue("Purchasing with more than enough money returns true #2",
                vendingMachine.vendItem(R.mipmap.ic_car_prius));
        assertEquals("Balance after Prius purchase should be 260 cents", 260, vendingMachine.getCurrentBalance());

        assertTrue("Purchasing with more than enough money returns true #3, purchase cost less than smallest coin",
                vendingMachine.vendItem(R.mipmap.ic_candy_jellybean));
        assertEquals("Balance after Jellybean purchase should be 255 cents", 255, vendingMachine.getCurrentBalance());
    }
    // try vending some things exact money
    public void testVendWithExactMoney() {
        vendingMachine.insertCoin(25);
        vendingMachine.insertCoin(25);
        vendingMachine.insertCoin(5);

        assertEquals("Current balance should be 55 cents", 55, vendingMachine.getCurrentBalance());
        assertTrue("Purchasing with exact change returns true #1",
                vendingMachine.vendItem(R.mipmap.ic_candy_kitkat));
        assertEquals("Balance after Kitkat purchase should be 0 cents", 0, vendingMachine.getCurrentBalance());

        vendingMachine.insertCoin(5);
        vendingMachine.insertCoin(5);
        vendingMachine.insertCoin(5);
        vendingMachine.insertCoin(5);
        assertEquals("Current balance should be 20 cents", 20, vendingMachine.getCurrentBalance());
        assertTrue("Purchasing with exact change returns true #2",
                vendingMachine.vendItem(R.mipmap.ic_candy_honeycomb));
        assertEquals("Balance after Honeycomb purchase should be 0 cents", 0, vendingMachine.getCurrentBalance());
    }

    // Test that inventory goes down and can be restocked
    public void testInventoryControl() {
        // Make sure we can cover the cost of 3 CrossTeks (60)
        vendingMachine.insertCoin(100);

        // There are two CrossTeks in stock from setup.  Buy both and then make sure we can't buy
        // a third.
        assertTrue("Purchasing first item in stock returns true #1",
                vendingMachine.vendItem(R.mipmap.ic_car_crosstek));
        assertEquals("Inventory after CrossTek purchase #1 should be 1", 1, vendingMachine.itemCount(R.mipmap.ic_car_crosstek));

        assertTrue("Purchasing second item in stock returns true #2",
                vendingMachine.vendItem(R.mipmap.ic_car_crosstek));
        assertEquals("Inventory after CrossTek purchase #2 should be 0", 0, vendingMachine.itemCount(R.mipmap.ic_car_crosstek));

        assertFalse("Purchasing out of stock item returns false",
                vendingMachine.vendItem(R.mipmap.ic_car_crosstek));
        assertEquals("Inventory after CrossTek purchase #3 should still be 0", 0, vendingMachine.itemCount(R.mipmap.ic_car_crosstek));

        // Restock with just one item.  This should clear all previous inventory.
        // Buy the item twice, try to buy an item that is not in the inventory.
        VendingItem[] testInventory = {new VendingItem(R.mipmap.ic_candy_cat, 100, 1)};
        vendingMachine.setInventory(testInventory);

        assertEquals("Inventory after only one item stocked should be 1", 1, vendingMachine.itemCount(R.mipmap.ic_candy_cat));
        assertEquals("Inventory after only item not stocked should be 0", 0, vendingMachine.itemCount(R.mipmap.ic_car_fit_electric));

        // Make sure there is enough money to cover purchases
        vendingMachine.insertCoin(100);
        vendingMachine.insertCoin(100);
        assertFalse("Purchasing item not in inventory returns false",
                vendingMachine.vendItem(R.mipmap.ic_car_fit_electric));
        assertEquals("Inventory after Fit purchase should be 0", 0, vendingMachine.itemCount(R.mipmap.ic_car_fit_electric));

        assertTrue("Purchasing only item in stock returns true",
                vendingMachine.vendItem(R.mipmap.ic_candy_cat));
        assertEquals("Inventory after only item purchase should be 0", 0, vendingMachine.itemCount(R.mipmap.ic_candy_cat));

        assertFalse("Purchasing with no items in stock returns false",
                vendingMachine.vendItem(R.mipmap.ic_candy_cat));
        assertEquals("Inventory after only item purchase should be 0", 0, vendingMachine.itemCount(R.mipmap.ic_candy_cat));

    }

}
