
package com.broadcom.bt.service.gatt;

import android.os.Parcel;
import android.os.Parcelable;

public final class BluetoothGattInclSrvcID
        implements Parcelable
{
    private final BluetoothGattID mSrvcId;
    private final BluetoothGattID mInclSrvcId;
    
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static final Parcelable.Creator<BluetoothGattInclSrvcID> CREATOR = new Parcelable.Creator()
    {
        public BluetoothGattInclSrvcID createFromParcel(Parcel source) {
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
            serviceType = source.readInt();
            BluetoothGattID inclServiceId;
            if (uuidtype == 16) {
                String sInclServiceUuid = source.readString();
                inclServiceId = new BluetoothGattID(instId, sInclServiceUuid, serviceType);
            } else {
                int inclServiceUuid = source.readInt();
                inclServiceId = new BluetoothGattID(instId, inclServiceUuid, serviceType);
            }

            return new BluetoothGattInclSrvcID(serviceId, inclServiceId);
        }

        public BluetoothGattInclSrvcID[] newArray(int size) {
            return new BluetoothGattInclSrvcID[size];
        }

    };

    public BluetoothGattInclSrvcID(BluetoothGattID srvcId, BluetoothGattID inclSrvcId)
    {
        this.mSrvcId = srvcId;
        this.mInclSrvcId = inclSrvcId;
    }

    public BluetoothGattID getSrvcId()
    {
        return this.mSrvcId;
    }

    public BluetoothGattID getInclSrvcId()
    {
        return this.mInclSrvcId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int serviceUuidType = this.mSrvcId.getUuidType();
        int inclServiceUuidType = this.mInclSrvcId.getUuidType();

        dest.writeInt(this.mSrvcId.getInstanceID());
        dest.writeInt(this.mSrvcId.getUuidType());
        dest.writeInt(this.mSrvcId.getServiceType());
        if (serviceUuidType == 16)
            dest.writeString(this.mSrvcId.toString());
        else {
            dest.writeInt(this.mSrvcId.getUuid16());
        }
        dest.writeInt(this.mInclSrvcId.getInstanceID());
        dest.writeInt(this.mInclSrvcId.getUuidType());
        dest.writeInt(this.mInclSrvcId.getServiceType());
        if (inclServiceUuidType == 16)
            dest.writeString(this.mInclSrvcId.toString());
        else
            dest.writeInt(this.mInclSrvcId.getUuid16());
    }

}
