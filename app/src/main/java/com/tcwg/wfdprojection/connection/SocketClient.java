package com.tcwg.wfdprojection.connection;

import android.util.Log;

import com.tcwg.wfdprojection.activity.SenderActivity;
import com.tcwg.wfdprojection.constant.ReceiverConstants;
import com.tcwg.wfdprojection.constant.SenderConstants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SocketClient extends WebSocketClient {

    private final String TAG = SocketClient.class.getSimpleName();



    public SocketClient(URI serverUri) {
        super(serverUri);
    }



    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.e(TAG, "onOpen");

    }

    @Override
    public void onMessage(String message) {
//        String[] constant = message.split(":");
//        SenderConstants.setTransferAccuracy(Integer.parseInt(constant[0]),Integer.parseInt(constant[1]));
//        Log.e(TAG,"set accuracy width "+SenderConstants.getVideoWidth()+" height "+SenderConstants.getVideoHeight()+" fps "+SenderConstants.getScreenFrameRate());
//        this.send(SenderConstants.getVideoHeight()+":"+SenderConstants.getVideoWidth()+":"+SenderConstants.getScreenFrameRate());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, "onClose:"+reason);

    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "onError:"+ex);
    }

    public void sendData(byte[] bytes) {

        if (this.isOpen()) {
            // 通过WebSocket 发送数据
            Log.e(TAG, "data");
            this.send(bytes);
        }
    }

}
