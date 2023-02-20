package com.tcwg.wfdprojection.connection;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class AudioSocketServer extends WebSocketServer {

    public static final String TAG = AudioSocketServer.class.getSimpleName();
    private SocketCallback socketCallback;


    public AudioSocketServer(SocketCallback socketCallback , InetSocketAddress inetSocketAddress){
        super(inetSocketAddress);
        this.socketCallback = socketCallback;
        this.setReuseAddr(true);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.e(TAG, "onOpen");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.e(TAG, "onClose:"+reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }


    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);

        if (socketCallback != null) {
            socketCallback.onReceiveAudioData(buf);
        }
    }


    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e(TAG, "onError:"+ex);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
    }

    public interface SocketCallback {
        void onReceiveAudioData(byte[] data);
    }

}
