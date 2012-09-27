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

import com.broadcom.bt.service.gatt.BluetoothGattID;

import java.util.UUID;

/**
 * Identifies a Bluetooth GATT characteristic or attribute.
 */
public final class BleGattID extends BluetoothGattID
        implements Parcelable
{
    private static final String BASE_UUID_TPL = "%08x-0000-1000-8000-00805f9b34fb";
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static final Parcelable.Creator<BleGattID> CREATOR = new Parcelable.Creator() {
        public BleGattID createFromParcel(Parcel source) {
            int instId = source.readInt();
            int type = source.readInt();
            int serviceType = source.readInt();

            if (type == 16) {
                String sUuid = source.readString();
                return new BleGattID(instId, sUuid, serviceType);
            }
            int uuid = source.readInt();
            return new BleGattID(instId, uuid, serviceType);
        }

        public BleGattID[] newArray(int size)
        {
            return new BleGattID[size];
        }

    };

    public BleGattID(int instId, UUID uuid)
    {
        super(instId, uuid);
    }

    public BleGattID(int instId, UUID uuid, int serviceType) {
        super(instId, uuid, serviceType);
    }

    public BleGattID(int instId, long uuidLsb, long uuidMsb) {
        super(instId, uuidLsb, uuidMsb);
    }

    public BleGattID(long uuidLsb, long uuidMsb, int uuidType) {
        super(uuidLsb, uuidMsb, uuidType);
    }

    public BleGattID(int instId, int uuidType, long uuidLsb, long uuidMsb) {
        super(instId, uuidType, uuidLsb, uuidMsb);
    }

    public BleGattID(int instId, long uuidLsb, long uuidMsb, int serviceType) {
        super(instId, uuidLsb, uuidMsb, serviceType);
    }

    public BleGattID(int instId, int uuidType, long uuidLsb, long uuidMsb, int serviceType) {
        super(instId, uuidType, uuidLsb, uuidMsb, serviceType);
    }

    public BleGattID(int instId, String sUUID) {
        super(instId, sUUID);
    }

    public BleGattID(int instId, String sUUID, int serviceType) {
        super(instId, sUUID, serviceType);
    }

    public BleGattID(int instId, int uuid) {
        super(instId, uuid);
    }

    public BleGattID(int instId, int iUUID, int serviceType) {
        super(instId, iUUID, serviceType);
    }

    public BleGattID(UUID uuid) {
        super(uuid);
    }

    public BleGattID(UUID uuid, int serviceType) {
        super(uuid, serviceType);
    }

    public BleGattID(String sUUID) {
        super(sUUID);
    }

    public BleGattID(String sUUID, int serviceType) {
        super(sUUID, serviceType);
    }

    public BleGattID(int uuid) {
        super(uuid);
    }

    /**
     * Get the UUID type represented by this BleGattID (16 or 128 bit).
     */
    public UUID getUuid() {
        if (getUuidType() == BleConstants.GATT_UUID_TYPE_128) {
            Log.v(TAG, "returning 128b BleGattID " + super.getUuid());
            return super.getUuid();
        }
        Log.v(TAG, "making uuid out of 16b gattID " + Integer.toHexString(this.getUuid16()));
        return UUID.fromString(String.format(BASE_UUID_TPL,
                new Object[] {
                    Integer.valueOf(getUuid16())
                }));
    }

    public int getUuid16() {
        return super.getUuid16();
    }

    public int getUuidType()
    {
        return super.getUuidType();
    }

    public void setInstanceId(int instanceId) {
        super.setInstId(instanceId);
    }

    public int getInstanceID() {
        return super.getInstanceID();
    }

    public int getServiceType() {
        return super.getServiceType();
    }

    public void setServiceType(int serviceType) {
        super.setServiceType(serviceType);
    }

    public int hashCode() {
        return getUuid().hashCode();
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }

        if (this == target) {
            return true;
        }

        if (!(target instanceof BleGattID)) {
            return super.equals(target);
        }

        BleGattID lhs = (BleGattID) target;
        UUID mine = this.getUuid();
        UUID other = lhs.getUuid();
        Log.v(TAG, "compairing " + mine + " " + other);
        return getUuid().equals(lhs.getUuid());
    }

    public int describeContents() {
        return super.describeContents();
    }

    public String toString() {
        return getUuid().toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
