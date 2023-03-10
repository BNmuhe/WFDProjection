package com.tcwg.wfdprojection.service;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.activity.SenderActivity;
import com.tcwg.wfdprojection.manager.SenderSocketManager;

import java.io.IOException;

public class ScreenService extends Service {


    public static final String TAG = ScreenService.class.getSimpleName();
    private MediaProjectionManager mMediaProjectionManager;

    private WifiP2pInfo wifiP2pInfo;


    private MediaProjection mediaProjection;

    private AudioRecord audioRecord;

    AudioPlaybackCaptureConfiguration audioPlaybackCaptureConfiguration;

    private SenderSocketManager senderSocketManager;

    public ScreenService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        createNotificationChannel();

        Log.e(TAG, "service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resultCode = intent.getIntExtra("code", -1);
        Intent resultData = intent.getParcelableExtra("data");
        wifiP2pInfo = intent.getParcelableExtra("IP");
        try {
            startProject(resultCode, resultData);
            Log.e(TAG, "startProject");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startProject(int resultCode, Intent data) throws IOException {


        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);


        //????????????AudioPlaybackCapture
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            //??????AudioPlaybackCapture,????????????????????????????????????
            AudioPlaybackCaptureConfiguration.Builder builder;
            builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
            builder.addMatchingUsage(AudioAttributes.USAGE_MEDIA);//?????????
            builder.addMatchingUsage(AudioAttributes.USAGE_ALARM);//??????
            builder.addMatchingUsage(AudioAttributes.USAGE_GAME);//??????
            builder.addMatchingUsage(AudioAttributes.USAGE_UNKNOWN);//??????
            audioPlaybackCaptureConfiguration = builder.build();
            AudioRecord.Builder audioRecodeBuilder = new AudioRecord.Builder();
            audioRecodeBuilder
//                .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(44100)
                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                            .build())
                    .setBufferSizeInBytes(2*AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT))
                    .setAudioPlaybackCaptureConfig(audioPlaybackCaptureConfiguration);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            audioRecord = audioRecodeBuilder.build();

            if (audioRecord == null) {
                return;
            }

            if (mediaProjection == null) {
                return;
            }

            Log.e(TAG, "project and audioRecord start");
            // ??????????????????????????????????????????socket
            senderSocketManager = SenderSocketManager.getInstance();
            senderSocketManager.initSenderSocketManager(wifiP2pInfo.groupOwnerAddress.getHostAddress(), mediaProjection,audioRecord,true);


        }else {
            //?????????AudioPlaybackCapture,??????????????????????????????
            if (mediaProjection == null) {
                return;
            }
            Log.e(TAG, "project start");
            // ?????????????????????????????????socket
            senderSocketManager = SenderSocketManager.getInstance();
            senderSocketManager.initSenderSocketManager(wifiP2pInfo.groupOwnerAddress.getHostAddress(), mediaProjection);
        }


        senderSocketManager.start();
    }


    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, SenderActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // ??????????????????????????????(?????????)
                .setSmallIcon(R.mipmap.ic_launcher) // ??????????????????????????????
                .setContentText("is running......") // ?????????????????????
                .setWhen(System.currentTimeMillis()); // ??????????????????????????????

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            // ????????????notification??????
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel =
                    new NotificationChannel(
                            "notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; // ????????????????????????
        startForeground(110, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        if (senderSocketManager != null) {

            senderSocketManager.close();
            Log.e(TAG, "close sender socket");
            senderSocketManager = null;
        }

        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        Log.e(TAG, "onDestroy finish");

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}