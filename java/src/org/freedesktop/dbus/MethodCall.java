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

import com.android.internal.util.HexDump;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.MessageFormatException;

import java.util.Vector;

public class MethodCall extends Message
{
    private static final String TAG = "DBus-DC";

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

    MethodCall() {
    }

    public MethodCall(String dest, String path, String iface, String member, byte flags,
            String sig, Object... args) throws DBusException
    {
        this(null, dest, path, iface, member, flags, sig, args);
    }

    public MethodCall(String source, String dest, String path, String iface, String member,
            byte flags, String sig, Object... args) throws DBusException
    {
        super(Message.Endian.BIG, Message.MessageType.METHOD_CALL, flags);

        if (null == member || null == path)
            throw new MessageFormatException(
                    _("Must specify destination, path and function name to MethodCalls."));
        headers.put(Message.HeaderField.PATH, path);
        headers.put(Message.HeaderField.MEMBER, member);

        Vector<Object> hargs = new Vector<Object>();

        hargs.add(new Object[] {
                Message.HeaderField.PATH, new Object[] {
                        ArgumentType.OBJECT_PATH_STRING, path
                }
        });

        if (null != source) {
            headers.put(Message.HeaderField.SENDER, source);
            hargs.add(new Object[] {
                    Message.HeaderField.SENDER, new Object[] {
                            ArgumentType.STRING_STRING, source
                    }
            });
        }

        if (null != dest) {
            headers.put(Message.HeaderField.DESTINATION, dest);
            hargs.add(new Object[] {
                    Message.HeaderField.DESTINATION, new Object[] {
                            ArgumentType.STRING_STRING, dest
                    }
            });
        }

        if (null != iface) {
            hargs.add(new Object[] {
                    Message.HeaderField.INTERFACE, new Object[] {
                            ArgumentType.STRING_STRING, iface
                    }
            });
            headers.put(Message.HeaderField.INTERFACE, iface);
        }

        hargs.add(new Object[] {
                Message.HeaderField.MEMBER, new Object[] {
                        ArgumentType.STRING_STRING, member
                }
        });

        if (null != sig) {

            debug(DEBUG, "Appending arguments with signature: " + sig);
            hargs.add(new Object[] {
                    Message.HeaderField.SIGNATURE, new Object[] {
                            ArgumentType.SIGNATURE_STRING, sig
                    }
            });
            headers.put(Message.HeaderField.SIGNATURE, sig);
            setArgs(args);
        }

        byte[] blen = new byte[4];
        appendBytes(blen);
        append("ua(yv)", serial, hargs.toArray());
        pad((byte) 8);

        long c = bytecounter;
        if (null != sig)
            append(sig, args);

        debug(DEBUG, "Appended body, type: " + sig + " start: " + c + " end: "
                + bytecounter + " size: " + (bytecounter - c));
        marshallint(bytecounter - c, blen, 0, 4);

        debug(DEBUG, "marshalled size (" + blen + "): " + HexDump.dumpHexString(blen));
    }

    private static long REPLY_WAIT_TIMEOUT = 20000;

    /**
     * Set the default timeout for method calls. Default is 20s.
     * 
     * @param timeout New timeout in ms.
     */
    public static void setDefaultTimeout(long timeout)
    {
        REPLY_WAIT_TIMEOUT = timeout;
    }

    Message reply = null;

    public synchronized boolean hasReply()
    {
        return null != reply;
    }

    /**
     * Block (if neccessary) for a reply.
     * 
     * @return The reply to this MethodCall, or null if a timeout happens.
     * @param timeout The length of time to block before timing out (ms).
     */
    public synchronized Message getReply(long timeout)
    {

        debug(VERBOSE, "Blocking on " + this);
        if (null != reply)
            return reply;
        try {
            wait(timeout);
            return reply;
        } catch (InterruptedException Ie) {
            return reply;
        }
    }

    /**
     * Block (if neccessary) for a reply. Default timeout is 20s, or can be
     * configured with setDefaultTimeout()
     * 
     * @return The reply to this MethodCall, or null if a timeout happens.
     */
    public synchronized Message getReply()
    {

        debug(VERBOSE, "Blocking on " + this);
        if (null != reply)
            return reply;
        try {
            wait(REPLY_WAIT_TIMEOUT);
            return reply;
        } catch (InterruptedException Ie) {
            return reply;
        }
    }

    protected synchronized void setReply(Message reply)
    {

        debug(VERBOSE, "Setting reply to " + this + " to " + reply);
        this.reply = reply;
        notifyAll();
    }

}
