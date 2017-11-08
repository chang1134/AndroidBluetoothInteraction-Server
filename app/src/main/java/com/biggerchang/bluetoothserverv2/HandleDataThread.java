package com.biggerchang.bluetoothserverv2;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HandleDataThread extends Thread {
    private static final String TAG = "HandleDataThread";
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private boolean mIsClose = false;

    public HandleDataThread(BluetoothSocket socket) {
        Log.d(TAG, "HandleDataThread: " + socket.getRemoteDevice().getName());
        mSocket = socket;
        try {
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

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
        try {
            // 装饰流BufferedReader封装输入流（接收客户端的流）

            //   DataInputStream dis = new DataInputStream(new BufferedInputStream(mInputStream));
            //   byte[] bytes = new byte[1024]; // 一次读取一个byte
            //   //String ret = "";
            //   int len;
            //   while ((len = dis.read(bytes)) != -1) {
            //       Log.d(TAG, "receive: data:" + new String(bytes, 0, len));
            //      //ret += bytesToHexString(bytes) + " ";
            //      //if (dis.available() == 0) { //一个请求
            //      //    Log.d(TAG, "receive: ret:" + ret);
            //      //}
            //   }

            byte[] bytes = new byte[4096];
            int len;
//            while ((len = mInputStream.read(bytes)) != -1) {
//                String data = new String(bytes, 0, len);
//                String[] array = data.split(SysDefine.MESSAGE_END_MARK);
//                for (String item : array) {
//                    if (item != null && !item.equals("")) {
//                        Log.d(TAG, "receive: data:" + item + "  接收次数：" + index++);
//                    } else {
//                        break;
//                    }
//                }
//            }
            while (true) {
                len = mInputStream.read(bytes);
                String content = new String(bytes, 0, len);
                Log.d(TAG, "receive: " + content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String process(InputStream in, String charset) {
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        int len = 0;
        try {
            while ((len = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public void send(String data) {
        try {
            if (mOutputStream != null) {
                data += SysDefine.MESSAGE_END_MARK;//自定义的结束符号
                mOutputStream.write(data.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        mIsClose = true;
        try {
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
