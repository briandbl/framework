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

public final class BluetoothGattCharPres
        implements Parcelable
{
    public static final int GATT_FORMAT_RES = 0;
    public static final int GATT_FORMAT_BOOL = 1;
    public static final int GATT_FORMAT_2BITS = 2;
    public static final int GATT_FORMAT_NIBBLE = 3;
    public static final int GATT_FORMAT_UINT8 = 4;
    public static final int GATT_FORMAT_UINT12 = 5;
    public static final int GATT_FORMAT_UINT16 = 6;
    public static final int GATT_FORMAT_UINT24 = 7;
    public static final int GATT_FORMAT_UINT32 = 8;
    public static final int GATT_FORMAT_UINT48 = 9;
    public static final int GATT_FORMAT_UINT64 = 10;
    public static final int GATT_FORMAT_UINT128 = 11;
    public static final int GATT_FORMAT_SINT8 = 12;
    public static final int GATT_FORMAT_SINT12 = 13;
    public static final int GATT_FORMAT_SINT16 = 14;
    public static final int GATT_FORMAT_SINT24 = 15;
    public static final int GATT_FORMAT_SINT32 = 16;
    public static final int GATT_FORMAT_SINT48 = 17;
    public static final int GATT_FORMAT_SINT64 = 18;
    public static final int GATT_FORMAT_SINT128 = 19;
    public static final int GATT_FORMAT_FLOAT32 = 20;
    public static final int GATT_FORMAT_FLOAT64 = 21;
    public static final int GATT_FORMAT_SFLOAT = 22;
    public static final int GATT_FORMAT_FLOAT = 23;
    public static final int GATT_FORMAT_DUINT16 = 24;
    public static final int GATT_FORMAT_UTF8S = 25;
    public static final int GATT_FORMAT_UTF16S = 26;
    public static final int GATT_FORMAT_STRUCT = 27;
    public static final int GATT_FORMAT_MAX = 28;
    public int unit;
    public int descr;
    public int format;
    public int exp;
    public int nameSpc;
    public static final Parcelable.Creator<BluetoothGattCharPres> CREATOR = new Parcelable.Creator()
    {
        public BluetoothGattCharPres createFromParcel(Parcel source) {
            int unit = source.readInt();
            int descr = source.readInt();
            int format = source.readInt();
            int exp = source.readInt();
            int nameSpc = source.readInt();

            return new BluetoothGattCharPres(unit, descr, format, exp, nameSpc);
        }

        public BluetoothGattCharPres[] newArray(int size) {
            return new BluetoothGattCharPres[size];
        }

    };

    public BluetoothGattCharPres()
    {
        this.unit = 0;
        this.descr = 0;
        this.format = 0;
        this.exp = 0;
        this.nameSpc = 0;
    }

    public BluetoothGattCharPres(int unit, int dscp, int format, int exp, int nameSpc)
    {
        this.unit = unit;
        this.descr = this.descr;
        this.format = format;
        this.exp = exp;
        this.nameSpc = nameSpc;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.unit);
        dest.writeInt(this.descr);
        dest.writeInt(this.format);
        dest.writeInt(this.exp);
        dest.writeInt(this.nameSpc);
    }

}
