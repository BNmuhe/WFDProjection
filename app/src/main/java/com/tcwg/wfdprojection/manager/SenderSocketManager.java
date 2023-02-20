package com.tcwg.wfdprojection.manager;

import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.util.Log;

import com.tcwg.wfdprojection.codec.AudioEncoder;
import com.tcwg.wfdprojection.connection.AudioSocketClient;
import com.tcwg.wfdprojection.connection.ScreenSocketClient;
import com.tcwg.wfdprojection.codec.ScreenEncoder;

import java.net.URI;
import java.net.URISyntaxException;

public class SenderSocketManager implements AudioSocketClient.SocketCallback{

    public static final String TAG =SenderSocketManager.class.getSimpleName();
    private static final int SOCKET_SCREEN_PORT = 50000;
    private static final int SOCKET_AUDIO_PORT = 50001;
    private String IP;
    private ScreenSocketClient screenSocketClient;
    private AudioSocketClient audioSocketClient;
    private ScreenEncoder screenEncoder;
    private AudioEncoder audioEncoder;
    private final MediaProjection mediaProjection;
    private AudioRecord audioRecord;

    public SenderSocketManager(String IP,MediaProjection mediaProjection) {
        this.IP=IP;
        this.mediaProjection = mediaProjection;
    }
    public SenderSocketManager(String IP, MediaProjection mediaProjection, AudioRecord audioRecord) {
        this.IP=IP;
        this.mediaProjection = mediaProjection;
        this.audioRecord =audioRecord;
    }


    public void start(){
        if(audioRecord!=null){
            startAudioRecodeSocket();
        }
        startProjectionSocket();
    }

    public void  startAudioRecodeSocket(){
        try{

            URI uri = new URI("ws://"+IP+":" + SOCKET_AUDIO_PORT);

            audioSocketClient = new AudioSocketClient(this,uri);
            audioSocketClient.connect();
            Log.e(TAG,"connect "+ uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void startProjectionSocket() {
        try {
            // 需要修改为服务端的IP地址与端口
            URI uri = new URI("ws://"+IP+":" + SOCKET_SCREEN_PORT);
            screenSocketClient = new ScreenSocketClient(uri,this);
            screenSocketClient.connect();
            Log.e(TAG,"connect "+ uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void startScreenEncode(){
        screenEncoder = new ScreenEncoder(mediaProjection, this);
        screenEncoder.startEncode();
    }

    public void startAudioEncode(){
        audioEncoder = new AudioEncoder(audioRecord, this);
        audioEncoder.startEncoder();
    }

    public void sendScreenData(byte[] newBytes)  {
        screenSocketClient.sendData(newBytes);
    }

    public void sendAudioData(byte[] newBytes)  {

        audioSocketClient.sendData(newBytes);


    }



    public void close() {
        if (screenEncoder != null) {
            screenEncoder.stopEncode();
            screenEncoder = null;
        }
        if (audioEncoder != null) {
            audioEncoder.stopEncode();
            audioEncoder = null;
        }
        if(!screenSocketClient.isClosed()){
            screenSocketClient.close();
        }
        if(audioSocketClient!=null&&!audioSocketClient.isClosed()){
            audioSocketClient.close();
        }
    }

    @Override
    public void onConnection() {
        startAudioEncode();
    }
}
