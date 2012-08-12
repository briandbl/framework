/**
 * 
 */

package android.bluetooth.le.server;

import android.bluetooth.le.IBluetoothLE;
import android.bluetooth.le.IBluetoothLE.Stub;
import android.os.RemoteException;
import android.util.Log;

import org.bluez.Adapter;
import org.bluez.Manager;
import org.freedesktop.DBus;
import org.freedesktop.DBus.NameOwnerChanged;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author manuel
 */
public class Service extends IBluetoothLE.Stub {
    private static String TAG = "BluetoothLEService";
    private DBusConnection bus = null;
    private Manager manager = null;
    private Adapter adapter = null;
    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = new HashMap<Class, DBusSigHandler>();

    class DBusOwnerNameChanged implements DBusSigHandler<NameOwnerChanged> {

        @Override
        public void handle(NameOwnerChanged s) {
            if (!s.name.equals("org.bluez"))
                return;

            Log.i(TAG, "BlueZ name owner changed, old " + s.old_owner +
                    ", new " + s.new_owner);
            
            if (s.old_owner==null || s.old_owner.trim().length()>0){
                if (s.new_owner!=null && s.new_owner.trim().length()>0){
                    Log.i(TAG, "BlueZ is up now, getting manager");
                    Service.this.Start();
                }
                return;
            }
            
            if (s.new_owner==null || s.new_owner.trim().length()>0){
                Log.i(TAG, "BlueZ is down, closing connections");
                Service.this.resetBlueZConnection();
            }
        }

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
                    (Map.Entry<Class, DBusSigHandler>)it.next();
            
            try {
                bus.removeSigHandler(pairs.getKey(), pairs.getValue());
            } catch (DBusException e) {
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        listeners.clear();
        bus.disconnect();
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
            DBusSigHandler s = new DBusOwnerNameChanged();
            bus.addSigHandler(NameOwnerChanged.class, s);
            listeners.put(NameOwnerChanged.class, s);
            Log.i(TAG, "dbus handlers registered");
            return;
        } catch (DBusException e) {
            Log.e(TAG, "Failed to get on bus", e);
        }
        Stop();
    }
    
    private synchronized Manager getBluezManager(){
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
    
    private synchronized void resetBlueZConnection(){
        this.adapter = null;
        this.manager = null;
    }
    
    private synchronized Adapter getBluezAdapter(){
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
    @Override
    public String getAdapterPath() throws RemoteException {
        if (manager == null)
            return null;
        
        return manager.DefaultAdapter().toString();
    }
    
    public synchronized boolean Start(){
        connectDBus();       
        manager = getBluezManager();
        adapter = getBluezAdapter();
        
        return adapter!=null;
    }
    
    public synchronized void Stop(){
        this.disconnectDBus();
    }
    
    public synchronized boolean Status(){
        return adapter!=null;
    }
}
