/*
   D-Bus Java Implementation
   Copyright (c) 2005-2006 Matthew Johnson

   This program is free software; you can redistribute it and/or modify it
   under the terms of either the GNU Lesser General Public License Version 2 or the
   Academic Free Licence Version 2.1.

   Full licence texts are included in the COPYING file with this program.
 */

package org.freedesktop.dbus;

import android.util.Log;

import cx.ath.matthew.utils.Hexdump;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MessageWriter
{
    private static final String TAG="DBus-MW";
    
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
    
    @SuppressWarnings("unused")
    private static void debug(Object o){
        debug(DEBUG, o);
    }
    private OutputStream out;
    private boolean isunix;

    public MessageWriter(OutputStream out)
    {
        this.out = out;
        this.isunix = true;
        if (!this.isunix)
            this.out = new BufferedOutputStream(this.out);
    }

    public void writeMessage(Message m) throws IOException
    {
        debug(INFO, "<= " + m);
        if (null == m)
            return;
        if (null == m.getWireData()) {
            debug(WARN, "Message " + m + " wire-data was null!");
            return;
        }
        for (byte[] buf : m.getWireData()) {
            debug(VERBOSE,
                        "(" + buf + "):" + (null == buf ? "" : Hexdump.format(buf)));
            if (null == buf)
                break;
            out.write(buf);
        }
        out.flush();
    }

    public void close() throws IOException
    {
        debug(INFO, "Closing Message Writer");
        out.close();
    }
}
