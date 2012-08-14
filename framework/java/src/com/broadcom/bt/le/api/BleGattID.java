
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.broadcom.bt.service.gatt.BluetoothGattID;

import java.util.UUID;

public final class BleGattID extends BluetoothGattID
        implements Parcelable
{
    private static final String BASE_UUID_TPL = "%08x-0000-1000-8000-00805f9b34fb";
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

    public UUID getUuid() {
        if (getUuidType() == 16)
            return super.getUuid();
        return UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb",
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
            return false;
        }

        BleGattID lhs = (BleGattID) target;
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
