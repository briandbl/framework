
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

public class BleUserDescription extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleUserDescription";
    public static final Parcelable.Creator<BleUserDescription> CREATOR = new Parcelable.Creator()
    {
        public BleUserDescription createFromParcel(Parcel source) {
            return new BleUserDescription(source);
        }

        public BleUserDescription[] newArray(int size)
        {
            return new BleUserDescription[size];
        }

    };

    public BleUserDescription(Parcel source)
    {
        super(source);
    }

    public BleUserDescription()
    {
        super(new BleGattID(10497));
    }

}
