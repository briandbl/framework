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
