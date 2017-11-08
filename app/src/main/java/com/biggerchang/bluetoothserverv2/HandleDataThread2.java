package com.biggerchang.bluetoothserverv2;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class HandleDataThread2 extends Thread {
    private static final String TAG = "HandleDataThread";
    private BluetoothSocket mSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean mIsClose = false;

    public HandleDataThread2(BluetoothSocket socket) {
        Log.d(TAG, "HandleDataThread: " + socket.getRemoteDevice().getName());
        mSocket = socket;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(mSocket.getInputStream()));
            oos = new ObjectOutputStream(mSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "run: receive ");
        receive();
    }

    private int index = 0;

    private void receive() {
        while (true) {
             if (!mIsClose) break;
            try {
                Object obj = ois.readUTF();
                doSomething(obj);
            } catch (IOException e) {
                e.printStackTrace();
                closeSocket();
            }
        }
    }

    private long preTime = 0;

    private void doSomething(Object obj) {
        if (preTime != 0) {
            Log.d(TAG, "receive: obj:" + obj + "  index:" + index++ + "   offset:" + (System.currentTimeMillis() - preTime) + "  isConnected:" + mSocket.isConnected());
        }
        preTime = System.currentTimeMillis();
    }

    public void send(String data) {
        try {
            oos.flush();
            oos.writeUTF(data);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        mIsClose = true;
        try {
            oos.close();
            ois.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
