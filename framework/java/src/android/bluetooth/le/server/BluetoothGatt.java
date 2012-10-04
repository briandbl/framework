/*
 * Copyright (c) 2012 Naranjo Manuel Francisco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.bluetooth.le.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.server.BlueZInterface.BlueZConnectionError;
import android.bluetooth.le.server.GattToolWrapper.SEC_LEVEL;
import android.bluetooth.le.server.GattToolWrapper.SHELL_ERRORS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;

import com.broadcom.bt.le.api.BleAdapter;
import com.broadcom.bt.le.api.BleConstants;
import com.broadcom.bt.le.api.BleGattID;
import com.broadcom.bt.le.api.IBleCharacteristicDataCallback;
import com.broadcom.bt.le.api.IBleClientCallback;
import com.broadcom.bt.le.api.IBleProfileEventCallback;
import com.broadcom.bt.le.api.IBleServiceCallback;
import com.broadcom.bt.service.gatt.BluetoothGattCharDescrID;
import com.broadcom.bt.service.gatt.BluetoothGattCharID;
import com.broadcom.bt.service.gatt.BluetoothGattID;
import com.broadcom.bt.service.gatt.BluetoothGattInclSrvcID;
import com.broadcom.bt.service.gatt.IBluetoothGatt;

import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;

public class BluetoothGatt extends IBluetoothGatt.Stub implements
        BlueZInterface.Listener, GattToolWrapper.GattToolListener {
    private BlueZInterface mBluezInterface;
    private BluetoothAdapter mAdapter;
    private IActivityManager mAm;
    private static String TAG = "BT-GATT";

    public static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";
    public static final String BLUETOOTH_LE_SERVICE = BleConstants.BLUETOOTH_LE_SERVICE;

    public static int API_LEVEL = 5;
    public static String FRAMEWORK_VERSION = "0.5.4a";

    private Map<BluetoothGattID, AppWrapper> registeredApps = new HashMap<BluetoothGattID, AppWrapper>();
    private AppWrapper[] registeredAppsByID = new AppWrapper[Byte.MAX_VALUE];
    private byte mNextAppID = 0;

    private static final int GATTTOOL_POOL_SIZE = 5;

    /**
     * this class member is used for enabling and disabling the Bluez interface
     * depending on whether it's running or not.
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            if (i.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int s = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (s == BluetoothAdapter.STATE_ON) {
                    Log.v(TAG, "enabling my interface");
                    Thread a = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "error", e);
                            }
                            BluetoothGatt.this.mBluezInterface.Start(true);
                        }
                    };
                    a.start();

                } else if (s == BluetoothAdapter.STATE_OFF) {
                    Log.v(TAG, "bluez is down");
                    BluetoothGatt.this.mBluezInterface.Stop();
                }
            }

        }
    };

    /**
     * Constructor for the class, initializes the pieces needed by us.
     * 
     * @throws IOException if GattTool pool fails to initialize.
     */
    public BluetoothGatt() throws IOException {
        Log.v(TAG, "new bluetoothGatt");

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null)
            throw new RuntimeException("Bluetooth Adapter not ready");

        mAm = ActivityManagerNative.getDefault();
        if (mAm == null)
            throw new RuntimeException("Activity Manager not ready");

        this.initBroadcast();

        mBluezInterface = new BlueZInterface(this);
        mBluezInterface.Start();

        registerBroadcastReceiver(mReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        GattToolWrapper.initWorkerPool(GATTTOOL_POOL_SIZE);
    }

    @SuppressWarnings("rawtypes")
    @Override
    /**
     * This function will return the kind of Bluetooth Device based on the 
     * Bluetooth Address.
     */
    public byte getDeviceType(String address) {
        @SuppressWarnings("rawtypes")
        Map<String, Variant> prop = null;
        try {
            prop = mBluezInterface.getDeviceProperties(address);
        } catch (Exception e) {
            Log.e(TAG, "error on getDeviceType", e);
        }
        if (prop == null)
            return BleConstants.GATT_UNDEFINED;

        /*
         * LE provides - Address, RSSI, Name, Paired, Broadcaster, UUIDs, Class
         * 0 or no class at all BD/EDR provides: Address, Class, Icon, RSSI,
         * Name, Alias, LegacyPairing, Paired, UUIDs
         */
        if (prop.containsKey("Icon") && prop.containsKey("LegacyPairing"))
            return BleAdapter.DEVICE_TYPE_BREDR;

        Variant c = null;
        if (prop.containsKey("Class"))
            c = prop.get("Class");

        if (c != null && ((UInt32) c.getValue()).intValue() != 0)
            return BleAdapter.DEVICE_TYPE_DUMO;

        return BleAdapter.DEVICE_TYPE_BLE;
    }

    @SuppressWarnings("unused")
    /**
     *
     */
    private class IntentReceiver extends IIntentReceiver.Stub {
        private boolean mFinished = false;

        public synchronized void performReceive(
                Intent intent, int rc, String data, Bundle ext, boolean ord,
                boolean sticky) {
            String line = "Broadcast completed: result=" + rc;
            if (data != null)
                line = line + ", data=\"" + data + "\"";
            if (ext != null)
                line = line + ", extras: " + ext;
            Log.v(TAG, line);
            mFinished = true;
            notifyAll();
        }

        public synchronized void waitForFinish() {
            try {
                while (!mFinished)
                    wait();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private class IBroadcastReceiver extends IIntentReceiver.Stub {
        private BroadcastReceiver receiver = null;

        public IBroadcastReceiver(BroadcastReceiver receiver) {
            super();
            this.receiver = receiver;
        }

        public synchronized void performReceive(
                Intent intent, int rc, String data, Bundle ext, boolean ord,
                boolean sticky) {
            String line = "Broadcast received: " + intent;
            Log.v(TAG, line);
            receiver.onReceive(null, intent);
            notifyAll();
        }
    }

    private Method mBroadcast;
    private Class<?>[] mBroadcastArgs;

    private void initBroadcast() {
        if (mBroadcast != null)
            return;
        Class<?> c;
        try {
            c = Class.forName("android.app.IActivityManager");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "failed on initBroadcast", e);
            return;
        }
        Log.i(TAG, "found class");

        for (Method m : c.getMethods()) {
            Log.v(TAG, "m " + m.getName());

            if (m.getName().equals("broadcastIntent")) {
                mBroadcast = m;
                mBroadcastArgs = m.getParameterTypes();
                Log.v(TAG, "found method, argument count " + mBroadcastArgs.length);

                for (int i = 0; i < mBroadcastArgs.length; i++) {
                    Log.v(TAG, "argument " + i + " " + mBroadcastArgs[i]);
                }
                return;
            }
        }

        Log.e(TAG, "couldn't resolve broadcastIntent");
    }

    /**
     * Internal method that will broadcast intents using reflections.
     * 
     * @param intent
     */
    public void broadcastIntent(Intent intent) {
        if (mBroadcast == null) {
            Log.v(TAG, "no broadcastIntent, sorry");
            return;
        }

        Object[] args = new Object[mBroadcastArgs.length];

        boolean flag = true;

        for (int i = 0; i < args.length; i++) {
            if (mBroadcastArgs[i].equals(Intent.class)) {
                args[i] = intent;
                continue;
            }

            if (mBroadcastArgs[i].equals(int.class)) {
                args[i] = 0;
                continue;
            }

            if (mBroadcastArgs[i].equals(boolean.class)) {
                args[i] = flag;
                if (flag)
                    flag = false;
                continue;
            }

            args[i] = null;
        }

        try {
            Log.v(TAG, "broadcasting " + args);
            mBroadcast.invoke(mAm, args);
        } catch (Exception e) {
            Log.e(TAG, "failed to broadcast signal!", e);
        }
    }

    private void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        Log.v(TAG, "registering broadcast receiver");
        IBroadcastReceiver ireceiver = new IBroadcastReceiver(receiver);
        try {
            mAm.registerReceiver(null, null, ireceiver, filter, null);
            Log.v(TAG, "registered");
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "failed registering receiver");
        }
    }

    @Override
    /**
     * Will get called by BlueZ layer when a new device is discovered.
     */
    public void deviceDiscovered(String address, String name, short rssi) {
        Intent intent = new Intent(BluetoothDevice.ACTION_FOUND);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, mAdapter.getRemoteDevice(address));
        intent.putExtra(BluetoothDevice.EXTRA_NAME, name);
        intent.putExtra(BluetoothDevice.EXTRA_RSSI, rssi);
        intent.putExtra(BleAdapter.EXTRA_DEVICE_TYPE, BleAdapter.DEVICE_TYPE_BLE);
        broadcastIntent(intent);
    }

    @Override
    /**
     * Allows clients to list all the UUIDs provided by the remote device
     * address.
     */
    public void getUUIDs(String address) {
        List<String> uuids = null;

        try {
            uuids = mBluezInterface.getUUIDs(address);
        } catch (Exception e) {
            Log.e(TAG, "failed to get uuids for " + address, e);
            return;
        }

        BluetoothDevice d = mAdapter.getRemoteDevice(address);
        for (String u : uuids) {
            Intent intent = new Intent(BleAdapter.ACTION_UUID);
            intent.putExtra(BleAdapter.EXTRA_DEVICE, d);
            intent.putExtra(BleAdapter.EXTRA_UUID, new ParcelUuid(UUID.fromString(u)));
            broadcastIntent(intent);
        }
        Intent intent = new Intent(BleAdapter.ACTION_UUID);
        intent.putExtra(BleAdapter.EXTRA_DEVICE, d);

        broadcastIntent(intent);
    }

    /**
     * Internal class used to wrap all the information needed to talk to binder
     * clients.
     */
    class AppWrapper {
        BluetoothGattID mGattID;
        byte mIfaceID;
        IBleClientCallback mCallback;

        public AppWrapper(BluetoothGattID mGattID, byte mIfaceID, IBleClientCallback mCallback) {
            super();
            this.mGattID = mGattID;
            this.mIfaceID = mIfaceID;
            this.mCallback = mCallback;
        }
    }

    /**
     * internal class used for "remembering the services", only once instance of this
     * class should be available per registered service per instance per device.
     */
    private class Service {
        BluetoothGattID uuid;
        int start;
        int end;
        List<Characteristic> chars = new Vector<Characteristic>();
        Characteristic lastChar = null;
        Integer lastCharResult = null;
        
        IBleCharacteristicDataCallback callback;
        
        public Service(BluetoothGattID u, int s, int e){
            this.uuid = u;
            this.start = s;
            this.end = e;
        }
        
        public void addCharacteristic(Characteristic c){
            c.service = this;
            c.uuid.setInstId(this.chars.size());
            this.chars.add(c);
        }
    }
    
    private class Characteristic {
        int handle;
        BleGattID uuid;
        short properties;
        int value_handle;
        Service service;
        Descriptor lastDescriptor;
        List<Descriptor> descriptors = new Vector<Descriptor>();
        
        public Characteristic(int handle, short properties, int value_handle, BleGattID id){
            this.handle = handle;
            this.properties = properties;
            this.value_handle = value_handle;
            this.uuid = id;
        }
        
        public void addDescriptor(Descriptor d){
            this.descriptors.add(d);
            d.parent = this;
        }
    }
    
    private class Descriptor {
        int handle;
        BleGattID uuid;
        Characteristic parent;
    }
    
    /**
     * Internal class that allows to map connection ids with remote address,
     * application wrapper and gatttool instance.
     */
    private class ConnectionWrapper {
        int connID;
        AppWrapper wrapper;
        String remote;
        GattToolWrapper mGattTool;
        Map<BleGattID, List<Service>> services;
        BleGattID lastPrimaryUuid;
        Service lastService;
        
        public ConnectionWrapper(AppWrapper w, String r) {
            this.connID = -1; // mark as pending
            this.wrapper = w;
            this.remote = r;
            this.services = new HashMap<BleGattID, List<Service>>();
            this.lastPrimaryUuid = null;
        }
    }

    /*
     * Map of connections that still didn't complete or failed, we use Address
     * as key as we don't have a connection handle until connection is
     * stablished.
     */
    private Map<String, ConnectionWrapper> mPendingConnections =
            new HashMap<String, ConnectionWrapper>();

    /*
     * Map of connections running, we map with connection id as we now it.
     */
    private Map<Integer, ConnectionWrapper> mConnectionMap =
            new HashMap<Integer, ConnectionWrapper>();

    /* ************************************************************************************
     * Connection handling methods
     * ***********************************************
     * ************************************
     */
    @Override
    /**
     * Callback from GattToolWrapper when connection completes or fails.
     */
    public synchronized void connected(int conn_handle, String addr, int status) {
        Log.v(TAG, "connected " + addr + " -> " + conn_handle + " " + status);
        if (!mPendingConnections.containsKey(addr)) {
            Log.e(TAG, "remote no longer pending!");
            return;
        }

        ConnectionWrapper cw = mPendingConnections.get(addr);
        mPendingConnections.remove(addr);

        cw.connID = conn_handle;

        try {
            if (status == BleConstants.GATT_SUCCESS) {
                mConnectionMap.put(conn_handle, cw);
                cw.wrapper.mCallback.onConnected(addr, conn_handle);
            }
            else
                cw.wrapper.mCallback.onDisconnected(conn_handle, addr);
        } catch (RemoteException e) {
            Log.e(TAG, "failed calling callback from connection wrapper", e);
        }

        this.notifyAll();
    }

    @Override
    /**
     * Method called by binder clients to start connecting to remote devices.
     */
    public synchronized void open(byte interfaceID, final String remote, boolean foreground) {
        Log.v(TAG, "open " + interfaceID + " " + remote);

        final AppWrapper w = this.registeredAppsByID[interfaceID];

        GattToolWrapper gtw = null;
        ConnectionWrapper cw = null;

        try {
            gtw = GattToolWrapper.getWorker();
            cw = new ConnectionWrapper(w, remote);
            gtw.setListener(this); // register to get signals from this worker.
            cw.mGattTool = gtw;
            mPendingConnections.put(remote, cw);
        } catch (Exception e) {
            Log.e(TAG, "something failed while getting gatttool wrapper and connection wrapper");
            try {
                w.mCallback.onConnected(remote, -1);
            } catch (RemoteException e2) {
                Log.e(TAG, "we failed to notify other end", e2);
            }
            return;
        }

        if (getDeviceType(remote) == BleAdapter.DEVICE_TYPE_BREDR) {
            Log.v(TAG, "Connecting to BR device, setting psm=31");
            gtw.psm(31);
        }
        gtw.connect(remote);

        if (foreground)
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
    }

    @Override
    /**
     * Callback from GattToolWrapper that tells us when a connection has
     * been closed, for what ever reason it did.
     */
    public synchronized void disconnected(int conn_handle, String addr) {
        Log.v(TAG, "disconnected " + addr + " -> " + conn_handle);
        ConnectionWrapper cw;
        if (mPendingConnections.containsKey(addr)) {
            Log.i(TAG, "disconnect on pending connection");
            cw = mPendingConnections.get(addr);
            mPendingConnections.remove(addr);
        } else if (mConnectionMap.containsKey(conn_handle)) {
            Log.i(TAG, "disconnected from real connection");
            cw = mConnectionMap.get(addr);
            mConnectionMap.remove(addr);
        } else {
            Log.e(TAG, "Address is not registered as pending or connected, aborting");
            return;
        }
        cw.mGattTool.setListener(null); // stop getting signals
        cw.mGattTool.releaseWorker();

        try {
            cw.wrapper.mCallback.onDisconnected(conn_handle, addr);
        } catch (RemoteException e) {
            Log.e(TAG, "failed calling callback from connection wrapper", e);
        }

        this.notifyAll();
    }

    @Override
    /**
     * Method that binder clients will call when they want to close a connection, or
     * cancel a pending connection. The way to tell what's the case is given by
     * connHandle, connHandle should be 0 for pending connections.
     */
    public synchronized void close(final byte interfaceID, final String remote,
            int connHandle, boolean foreground) {
        Log.v(TAG, "close called for " + remote + " ifaceID " + interfaceID
                + " connHandle " + connHandle);

        ConnectionWrapper cw = null;
        if (connHandle == 0) {
            if (mPendingConnections.containsKey(remote)) {
                cw = mPendingConnections.get(remote);
                mPendingConnections.remove(remote);
            }
        } else {
            if (mConnectionMap.containsKey(connHandle)) {
                cw = mConnectionMap.get(remote);
                mConnectionMap.remove(remote);
            }
        }

        if (cw == null) {
            Log.e(TAG, "disconnect for non pending or known connection");
            return;
        }

        cw.mGattTool.disconnect();
        cw.mGattTool.releaseWorker();
        cw.mGattTool = null;
        if (foreground)
            try {
                this.wait();
            } catch (InterruptedException e) {
                Log.e(TAG, "got interrupted while waiting for disconnection signal", e);
            }
    }

    /* *******************************************************************************
     * Application handling methods
     * **********************************************
     * ********************************
     */
    @Override
    /**
     * This method will get called when ever a new BLE application starts running.
     * Apps needs to be registered for us to talk to it.
     */
    public synchronized void registerApp(BluetoothGattID appUuid, IBleClientCallback callback) {
        AppWrapper wrapper = null;
        Log.v(TAG, "register app " + appUuid + " callback " + callback);
        if (registeredApps.containsKey(appUuid)) {
            Log.v(TAG, "uuid all ready registered");

            wrapper = registeredApps.get(appUuid);
            if (wrapper.mCallback.asBinder().pingBinder()) {
                Log.e(TAG, "uuid is registered and alive");
                try {
                    callback.onAppRegistered((byte) BleConstants.GATT_ERROR, (byte) -1);
                } catch (RemoteException e) {
                    Log.e(TAG, "failed to tell other end", e);
                }
                return;
            }
            Log.v(TAG, "no ping back " + appUuid + " registering again");
        }
        int status = BleConstants.GATT_SUCCESS;

        if (wrapper == null) {
            if (mNextAppID == Byte.MAX_VALUE)
                status = BleConstants.GATT_ERROR;
            else
                wrapper = new AppWrapper(appUuid, ++mNextAppID, callback);
        } else
            wrapper.mCallback = callback;

        byte ifaceID = -1;

        if (wrapper != null) {
            this.registeredAppsByID[wrapper.mIfaceID] = wrapper;
            registeredApps.put(appUuid, wrapper);
            ifaceID = wrapper.mIfaceID;
        }

        try {
            callback.onAppRegistered((byte) status, ifaceID);
        } catch (RemoteException e) {
            Log.e(TAG, "Faield to notify AppRegistered", e);
        }
    }

    @Override
    /**
     * When an application is finishing or don't want to do LE any more should
     * call this method.
     */
    public synchronized void unregisterApp(byte interfaceID) {
        for (Entry<BluetoothGattID, AppWrapper> v : registeredApps.entrySet()) {
            AppWrapper a = v.getValue();
            if (a.mIfaceID != interfaceID)
                continue;

            for (Entry<String, ConnectionWrapper> e : mPendingConnections.entrySet()) {
                ConnectionWrapper cw = e.getValue();
                if (cw.wrapper != a)
                    continue;
                Log.v(TAG, "canceling connection for " + e.getKey());
                if (cw.mGattTool != null)
                    cw.mGattTool.disconnect();
                cw.mGattTool.setListener(null);
                cw.mGattTool.releaseWorker();
                cw.mGattTool = null;
                mPendingConnections.remove(e.getKey());
            }

            for (Entry<Integer, ConnectionWrapper> e : mConnectionMap.entrySet()) {
                ConnectionWrapper cw = e.getValue();
                if (cw.wrapper != a)
                    continue;
                Log.v(TAG, "forcing connection close for " + e.getKey());
                if (cw.mGattTool != null)
                    cw.mGattTool.disconnect();
                cw.mGattTool.setListener(null);
                cw.mGattTool.releaseWorker();
                cw.mGattTool = null;
                mConnectionMap.remove(e.getKey());
            }

            registeredApps.remove(v.getKey());
            try {
                a.mCallback.onAppDeregistered(interfaceID);
            } catch (RemoteException e) {
                Log.e(TAG, "failed notifying client of deregistration", e);
            }
            Log.v(TAG, "app successfully unregistered for interface: " + interfaceID +
                    ", uuid: " + v.getValue().mGattID);
            return;
        }
        Log.e(TAG, "interfaceID not known " + interfaceID);
        return;
    }

    private ConnectionWrapper getConnectionWrapperForAddress(String remote) {
        for (Entry<String, ConnectionWrapper> e : mPendingConnections.entrySet()) {
            ConnectionWrapper cw = e.getValue();
            if (cw.remote.equals(remote))
                return cw;
        }

        for (Entry<Integer, ConnectionWrapper> e : mConnectionMap.entrySet()) {
            ConnectionWrapper cw = e.getValue();
            if (cw.remote.equals(remote))
                return cw;
        }
        
        return null;
    }

    @Override
    /**
     * Change the security level of the connection.
     */
    public synchronized void setEncryption(String address, byte action) {
        Log.v(TAG, "setEncryption " + address + " " + action);
        Log.e(TAG, "not implemented");

        ConnectionWrapper cw = getConnectionWrapperForAddress(address);
        if (cw==null){
            Log.e(TAG, "no connection wrapper for this address");
            return;
        }
        if (cw.mGattTool==null){
            Log.w(TAG, "no gatttool wrapper for this address");
        }
        

        if (action == BleConstants.GATT_AUTH_REQ_NO_MITM)
            cw.mGattTool.secLevel(SEC_LEVEL.LOW);
        if (action == BleConstants.GATT_AUTH_REQ_MITM)
            cw.mGattTool.secLevel(SEC_LEVEL.MEDIUM);
        if (action == BleConstants.GATT_AUTH_REQ_SIGNED_NO_MITM || 
                action == BleConstants.GATT_AUTH_REQ_SIGNED_MITM)
            cw.mGattTool.secLevel(SEC_LEVEL.HIGH);
        else {
            Log.e(TAG, "invalid sec level");
            return;
        }
        try {
            this.wait();
        } catch (InterruptedException e) {
            Log.e(TAG, "interrupted while waiting sec-level to settle");
        }
    }
    
    @Override
    /**
     * callback from GattToolWrapper to let us know sec-level transaction completed
     */
    public synchronized void gotSecurityLevelResult(int conn_handle, int status) {
        Log.v(TAG, "got security level result " + conn_handle + " " + status);
        this.notifyAll();
    }

    private ConnectionWrapper getConnectionWrapperForConnID(int connID) {
        return this.getConnectionWrapperForConnID(connID, "NN");
    }
    
    private ConnectionWrapper getConnectionWrapperForConnID(int connID, String f) {
        if (!mConnectionMap.containsKey(connID)) {
            Log.e(TAG, "connection id not known on " + f);
            return null;
        }
        
        return mConnectionMap.get(connID);
    }
    
    /* ********************************************************************************
     * service search methods
     **********************************************************************************/
    @Override
    /**
     * Method called by binder clients to start a service discovery process
     */
    public synchronized void searchService(final int connID, final BluetoothGattID serviceID) {
        Log.v(TAG, "searchService " + connID + " " + serviceID);
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "searchService");
        if (cw == null)
            return;
        
        GattToolWrapper gatt = cw.mGattTool;
        
        if (gatt == null) {
            Log.e(TAG, "gatt tool wrapper is null!!!");
            return;
        }

        if (serviceID != null) {
            BleGattID i = null;
            int u16 = serviceID.getUuid16();
            if (u16 > 0)
                i = new BleGattID(new Integer(u16));
            else
                i = new BleGattID(serviceID.getUuid());
            cw.lastPrimaryUuid = i;
            if (cw.services.containsKey(i))
                cw.services.remove(i);
            Log.v(TAG, "searcing for uuid " + i);
            gatt.primaryDiscoveryByUUID(i);
        } else {
            Log.v(TAG, "doing a general primary service discovery");
            cw.lastPrimaryUuid = null;
            cw.services.clear();
            gatt.primaryDiscovery();
        }
    }
    
    @Override
    /**
     * This method is a GattToolWrapper callback that let us know for a primary result
     */
    public synchronized void primaryAll(int connID, int start, int end, BleGattID uuid) {
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "primaryAll");
        if (cw == null)
            return;
        
        if (!cw.services.containsKey(uuid))
            cw.services.put(uuid, new Vector<Service>());
        
        int sid = cw.services.get(uuid).size();
        int u16 = uuid.getUuid16();
        BluetoothGattID svcId;
        if (u16 > -1)
            svcId = new BluetoothGattID(sid, u16);
        else
            svcId = new BluetoothGattID(sid, uuid.getUuid());
        Service s = new Service(svcId, start, end);
        cw.services.get(uuid).add(s);
        try {
            cw.wrapper.mCallback.onSearchResult(connID, svcId);
        } catch (Exception e) {
            Log.e(TAG, "exception will calling onSearchResult");
        }
    }

    @Override
    /**
     * GattToolWrapper callback to let us know the primary scan completed.
     */
    public synchronized void primaryAllEnd(int connID, int status) {
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "primaryAllEnd");
        if (cw == null)
            return;
        
        try {
            cw.wrapper.mCallback.onSearchCompleted(connID, status);
        } catch (Exception e) {
            Log.e(TAG, "exception will calling onSearchCompleted");
        }
        this.notifyAll();
    }

    /**
     * callback from GattToolWrapper that let us know we have a result
     * from an uuid search.
     */
    @Override
    public void primaryUuid(int connID, int start, int end) {
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "primaryUuid");
        if (cw == null)
            return;
        
        BleGattID uuid = cw.lastPrimaryUuid;
        Log.v(TAG, "primaryUuid " + connID + ", " + start + ", " + end);
        this.primaryAll(connID, start, end, uuid);
    }

    /**
     * callback that let us know a service search with uuid set completed.
     */
    @Override
    public void primaryUuidEnd(int connID, int status) {
        Log.v(TAG, "primaryUuidEnd " + connID + ", " + status);
        this.primaryAllEnd(connID, status);
    }
    
    private Service getServiceForConnIDServiceID(int connID, BluetoothGattID serviceID){
        return getServiceForConnIDServiceID(connID, serviceID, "NN");
    }
    
    private Service getServiceForConnIDServiceID(int connID, BluetoothGattID serviceID, String f){
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "getServiceForConnIDServiceID for " + f);
        if (cw == null)
            return null;
        
        for (Entry<BleGattID, List<Service>>e: cw.services.entrySet()){
            if (!e.getKey().toString().equals(serviceID.toString()))
                continue;
            if (e.getValue().size()<serviceID.getInstanceID()){
                Log.e(TAG, "instanceID is bigger than known services list: " + 
                        e.getValue().size() + ", " +serviceID.getInstanceID());
                continue;
            }
            
            return e.getValue().get(serviceID.getInstanceID());
        }
        Log.v(TAG, "couldn't find ServiceWrapper in " + f);
        return null;
    }
    
    @Override
    /**
     * This method gets called by remote gatt client for registering a callback
     * function for service data related activities.
     */
    public synchronized void registerServiceDataCallback(int connID, BluetoothGattID serviceID,
            String address, IBleCharacteristicDataCallback callback) {
        Log.v(TAG, "registerServiceDataCallback");
       
        Service s = getServiceForConnIDServiceID(connID, serviceID, "registerServiceDataCallback");
        if (s==null){
            Log.e(TAG, "can't register callback");
            return;
        }
        s.callback = callback;
        Log.v(TAG, "registered succesffully");
    }
    
    /* ****************************************************************************
     * characteristics discovery methods
     ******************************************************************************/
    
    @Override
    /**
     * This method gets called by the binder client to start a service discovery.
     */
    public synchronized void getFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id)
    {
        Log.v(TAG, "getFirstChar " + connID + " " + serviceID + " " + id);
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "getFirstChar");
        Service s = getServiceForConnIDServiceID(connID, serviceID, "getFirstChar");
        if (cw==null || s==null || s.callback == null){
            Log.e(TAG, "no callback access, can't discover chars");
            return;
        }
        
        if (id!=null) {
            Log.e(TAG, "no included services support yet sorry.");
            try {
                s.callback.onGetFirstIncludedService(connID, BleConstants.GATT_ERROR, serviceID, id);
            } catch (RemoteException e) {
                Log.e(TAG, "exception while calling onGetFirstIncludedService");
            }
            return;
        }
        
        cw.mGattTool.characteristicsDiscovery(s.start, s.end);
        
        cw.lastService = s;
        
        s.lastCharResult = null;
        
        while (s.lastCharResult == null)
            try {
                Log.v(TAG, "waiting for lastCharResult to stop been null");
                this.wait();
            } catch (InterruptedException e) {
                Log.e(TAG, "interrupted while waiting for characteristics discovery to complete", e);
            }
        
        try {
            if (s.chars.size() > 0) {
                Characteristic c = s.chars.get(0);
                s.callback.onGetFirstCharacteristic(connID, s.lastCharResult.intValue(), 
                        serviceID, c.uuid, c.properties);
            }
            else
                s.callback.onGetFirstCharacteristic(connID, BleConstants.GATT_NOT_FOUND, 
                        serviceID, null, 0);
            s.lastCharResult = null;
        } catch (RemoteException e) {
            Log.e(TAG, "error while calling onGetFirstCharacteristic", e);
        }
    }
    
    @Override
    /**
     * GattToolWrapper callback that gets called once per discovered characteristic.
     */
    public synchronized void characteristic(int connID, int handle, short properties, int value_handle,
            BleGattID uuid) {
        Log.v(TAG, "got characteristic " + connID + " " + handle);
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "characteristic callback");
        if ( cw == null || cw.lastService == null)
            return;
        
        Service s = cw.lastService;
        s.addCharacteristic(new Characteristic(handle, properties, value_handle, uuid));
    }
    
    @Override
    /**
     * GattToolWrapper callback telling characteristic discovery completed
     */
    public synchronized void characteristicEnd(int connID, int status) {
        Log.v(TAG, "characteristicEnd " + connID + " " + status);
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "characteristicEnd callback");
        if ( cw == null || cw.lastService == null)
            return;
        Service s = cw.lastService;
        s.lastCharResult = new Integer(status);
        this.notifyAll();
    }
    
    @Override
    public synchronized void getNextChar(int connID, BluetoothGattCharID svcChrID, BluetoothGattID id) {
        Log.v(TAG, "getFirstChar " + connID + " " + svcChrID + " " + id);
        BluetoothGattID serviceID = svcChrID.getSrvcId();
        BluetoothGattID prevChar = svcChrID.getCharId();
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "getFirstChar");
        Service s = getServiceForConnIDServiceID(connID, serviceID, "getFirstChar");
        
        if (cw==null || s==null || s.callback == null){
            Log.e(TAG, "no callback access, can't discover chars");
            return;
        }
        
        if (id!=null) {
            Log.e(TAG, "no included services support yet sorry.");
            try {
                s.callback.onGetFirstIncludedService(connID, BleConstants.GATT_ERROR, serviceID, id);
            } catch (RemoteException e) {
                Log.e(TAG, "exception while calling onGetFirstIncludedService");
            }
            return;
        }
        
        BleGattID gid = null;
        short prop = 0;
        if (prevChar.getInstanceID() < s.chars.size()-1){
            Characteristic c = s.chars.get(prevChar.getInstanceID()+1);
            gid = c.uuid;
            prop = c.properties;
        }
        
        int status = gid != null ? BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR;

        try {
            s.callback.onGetNextCharacteristic(connID, status, serviceID, gid, prop);
        } catch (RemoteException e) {
            Log.e(TAG, "failed calling onGetNextCharacteristic with status: " + status +
                    " charID: " + gid);
        }
    }
    
    /* **************************************************************************************
     * char and descriptors reading methods
     */
    
    private Characteristic getCharacteristicFromService(Service service, BluetoothGattID charID, String f){
        
        if (service.chars==null || service.chars.size() < charID.getInstanceID()+1) {
            Log.v(TAG, "either no chars for this service, or the id is too big at " + f);
            return null;
        }
        return service.chars.get(charID.getInstanceID());
    }
    
    /**
     * called by binder client when it wants to get the value for the descriptors
     **/
    @Override
    public synchronized void readCharDescr(int connID, BluetoothGattCharDescrID charDescID, byte authReq) {
        Log.v(TAG, "readCharDescr " + connID + " " + charDescID + " " + authReq);
        BluetoothGattID serviceID = charDescID.getSrvcId();
        BluetoothGattID charID = charDescID.getCharId();
        BluetoothGattID descID = charDescID.getDescrId();
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "readCharDescr");
        Service s = getServiceForConnIDServiceID(connID, serviceID, "readCharDescr");
        
        if (cw==null || cw.mGattTool == null || s==null || s.callback == null){
            Log.e(TAG, "something is missing can't go on");
            return;
        }
        
        Characteristic c = getCharacteristicFromService(s, charID, "readCharDescr");
        if (c == null) {
            Log.e(TAG, "no characteristic can't go on");
            return;
        }
        
        if (c.descriptors.size() < descID.getInstanceID()){
            Log.e(TAG, "descriptor out of range");
            return;
        }
        Descriptor d = c.descriptors.get(descID.getInstanceID());
        cw.lastService = s;
        s.lastChar = c;
        c.lastDescriptor = d;
        cw.mGattTool.readCharacteristicByHandle(d.handle);    
    }
    
    @Override
    /**
     * called by binder client when it wants to get the value of a char
     **/
    public void readChar(int connID, BluetoothGattCharID charSvcID, byte authReq) {
        Log.v(TAG, "readChar " + connID + " " + charSvcID + " " + authReq);
        BluetoothGattID serviceID = charSvcID.getSrvcId();
        BluetoothGattID charID = charSvcID.getCharId();
        
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "readChar");
        Service s = getServiceForConnIDServiceID(connID, serviceID, "readChar");
        
        if (cw==null || cw.mGattTool == null || s==null || s.callback == null){
            Log.e(TAG, "something is missing can't go on");
            return;
        }
        
        Characteristic c = getCharacteristicFromService(s, charID, "readChar");
        if (c == null) {
            Log.e(TAG, "no characteristic can't go on");
            return;
        }
        
        cw.lastService = s;
        s.lastChar = c;
        c.lastDescriptor = null;
        cw.mGattTool.readCharacteristicByHandle(c.handle);    
    }
    
    @Override
    /**
     * This callback gets called when GattToolWrapper was able to resolve a value.
     */
    public synchronized void gotValueByHandle(int connID, byte[] value, int status) {
        Log.v(TAG, "gotValueByHandle " + connID +" " + status +" got" + value);
        ConnectionWrapper cw = getConnectionWrapperForConnID(connID, "gotValueByHandle");
        if (cw==null)
            return;
        Service s = cw.lastService;
        Characteristic c = s.lastChar;
        
        if (c.lastDescriptor != null) {
            Descriptor d = c.lastDescriptor;
            try {
                s.callback.onReadCharDescriptorValue(connID, status, s.uuid, c.uuid, d.uuid, value);
            } catch (RemoteException e) {
                Log.e(TAG, "error when calling onReadCharDescriptorValue", e);
            }
        } else {
            try {
                s.callback.onReadCharacteristicValue(connID, status, s.uuid, c.uuid, value);
            } catch (RemoteException e) {
                Log.e(TAG, "error when calling onReadCharacteristicValue", e);
            }
        }

        // cleanup stack.
        c.lastDescriptor = null;
        s.lastChar = null;
        cw.lastService = null;
    }

    class CharacteristicWrapper {
        BluetoothGattID gattID;
        String path;

        public CharacteristicWrapper(BluetoothGattID i, String p) {
            gattID = i;
            path = p;
        }
    }

    class ServiceWrapper {
        String mAddress;
        Map<String, String> mUuids = new HashMap<String, String>();
        Map<String, List<CharacteristicWrapper>> mCharacteristics = new HashMap<String,
                List<CharacteristicWrapper>>();
        IBleCharacteristicDataCallback mCallback;
        BleGattID svcID;

        public ServiceWrapper(String address, String uuid, String path) {
            super();
            this.svcID = new BleGattID(uuid);
            this.mAddress = address;
            this.mUuids.put(uuid, path);
        }
    }

    private Map<String, ServiceWrapper> mRemoteServices = new HashMap<String, ServiceWrapper>();

    @Override
    public void serviceDiscovered(int connID, String address, String uuid, String path) {
        if (mRemoteServices.get(address) != null) {
            mRemoteServices.get(address).mUuids.put(uuid, path);
        } else {
            ServiceWrapper w = new ServiceWrapper(address, uuid, path);
            mRemoteServices.put(address, w);
        }

        if (!mConnectionMap.containsKey(new Integer(connID))) {
            Log.e(TAG, "device got disconnected before we resolved services");
            return;
        }

        try {
            AppWrapper w = getConnectionWrapper(connID).wrapper;
            w.mCallback.onSearchResult(connID, new BluetoothGattID(uuid));
        } catch (RemoteException e) {
            Log.e(TAG, "error when sending back search results", e);
        }
    }

    @Override
    public void serviceDiscoveredFinished(int connID, int status) {
        AppWrapper w = mConnectionMap.get(new Integer(connID)).wrapper;
        try {
            if (w.mCallback != null)
                w.mCallback.onSearchCompleted(connID, status);
        } catch (RemoteException e) {
            Log.e(TAG, "error on serviceDiscoveredFinished", e);
        }
    }

    @Override
    public void characteristicsSolved(int connID, String serPath, List<Path> chars,
            List<BluetoothGattID> uuids) {
        String remote;
        ConnectionWrapper w = getConnectionWrapper(connID);
        if (w == null) {
            Log.e(TAG, "no connection wrapper can't do a thing");
            return;
        }

        remote = w.remote;

        if (mRemoteServices.get(remote) == null) {
            Log.e(TAG, "device is no longer in cache, WTF");
            return;
        }

        List<CharacteristicWrapper> t = new ArrayList<CharacteristicWrapper>();
        for (int i = 0; i < Math.min(chars.size(), uuids.size()); i++) {
            CharacteristicWrapper cw = new CharacteristicWrapper(uuids.get(i),
                    chars.get(i).toString());
            t.add(cw);
            Log.d(TAG, "added char for index " + i + " -. " + cw.gattID + " - " + cw.path);
        }

        mRemoteServices.get(remote).mCharacteristics.put(serPath, t);
    }

    private ConnectionWrapper getConnectionWrapper(int connID) {
        if (!mConnectionMap.containsKey(new Integer(connID))) {
            return null;
        }

        ConnectionWrapper conn = mConnectionMap.get(new Integer(connID));
        return conn;
    }

    private ServiceWrapper getServiceWrapper(String remote) {
        if (!mRemoteServices.containsKey(remote))
            return null;
        return mRemoteServices.get(remote);
    }

    private ServiceWrapper getServiceWrapper(int connID) {
        ConnectionWrapper conn = getConnectionWrapper(connID);
        return getServiceWrapper(conn.remote);

    }

    private List<CharacteristicWrapper> solveCharacteristics(int connID,
            BluetoothGattID serviceID) {
        ConnectionWrapper conn = getConnectionWrapper(connID);
        ServiceWrapper ser = getServiceWrapper(connID);

        if (!ser.mUuids.containsKey(serviceID.toString())) {
            Log.e(TAG, "uuid not known");
            return null;
        }

        String service = ser.mUuids.get(serviceID.toString());

        if (!ser.mCharacteristics.containsKey(service)) {
            try {
                mBluezInterface.getCharacteristicsForService(connID, conn.remote, service);
            } catch (Exception e) {
                Log.e(TAG, "failed getting characteristics", e);
                return null;
            }
            Log.v(TAG, "got characteristics");
        }

        return ser.mCharacteristics.get(service);
    }

   

    @Override
    public void getFirstCharDescr(int connID, BluetoothGattCharID charID, BluetoothGattID id) {
        Log.v(TAG, "getFirstCharDescr");
        // BlueZ doesn't support this over DBUS yet, so it makes no sense to do
        // any processing here.
        ServiceWrapper w = getServiceWrapper(connID);

        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or no callback, can't do anything");
            return;
        }

        Log.w(TAG, "not supported");

        try {
            w.mCallback.onGetFirstCharacteristicDescriptor(connID, BleConstants.GATT_NOT_FOUND,
                    charID.getSrvcId(), charID.getSrvcId(), null);
        } catch (RemoteException e) {
            Log.e(TAG, "onGetFirstCharacteristicDescriptor", e);
        }

        /*
         * Log.v(TAG, "getFirstCharDescr " + connID + " " + charID + " " + id);
         * ServiceWrapper w = getServiceWrapper(connID);
         * List<CharacteristicWrapper> lcw = solveCharacteristics(connID,
         * charID.getSrvcId()); if (lcw.size() == 0) { Log.v(TAG, "no match");
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID,
         * BleConstants.GATT_NOT_FOUND, charID.getSrvcId(), charID.getCharId(),
         * null); return; } CharacteristicWrapper cw = lcw.get(0); Object o =
         * this.mBluezInterface.GetCharacteristicValue(cw.path, "Format"); if (o
         * == null) { Log.v(TAG, "no format");
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID,
         * BleConstants.GATT_ERROR, charID.getSrvcId(), charID.getCharId(),
         * null); return; } Characteristic.Format fmt = (Characteristic.Format)
         * o; Log.v(TAG, "Got format " + fmt.NameSpace + " " + fmt.Description +
         * " " + fmt.Unit + " " + fmt.Format + " " + fmt.Exponent);
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID,
         * BleConstants.GATT_SUCCESS, charID.getSrvcId(), charID.getSrvcId(),
         * new BluetoothGattID(fmt.Description.intValue()));
         */
    }

    @Override
    public void getNextCharDescr(int connID, BluetoothGattCharDescrID charDescrID,
            BluetoothGattID id) {
        Log.v(TAG, "getNextCharDescr " + connID + " " + charDescrID + " " + id);

        // BlueZ doesn't support this over DBUS yet, so it makes no sense to do
        // any processing here.
        ServiceWrapper w = getServiceWrapper(connID);

        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or no callback, can't do anything");
            return;
        }

        Log.w(TAG, "not supported");

        try {
            w.mCallback.onGetNextCharacteristicDescriptor(connID, BleConstants.GATT_NOT_FOUND,
                    null,
                    null, null);
        } catch (RemoteException e) {
            Log.e(TAG, "onGetNextCharacteristicDescriptor", e);
        }

        /*
         * List<CharacteristicWrapper> lcw = solveCharacteristics(connID,
         * charDescrID.getSrvcId()); int i = 0; for (CharacteristicWrapper cw :
         * lcw) { if (cw.gattID.equals(charDescrID.getCharId())) break; i++; }
         * Log.v(TAG, "char wrapper " + ++i); CharacteristicWrapper cw =
         * lcw.get(i); Log.v(TAG, "got char wrapper"); Object o =
         * mBluezInterface.GetCharacteristicValue(cw.path, "Format"); Log.v(TAG,
         * " " + o); int status = i <= lcw.size() - 1 ?
         * BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR; if (o != null) {
         * Characteristic.Format fmt = (Characteristic.Format) o; Log.v(TAG,
         * "getFirstCharDescr " + fmt.Description);
         * w.mCallback.onGetNextCharacteristicDescriptor( connID, status,
         * charDescrID.getSrvcId(), charDescrID.getCharId(), new
         * BluetoothGattID(fmt.Description.intValue())); } else { Log.v(TAG,
         * "getFirstCharDescr no format ");
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID, status,
         * charDescrID.getSrvcId(), charDescrID.getCharId(), null); }
         */
    }

    private CharacteristicWrapper internalCharacteristicWrapper(int connID,
            BluetoothGattCharID svcID) {
        if (svcID == null)
            return null;

        List<CharacteristicWrapper> l = solveCharacteristics(connID, svcID.getSrvcId());
        if (l == null || l.size() == 0)
            return null;

        BluetoothGattID charID = svcID.getCharId();

        Iterator<CharacteristicWrapper> icw = l.iterator();
        while (icw.hasNext()) {
            CharacteristicWrapper cw = icw.next();
            if (charID.equals(cw.gattID)) {
                Log.v(TAG, "found match");
                return cw;
            }
        }
        return null;
    }

    private byte[] internalGetCharacteristicWrapperValue(int connID, BluetoothGattCharID svcID) {
        CharacteristicWrapper cw = internalCharacteristicWrapper(connID, svcID);
        byte[] value = null;
        if (cw != null) {
            try {
                value = mBluezInterface.GetCharacteristicValueValue(cw.path);
                Log.v(TAG, "got read value " + value);
            } catch (Exception e) {
                Log.e(TAG, "failed getting characteristic value", e);
            }
        }
        return value;
    }

    private boolean internalWriteCharWrapper(int connID, BluetoothGattCharID svcID, byte[] val) {
        CharacteristicWrapper cw = internalCharacteristicWrapper(connID, svcID);
        if (cw == null) {
            Log.e(TAG, "no characteristic wrapper");
            return false;
        }
        Log.v(TAG, "writting on path " + cw.path);
        try {
            return mBluezInterface.writeCharacteristicValue(cw.path, val);
        } catch (Exception e) {
            Log.e(TAG, "failed writting char value", e);
        }
        return false;
    }

    @Override
    public void writeCharValue(int connID, BluetoothGattCharID svcID, int writeType, byte authReq,
            byte[] value) {
        Log.v(TAG, "writeCharValue");

        ServiceWrapper w = getServiceWrapper(connID);
        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or callback can't do anything");
            return;
        }

        Log.v(TAG, "got ser wrapper");
        int status = BleConstants.GATT_SUCCESS;
        if (!w.svcID.equals(svcID.getSrvcId())) {
            Log.e(TAG, "wrong service ID");
            status = BleConstants.GATT_ERROR;
        } else {
            boolean r = internalWriteCharWrapper(connID, svcID, value);
            if (!r)
                status = BleConstants.GATT_ERROR;
        }

        try {
            w.mCallback.onWriteCharValue(connID, status,
                    svcID.getSrvcId(), svcID.getCharId());
        } catch (RemoteException e) {
            Log.e(TAG, "failed calling onWriteCharValue status: " + status);
        }
    }

    @Override
    public int getApiLevel() {
        return API_LEVEL;
    }

    @Override
    public String getFrameworkVersion() {
        return FRAMEWORK_VERSION;
    }

    // maps address to services been watched
    private Map<String, List<BluetoothGattCharID>> mNotificationListener =
            new HashMap<String, List<BluetoothGattCharID>>();

    private int internalRegisterForNotifications(ServiceWrapper ser, byte ifaceID,
            String address, BluetoothGattCharID charID) {

        if (!mNotificationListener.containsKey(address)) {
            Log.v(TAG, "address not registered for notifications, adding map");
            mNotificationListener.put(address, new Vector<BluetoothGattCharID>());
        }

        Log.v(TAG, "registering new notification receiver");

        if (!ser.mUuids.containsKey(charID.getSrvcId().toString())) {
            return BleConstants.GATT_ERROR;
        }

        String service = ser.mUuids.get(charID.getSrvcId().toString());

        Map<BluetoothGattID, String> ids = null;

        try {
            ids = mBluezInterface.getCharacteristicsForService(service);
        } catch (Exception e) {
            Log.e(TAG, "failed to get ids", e);
            return BleConstants.GATT_ERROR;
        }

        for (Entry<BluetoothGattID, String> e : ids.entrySet()) {
            Log.v(TAG, e.getValue() + " -> " + e.getKey());
            try {
                mBluezInterface.registerCharacteristicWatcher(e.getValue());
            } catch (BlueZConnectionError e1) {
                Log.e(TAG, "failed to get watcher registered", e1);
                continue;
            }
            mNotificationListener.get(address).add(charID);
            Log.v(TAG, "registered");
        }

        Log.v(TAG, "registered new notification listener");

        return BleConstants.GATT_SUCCESS;
    }

    @Override
    public boolean registerForNotifications(byte ifaceID, String address,
            BluetoothGattCharID charID) {
        Log.i(TAG,
                "registering for notifications from " + address + " for uuid " + charID.getCharId());

        ServiceWrapper ser = getServiceWrapper(address);
        if (ser == null || ser.mCallback == null)
            return false;

        int ret = internalRegisterForNotifications(ser, ifaceID, address, charID);

        try {
            ser.mCallback.onRegForNotifications(-1, ret, charID.getSrvcId(), charID.getCharId());
        } catch (RemoteException e) {
            Log.e(TAG, "failed during onRegForNotifications ret: " + ret);
        }

        return ret == BleConstants.GATT_SUCCESS;
    }

    @Override
    public boolean deregisterForNotifications(byte interfaceID, String address,
            BluetoothGattCharID charID) {

        if (!mNotificationListener.containsKey(address)) {
            Log.e(TAG, "deregisterForNotifications for non registered remote device");
            return false;
        }

        Log.i(TAG, "unregistering " + address + " from notifications");
        Vector<Integer> r = new Vector<Integer>();
        int i = 0;
        for (BluetoothGattCharID c : mNotificationListener.get(address)) {
            if (c.equals(charID)) {
                Log.v(TAG, "unregistering");
                r.add(i);
            }
            i++;
        }

        for (Integer I : r) {
            mNotificationListener.get(address).remove(I);
        }
        Log.v(TAG, "removed " + r.size() + " listeners");

        return r.size() > 0;
    }

    @Override
    public void setScanParameters(int scanInterval, int scanWindow) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI setscanparam\n");
        System.exit(0);
    }

    @Override
    public void filterEnable(boolean p) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI filterenable\n");
        System.exit(0);

    }

    @Override
    public void filterEnableBDA(boolean enable, int addr_type, String address) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI filterenablebda\n");
        System.exit(0);

    }

    @Override
    public void clearManufacturerData() {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI clearmanufcdata\n");
        System.exit(0);

    }

    @Override
    public void filterManufacturerData(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI filtermanufacdata\n");
        System.exit(0);

    }

    @Override
    public void filterManufacturerDataBDA(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4, boolean has_bda, int addr_type, String address) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI filtermanufdataBDA\n");
        System.exit(0);

    }

    @Override
    public void observe(boolean start, int duration) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI observe\n");
        System.exit(0);

    }

    @Override
    public void getFirstIncludedService(int connID, BluetoothGattID serviceID, BluetoothGattID id2) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI getFirstIncServ\n");
        System.exit(0);

    }

    @Override
    public void getNextIncludedService(int connID, BluetoothGattInclSrvcID includedServiceID,
            BluetoothGattID id) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI getNextIncServ\n");
        System.exit(0);

    }

    @Override
    public void writeCharDescrValue(int connID, BluetoothGattCharDescrID descID, int writeType,
            byte authReq, byte[] value) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI writechardescrvale\n");
        System.exit(0);

    }

    @Override
    public void sendIndConfirm(int connID, BluetoothGattCharID charID) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI sendindconfig\n");
        System.exit(0);

    }

    @Override
    public void prepareWrite(int paramInt1, BluetoothGattCharID charID, int paramInt2,
            int paramInt3, byte[] paramArrayOfByte) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI prepwriter\n");
        System.exit(0);

    }

    @Override
    public void executeWrite(int paramInt, boolean paramBoolean) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI execwriter\n");
        System.exit(0);

    }

    @Override
    public void registerServerServiceCallback(BluetoothGattID id1, BluetoothGattID id2,
            IBleServiceCallback callback) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI regServiceCallback\n");
        System.exit(0);

    }

    @Override
    public void registerServerProfileCallback(BluetoothGattID id, IBleProfileEventCallback callback) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI regServerProfCallback\n");
        System.exit(0);

    }

    @Override
    public void unregisterServerServiceCallback(int paramInt) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI unregServServcall\n");
        System.exit(0);

    }

    @Override
    public void unregisterServerProfileCallback(int paramInt) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI unrefServerProfCallback\n");
        System.exit(0);

    }

    @Override
    public void GATTServer_CreateService(byte paramByte, BluetoothGattID id, int paramInt) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddIncludedService(int paramInt1, int paramInt2) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddCharacteristic(int paramInt1, BluetoothGattID id, int paramInt2,
            int paramInt3, boolean paramBoolean, int paramInt4) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddCharDescriptor(int paramInt1, int paramInt2, BluetoothGattID id) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_DeleteService(int paramInt) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_StartService(int paramInt, byte paramByte) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_StopService(int paramInt) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_HandleValueIndication(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_HandleValueNotification(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_SendRsp(int paramInt1, int paramInt2, byte paramByte1, int paramInt3,
            int paramInt4, byte[] paramArrayOfByte, byte paramByte2, boolean paramBoolean) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_Open(byte paramByte, String paramString, boolean paramBoolean) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_CancelOpen(byte paramByte, String paramString, boolean paramBoolean) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_Close(int paramInt) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void valueChanged(String path, byte[] val) {
        String[] p = path.split("/");
        String dev = p[p.length - 2];
        Log.v(TAG, "device " + dev);

        BluetoothGattCharID id = null;

        if (!mNotificationListener.containsKey(dev)) {
            Log.v(TAG, "device not registered for notifications");
            return;
        }

        id = mNotificationListener.get(dev).get(0);

        ServiceWrapper w = null;

        if (!mRemoteServices.containsKey(dev)) {
            Log.v(TAG, "service wrapper not available can't notify");
            return;
        }

        w = mRemoteServices.get(dev);

        int connID = -1;

        for (Entry<Integer, ConnectionWrapper> conn : mConnectionMap.entrySet()) {
            if (conn.getValue().remote.equals(dev)) {
                connID = conn.getKey();
                break;
            }
        }

        if (connID == -1) {
            Log.v(TAG, "failed to resolve connID");
            return;
        }

        Log.v(TAG, "notifing");
        try {
            w.mCallback.onReadCharacteristicValue(connID, BleConstants.GATT_SUCCESS,
                    id.getSrvcId(), id.getCharId(), val);
        } catch (RemoteException e) {
            Log.e(TAG, "failed notifiying", e);
        }
    }

    @Override
    public void onNotification(int conn_handle, int handle, byte[] value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIndication(int conn_handle, int handle, byte[] value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void characteristicDescriptor(int conn_handle, int handle, BleGattID uuid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void characteristicDescriptorEnd(int conn_handle, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotValueByUuid(int conn_handle, int handle, byte[] value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotValueByUuidEnd(int conn_handle, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotWriteResult(int conn_handle, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotMtuResult(int conn_handle, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gotPsmResult(int psm) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processExit(int retcode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processStdinClosed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void shellError(SHELL_ERRORS e) {
        // TODO Auto-generated method stub

    }
}
