
package android.bluetooth.le.server.handlers;

import android.bluetooth.le.server.BlueZInterface;
import android.util.Log;

import org.freedesktop.DBus.NameOwnerChanged;
import org.freedesktop.dbus.DBusSigHandler;

public class DBusOwnerNameChanged implements DBusSigHandler<NameOwnerChanged> {
    private static final String TAG = "DB-NameChanged";
    private static final boolean debug = true;

    private final BlueZInterface mInterface;

    public DBusOwnerNameChanged(BlueZInterface blueZInterface) {
        mInterface = blueZInterface;
    }

    @Override
    public void handle(NameOwnerChanged s) {
        if (!s.name.equals("org.bluez"))
            return;

        if (debug)
            Log.i(TAG, "BlueZ name owner changed, old " + s.old_owner +
                    ", new " + s.new_owner);

        if (s.old_owner == null || s.old_owner.trim().length() > 0) {
            if (s.new_owner != null && s.new_owner.trim().length() > 0) {
                if (debug)
                    Log.i(TAG, "BlueZ is up now, getting manager");
                new Thread() {
                    public void run(){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        mInterface.Start();
                        
                    }
                };
            }
            return;
        }

        if (s.new_owner == null || s.new_owner.trim().length() > 0) {
            if (debug)
                Log.i(TAG, "BlueZ is down, closing connections");
            mInterface.resetBlueZConnection();
        }
    }
}
