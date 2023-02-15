package com.tcwg.wfdprojection.connection;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Sender extends WebSocketClient {

    private final String TAG = Sender.class.getSimpleName();



    public Sender(URI serverUri) {
        super(serverUri);
    }



    @Override
    public void onOpen(ServerHandshake handshake) {
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

    public void sendData(byte[] bytes) {

        if (this.isOpen()) {
            // 通过WebSocket 发送数据
            Log.e(TAG, "data");
            this.send(bytes);
        }
    }

}
