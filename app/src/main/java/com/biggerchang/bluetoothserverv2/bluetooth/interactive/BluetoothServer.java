package com.biggerchang.bluetoothserverv2.bluetooth.interactive;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.biggerchang.bluetoothserverv2.SysDefine;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket mServerSocket;
    private Handler mHandler;
    private List<BluetoothIO> mBluetoothIOs = new CopyOnWriteArrayList<>();
    private ExecutorService mCatchThreadPool;


    public BluetoothServer(BluetoothAdapter adapter, Handler handler) {
        mCatchThreadPool = Executors.newCachedThreadPool();
        mHandler = handler;
        try {
            mServerSocket = adapter.listenUsingRfcommWithServiceRecord("Server", UUID.fromString(SysDefine.SOCKET_UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (mServerSocket == null) return;
        while (true) {
            try {
                Log.d("Bluetooth", "run: 等待接入");
                BluetoothSocket socket = mServerSocket.accept();//蓝牙一对一通讯
                Log.d("Bluetooth", "run: 连接成功:" + socket.getRemoteDevice().getAddress());
                BluetoothIO bluetoothIO = new BluetoothIO(socket, mHandler);
                mBluetoothIOs.add(bluetoothIO);
                mCatchThreadPool.execute(bluetoothIO);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void send(Object data) {
        for (BluetoothIO item : mBluetoothIOs) {
            boolean isSend = item.send(data);
            if (!isSend) mBluetoothIOs.remove(item);
        }
    }

    public void close() {
        try {
            for (BluetoothIO item : mBluetoothIOs) {
                if (item != null) item.closeSocket();
            }
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mBluetoothIOs.clear();
        }
    }
}
