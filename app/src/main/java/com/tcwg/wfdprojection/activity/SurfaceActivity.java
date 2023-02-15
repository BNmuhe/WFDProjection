package com.tcwg.wfdprojection.activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.manager.ReceiverSocketManager;

public class SurfaceActivity extends BaseActivity {

    private ReceiverSocketManager receiverSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        SurfaceView surfaceView = findViewById(R.id.sv_screen);
        surfaceView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                                // 连接到服务端
                                initSocketManager(holder.getSurface());
                            }

                            @Override
                            public void surfaceChanged(
                                    @NonNull SurfaceHolder holder, int format, int width, int height) {}

                            @Override
                            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {}
                        });
    }

    private void initSocketManager(Surface surface) {
        receiverSocketManager = new ReceiverSocketManager();
        receiverSocketManager.start(surface);
    }

    protected void onDestroy() {
        if (receiverSocketManager != null) {
            receiverSocketManager.close();
        }
        super.onDestroy();
    }


}