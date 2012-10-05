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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

/**
 * Represents a characteristic descriptor. This class is a data container that
 * allows easy access to characteristic descriptor values. A GATT characteristic
 * may contain multiple descriptor values containing related information about a
 * characteristic.
 */
public class BleDescriptor extends BleAttribute
        implements Parcelable
{
    private static final String TAG = "BleDescriptor";
    private BleCharacteristic mCharObj;
    protected HashMap<String, Integer> mClientcfgMap = new HashMap();

    /** @hide */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static final Parcelable.Creator<BleDescriptor> CREATOR = new Parcelable.Creator()
    {
        public BleDescriptor createFromParcel(Parcel source) {
            return new BleDescriptor(source);
        }

        public BleDescriptor[] newArray(int size)
        {
            return new BleDescriptor[size];
        }

    };

    public BleDescriptor(Parcel source)
    {
        super(source);
    }

    public BleDescriptor(BleGattID descID)
    {
        super(descID);
    }

    public void setCharRef(BleCharacteristic charObj)
    {
        this.mCharObj = charObj;
    }

    /**
     * Set the raw value bytes for this descriptor starting at a given offset,
     * 
     * @return {@link BleConstants#GATT_SUCCESS if successful}
     */
    @Override
    public byte setValue(byte[] value, int offset, int length, BleGattID gattUuid,
            int totalSize, String address)
    {
        int uuidType = gattUuid.getUuidType();
        int uuid = -1;
        Log.e("BleDescriptor", "#### UUID type=" + gattUuid.getUuidType());

        if (uuidType == 2) {
            uuid = gattUuid.getUuid16();
            if (uuid == -1) {
                Log.e("BleDescriptor", "setValue: Invalid handle (UUID16 not found)");
                return 1;
            }
        }
        if (uuid == 10500) {
            Log.i("BleDescriptor", "##Writing a Presentation format..");
        } else if (uuid == 10498) {
            Log.i("BleDescriptor", "##Writing a characteristic client config");
            if (totalSize > this.mMaxLength)
                return 13;
            int valueInt = 0;
            for (int i = 0; i < length; i++) {
                int shift = (length - 1 - i) * 8;
                valueInt += ((value[i] & 0xFF) << shift);
            }
            this.mClientcfgMap.put(address, Integer.valueOf(valueInt));
        } else if (gattUuid.equals(this.mID)) {
            Log.i("BleDescriptor", "##Writing a descriptor value..");
            Log.i("BleDescriptor", "##offset=" + offset + " mMaxLength=" + this.mMaxLength
                    + " length=" + length);
            super.setValue(value, offset, length, gattUuid, totalSize, address);
        }
        this.mDirty = true;
        return 0;
    }
}
