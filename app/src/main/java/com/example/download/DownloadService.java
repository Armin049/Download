package com.example.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class DownloadService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();     //start

        final String CHANNELID = "Foreground";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_DEFAULT      //Notification
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification= new Notification.Builder(this, CHANNELID)
                .setContentText("Download still running")
                .setSmallIcon(R.drawable.ic_launcher_background);
        startForeground(101, notification.build());  //create a Notification when the service is running, keeps the Download
                                                        //running even after he is getting killed
        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind (Intent intent){
        return null;
    }
}
