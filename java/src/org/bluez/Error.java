package org.bluez;

import org.freedesktop.dbus.exceptions.DBusExecutionException;

public interface Error {
    @SuppressWarnings("serial")
    public class DoesNotExist extends DBusExecutionException
    {
       public DoesNotExist(String message)
       {
          super(message);
       }
    }

    @SuppressWarnings("serial")
    public class InvalidArguments extends DBusExecutionException
    {
       public InvalidArguments(String message)
       {
          super(message);
       }
    }
    
    @SuppressWarnings("serial")
    public class NoSuchAdapter extends DBusExecutionException
    {
       public NoSuchAdapter(String message)
       {
          super(message);
       }
    }  
}
