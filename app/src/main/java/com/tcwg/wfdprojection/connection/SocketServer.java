package com.tcwg.wfdprojection.connection;

import android.annotation.SuppressLint;
import android.util.Log;

import com.tcwg.wfdprojection.constant.ReceiverConstants;
import com.tcwg.wfdprojection.constant.SenderConstants;
import com.tcwg.wfdprojection.util.ScreenDecoder;

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
//        conn.send(SenderConstants.getVideoWidth()+":"+SenderConstants.getVideoHeight());
//        Log.e(TAG,"send receiver constant "+SenderConstants.getVideoWidth()+" "+SenderConstants.getVideoHeight());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.e(TAG, "onClose:"+reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
//        String[] constant = message.split(":");
//        ReceiverConstants.setVideoHeight(Integer.parseInt(constant[0]));
//        ReceiverConstants.setVideoWidth(Integer.parseInt(constant[1]));
//        ReceiverConstants.setScreenFrameRate(Integer.parseInt(constant[2]));
//        Log.e(TAG,"receive sender constant "+ReceiverConstants.getVideoWidth()+" height "+ReceiverConstants.getVideoHeight()+" fps "+ReceiverConstants.getScreenFrameRate());


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
        Log.e(TAG, "onError:"+ex);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
    }

    public void close() {
        if(webSocket!=null&&!webSocket.isClosed()){

            webSocket.close();
        }
    }

    public interface SocketCallback {
        void onReceiveData(byte[] data);
    }


}
