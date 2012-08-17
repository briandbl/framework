/**
 * 
 */

package android.bluetooth.le.server;

import android.bluetooth.le.server.handlers.DeviceFoundHandler;
import android.util.Log;

import org.bluez.Adapter;
import org.bluez.Device;
import org.bluez.Error;
import org.bluez.Manager;
import org.freedesktop.DBus;
import org.freedesktop.DBus.NameOwnerChanged;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author manuel
 */
public class BlueZInterface {
    public interface Listener {
        public void deviceDiscovered(String address, String name, short rssi);
    }
    
    static String TAG = "BluetoothLEService";
    private DBusConnection bus = null;
    private Manager manager = null;
    private Adapter adapter = null;
    private Listener listener = null;
    @SuppressWarnings("rawtypes")
    
    private Map<Class, DBusSigHandler> listeners = new HashMap<Class, DBusSigHandler>();

    public synchronized Listener getListener(){
        return listener;
    }
    
    public synchronized void setListener(Listener l){
        this.listener = l;
    }
    
    public synchronized void removeListener(){
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
        bus=null;
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
            //DBusSigHandler s = new DBusOwnerNameChanged(this);
            //bus.addSigHandler(NameOwnerChanged.class, s);
            //listeners.put(NameOwnerChanged.class, s);
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
            out = bus.getRemoteObject("org.bluez", "/", Manager.class);
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
            out = bus.getRemoteObject("org.bluez", p, Adapter.class);
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

    public String getAdapterPath() {
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
    
    public synchronized boolean Start(){
        return Start(false);
    }

    public synchronized void Stop() {
        this.disconnectDBus();
    }

    public synchronized boolean Status() {
        return adapter != null;
    }

    private String createDevice(String address){
        try {
            return adapter.CreateDevice(address).toString();
        } catch (Error.Failed e1) {
            Log.e(TAG, "error", e1);
            return null;
        } catch (Error.InvalidArguments e2) {
            Log.e(TAG, "error", e2);
            throw new RuntimeException("Invalid address "+e2.toString());
        }
    }

    private String findDevice(String address) {
        try {
            return adapter.FindDevice(address).toString();
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
            return bus.getRemoteObject("org.bluez", path, Device.class);
        } catch (DBusException e) {
            Log.e(TAG, "error", e);
            throw new RuntimeException("Something failed " + e.toString());
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String, Variant> getDeviceProperties(String address){
        if (Status() == false) {
            if (Start() == false) {
                throw new RuntimeException("Failed to connect to BlueZ");
            }
        }
        
        Device d = getDevice(address);
        if (d==null)
            return null;
        
        try {
            return d.GetProperties();
        } catch (Error.DoesNotExist e1){
            Log.e(TAG, "error", e1);
            return null;
        } catch(Error.InvalidArguments e2){
            throw new RuntimeException("Invalid address " + e2.toString());
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getUUIDs(String address){
        ArrayList<String> out = new ArrayList<String>();
        Map<String, Variant> temp = getDeviceProperties(address);
        
        if (temp.containsKey("UUIDs")){
            List<String> t = (List<String>)temp.get("UUIDs").getValue();
            for(String k: t)
                out.add(k);
        }
        
        return out;
    }
}
