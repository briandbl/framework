package android.bluetooth.le.server.gatttool;

import android.bluetooth.le.server.GattToolWrapper;
import android.bluetooth.le.server.GattToolWrapper.GattToolListener;
import android.util.Log;

import com.broadcom.bt.le.api.BleGattID;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Response implements Cloneable{
    protected static String TAG = GattToolWrapper.TAG;
    
    private static Map<String, Response> sCommands = new HashMap<String, Response>();
    
    protected String mCommand;
    protected boolean mChangeStatus;
    protected GattToolWrapper mWrapper;
    
    private Response(){
        Log.v(TAG, "initializing reponse listeners");
    }
    
    public Response(String c, boolean status){
        Log.v(TAG, "registering " + c);
        if (sCommands.containsKey(c))
            Log.e(TAG, "replacing listener " + c);
        
        sCommands.put(c, this);
        mCommand = c;
        mChangeStatus = status;
    }
    
    protected boolean doesStatusChange(){
        return mChangeStatus;
    }
    
    protected boolean processArguments(GattToolListener listener, int handle, String args){
        return false;
    }
    
    protected void callListener(){
        // will only get called on classes with doesStatusChange() == true
        Log.e(TAG, "callListener not implemented");
    }
    
    public static boolean processLine(GattToolWrapper w, GattToolListener listener, 
            String command, int handle, String argument){
        Log.v(TAG, "Processing command: " + command);
        
        if (!sCommands.containsKey(command)){
        	w.notifyAll();
        	return false;
        }
        
        argument = argument.trim();
        
        Log.v(TAG, "handle: " + handle + ", wrapper: " + w + ", " + argument);
        Response cmd;
        try {
            cmd = (Response)sCommands.get(command).clone();
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "error cloning command!!!", e);
            return false;
        }
        cmd.mWrapper = w;
        boolean ret = cmd.processArguments(listener, handle, argument);
        
        if (ret && cmd.doesStatusChange())
        {
            w.endCommand();
            cmd.callListener();
        }
        
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
                int conn_handle, int handle, byte[] val) {
            listener.onIndication(super.mWrapper, conn_handle, handle, val);
            return true;
        }
    }
    
    public class NotificationResponse extends EventResponse {
        public NotificationResponse() {
            super("NOTIFICATION");
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, 
                int conn_handle, int handle, byte[] val) {
            listener.onIndication(super.mWrapper, conn_handle, handle, val);
            return true;
        }
    }

    public class ConnectedResponse extends GenericEndResponse{
        private String mAddr;
        public ConnectedResponse() {
            super("CONNECTED", true);
        }
        
        @Override
        protected boolean processArguments(GattToolListener listener, int handle, 
                String args) {
            String[] pieces = args.trim().split("\\s");
            String addr = pieces[0];
            int status = Integer.parseInt(pieces[1]);
            
            Log.v(TAG, super.mCommand + " parsed " + handle +" -> " + addr+ ", " + status);
            
            mListener = listener;
            mConnHandle = handle;
            mValue = status;
            mAddr = addr;
            return true;
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int connHandle,
                int status) {
            listener.connected(super.mWrapper, mConnHandle, mAddr, status);
            return true;
        }
    }
    
    public class DisconnectedResponse extends Response{
        public DisconnectedResponse() {
            super("DISCONNECTED", true);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            listener.disconnected(super.mWrapper, conn_handle, args.trim());
            return true;
        }
    }
    
    public class PrimaryAllResponse extends Response{
        public PrimaryAllResponse() {
            super("PRIMARY-ALL", false);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
            if (parts.length < 3){
                Log.e(TAG, "PRIMARY-ALL incomplete response " + args);
                return false;
            }
            
            if (parts.length > 3){
                Log.w(TAG, "PRIMARY-ALL with extra arguments " + args + " " + parts.length);
                for (String p: parts)
                    Log.v(TAG, p);
            }
            
            int start, end;
            BleGattID uuid;
            start = Integer.parseInt(parts[0], 16);
            end = Integer.parseInt(parts[1], 16);
            uuid = new BleGattID(parts[2]);
            Log.v(TAG, "PRIMARY-ALL start " + start + ", end " + end + ", uuid " + uuid);
            listener.primaryAll(super.mWrapper, conn_handle, start, end, uuid);
            return true;
        }
    }
    
    public class PrimaryAllEndResponse extends GenericEndResponse{
        public PrimaryAllEndResponse() {
            super("PRIMARY-ALL-END", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.primaryAllEnd(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class PrimaryUuidResponse extends Response{
        public PrimaryUuidResponse() {
            super("PRIMARY-UUID", false);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
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
            listener.primaryUuid(super.mWrapper, conn_handle, start, end);
            return true;
        }
    }
    
    public class PrimaryUuidEndResponse extends GenericEndResponse{
        public PrimaryUuidEndResponse() {
            super("PRIMARY-UUID-END", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.primaryUuidEnd(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class CharResponse extends Response{
        public CharResponse() {
            super("CHAR", false);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
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
            properties = GattToolWrapper.parseSignedByte(parts[1]);
            value_handle = Integer.parseInt(parts[2], 16);
            uuid = new BleGattID(parts[3]);
            Log.v(TAG, "CHAR handle " + handle + ", properties " + properties + 
                    ", value_handle " + value_handle + ", uuid " + uuid);
            listener.characteristic(super.mWrapper, conn_handle, handle, properties, value_handle, uuid);
            return true;
        }
    }
    
    public class CharEndResponse extends GenericEndResponse{
        public CharEndResponse() {
            super("CHAR-END", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.characteristicEnd(super.mWrapper, conn_handle, status);
            return false;
        }
    }
    
    public class CharDescResponse extends Response{
        public CharDescResponse() {
            super("CHAR-DESC", false);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
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
            if (parts[1].length() > 4)
                uuid = new BleGattID(parts[1]);
            else
                uuid = new BleGattID(Integer.parseInt(parts[1], 16));
            Log.v(TAG, "CHAR-DESC handle " + handle + ", uuid " + uuid);
            listener.characteristicDescriptor(super.mWrapper, conn_handle, handle, uuid);
            return true;
        }
    }
    
    public class CharDescEndResponse extends GenericEndResponse{
        public CharDescEndResponse() {
            super("CHAR-DESC-END", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.characteristicDescriptorEnd(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class CharValDescResponse extends Response{
        private int mConnHandle;
        private byte[] mValue;
        private int mResult;
        private GattToolListener mListener;
        
        public CharValDescResponse() {
            super("CHAR-VAL-DESC", true);
        }
        
        @Override
        protected void callListener() {
            Log.v(TAG, "char-val-desc callListener");
            mListener.gotValueByHandle(super.mWrapper, mConnHandle, mValue, mResult);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
            if (parts.length < 1) {
                Log.e(TAG, "CHAR-VAL-DESC without status");
                return false;
            }
            
            if (parts.length < 2)
                Log.w(TAG, "CHAR-VAL-DESC with no value, weird");
            
            byte[] val = new byte[parts.length-1];
            
            for (int i = 1; i < parts.length ; i++)
                val[i-1] = GattToolWrapper.parseSignedByte(parts[i]);
            
            Log.v(TAG, "CHAR-VAL-DESC " + val);
            this.mConnHandle = conn_handle;
            this.mValue = val;
            this.mResult = Integer.parseInt(parts[0]);
            this.mListener = listener;
            return true;
        }
    }
    
    public class CharUuidResponse extends Response{
        public CharUuidResponse() {
            super("CHAR-READ-UUID", false);
        }

        @Override
        protected boolean processArguments(GattToolListener listener, int conn_handle, String args) {
            String[] parts = args.split("\\s+");
            if (parts.length < 1){
                Log.e(TAG, "CHAR-READ-UUID with no handle");
                return false;
            }
            if (parts.length < 2)
                Log.w(TAG, "CHAR-READ-UUID with no value, weird");
            
            int handle = Integer.parseInt(parts[0], 16);
            
            byte[] val = new byte[parts.length-1];
            
            for (int i = 0; i < val.length ; i++)
                val[i] = GattToolWrapper.parseSignedByte(parts[1+i]);
            
            Log.v(TAG, "CHAR-READ-UUID handle " + handle + ", value "+ val);
            listener.gotValueByUuid(super.mWrapper, conn_handle, handle, val);
            return true;
        }
    }
    
    public class CharUuidEndResponse extends GenericEndResponse{
        public CharUuidEndResponse() {
            super("CHAR-READ-UUID-END", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.gotValueByUuidEnd(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class CharWriteResponse extends GenericEndResponse{
        public CharWriteResponse() {
            super("CHAR-WRITE-CMD", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.gotWriteResult(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class CharWriteReqResponse extends GenericEndResponse{
        public CharWriteReqResponse() {
            super("CHAR-WRITE-REQ", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.gotWriteResultReq(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class SecLevelResponse extends GenericEndResponse{
        public SecLevelResponse() {
            super("SEC-LEVEL", true);
        }

        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.gotSecurityLevelResult(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class MtuResponse extends GenericEndResponse{
        public MtuResponse() {
            super("MTU", true);
        }
        
        @Override
        protected boolean internalProcessArguments(GattToolListener listener, int conn_handle,
                int status) {
            listener.gotMtuResult(super.mWrapper, conn_handle, status);
            return true;
        }
    }
    
    public class PsmResponse extends Response{
        int mValue;
        GattToolListener mListener;
        
        public PsmResponse() {
            super("PSM", true);
        }
           
        @Override
        protected void callListener() {
            Log.v(TAG, "psm calllistener");
            mListener.gotPsmResult(super.mWrapper, mValue);
        }

        protected boolean processArguments(GattToolListener listener, int conn_handle, 
                String args) {
            if (!args.matches("\\d*")) {
                Log.e(TAG, super.mCommand + " doesn't match " + args);
                return false;
            }
            int status = Integer.parseInt(args);
            mValue = status;
            mListener = listener;
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
        new CharWriteReqResponse();
        new SecLevelResponse();
        new MtuResponse();
        new PsmResponse();
    }
}

abstract class EventResponse extends Response {
    public EventResponse(String s){
        super(s, false);
    }
    
    @Override
    protected boolean processArguments(GattToolListener listener, int conn_handle, 
            String args) {
        String[] t = args.split("\\s+");
        if (t.length < 1){
            Log.e(TAG, super.mCommand + " with missing arguments " + args);
            return false;
        }
        int handler = Integer.parseInt(t[0], 16);
        int l = t.length-1;
        byte[] value = new byte[l];
        for (int i = 1; i < t.length; i++)
            value[i-1] = GattToolWrapper.parseSignedByte(t[i]);
        return this.internalProcessArguments(listener, conn_handle, handler, value);
    }
    
    protected abstract boolean internalProcessArguments(GattToolListener listener,
            int conn_handle, int handler, byte[] val);
}

abstract class GenericEndResponse extends Response {
    protected static final Pattern RESULT = Pattern.compile("\\s*(\\d*)\\s*(.*)"); 
    //result, result str
    
    protected int mConnHandle;
    protected int mValue;
    protected GattToolListener mListener;
    
    public GenericEndResponse(String s, boolean status){
        super(s, status);
    }
    
    @Override
    protected void callListener(){
        Log.v(TAG, mCommand + " callListener ");
        this.internalProcessArguments(mListener, mConnHandle, mValue);
    }
    
    @Override
    protected boolean processArguments(GattToolListener listener, int handle, 
            String args) {
        Matcher m = RESULT.matcher(args);
        
        if (m==null || m.find()==false){
            Log.e(TAG, super.mCommand + " doesn't match " + args);
            return false;
        }
        
        int status = Integer.parseInt(m.group(1));
        
        Log.v(TAG, super.mCommand + " parsed " + handle + ", " + status);
        
        if (status!=0 && m.groupCount() > 1){
            Log.e(TAG, mCommand + " error " + m.group(2));
        }
        
        mListener = listener;
        mConnHandle = handle;
        mValue = status;
        return true;
    }
    
    protected abstract boolean internalProcessArguments(GattToolListener listener,
            int conn_handle, int status);
}
