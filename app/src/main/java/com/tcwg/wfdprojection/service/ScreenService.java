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


        //是否支持AudioPlaybackCapture
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            //支持AudioPlaybackCapture,将开启视频和音频传输服务
            AudioPlaybackCaptureConfiguration.Builder builder;
            builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
            builder.addMatchingUsage(AudioAttributes.USAGE_MEDIA);//多媒体
            builder.addMatchingUsage(AudioAttributes.USAGE_ALARM);//闹铃
            builder.addMatchingUsage(AudioAttributes.USAGE_GAME);//游戏
            builder.addMatchingUsage(AudioAttributes.USAGE_UNKNOWN);//未知
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
            // 初始化发送端，包括视频和音频socket
            senderSocketManager = new SenderSocketManager(wifiP2pInfo.groupOwnerAddress.getHostAddress(), mediaProjection,audioRecord,true);


        }else {
            //不支持AudioPlaybackCapture,将仅开启视频传输服务
            if (mediaProjection == null) {
                return;
            }
            Log.e(TAG, "project start");
            // 初始化发送端，包括视频socket
            senderSocketManager = new SenderSocketManager(wifiP2pInfo.groupOwnerAddress.getHostAddress(), mediaProjection);
        }


        senderSocketManager.start();
    }


    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, SenderActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            // 前台服务notification适配
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel =
                    new NotificationChannel(
                            "notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认通知音
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