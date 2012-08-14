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
/*     */public abstract interface IBleCharacteristicDataCallback extends IInterface
/*     */{
    /*     */public abstract void onGetFirstCharacteristic(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onGetFirstCharacteristicDescriptor(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            BluetoothGattID paramBluetoothGattID3)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onGetNextCharacteristic(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onGetNextCharacteristicDescriptor(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            BluetoothGattID paramBluetoothGattID3)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onReadCharacteristicValue(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            byte[] paramArrayOfByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onReadCharDescriptorValue(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            BluetoothGattID paramBluetoothGattID3, byte[] paramArrayOfByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onWriteCharValue(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onWriteCharDescrValue(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            BluetoothGattID paramBluetoothGattID3)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onRegForNotifications(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onUnregisterNotifications(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onNotify(int paramInt, String paramString,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2,
            boolean paramBoolean, byte[] paramArrayOfByte)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onGetFirstIncludedService(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onGetNextIncludedService(int paramInt1, int paramInt2,
            BluetoothGattID paramBluetoothGattID1, BluetoothGattID paramBluetoothGattID2)
            /*     */throws RemoteException;

    /*     */
    /*     */public static abstract class Stub extends Binder
            /*     */implements IBleCharacteristicDataCallback
    /*     */{
        /*     */private static final String DESCRIPTOR = "com.broadcom.bt.le.api.IBleCharacteristicDataCallback";
        /*     */static final int TRANSACTION_onGetFirstCharacteristic = 1;
        /*     */static final int TRANSACTION_onGetFirstCharacteristicDescriptor = 2;
        /*     */static final int TRANSACTION_onGetNextCharacteristic = 3;
        /*     */static final int TRANSACTION_onGetNextCharacteristicDescriptor = 4;
        /*     */static final int TRANSACTION_onReadCharacteristicValue = 5;
        /*     */static final int TRANSACTION_onReadCharDescriptorValue = 6;
        /*     */static final int TRANSACTION_onWriteCharValue = 7;
        /*     */static final int TRANSACTION_onWriteCharDescrValue = 8;
        /*     */static final int TRANSACTION_onRegForNotifications = 9;
        /*     */static final int TRANSACTION_onUnregisterNotifications = 10;
        /*     */static final int TRANSACTION_onNotify = 11;
        /*     */static final int TRANSACTION_onGetFirstIncludedService = 12;
        /*     */static final int TRANSACTION_onGetNextIncludedService = 13;

        /*     */
        /*     */public Stub()
        /*     */{
            /* 19 */attachInterface(this, "com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
            /*     */}

        /*     */
        /*     */public static IBleCharacteristicDataCallback asInterface(IBinder obj)
        /*     */{
            /* 27 */if (obj == null) {
                /* 28 */return null;
                /*     */}
            /* 30 */IInterface iin = obj
                    .queryLocalInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
            /* 31 */if ((iin != null) && ((iin instanceof IBleCharacteristicDataCallback))) {
                /* 32 */return (IBleCharacteristicDataCallback) iin;
                /*     */}
            /* 34 */return new Proxy(obj);
            /*     */}

        /*     */
        /*     */public IBinder asBinder() {
            /* 38 */return this;
            /*     */}

        /*     */
        /*     */public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            /* 42 */switch (code)
            /*     */{
            /*     */case 1598968902:
                    /* 46 */reply
                            .writeString("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 47 */return true;
                    /*     */case 1:
                    /* 51 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 53 */int _arg0 = data.readInt();
                    /*     */
                    /* 55 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 57 */if (0 != data.readInt()) {
                        /* 58 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 61 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 64 */if (0 != data.readInt()) {
                        /* 65 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 68 */_arg3 = null;
                        /*     */}
                    /* 70 */onGetFirstCharacteristic(_arg0, _arg1, _arg2, _arg3);
                    /* 71 */reply.writeNoException();
                    /* 72 */return true;
                    /*     */case 2:
                    /* 76 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 78 */int _arg0 = data.readInt();
                    /*     */
                    /* 80 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 82 */if (0 != data.readInt()) {
                        /* 83 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 86 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 89 */if (0 != data.readInt()) {
                        /* 90 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 93 */_arg3 = null;
                    /*     */BluetoothGattID _arg4;
                    /*     */BluetoothGattID _arg4;
                    /* 96 */if (0 != data.readInt()) {
                        /* 97 */_arg4 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 100 */_arg4 = null;
                        /*     */}
                    /* 102 */onGetFirstCharacteristicDescriptor(_arg0, _arg1, _arg2, _arg3, _arg4);
                    /* 103 */reply.writeNoException();
                    /* 104 */return true;
                    /*     */case 3:
                    /* 108 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 110 */int _arg0 = data.readInt();
                    /*     */
                    /* 112 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 114 */if (0 != data.readInt()) {
                        /* 115 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 118 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 121 */if (0 != data.readInt()) {
                        /* 122 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 125 */_arg3 = null;
                        /*     */}
                    /* 127 */onGetNextCharacteristic(_arg0, _arg1, _arg2, _arg3);
                    /* 128 */reply.writeNoException();
                    /* 129 */return true;
                    /*     */case 4:
                    /* 133 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 135 */int _arg0 = data.readInt();
                    /*     */
                    /* 137 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 139 */if (0 != data.readInt()) {
                        /* 140 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 143 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 146 */if (0 != data.readInt()) {
                        /* 147 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 150 */_arg3 = null;
                    /*     */BluetoothGattID _arg4;
                    /*     */BluetoothGattID _arg4;
                    /* 153 */if (0 != data.readInt()) {
                        /* 154 */_arg4 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 157 */_arg4 = null;
                        /*     */}
                    /* 159 */onGetNextCharacteristicDescriptor(_arg0, _arg1, _arg2, _arg3, _arg4);
                    /* 160 */reply.writeNoException();
                    /* 161 */return true;
                    /*     */case 5:
                    /* 165 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 167 */int _arg0 = data.readInt();
                    /*     */
                    /* 169 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 171 */if (0 != data.readInt()) {
                        /* 172 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 175 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 178 */if (0 != data.readInt()) {
                        /* 179 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 182 */_arg3 = null;
                        /*     */}
                    /*     */
                    /* 185 */byte[] _arg4 = data.createByteArray();
                    /* 186 */onReadCharacteristicValue(_arg0, _arg1, _arg2, _arg3, _arg4);
                    /* 187 */reply.writeNoException();
                    /* 188 */return true;
                    /*     */case 6:
                    /* 192 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 194 */int _arg0 = data.readInt();
                    /*     */
                    /* 196 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 198 */if (0 != data.readInt()) {
                        /* 199 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 202 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 205 */if (0 != data.readInt()) {
                        /* 206 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 209 */_arg3 = null;
                    /*     */BluetoothGattID _arg4;
                    /*     */BluetoothGattID _arg4;
                    /* 212 */if (0 != data.readInt()) {
                        /* 213 */_arg4 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 216 */_arg4 = null;
                        /*     */}
                    /*     */
                    /* 219 */byte[] _arg5 = data.createByteArray();
                    /* 220 */onReadCharDescriptorValue(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    /* 221 */reply.writeNoException();
                    /* 222 */return true;
                    /*     */case 7:
                    /* 226 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 228 */int _arg0 = data.readInt();
                    /*     */
                    /* 230 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 232 */if (0 != data.readInt()) {
                        /* 233 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 236 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 239 */if (0 != data.readInt()) {
                        /* 240 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 243 */_arg3 = null;
                        /*     */}
                    /* 245 */onWriteCharValue(_arg0, _arg1, _arg2, _arg3);
                    /* 246 */reply.writeNoException();
                    /* 247 */return true;
                    /*     */case 8:
                    /* 251 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 253 */int _arg0 = data.readInt();
                    /*     */
                    /* 255 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 257 */if (0 != data.readInt()) {
                        /* 258 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 261 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 264 */if (0 != data.readInt()) {
                        /* 265 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 268 */_arg3 = null;
                    /*     */BluetoothGattID _arg4;
                    /*     */BluetoothGattID _arg4;
                    /* 271 */if (0 != data.readInt()) {
                        /* 272 */_arg4 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 275 */_arg4 = null;
                        /*     */}
                    /* 277 */onWriteCharDescrValue(_arg0, _arg1, _arg2, _arg3, _arg4);
                    /* 278 */reply.writeNoException();
                    /* 279 */return true;
                    /*     */case 9:
                    /* 283 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 285 */int _arg0 = data.readInt();
                    /*     */
                    /* 287 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 289 */if (0 != data.readInt()) {
                        /* 290 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 293 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 296 */if (0 != data.readInt()) {
                        /* 297 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 300 */_arg3 = null;
                        /*     */}
                    /* 302 */onRegForNotifications(_arg0, _arg1, _arg2, _arg3);
                    /* 303 */reply.writeNoException();
                    /* 304 */return true;
                    /*     */case 10:
                    /* 308 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 310 */int _arg0 = data.readInt();
                    /*     */
                    /* 312 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 314 */if (0 != data.readInt()) {
                        /* 315 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 318 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 321 */if (0 != data.readInt()) {
                        /* 322 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 325 */_arg3 = null;
                        /*     */}
                    /* 327 */onUnregisterNotifications(_arg0, _arg1, _arg2, _arg3);
                    /* 328 */reply.writeNoException();
                    /* 329 */return true;
                    /*     */case 11:
                    /* 333 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 335 */int _arg0 = data.readInt();
                    /*     */
                    /* 337 */String _arg1 = data.readString();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 339 */if (0 != data.readInt()) {
                        /* 340 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 343 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 346 */if (0 != data.readInt()) {
                        /* 347 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 350 */_arg3 = null;
                        /*     */}
                    /*     */
                    /* 353 */boolean _arg4 = 0 != data.readInt();
                    /*     */
                    /* 355 */byte[] _arg5 = data.createByteArray();
                    /* 356 */onNotify(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    /* 357 */reply.writeNoException();
                    /* 358 */return true;
                    /*     */case 12:
                    /* 362 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 364 */int _arg0 = data.readInt();
                    /*     */
                    /* 366 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 368 */if (0 != data.readInt()) {
                        /* 369 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 372 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 375 */if (0 != data.readInt()) {
                        /* 376 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 379 */_arg3 = null;
                        /*     */}
                    /* 381 */onGetFirstIncludedService(_arg0, _arg1, _arg2, _arg3);
                    /* 382 */reply.writeNoException();
                    /* 383 */return true;
                    /*     */case 13:
                    /* 387 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /*     */
                    /* 389 */int _arg0 = data.readInt();
                    /*     */
                    /* 391 */int _arg1 = data.readInt();
                    /*     */BluetoothGattID _arg2;
                    /*     */BluetoothGattID _arg2;
                    /* 393 */if (0 != data.readInt()) {
                        /* 394 */_arg2 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else
                        /* 397 */_arg2 = null;
                    /*     */BluetoothGattID _arg3;
                    /*     */BluetoothGattID _arg3;
                    /* 400 */if (0 != data.readInt()) {
                        /* 401 */_arg3 = (BluetoothGattID) BluetoothGattID.CREATOR
                                .createFromParcel(data);
                        /*     */}
                    /*     */else {
                        /* 404 */_arg3 = null;
                        /*     */}
                    /* 406 */onGetNextIncludedService(_arg0, _arg1, _arg2, _arg3);
                    /* 407 */reply.writeNoException();
                    /* 408 */return true;
                    /*     */}
            /*     */
            /* 411 */return super.onTransact(code, data, reply, flags);
            /*     */}

        /*     */private static class Proxy implements IBleCharacteristicDataCallback {
            /*     */private IBinder mRemote;

            /*     */
            /*     */Proxy(IBinder remote) {
                /* 418 */this.mRemote = remote;
                /*     */}

            /*     */
            /*     */public IBinder asBinder() {
                /* 422 */return this.mRemote;
                /*     */}

            /*     */
            /*     */public String getInterfaceDescriptor() {
                /* 426 */return "com.broadcom.bt.le.api.IBleCharacteristicDataCallback";
                /*     */}

            /*     */
            /*     */public void onGetFirstCharacteristic(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID characteristicID) throws RemoteException {
                /* 430 */Parcel _data = Parcel.obtain();
                /* 431 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 433 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 434 */_data.writeInt(connID);
                    /* 435 */_data.writeInt(status);
                    /* 436 */if (svcID != null) {
                        /* 437 */_data.writeInt(1);
                        /* 438 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 441 */_data.writeInt(0);
                        /*     */}
                    /* 443 */if (characteristicID != null) {
                        /* 444 */_data.writeInt(1);
                        /* 445 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 448 */_data.writeInt(0);
                        /*     */}
                    /* 450 */this.mRemote.transact(1, _data, _reply, 0);
                    /* 451 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 454 */_reply.recycle();
                    /* 455 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onGetFirstCharacteristicDescriptor(int connID, int status,
                    BluetoothGattID svcId, BluetoothGattID characteristicID,
                    BluetoothGattID descriptorID) throws RemoteException {
                /* 460 */Parcel _data = Parcel.obtain();
                /* 461 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 463 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 464 */_data.writeInt(connID);
                    /* 465 */_data.writeInt(status);
                    /* 466 */if (svcId != null) {
                        /* 467 */_data.writeInt(1);
                        /* 468 */svcId.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 471 */_data.writeInt(0);
                        /*     */}
                    /* 473 */if (characteristicID != null) {
                        /* 474 */_data.writeInt(1);
                        /* 475 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 478 */_data.writeInt(0);
                        /*     */}
                    /* 480 */if (descriptorID != null) {
                        /* 481 */_data.writeInt(1);
                        /* 482 */descriptorID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 485 */_data.writeInt(0);
                        /*     */}
                    /* 487 */this.mRemote.transact(2, _data, _reply, 0);
                    /* 488 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 491 */_reply.recycle();
                    /* 492 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onGetNextCharacteristic(int connID, int status, BluetoothGattID svcID,
                    BluetoothGattID characteristicID) throws RemoteException {
                /* 497 */Parcel _data = Parcel.obtain();
                /* 498 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 500 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 501 */_data.writeInt(connID);
                    /* 502 */_data.writeInt(status);
                    /* 503 */if (svcID != null) {
                        /* 504 */_data.writeInt(1);
                        /* 505 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 508 */_data.writeInt(0);
                        /*     */}
                    /* 510 */if (characteristicID != null) {
                        /* 511 */_data.writeInt(1);
                        /* 512 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 515 */_data.writeInt(0);
                        /*     */}
                    /* 517 */this.mRemote.transact(3, _data, _reply, 0);
                    /* 518 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 521 */_reply.recycle();
                    /* 522 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onGetNextCharacteristicDescriptor(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID characteristicID,
                    BluetoothGattID descriptorID) throws RemoteException {
                /* 527 */Parcel _data = Parcel.obtain();
                /* 528 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 530 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 531 */_data.writeInt(connID);
                    /* 532 */_data.writeInt(status);
                    /* 533 */if (svcID != null) {
                        /* 534 */_data.writeInt(1);
                        /* 535 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 538 */_data.writeInt(0);
                        /*     */}
                    /* 540 */if (characteristicID != null) {
                        /* 541 */_data.writeInt(1);
                        /* 542 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 545 */_data.writeInt(0);
                        /*     */}
                    /* 547 */if (descriptorID != null) {
                        /* 548 */_data.writeInt(1);
                        /* 549 */descriptorID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 552 */_data.writeInt(0);
                        /*     */}
                    /* 554 */this.mRemote.transact(4, _data, _reply, 0);
                    /* 555 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 558 */_reply.recycle();
                    /* 559 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onReadCharacteristicValue(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID characteristicID, byte[] data)
                    throws RemoteException {
                /* 564 */Parcel _data = Parcel.obtain();
                /* 565 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 567 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 568 */_data.writeInt(connID);
                    /* 569 */_data.writeInt(status);
                    /* 570 */if (svcID != null) {
                        /* 571 */_data.writeInt(1);
                        /* 572 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 575 */_data.writeInt(0);
                        /*     */}
                    /* 577 */if (characteristicID != null) {
                        /* 578 */_data.writeInt(1);
                        /* 579 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 582 */_data.writeInt(0);
                        /*     */}
                    /* 584 */_data.writeByteArray(data);
                    /* 585 */this.mRemote.transact(5, _data, _reply, 0);
                    /* 586 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 589 */_reply.recycle();
                    /* 590 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onReadCharDescriptorValue(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID characteristicID,
                    BluetoothGattID descrID, byte[] data) throws RemoteException {
                /* 595 */Parcel _data = Parcel.obtain();
                /* 596 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 598 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 599 */_data.writeInt(connID);
                    /* 600 */_data.writeInt(status);
                    /* 601 */if (svcID != null) {
                        /* 602 */_data.writeInt(1);
                        /* 603 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 606 */_data.writeInt(0);
                        /*     */}
                    /* 608 */if (characteristicID != null) {
                        /* 609 */_data.writeInt(1);
                        /* 610 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 613 */_data.writeInt(0);
                        /*     */}
                    /* 615 */if (descrID != null) {
                        /* 616 */_data.writeInt(1);
                        /* 617 */descrID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 620 */_data.writeInt(0);
                        /*     */}
                    /* 622 */_data.writeByteArray(data);
                    /* 623 */this.mRemote.transact(6, _data, _reply, 0);
                    /* 624 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 627 */_reply.recycle();
                    /* 628 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onWriteCharValue(int connID, int status, BluetoothGattID svcID,
                    BluetoothGattID characteristicID) throws RemoteException {
                /* 633 */Parcel _data = Parcel.obtain();
                /* 634 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 636 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 637 */_data.writeInt(connID);
                    /* 638 */_data.writeInt(status);
                    /* 639 */if (svcID != null) {
                        /* 640 */_data.writeInt(1);
                        /* 641 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 644 */_data.writeInt(0);
                        /*     */}
                    /* 646 */if (characteristicID != null) {
                        /* 647 */_data.writeInt(1);
                        /* 648 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 651 */_data.writeInt(0);
                        /*     */}
                    /* 653 */this.mRemote.transact(7, _data, _reply, 0);
                    /* 654 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 657 */_reply.recycle();
                    /* 658 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onWriteCharDescrValue(int connID, int status, BluetoothGattID svcID,
                    BluetoothGattID characteristicID, BluetoothGattID descrID)
                    throws RemoteException {
                /* 663 */Parcel _data = Parcel.obtain();
                /* 664 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 666 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 667 */_data.writeInt(connID);
                    /* 668 */_data.writeInt(status);
                    /* 669 */if (svcID != null) {
                        /* 670 */_data.writeInt(1);
                        /* 671 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 674 */_data.writeInt(0);
                        /*     */}
                    /* 676 */if (characteristicID != null) {
                        /* 677 */_data.writeInt(1);
                        /* 678 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 681 */_data.writeInt(0);
                        /*     */}
                    /* 683 */if (descrID != null) {
                        /* 684 */_data.writeInt(1);
                        /* 685 */descrID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 688 */_data.writeInt(0);
                        /*     */}
                    /* 690 */this.mRemote.transact(8, _data, _reply, 0);
                    /* 691 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 694 */_reply.recycle();
                    /* 695 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onRegForNotifications(int connID, int status, BluetoothGattID svcID,
                    BluetoothGattID characteristicID) throws RemoteException {
                /* 700 */Parcel _data = Parcel.obtain();
                /* 701 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 703 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 704 */_data.writeInt(connID);
                    /* 705 */_data.writeInt(status);
                    /* 706 */if (svcID != null) {
                        /* 707 */_data.writeInt(1);
                        /* 708 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 711 */_data.writeInt(0);
                        /*     */}
                    /* 713 */if (characteristicID != null) {
                        /* 714 */_data.writeInt(1);
                        /* 715 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 718 */_data.writeInt(0);
                        /*     */}
                    /* 720 */this.mRemote.transact(9, _data, _reply, 0);
                    /* 721 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 724 */_reply.recycle();
                    /* 725 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onUnregisterNotifications(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID characteristicID) throws RemoteException {
                /* 730 */Parcel _data = Parcel.obtain();
                /* 731 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 733 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 734 */_data.writeInt(connID);
                    /* 735 */_data.writeInt(status);
                    /* 736 */if (svcID != null) {
                        /* 737 */_data.writeInt(1);
                        /* 738 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 741 */_data.writeInt(0);
                        /*     */}
                    /* 743 */if (characteristicID != null) {
                        /* 744 */_data.writeInt(1);
                        /* 745 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 748 */_data.writeInt(0);
                        /*     */}
                    /* 750 */this.mRemote.transact(10, _data, _reply, 0);
                    /* 751 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 754 */_reply.recycle();
                    /* 755 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onNotify(int connID, String address, BluetoothGattID svcID,
                    BluetoothGattID characteristicID, boolean isNotify, byte[] data)
                    throws RemoteException {
                /* 760 */Parcel _data = Parcel.obtain();
                /* 761 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 763 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 764 */_data.writeInt(connID);
                    /* 765 */_data.writeString(address);
                    /* 766 */if (svcID != null) {
                        /* 767 */_data.writeInt(1);
                        /* 768 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 771 */_data.writeInt(0);
                        /*     */}
                    /* 773 */if (characteristicID != null) {
                        /* 774 */_data.writeInt(1);
                        /* 775 */characteristicID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 778 */_data.writeInt(0);
                        /*     */}
                    /* 780 */_data.writeInt(isNotify ? 1 : 0);
                    /* 781 */_data.writeByteArray(data);
                    /* 782 */this.mRemote.transact(11, _data, _reply, 0);
                    /* 783 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 786 */_reply.recycle();
                    /* 787 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onGetFirstIncludedService(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID inclsvcID) throws RemoteException {
                /* 792 */Parcel _data = Parcel.obtain();
                /* 793 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 795 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 796 */_data.writeInt(connID);
                    /* 797 */_data.writeInt(status);
                    /* 798 */if (svcID != null) {
                        /* 799 */_data.writeInt(1);
                        /* 800 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 803 */_data.writeInt(0);
                        /*     */}
                    /* 805 */if (inclsvcID != null) {
                        /* 806 */_data.writeInt(1);
                        /* 807 */inclsvcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 810 */_data.writeInt(0);
                        /*     */}
                    /* 812 */this.mRemote.transact(12, _data, _reply, 0);
                    /* 813 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 816 */_reply.recycle();
                    /* 817 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onGetNextIncludedService(int connID, int status,
                    BluetoothGattID svcID, BluetoothGattID inclsvcID) throws RemoteException {
                /* 822 */Parcel _data = Parcel.obtain();
                /* 823 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 825 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleCharacteristicDataCallback");
                    /* 826 */_data.writeInt(connID);
                    /* 827 */_data.writeInt(status);
                    /* 828 */if (svcID != null) {
                        /* 829 */_data.writeInt(1);
                        /* 830 */svcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 833 */_data.writeInt(0);
                        /*     */}
                    /* 835 */if (inclsvcID != null) {
                        /* 836 */_data.writeInt(1);
                        /* 837 */inclsvcID.writeToParcel(_data, 0);
                        /*     */}
                    /*     */else {
                        /* 840 */_data.writeInt(0);
                        /*     */}
                    /* 842 */this.mRemote.transact(13, _data, _reply, 0);
                    /* 843 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 846 */_reply.recycle();
                    /* 847 */_data.recycle();
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
 * com.broadcom.bt.le.api.IBleCharacteristicDataCallback JD-Core Version: 0.6.0
 */
