
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the extended property descriptor for a given characteristic.
 */
public class BleExtProperty extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleExtProperty";
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static final Parcelable.Creator<BleExtProperty> CREATOR = new Parcelable.Creator()
    {
        public BleExtProperty createFromParcel(Parcel source) {
            return new BleExtProperty(source);
        }

        public BleExtProperty[] newArray(int size)
        {
            return new BleExtProperty[size];
        }

    };

    public BleExtProperty(Parcel source)
    {
        super(source);
    }

    public BleExtProperty()
    {
        super(new BleGattID(BleConstants.GATT_UUID_CHAR_EXT_PROP16));
    }

}
