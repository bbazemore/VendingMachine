package com.android.bbkiszka.vendingmachine.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.bbkiszka.vendingmachine.R;
import com.android.bbkiszka.vendingmachine.VendingApplication;
import com.android.bbkiszka.vendingmachine.VendingItem;
import com.android.bbkiszka.vendingmachine.VendingMachine;
import com.android.bbkiszka.vendingmachine.vendevents.SoldOutEvent;
import com.android.bbkiszka.vendingmachine.vendevents.VendItemEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Displays the vending machine items in a grid
 */
public class MainActivityFragment extends Fragment {
    private final static String TAG = MainActivityFragment.class.getSimpleName();
    private final static String KEY_INVENTORY_LIST = "com.android.bbkiszka.vendingmachine.inventory";

    // The data model
    VendingMachine mVendingMachine = null;

    // The view and view holders
    View mRootView = null;
    GridView mGridView = null;  // View holder so we don't have to keep looking it up
    VendingItemAdapter mAdapter = null;

    // event / message bus to talk to back end for any long running data requests
    Bus mBus = null;
    boolean mReceivingEvents = false;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set up the RESTful connection to the movie database
        // using our buddies Retrofit and Otto.
        receiveEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_vending_state))) {
            // Starting from scratch.  Use the defaults Luke.  That means default stock of items.
            mVendingMachine = new VendingMachine(VendingMachine.getDefaultInventory());
        } else {
            // Restore the vending stock as we last saw it.
            restoreState(savedInstanceState);
        }

        // Connect the UI with our fine list of vending items

        // Fill the grid with stock from the Vending Machine
        // The GridView really likes ArrayLists, so massage the data into an ArrayList
        // when passing it to the GridView through the adapter.
        VendingItem[] itemArray = mVendingMachine.getInventory();
        ArrayList<VendingItem> itemList = new ArrayList(Arrays.asList(itemArray));
        mAdapter = new VendingItemAdapter(getActivity(), itemList);
        mGridView = (GridView) mRootView.findViewById(R.id.vending_grid);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // User clicked on a movie at "position".
                // Attempt to sell the item to the user / customer
                VendingItem item = mAdapter.getItem(position);

                // This is a very quick data access.  If we were going
                // out to the internet, in a restful way, we would
                // expect the result of the operation to be delivered as an
                // event later on, through the bus.
                if (mVendingMachine.vendItem(item.getId())) {
                    // Play the vend item animation
                } else {
                    // play error sound
                }
            }
        });

        // TODO: set  up Balance display with MoneyBox
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INVENTORY_LIST, mVendingMachine);
        // save any other setting values, like coinage
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        restoreState(savedInstanceState);
        receiveEvents();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mVendingMachine = savedInstanceState.getParcelable(KEY_INVENTORY_LIST);
            //mVendingMachine = new VendingMachine(inventory);
            // restore any other setting values, like coinage
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // We are back on display. Pay attention to vending events again.
        receiveEvents();
        //updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Don't bother processing results when we aren't on display.
        stopReceivingEvents();
    }

    // Use some kind of injection, so that we can swap in a mock for tests.
    // Here we just use simple getter/setter injection for simplicity.
    private Bus getBus() {
        if (mBus == null) {
            setBus(VendingApplication.shareBus());
        }
        return mBus;
    }

    private void setBus(Bus bus) {
        mBus = bus;
    }

    private void receiveEvents() {
        if (!mReceivingEvents) {
            try {
                getBus().register(this);
                mReceivingEvents = true;
            } catch (Exception e) {
                Log.i(TAG, "receiveEvents could not register with Otto bus");
            }
        }
    }

    private void stopReceivingEvents() {

        if (mReceivingEvents) {
            try {
                getBus().unregister(this);
                mReceivingEvents = false;
            } catch (Exception e) {
                Log.i(TAG, "stopReceivingEvents could not unregister with Otto bus");
            }
        }
    }

    @Subscribe
    public void onVendItemEvent(VendItemEvent event) {
        Log.i(TAG, "onVendItemEvent ");

        // TODO: Display item vended animation, use Material Design curve motion
    }

    @Subscribe
    public void onSoldOutEvent(SoldOutEvent event) {
        Log.i(TAG, "onSoldOutEvent ");

        // TODO: Display sold out animation, use Material Design lift motion
    }
}
