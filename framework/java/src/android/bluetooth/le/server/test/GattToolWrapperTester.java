
package android.bluetooth.le.server.test;

import android.bluetooth.le.server.GattToolWrapper;
import android.bluetooth.le.server.GattToolWrapper.GattToolListener;
import android.bluetooth.le.server.GattToolWrapper.SHELL_ERRORS;

import com.broadcom.bt.le.api.BleGattID;

import java.io.IOException;
import java.util.Vector;

public class GattToolWrapperTester implements GattToolListener {

    @Override
    public synchronized void onNotification(int conn_handle, int handle, byte[] value) {
        System.out.print("notification " + conn_handle + " " + Integer.toHexString(handle) + " ");
        if (value != null)
            for (int i = 0; i < value.length; i++)
                System.out.println(GattToolWrapper.toSignedByteString(value[i]) + " ");
        System.out.println();
    }

    @Override
    public synchronized void onIndication(int conn_handle, int handle, byte[] value) {
        System.out.print("notification " + conn_handle + " " + Integer.toHexString(handle) + " ");
        if (value != null)
            for (int i = 0; i < value.length; i++)
                System.out.print(GattToolWrapper.toSignedByteString(value[i]) + " ");
        System.out.println();
    }

    @Override
    public synchronized void connected(int conn_handle, String addr, int status) {
        System.out.println("connected to " + addr + " with handle " + conn_handle
                + " result " + status + "\n");
        this.notifyAll();
    }

    @Override
    public synchronized void disconnected(int conn_handle, String addr) {
        System.out.println("disconnected " + conn_handle + ", " + addr);
        this.notifyAll();
    }

    @Override
    public void primaryAll(int conn_handle, int start, int end, BleGattID uuid) {
        System.out.println("primary: " + conn_handle 
                + ", start: " + IntegralToString.intToHexString(start, true, 4)
                + ", end: " + IntegralToString.intToHexString(end, true, 4) 
                + ", uuid: " + uuid);
    }

    @Override
    public synchronized void primaryAllEnd(int conn_handle, int status) {
        System.out.println("primary completed " + conn_handle + ", " + status);
        this.notifyAll();
    }

    @Override
    public void primaryUuid(int conn_handle, int start, int end) {
        System.out.println("primary-uuid: " + conn_handle 
                + ", start: " + IntegralToString.intToHexString(start, true, 4)
                + ", end: " + IntegralToString.intToHexString(end, true, 4));
    }

    @Override
    public synchronized void primaryUuidEnd(int conn_handle, int status) {
        System.out.println("primary-uuid completed " + conn_handle + ", " + status);
        this.notifyAll();
    }

    @Override
    public void characteristic(int conn_handle, int handle, short properties, int value_handle,
            BleGattID uuid) {
        System.out.println("characteristics: " + conn_handle +
                ", start: " + IntegralToString.intToHexString(handle, true, 4) +
                ", properties: " + IntegralToString.intToHexString(properties, true, 2) +
                ", value handle: " + IntegralToString.intToHexString(value_handle, true, 4) +
                ", uuid: " + uuid);
    }

    @Override
    public synchronized void characteristicEnd(int conn_handle, int status) {
        System.out.println("characteristic-end: " + conn_handle + ", " + status);
        this.notifyAll();
    }

    @Override
    public void characteristicDescriptor(int conn_handle, int handle, BleGattID uuid) {
        System.out.println("characteristic-descriptor: " + conn_handle +
                ", start: " + IntegralToString.intToHexString(handle, true, 4) +
                ", uuid: " + uuid);

    }

    @Override
    public synchronized void characteristicDescriptorEnd(int conn_handle, int status) {
        System.out.println("characteristic-descriptor-end: " + conn_handle + ", " + status);
        this.notifyAll();
    }

    @Override
    public synchronized void gotValueByHandle(int conn_handle, byte[] value, int status) {
        System.out.println("gotValueByHandle: " + conn_handle + ", " + status);
        if (status == 0 && value != null) {
            for (int i = 0; i < value.length; i++)
                System.out.print(GattToolWrapper.toSignedByteString(value[i]) + " ");
            System.out.println();
        }
        this.notifyAll();
    }

    @Override
    public void gotValueByUuid(int conn_handle, int handle, byte[] value) {
        System.out.print("gotValueByUuid: " + conn_handle 
                + ", handle: " + IntegralToString.intToHexString(handle, true, 4) 
                + ", value: ");
        if (value != null) {
            for (int i = 0; i < value.length; i++)
                System.out.print(GattToolWrapper.toSignedByteString(value[i]) + " ");
        }
        System.out.println();
    }

    @Override
    public synchronized void gotValueByUuidEnd(int conn_handle, int status) {
        System.out.println("gotValueByUuid-end: " + conn_handle + ", status: " + status);
        this.notifyAll();
    }

    @Override
    public void gotWriteResult(int conn_handle, int status) {
        System.out.println("gotWriteResult: " + conn_handle + ", status: " + status);
        this.notifyAll();
    }

    @Override
    public synchronized void gotSecurityLevelResult(int conn_handle, int status) {
        System.out.println("gotSecurityLevelResult: " + conn_handle + ", status: " + status);
        this.notifyAll();
    }

    @Override
    public synchronized void gotMtuResult(int conn_handle, int status) {
        System.out.println("gotMtuResult: " + conn_handle + ", status: " + status);
        this.notifyAll();
    }
    
    @Override
    public synchronized void gotPsmResult(int psm) {
        System.out.println("gotPsmResult: " + ", psm: " + psm);
        this.notifyAll();
    }

    @Override
    public synchronized void processExit(int retcode) {
        System.out.println("Process exited:  " + retcode);
        this.notifyAll();
    }

    @Override
    public synchronized void processStdinClosed() {
        System.out.println("Process stdin lost!!!");
        this.notifyAll();
    }

    @Override
    public void shellError(SHELL_ERRORS e) {
        if (e==SHELL_ERRORS.ADDRESS_CHANGED){
            System.out.println("ADDRESS CHANGED!!!");
            return;
        }
        
        System.out.println("Invalid shell error " +e);
    }

    public static void main(String [ ] args) throws IOException{
        GattToolWrapper.initWorkerPool(1);
        System.out.println("pool started");
        
        GattToolWrapper w = GattToolWrapper.getWorker();
        GattToolWrapperTester wt = new GattToolWrapperTester();
        w.setListener(wt);
        
        System.out.println("Got wrapper " + w);
        
        Vector<String> stack = new Vector<String>();
        synchronized (wt){
            for (String p : args) {
                if (p.equals("--") && stack.size() > 0) {
                    String command = stack.get(0);
                    if (command.equals("connect")) {
                        if (stack.size() > 2)
                            w.connect(stack.get(1), stack.get(2));
                        else
                            w.connect(stack.get(1));
                    } else if (command.equals("psm")) {
                        w.psm(Integer.parseInt(stack.get(1)));
                    } else if (command.equals("disconnect")){
                        w.disconnect();
                    } else if (command.equals("primary")){
                        if (stack.size()<2)
                            w.primaryDiscovery();
                        else
                            w.primaryDiscoveryByUUID(new BleGattID(Integer.parseInt(stack.get(1), 16)));
                    } else if (command.equals("characteristics")){
                        Integer s = null, e = null;
                        BleGattID u = null;
                        if (stack.size()>1)
                            s = Integer.parseInt(stack.get(1), 16);
                        if (stack.size()>2)
                            e = Integer.parseInt(stack.get(2), 16);
                        if (stack.size()>3)
                            u = new BleGattID(stack.get(3));
                        w.characteristicsDiscovery(s, e, u);
                    } else if (command.equals("char-desc")){
                        Integer a = null;
                        Integer b = null;
                        if (stack.size()>1)
                            a = Integer.parseInt(stack.get(1), 16);
                        if (stack.size()>2)
                            b = Integer.parseInt(stack.get(2), 16);
                        w.characteristicsDescriptorDiscovery(a, b);
                    } else if (command.equals("char-read-hnd")){
                        Integer a = null;
                        Integer b = null;
                        a = Integer.parseInt(stack.get(1), 16);
                        if (stack.size()>2)
                            b = Integer.parseInt(stack.get(2), 16);
                        w.readCharacteristicByHandle(a,b);
                    } else if (command.equals("char-read-uuid")){
                        String u = stack.get(1);
                        BleGattID uuid;
                        if (u.length() == 4) {
                            uuid = new BleGattID(Integer.parseInt(u, 16));
                        } else
                            uuid = new BleGattID(u);
                        Integer a = null;
                        Integer b = null;
                        if (stack.size() > 3)
                            a = Integer.parseInt(stack.get(2), 16);
                        if (stack.size()>2)
                            b = Integer.parseInt(stack.get(3), 16);
                        w.readCharacteristicByUUID(uuid, a, b);
                    } else if (command.equals("char-write-req")){
                        Integer a = Integer.parseInt(stack.get(1), 16);
                        byte[] val = new byte[stack.size()-2];
                        for (int i = 0; i < val.length; i++)
                            val[i] = Byte.parseByte(stack.get(i+2), 16);
                        w.writeCharReq(a, val);
                    } else if (command.equals("char-write-cmd")){
                        Integer a = Integer.parseInt(stack.get(1), 16);
                        byte[] val = new byte[stack.size()-2];
                        for (int i = 0; i < val.length; i++)
                            val[i] = Byte.parseByte(stack.get(i+2), 16);
                        w.writeCharCmd(a, val);
                    } else if (command.equals("seq-level")){
                        w.secLevel(stack.get(1));
                    } else if (command.equals("mtu")){
                        w.mtu(Integer.parseInt(stack.get(1)));
                    } else if (command.equals("exit")){
                        System.out.println("ending");
                        break;
                    } else {
                        System.out.println("Invalid command: " + command);
                        continue;
                    }
                    try {
                        wt.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stack.clear();
                    continue;
                }
                stack.add(p);
            }
        }
    }
}
