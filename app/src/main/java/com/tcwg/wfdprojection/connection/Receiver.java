package com.tcwg.wfdprojection.connection;

import android.util.Log;

import com.tcwg.wfdprojection.manager.ReceiverSocketManager;
import com.tcwg.wfdprojection.util.ScreenDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

public class Receiver extends WebSocketServer {

    public static final String TAG = Receiver.class.getSimpleName();
    private SocketCallback socketCallback;

    private WebSocket webSocket;

    public Receiver(SocketCallback socketCallback,InetSocketAddress inetSocketAddress) {
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
