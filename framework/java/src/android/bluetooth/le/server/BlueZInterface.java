/**
 * 
 */

package android.bluetooth.le.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.server.handlers.DeviceFoundHandler;
import android.os.RemoteException;
import android.util.Log;

import com.broadcom.bt.le.api.BleConstants;
import com.broadcom.bt.service.gatt.BluetoothGattID;

import org.bluez.Adapter;
import org.bluez.Characteristic;
import org.bluez.Device;
import org.bluez.Error;
import org.bluez.Manager;
import org.bluez.Service;
import org.bluez.Watcher;
import org.freedesktop.DBus;
import org.freedesktop.DBus.NameOwnerChanged;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * @author manuel
 */
public class BlueZInterface {
    public interface Listener {
        public void deviceDiscovered(String address, String name, short rssi);

        public void serviceDiscovered(int connID, String address, String uuid, String path);

        // public void deviceCreated(String path);

        public void serviceDiscoveredFinished(int connID, int status);

        public void characteristicsSolved(int connID, String serPath,
                List<Path> charPath, List<BluetoothGattID> uuids);
        
        public void valueChanged(String charPath, Map<String, Variant> value);
        public void rawValueChanged(String charPath, List<Byte> value);
    }

    private static final String DBUS_BLUEZ = "org.bluez";

    static String TAG = "BluetoothLEService";
    private DBusConnection bus = null;
    private Manager manager = null;
    private Adapter adapter = null;
    private Listener listener = null;
    private CharacteristicWatcher mWatcher = null;
    private List<String> mWatchedServices = null;

    private BluetoothAdapter mAndroidBluetoothAdapter;

    public BlueZInterface(Listener listener, BluetoothAdapter mAndroidBluetoothAdapter) {
        super();
        this.listener = listener;
        this.mAndroidBluetoothAdapter = mAndroidBluetoothAdapter;
    }

    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = new HashMap<Class, DBusSigHandler>();

    public synchronized Listener getListener() {
        return listener;
    }

    public synchronized void setListener(Listener l) {
        this.listener = l;
    }

    public synchronized void removeListener() {
        this.listener = null;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private synchronized void disconnectDBus() {        
        adapter = null;
        manager = null;

        if (bus == null)
            return;

        // there's no way we will not have a bus but a watcher
        if (mWatcher != null){
            for (String p : mWatchedServices){
                try {
                    Service s;
                    s = bus.getRemoteObject(DBUS_BLUEZ, p, Service.class);
                    s.UnregisterCharacteristicsWatcher(new Path(Watcher.PATH));
                } catch (Exception e){
                    Log.e(TAG, "failure while unregistering watcher", e);
                }
            }
            mWatchedServices = null;
            bus.unExportObject(Watcher.PATH);
            mWatcher = null;
        }

        Iterator<Map.Entry<Class, DBusSigHandler>> it =
                listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class, DBusSigHandler> pairs =
                    (Map.Entry<Class, DBusSigHandler>) it.next();

            try {
                bus.removeSigHandler(pairs.getKey(), pairs.getValue());
            } catch (DBusException e) {
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        listeners.clear();
        bus.disconnect();
        bus = null;
        Log.i(TAG, "disconnected from bus");
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private synchronized void connectDBus() {
        if (bus != null) {
            Log.i(TAG, "DBus is connected, no need to connect again");
            return;
        }

        try {
            Log.i(TAG, "getting on bus");
            bus = DBusConnection.getConnection(DBusConnection.SYSTEM);

            Log.i(TAG, "registering signals");
            // DBusSigHandler s = new DBusOwnerNameChanged(this);
            // bus.addSigHandler(NameOwnerChanged.class, s);
            // listeners.put(NameOwnerChanged.class, s);
            Log.i(TAG, "dbus handlers registered");
            return;
        } catch (DBusException e) {
            Log.e(TAG, "Failed to get on bus", e);
        }
        Stop();
    }

    private synchronized Manager getBluezManager() {
        if (bus == null)
            return null;

        Manager out;
        try {
            out = bus.getRemoteObject(DBUS_BLUEZ, "/", Manager.class);
        } catch (DBusException e) {
            Log.e(TAG, "BlueZ isn't available", e);
            return null;
        } catch (DBus.Error.ServiceUnknown e1) {
            Log.e(TAG, "BlueZ isn't ready");
            return null;
        }

        Log.d(TAG, "Got manager object");
        return out;
    }

    public synchronized void resetBlueZConnection() {
        this.adapter = null;
        this.manager = null;
    }

    private synchronized Adapter getBluezAdapter() {
        if (bus == null)
            return null;

        if (manager == null)
            return null;

        Adapter out;
        try {
            String p = manager.DefaultAdapter().toString();
            out = bus.getRemoteObject(DBUS_BLUEZ, p, Adapter.class);
        } catch (DBusException e) {
            Log.e(TAG, "BlueZ issue", e);
            return null;
        } catch (DBus.Error.ServiceUnknown e1) {
            Log.e(TAG, "BlueZ isn't ready");
            return null;
        }

        Log.d(TAG, "Got manager object");
        return out;
    }

    private String getAdapterPath() {
        if (manager == null)
            return null;

        return manager.DefaultAdapter().toString();
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public synchronized boolean Start(boolean disconnect) {
        if (disconnect == true)
            disconnectDBus();
        connectDBus();
        manager = getBluezManager();
        adapter = getBluezAdapter();

        try {
            DBusSigHandler s = new DeviceFoundHandler(this);
            bus.addSigHandler(org.bluez.Adapter.DeviceFound.class, s);
            listeners.put(NameOwnerChanged.class, s);
        } catch (DBusException e) {
            Log.e(TAG, "failed registering for dbus signal", e);
        }

        return adapter != null;
    }

    public synchronized boolean Start() {
        return Start(false);
    }

    public synchronized void Stop() {
        this.disconnectDBus();
    }

    public synchronized boolean Status() {
        return adapter != null;
    }

    private String createDevice(String address) {
        try {
            Path d = adapter.CreateDevice(address);
            if (d == null)
                return null;
            return d.toString();
        } catch (Error.Failed e1) {
            Log.e(TAG, "error", e1);
            return null;
        } catch (Error.InvalidArguments e2) {
            Log.e(TAG, "error", e2);
            throw new RuntimeException("Invalid address " + e2.toString());
        }
    }

    private String findDevice(String address) {

        try {
            Path d = adapter.FindDevice(address);
            if (d != null)
                return d.toString();
            return createDevice(address);
        } catch (Error.DoesNotExist e1) {
            return createDevice(address);
        } catch (Error.InvalidArguments e2) {
            Log.e(TAG, "error", e2);
            throw new RuntimeException("Invalid address " + e2.toString());
        }
    }

    public Device getDevice(String address) {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }

        String path = findDevice(address);

        if (path == null)
            return null;

        try {
            return bus.getRemoteObject(DBUS_BLUEZ, path, Device.class);
        } catch (DBusException e) {
            Log.e(TAG, "error", e);
            throw new RuntimeException("Something failed " + e.toString());
        }
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Variant> getDeviceProperties(String address) {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }

        Device d;
        d = getDevice(address);

        if (d == null)
            return null;

        try {
            Map<String, Variant> o = d.GetProperties();
            System.out.println("device " + o);
            return o;
        } catch (Error.DoesNotExist e1) {
            Log.e(TAG, "error", e1);
            return null;
        } catch (Error.InvalidArguments e2) {
            throw new RuntimeException("Invalid address " + e2.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getUUIDs(String address) {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }

        ArrayList<String> out = new ArrayList<String>();
        Map<String, Variant> temp = getDeviceProperties(address);

        if (temp.containsKey("UUIDs")) {
            List<String> t = (List<String>) temp.get("UUIDs").getValue();
            for (String k : t)
                out.add(k);
        }

        return out;
    }
    
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public Map<String, String> getServicesPathForID(String address, BluetoothGattID serviceID){
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }
        
        Map<String, String> out = new HashMap<String, String>();
        Map<String, Variant> temp = getDeviceProperties(address);
        
        if (temp.containsKey("Services")) {
            List<Path> services = (List<Path>) temp.get("Services").getValue();
            for (Path p : services) {
                Service s;
                try {
                    s = bus.getRemoteObject(DBUS_BLUEZ, p.toString(), Service.class);
                    Map<String, Variant> sprop = s.GetProperties();
                    Log.d(TAG, "service " + sprop);
                    if (!sprop.containsKey("UUID"))
                        continue;

                    String s_uuid = sprop.get("UUID").getValue().toString().trim();
                    Log.d(TAG, "service " + p.toString() + " " + s_uuid);

                    if (s_uuid.length() > 8 && !s_uuid.endsWith("1000-8000-00805f9b34fb"))
                        continue;

                    Log.d(TAG, "possible match");
                    BluetoothGattID id;
                    if (s_uuid.length() <= 8) {
                        id = BluetoothGattID.getUuuid128FromUuid16(
                                Integer.valueOf(s_uuid, 16));
                    } else {
                        try {
                            id = new BluetoothGattID(s_uuid);
                        } catch (IllegalArgumentException e) {
                            Log.v(TAG, "ignoring");
                            continue;
                        }
                    }

                    if (serviceID == null || serviceID.equals(id)) {
                        Log.d(TAG, "match!");
                        out.put(p.toString(), id.toString());
                    }

                } catch (DBusException e) {
                    Log.e(TAG, "error while resolving services", e);
                    return null;
                }

            }
        }
        return out;
    }

    public void getServices(int connID, String address, BluetoothGattID serviceID)
            throws RemoteException {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }

        Map<String, String> paths = getServicesPathForID(address, serviceID);
        
        if (paths == null){
            listener.serviceDiscoveredFinished(connID, BleConstants.GATT_ERROR);
            return;
        }
        
        for(Entry<String, String> p: paths.entrySet()){
            listener.serviceDiscovered(connID, address, p.getValue(), p.getKey());
        }
        listener.serviceDiscoveredFinished(connID, BleConstants.GATT_SUCCESS);
    }

    @SuppressWarnings("unchecked")
    public void getCharacteristicsForService(int connID, String address, String serPath)
            throws RemoteException {
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }

        String d = findDevice(address);
        Service s;
        List<Path> cha = null;
        List<BluetoothGattID> uuids = null;
        try {
            s = bus.getRemoteObject(DBUS_BLUEZ, serPath, Service.class);
            cha = s.DiscoverCharacteristics();
            uuids = new ArrayList<BluetoothGattID>();
            for (Path p : cha) {
                Characteristic c = bus.getRemoteObject(DBUS_BLUEZ, p.toString(),
                        Characteristic.class);
                Map<String, Variant> prop = c.GetProperties();
                System.out.println("Char " + prop);
                if (prop.containsKey("UUID")) {
                    Object u = c.GetProperties().get("UUID").getValue();
                    System.out.println("uuid " + u);
                    uuids.add(new BluetoothGattID(u.toString()));
                } else {
                    Log.e(TAG, "oops no uuid");
                    uuids.add(null);
                }
            }
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listener.characteristicsSolved(connID, serPath, cha, uuids);
    }

    public Object GetCharacteristicValue(String path, String key) {
        Characteristic c;
        try {
            c = bus.getRemoteObject(DBUS_BLUEZ, path,
                    Characteristic.class);
            Map<String, Variant> p = c.GetProperties();
            System.out.println("char " + p);
            if (!p.containsKey(key))
                return null;
            return p.get(key).getValue();
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    class CharacteristicWatcher implements org.bluez.Watcher{

        @SuppressWarnings("rawtypes")
        @Override
        public void ValueChanged(Path characteristic, Map<String, Variant> values) {
            Log.v(TAG, "CharacteristicWatcher.ValueChanged " + characteristic);
            BlueZInterface.this.listener.valueChanged(characteristic.getPath(), values);
        }

        @Override
        public void RawValueChanged(Path characteristic, List<Byte> values) {
            Log.v(TAG, "CharacteristicWatcher.RawValueChanged " + characteristic);
            BlueZInterface.this.listener.rawValueChanged(characteristic.getPath(), values);
        }

        @Override
        public boolean isRemote() {
            return false;
        }
        
    }
    
    public boolean writeCharacteristicValue(String path, byte[] value){
        try {
            Characteristic c = bus.getRemoteObject(DBUS_BLUEZ, path,
                    Characteristic.class);
            
            c.SetProperty("Value", new Variant<byte []>(value));
            return true;
        } catch (DBusException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "error while writting characteristic", e);
        }
        return false;
    }
    
    public enum REGISTER_RET_VALUES {
        ERROR,
        ALL_READY_REGISTERED,
        REGISTERED
    };
    
    public REGISTER_RET_VALUES registerCharacteristicWatcher(String serPath) {
        if (Status() == false) {
            if (Start() == false) {
                return REGISTER_RET_VALUES.ERROR;
            }
        }
    
        if (mWatcher == null){
            Log.v(TAG, "Exporting characteristic watcher");
            mWatcher = new CharacteristicWatcher();
            try {
                bus.exportObject(Watcher.PATH, mWatcher);
            } catch (DBusException e) {
                Log.e(TAG, "Failed to export watcher", e);
                return REGISTER_RET_VALUES.ERROR;
            }
            Log.v(TAG, "Exported");
            
            mWatchedServices = new Vector<String>();
        }
        
        if (mWatchedServices.contains(serPath))
            return REGISTER_RET_VALUES.ALL_READY_REGISTERED;
        Service s = null;
        
        try {
            s = bus.getRemoteObject(DBUS_BLUEZ, serPath, Service.class);
        } catch (Exception e){
            Log.e(TAG, "failed getting service object", e);
            return REGISTER_RET_VALUES.ERROR;
        }
        s.RegisterCharacteristicsWatcher(new Path(Watcher.PATH));
        mWatchedServices.add(serPath);
        return REGISTER_RET_VALUES.REGISTERED;
    }
    
}
