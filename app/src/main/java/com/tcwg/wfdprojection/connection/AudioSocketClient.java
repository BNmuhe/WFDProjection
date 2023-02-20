package com.tcwg.wfdprojection.connection;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class AudioSocketClient extends WebSocketClient {

    SocketCallback socketCallback;

    public static final String TAG = AudioSocketClient.class.getSimpleName();
    public AudioSocketClient(SocketCallback socketCallback,URI serverUri) {
        super(serverUri);
        this.socketCallback = socketCallback;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        socketCallback.onConnection();
        Log.e(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public interface SocketCallback {
        void onConnection();
    }

    public void sendData(byte[] bytes) {

        if (this.isOpen()) {
            // 通过WebSocket 发送数据
            this.send(bytes);
        }
    }

}