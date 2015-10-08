package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * VendingItem - can represent any item to put in the vending machine.
 * For a more plug and play style application VendingItem would be an interface
 * and each type of item would have its own class implementing that interface.
 * For now, just keeping it simple
 */
public class VendingItem implements Parcelable {
    int mImageResourceId; // picture of the item to sell, also serves as the unique id
    int   mPrice; // price charged for the item
    int mQuantity; // number of this type of item in stock.
    // int   mRestockThreshold; // when stock of this item gets this low, notify the vendor

    //public final static int MAX_QUANTITY = 100; // arbitrary limit to mimic physical limit

    // for more realism include height, width, depth, weight, wholesale cost
    // But since this is online in Android - everything fits! unlimited quantities! (well up to Integer.MAX_VALUE)

    public VendingItem(int imageId, int price) {
        mImageResourceId = imageId;
        mPrice = price;
    }

    public VendingItem(int imageId, int price, int quantity) {
        mImageResourceId = imageId;
        mPrice = price;
        mQuantity = quantity;
    }

    private VendingItem(Parcel in){
        mImageResourceId = in.readInt();
        mPrice = in.readInt();
        mQuantity = in.readInt();
    }
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mImageResourceId);
        parcel.writeInt(mPrice);
        parcel.writeInt(mQuantity);
    }

    @Override
    public int describeContents() {
        // no special processing due to child classes or whatnot
        return 0;
    }

    public final Parcelable.Creator<VendingItem> CREATOR = new Parcelable.Creator<VendingItem>() {
        @Override
        public VendingItem createFromParcel(Parcel parcel) {
            return new VendingItem(parcel);
        }

        @Override
        public VendingItem[] newArray(int i) {
            return new VendingItem[i];
        }

    };

    public int getId() {
        // Ideally this would be a stock code, barcode id, or some such
        // To keep it simple, just use the image resource id, which should be unique for this app.
        return mImageResourceId;
    }
    public int getImageId() {return mImageResourceId;}
    public int getPrice() {return mPrice;}

    public void setPrice(int price) {
        mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public boolean sellOne() {
        boolean sold = false;
        if (mQuantity > 0) {
            sold = true;
            mQuantity -= 1;
        }
        return sold;
    }

    public void setQuantity(int newQuantity) {
        mQuantity = newQuantity;
    }
}
