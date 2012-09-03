package android.bluetooth.le.server;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.bluez.Characteristic;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                                // TODO Auto-generated catch block
                                e.printStackTrace();
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

    public BluetoothGatt(Context ctx) throws RemoteException {
        Log.v(TAG, "new bluetoothGatt");

        mContext = ctx;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null)
            throw new RuntimeException("Bluetooth Adapter not ready");
        
        mAm = ActivityManagerNative.getDefault();
        if (mAm == null)
            throw new RuntimeException("Activity Manager not ready");
        
        this.initBroadcast();
        
        mBluezInterface = new BlueZInterface(this, mAdapter);
        mBluezInterface.Start();

        registerBroadcastReceiver(mReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public byte getDeviceType(String address) throws RemoteException {
        @SuppressWarnings("rawtypes")
        Map<String, Variant> prop = mBluezInterface.getDeviceProperties(address);
        if (prop == null)
            return BleConstants.GATT_UNDEFINED;

        /**
         * LE provides - Address, RSSI, Name, Paired Broadcaster UUIDs Class 0
         * or no class at all<br>
         * <br>
         * BD/EDR provides: Address Class Icon RSSI Name Alias LegacyPairing
         * Paired UUIDs
         */
        if (prop.containsKey("Icon") && prop.containsKey("Icon") && prop.containsKey("Alias")
                && prop.containsKey("LegacyPairing"))
            return BleAdapter.DEVICE_TYPE_BREDR;

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
            System.out.println(line);
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
            System.out.println(line);
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
        
        for (Method m: c.getMethods()){
            Log.v(TAG, "m " + m.getName());
            
            if (m.getName().equals("broadcastIntent")){
                mBroadcast = m;
                mBroadcastArgs = m.getParameterTypes();
                Log.v(TAG, "found method, argument count " + mBroadcastArgs.length);
                
                for (int i = 0; i < mBroadcastArgs.length ; i++){
                    Log.v(TAG, "argument " + i + " " + mBroadcastArgs[i]);
                }
                return;
            }
        }
        
        Log.e(TAG, "couldn't resolve broadcastIntent");
    }
    
    public void broadcastIntent(Intent intent) {
        
        if (mBroadcast == null){
            Log.v(TAG, "no broadcastIntent, sorry");
            return;
        }
        
        Object[] args = new Object[mBroadcastArgs.length];
        
        boolean flag = true;
        
        for (int i = 0; i < args.length ; i++){
            if (mBroadcastArgs[i].equals(Intent.class)){
                args[i] = intent;
                continue;
            } 
            
            if (mBroadcastArgs[i].equals(int.class)){
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
        
        try{
            mBroadcast.invoke(mAm, args);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) throws RemoteException {
        Log.v(TAG, "registering broadcast receiver");
        IBroadcastReceiver ireceiver = new IBroadcastReceiver(receiver);
        mAm.registerReceiver(null, null, ireceiver, filter, null);
        Log.v(TAG, "registered");
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
    public void getUUIDs(String address) throws RemoteException {
        List<String> uuids = mBluezInterface.getUUIDs(address);
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
    public void open(byte interfaceID, final String remote, boolean foreground)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("open " + interfaceID + " " + remote);

        final AppWrapper w = this.registeredAppsByID[interfaceID];
        if (mNextConnID == Integer.MAX_VALUE) {
            throw new RemoteException("Out of connection IDs");
        }
        Thread t = new Thread() {
            public void run() {
                try {
                    int conn = ++mNextConnID;
                    mConnectionMap.put(new Integer(conn), new ConnectionWrapper(
                            conn, w, remote));
                    w.mCallback.onConnected(remote, conn);
                } catch (RemoteException e) {
                    Log.e(TAG, "WTF", e);
                }
            }
        };
        
        if (foreground)
            t.run();
        else
            t.start();
    }

    @Override
    public void registerApp(BluetoothGattID appUuid, IBleClientCallback callback)
            throws RemoteException {

        System.out.println("register app " + appUuid + " callback " + callback);
        if (registeredApps.containsKey(appUuid))
            throw new RemoteException("uuid all ready registered");

        if (mNextAppID == Byte.MAX_VALUE)
            throw new RemoteException("Can't register more services");

        AppWrapper wrapper = new AppWrapper(appUuid, ++mNextAppID, callback);
        this.registeredAppsByID[wrapper.mIfaceID] = wrapper;
        registeredApps.put(appUuid, wrapper);
        callback.onAppRegistered((byte) BleConstants.GATT_SUCCESS, wrapper.mIfaceID);
    }

    @Override
    public void setEncryption(String address, byte action) throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("setEncryption " + address + " " + action);
        this.mAdapter.getRemoteDevice(address).createBond();
    }

    @Override
    public void searchService(final int connID, final BluetoothGattID serviceID)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("searchService " + connID + " " + serviceID);

        final Integer id = new Integer(connID);
        if (!mConnectionMap.containsKey(id)) {
            throw new RemoteException("Invalid connection id");
        }

        new Thread() {
            public void run() {
                String address = mConnectionMap.get(id).remote;
                try {
                    mBluezInterface.getServices(connID, address, serviceID);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
        // TODO Auto-generated method stub
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void serviceDiscoveredFinished(int connID, int status) {
        AppWrapper w = mConnectionMap.get(new Integer(connID)).wrapper;
        try {
            if (w.mCallback != null)
                w.mCallback.onSearchCompleted(connID, status);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void characteristicsSolved(int connID, String serPath, List<Path> chars,
            List<BluetoothGattID> uuids) {
        String remote;
        try {
            remote = getConnectionWrapper(connID).remote;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

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

    private ConnectionWrapper getConnectionWrapper(int connID) throws RemoteException {
        if (!mConnectionMap.containsKey(new Integer(connID))) {
            throw new RemoteException("invalid device");
        }

        ConnectionWrapper conn = mConnectionMap.get(new Integer(connID));
        return conn;
    }

    private ServiceWrapper getServiceWrapper(int connID) throws RemoteException {
        ConnectionWrapper conn = getConnectionWrapper(connID);
        String address = conn.remote;

        if (!mRemoteServices.containsKey(address)) {
            throw new RemoteException("Address not in cache");
        }

        return mRemoteServices.get(address);

    }

    private List<CharacteristicWrapper> solveCharacteristics(int connID,
            BluetoothGattID serviceID) throws RemoteException {
        ConnectionWrapper conn = getConnectionWrapper(connID);
        ServiceWrapper ser = getServiceWrapper(connID);

        if (!ser.mUuids.containsKey(serviceID.toString())) {
            throw new RuntimeException("invalid service id");
        }

        String service = ser.mUuids.get(serviceID.toString());

        if (!ser.mCharacteristics.containsKey(service)) {
            mBluezInterface.getCharacteristicsForService(connID, conn.remote, service);
            Log.v(TAG, "got characteristics");
        }

        return ser.mCharacteristics.get(service);
    }

    @Override
    public void registerServiceDataCallback(int connID, BluetoothGattID serviceID,
            String address, IBleCharacteristicDataCallback callback)
            throws RemoteException {
        System.out.println("registerServiceDataCallback");
        ServiceWrapper w = getServiceWrapper(connID);
        w.mCallback = callback;
    }

    public void internalGetFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id)
            throws RemoteException {
        System.out.println( "getFirstChar " + connID + ", " + serviceID + ", " + id);
        ServiceWrapper w = getServiceWrapper(connID);
        List<CharacteristicWrapper> lcw = solveCharacteristics(connID, serviceID);
        if (lcw.size() == 0) {
            if (w.mCallback != null) {
                w.mCallback.onGetFirstCharacteristic(connID, BleConstants.GATT_NOT_FOUND,
                        serviceID, null);
            }
            return;
        }
        CharacteristicWrapper cw = lcw.get(0);
        String c = cw.path;
        System.out.println("onFirstChar " + c + " " + serviceID + " " + cw.gattID);

        if (w.mCallback != null) {
            w.mCallback.onGetFirstCharacteristic(connID, BleConstants.GATT_SUCCESS,
                    serviceID, cw.gattID);
        }
    }

    @Override
    public void getFirstChar(int connID, BluetoothGattID serviceID, BluetoothGattID id)
    {
        try {
            internalGetFirstChar(connID, serviceID, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getNextChar(int connID, BluetoothGattCharID charID, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("getNextChar");
        try {
            System.out.println("getNextChar " + connID + ", " + charID + ", " + id);

            ServiceWrapper w = getServiceWrapper(connID);
            int i = 0;
            BluetoothGattID gattID = charID.getCharId();
            List<CharacteristicWrapper> l = solveCharacteristics(connID, charID.getSrvcId());
            for (CharacteristicWrapper cw : l) {
                if (gattID.equals(cw.gattID)) {
                    break;
                }
                i++;
            }
            System.out.println("char i: " + i + " out of " + l.size());

            if (i == l.size()) {
                System.out.println("" + connID + ", " + charID);
                w.mCallback.onGetNextCharacteristic(connID,
                        BleConstants.GATT_NOT_FOUND,
                        charID.getSrvcId(), null);
                return;
            }

            if (w.mCallback != null) {
                System.out.println("sending onGetNextCharacteristic " + connID);
                int status = i < l.size() - 1 ? BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR;
                Log.d(TAG, "status " + status);
                if (charID != null)
                    Log.d(TAG, "srvcId " + charID.getSrvcId());
                Log.d(TAG, "gattID" + l.get(i).gattID);

                w.mCallback.onGetNextCharacteristic(connID,
                        status,
                        charID.getSrvcId(), l.get(i).gattID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readCharDescr(int connID, BluetoothGattCharDescrID charDescID, byte authReq)
            throws RemoteException {
        System.out.println("readCharDescr " + connID + " " + charDescID);
        // TODO Auto-generated method stub

    }

    @Override
    public void getFirstCharDescr(int connID, BluetoothGattCharID charID, BluetoothGattID id)
            throws RemoteException {
        System.out.println("getFirstCharDescr " + connID + " " + charID + " " + id);

        // TODO: not implemented yet

        ServiceWrapper w = getServiceWrapper(connID);
        
        List<CharacteristicWrapper> lcw = solveCharacteristics(connID, charID.getSrvcId());
        
        if (lcw.size() == 0) {       
            System.out.println("no match");
            w.mCallback.onGetFirstCharacteristicDescriptor(connID, BleConstants.GATT_NOT_FOUND,
                charID.getSrvcId(), charID.getSrvcId(), null);
            return;
        }
        
        CharacteristicWrapper cw = lcw.get(0);
        Object o = this.mBluezInterface.GetCharacteristicValue(cw.path, "Format");
        if (o == null){
            System.out.println("no format");
            w.mCallback.onGetFirstCharacteristicDescriptor(connID, BleConstants.GATT_ERROR,
                charID.getSrvcId(), charID.getSrvcId(), null);
            return;
        }
        
        Characteristic.Format fmt = (Characteristic.Format)o;
        System.out.println("Got format " + fmt.NameSpace + " " + fmt.Description + " " + fmt.Unit + 
                " " + fmt.Format + " " + fmt.Exponent);
        
        w.mCallback.onGetFirstCharacteristicDescriptor(connID, BleConstants.GATT_SUCCESS, 
                charID.getSrvcId(), charID.getSrvcId(), new BluetoothGattID(fmt.Description.intValue()));
        
        /*
         * ServiceWrapper w = getServiceWrapper(connID);
         * System.out.println("got ser wrapper"); CharacteristicWrapper cw =
         * solveCharacteristics(connID, charID.getSrvcId()).get(0);
         * System.out.println("got char wrapper"); Object o =
         * mBluezInterface.GetCharacteristicValue(cw.path, "Format");
         * System.out.println("got char value " + o); if (o != null) {
         * Characteristic.Format fmt = (Characteristic.Format) o;
         * System.out.println("getFirstCharDescr " + fmt.Description);
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID,
         * BleConstants.GATT_SUCCESS, charID.getSrvcId(), charID.getCharId(),
         * new BluetoothGattID(fmt.Description.intValue())); } else { Log.v(TAG,
         * "getFirstCharDescr no format ");
         * w.mCallback.onGetFirstCharacteristicDescriptor(connID,
         * BleConstants.GATT_ERROR, charID.getSrvcId(), charID.getCharId(),
         * null); }
         */
    }

    @Override
    public void getNextCharDescr(int connID, BluetoothGattCharDescrID charDescrID,
            BluetoothGattID id) throws RemoteException {
        System.out.println("getNextCharDescr " + connID + " " + charDescrID + " " + id);

        ServiceWrapper w = getServiceWrapper(connID);
        System.out.println("got ser wrapper");

        List<CharacteristicWrapper> lcw = solveCharacteristics(connID, charDescrID.getSrvcId());

        int i = 0;
        for (CharacteristicWrapper cw : lcw) {
            if (cw.gattID.equals(charDescrID.getCharId()))
                break;
            i++;
        }

        Log.v(TAG, "char wrapper " + ++i);

        CharacteristicWrapper cw = lcw.get(i);
        System.out.println("got char wrapper");
        Object o = mBluezInterface.GetCharacteristicValue(cw.path, "Format");
        System.out.println("got char value " + o);
        int status = i <= lcw.size() - 1 ? BleConstants.GATT_SUCCESS : BleConstants.GATT_ERROR;
        if (o != null) {
            Characteristic.Format fmt = (Characteristic.Format) o;
            System.out.println("getFirstCharDescr " + fmt.Description);
            w.mCallback.onGetNextCharacteristicDescriptor(
                    connID,
                    status,
                    charDescrID.getSrvcId(), charDescrID.getCharId(),
                    new BluetoothGattID(fmt.Description.intValue()));
        } else {
            Log.v(TAG, "getFirstCharDescr no format ");
            w.mCallback.onGetFirstCharacteristicDescriptor(connID,
                    status,
                    charDescrID.getSrvcId(),
                    charDescrID.getCharId(),
                    null);
        }

    }
    
    @Override
    public void readChar(int connID, BluetoothGattCharID charID, byte authReq)
            throws RemoteException {

        System.out.println("readChar\n");
        
        ServiceWrapper w = getServiceWrapper(connID);
        System.out.println("got ser wrapper");

        List<CharacteristicWrapper> lcw = solveCharacteristics(connID, charID.getSrvcId());

        int i = 0;
        for (CharacteristicWrapper cw : lcw) {
            if (cw.gattID.equals(charID.getCharId()))
                break;
            i++;
        }
        
        if (i == lcw.size()){
            System.out.println("out of bounds");
            w.mCallback.onReadCharacteristicValue(connID, BleConstants.GATT_ERROR, 
                    charID.getSrvcId(), charID.getCharId(), null);
            return;
        }

        Log.v(TAG, "char wrapper " + i);

        CharacteristicWrapper cw = lcw.get(i);
        System.out.println("got char wrapper");
        
        Object o = mBluezInterface.GetCharacteristicValue(cw.path, "Value");
        System.out.println("got char value " + o);
        
        byte[] d = (byte[]) o;
        System.out.println("got char value " + d);
        
        w.mCallback.onReadCharacteristicValue(connID, BleConstants.GATT_SUCCESS, 
                charID.getSrvcId(), charID.getCharId(), d);
    }

    @Override
    public void writeCharValue(int connID, BluetoothGattCharID charID, int writeType, byte authReq,
            byte[] value) throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("writeCharValue");
        ServiceWrapper w = getServiceWrapper(connID);
        if (!w.svcID.equals(charID.getSrvcId())) {
            System.out.println("oops svcid is different\n");
        }

        for (List<CharacteristicWrapper> lcw : w.mCharacteristics.values()) {
            for (CharacteristicWrapper cw : lcw) {
                if (cw.gattID.equals(charID.getCharId())) {
                    if (this.mBluezInterface.writeCharacteristicValue(cw.path, value)){
                        w.mCallback.onWriteCharValue(connID, BleConstants.GATT_SUCCESS,
                                charID.getSrvcId(), charID.getCharId());
                        return;
                    }
                }
            }
        }
        w.mCallback.onWriteCharValue(connID, BleConstants.GATT_ERROR,
                charID.getSrvcId(), charID.getCharId());
    }
    
    @Override
    public int getApiLevel() throws RemoteException {
        return API_LEVEL;
    }

    @Override
    public String getFrameworkVersion() throws RemoteException {
        return FRAMEWORK_VERSION;
    }

    @Override
    public void unregisterApp(byte interfaceID) throws RemoteException {
        // TODO Auto-generated method stub
        System.out.println("NI unregisterapp\n");
        System.exit(0);
    }

    @Override
    public void setScanParameters(int scanInterval, int scanWindow) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI setscanparam\n");
        System.exit(0);
    }

    @Override
    public void filterEnable(boolean p) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI filterenable\n");
        System.exit(0);

    }

    @Override
    public void filterEnableBDA(boolean enable, int addr_type, String address)
            throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI filterenablebda\n");
        System.exit(0);

    }

    @Override
    public void clearManufacturerData() throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI clearmanufcdata\n");
        System.exit(0);

    }

    @Override
    public void filterManufacturerData(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI filtermanufacdata\n");
        System.exit(0);

    }

    @Override
    public void filterManufacturerDataBDA(int company, byte[] data1, byte[] data2, byte[] data3,
            byte[] data4, boolean has_bda, int addr_type, String address) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI filtermanufdataBDA\n");
        System.exit(0);

    }

    @Override
    public void observe(boolean start, int duration) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI observe\n");
        System.exit(0);

    }

    @Override
    public void close(byte interfaceID, String remote, int clientID, boolean foreground)
            throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI close\n");
        System.exit(0);

    }

    @Override
    public void getFirstIncludedService(int connID, BluetoothGattID serviceID, BluetoothGattID id2)
            throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI getFirstIncServ\n");
        System.exit(0);

    }

    @Override
    public void getNextIncludedService(int connID, BluetoothGattInclSrvcID includedServiceID,
            BluetoothGattID id) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI getNextIncServ\n");
        System.exit(0);

    }

    @Override
    public void writeCharDescrValue(int connID, BluetoothGattCharDescrID descID, int writeType,
            byte authReq, byte[] value) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI writechardescrvale\n");
        System.exit(0);

    }

    @Override
    public void sendIndConfirm(int connID, BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI sendindconfig\n");
        System.exit(0);

    }

    @Override
    public void prepareWrite(int paramInt1, BluetoothGattCharID charID, int paramInt2,
            int paramInt3, byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI prepwriter\n");
        System.exit(0);

    }

    @Override
    public void executeWrite(int paramInt, boolean paramBoolean) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI execwriter\n");
        System.exit(0);

    }

    @Override
    public void registerForNotifications(byte interfaceID, String address,
            BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI registerForNot\n");
        System.exit(0);

    }

    @Override
    public void deregisterForNotifications(byte interfaceID, String address,
            BluetoothGattCharID charID) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI deregForNot\n");
        System.exit(0);

    }

    @Override
    public void registerServerServiceCallback(BluetoothGattID id1, BluetoothGattID id2,
            IBleServiceCallback callback) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI regServiceCallback\n");
        System.exit(0);

    }

    @Override
    public void registerServerProfileCallback(BluetoothGattID id, IBleProfileEventCallback callback)
            throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI regServerProfCallback\n");
        System.exit(0);

    }

    @Override
    public void unregisterServerServiceCallback(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI unregServServcall\n");
        System.exit(0);

    }

    @Override
    public void unregisterServerProfileCallback(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub

        System.out.println("NI unrefServerProfCallback\n");
        System.exit(0);

    }

    @Override
    public void GATTServer_CreateService(byte paramByte, BluetoothGattID id, int paramInt)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddIncludedService(int paramInt1, int paramInt2) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddCharacteristic(int paramInt1, BluetoothGattID id, int paramInt2,
            int paramInt3, boolean paramBoolean, int paramInt4) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_AddCharDescriptor(int paramInt1, int paramInt2, BluetoothGattID id)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_DeleteService(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_StartService(int paramInt, byte paramByte) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_StopService(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_HandleValueIndication(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_HandleValueNotification(int paramInt1, int paramInt2,
            byte[] paramArrayOfByte) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_SendRsp(int paramInt1, int paramInt2, byte paramByte1, int paramInt3,
            int paramInt4, byte[] paramArrayOfByte, byte paramByte2, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_Open(byte paramByte, String paramString, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_CancelOpen(byte paramByte, String paramString, boolean paramBoolean)
            throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void GATTServer_Close(int paramInt) throws RemoteException {
        // TODO Auto-generated method stub
        System.exit(0);

    }
}
