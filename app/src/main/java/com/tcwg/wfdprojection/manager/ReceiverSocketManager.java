package com.tcwg.wfdprojection.manager;

import android.util.Log;
import android.view.Surface;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.connection.Receiver;
import com.tcwg.wfdprojection.util.ScreenDecoder;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class ReceiverSocketManager implements Receiver.SocketCallback{



    public static final String TAG=ReceiverSocketManager.class.getSimpleName();
    private static final int SOCKET_PORT = 50000;
    private Receiver receiver;

    private ScreenDecoder screenDecoder;


    public void start(Surface surface) {


        screenDecoder = new ScreenDecoder();
        screenDecoder.startDecode(surface);

        receiver =new Receiver(this,new InetSocketAddress(SOCKET_PORT));
        receiver.start();

    }
    public void close() {
        try {
            receiver.stop();
            receiver.close();
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
