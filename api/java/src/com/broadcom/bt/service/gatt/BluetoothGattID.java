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
import android.util.Log;

import com.broadcom.bt.le.api.BleConstants;

import java.util.UUID;

public class BluetoothGattID
        implements Parcelable
{
    private int mInstId = 0;
    private UUID mUuid128 = null;
    private int mUuid16 = -1;
    private int mType = -1;
    private int mServiceType = -1;
    protected static final String TAG = "BluetoothGattID";
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    
    public static final Parcelable.Creator<BluetoothGattID> CREATOR = new Parcelable.Creator()
    {
        public BluetoothGattID createFromParcel(Parcel source) {
            int instId = source.readInt();
            int type = source.readInt();
            int serviceType = source.readInt();

            if (type == BleConstants.GATT_UUID_TYPE_128) {
                String sUuid = source.readString();
                return new BluetoothGattID(instId, sUuid, serviceType);
            }
            int uuid = source.readInt();
            return new BluetoothGattID(instId, uuid, serviceType);
        }

        public BluetoothGattID[] newArray(int size)
        {
            return new BluetoothGattID[size];
        }

    };

    private void initServiceType(int serviceType)
    {
        if ((serviceType == BleConstants.GATT_SERVICE_PRIMARY)
                || (serviceType == BleConstants.GATT_SERVICE_SECONDARY))
        {
            this.mServiceType = serviceType;
        }
    }

    public BluetoothGattID(int instId, UUID uuid)
    {
        this.mInstId = instId;
        this.mUuid128 = uuid;
        this.mType = BleConstants.GATT_UUID_TYPE_128;
    }

    public BluetoothGattID(int instId, UUID uuid, int serviceType) {
        this(instId, uuid);
        initServiceType(serviceType);
    }

    public BluetoothGattID(int instId, long uuidLsb, long uuidMsb)
    {
        this.mInstId = instId;
        this.mUuid128 = new UUID(uuidMsb, uuidLsb);
        this.mType = BleConstants.GATT_UUID_TYPE_128;
    }

    public BluetoothGattID(long uuidLsb, long uuidMsb, int uuidType) {
        if (uuidType == BleConstants.GATT_UUID_TYPE_128) {
            this.mUuid128 = new UUID(uuidMsb, uuidLsb);
            this.mType = BleConstants.GATT_UUID_TYPE_128;
        } else {
            this.mUuid16 = (int) uuidLsb;
            this.mType = BleConstants.GATT_UUID_TYPE_16;
        }
    }

    public BluetoothGattID(int instId, int uuidType, long uuidLsb, long uuidMsb) {
        this(uuidLsb, uuidMsb, uuidType);
        this.mInstId = instId;
    }

    public BluetoothGattID(int instId, long uuidLsb, long uuidMsb, int serviceType) {
        this(instId, uuidLsb, uuidMsb);
        initServiceType(serviceType);
    }

    public BluetoothGattID(int instId, int uuidType, long uuidLsb, long uuidMsb,
            int serviceType) {
        this(uuidLsb, uuidMsb, uuidType);
        this.mInstId = instId;
        initServiceType(serviceType);
    }

    public BluetoothGattID(int instId, String sUUID) {
        this.mInstId = instId;
        this.mUuid128 = UUID.fromString(sUUID);
        this.mType = BleConstants.GATT_UUID_TYPE_128;
    }

    public BluetoothGattID(int instId, String sUUID, int serviceType) {
        this(instId, sUUID);
        initServiceType(serviceType);
    }

    public BluetoothGattID(int instId, int uuid)
    {
        this.mInstId = instId;
        this.mUuid16 = uuid;
        this.mType = BleConstants.GATT_UUID_TYPE_16;
    }

    public BluetoothGattID(int instId, int iUUID, int serviceType) {
        this(instId, iUUID);
        initServiceType(serviceType);
    }

    public BluetoothGattID(UUID uuid)
    {
        this.mUuid128 = uuid;
        this.mType = BleConstants.GATT_UUID_TYPE_128;
    }

    public BluetoothGattID(UUID uuid, int serviceType) {
        this(uuid);
        initServiceType(serviceType);
    }

    public BluetoothGattID(String sUUID) {
        this.mUuid128 = UUID.fromString(sUUID);
        this.mType = BleConstants.GATT_UUID_TYPE_128;
    }

    public BluetoothGattID(String sUUID, int serviceType) {
        this(sUUID);
        initServiceType(serviceType);
    }

    public BluetoothGattID(int uuid)
    {
        this.mUuid16 = uuid;
        this.mType = BleConstants.GATT_UUID_TYPE_16;
    }

    public UUID getUuid()
    {
        if (this.mUuid128==null)
            return UUID.fromString(this.toString());
        return this.mUuid128;
    }

    public int getUuid16()
    {
        if (this.mUuid16 < 0){
            String uuid = mUuid128.toString();
            if (uuid.startsWith("0000") && uuid.endsWith("-0000-1000-8000-00805f9b34fb")){
                uuid=uuid.split("-")[0];
                return Integer.parseInt(uuid, 16);
            }
            Log.v(TAG, "tried to extract UUID 16 from non SIG UUID16");
        }
        return this.mUuid16;
    }

    public void setInstId(int instId)
    {
        this.mInstId = instId;
    }

    public int getUuidType()
    {
        return this.mType;
    }

    public int getInstanceID()
    {
        return this.mInstId;
    }

    public int getServiceType()
    {
        return this.mServiceType;
    }

    public void setServiceType(int serviceType) {
        this.mServiceType = serviceType;
    }

    public long getLeastSignificantBits() {
        if (this.mType == BleConstants.GATT_UUID_TYPE_128)
            return this.mUuid128.getLeastSignificantBits();
        return this.mUuid16;
    }

    public long getMostSignificantBits() {
        if (this.mType == BleConstants.GATT_UUID_TYPE_128)
            return this.mUuid128.getMostSignificantBits();
        return 0L;
    }

    public int hashCode()
    {
        if (this.mType == BleConstants.GATT_UUID_TYPE_128) {
            return this.mUuid128.hashCode();
        }
        return new Integer(this.mUuid16).hashCode();
    }

    public boolean equals(Object target)
    {
        Log.v(TAG, "comparing " + this + " with " + target);
        
        if (target == null) {
            return false;
        }

        if (this == target) {
            Log.v(TAG, "same object");
            return true;
        }
        
        if (!(target instanceof BluetoothGattID)) {
            Log.v(TAG, "wrong instance");
            return false;
        }

        BluetoothGattID targetId = (BluetoothGattID) target;
        if (this.mType != targetId.getUuidType()) {
            Log.v(TAG, "different types!, comparing String level");
            return this.toString().toLowerCase().equals(targetId.toString().toLowerCase());
        }
        
        if ((this.mType == BleConstants.GATT_UUID_TYPE_128)
                && (targetId.getInstanceID() == this.mInstId)
                && (this.mUuid128.equals(targetId.getUuid())))
        {
            Log.v(TAG, "128b equals");
            return true;
        }

        Log.v(TAG, "comparing 16b");
        return (this.mType == BleConstants.GATT_UUID_TYPE_16)
                && (targetId.getInstanceID() == this.mInstId)
                && (this.mUuid16 == targetId.getUuid16());
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mInstId);
        dest.writeInt(this.mType);
        dest.writeInt(this.mServiceType);

        if (this.mType == BleConstants.GATT_UUID_TYPE_128)
            dest.writeString(this.mUuid128.toString());
        else
            dest.writeInt(this.mUuid16);
    }

    public String toString()
    {
        if (this.mType == BleConstants.GATT_UUID_TYPE_128) {
            return this.mUuid128 == null ? null : this.mUuid128.toString();
        }
        return String.valueOf(String.format("0000%04x-0000-1000-8000-00805f9b34fb",
                new Object[] {
                    Integer.valueOf(0x0ffff & this.mUuid16)
                }));
    }
    
    public static BluetoothGattID getUuuid128FromUuid16(int uuid16){
        return new BluetoothGattID(new BluetoothGattID(uuid16).toString());
    }
}
