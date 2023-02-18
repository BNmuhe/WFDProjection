package com.tcwg.wfdprojection.util;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.tcwg.wfdprojection.constant.MyDeviceConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenDecoder {
    private int VIDEO_WIDTH;
    private int VIDEO_HEIGHT;
//    private int SCREEN_FRAME_RATE;
    private static final long DECODE_TIME_OUT = 10000;
//    private static final int SCREEN_FRAME_INTERVAL = 1;
    private MediaCodec mMediaCodec;

    public ScreenDecoder() {

        this.VIDEO_WIDTH = MyDeviceConstants.getVideoWidth();
        this.VIDEO_HEIGHT = MyDeviceConstants.getVideoHeight();
//        this.SCREEN_FRAME_RATE = MyDeviceConstants.getScreenFrameRate();
    }

    public void startDecode(Surface surface) {
        try {
            // 配置MediaCodec
            mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            MediaFormat mediaFormat =
                    MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, VIDEO_WIDTH, VIDEO_HEIGHT);
//            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 8192000);
//            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, SCREEN_FRAME_RATE);
//            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, SCREEN_FRAME_INTERVAL);
//            mediaFormat.setInteger(MediaFormat.KEY_MAX_WIDTH,VIDEO_WIDTH);
//            mediaFormat.setInteger(MediaFormat.KEY_MAX_HEIGHT,VIDEO_HEIGHT);
            mMediaCodec.configure(mediaFormat, surface, null, 0);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decodeData(byte[] data) {
        int index = mMediaCodec.dequeueInputBuffer(DECODE_TIME_OUT);
        if (index >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            inputBuffer.put(data, 0, data.length);
            mMediaCodec.queueInputBuffer(index, 0, data.length, System.currentTimeMillis(), 0);
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, DECODE_TIME_OUT);
        while (outputBufferIndex > 0) {
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    public void stopDecode() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }
    }

}
