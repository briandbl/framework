/*     */

package com.broadcom.bt.le.api;

/*     */
/*     */import android.os.Binder;
/*     */
import android.os.IBinder;
/*     */
import android.os.IInterface;
/*     */
import android.os.Parcel;
/*     */
import android.os.Parcelable.Creator;
/*     */
import android.os.RemoteException;
/*     */
import com.broadcom.bt.service.gatt.BluetoothGattID;

/*     */
/*     */public abstract interface IBleClientCallback extends IInterface
/*     */{
    /*     */public abstract void onAppRegistered(byte paramByte1, byte paramByte2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onAppDeregistered(byte paramByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onConnected(String paramString, int paramInt)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onDisconnected(int paramInt, String paramString)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onSearchResult(int paramInt, BluetoothGattID paramBluetoothGattID)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onSearchCompleted(int paramInt1, int paramInt2)
            /*     */throws RemoteException;

    /*     */
    /*     */public static abstract class Stub extends Binder
            /*     */implements IBleClientCallback
    /*     */{
        /*     */private static final String DESCRIPTOR = "com.broadcom.bt.le.api.IBleClientCallback";
        /*     */static final int TRANSACTION_onAppRegistered = 1;
        /*     */static final int TRANSACTION_onAppDeregistered = 2;
        /*     */static final int TRANSACTION_onConnected = 3;
        /*     */static final int TRANSACTION_onDisconnected = 4;
        /*     */static final int TRANSACTION_onSearchResult = 5;
        /*     */static final int TRANSACTION_onSearchCompleted = 6;

        /*     */
        /*     */public Stub()
        /*     */{
            /* 18 */attachInterface(this, "com.broadcom.bt.le.api.IBleClientCallback");
            /*     */}

        /*     */
        /*     */public static IBleClientCallback asInterface(IBinder obj)
        /*     */{
            /* 26 */if (obj == null) {
                /* 27 */return null;
                /*     */}
            /* 29 */IInterface iin = obj
                    .queryLocalInterface("com.broadcom.bt.le.api.IBleClientCallback");
            /* 30 */if ((iin != null) && ((iin instanceof IBleClientCallback))) {
                /* 31 */return (IBleClientCallback) iin;
                /*     */}
            /* 33 */return new Proxy(obj);
            /*     */}

        /*     */
        /*     */public IBinder asBinder() {
            /* 37 */return this;
            /*     */}

        /*     */
        /*     */public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            /* 41 */switch (code)
            /*     */{
            /*     */case 1598968902:
                    /* 45 */reply.writeString("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 46 */return true;
                    /*     */case 1:
                    /* 50 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 52 */byte _arg0 = data.readByte();
                    /*     */
                    /* 54 */byte _arg1 = data.readByte();
                    /* 55 */onAppRegistered(_arg0, _arg1);
                    /* 56 */reply.writeNoException();
                    /* 57 */return true;
                    /*     */case 2:
                    /* 61 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 63 */byte _arg0 = data.readByte();
                    /* 64 */onAppDeregistered(_arg0);
                    /* 65 */reply.writeNoException();
                    /* 66 */return true;
                    /*     */case 3:
                    /* 70 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 72 */String _arg0 = data.readString();
                    /*     */
                    /* 74 */int _arg1 = data.readInt();
                    /* 75 */onConnected(_arg0, _arg1);
                    /* 76 */reply.writeNoException();
                    /* 77 */return true;
                    /*     */case 4:
                    /* 81 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 83 */int _arg0 = data.readInt();
                    /*     */
                    /* 85 */String _arg1 = data.readString();
                    /* 86 */onDisconnected(_arg0, _arg1);
                    /* 87 */reply.writeNoException();
                    /* 88 */return true;
                    /*     */case 5:
                    /* 92 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 94 */int _arg0 = data.readInt();
                    /*     */BluetoothGattID _arg1;
                    /*     */BluetoothGattID _arg1;
                    /* 96 */if (0 != data.readInt()) {
                        /* 97 */_arg1 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 100 */_arg1 = null;
                        /*     */}
                    /* 102 */onSearchResult(_arg0, _arg1);
                    /* 103 */reply.writeNoException();
                    /* 104 */return true;
                    /*     */case 6:
                    /* 108 */data.enforceInterface("com.broadcom.bt.le.api.IBleClientCallback");
                    /*     */
                    /* 110 */int _arg0 = data.readInt();
                    /*     */
                    /* 112 */int _arg1 = data.readInt();
                    /* 113 */onSearchCompleted(_arg0, _arg1);
                    /* 114 */reply.writeNoException();
                    /* 115 */return true;
                    /*     */}
            /*     */
            /* 118 */return super.onTransact(code, data, reply, flags);
            /*     */}

        /*     */private static class Proxy implements IBleClientCallback {
            /*     */private IBinder mRemote;

            /*     */
            /*     */Proxy(IBinder remote) {
                /* 125 */this.mRemote = remote;
                /*     */}

            /*     */
            /*     */public IBinder asBinder() {
                /* 129 */return this.mRemote;
                /*     */}

            /*     */
            /*     */public String getInterfaceDescriptor() {
                /* 133 */return "com.broadcom.bt.le.api.IBleClientCallback";
                /*     */}

            /*     */
            /*     */public void onAppRegistered(byte status, byte clientIf) throws RemoteException {
                /* 137 */Parcel _data = Parcel.obtain();
                /* 138 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 140 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 141 */_data.writeByte(status);
                    /* 142 */_data.writeByte(clientIf);
                    /* 143 */this.mRemote.transact(1, _data, _reply, 0);
                    /* 144 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 147 */_reply.recycle();
                    /* 148 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onAppDeregistered(byte clientIf) throws RemoteException {
                /* 153 */Parcel _data = Parcel.obtain();
                /* 154 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 156 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 157 */_data.writeByte(clientIf);
                    /* 158 */this.mRemote.transact(2, _data, _reply, 0);
                    /* 159 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 162 */_reply.recycle();
                    /* 163 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onConnected(String deviceAddress, int connId) throws RemoteException {
                /* 168 */Parcel _data = Parcel.obtain();
                /* 169 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 171 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 172 */_data.writeString(deviceAddress);
                    /* 173 */_data.writeInt(connId);
                    /* 174 */this.mRemote.transact(3, _data, _reply, 0);
                    /* 175 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 178 */_reply.recycle();
                    /* 179 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onDisconnected(int connId, String deviceAddress)
                    throws RemoteException {
                /* 184 */Parcel _data = Parcel.obtain();
                /* 185 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 187 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 188 */_data.writeInt(connId);
                    /* 189 */_data.writeString(deviceAddress);
                    /* 190 */this.mRemote.transact(4, _data, _reply, 0);
                    /* 191 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 194 */_reply.recycle();
                    /* 195 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onSearchResult(int connId, BluetoothGattID srvcId)
                    throws RemoteException {
                /* 200 */Parcel _data = Parcel.obtain();
                /* 201 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 203 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 204 */_data.writeInt(connId);
                    /* 205 */if (srvcId != null) {
                        /* 206 */_data.writeInt(1);
                        /* 207 */srvcId.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 210 */_data.writeInt(0);
                        /*     */}
                    /* 212 */this.mRemote.transact(5, _data, _reply, 0);
                    /* 213 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 216 */_reply.recycle();
                    /* 217 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onSearchCompleted(int connId, int status) throws RemoteException {
                /* 222 */Parcel _data = Parcel.obtain();
                /* 223 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 225 */_data.writeInterfaceToken("com.broadcom.bt.le.api.IBleClientCallback");
                    /* 226 */_data.writeInt(connId);
                    /* 227 */_data.writeInt(status);
                    /* 228 */this.mRemote.transact(6, _data, _reply, 0);
                    /* 229 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 232 */_reply.recycle();
                    /* 233 */_data.recycle();
                    /*     */}
                /*     */}
            /*     */
        }
        /*     */
    }
    /*     */
}

/*
 * Location:
 * /opt/android/sdk/add-ons/addon-open_bluetooth_low-energy_api-broadcom
 * -10/libs/com.broadcom.bt.le.jar Qualified Name:
 * com.broadcom.bt.le.api.IBleClientCallback JD-Core Version: 0.6.0
 */
