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

class MethodTuple
{
    private static final String TAG = "DBus-MT";

    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int ASSERT = Log.ASSERT;
    private static int LEVEL = INFO;

    @SuppressWarnings("unused")
    private static void debug(Throwable o) {
        Log.e(TAG, "error", o);
    }

    @SuppressWarnings("unused")
    private static void debug(int l, Object o) {
        if (l >= LEVEL)
            if (o != null)
                Log.println(l, TAG, o.toString());
            else
                Log.println(l, TAG, "NULL");
    }

    @SuppressWarnings("unused")
    private static void debug(Object o) {
        debug(DEBUG, o);
    }

    String name;
    String sig;

    public MethodTuple(String name, String sig)
    {
        this.name = name;
        if (null != sig)
            this.sig = sig;
        else
            this.sig = "";

        debug(VERBOSE, "new MethodTuple(" + this.name + ", " + this.sig + ")");
    }

    public boolean equals(Object o)
    {
        return o.getClass().equals(MethodTuple.class)
                && ((MethodTuple) o).name.equals(this.name)
                && ((MethodTuple) o).sig.equals(this.sig);
    }

    public int hashCode()
    {
        return name.hashCode() + sig.hashCode();
    }
}
