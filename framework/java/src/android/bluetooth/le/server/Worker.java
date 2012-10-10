package android.bluetooth.le.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

class Worker extends Thread {
    public interface Handler {
        void EOF(int exitValue);
        void lineReceived(String t);
    }

    private static final String TAG="GATT-Worker";
    
    private Process mProcess;
    private DataInputStream mInput;
    private DataOutputStream mOutput;
    private BufferedReader in;
    private Handler mHandler;
    private boolean running;
    private boolean ended = false;

    public Worker(Handler h, String...command) throws IOException {
        Log.v(TAG, "creating new worker for " + command);
        mProcess = new ProcessBuilder(command).redirectErrorStream(true)
                .start();
        mInput = new DataInputStream(mProcess.getInputStream());
        mOutput = new DataOutputStream(mProcess.getOutputStream());
        in = new BufferedReader(new InputStreamReader(mInput));
        mHandler = h;
        running = true;
        this.start();
    }

    public void run() {
        try {
            Log.v(TAG, "starting worker");

            while (running) {
                String line = in.readLine();
                if (line == null) {
                    Log.v(TAG, "EOF");
                    int ret = 0xffff;
                    try{
                        ret = mProcess.exitValue();
                    } catch (Exception e){
                        Log.e(TAG, "no ret value", e);
                    }
                    mHandler.EOF(ret);
                    break;
                }
                if ("".equals(line.trim())) {
                    Log.v(TAG, "empty line");
                    continue;
                }
                if (running == false) // mnaranjo: I'm not sure if closing the process will trigger EOF.
                    break;
                Log.v(TAG, "got line: " + line);
                mHandler.lineReceived(line);
            }
        } catch (Exception e) {
            Log.e(TAG, "something failed", e);
        }
        
        Log.v(TAG, "worker ending");
        synchronized (this){
            this.notifyAll();
        }
        ended = true;
        Log.v(TAG, "worker ended");
    }

    public void quit() {
        this.running = false;
        if (ended && mProcess==null)
            return;
        
        Log.v(TAG, "destroying process");
        this.mProcess.destroy();
        try {
            mInput.close();
        } catch (IOException e) {
            Log.e(TAG, "failed to close mInput", e);
        }
        
        Log.v(TAG, "closing input");
        try {
            mOutput.close();
        } catch (IOException e) {
            Log.e(TAG, "failed to close mOutput", e);
        }
        
        synchronized (this){
            while (!ended){
                Log.v(TAG, "waiting for me now");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "interrupted while waiting thread to exit", e);
                }
            }
        }
        mProcess = null;
        mInput = null;
        mOutput = null;
        Log.v(TAG, "quit completed");
    }
    
    public DataOutputStream getOutputStream(){
        return this.mOutput;
    }
}

