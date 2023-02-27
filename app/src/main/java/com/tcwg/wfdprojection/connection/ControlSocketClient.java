package com.tcwg.wfdprojection.connection;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ControlSocketClient extends WebSocketClient {

    public static final String TAG = ControlSocketClient.class.getSimpleName();

    SocketCallback socketCallback;
    public ControlSocketClient(SocketCallback socketCallback,URI serverUri) {
        super(serverUri);
        this.socketCallback = socketCallback;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.e(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {
        Log.e(TAG, "onMessage");
        socketCallback.onReceiveCommand(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public interface SocketCallback {

        void onReceiveCommand(String command);

    }

    public void sendData(String s){
        if (this.isOpen()) {
            // 通过WebSocket 发送数据
            this.send(s);
        }
    }

}
