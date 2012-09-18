
package android.bluetooth.le.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.server.BlueZInterface.BlueZConnectionError;
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
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;

public class BluetoothGatt extends IBluetoothGatt.Stub implements BlueZInterface.Listener {
    private BlueZInterface mBluezInterface;
    private Context mContext;
    private BluetoothAdapter mAdapter;
    private IActivityManager mAm;
    private static String TAG = "BT-GATT";

    public static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";
    public static final String BLUETOOTH_LE_SERVICE = BleConstants.BLUETOOTH_LE_SERVICE;

    public static int API_LEVEL = 5;
    public static String FRAMEWORK_VERSION = "0.5.2";

    private Map<BluetoothGattID, AppWrapper> registeredApps = new HashMap<BluetoothGattID, AppWrapper>();
    private AppWrapper[] registeredAppsByID = new AppWrapper[Byte.MAX_VALUE];
    private byte mNextAppID = 0;

    private int mNextConnID = 0;

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

    public BluetoothGatt(Context ctx) {
        Log.v(TAG, "new bluetoothGatt");

        mContext = ctx;
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
    }

    @SuppressWarnings("rawtypes")
    @Override
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

        /**
         * LE provides - Address, RSSI, Name, Paired, Broadcaster, UUIDs, Class
         * 0 or no class at all<br>
         * <br>
         * BD/EDR provides: Address Class Icon RSSI Name Alias LegacyPairing
         * Paired UUIDs
         */
        if (prop.containsKey("Icon") && prop.containsKey("Icon") && prop.containsKey("Alias")
                && prop.containsKey("LegacyPairing"))
            return BleAdapter.DEVICE_TYPE_BREDR;

        Variant c = null;
        if (prop.containsKey("Class"))
            c = prop.get("Class");
        if (c != null && (Integer) c.getValue() != 0)
            return BleAdapter.DEVICE_TYPE_DUMO;

        return BleAdapter.DEVICE_TYPE_BLE;
    }

    @SuppressWarnings("unused")
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
    public void deviceDiscovered(String address, String name, short rssi) {
        Intent intent = new Intent(BluetoothDevice.ACTION_FOUND);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, mAdapter.getRemoteDevice(address));
        intent.putExtra(BluetoothDevice.EXTRA_NAME, name);
        intent.putExtra(BluetoothDevice.EXTRA_RSSI, rssi);
        intent.putExtra(BleAdapter.EXTRA_DEVICE_TYPE, BleAdapter.DEVICE_TYPE_BLE);
        broadcastIntent(intent);
    }

    @Override
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

    class ConnectionWrapper {
        int connID;
        AppWrapper wrapper;
        String remote;

        public ConnectionWrapper(int c, AppWrapper w, String r) {
            this.connID = c;
            this.wrapper = w;
            this.remote = r;
        }
    }

    private Map<Integer, ConnectionWrapper> mConnectionMap = new HashMap<Integer, ConnectionWrapper>();

    @Override
    public void open(byte interfaceID, final String remote, boolean foreground) {
        Log.v(TAG, "open " + interfaceID + " " + remote);

        final AppWrapper w = this.registeredAppsByID[interfaceID];
        if (mNextConnID == Integer.MAX_VALUE) {
            Log.e(TAG, "failed to connect to: " + remote + ", out of connection IDs");
            try {
                w.mCallback.onConnected(remote, -1);
            } catch (RemoteException e) {
                Log.e(TAG, "we failed to notify other end", e);
            }
            return;
        }

        Thread t = new Thread() {
            public void run() {
                try {
                    int conn = ++mNextConnID;
                    w.mCallback.onConnected(remote, conn);
                    mConnectionMap.put(new Integer(conn), new ConnectionWrapper(
                            conn, w, remote));
                } catch (RemoteException e) {
                    Log.e(TAG, "error during connection", e);
                }
            }
        };

        if (foreground)
            t.run();
        else
            t.start();
    }

    private boolean pingClient(Binder b) {
        return b.pingBinder();
    }

    @Override
    public void registerApp(BluetoothGattID appUuid, IBleClientCallback callback) {
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
        }

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
    public void unregisterApp(byte interfaceID) {
        for (Entry<BluetoothGattID, AppWrapper> v : registeredApps.entrySet()) {
            if (v.getValue().mIfaceID == interfaceID) {
                if (v.getValue().mCallback.asBinder().pingBinder())
                    try {
                        v.getValue().mCallback.onAppDeregistered(interfaceID);
                    } catch (RemoteException e) {
                        Log.e(TAG, "failed notifying client of deregistration", e);
                    }
                registeredApps.remove(v.getKey());
                Log.v(TAG, "app successfully unregistered for interface: " + interfaceID +
                        ", uuid: " + v.getValue().mGattID);
                return;
            }
        }
        Log.e(TAG, "interfaceID not known " + interfaceID);
        return;
    }

    @Override
    public void setEncryption(String address, byte action) {
        Log.v(TAG, "setEncryption " + address + " " + action);
        this.mAdapter.getRemoteDevice(address).createBond();
    }

    @Override
    public void searchService(final int connID, final BluetoothGattID serviceID) {
        Log.v(TAG, "searchService " + connID + " " + serviceID);

        final Integer id = new Integer(connID);
        if (!mConnectionMap.containsKey(id)) {
            Log.e(TAG, "id not known on search service");
            return;
        }

        new Thread() {
            public void run() {
                String address = mConnectionMap.get(id).remote;
                try {
                    mBluezInterface.getServices(connID, address, serviceID);
                } catch (Exception e) {
                    Log.e(TAG, "getServices error", e);
                }
            }
        }.start();
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
    public void registerServiceDataCallback(int connID, BluetoothGattID serviceID,
            String address, IBleCharacteristicDataCallback callback) {
        Log.v(TAG, "registerServiceDataCallback");
        ServiceWrapper w = getServiceWrapper(connID);
        w.mCallback = callback;
    }

    public void internalGetFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id) {
        Log.v(TAG, "getFirstChar " + connID + ", " + serviceID + ", " + id);
        ServiceWrapper w = getServiceWrapper(connID);
        List<CharacteristicWrapper> lcw = solveCharacteristics(connID, serviceID);

        int ret = BleConstants.GATT_SUCCESS;
        BluetoothGattID out = null;
        if (lcw == null || lcw.size() == 0) {
            ret = BleConstants.GATT_NOT_FOUND;
        } else {
            CharacteristicWrapper cw = lcw.get(0);
            String c = cw.path;
            Log.v(TAG, "onFirstChar " + c + " " + serviceID + " " + cw.gattID);
            out = cw.gattID;
        }

        if (w.mCallback != null) {
            try {
                w.mCallback.onGetFirstCharacteristic(connID, ret, serviceID, out);
            } catch (RemoteException e) {
                Log.e(TAG, "failed to tell other end status " + ret + " id: " + out);
            }
        } else {
            Log.e(TAG, "no callback known, can't call onGetFirstCharacteristic");
        }
    }

    @Override
    public void getFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id)
    {
        internalGetFirstChar(connID, serviceID, id);
    }

    private BluetoothGattID internalGetNextChar(int connID, BluetoothGattCharID svcID) {
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
                if (icw.hasNext()) {
                    Log.v(TAG, "has more chars");
                    return icw.next().gattID;
                }
            }
        }
        return null;
    }

    @Override
    public void getNextChar(int connID, BluetoothGattCharID svcID, BluetoothGattID id) {
        Log.v(TAG, "getNextChar");
        Log.v(TAG, connID + ", " + svcID + ", " + id);
        ServiceWrapper w = getServiceWrapper(connID);

        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or no callback, can't do anything");
            return;
        }

        BluetoothGattID gid = internalGetNextChar(connID, svcID);
        int status = gid != null ? BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR;

        try {
            w.mCallback.onGetNextCharacteristic(connID, status,
                    svcID.getSrvcId(), gid);
        } catch (RemoteException e) {
            Log.e(TAG, "failed calling onGetNextCharacteristic with status: " + status +
                    " charID: " + gid);
        }
    }

    @Override
    public void readCharDescr(int connID, BluetoothGattCharDescrID charDescID, byte authReq) {
        Log.v(TAG, "readCharDescr " + connID + " " + charDescID);

        ServiceWrapper w = getServiceWrapper(connID);

        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or no callback, can't do anything");
            return;
        }

        Log.v(TAG, "No support for this, so we will tell we can't find it");

        try {
            w.mCallback.onReadCharDescriptorValue(connID, BleConstants.GATT_NOT_FOUND,
                    null, null, null, null);
        } catch (RemoteException e) {
            Log.e(TAG, "failed sending onReadCharDescriptorValue");
        }
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
            } catch (Exception e) {
                Log.e(TAG, "failed getting characteristic value", e);
            }
        }
        return value;
    }

    @Override
    public void readChar(int connID, BluetoothGattCharID svcID, byte authReq) {
        Log.v(TAG, "readChar");

        ServiceWrapper w = getServiceWrapper(connID);
        if (w == null || w.mCallback == null) {
            Log.e(TAG, "no service wrapper or callback can't do anything");
            return;
        }

        Log.v(TAG, "got ser wrapper");

        byte[] value = null;

        if (!w.svcID.equals(svcID.getSrvcId())) {
            Log.e(TAG, "wrong service ID");
        } else {
            value = this.internalGetCharacteristicWrapperValue(connID, svcID);
        }

        int status = value != null ? BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR;

        Log.v(TAG, "doing onReadCharacteristicValue status: " + status + " value: " + value);
        try {
            w.mCallback.onReadCharacteristicValue(connID, status, svcID.getSrvcId(),
                    svcID.getCharId(), value);
        } catch (RemoteException e) {
            Log.e(TAG, "failed doing onReadCharacteristicValue");
        }
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

    @Override
    public boolean registerForNotifications(byte ifaceID, String address,
            BluetoothGattCharID charID) {
        Log.i(TAG,
                "registering for notifications from " + address + " for uuid " + charID.getCharId());

        if (!mNotificationListener.containsKey(address)) {
            Log.v(TAG, "address not registered for notifications, adding map");
            mNotificationListener.put(address, new Vector<BluetoothGattCharID>());
        }

        Log.v(TAG, "registering new notification receiver");

        ServiceWrapper ser = getServiceWrapper(address);

        if (!ser.mUuids.containsKey(charID.getSrvcId().toString())) {
            throw new RuntimeException("invalid service id");
        }

        String service = ser.mUuids.get(charID.getSrvcId().toString());

        Map<BluetoothGattID, String> ids = mBluezInterface.getCharacteristicsForService(service);

        for (Entry<BluetoothGattID, String> e : ids.entrySet()) {
            Log.v(TAG, e.getValue() + " -> " + e.getKey());
            mBluezInterface.registerCharacteristicWatcher(e.getValue());
            mNotificationListener.get(address).add(charID);
            Log.v(TAG, "registered");
        }

        Log.v(TAG, "registered new notification listener");
        return true;
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
    public void close(byte interfaceID, String remote, int clientID, boolean foreground) {
        // TODO Auto-generated method stub

        Log.v(TAG, "NI close\n");
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
}
