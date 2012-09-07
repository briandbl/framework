package org.bluez;

import org.freedesktop.DBus.Method;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;

import java.util.List;
import java.util.Map;

/**
 * 
 * Interface for describing an org.bluez.Watcher watcher
 * 
 * @author Manuel Naranjo <naranjo.manuel@gmail.com>
 *
 */
public interface Watcher extends DBusInterface{

    @Method.NoReply()
    @SuppressWarnings("rawtypes")
    void ValueChanged(Path characteristic,  Map<String, Variant> values);
    
    @Method.NoReply()
    void RawValueChanged(Path characteristic, List<Byte> values);
    
    public static String PATH="/com/manuelnaranjo/le/watcher";
}
