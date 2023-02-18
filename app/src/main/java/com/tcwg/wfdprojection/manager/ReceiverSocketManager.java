package com.tcwg.wfdprojection.manager;

import android.util.Log;
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




        socketServer =new SocketServer(this,new InetSocketAddress(SOCKET_PORT));
        socketServer.start();
        Log.e(TAG, "socketServer start");

        screenDecoder = new ScreenDecoder();
        screenDecoder.startDecode(surface);

    }
    public void close() {
        try {
//            socketServer.close();
            socketServer.stop();



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
