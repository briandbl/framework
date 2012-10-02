package android.bluetooth.le.server.gatttool;

import android.bluetooth.le.server.GattToolWrapper;
import android.bluetooth.le.server.GattToolWrapper.GattToolListener;
import android.util.Log;

import com.broadcom.bt.le.api.BleGattID;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response {
    protected static String TAG = GattToolWrapper.TAG;
    
    private static Map<String, Response> sCommands = new HashMap<String, Response>();
    
    protected String mCommand;
    
    private Response(){
        Log.v(TAG, "initializing reponse listeners");
    }
    
    public Response(String c){
        Log.v(TAG, "registering " + c);
        if (sCommands.containsKey(c))
            Log.e(TAG, "replacing listener " + c);
        
        sCommands.put(c, this);
        mCommand = c;
    }
    
    protected boolean processArguments(GattToolListener listener, String addr, String args){
        return false;
    }
    
    public static boolean processLine(GattToolListener listener, String command, 
            String address, String argument){
        boolean ret = true;
        
        Log.v(TAG, "Processing command: " + command);
        
        if (!sCommands.containsKey(command))
            return false;
        
        argument = argument.trim();
        
        Log.v(TAG, "addr: " + address + ", " + argument);
        Response cmd = sCommands.get(command);
        cmd.processArguments(listener, address, argument);
        return ret;
    }
    
    static {
        new Response().initResponses();
    }
        
    public class IndicationResponse extends EventResponse {
        public IndicationResponse() {
            super("INDICATION");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, 
                String address, int handle, byte[] val) {
            listener.onIndication(address, handle, val);
            return true;
        }
    }
    
    public class NotificationResponse extends EventResponse {
        public NotificationResponse() {
            super("NOTIFICATION");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, 
                String address, int handle, byte[] val) {
            listener.onIndication(address, handle, val);
            return true;
        }
    }

    public class ConnectedResponse extends GenericEndResponse{
        public ConnectedResponse() {
            super("CONNECTED");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.connected(address, status);
            return true;
        }
    }
    
    public class DisconnectedResponse extends Response{
        public DisconnectedResponse() {
            super("DISCONNECTED");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            listener.disconnected(addr);
            return true;
        }
    }
    
    public class PrimaryAllResponse extends Response{
        public PrimaryAllResponse() {
            super("PRIMARY-ALL");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 3){
                Log.e(TAG, "PRIMARY-ALL incomplete response " + args);
                return false;
            }
            
            if (parts.length > 3){
                Log.w(TAG, "PRIMARY-ALL with extra arguments " + args);
            }
            
            int start, end;
            BleGattID uuid;
            start = Integer.parseInt(parts[0], 16);
            end = Integer.parseInt(parts[1], 16);
            uuid = new BleGattID(parts[2]);
            Log.v(TAG, "PRIMARY-ALL start " + start + ", end " + end + ", uuid " + uuid);
            listener.primaryAll(addr, start, end, uuid);
            return true;
        }
    }
    
    public class PrimaryAllEndResponse extends GenericEndResponse{
        public PrimaryAllEndResponse() {
            super("PRIMARY-ALL-END");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.primaryAllEnd(address, status);
            return true;
        }
    }
    
    public class PrimaryUuidResponse extends Response{
        public PrimaryUuidResponse() {
            super("PRIMARY-UUID");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 2){
                Log.e(TAG, "PRIMARY-UUID incomplete response " + args);
                return false;
            }
            
            if (parts.length > 2){
                Log.w(TAG, "PRIMARY-UUID with extra arguments " + args);
            }
            
            int start, end;
            start = Integer.parseInt(parts[0], 16);
            end = Integer.parseInt(parts[1], 16);
            Log.v(TAG, "PRIMARY-UUID start " + start + ", end " + end);
            listener.primaryUuid(addr, start, end);
            return true;
        }
    }
    
    public class PrimaryUuidEndResponse extends GenericEndResponse{
        public PrimaryUuidEndResponse() {
            super("PRIMARY-UUID-END");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.primaryUuidEnd(address, status);
            return true;
        }
    }
    
    public class CharResponse extends Response{
        public CharResponse() {
            super("CHAR");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 4){
                Log.e(TAG, "CHAR incomplete response " + args);
                return false;
            }
            
            if (parts.length > 4){
                Log.w(TAG, "CHAR with extra arguments " + args);
            }
            
            int handle, value_handle;
            byte properties;
            BleGattID uuid;
            handle = Integer.parseInt(parts[0], 16);
            properties = Byte.parseByte(parts[1], 16);
            value_handle = Integer.parseInt(parts[2], 16);
            uuid = new BleGattID(parts[3]);
            Log.v(TAG, "CHAR handle " + handle + ", properties " + properties + 
                    ", value_handle " + value_handle + ", uuid " + uuid);
            listener.characteristic(addr, handle, properties, value_handle, uuid);
            return true;
        }
    }
    
    public class CharEndResponse extends GenericEndResponse{
        public CharEndResponse() {
            super("CHAR-END");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.characteristicEnd(address, status);
            return false;
        }
    }
    
    public class CharDescResponse extends Response{
        public CharDescResponse() {
            super("CHAR-DESC");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 2){
                Log.e(TAG, "CHAR-DESC with incomplete response " + args);
                return false;
            }
            
            if (parts.length > 2){
                Log.w(TAG, "CHAR-DESC with extra arguments " + args);
            }
            
            int handle;
            BleGattID uuid;
            handle = Integer.parseInt(parts[0], 16);
            uuid = new BleGattID(parts[1]);
            Log.v(TAG, "CHAR-DESC handle " + handle + ", uuid " + uuid);
            listener.characteristicDescriptor(addr, handle, uuid);
            return true;
        }
    }
    
    public class CharDescEndResponse extends GenericEndResponse{
        public CharDescEndResponse() {
            super("CHAR-DESC-END");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.characteristicDescriptorEnd(address, status);
            return true;
        }
    }
    
    public class CharValDescResponse extends Response{
        public CharValDescResponse() {
            super("CHAR-VAL-DESC");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 1) {
                Log.e(TAG, "CHAR-VAL-DESC without status");
                return false;
            }
            
            if (parts.length < 2)
                Log.w(TAG, "CHAR-VAL-DESC with no value, weird");
            
            byte[] val = new byte[parts.length-1];
            
            for (int i = 1; i < val.length ; i++)
                val[i-1] = Byte.parseByte(parts[i], 16);
            
            Log.v(TAG, "CHAR-VAL-DESC " + val);
            listener.gotValueByHandle(addr, val, Integer.parseInt(parts[0]));
            return true;
        }
    }
    
    public class CharUuidResponse extends Response{
        public CharUuidResponse() {
            super("CHAR-READ-UUID");
        }

        @Override
        protected boolean processArguments(GattToolListener listener, String addr, String args) {
            String[] parts = args.split("\\s*");
            if (parts.length < 1){
                Log.e(TAG, "CHAR-READ-UUID with no handle");
                return false;
            }
            if (parts.length < 2)
                Log.w(TAG, "CHAR-READ-UUID with no value, weird");
            
            BleGattID handle = new BleGattID(Integer.parseInt(parts[0], 16));
            
            byte[] val = new byte[parts.length-1];
            
            for (int i = 0; i < val.length ; i++)
                val[i] = Byte.parseByte(parts[1+i], 16);
            
            Log.v(TAG, "CHAR-READ-UUID handle " + handle + ", value "+ val);
            listener.gotValueByUuid(addr, handle, val);
            return true;
        }
    }
    
    public class CharUuidEndResponse extends GenericEndResponse{
        public CharUuidEndResponse() {
            super("CHAR-READ-UUID-END");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.gotValueByUuidEnd(address, status);
            return true;
        }
    }
    
    public class CharWriteResponse extends GenericEndResponse{
        public CharWriteResponse() {
            super("CHAR-WRITE");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.gotWriteResult(address, status);
            return true;
        }
    }
    
    public class SecLevelResponse extends GenericEndResponse{
        public SecLevelResponse() {
            super("SEC-LEVEL");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.gotSecurityLevelResult(address, status);
            return true;
        }
    }
    
    public class MtuResponse extends GenericEndResponse{
        public MtuResponse() {
            super("MTU");
        }
        
        @Override
        protected boolean internalProcessArguments(GattToolListener listener, String address,
                int status) {
            listener.gotMtuResult(address, status);
            return true;
        }
    }
    
    
    private void initResponses(){
        new IndicationResponse();
        new NotificationResponse();
        new ConnectedResponse();
        new DisconnectedResponse();
        new PrimaryAllResponse();
        new PrimaryAllEndResponse();
        new PrimaryUuidResponse();
        new PrimaryUuidEndResponse();
        new CharResponse();
        new CharEndResponse();
        new CharDescResponse();
        new CharDescEndResponse();
        new CharValDescResponse();
        new CharUuidResponse();
        new CharUuidEndResponse();
        new CharWriteResponse();
        new SecLevelResponse();
        new MtuResponse();
    }
}

abstract class EventResponse extends Response {
    public EventResponse(String s){
        super(s);
    }
    
    @Override
    protected boolean processArguments(GattToolListener listener, String addr, 
            String args) {
        String[] t = args.split("\\s*");
        if (t.length < 1){
            Log.e(TAG, super.mCommand + " with missing arguments " + args);
            return false;
        }
        int handler = Integer.parseInt(t[0], 16);
        int l = t.length-1;
        byte[] value = new byte[l];
        for (int i = 1; i < t.length; i++)
            value[i-1] = Byte.valueOf(t[i], 16);
        return this.internalProcessArguments(listener, addr, handler, value);
    }
    
    protected abstract boolean internalProcessArguments(GattToolListener listener,
            String address, int handler, byte[] val);
}

abstract class GenericEndResponse extends Response {
    private static final Pattern RESULT = Pattern.compile("\\s*(\\d*)\\s*(.*)"); 
    //result, result str
    
    public GenericEndResponse(String s){
        super(s);
    }
    
    @Override
    protected boolean processArguments(GattToolListener listener, String addr, 
            String args) {
        Matcher m = RESULT.matcher(args);
        
        if (m==null){
            Log.e(TAG, super.mCommand + " doesn't match " + args);
            return false;
        }
        
        int status = Integer.parseInt(m.group(1));
        if (status!=0 && m.groupCount() > 1){
            Log.e(TAG, mCommand + " error " + m.group(2));
        }
        return this.internalProcessArguments(listener, addr, status);
    }
    
    protected abstract boolean internalProcessArguments(GattToolListener listener,
            String address, int status);
}
