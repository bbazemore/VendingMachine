package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Handle money going in and out of the vending machine
 */
public class MoneyBox implements Parcelable {
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

    public boolean insertCoin(int value) {
        boolean accepted = false;

        // Make sure coin is in the list
        if (mCoinage.isValidCoin(value)) {
            accepted = true;
            mMoneyCollected += value;
        }

        // We don't have to worry about the money box getting full
        // so we don't have to check for exact change
        return accepted;
    }

    // Shortcut to add money when we don't want to figure out the coins needed.
    // This could be a negative value.
    public void insertAmount(int value) {
        mMoneyCollected += value;
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
        int remainingCost = itemCost;
        if (itemCost <= mMoneyCollected) {
            // remove item cost coin by coin, we may end up taking
            // an extra coin if for some reason the price is smaller than
            // the smallest valid coin, e.g. 4 cents.
            while (remainingCost > 0 && remainingCost <= itemCost) {
                int useCoin = mCoinage.getLargestCoin(remainingCost);
                if (useCoin == 0) {
                    useCoin = mCoinage.getSmallestCoin();
                }
                mMoneyCollected -= useCoin;
                remainingCost -= useCoin;
                Log.d(LOG_TAG, "Balance: " + mMoneyCollected + " after deducting " + useCoin);
            }
            purchaseComplete = true;
            Log.d(LOG_TAG, "Purchase of " + itemCost + " complete. Used " + mMoneyCollected + ", Balance: " + mMoneyCollected);
        }
        return purchaseComplete;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
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
        mCoinage.writeToParcel(dest, flags);
        dest.writeInt(mMoneyCollected);
    }

    public MoneyBox(Parcel in) {
        mCoinage = in.readParcelable(Coinage.class.getClassLoader());
        mMoneyCollected = in.readInt();
    }
}
