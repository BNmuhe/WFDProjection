package com.tcwg.wfdprojection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.tcwg.wfdprojection.activity.BaseActivity;
import com.tcwg.wfdprojection.activity.ReceiverActivity;
import com.tcwg.wfdprojection.activity.SenderActivity;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CODE_REQ_PERMISSIONS = 665;

    private Button btnCheckPermission;
    private Button btnReceiver;
    private Button btnSender;

    private Button btnGetAccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnCheckPermission=findViewById(R.id.btnCheckPermission);
        btnReceiver= findViewById(R.id.btnReceiver);
        btnSender= findViewById(R.id.btnSender);
        btnGetAccessibility=findViewById(R.id.btnGetAccessibility);

        btnCheckPermission.setEnabled(true);
        btnGetAccessibility.setEnabled(false);
        btnSender.setEnabled(false);
        btnReceiver.setEnabled(false);

        checkPermission();


        //requestPermission
        btnCheckPermission.setOnClickListener(v ->checkPermission());

        btnGetAccessibility.setOnClickListener(v -> getAccessibility());

        btnReceiver.setOnClickListener(v ->
                startActivity(ReceiverActivity.class));

        btnSender.setOnClickListener(v ->
                startActivity(SenderActivity.class));

    }


    private void getAccessibility() {

        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

    }

    private void checkPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.RECORD_AUDIO}, CODE_REQ_PERMISSIONS);
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
            btnSender.setEnabled(true);
            btnGetAccessibility.setEnabled(true);
            btnReceiver.setEnabled(true);
        }
    }
}