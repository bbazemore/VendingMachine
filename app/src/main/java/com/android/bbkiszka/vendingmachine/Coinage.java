package com.android.bbkiszka.vendingmachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Set of coins supported by the Vending Machine.
 * Values are in pennies.
 * Avoid dollars since floats are imprecise and BigDecimal is more complex than needed for this exercise.
 */
public class Coinage {
    List<Integer> mSupportedCoins;

    public  Coinage() {
        // default to US nickel, dime, quarter, Susan B. Anthony /Sacajawea
        mSupportedCoins= new ArrayList<Integer>(Arrays.asList(5,10,25,100));
    }
    public Coinage(List<Integer> coinValues) {
        // use the coin values provided
        mSupportedCoins= coinValues;
    }
    public void clear() {
        mSupportedCoins.clear();
    }
    public void addCoin(Integer coinValue) {
        // Make sure we don't duplicate the coin in the coin collection
        if (!mSupportedCoins.contains(coinValue)) {
            mSupportedCoins.add(coinValue);

            // Keep the coins in small-to-large order
            Collections.sort(mSupportedCoins);
        }
    }
    // Take a whole list of coins for convenience
    public void addCoin(List<Integer> coinValueList){
        for (Integer coin: coinValueList) addCoin(coin);
    }

    public boolean isValidCoin(Integer testCoin) {
        return mSupportedCoins.contains(testCoin);
    }

    // Find the coin that will take the biggest chunk out of value,
    // without exceeding value. If all coins are larger than the
    // provided value, return 0.
    public int getLargestCoin(int value) {
        int largestCoinValue = 0;
        ListIterator<Integer> iter = mSupportedCoins.listIterator();
        while (iter.hasPrevious()) {
            int coinValue = iter.previous();
            if (value <= coinValue)  {
                largestCoinValue = coinValue;
                break;
            }
        }
        return largestCoinValue;
    }

    public int getSmallestCoin() {
        if (mSupportedCoins.size() == 0)
            return 0;
        else
            return mSupportedCoins.get(0);
    }
}
