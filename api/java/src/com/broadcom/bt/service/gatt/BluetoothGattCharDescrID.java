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
package com.broadcom.bt.service.gatt;

import android.os.Parcel;
import android.os.Parcelable;

import com.broadcom.bt.le.api.BleConstants;

public final class BluetoothGattCharDescrID
        implements Parcelable
{
    private final BluetoothGattID mSrvcId;
    private final BluetoothGattID mCharId;
    private final BluetoothGattID mDescrId;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static final Parcelable.Creator<BluetoothGattCharDescrID> CREATOR =
            new Parcelable.Creator()
            {
                public BluetoothGattCharDescrID createFromParcel(Parcel source) {
                    
                    BluetoothGattID serviceId = BluetoothGattID.CREATOR.createFromParcel(source);
                    BluetoothGattID charId = BluetoothGattID.CREATOR.createFromParcel(source);
                    BluetoothGattID descrId = BluetoothGattID.CREATOR.createFromParcel(source);
                    return new BluetoothGattCharDescrID(serviceId, charId, descrId);
                }

                public BluetoothGattCharDescrID[] newArray(int size) {
                    return new BluetoothGattCharDescrID[size];
                }
            };

    public BluetoothGattCharDescrID(BluetoothGattID srvcId, BluetoothGattID charId,
            BluetoothGattID descrId)
    {
        this.mSrvcId = srvcId;
        this.mCharId = charId;
        this.mDescrId = descrId;
    }

    public BluetoothGattID getSrvcId()
    {
        return this.mSrvcId;
    }

    public BluetoothGattID getCharId()
    {
        return this.mCharId;
    }

    public BluetoothGattID getDescrId()
    {
        return this.mDescrId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        this.mSrvcId.writeToParcel(dest, flags);
        this.mCharId.writeToParcel(dest, flags);
        this.mDescrId.writeToParcel(dest, flags);
    }

    public String toString(){
        return "Service: " + this.getSrvcId() + ", Char: " + this.getCharId()  + ", Descriptor: " + this.getDescrId();
    }
}
