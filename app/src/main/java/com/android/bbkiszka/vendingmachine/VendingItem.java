package com.android.bbkiszka.vendingmachine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * VendingItem interface
 */
public class VendingItem implements Parcelable {
    int mImageResourceId; // picture of the item to sell
    int   mPrice; // price charged for the item
    // for more realism include height, width, depth, weight
    // But since this is Android - everything fits!

    public VendingItem(int imageId, int price) {
        mImageResourceId = imageId;
        mPrice = price;
    }

    private VendingItem(Parcel in){
        mImageResourceId = in.readInt();
        mPrice = in.readInt();
    }
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mImageResourceId);
        parcel.writeInt(mPrice);
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
    public int getImageId() {return mImageResourceId;}
    public int getPrice() {return mPrice;}
}
