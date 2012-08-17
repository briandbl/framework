
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class representign a user defined descriptor.
 */
public class BleUserDescriptor extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleUserDescriptor";
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static final Parcelable.Creator<BleUserDescriptor> CREATOR = new Parcelable.Creator()
    {
        public BleUserDescriptor createFromParcel(Parcel source) {
            return new BleUserDescriptor(source);
        }

        public BleUserDescriptor[] newArray(int size)
        {
            return new BleUserDescriptor[size];
        }

    };

    public BleUserDescriptor(Parcel source)
    {
        super(source);
    }

    public BleUserDescriptor(BleGattID id)
    {
        super(id);
    }

}
