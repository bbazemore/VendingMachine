package com.android.bbkiszka.vendingmachine.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.bbkiszka.vendingmachine.Coinage;
import com.android.bbkiszka.vendingmachine.R;

import java.util.List;

// Used to display coins to the user, pretty simple
public class CoinAdapter extends ArrayAdapter<Integer> {
    TextView mCoinTextView = null;

    CoinAdapter(Activity context, List<Integer> coins) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        super(context, R.layout.list_item_coin, coins);
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This item in the list represents a coin of a particular value
        Integer coinValue = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_coin, parent, false);

            // Look up the view ids once, not every time the item is updated.
            // Yay better performance.
            mCoinTextView = (TextView) convertView.findViewById(R.id.coin_value);
        }

        // Display the coin value
        mCoinTextView.setText(Coinage.getDisplayValue(coinValue));
        return convertView;
    }
}
