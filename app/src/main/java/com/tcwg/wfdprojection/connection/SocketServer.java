package com.tcwg.wfdprojection.connection;

import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SocketServer extends WebSocketServer {

    public static final String TAG = SocketServer.class.getSimpleName();
    private SocketCallback socketCallback;

    private WebSocket webSocket;

    public SocketServer(SocketCallback socketCallback, InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
        this.socketCallback = socketCallback;

    }



    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.e(TAG, "onOpen");
        webSocket = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (socketCallback != null) {
            socketCallback.onReceiveData(buf);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }
    public void close() {
        if(webSocket!=null&&!webSocket.isClosed())
        webSocket.close();
    }

    public interface SocketCallback {
        void onReceiveData(byte[] data);
    }


}
