
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

public class BleServerConfig extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleServerConfig";
    public static final Parcelable.Creator<BleServerConfig> CREATOR = new Parcelable.Creator()
    {
        public BleServerConfig createFromParcel(Parcel source) {
            return new BleServerConfig(source);
        }

        public BleServerConfig[] newArray(int size)
        {
            return new BleServerConfig[size];
        }

    };

    public BleServerConfig(Parcel source)
    {
        super(source);
    }

    public BleServerConfig() {
        super(new BleGattID(10499));
    }

}
