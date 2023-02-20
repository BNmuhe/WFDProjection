package com.tcwg.wfdprojection.manager;

import android.util.Log;
import android.view.Surface;

import com.tcwg.wfdprojection.codec.AudioDecoder;
import com.tcwg.wfdprojection.connection.AudioSocketServer;
import com.tcwg.wfdprojection.connection.ScreenSocketServer;
import com.tcwg.wfdprojection.codec.ScreenDecoder;

import java.net.InetSocketAddress;

public class ReceiverSocketManager implements ScreenSocketServer.SocketCallback, AudioSocketServer.SocketCallback {


    public static final String TAG = ReceiverSocketManager.class.getSimpleName();
    private static final int SOCKET_SCREEN_PORT = 50000;
    private static final int SOCKET_AUDIO_PORT = 50001;
    private ScreenSocketServer screenSocketServer;

    private ScreenDecoder screenDecoder;

    private AudioDecoder audioDecoder;

    private AudioSocketServer audioSocketServer;


    public void start(Surface surface) {
        startAudioRecode();
        startProjection(surface);
    }

    public void startAudioRecode(){
        audioSocketServer = new AudioSocketServer(this, new InetSocketAddress(SOCKET_AUDIO_PORT));
        audioSocketServer.start();
        Log.e(TAG, "startAudioRecode start");
        audioDecoder = new AudioDecoder();
        audioDecoder.startDecoder();

    }

    public void startProjection(Surface surface) {
        screenSocketServer = new ScreenSocketServer(this, new InetSocketAddress(SOCKET_SCREEN_PORT));
        screenSocketServer.start();
        Log.e(TAG, "startProjection start");
        screenDecoder = new ScreenDecoder();
        screenDecoder.startDecode(surface);
    }

    public void close() {
        try {
            if(screenSocketServer!=null){
                screenSocketServer.stop();
                Log.e(TAG, "screenSocketServer.stop");
            }
            if(audioSocketServer!=null){
                audioSocketServer.stop();
                Log.e(TAG, "audioSocketServer.stop");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (screenDecoder != null) {
            screenDecoder.stopDecode();
            Log.e(TAG, "screenDecoder.stopDecode");
        }
        if (audioDecoder != null) {
            audioDecoder.stopDecoder();
            Log.e(TAG, "audioDecoder.stopDecoder");
        }
    }

    @Override
    public void onReceiveScreenData(byte[] data) {
        if (screenDecoder != null) {
            screenDecoder.decodeData(data);
        }
    }

    @Override
    public void onReceiveAudioData(byte[] data) {
        if (audioDecoder != null) {
            audioDecoder.decode(data);
        }
    }
}
