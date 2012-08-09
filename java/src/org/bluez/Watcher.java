package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;

/**
 * 
 * Interface for describing an org.bluez.Watcher watcher
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 *
 */
public interface Watcher extends DBusInterface{

    /**
     * New raw value of the Characteristic Value attribute
     */
    public class ValueChanged extends DBusSignal {
        Path characteristic;
        List<Byte> value;
           
        public ValueChanged(String path, Path c, List<Byte>v) 
                throws DBusException{
            super(path, c, v);
            this.characteristic = c;
            this.value = v;
        }
    }
    
    public static String PATH="android.bluetooth.le.watcher";
}
