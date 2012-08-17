
package android.server.le.test;

import org.bluez.Adapter;
import org.bluez.Characteristic;
import org.bluez.Manager;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestCharacteristic{
    private DBusConnection dbus;
    private Manager manager;
    private Adapter adapter;
    
    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = 
            new HashMap<Class, DBusSigHandler>();
    
    public TestCharacteristic(){
    }
    
    
    @SuppressWarnings("rawtypes")
    public void ConnectDBus() throws DBusException{
        info("getting on bus");
        dbus = DBusConnection.getConnection(DBusConnection.SYSTEM);
        info("got bus");

        info("registering signals");
        
        //ManagerPropertyChangedHandler mpch = new ManagerPropertyChangedHandler();
        info ("dbus handlers registered");
    }
    
    public Characteristic getCharacteristic(String p) throws DBusException{
        info("Getting " + p);
        Characteristic out = dbus.getRemoteObject("org.bluez", 
                p, Characteristic.class);
        info("Got it :D");
        return out;
    }
    
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public void DisconnectDBus() throws DBusException{
        
        Iterator<Map.Entry<Class, DBusSigHandler>> it = 
                listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class, DBusSigHandler> pairs = 
                    (Map.Entry<Class, DBusSigHandler>)it.next();
            
            dbus.removeSigHandler(pairs.getKey(), pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        listeners.clear();
        
        adapter = null;
        manager = null;
        dbus.disconnect();
        dbus = null;
    }
    
    public void ConnectToBlueZ() throws DBusException{
        manager = dbus.getRemoteObject("org.bluez", "/", Manager.class);
        info("got manager");
        info("adapter path is " + manager.DefaultAdapter().toString());
        adapter = dbus.getRemoteObject("org.bluez",
                manager.DefaultAdapter().toString(),
                Adapter.class);    
    }
    
    static void info(String s) {
        System.out.println(s);
    }

    static void error(String s, Throwable t) {
        System.err.println(s);
        t.printStackTrace();
    }
    
    void DumpCharacteristics(Characteristic c) {
        Map<String, Variant> p = c.GetProperties();

        for (String k : p.keySet())
            info(k + ":\t" + p.get(k).toString());
    }

    @SuppressWarnings("rawtypes")
    public void process(String path) throws Exception {
        Characteristic c = getCharacteristic(path);
        DumpCharacteristics(c);
    }

    public static void main(String[] args)
    {
        info("Starting test");
        
        TestCharacteristic test = new TestCharacteristic();
        try {
            test.ConnectDBus();
            test.ConnectToBlueZ();
            for (String addr: args)
                try {
                    info("proccessing " + addr);
                    test.process(addr);
                } catch (Exception e) {
                    error("Something failed", e);
                }
            Thread.sleep(100000);
            test.DisconnectDBus();
        } catch (Exception e){
            error("Something failed", e);
        }
    }
}
