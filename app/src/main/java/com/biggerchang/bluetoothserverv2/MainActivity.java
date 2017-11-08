package com.biggerchang.bluetoothserverv2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.biggerchang.bluetoothserverv2.bluetooth.connect.BluetoothConnector;
import com.biggerchang.bluetoothserverv2.bluetooth.interactive.BluetoothIO;

/*
    盒子端：蓝牙服务端，因为以后功能扩充的话，一个盒子能被多个手机控制
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothConnector mBluetoothConnector;
    private static final String TAG = "MainActivity";
    private TextView mTvSend;
    private TextView mTvReceive;
    private EditText mEtSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mBluetoothConnector = new BluetoothConnector(this, mHandler);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothIO.MSG_WHAT_ON_RECEIVE_DATA:
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    if (msg.obj != null) {
                        mTvReceive.setText("server receive:" + msg.obj.toString());
                        mBluetoothConnector.send(msg.obj);
                        mTvSend.setText("server send:" + msg.obj.toString());
                    }
                    break;
            }
        }
    };

    private void initView() {
        findViewById(R.id.btn_turn_on_bluetooth).setOnClickListener(this);
        findViewById(R.id.btn_detection).setOnClickListener(this);
        findViewById(R.id.btn_turn_on_server).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mTvSend = (TextView) findViewById(R.id.tv_send_data);
        mTvReceive = (TextView) findViewById(R.id.tv_receive_data);
        mEtSend = (EditText) findViewById(R.id.et_send);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_turn_on_bluetooth:
                mBluetoothConnector.setAdapterEnable();
                break;
            case R.id.btn_detection:
                mBluetoothConnector.setDiscoverable();
                break;
            case R.id.btn_turn_on_server:
                mBluetoothConnector.turnOnServer();
                break;
            case R.id.btn_send:
                String txt = mEtSend.getText().toString();
                mTvSend.setText(txt);
                mBluetoothConnector.send(txt);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //关闭socket
        mBluetoothConnector.close();
        super.onDestroy();
    }
}
