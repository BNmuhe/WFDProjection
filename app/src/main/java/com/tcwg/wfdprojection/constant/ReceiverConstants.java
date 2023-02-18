package com.tcwg.wfdprojection.constant;

public class ReceiverConstants {
    private static Integer VIDEO_WIDTH = 1080;
    private static Integer VIDEO_HEIGHT = 1920;
    private static Integer SCREEN_FRAME_RATE = 60;

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
