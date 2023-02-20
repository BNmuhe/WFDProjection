package com.tcwg.wfdprojection.constant;

public class MyDeviceConstants {

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


}
