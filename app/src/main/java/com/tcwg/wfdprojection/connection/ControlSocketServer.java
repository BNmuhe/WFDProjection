package com.tcwg.wfdprojection.connection;

import android.util.Log;

import com.tcwg.wfdprojection.constant.MyDeviceConstants;
import com.tcwg.wfdprojection.constant.P2pDeviceConstants;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ControlSocketServer extends WebSocketServer {


    public static final String TAG = ControlSocketServer.class.getSimpleName();
    private WebSocket conn;
    public ControlSocketServer( InetSocketAddress inetSocketAddress){
        super(inetSocketAddress);
        this.setReuseAddr(true);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.e(TAG, "onOpen");
        this.conn = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //在这里要接收判断对方的屏幕是竖着的还是横着的
        //在后台中计算出点击屏幕时的相对位置，进行判断，然后将命令发出
        String[] constant = message.split(":");
        P2pDeviceConstants.setVideoWidth(Integer.parseInt(constant[0]));
        P2pDeviceConstants.setVideoHeight(Integer.parseInt(constant[1]));
        Log.e(TAG,"receive p2p device constant "+P2pDeviceConstants.getVideoWidth()+" "+P2pDeviceConstants.getVideoHeight());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }


    public void sendData(String command) {

        Log.e(TAG,"ready to send command:"+command);
        if(conn!=null&&!conn.isClosed()){
            conn.send(command);
        }
    }



}
