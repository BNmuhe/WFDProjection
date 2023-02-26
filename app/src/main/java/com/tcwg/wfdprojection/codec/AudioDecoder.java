package com.tcwg.wfdprojection.codec;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;


import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioDecoder {

    public static final String TAG = AudioDecoder.class.getSimpleName();

    //解码器
    private MediaCodec mediaCodec;

    //解码器的bufferInfo
    private MediaCodec.BufferInfo bufferInfo;

    //用于播放解码后的音频流
    private AudioTrack audioTrack;

    //采样率
    private static final int SAMPLE_RATE = 44100;



    public void startDecoder()  {

        try {
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MediaFormat mediaFormat = new MediaFormat();

        mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);

        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);

        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);

        mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);

        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);

        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 1024);

        byte[] data = new byte[]{(byte) 0x11, (byte) 0x90};
        ByteBuffer csd_0 = ByteBuffer.wrap(data);
        mediaFormat.setByteBuffer("csd-0", csd_0);

        mediaCodec.configure(mediaFormat, null, null, 0);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
        audioTrack.play();

        mediaCodec.start();

        bufferInfo = new MediaCodec.BufferInfo();
    }
    public void stopDecoder() {
        mediaCodec.stop();
        mediaCodec.release();
        audioTrack.stop();
        audioTrack.release();
    }
    public void decode(byte[] input) {
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(10000);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(input,0,input.length);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, System.currentTimeMillis(), 0);

        }
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);
            outputBuffer.clear();
            audioTrack.write(outData, 0, outData.length);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }
}