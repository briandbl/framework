
package com.broadcom.bt.le.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a client configuration descriptor.
 */
public class BleClientConfig extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BleClientConfig";

    public BleClientConfig(Context context, BleGattID profileUuid) {
        super(profileUuid);
    }

    public BleClientConfig(Parcel parcel)
    {
        super(parcel);
    }

    public BleClientConfig() {
        super(new BleGattID(BleConstants.GATT_UUID_CHAR_CLIENT_CONFIG16));
    }

    /**
     * Gets the client configuration descriptor for this characteristic that is
     * assigned to a specific remote device.
     * 
     * @param address - Remote device address ("00:11:22:33:44:55" format)
     */
    public int getValue(String address)
    {
        return this.mClientcfgMap.get(address).intValue();
    }

    /**
     * Assigns a client configuration descriptor for this characteristic to a
     * given remote device. This allows the same characteristic to report
     * per-client configuration descriptors.
     * 
     * @param address - Remote device address ("00:11:22:33:44:55" format)
     * @param clientConfig - Client configuration characteristic bits
     */
    public void setValue(String address, int clientConfig)
    {
        this.mClientcfgMap.put(address, Integer.valueOf(clientConfig));
    }

}
