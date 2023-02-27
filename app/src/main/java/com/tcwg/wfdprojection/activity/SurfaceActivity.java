package com.tcwg.wfdprojection.activity;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.manager.ReceiverSocketManager;
import com.tcwg.wfdprojection.service.ScreenService;

public class SurfaceActivity extends BaseActivity implements View.OnTouchListener {


    public static final String TAG = SurfaceActivity.class.getSimpleName();

    private ReceiverSocketManager receiverSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_surface);
        SurfaceView surfaceView = findViewById(R.id.sv_screen);
        LinearLayout llTouch = findViewById(R.id.ll_touch);
        llTouch.setOnTouchListener(this);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                // 连接到服务端
                initSocketManager(holder.getSurface());
            }

            @Override
            public void surfaceChanged(
                    @NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
    }

    private void initSocketManager(Surface surface) {
        receiverSocketManager = new ReceiverSocketManager();
        receiverSocketManager.start(surface);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            /**
             * 点击的开始位置
             */
            case MotionEvent.ACTION_DOWN:
            //    Log.e(TAG,"起始位置：(" + event.getX() + "," + event.getY());
                if(receiverSocketManager!=null){
                    receiverSocketManager.sendControlData("start:" + event.getX() + ":" + event.getY());
                }
                break;
            /**
             * 触屏实时位置
             */
            case MotionEvent.ACTION_MOVE:
               // Log.e(TAG,"实时位置：(" + event.getX() + "," + event.getY());
                if(receiverSocketManager!=null){
                    receiverSocketManager.sendControlData("keep:" + event.getX() + ":" + event.getY());
                }
                break;
            /**
             * 离开屏幕的位置
             */
            case MotionEvent.ACTION_UP:
              //  Log.e(TAG,"结束位置：(" + event.getX() + "," + event.getY());
                if(receiverSocketManager!=null){
                    receiverSocketManager.sendControlData("end:" + event.getX() + ":" + event.getY());
                }
                break;
            default:
                break;
        }
        /**
         *  注意返回值
         *  true：view继续响应Touch操作；
         *  false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
         */
        return true;
    }


    protected void onDestroy() {
        super.onDestroy();
        if (receiverSocketManager != null) {
            receiverSocketManager.close();
        }
    }

}