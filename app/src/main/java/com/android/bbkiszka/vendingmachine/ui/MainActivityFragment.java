package com.android.bbkiszka.vendingmachine.ui;

import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bbkiszka.vendingmachine.Coinage;
import com.android.bbkiszka.vendingmachine.R;
import com.android.bbkiszka.vendingmachine.VendingApplication;
import com.android.bbkiszka.vendingmachine.VendingItem;
import com.android.bbkiszka.vendingmachine.VendingMachine;
import com.android.bbkiszka.vendingmachine.vendevents.BalanceChangeEvent;
import com.android.bbkiszka.vendingmachine.vendevents.CoinChangeEvent;
import com.android.bbkiszka.vendingmachine.vendevents.InsufficientFundsEvent;
import com.android.bbkiszka.vendingmachine.vendevents.InventoryChangedEvent;
import com.android.bbkiszka.vendingmachine.vendevents.InventoryRestockRequestEvent;
import com.android.bbkiszka.vendingmachine.vendevents.RefundedCoinsEvent;
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
    private final static String KEY_MONEY_BOX = "com.android.bbkiszka.vendingmachine.moneybox";

    // The data model
    VendingMachine mVendingMachine = null;

    // The view and view holders
    View mRootView = null;
    VendingItemAdapter mAdapter = null;
    CoinAdapter mCoinAdapter = null;
    ViewHolder mHolder = null;

    // event / message bus to talk to back end for any long running data requests
    protected Bus mBus = null;
    protected boolean mReceivingEvents = false;

    public MainActivityFragment() {
    }

    // Cache child views here to avoid multiple look ups and improve performance
    public class ViewHolder {
        public final GridView mGridView;   // View holder so we don't have to keep looking it up
        public final TextView mBalanceView;
        public final ImageButton mInsertCoinButton;
        public final ImageButton mRefundButton;
        public final ListView mCoinSelectView;

        public View mItemBeingVendedView; // Cache the vending item being processed so we can update the UI

        public ViewHolder(View view) {
            mGridView = (GridView) view.findViewById(R.id.vending_grid);
            mBalanceView = (TextView) view.findViewById(R.id.vending_balance);
            mInsertCoinButton = (ImageButton) view.findViewById(R.id.vending_insert_coin);
            mRefundButton = (ImageButton) view.findViewById(R.id.vending_refund);
            mCoinSelectView = (ListView) view.findViewById(R.id.vending_coin_select);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set up the RESTful connection to the vending machine
        // using our buddy Otto bus.
        receiveEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.key_vending_state))) {
            // Starting from scratch.  Use the defaults Luke.  That means default stock of items
            // and default United States coinage.
            mVendingMachine = new VendingMachine(VendingMachine.getDefaultInventory());
            mVendingMachine.setCoinage(VendingMachine.getPreferredCoinage(getActivity()));
        } else {
            // Restore the vending stock as we last saw it.
            restoreState(savedInstanceState);
        }

        // Fill the grid with stock from the Vending Machine
        // The GridView really likes ArrayLists, so massage the data into an ArrayList
        // when passing it to the GridView through the adapter.
        VendingItem[] itemArray = mVendingMachine.getInventory();
        ArrayList<VendingItem> itemList = new ArrayList<VendingItem>(Arrays.asList(itemArray));
        mAdapter = new VendingItemAdapter(getActivity(), itemList);
        mHolder = new ViewHolder(mRootView);
        mHolder.mGridView.setAdapter(mAdapter);
        mHolder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // User clicked on the vending item at "position".
                mHolder.mItemBeingVendedView = v;
                // Attempt to sell the item to the user / customer
                VendingItem item = mAdapter.getItem(position);

                // This is a very quick data access.  If we were going
                // out to the internet, in a restful way, we would
                // expect the result of the operation to be delivered as an
                // event later on, through the bus.
                mVendingMachine.vendItem(item.getId());
            }
        });

        // Set up Insert Coin and Balance display with MoneyBox.
        updateBalance();
        mCoinAdapter = new CoinAdapter(getActivity(), mVendingMachine.getCoinage().getCoinList());
        mHolder.mCoinSelectView.setAdapter(mCoinAdapter);
        mHolder.mCoinSelectView.setOnItemClickListener(new ListView.OnItemClickListener() {

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int coinValue = mCoinAdapter.getItem(position);
                mVendingMachine.insertCoin(coinValue);
                updateBalance();  // Because this fragment is hogging the BalanceChanged event
            }
        });
        mHolder.mInsertCoinButton.setOnClickListener(new ImageButton.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                int toggle = mHolder.mCoinSelectView.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE;
                mHolder.mCoinSelectView.setVisibility(toggle);
            }
        });

        // Set up the refund button
        mHolder.mRefundButton.setOnClickListener(new ImageButton.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // Issue a refund. This will send a RefundedCoinsEvent to update the UI
                mVendingMachine.refund();
            }
        });

        //mRootView.setTag(mHolder);  // not necessary since we stored it as a member variable
        return mRootView;
    }

    private void updateBalance() {
        mHolder.mBalanceView.setText(Coinage.getDisplayValue(mVendingMachine.getCurrentBalance()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INVENTORY_LIST, mVendingMachine);
        // save any other setting values, like restock preference
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
        }
        // purposely lose our place since the old view is probably gone
        mHolder.mItemBeingVendedView = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Pick up any preference changes
        mVendingMachine.setCoinage(VendingMachine.getPreferredCoinage(getActivity()));

        // We are back on display. Pay attention to vending events again.
        receiveEvents();
        updateBalance();  // the only thing in the UI that changes so far
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

    /*  Not used
        @Subscribe
        public void onInsertCoinEvent(InsertCoinEvent event) {
            mVendingMachine.insertCoin(event.coinValue);
        }
    */
    @Subscribe
    public void onBalanceChangeEvent(BalanceChangeEvent event) {
        updateBalance();
    }

    @Subscribe
    public void onInventoryRestockRequestEvent(InventoryRestockRequestEvent event) {
        Log.d(TAG, "onInventoryRestockRequestEvent received ");

        // Restock the vending machine with the default items
        mVendingMachine.setInventory(VendingMachine.getDefaultInventory());

        // TODO: allow user to specify quantities in Settings, then pass the new
        // quantities in the event
    }

    @Subscribe
    public void onInventoryChangedEvent(InventoryChangedEvent event) {
        Log.d(TAG, "onInventoryChangedEvent ");
        // Get the current inventory from the VendingMachine and
        // display it.
        mHolder.mItemBeingVendedView = null;
        mAdapter.addAll(mVendingMachine.getInventory());

        // Possible future: fine tune and only update quantities for one item at a time
    }
    @Subscribe
    public void onVendItemEvent(VendItemEvent event) {
        Log.d(TAG, "onVendItemEvent ");
        Toast.makeText(getActivity(), R.string.vend_purchase_complete, Toast.LENGTH_LONG).show();
        updateBalance();
        // TODO: Display item vended animation, use Material Design curve motion

        if (mVendingMachine.itemCount(event.itemId) == 0) {
            // post sold out icon
            if (null != mHolder.mItemBeingVendedView) {
                VendingItemAdapter.SellOut(mHolder.mItemBeingVendedView);
            }
        }
        // done vending this item
        mHolder.mItemBeingVendedView = null;
    }

    @Subscribe
    public void onSoldOutEvent(SoldOutEvent event) {
        Log.d(TAG, "onSoldOutEvent ");

        // post sold out icon
        if (null != mHolder.mItemBeingVendedView) {
            VendingItemAdapter.SellOut(mHolder.mItemBeingVendedView);
        }

        Toast.makeText(getActivity(), R.string.vend_sold_out, Toast.LENGTH_SHORT).show();

        // TODO: Display sold out animation, use Material Design lift motion
        // done vending this item
        mHolder.mItemBeingVendedView = null;
    }

    @Subscribe
    public void onInsufficientFundsEvent(InsufficientFundsEvent event) {
        Log.d(TAG, "onInsufficientFundsEvent ");
        // No sale because not enough money in the money box
        // Simple notification
        String message = String.format(getString(R.string.vend_insufficient_funds),
                Coinage.getDisplayValue(event.additionalAmountRequired));
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        // TODO: Remove, notification is noisy, obnoxious overkill for this app
        /*
        //Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setSound(soundUri); //This sets the sound to play

        //Display notification
        notificationManager.notify(0, mBuilder.build());
        */
    }

    @Subscribe
    public void onRefundedCoinsEvent(RefundedCoinsEvent event) {
        StringBuilder message = new StringBuilder();
        message.append(String.format(getString(R.string.vend_refund_notice),
                Coinage.getDisplayValue(event.refundAmount)))
                .append(String.format("%n")) // system independent new line
                .append(event.refundCoinsMessage);

        updateBalance();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onCoinChangeEvent(CoinChangeEvent event) {
        // New coin values
        mVendingMachine.setCoinage(event.coinValueList);
        mCoinAdapter = new CoinAdapter(getActivity(), mVendingMachine.getCoinage().getCoinList());
        mHolder.mCoinSelectView.setAdapter(mCoinAdapter);
    }
}
