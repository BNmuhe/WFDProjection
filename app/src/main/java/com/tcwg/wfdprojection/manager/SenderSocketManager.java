package com.tcwg.wfdprojection.manager;

import android.media.projection.MediaProjection;
import android.util.Log;

import com.tcwg.wfdprojection.connection.Sender;
import com.tcwg.wfdprojection.util.ScreenEncoder;

import java.io.IOException;

public class SenderSocketManager {

    public static final String TAG =SenderSocketManager.class.getSimpleName();


    private static final int SOCKET_PORT = 50000;


    private final Sender sender;
    private ScreenEncoder mScreenEncoder;

    public SenderSocketManager(String IP) {
        sender = new Sender();
        sender.init(IP,SOCKET_PORT);
    }



    public void start(MediaProjection mediaProjection) {
        mScreenEncoder = new ScreenEncoder(mediaProjection, this);
        mScreenEncoder.startEncode();
        Log.e(TAG,"start");
    }


    public void sendData(byte[] newBytes) throws IOException {
        sender.sendData(newBytes);
    }



    public void close() {
        try {

            mScreenEncoder.stopEncode();
            sender.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (mScreenEncoder != null) {
            mScreenEncoder.stopEncode();
        }
    }

}
