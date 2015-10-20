package com.android.bbkiszka.vendingmachine.general;

import android.test.AndroidTestCase;

import com.android.bbkiszka.vendingmachine.Coinage;
import com.android.bbkiszka.vendingmachine.MoneyBox;

import java.util.Arrays;

/**
 * Based on http://en.literateprograms.org/Testing_Vending_Machine_(Java,_Junit)
 */
public class TestMoneyBox extends AndroidTestCase {

    public static final String TAG = TestMoneyBox.class.getSimpleName();
    private MoneyBox moneyBox;

    /*
    This function gets called before each test is executed to delete the database.  This makes
    sure that we always have a clean test.
    */
    public void setUp() {
        // We could use the non-default set of coins to make it more interesting
        Coinage testCoinage = new Coinage(Arrays.asList(5, 10, 25, 100));
        moneyBox = new MoneyBox(testCoinage);
    }

    /*
    Start testing with the programmer's favorite start number - zero.
     */
    public void testBalanceIsInitiallyZero() {
        assertEquals("Balance of money box is initially zero",
                0,
                moneyBox.getCurrentBalance());
    }

    /*
    Try adding an invalid coin
     */
    public void testInvalidCoin() {
        assertFalse("Inserting an invalid coin returns false",
                moneyBox.insertCoin(13));
    }

    /*
    Try adding valid coins
     */
    public void testValidCoins() {
        assertTrue("Insert Quarter", moneyBox.insertCoin(25));
        assertTrue("Insert Dime", moneyBox.insertCoin(10));
        assertTrue("Insert Nickel", moneyBox.insertCoin(5));
        assertTrue("Insert Dollar", moneyBox.insertCoin(100));
        assertEquals("Total from valid coins is 140", 140, moneyBox.getCurrentBalance());
    }

    public void testRefund() {
        // Set up a current balance of 140
        moneyBox.insertCoin(100);
        moneyBox.insertCoin(25);
        moneyBox.insertCoin(10);
        moneyBox.insertCoin(5);
        assertEquals("Balance after inserting coins 140", 140, moneyBox.getCurrentBalance());
        // Now ask for a refund of the balance
        assertEquals("Test refund returns balance", 140, moneyBox.refund());
        assertEquals("Test refund zeroes balance", 0, moneyBox.getCurrentBalance());
    }

    public void testMixedCoins() {
        assertTrue("Insert Quarter 1", moneyBox.insertCoin(25));
        assertTrue("Insert Quarter 2", moneyBox.insertCoin(25));
        assertTrue("Insert Quarter 3", moneyBox.insertCoin(25));
        assertTrue("Insert Dollar", moneyBox.insertCoin(100));
        assertEquals("Balance after inserting coins 175", 175, moneyBox.getCurrentBalance());
    }

    public void testPurchase() {
        // Set up a current balance of 175
        moneyBox.insertCoin(100);
        moneyBox.insertCoin(25);
        moneyBox.insertCoin(25);
        moneyBox.insertCoin(25);
        assertEquals("Balance after inserting coins 175", 175, moneyBox.getCurrentBalance());

        assertFalse("Try to purchase without sufficient funds 1.", moneyBox.purchase(300));
        assertFalse("Try to purchase without sufficient funds 2.", moneyBox.purchase(3000000));
        assertFalse("Try to purchase without sufficient funds 3.", moneyBox.purchase(176));
        assertEquals("Balance after invalid purchases 175", 175, moneyBox.getCurrentBalance());

        assertTrue("Try to purchase with sufficient funds free!", moneyBox.purchase(0));
        assertEquals("Balance after purchase 175", 175, moneyBox.getCurrentBalance());
        assertTrue("Try to purchase with sufficient funds cheap", moneyBox.purchase(5));
        assertEquals("Balance after purchase 170", 170, moneyBox.getCurrentBalance());
        assertTrue("Try to purchase with sufficient funds dime", moneyBox.purchase(10));
        assertEquals("Balance after purchase 160", 160, moneyBox.getCurrentBalance());
        assertTrue("Try to purchase with sufficient funds mixed", moneyBox.purchase(55));
        assertEquals("Balance after purchase 100", 105, moneyBox.getCurrentBalance());
        assertTrue("Try to purchase with sufficient funds dollar", moneyBox.purchase(100));
        assertEquals("Balance after purchase 100", 5, moneyBox.getCurrentBalance());
        assertTrue("Try to purchase with value that does not match coin", moneyBox.purchase(3));
        assertEquals("Balance after purchase for 3 cents should use a nickel", 0, moneyBox.getCurrentBalance());
    }

}
