
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

public class BlePresentationFormat extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BlePresentationFormat";
    private byte[] mPresentFormat = new byte[7];

    public static final Parcelable.Creator<BlePresentationFormat> CREATOR = new Parcelable.Creator()
    {
        public BlePresentationFormat createFromParcel(Parcel source) {
            return new BlePresentationFormat(source);
        }

        public BlePresentationFormat[] newArray(int size)
        {
            return new BlePresentationFormat[size];
        }

    };

    public BlePresentationFormat(Parcel source)
    {
        super(source);
    }

    public BlePresentationFormat() {
        super(new BleGattID(10500));
    }

    public void setFormat(byte format)
    {
        this.mPresentFormat[0] = format;
        setValue(this.mPresentFormat);
    }

    public byte getFormat()
    {
        return this.mValue[0];
    }

    public void setExponent(byte exponent)
    {
        this.mPresentFormat[1] = exponent;
        setValue(this.mPresentFormat);
    }

    public byte getExponent()
    {
        return this.mValue[1];
    }

    public void setUnit(short unit)
    {
        this.mPresentFormat[2] = (byte) (unit & 0xFF);
        this.mPresentFormat[3] = (byte) (unit >> 8);
        setValue(this.mPresentFormat);
    }

    public short getUnit()
    {
        short nUnit = (short) ((this.mValue[2] & 0xFF) << 8 | this.mValue[3] & 0xFF);
        return nUnit;
    }

    public void setNameSpace(byte nameSpace)
    {
        this.mPresentFormat[4] = nameSpace;
        setValue(this.mPresentFormat);
    }

    public byte getNameSpace()
    {
        return this.mValue[4];
    }

    public void setDescr(short descr)
    {
        this.mPresentFormat[5] = (byte) (descr & 0xFF);
        this.mPresentFormat[6] = (byte) (descr >> 8);
        setValue(this.mPresentFormat);
    }

    public short getDescr()
    {
        short nDescr = (short) ((this.mValue[5] & 0xFF) << 8 | this.mValue[6] & 0xFF);
        return nDescr;
    }

}
