package com.tcwg.wfdprojection.constant;

import android.util.Log;

public class MyDeviceConstants {


    public static final String TAG = MyDeviceConstants.class.getSimpleName();
    private static String IP_ADDRESS = "192.168.49.1";
    private static Integer VIDEO_WIDTH = 1080;
    private static Integer VIDEO_HEIGHT = 1920;
    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }

    private static Integer SCREEN_FRAME_RATE = 90;

    public static Integer getVideoWidth() {
        return VIDEO_WIDTH;
    }

    public static void setVideoWidth(Integer videoWidth) {
        VIDEO_WIDTH = videoWidth;
    }

    public static Integer getVideoHeight() {
        return VIDEO_HEIGHT;
    }

    public static void setVideoHeight(Integer videoHeight) {
        VIDEO_HEIGHT = videoHeight;
    }

    public static Integer getScreenFrameRate() {
        return SCREEN_FRAME_RATE;
    }

    public static void setScreenFrameRate(Integer screenFrameRate) {
        SCREEN_FRAME_RATE = screenFrameRate;
    }



    public static Float resetX(Float x){
        float proportion;
        float result;
        float blank;
        Log.e(TAG,"X:"+x);
        Log.e(TAG,"VIDEO_WIDTH:"+VIDEO_WIDTH+"VIDEO_HEIGHT:"+VIDEO_HEIGHT);
        //如果接收方长宽比小于发送方
        if((float)MyDeviceConstants.getVideoHeight()/(float)MyDeviceConstants.getVideoWidth()
                <
                (float)P2pDeviceConstants.getVideoHeight()/(float)P2pDeviceConstants.getVideoWidth()){

            Log.e(TAG,"resetX");
            proportion = (float) MyDeviceConstants.getVideoHeight()/(float) P2pDeviceConstants.getVideoHeight();
            Log.e(TAG,"proportion:"+proportion);
            blank = (MyDeviceConstants.getVideoWidth()-P2pDeviceConstants.getVideoWidth()*proportion)/2;
            Log.e(TAG,"blank:"+blank);
            x-=blank;
        }else {

            proportion = (float) MyDeviceConstants.getVideoWidth()/(float) P2pDeviceConstants.getVideoWidth();
            Log.e(TAG,"proportion:"+proportion);
        }
        result=x/proportion;
        return result;
    }

    public static Float resetY(Float y){
        float proportion;
        float result;
        float blank;
        Log.e(TAG,"Y:"+y);
        Log.e(TAG,"VIDEO_WIDTH:"+VIDEO_WIDTH+"VIDEO_HEIGHT:"+VIDEO_HEIGHT);
        if((float)MyDeviceConstants.getVideoHeight()/(float)MyDeviceConstants.getVideoWidth()
                >
                (float)P2pDeviceConstants.getVideoHeight()/(float)P2pDeviceConstants.getVideoWidth()){
            Log.e(TAG,"resetY");
            proportion = (float) MyDeviceConstants.getVideoWidth()/(float) P2pDeviceConstants.getVideoWidth();
            Log.e(TAG,"proportion:"+proportion);
            blank = (MyDeviceConstants.getVideoHeight()-P2pDeviceConstants.getVideoHeight()*proportion)/2;
            Log.e(TAG,"blank:"+blank);
            y-=blank;
        }else {
            proportion = (float) MyDeviceConstants.getVideoHeight()/(float) P2pDeviceConstants.getVideoHeight();
            Log.e(TAG,"proportion:"+proportion);
        }
        result=y/proportion;
        return result;
    }


}
