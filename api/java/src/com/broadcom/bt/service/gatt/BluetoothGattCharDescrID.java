
package com.broadcom.bt.service.gatt;

import android.os.Parcel;
import android.os.Parcelable;

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

                    uuidtype = source.readInt();
                    BluetoothGattID descrId;
                    if (uuidtype == 16) {
                        String sDescrUuid = source.readString();
                        descrId = new BluetoothGattID(sDescrUuid);
                    } else {
                        int descrUuid = source.readInt();
                        descrId = new BluetoothGattID(descrUuid);
                    }

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
        int serviceUuidType = this.mSrvcId.getUuidType();
        int charUuidType = this.mCharId.getUuidType();
        int descrUuidType = this.mDescrId.getUuidType();

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
        else {
            dest.writeInt(this.mCharId.getUuid16());
        }
        dest.writeInt(this.mDescrId.getUuidType());
        if (charUuidType == 16)
            dest.writeString(this.mDescrId.toString());
        else
            dest.writeInt(this.mDescrId.getUuid16());
    }
}
