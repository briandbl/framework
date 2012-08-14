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
import android.os.RemoteException;

/*     */
/*     */public abstract interface IBleProfileEventCallback extends IInterface
/*     */{
    /*     */public abstract void onClientConnected(int paramInt, String paramString,
            boolean paramBoolean)
            /*     */throws RemoteException;

    /*     */
    /*     */public abstract void onAppRegisterCompleted(int paramInt1, int paramInt2)
            /*     */throws RemoteException;

    /*     */
    /*     */public static abstract class Stub extends Binder
            /*     */implements IBleProfileEventCallback
    /*     */{
        /*     */private static final String DESCRIPTOR = "com.broadcom.bt.le.api.IBleProfileEventCallback";
        /*     */static final int TRANSACTION_onClientConnected = 1;
        /*     */static final int TRANSACTION_onAppRegisterCompleted = 2;

        /*     */
        /*     */public Stub()
        /*     */{
            /* 18 */attachInterface(this, "com.broadcom.bt.le.api.IBleProfileEventCallback");
            /*     */}

        /*     */
        /*     */public static IBleProfileEventCallback asInterface(IBinder obj)
        /*     */{
            /* 26 */if (obj == null) {
                /* 27 */return null;
                /*     */}
            /* 29 */IInterface iin = obj
                    .queryLocalInterface("com.broadcom.bt.le.api.IBleProfileEventCallback");
            /* 30 */if ((iin != null) && ((iin instanceof IBleProfileEventCallback))) {
                /* 31 */return (IBleProfileEventCallback) iin;
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
                    /* 45 */reply.writeString("com.broadcom.bt.le.api.IBleProfileEventCallback");
                    /* 46 */return true;
                    /*     */case 1:
                    /* 50 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleProfileEventCallback");
                    /*     */
                    /* 52 */int _arg0 = data.readInt();
                    /*     */
                    /* 54 */String _arg1 = data.readString();
                    /*     */
                    /* 56 */boolean _arg2 = 0 != data.readInt();
                    /* 57 */onClientConnected(_arg0, _arg1, _arg2);
                    /* 58 */reply.writeNoException();
                    /* 59 */return true;
                    /*     */case 2:
                    /* 63 */data
                            .enforceInterface("com.broadcom.bt.le.api.IBleProfileEventCallback");
                    /*     */
                    /* 65 */int _arg0 = data.readInt();
                    /*     */
                    /* 67 */int _arg1 = data.readInt();
                    /* 68 */onAppRegisterCompleted(_arg0, _arg1);
                    /* 69 */reply.writeNoException();
                    /* 70 */return true;
                    /*     */}
            /*     */
            /* 73 */return super.onTransact(code, data, reply, flags);
            /*     */}

        /*     */private static class Proxy implements IBleProfileEventCallback {
            /*     */private IBinder mRemote;

            /*     */
            /*     */Proxy(IBinder remote) {
                /* 80 */this.mRemote = remote;
                /*     */}

            /*     */
            /*     */public IBinder asBinder() {
                /* 84 */return this.mRemote;
                /*     */}

            /*     */
            /*     */public String getInterfaceDescriptor() {
                /* 88 */return "com.broadcom.bt.le.api.IBleProfileEventCallback";
                /*     */}

            /*     */
            /*     */public void onClientConnected(int connId, String bdaddr, boolean fConnected)
                    throws RemoteException {
                /* 92 */Parcel _data = Parcel.obtain();
                /* 93 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 95 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleProfileEventCallback");
                    /* 96 */_data.writeInt(connId);
                    /* 97 */_data.writeString(bdaddr);
                    /* 98 */_data.writeInt(fConnected ? 1 : 0);
                    /* 99 */this.mRemote.transact(1, _data, _reply, 0);
                    /* 100 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 103 */_reply.recycle();
                    /* 104 */_data.recycle();
                    /*     */}
                /*     */}

            /*     */
            /*     */public void onAppRegisterCompleted(int status, int serIf) throws RemoteException {
                /* 109 */Parcel _data = Parcel.obtain();
                /* 110 */Parcel _reply = Parcel.obtain();
                /*     */try {
                    /* 112 */_data
                            .writeInterfaceToken("com.broadcom.bt.le.api.IBleProfileEventCallback");
                    /* 113 */_data.writeInt(status);
                    /* 114 */_data.writeInt(serIf);
                    /* 115 */this.mRemote.transact(2, _data, _reply, 0);
                    /* 116 */_reply.readException();
                    /*     */}
                /*     */finally {
                    /* 119 */_reply.recycle();
                    /* 120 */_data.recycle();
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
 * com.broadcom.bt.le.api.IBleProfileEventCallback JD-Core Version: 0.6.0
 */
