package com.tcwg.wfdprojection.manager;

import android.media.projection.MediaProjection;
import android.util.Log;

import com.tcwg.wfdprojection.connection.SocketClient;
import com.tcwg.wfdprojection.util.ScreenEncoder;

import java.net.URI;
import java.net.URISyntaxException;

public class SenderSocketManager {

    public static final String TAG =SenderSocketManager.class.getSimpleName();


    private static final int SOCKET_PORT = 50000;
    private String IP;

    private SocketClient socketClient;
    private ScreenEncoder mScreenEncoder;

    private MediaProjection mediaProjection;

    public SenderSocketManager(String IP,MediaProjection mediaProjection) {
        this.IP=IP;
        this.mediaProjection = mediaProjection;
    }
    public void start() {

        try {
            // 需要修改为服务端的IP地址与端口
            URI uri = new URI("ws://"+IP+":" + SOCKET_PORT);
            Log.e(TAG,uri.toString());
            socketClient = new SocketClient(uri,this);
            socketClient.connect();
            Log.e(TAG,"connect");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void startEncode(){
        mScreenEncoder = new ScreenEncoder(mediaProjection, this);
        mScreenEncoder.startEncode();
    }


    public void sendData(byte[] newBytes)  {
        socketClient.sendData(newBytes);
    }



    public void close() {
        if (mScreenEncoder != null) {
            mScreenEncoder.stopEncode();
            mScreenEncoder = null;

        }
        socketClient.close();

    }

}
