
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

public class BleClientConfig extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleClientConfig";

    public BleClientConfig(Parcel source)
    {
        super(source);
    }

    public BleClientConfig() {
        super(new BleGattID(10498));
    }

    public int getValue(String address)
    {
        return ((Integer) this.mClientcfgMap.get(address)).intValue();
    }

    public void setValue(String address, int clientConfig)
    {
        this.mClientcfgMap.put(address, Integer.valueOf(clientConfig));
    }

}
