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

package com.broadcom.bt.le.api;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.broadcom.bt.le.api.exceptions.BondRequiredException;
import com.broadcom.bt.service.gatt.IBluetoothGatt;

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
        mAppid = appId;
        mCtxt = ctxt;
        mServiceArr = serviceArr;
        mConnMap = new HashMap<String, Integer>();
        mMtuMap = new HashMap<Integer, Integer>();
        mSvcConn = new GattServiceConnection(null);
        Intent i = new Intent();
        i.setClassName("com.broadcom.bt.app.system",
                "com.broadcom.bt.app.system.GattService");
        mCtxt.bindService(i, mSvcConn, 1);

        throw new RuntimeException("Not implemented");
    }

    public synchronized void finish()
    {
        if (mSvcConn != null) {
            mCtxt.unbindService(mSvcConn);
            mSvcConn = null;
        }
    }

    public void finalize()
    {
        finish();
    }

    byte getAppHandle()
    {
        return mAppHandle;
    }

    HashMap<String, Integer> getConnMap() {
        return mConnMap;
    }

    void initProfile()
    {
        Log.i("BleServerProfile", "initProfile()");
        try {
            mService.registerServerProfileCallback(mAppid,
                    new BleServerProfileCallback(this));
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to start profile", t);
        }
    }

    void notifyAction(int event)
    {
        if ((event == 0) && (++mSvcCreated == mServiceArr.size()))
        {
            Log.i("BleServerProfile",
                    "All services created successfully. Calling onInitialized");
            onInitialized(true);
        } else if ((event == 4) && (--mSvcCreated == 0))
        {
            Log.i("BleServerProfile",
                    "All services stopped successfully. Calling onStopped");
            onStopped();
        } else if ((event == 2) && (++mSvcStarted == mServiceArr.size()))
        {
            Log.i("BleServerProfile",
                    "All services started successfully. Calling onStarted");
            onStarted(true);
        } else if (event == 1) {
            Log.i("BleServerProfile",
                    "One of the services creation failed. Calling onInitialized");
            mProfileStatus = 2;
            onInitialized(false);
        } else if (event == 3) {
            Log.i("BleServerProfile",
                    "One of the services start failed. Calling onStarted");
            mProfileStatus = 2;
            onStarted(false);
        } else {
            Log.e("BleServerProfile", "Unknown action from a service");
        }
    }

    public void startProfile()
    {
        Log.i("BleServerProfile", "startProfile()");
        if (mService == null) {
            Log.i("BleServerProfile", "Remote service object is null.. Returning..");
            return;
        }

        for (int i = 0; i < mServiceArr.size(); i++) {
            if (!((BleServerService) mServiceArr.get(i)).isRegistered()) {
                Log.i("BleServerProfile",
                        "One of the services is not registered. Stopping all the services");
                stopProfile();
                return;
            }

            ((BleServerService) mServiceArr.get(i)).startService();
        }
    }

    public void stopProfile()
    {
        Log.i("BleServerProfile", "stopProfile()");
        for (int i = 0; i < mServiceArr.size(); i++)
            ((BleServerService) mServiceArr.get(i)).stopService();
    }

    public void finishProfile()
    {
        Log.i("BleServerProfile", "finishProfile()");
        for (int i = 0; i < mServiceArr.size(); i++) {
            ((BleServerService) mServiceArr.get(i)).deleteService();
        }
        try
        {
            mService.unregisterServerProfileCallback(mAppHandle);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to stop profile", t);
            return;
        }
    }

    public void setMtuSize(int connId, int mtuSize)
    {
        Log.i("BleServerProfile", "setMtuSize");
        mMtuMap.put(Integer.valueOf(connId), Integer.valueOf(mtuSize));
    }

    public boolean setEncryption(String bdaddr, byte action) throws BondRequiredException
    {
        BluetoothDevice d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bdaddr);
        if (d.getBondState()!=BluetoothDevice.BOND_BONDED)
            throw new BondRequiredException();
        
        try
        {
            return mService.setEncryption(bdaddr, action);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to set encryption for connection", t);
        }
        throw new BondRequiredException();
    }

    public void setScanParameters(int scanInterval, int scanWindow)
    {
        try
        {
            mService.setScanParameters(scanInterval, scanWindow);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to set scan parameters", t);
        }
    }

    public void open(String bdaddr, boolean isDirect)
    {
        try
        {
            mService.GATTServer_Open(mAppHandle, bdaddr, isDirect);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
        }
    }

    public void cancelOpen(String bdaddr, boolean isDirect)
    {
        try
        {
            mService.GATTServer_CancelOpen(mAppHandle, bdaddr, isDirect);
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
            return;
        }
    }

    public void close(String bdaddr)
    {
        try
        {
            mService.GATTServer_Close(((Integer) mConnMap.get(bdaddr))
                    .intValue());
        } catch (Throwable t) {
            Log.e("BleServerProfile", "Unable to open Gatt connection", t);
            return;
        }
    }

    protected void onAppRegisterCompleted(int status, int serIf)
    {
        if (status == 0) {
            mAppHandle = (byte) serIf;
            for (int i = 0; i < mServiceArr.size(); i++)
                ((BleServerService) mServiceArr.get(i)).onServiceAvailable(this,
                        mService, mAppid);
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
            mProfile = profile;
        }

        public void onClientConnected(int connId, String bdaddr, boolean isConnected)
        {
            Log.i("BleServerProfile", "onClientConncted addr is " + bdaddr + " connId is "
                    + connId);

            mProfile.onClientConnected(bdaddr, isConnected);
            if (isConnected)
                mProfile.mConnMap.put(bdaddr, Integer.valueOf(connId));
            else
                mProfile.mConnMap.remove(bdaddr);
        }

        public void onAttributeMtuExchange(String address, int connId, int transId, int mtuSize)
        {
            Log.i("BleServerProfile", "onAttributeMtuExchange");
        }

        public void onAppRegisterCompleted(int status, int serIf) {
            Log.i("BleServerProfile", "onAppRegisterCompleted");
            mProfile.onAppRegisterCompleted(status, serIf);
        }

    }

    private class GattServiceConnection
            implements ServiceConnection
    {
        private Context context;

        private GattServiceConnection(Context c) {
            context = c;
        }

        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d("BleServerProfile", "Connected to GattService!");

            if (service != null)
                try {
                    BleServerProfile.this.mService =
                            IBluetoothGatt.Stub.asInterface(service);
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
