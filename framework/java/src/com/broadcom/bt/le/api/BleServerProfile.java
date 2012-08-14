
package com.broadcom.bt.le.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.broadcom.bt.service.gatt.IBluetoothGatt;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BleServerProfile
{
    private static final boolean D = true;
    private static final String TAG = "BleServerProfile";
    private Context mCtxt = null;
    private BleGattID mAppid;
    ArrayList<BleServerService> mServiceArr = null;
    private HashMap<String, Integer> mConnMap = null;
    private HashMap<Integer, Integer> mMtuMap = null;
    private IBluetoothGatt mService;
    private int mSvcCreated = 0;
    private int mSvcStarted = 0;
    private byte mAppHandle = -1;
    private int mProfileStatus = 2;
    private GattServiceConnection mSvcConn;

    public BleServerProfile(Context ctxt, BleGattID appId,
            ArrayList<BleServerService> serviceArr)
    {
        this.mAppid = appId;
        this.mCtxt = ctxt;
        this.mServiceArr = serviceArr;
        this.mConnMap = new HashMap();
        this.mMtuMap = new HashMap();
        this.mSvcConn = new GattServiceConnection(null);
        Intent i = new Intent();
        i.setClassName("com.broadcom.bt.app.system",
                "com.broadcom.bt.app.system.GattService");
        this.mCtxt.bindService(i, this.mSvcConn, 1);
    }

    public synchronized void finish()
    {
        if (this.mSvcConn != null) {
            this.mCtxt.unbindService(this.mSvcConn);
            this.mSvcConn = null;
        }
    }

    public void finalize()
    {
        finish();
    }

    byte getAppHandle()
    {
        return this.mAppHandle;
    }

    HashMap<String, Integer> getConnMap() {
        return this.mConnMap;
    }

    void initProfile()
    {
        Log.i("BleServerProfile", "initProfile()");
        try {
            this.mService.registerServerProfileCallback(this.mAppid,
                    new BleServerProfileCallback(this));
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to start profile", t);
        }
    }

    void notifyAction(int event)
    {
        if ((event == 0) && (++this.mSvcCreated == this.mServiceArr.size()))
        {
            Log.i("BleServerProfile",
                    "All services created successfully. Calling onInitialized");
            onInitialized(true);
        } else if ((event == 4) && (--this.mSvcCreated == 0))
        {
            Log.i("BleServerProfile",
                    "All services stopped successfully. Calling onStopped");
            onStopped();
        } else if ((event == 2) && (++this.mSvcStarted == this.mServiceArr.size()))
        {
            Log.i("BleServerProfile",
                    "All services started successfully. Calling onStarted");
            onStarted(true);
        } else if (event == 1) {
            Log.i("BleServerProfile",
                    "One of the services creation failed. Calling onInitialized");
            this.mProfileStatus = 2;
            onInitialized(false);
        } else if (event == 3) {
            Log.i("BleServerProfile",
                    "One of the services start failed. Calling onStarted");
            this.mProfileStatus = 2;
            onStarted(false);
        } else {
            Log.e("BleServerProfile", "Unknown action from a service");
        }
    }

    public void startProfile()
    {
        Log.i("BleServerProfile", "startProfile()");
        if (this.mService == null) {
            Log.i("BleServerProfile", "Remote service object is null.. Returning..");
            return;
        }

        for (int i = 0; i < this.mServiceArr.size(); i++) {
            if (!((BleServerService) this.mServiceArr.get(i)).isRegistered()) {
                Log.i("BleServerProfile",
                        "One of the services is not registered. Stopping all the services");
                stopProfile();
                return;
            }

            ((BleServerService) this.mServiceArr.get(i)).startService();
        }
    }

    public void stopProfile()
    {
        Log.i("BleServerProfile", "stopProfile()");
        for (int i = 0; i < this.mServiceArr.size(); i++)
            ((BleServerService) this.mServiceArr.get(i)).stopService();
    }

    public void finishProfile()
    {
        Log.i("BleServerProfile", "finishProfile()");
        for (int i = 0; i < this.mServiceArr.size(); i++) {
            ((BleServerService) this.mServiceArr.get(i)).deleteService();
        }
        try
        {
            this.mService.unregisterServerProfileCallback(this.mAppHandle);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to stop profile", t);
            return;
        }
    }

    public void setMtuSize(int connId, int mtuSize)
    {
        Log.i("BleServerProfile", "setMtuSize");
        this.mMtuMap.put(Integer.valueOf(connId), Integer.valueOf(mtuSize));
    }

    public void setEncryption(String bdaddr, byte action)
    {
        try
        {
            this.mService.setEncryption(bdaddr, action);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to set encryption for connection", t);
        }
    }

    public void setScanParameters(int scanInterval, int scanWindow)
    {
        try
        {
            this.mService.setScanParameters(scanInterval, scanWindow);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to set scan parameters", t);
        }
    }

    public void open(String bdaddr, boolean isDirect)
    {
        try
        {
            this.mService.GATTServer_Open(this.mAppHandle, bdaddr, isDirect);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
        }
    }

    public void cancelOpen(String bdaddr, boolean isDirect)
    {
        try
        {
            this.mService.GATTServer_CancelOpen(this.mAppHandle, bdaddr, isDirect);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
            return;
        }
    }

    public void close(String bdaddr)
    {
        try
        {
            this.mService.GATTServer_Close(((Integer) this.mConnMap.get(bdaddr))
                    .intValue());
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
            return;
        }
    }

    protected void onAppRegisterCompleted(int status, int serIf)
    {
        if (status == 0) {
            this.mAppHandle = (byte) serIf;
            for (int i = 0; i < this.mServiceArr.size(); i++)
                ((BleServerService) this.mServiceArr.get(i)).onServiceAvailable(this,
                        this.mService, this.mAppid);
        } else {
            Log.e("BleServerProfile", "Application registration failed.. Aborting");
        }
    }

    public abstract void onInitialized(boolean paramBoolean);

    public abstract void onStarted(boolean paramBoolean);

    public abstract void onStopped();

    public abstract void onClientConnected(String paramString, boolean paramBoolean);

    public abstract void onOpenCompleted(int paramInt);

    public abstract void onOpenCancelCompleted(int paramInt);

    public abstract void onCloseCompleted(int paramInt);

    private class BleServerProfileCallback extends IBleProfileEventCallback.Stub
    {
        private BleServerProfile mProfile;

        public BleServerProfileCallback(BleServerProfile profile)
        {
            this.mProfile = profile;
        }

        public void onClientConnected(int connId, String bdaddr, boolean isConnected)
        {
            Log.i("BleServerProfile", "onClientConncted addr is " + bdaddr + " connId is "
                    + connId);

            this.mProfile.onClientConnected(bdaddr, isConnected);
            if (isConnected)
                this.mProfile.mConnMap.put(bdaddr, Integer.valueOf(connId));
            else
                this.mProfile.mConnMap.remove(bdaddr);
        }

        public void onAttributeMtuExchange(String address, int connId, int transId, int mtuSize)
        {
            Log.i("BleServerProfile", "onAttributeMtuExchange");
        }

        public void onAppRegisterCompleted(int status, int serIf) {
            Log.i("BleServerProfile", "onAppRegisterCompleted");
            this.mProfile.onAppRegisterCompleted(status, serIf);
        }

    }

    private class GattServiceConnection
            implements ServiceConnection
    {
        private Context context;

        private GattServiceConnection(Context c) {
            this.context = c;
        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d("BleServerProfile", "Connected to GattService!");

            if (service != null)
                try {
                    BleServerProfile.access$002(BleServerProfile.this,
                            IBluetoothGatt.Stub.asInterface(service));
                    BleServerProfile.this.initProfile();
                } catch (Throwable t) {
                    Log.e("BleServerProfile", "Unable to get Binder to GattService", t);
                }
        }

        public void onServiceDisconnected(ComponentName name)
        {
            Log.d("BleServerProfile", "Disconnected from GattService!");
        }

    }

}
