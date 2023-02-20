package com.tcwg.wfdprojection.connection;

import android.content.Intent;
import android.util.Log;

import com.tcwg.wfdprojection.activity.SenderActivity;
import com.tcwg.wfdprojection.constant.P2pDeviceConstants;
import com.tcwg.wfdprojection.manager.SenderSocketManager;
import com.tcwg.wfdprojection.service.ScreenService;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SocketClient extends WebSocketClient {

    private final String TAG = SocketClient.class.getSimpleName();

    private final SenderSocketManager senderSocketManager;

    public SocketClient(URI serverUri,SenderSocketManager senderSocketManager) {
        super(serverUri);
        this.senderSocketManager = senderSocketManager;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.e(TAG, "onOpen");
    }

    @Override
    public void onMessage(String message) {
        String[] constant = message.split(":");
        P2pDeviceConstants.setVideoWidth(Integer.parseInt(constant[0]));
        P2pDeviceConstants.setVideoHeight(Integer.parseInt(constant[1]));
        Log.e(TAG,"receive p2p device constant "+P2pDeviceConstants.getVideoWidth()+" "+P2pDeviceConstants.getVideoHeight());
        senderSocketManager.startEncode();
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
            this.send(bytes);
        }
    }

}
