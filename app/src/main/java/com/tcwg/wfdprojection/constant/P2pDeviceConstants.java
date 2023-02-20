package com.tcwg.wfdprojection.constant;

public class P2pDeviceConstants {
    private static Integer VIDEO_WIDTH = 1080;
    private static Integer VIDEO_HEIGHT = 1920;
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


    //根据双方的屏幕尺寸设置合适的传输时分辨率
    public static void setTransferAccuracy(Integer receiverWidth,Integer receiverHeight){
        //如果接收方的高宽比大于发送方的高宽比
        if(receiverHeight/receiverWidth>VIDEO_HEIGHT/VIDEO_WIDTH){

            float proportion = (float)VIDEO_WIDTH/(float)receiverWidth;
            //宽度变为接收方的宽度
            VIDEO_WIDTH = receiverWidth;
            //高度等比例缩小
            VIDEO_HEIGHT = (int)((float)VIDEO_HEIGHT/proportion);

        }else if(receiverHeight/receiverWidth<VIDEO_HEIGHT/VIDEO_WIDTH){
            float proportion = (float)VIDEO_HEIGHT/(float)receiverHeight;
            VIDEO_HEIGHT = receiverHeight;
            VIDEO_WIDTH = (int)((float)VIDEO_WIDTH/proportion);
        }
    }
}
