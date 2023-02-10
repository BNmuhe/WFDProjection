package com.tcwg.wfdprojection;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.tcwg.wfdprojection.activity.BaseActivity;
import com.tcwg.wfdprojection.activity.ReceiverActivity;
import com.tcwg.wfdprojection.activity.SenderActivity;


public class MainActivity extends BaseActivity {

    private static final int CODE_REQ_PERMISSIONS = 665;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requestPermission
        findViewById(R.id.btnCheckPermission).setOnClickListener(v ->
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CHANGE_NETWORK_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, CODE_REQ_PERMISSIONS));

        findViewById(R.id.btnReceiver).setOnClickListener(v ->
                startActivity(ReceiverActivity.class));

        findViewById(R.id.btnSender).setOnClickListener(v ->
                startActivity(SenderActivity.class));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQ_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    showToast("缺少权限，请先授予权限: " + permissions[i]);
                    return;
                }
            }
            showToast("已获得权限");
        }
    }
}