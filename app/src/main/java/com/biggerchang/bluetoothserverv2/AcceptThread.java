package com.biggerchang.bluetoothserverv2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {

    private BluetoothServerSocket mmServerSocket = null;
    private static final String TAG = "AcceptThread";

    public AcceptThread(BluetoothAdapter adapter) {
        try {
            //name:名字随意，  UUID，类似于Ip地址的端口号，服务端和客户端的“端口”要保持一致，才能连接成功（UUID是任意的）
            mmServerSocket = adapter.listenUsingRfcommWithServiceRecord("aaaa", UUID.fromString(SysDefine.SOCKET_UUID));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "AcceptThread:开启失败 ");
        }
        Log.d(TAG, "AcceptThread:开启服务成功 ");
    }

    public void run() {
        if (mmServerSocket == null) return;
        while (true) {
            try {
                Log.d(TAG, "run: 等待客户端接入");
                BluetoothSocket socket = mmServerSocket.accept();
                //处理数据
                new Thread(new HandleDataThread2(socket)).start();
            } catch (IOException e) {
                Log.e(TAG, "run: accept发生异常");
                closeServerSocket();
                e.printStackTrace();
                break;
            }
        }
    }

    public void closeServerSocket() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "run:close 发生异常");
            e.printStackTrace();
        }
    }

}
