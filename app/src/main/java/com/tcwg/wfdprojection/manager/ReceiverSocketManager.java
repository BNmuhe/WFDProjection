package com.tcwg.wfdprojection.manager;

import android.view.Surface;

import com.tcwg.wfdprojection.connection.SocketServer;
import com.tcwg.wfdprojection.util.ScreenDecoder;

import java.net.InetSocketAddress;

public class ReceiverSocketManager implements SocketServer.SocketCallback{



    public static final String TAG=ReceiverSocketManager.class.getSimpleName();
    private static final int SOCKET_PORT = 50000;
    private SocketServer socketServer;

    private ScreenDecoder screenDecoder;


    public void start(Surface surface) {


        screenDecoder = new ScreenDecoder();
        screenDecoder.startDecode(surface);

        socketServer =new SocketServer(this,new InetSocketAddress(SOCKET_PORT));
        socketServer.start();

    }
    public void close() {
        try {
            socketServer.stop();
            socketServer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (screenDecoder != null) {
            screenDecoder.stopDecode();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {
        if (screenDecoder != null) {
            screenDecoder.decodeData(data);
        }
    }
}
