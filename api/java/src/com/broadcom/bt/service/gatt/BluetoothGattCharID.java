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

public final class BluetoothGattCharID
        implements Parcelable
{
    private final BluetoothGattID mSrvcId;
    private final BluetoothGattID mCharId;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static final Parcelable.Creator<BluetoothGattCharID> CREATOR =
            new Parcelable.Creator()
            {
                public BluetoothGattCharID createFromParcel(Parcel source)
                {
                    int instId = source.readInt();
                    int uuidtype = source.readInt();
                    int serviceType = source.readInt();
                    BluetoothGattID serviceId;
                    if (uuidtype == 16) {
                        String sServiceUuid = source.readString();
                        serviceId = new BluetoothGattID(instId, sServiceUuid, serviceType);
                    } else {
                        int serviceUuid = source.readInt();
                        serviceId = new BluetoothGattID(instId, serviceUuid, serviceType);
                    }

                    instId = source.readInt();
                    uuidtype = source.readInt();
                    BluetoothGattID charId;
                    if (uuidtype == 16) {
                        String sCharUuid = source.readString();
                        charId = new BluetoothGattID(instId, sCharUuid);
                    } else {
                        int charUuid = source.readInt();
                        charId = new BluetoothGattID(instId, charUuid);
                    }

                    return new BluetoothGattCharID(serviceId, charId);
                }

                public BluetoothGattCharID[] newArray(int size) {
                    return new BluetoothGattCharID[size];
                }

            };

    public BluetoothGattCharID(BluetoothGattID srvcId, BluetoothGattID charId)
    {
        this.mSrvcId = srvcId;
        this.mCharId = charId;
    }

    public BluetoothGattID getSrvcId()
    {
        return this.mSrvcId;
    }

    public BluetoothGattID getCharId()
    {
        return this.mCharId;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(BluetoothGattCharID target)
    {
        return (target.getCharId().equals(getCharId()))
                && (target.getSrvcId().equals(getSrvcId()));
    }

    public void writeToParcel(Parcel dest, int flags)
    {
        int serviceUuidType = this.mSrvcId.getUuidType();
        int charUuidType = this.mCharId.getUuidType();

        dest.writeInt(this.mSrvcId.getInstanceID());
        dest.writeInt(this.mSrvcId.getUuidType());
        dest.writeInt(this.mSrvcId.getServiceType());
        if (serviceUuidType == 16)
            dest.writeString(this.mSrvcId.toString());
        else {
            dest.writeInt(this.mSrvcId.getUuid16());
        }
        dest.writeInt(this.mCharId.getInstanceID());
        dest.writeInt(this.mCharId.getUuidType());
        if (charUuidType == 16)
            dest.writeString(this.mCharId.toString());
        else
            dest.writeInt(this.mCharId.getUuid16());
    }
    
    public String toString(){
        return "Service: " + this.getSrvcId() + ", Char: " + this.getCharId();
    }

}
