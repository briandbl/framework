/**
 * 
 */
package android.server.le;

import android.bluetooth.le.IBluetoothLE;
import android.os.RemoteException;

import org.bluez.Adapter;
import org.bluez.Manager;
import org.freedesktop.dbus.DBusConnection;

/**
 * @author manuel
 *
 */
public class Service extends IBluetoothLE.Stub{
    DBusConnection bus;
    Manager manager;
    Adapter adapter;
    
    @Override
    public String getAdapterPath() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
}
