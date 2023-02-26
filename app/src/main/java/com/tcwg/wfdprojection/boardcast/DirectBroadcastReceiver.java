package com.tcwg.wfdprojection.boardcast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.tcwg.wfdprojection.listener.DirectActionListener;

import java.util.ArrayList;
import java.util.List;

public class DirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DirectBroadcastReceiver";
    private final WifiP2pManager wifiP2pManager;
    private final WifiP2pManager.Channel channel;
    private final DirectActionListener directActionListener;

    //构造
    public DirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, DirectActionListener directActionListener) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.directActionListener = directActionListener;
    }

    //添加广播
    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                //WifiP2P是否可用
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -100);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        directActionListener.wifiP2pEnabled(true);
                    } else {
                        directActionListener.wifiP2pEnabled(false);
                        List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
                        directActionListener.onPeersAvailable(wifiP2pDeviceList);
                    }
                    break;
                }
                //对等设备列表变化
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    wifiP2pManager.requestPeers(channel, peers -> directActionListener.onPeersAvailable(peers.getDeviceList()));
                    break;
                }
                //WifiP2P连接状态改变
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null && networkInfo.isConnected()) {
                        wifiP2pManager.requestConnectionInfo(channel, info -> directActionListener.onConnectionInfoAvailable(info));
                        Log.e(TAG, "connect to p2p device");
                    } else {
                        directActionListener.onDisconnection();
                        Log.e(TAG, "disconnect to p2p device");
                    }
                    break;
                }
                //本设备信息变化
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: {
                    WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    directActionListener.onSelfDeviceAvailable(wifiP2pDevice);
                    break;
                }
            }
        }
    }
}
