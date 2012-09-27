/************************************************************************************
 *
 *  Copyright (C) 2012      Naranjo Manuel Francisco <naranjo.manuel@gmail.com>
 *  Copyright (C) 2009-2011 Broadcom Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/

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
