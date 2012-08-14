
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a user description descriptor.
 */
public class BleUserDescription extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleUserDescription";
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
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
        super(new BleGattID(BleConstants.GATT_UUID_CHAR_DESCRIPTION16));
    }

}
