package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.bbkiszka.vendingmachine.vendevents.BalanceChangeEvent;
import com.android.bbkiszka.vendingmachine.vendevents.RefundedCoinsEvent;

/**
 * Handle money going in and out of the vending machine
 */
public class MoneyBox implements Parcelable {
    private final String LOG_TAG = MoneyBox.class.getSimpleName();
    Coinage mCoinage;
    int mMoneyCollected; // sum of money inserted into the box

    public MoneyBox(Coinage coinage) {
        mCoinage = coinage;
        mMoneyCollected = 0;
    }

    // TODO: allow entering different coin combinations in Settings
    public Coinage getCoinage() {
        return mCoinage;
    }

    public void setCoinage(Coinage newCoinage) {
        mCoinage = newCoinage;
    }

    public boolean insertCoin(int value) {
        boolean accepted = false;

        // Make sure coin is in the list
        if (mCoinage.isValidCoin(value)) {
            accepted = true;
            mMoneyCollected += value;

            BalanceChangeEvent balanceEvent = new BalanceChangeEvent(mMoneyCollected);
            VendingApplication.shareBus().post(balanceEvent);
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
    public int refund() {
        int refund = mMoneyCollected;
        mMoneyCollected = 0;
        if (refund > 0) {
            RefundedCoinsEvent refundEvent = new RefundedCoinsEvent(refund, mCoinage.getRefundChange(refund));
            VendingApplication.shareBus().post(refundEvent);
            BalanceChangeEvent balanceEvent = new BalanceChangeEvent(mMoneyCollected);
            VendingApplication.shareBus().post(balanceEvent);
        }
        return refund;
    }

    public int getCurrentBalance() {
        return mMoneyCollected;
    }

    public Boolean purchase(int itemCost) {
        Boolean purchaseComplete = false;
        int remainingCost = itemCost;
        if (itemCost <= mMoneyCollected) {
            // remove item cost coin by coin, we may end up taking
            // an extra coin if for some reason the price is smaller than
            // the smallest valid coin, e.g. 4 cents.
            while (remainingCost > 0) {
                int useCoin = mCoinage.getLargestCoin(remainingCost);
                if (useCoin == 0) {
                    useCoin = mCoinage.getSmallestCoin();
                }
                if (useCoin == 0) break; // something is wrong, empty coin set?, should ASSERT
                mMoneyCollected -= useCoin;
                remainingCost -= useCoin;
                Log.d(LOG_TAG, "Balance: " + mMoneyCollected + " after deducting " + useCoin);
            }
            if (remainingCost <= 0) {
                purchaseComplete = true;
            }
            Log.d(LOG_TAG, "Purchase of " + itemCost + " complete. Used " + mMoneyCollected + ", Balance: " + mMoneyCollected);
        }
        return purchaseComplete;
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

    public static final Creator<MoneyBox> CREATOR = new Creator<MoneyBox>() {
        @Override
        public MoneyBox createFromParcel(Parcel in) {
            return new MoneyBox(in);
        }

        @Override
        public MoneyBox[] newArray(int size) {
            return new MoneyBox[size];
        }
    };
}
