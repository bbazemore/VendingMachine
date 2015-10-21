package com.android.bbkiszka.vendingmachine;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


/**
 * This is where we are keeping global context for VendingMachine
 * which is mostly the event bus that provides messaging between the UI and the data back end
 */
public class VendingApplication extends Application {
    private final static String TAG = VendingApplication.class.getSimpleName();

    // Event bus used to communicate between threads
    private final static Bus mBus = new Bus(ThreadEnforcer.ANY); // we can get fancy later and allow injection BusProvider.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        mBus.register(this); //listen for "global" events
    }

    // The UI and data classes will use this one bus to communicate.
    // It lives here because main is a long-lived class that won't disappear when
    // a UI fragment disappears.
    public static Bus shareBus() {
        return mBus;
    }
}

