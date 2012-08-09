package android.server.le.dbus;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Variant;

import java.util.Map;

public interface IManager extends DBusInterface{

    @SuppressWarnings("rawtypes")
    public Map<String, Variant> GetProperties() throws 
            Errors.DoesNotExist, Errors.InvalidArguments;
}
