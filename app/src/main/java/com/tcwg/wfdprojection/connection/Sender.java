package com.tcwg.wfdprojection.connection;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sender extends Thread{


    private final String TAG = Sender.class.getSimpleName();

    private String IP;

    private int port;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;

    public void init(String IP, int port){
        this.IP = IP;
        this.port = port;
        Log.e(TAG,"get receiver info: "+IP+":"+port);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(IP,port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.e(TAG,"socket established");
    }

    public void sendData(byte[] bytes) throws IOException {
        if(socket!=null&& socket.isConnected()){
            Log.e(TAG, "sendData");
            outputStream.write(bytes);
        }
    }

    public void close() throws IOException {
        if(socket!=null){
            socket.close();
        }
    }

}
