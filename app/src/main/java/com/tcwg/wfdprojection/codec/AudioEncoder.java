package com.tcwg.wfdprojection.codec;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.tcwg.wfdprojection.manager.SenderSocketManager;

import java.nio.ByteBuffer;

public class AudioEncoder extends Thread{

    public static final String TAG = AudioEncoder.class.getSimpleName();

    private MediaCodec mediaCodec;

    private MediaCodec.BufferInfo bufferInfo;
    private AudioRecord audioRecord;

    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;

    private static final long SOCKET_TIME_OUT = 10000;

    private boolean isPlaying = true;
    private SenderSocketManager senderSocketManager;
    private static  int bufferSize;

    public AudioEncoder(AudioRecord audioRecord, SenderSocketManager senderSocketManager){
        this.audioRecord=audioRecord;
        this.senderSocketManager = senderSocketManager;
    }


    public void startEncoder() {

        try{
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        }catch (Exception e){
            e.printStackTrace();
        }
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);


//        audioRecord.startRecording();
//        mediaCodec.start();
        start();

    }



    public void stopEncoder() {

        if(audioRecord!=null){
            audioRecord.stop();
            audioRecord.release();
        }
        if(mediaCodec!=null){
            mediaCodec.stop();
            mediaCodec.release();
        }

    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 1;  //CPE
        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;

    }


    @Override
    public void run(){
        audioRecord.startRecording();
        mediaCodec.start();
        bufferInfo = new MediaCodec.BufferInfo();
        while(isPlaying){
            byte[] bytes = encode();
            if(bytes!=null){

                senderSocketManager.sendAudioData(bytes);
            }
        }
        stopEncoder();
    }

    public void stopEncode() {

        isPlaying = false;

        Log.e(TAG, "encoder stop");
    }



    public byte[] encode() {

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(SOCKET_TIME_OUT);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            int readResult = audioRecord.read(inputBuffer, inputBuffer.capacity());
            if (readResult == AudioRecord.ERROR_INVALID_OPERATION || readResult == AudioRecord.ERROR_BAD_VALUE) {
                return null;
            }
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, readResult, 0, 0);
        }
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, SOCKET_TIME_OUT);
        if (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
            byte[] outData = new byte[bufferInfo.size+7];
            addADTStoPacket(outData, outData.length);
            outputBuffer.get(outData,7,bufferInfo.size);
            outputBuffer.clear();
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

            return outData;
        }
        return null;
    }



    public static String printHexString(byte[] b) {
        String res = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            res += hex;
        }
        return res;
    }


}
