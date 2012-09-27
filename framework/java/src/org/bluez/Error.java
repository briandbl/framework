/************************************************************************************
 *
 *  Copyright (C) 2012      Naranjo Manuel Francisco <naranjo.manuel@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/
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
