
package android.server.le.test;

import org.bluez.Adapter;
import org.bluez.Characteristic;
import org.bluez.Manager;
import org.bluez.Service;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestService{
    private DBusConnection dbus;
    private Manager manager;
    private Adapter adapter;
    
    @SuppressWarnings("rawtypes")
    private Map<Class, DBusSigHandler> listeners = 
            new HashMap<Class, DBusSigHandler>();
    
    public TestService(){
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
    
    public Service getService(String p) throws DBusException{
        info("Getting " + p);
        Service out = dbus.getRemoteObject("org.bluez", 
                p, Service.class);
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
    
    void DumpService(Service s) {
        Map<String, Variant> p = s.GetProperties();

        for (String k : p.keySet())
            info(k + ":\t" + p.get(k).toString());
        
        List<Path> cs = s.DiscoverCharacteristics();
        
        for (Path pa: cs)
            info("characteristics: " + pa.getPath());
    }

    @SuppressWarnings("rawtypes")
    public void process(String path) throws Exception {
        Service c = getService(path);
        DumpService(c);
    }

    public static void main(String[] args)
    {
        info("Starting test");
        
        TestService test = new TestService();
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
