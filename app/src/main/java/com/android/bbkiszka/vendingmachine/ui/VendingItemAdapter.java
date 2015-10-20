package com.android.bbkiszka.vendingmachine.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bbkiszka.vendingmachine.Coinage;
import com.android.bbkiszka.vendingmachine.R;
import com.android.bbkiszka.vendingmachine.VendingItem;

import java.util.List;

/*
 *  VendingItemAdapter marries the vending item data (image) to the Grid UI
 */
public class VendingItemAdapter extends ArrayAdapter<VendingItem> {
    private static final String LOG_TAG = VendingItemAdapter.class.getSimpleName();

    ItemViewHolder mViewHolder;

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context   The current context. Used to inflate the layout file.
     * @param inventory A List of Movie objects to display in a list
     */
    public VendingItemAdapter(Activity context, List<VendingItem> inventory) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for a TextView and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, inventory);

        // we have the data from the start, so it will be displayed as soon
        // as we are hooked into the grid.
        // In RESTful situations addAll may be called later.
    }

    public void addAll(List<VendingItem> inventory) {
        // Clear previous inventory
        clear();

        // restock with new inventory
        for (VendingItem itemData : inventory) {
            add(itemData);
        }
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
        // Gets the VendingItem object from the ArrayAdapter at the appropriate position
        VendingItem item = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_vend, parent, false);

            // Look up the view ids once, not every time the item is updated.
            // Yay better performance.
            mViewHolder = new ItemViewHolder(convertView);
        }

        // Display the price with the product. Change from pennies to dollars
        String price = Coinage.getDisplayValue(item.getPrice());
        mViewHolder.priceView.setText(price);
        mViewHolder.productView.setImageResource(item.getImageId());
        // Display the image of the product - let Picasso paint the corners :)
       /* Picasso.with(convertView.getContext())
                .load(item.getImageId())
                        //.placeholder(R.mipmap.ic_launcher) too busy looking
                .error(R.mipmap.ic_error)         // optional
                .into(mViewHolder.productView);
        */
        return convertView;
    }

    // Handy dandy little class to cache the View ids so we don't keep looking for them every
    // time we refresh the UI.  We only need to fetch them after the inflate in onCreateView
    class ItemViewHolder {
        final TextView priceView;
        final ImageView productView;

        ItemViewHolder(View rootView) {
            priceView = (TextView) rootView.findViewById(R.id.list_item_price);
            productView = (ImageView) rootView.findViewById(R.id.list_item_imageview);
        }
    }
}
