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
    
    @SuppressWarnings("serial")
    public class Rejected extends DBusExecutionException
    {
        public Rejected(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class NotReady extends DBusExecutionException
    {
        public NotReady(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class Failed extends DBusExecutionException
    {
        public Failed(String msg)
        {
            super (msg);
        }
    }

    @SuppressWarnings("serial")
    public class NotAuthorized extends DBusExecutionException
    {
        public NotAuthorized(String msg)
        {
            super (msg);
        }
    }

    @SuppressWarnings("serial")
    public class OutOfMemory extends DBusExecutionException
    {
        public OutOfMemory(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class AlreadyExists extends DBusExecutionException
    {
        public AlreadyExists(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class NotInProgress extends DBusExecutionException
    {
        public NotInProgress(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class InProgress extends DBusExecutionException
    {
        public InProgress(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class NotConnected extends DBusExecutionException
    {
        public NotConnected(String msg)
        {
            super (msg);
        }
    }
    
    @SuppressWarnings("serial")
    public class NotSupported extends DBusExecutionException
    {
        public NotSupported(String msg)
        {
            super (msg);
        }
    }
}
