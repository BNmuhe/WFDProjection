package com.tcwg.wfdprojection.manager;

import android.media.projection.MediaProjection;
import android.util.Log;

import com.tcwg.wfdprojection.connection.Sender;
import com.tcwg.wfdprojection.util.ScreenEncoder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SenderSocketManager {

    public static final String TAG =SenderSocketManager.class.getSimpleName();


    private static final int SOCKET_PORT = 50000;
    private String IP;

    private Sender sender;
    private ScreenEncoder mScreenEncoder;

    public SenderSocketManager(String IP) {

        this.IP=IP;
    }
    public void start(MediaProjection mediaProjection) {
        mScreenEncoder = new ScreenEncoder(mediaProjection, this);
        mScreenEncoder.startEncode();
        try {
            // 需要修改为服务端的IP地址与端口
            URI uri = new URI("ws://"+IP+":" + SOCKET_PORT);
            Log.e(TAG,uri.toString());
            sender = new Sender(uri);
            sender.connect();
            Log.e(TAG,"connect");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }


    public void sendData(byte[] newBytes)  {
        sender.sendData(newBytes);
    }



    public void close() {
        mScreenEncoder.stopEncode();
        sender.close();
        if (mScreenEncoder != null) {
            mScreenEncoder.stopEncode();
        }
    }

}
