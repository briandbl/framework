/*
   D-Bus Java Implementation
   Copyright (c) 2005-2006 Matthew Johnson

   This program is free software; you can redistribute it and/or modify it
   under the terms of either the GNU Lesser General Public License Version 2 or the
   Academic Free Licence Version 2.1.

   Full licence texts are included in the COPYING file with this program.
 */

package org.freedesktop.dbus;

import static org.freedesktop.dbus.Gettext._;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.freedesktop.DBus.Error.NoReply;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

/**
 * A handle to an asynchronous method call.
 */
public class DBusAsyncReply<ReturnType>
{
    private static final String TAG="DBus-AsyncReply";
    
    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int ASSERT = Log.ASSERT;
    private static int LEVEL=INFO;
    
    @SuppressWarnings("unused")
    private static void debug(Throwable o){
        Log.e(TAG, "error", o);
    } 
    
    @SuppressWarnings("unused")
    private static void debug(int l, Object o){
        if (l>=LEVEL)
            if (o != null)
                Log.println(l, TAG, o.toString());
            else
                Log.println(l, TAG, "NULL");
    }  
    
    /**
     * Check if any of a set of asynchronous calls have had a reply.
     * 
     * @param replies A Collection of handles to replies to check.
     * @return A Collection only containing those calls which have had replies.
     */
    public static Collection<DBusAsyncReply<? extends Object>> hasReply(
            Collection<DBusAsyncReply<? extends Object>> replies)
    {
        Collection<DBusAsyncReply<? extends Object>> c = new ArrayList<DBusAsyncReply<? extends Object>>(
                replies);
        Iterator<DBusAsyncReply<? extends Object>> i = c.iterator();
        while (i.hasNext())
            if (!i.next().hasReply())
                i.remove();
        return c;
    }

    private ReturnType rval = null;
    private DBusExecutionException error = null;
    private MethodCall mc;
    private Method me;
    private AbstractConnection conn;

    DBusAsyncReply(MethodCall mc, Method me, AbstractConnection conn)
    {
        this.mc = mc;
        this.me = me;
        this.conn = conn;
    }

    @SuppressWarnings("unchecked")
    private synchronized void checkReply()
    {
        if (mc.hasReply()) {
            Message m = mc.getReply();
            if (m instanceof Error)
                error = ((Error) m).getException();
            else if (m instanceof MethodReturn) {
                try {
                    rval = (ReturnType) RemoteInvocationHandler.convertRV(m.getSig(),
                            m.getParameters(), me, conn);
                } catch (DBusExecutionException DBEe) {
                    debug(DBEe);
                    error = DBEe;
                } catch (DBusException DBe) {
                    debug(DBe);
                    error = new DBusExecutionException(DBe.getMessage());
                }
            }
        }
    }

    /**
     * Check if we've had a reply.
     * 
     * @return True if we have a reply
     */
    public boolean hasReply()
    {
        if (null != rval || null != error)
            return true;
        checkReply();
        return null != rval || null != error;
    }

    /**
     * Get the reply.
     * 
     * @return The return value from the method.
     * @throws DBusExecutionException if the reply to the method was an error.
     * @throws NoReply if the method hasn't had a reply yet
     */
    public ReturnType getReply() throws DBusExecutionException
    {
        if (null != rval)
            return rval;
        else if (null != error)
            throw error;
        checkReply();
        if (null != rval)
            return rval;
        else if (null != error)
            throw error;
        else
            throw new NoReply(_("Async call has not had a reply"));
    }

    public String toString()
    {
        return _("Waiting for: ") + mc;
    }

    Method getMethod() {
        return me;
    }

    AbstractConnection getConnection() {
        return conn;
    }

    MethodCall getCall() {
        return mc;
    }
}
