package com.android.bbkiszka.vendingmachine.vendevents;

import android.util.Log;

import com.android.bbkiszka.vendingmachine.Coinage;

import java.util.Arrays;
import java.util.List;

/**
 * Track request for new set of coin values, e.g. "5,10,25"
 * Must be a comma separated list of integers
 */
public class CoinChangeEvent {
    private final static String TAG = CoinChangeEvent.class.getSimpleName();

    public final List<Integer> coinValueList;

    public CoinChangeEvent(String coinValueStrings) {
        Log.i(TAG, "CoinChangeEvent created");
        List<String> coinValueNames = Arrays.asList(coinValueStrings.split("\\s*,\\s*"));
        coinValueList = Coinage.getCoinListFromString(coinValueStrings);
    }
}
