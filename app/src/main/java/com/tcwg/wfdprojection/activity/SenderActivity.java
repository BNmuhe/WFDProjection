package com.tcwg.wfdprojection.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.adapter.DeviceAdapter;
import com.tcwg.wfdprojection.boardcast.DirectBroadcastReceiver;
import com.tcwg.wfdprojection.listener.DirectActionListener;
import com.tcwg.wfdprojection.service.ScreenService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SenderActivity extends BaseActivity {

    private static final String TAG = "SenderActivity";
    private static final int PROJECTION_REQUEST_CODE = 1;

    private TextView tv_myDeviceName;
    private Button btn_startSend;

    private Button btn_disconnect;

    private Button btn_deviceDiscover;

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private WifiP2pInfo wifiP2pInfo;

    private DeviceAdapter deviceAdapter;

    private boolean wifiP2pEnabled = false;

    private List<WifiP2pDevice> wifiP2pDeviceList;

    private BroadcastReceiver broadcastReceiver;

    private WifiP2pDevice mWifiP2pDevice;

    private MediaProjectionManager mediaProjectionManager;

    private final DirectActionListener directActionListener = new DirectActionListener() {
        @Override
        public void wifiP2pEnabled(boolean enabled) {
            wifiP2pEnabled = enabled;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            btn_disconnect.setEnabled(true);
            btn_startSend.setEnabled(true);
            Log.e(TAG, "onConnectionInfoAvailable");
            Log.e(TAG, "onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed);
            Log.e(TAG, "onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            Log.e(TAG, "onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                SenderActivity.this.wifiP2pInfo = wifiP2pInfo;
            }
        }

        @Override
        public void onDisconnection() {
            btn_disconnect.setEnabled(false);
            btn_startSend.setEnabled(false);
            wifiP2pDeviceList.clear();
            wifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable");
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName);
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Log.e(TAG, "Status: " + wifiP2pDevice.status);
            tv_myDeviceName.setText(wifiP2pDevice.deviceName);
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Log.e(TAG, "onPeersAvailable : " + wifiP2pDeviceList.size());
            SenderActivity.this.wifiP2pDeviceList.clear();
            SenderActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager == null) {
            finish();
            return;
        }

        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        setTitle("发送方");
        tv_myDeviceName = findViewById(R.id.tv_myDeviceName);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_deviceDiscover = findViewById(R.id.btn_deviceDiscover);
        btn_startSend = findViewById(R.id.btn_startSend);
        btn_disconnect.setEnabled(false);
        btn_startSend.setEnabled(false);
        RecyclerView rv_deviceList = findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);
        deviceAdapter.setClickListener(position -> {
            mWifiP2pDevice = wifiP2pDeviceList.get(position);
            showToast(mWifiP2pDevice.deviceName);
            connect();
        });

        rv_deviceList.setAdapter(deviceAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));

        btn_deviceDiscover.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showToast("请先授予位置权限");
            }
            if (!wifiP2pEnabled) {
                showToast("需要先打开Wifi");
            }
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    showToast("Success");
                }

                @Override
                public void onFailure(int reasonCode) {
                    showToast("Failure");
                }
            });

        });
        btn_disconnect.setOnClickListener(v -> disconnect());
        btn_startSend.setOnClickListener(v -> {
            try {
                startProjection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void disconnect(){
        btn_disconnect.setEnabled(false);
        btn_startSend.setEnabled(false);
        Intent service = new Intent(this, ScreenService.class);
        stopService(service);
        Log.e(TAG,"disconnect");
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect onFailure:" + reasonCode);
            }
            @Override
            public void onSuccess() {
                Log.e(TAG, "disconnect onSuccess");
            }
        });
    }

    private void startProjection() throws IOException {
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PROJECTION_REQUEST_CODE) {
            Intent service = new Intent(this, ScreenService.class);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            service.putExtra("IP",wifiP2pInfo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }

        }

    }


    private void connect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showToast("请先授予位置权限");
            return;
        }
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "connect onSuccess");

                    showToast("连接成功 ");

                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败 " + reason);
                }
            });
        }
    }

}
