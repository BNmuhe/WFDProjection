package com.tcwg.wfdprojection.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Context;
import android.graphics.Path;

import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.tcwg.wfdprojection.Command;
import com.tcwg.wfdprojection.connection.ControlSocketClient;
import com.tcwg.wfdprojection.connection.ControlSocketServer;
import com.tcwg.wfdprojection.constant.MyDeviceConstants;
import com.tcwg.wfdprojection.manager.SenderSocketManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Objects;

public class ControlService extends AccessibilityService {

    public static final String TAG = ControlService.class.getSimpleName();
    private static final int SOCKET_CONTROL_PORT = 50002;

    private Command  startCommand;

    private Long startTime;
    SenderSocketManager senderSocketManager;
    @Override
    public void onCreate(){
        super.onCreate();

        senderSocketManager = SenderSocketManager.getInstance();
        senderSocketManager.setControlService(this);
        Log.e(TAG, "startControl start");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(AccessibilityEvent.TYPE_WINDOWS_CHANGED!=event.getEventType()){
            return;
        }
        Point point = new Point();


        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getRealSize(point);
        MyDeviceConstants.setVideoHeight(point.y);
        MyDeviceConstants.setVideoWidth(point.x);

        senderSocketManager.sendControlData(MyDeviceConstants.getVideoWidth()+":"+MyDeviceConstants.getVideoHeight());

        Log.e(TAG,"reset my device constant "+MyDeviceConstants.getVideoWidth()+" "+MyDeviceConstants.getVideoHeight());

    }

    @Override
    public void onInterrupt() {

    }



    public void addCommand(Command command){
        if(command.getType().equals("start")){
            startCommand=command;
            startTime=System.currentTimeMillis();
        }
        if(command.getType().equals("end")){
            Long endTime = System.currentTimeMillis();
            click(startCommand.getX(),startCommand.getY(), command.getX(), command.getY(), endTime -startTime);

        }
    }


    public void click(Float startX, Float startY, Float endX, Float endY, Long time) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(startX,startY);
        path.lineTo(endX,endY);
        builder.addStroke(new GestureDescription.StrokeDescription(path,0,time));

        GestureDescription gestureDescription = builder.build();
        this.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.e(TAG, "touch");
                super.onCompleted(gestureDescription);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                Log.e(TAG, "touch cancelled");
                super.onCancelled(gestureDescription);
            }
        }, null);
    }


}
