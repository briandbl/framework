
package com.broadcom.bt.le.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Presentation format descriptor.
 */
public class BlePresentationFormat extends BleDescriptor
        implements Parcelable
{
    private static final String TAG = "BlePresentationFormat";
    private byte[] mPresentFormat = new byte[7];

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
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

    /**
     * Assigns the format octet value of the presentation format descriptor of
     * this characteristic.
     */
    public void setFormat(byte format)
    {
        mPresentFormat[0] = format;
        setValue(mPresentFormat);
    }

    /**
     * Returns the format octet value of the presentation format descriptor of
     * this characteristic.
     */
    public byte getFormat()
    {
        return mValue[0];
    }

    /**
     * Sets the exponent component of the presentation format descriptor of this
     * characteristic.
     */
    public void setExponent(byte exponent)
    {
        mPresentFormat[1] = exponent;
        setValue(mPresentFormat);
    }

    /**
     * Retrieves the exponent component of the presentation format descriptor of
     * this characteristic.
     */
    public byte getExponent()
    {
        return mValue[1];
    }

    /**
     * Sets presentation format unit component.
     */
    public void setUnit(short unit)
    {
        mPresentFormat[2] = (byte) (unit & 0xFF);
        mPresentFormat[3] = (byte) (unit >> 8);
        setValue(mPresentFormat);
    }

    /**
     * Gets the unit part of the presentation descriptor of this characteristic.
     */
    public short getUnit()
    {
        short nUnit = (short) ((mValue[2] & 0xFF) << 8 | mValue[3] & 0xFF);
        return nUnit;
    }

    /**
     * Sets the presentation format descriptors name space octet.
     */
    public void setNameSpace(byte nameSpace)
    {
        mPresentFormat[4] = nameSpace;
        setValue(mPresentFormat);
    }

    /**
     * Returns presentation format descriptors name space octet.
     */
    public byte getNameSpace()
    {
        return mValue[4];
    }

    /**
     * Sets the presentation format descriptors description value.
     */
    public void setDescr(short descr)
    {
        mPresentFormat[5] = (byte) (descr & 0xFF);
        mPresentFormat[6] = (byte) (descr >> 8);
        setValue(mPresentFormat);
    }

    /**
     * Retrieves the format description component of the presentation format
     * descriptor of this characteristic.
     */
    public short getDescr()
    {
        short nDescr = (short) ((mValue[5] & 0xFF) << 8 | mValue[6] & 0xFF);
        return nDescr;
    }

}
