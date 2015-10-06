package com.android.bbkiszka.vendingmachine;

import android.util.Log;

/**
 * Handle money going in and out of the vending machine
 */
public class MoneyBox {
    private final String LOG_TAG = MoneyBox.class.getSimpleName();
    Coinage mCoinage;
    int     mMoneyCollected; // sum of money inserted into the box

    public  MoneyBox(Coinage coinage) {
        mCoinage = coinage;
        mMoneyCollected = 0;
    }

    // TODO: allow different coin combinations in Settings
    public void setCoinage(Coinage newCoinage) {
        mCoinage = newCoinage;
    }

    public Boolean insertCoin(int value) {
        Boolean accepted = false;

        // Make sure coin is in the list
        if (mCoinage.isValidCoin(value)) {
            accepted = true;
            mMoneyCollected += value;
        }

        // We don't have to worry about the money box getting full
        // so we don't have to check for exact change
        return accepted;
    }

    // pushing the refund button expels all coins from the box
    // New total is 0
    public int refund()
    {
        int refund = mMoneyCollected;
        mMoneyCollected = 0;
        return refund;
    }
    public int getCurrentBalance() { return mMoneyCollected;}

    public Boolean purchase( int itemCost) {
        Boolean purchaseComplete = false;
        int moneyUsed = 0;
        int remainingCost = itemCost;
        if (itemCost <= mMoneyCollected) {
            // remove item cost coin by coin, we may end up taking
            // an extra coin if for some reason the price is smaller than
            // the smallest valid coin, e.g. 4 cents.
            while (moneyUsed < itemCost) {
                int useCoin = mCoinage.getLargestCoin(remainingCost);
                if (useCoin == 0) {
                    useCoin = mCoinage.getSmallestCoin();
                }
                mMoneyCollected -= useCoin;
                moneyUsed += useCoin;
                Log.d(LOG_TAG, "Money used: " + moneyUsed + " after deducting " + useCoin);
            }
            purchaseComplete = true;
            Log.d(LOG_TAG, "Purchase of " + itemCost + " complete. Used " + moneyUsed + ", Balance: " + mMoneyCollected);
        }
        return purchaseComplete;
    }

}
