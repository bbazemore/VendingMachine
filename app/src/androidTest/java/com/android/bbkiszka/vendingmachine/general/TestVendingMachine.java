package com.android.bbkiszka.vendingmachine.general;

import android.test.AndroidTestCase;

import com.android.bbkiszka.vendingmachine.R;
import com.android.bbkiszka.vendingmachine.VendingItem;
import com.android.bbkiszka.vendingmachine.VendingMachine;

import java.util.ArrayList;
import java.util.List;

/**
 * Give the VendingMachine a run for the money
 */
public class TestVendingMachine extends AndroidTestCase {
    public static final String TAG = TestMoneyBox.class.getSimpleName();
    private VendingMachine vendingMachine;

    private void setup() {

        List<VendingItem> inventory = new ArrayList<VendingItem>();
        inventory.add(  new VendingItem(R.mipmap.ic_candy_cat,  100) );  //0
        inventory.add(  new VendingItem(R.mipmap.ic_candy_honeycomb,  20) ); // 1
        inventory.add(  new VendingItem(R.mipmap.ic_candy_ice_cream_sandwich,  30) ); // 2
        inventory.add(  new VendingItem(R.mipmap.ic_candy_jellybean,  4) ); // 3
        inventory.add(  new VendingItem(R.mipmap.ic_candy_kitkat,  55) ); // 4
        inventory.add(  new VendingItem(R.mipmap.ic_candy_lollipop,  20) ); // 5
        inventory.add(  new VendingItem(R.mipmap.ic_car_crosstek,  20) ); // 6
        inventory.add(  new VendingItem(R.mipmap.ic_car_elantra,  30) ); // 7
        inventory.add(  new VendingItem(R.mipmap.ic_car_fit_electric,  80) ); // 8
        inventory.add(  new VendingItem(R.mipmap.ic_car_prius,  60) ); // 9
        vendingMachine = new VendingMachine(inventory);
    }

    // try vending some things with no money in the box

    // add money to the box

    // try vending some things with not enough money

    // try vending some things with more than enough money
    // try vending some things exact money

    // Add  more inventory


}
