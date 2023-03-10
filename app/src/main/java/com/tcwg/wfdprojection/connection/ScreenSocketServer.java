package com.tcwg.wfdprojection.connection;

import android.util.Log;

import com.tcwg.wfdprojection.constant.MyDeviceConstants;
import com.tcwg.wfdprojection.constant.P2pDeviceConstants;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ScreenSocketServer extends WebSocketServer {

    public static final String TAG = ScreenSocketServer.class.getSimpleName();
    private final SocketCallback socketCallback;



    private WebSocket webSocket;

    public ScreenSocketServer(SocketCallback socketCallback, InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
        this.socketCallback = socketCallback;
        this.setReuseAddr(true);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.e(TAG, "onOpen");
        webSocket = conn;
        conn.send(MyDeviceConstants.getVideoWidth()+":"+MyDeviceConstants.getVideoHeight());
        Log.e(TAG,"send my device constant "+MyDeviceConstants.getVideoWidth()+" "+MyDeviceConstants.getVideoHeight());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.e(TAG, "onClose:"+reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String[] constant = message.split(":");
        P2pDeviceConstants.setVideoWidth(Integer.parseInt(constant[0]));
        P2pDeviceConstants.setVideoHeight(Integer.parseInt(constant[1]));
        Log.e(TAG,"receive p2p device constant "+P2pDeviceConstants.getVideoWidth()+" "+P2pDeviceConstants.getVideoHeight());
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (socketCallback != null) {
            socketCallback.onReceiveScreenData(buf);
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
        void onReceiveScreenData(byte[] data);
    }

}
