package com.biggerchang.bluetoothserverv2.bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.biggerchang.bluetoothserverv2.SysDefine;
import com.biggerchang.bluetoothserverv2.bluetooth.interactive.BluetoothServer;
import com.biggerchang.bluetoothserverv2.bluetooth.utils.ClsUtils;

/**
 * 负责蓝牙连接和通讯
 */
public class BluetoothConnector {

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothServer mBluetoothServer;
    private static final String PIN = "1234";

    public BluetoothConnector(Context context, Handler handler) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获得数据时的接口回调
        mHandler = handler;
        initReceiver();
    }


    //设置可见性
    public void setDiscoverable() {
        //启动修改蓝牙可见性的Intent
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //设置蓝牙可见性的时间，方法本身规定最多可见300秒,0表示永久开启
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        mContext.startActivity(intent);
    }

    //开启蓝牙服务端
    public void turnOnServer() {
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter, mHandler);
        new Thread(mBluetoothServer).start();
    }

    public void setAdapterEnable() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    public void send(Object data) {
        mBluetoothServer.send(data);
    }

    public void close() {
        mBluetoothServer.close();
        mContext.unregisterReceiver(mReceive);
    }


    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SysDefine.BLUETOOTH_DEVICE_ACTION_PAIRING_REQUEST);
        mContext.registerReceiver(mReceive, filter);
    }

    private BroadcastReceiver mReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SysDefine.BLUETOOTH_DEVICE_ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    try {
                        //1.确认配对
                        ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                        //2.终止有序广播
                        abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                        //3.调用setPin方法进行配对...
                        ClsUtils.setPin(device.getClass(), device, PIN);
                        Toast.makeText(mContext, "匹配成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Log.e("提示信息", "这个设备不是目标蓝牙设备");
            }
        }
    };

}
