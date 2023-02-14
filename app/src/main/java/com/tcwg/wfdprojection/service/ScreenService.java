package com.tcwg.wfdprojection.service;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.tcwg.wfdprojection.R;
import com.tcwg.wfdprojection.activity.SenderActivity;
import com.tcwg.wfdprojection.manager.SenderSocketManager;

import java.io.IOException;

public class ScreenService extends Service {


    public static final String TAG = ScreenService.class.getSimpleName();
    private MediaProjectionManager mMediaProjectionManager;

    private WifiP2pInfo wifiP2pInfo;


    private MediaProjection mediaProjection;

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
        wifiP2pInfo=intent.getParcelableExtra("IP");
        try {
            startProject(resultCode, resultData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startProject(int resultCode, Intent data) throws IOException {
        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }
        Log.e(TAG, "project start");
        // 初始化服务器端
        senderSocketManager = new SenderSocketManager(wifiP2pInfo.groupOwnerAddress.getHostAddress());
        senderSocketManager.start(mediaProjection);
    }




    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, SenderActivity.class);

        builder
                .setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                                this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
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

       if (senderSocketManager!=null){
           senderSocketManager.close();
           senderSocketManager=null;
       }

        if(mediaProjection != null) {
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