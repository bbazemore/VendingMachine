package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
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
public class Coinage implements Parcelable {
    List<Integer> mSupportedCoins;
    static final NumberFormat mPriceFormat = NumberFormat.getCurrencyInstance();

    public Coinage() {
        // default to US nickel, dime, quarter, Susan B. Anthony /Sacajawea
        mSupportedCoins = new ArrayList<>(Arrays.asList(5, 10, 25, 100));
    }

    public Coinage(List<Integer> coinValues) {
        // use the coin values provided
        mSupportedCoins = coinValues;
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
    public void addCoin(List<Integer> coinValueList) {
        for (Integer coin : coinValueList) addCoin(coin);
    }

    public boolean isValidCoin(Integer testCoin) {
        return mSupportedCoins.contains(testCoin);
    }

    public static String getDisplayValue(Integer coinValue) {
        return mPriceFormat.format((double) coinValue / 100);
    }

    public static List<Integer> getCoinListFromString(String coinValueStrings) {
        List<String> coinValueNames = Arrays.asList(coinValueStrings.split("\\s*,\\s*"));
        List<Integer> coinValues = new ArrayList<Integer>();
        for (String coinValueStr : coinValueNames) {
            coinValues.add(Integer.parseInt(coinValueStr));
        }
        return coinValues;
    }

    // Find the coin that will take the biggest chunk out of value,
    // without exceeding value. If all coins are larger than the
    // provided value, return 0.
    public int getLargestCoin(int value) {
        int largestCoinValue = 0;
        // Start at the end position of the list and work back
        ListIterator<Integer> iter = mSupportedCoins.listIterator(mSupportedCoins.size());
        while (iter.hasPrevious()) {
            int coinValue = iter.previous();
            if (value >= coinValue) {
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

    public List<Integer> getCoinList() {
        return mSupportedCoins;
    }

    // Given a money amount, return the coins that would be returned to
    // equal that amount
    public String getRefundChange(int refundValue) {
        int amountToRefund = refundValue;
        int smallestValue = getSmallestCoin();
        int lastCoinRefunded = 0;
        int coinToRefund;
        int currentCoinCount = 0;

        StringBuilder refundString = new StringBuilder();

        // Walk through the coin values, largest to smallest and note
        // the number of each coin used for the refund
        while (amountToRefund >= smallestValue) {
            coinToRefund = getLargestCoin(amountToRefund);
            amountToRefund -= coinToRefund;
            if (coinToRefund == lastCoinRefunded) {
                currentCoinCount++;
            } else {
                // We are done with the last coin processed
                // Add it to the refund string "<number of coins> - <coin value>"
                if (currentCoinCount != 0) {
                    refundString.append(Integer.toString(currentCoinCount))
                            .append(" - ")
                            .append(getDisplayValue(lastCoinRefunded))
                            .append(String.format("%n")); // system independent new line
                }
                // reset to this new coin
                lastCoinRefunded = coinToRefund;
                currentCoinCount = 1;
            }
        }
        // one last time to pick up the last coin from the while loop
        if (currentCoinCount != 0) {
            refundString.append(Integer.toString(currentCoinCount))
                    .append(" - ")
                    .append(getDisplayValue(lastCoinRefunded))
                    .append(String.format("%n")); // system independent new line
        }
        return refundString.toString();
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
        dest.writeList(mSupportedCoins);
    }

    private Coinage(Parcel in) {
        in.readList(mSupportedCoins, List.class.getClassLoader());
    }

    public final Parcelable.Creator<Coinage> CREATOR = new Parcelable.Creator<Coinage>() {
        @Override
        public Coinage createFromParcel(Parcel parcel) {
            return new Coinage(parcel);
        }

        @Override
        public Coinage[] newArray(int i) {
            return new Coinage[i];
        }

    };

}
