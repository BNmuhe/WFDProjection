package com.tcwg.wfdprojection.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import  com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.boardcast.DirectBroadcastReceiver;
import com.tcwg.wfdprojection.listener.DirectActionListener;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Collection;

public class ReceiverActivity extends BaseActivity{

    private static final String TAG = "ReceiverActivity";

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private boolean connectionInfoAvailable;

    private BroadcastReceiver broadcastReceiver;

    private Button btn_createGroup;

    private  Button btn_removeGroup;
    private  Button btn_startListen;

    private final DirectActionListener directActionListener = new DirectActionListener() {
        @Override
        public void wifiP2pEnabled(boolean enabled) {
            Log.e(TAG, "wifiP2pEnabled: " + enabled);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            Log.e(TAG, "onConnectionInfoAvailable");
            Log.e(TAG, "isGroupOwner：" + wifiP2pInfo.isGroupOwner);
            Log.e(TAG, "groupFormed：" + wifiP2pInfo.groupFormed);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionInfoAvailable = true;

            }
        }

        @Override
        public void onDisconnection() {
            connectionInfoAvailable = false;
            Log.e(TAG, "onDisconnection");
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable");
            Log.e(TAG, wifiP2pDevice.toString());
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Log.e(TAG, "onPeersAvailable,size:" + wifiP2pDeviceList.size());
            for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
                Log.e(TAG, wifiP2pDevice.toString());
            }
        }

        @Override
        public void onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        initView();
        initEvent();
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    private void initView(){
        setTitle("接收方");
        btn_createGroup = findViewById(R.id.btn_createGroup);
        btn_removeGroup = findViewById(R.id.btn_removeGroup);
        btn_startListen = findViewById(R.id.btn_startListen);
        btn_removeGroup.setEnabled(false);

        btn_startListen.setOnClickListener(v -> {


            startActivity(SurfaceActivity.class);
        });

        btn_createGroup.setOnClickListener(v ->{
            if (ActivityCompat.checkSelfPermission(ReceiverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "createGroup onSuccess");
                    btn_createGroup.setEnabled(false);
                    btn_removeGroup.setEnabled(true);

                    showToast("onSuccess");

                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "createGroup onFailure: " + reason);
                    showToast("onFailure");
                }
            });
        });

        btn_removeGroup.setOnClickListener(v -> {


            removeGroup();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeGroup();
        unregisterReceiver(broadcastReceiver);


    }


    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "removeGroup onSuccess");
                btn_createGroup.setEnabled(true);
                btn_removeGroup.setEnabled(false);
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }
}