package com.example.overlaysample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class FilteringService extends Service {
    private final static String TAG = "FilteringService";

    private Boolean mIsServiceRunning;

    static final private String CHANNEL_ID = "OverlaySampleFilteringService";
    private Notification mNotification;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private NotificationManagerCompat mNotificationManagerCompat;
    static final private int NOTIFY_ID = 1;
    private Intent mNotifyIntent;
    private PendingIntent mPendingIntent;

    private WindowManager mWindowManager;

    public FilteringService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"FilteringService onCreate");
        mIsServiceRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"FilteringService onStartCommand");
        if (!mIsServiceRunning) {
            mIsServiceRunning = true;
            createNotification();
            mainBackGroundProcess();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create Notification Channel
            CharSequence name = "FilteringDisplay";
            String description = "Filtering display";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Display notification
            mNotifyIntent = new Intent(this, MainActivity.class);
            mNotifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mPendingIntent = PendingIntent.getActivity(this, 0, mNotifyIntent, 0);

            mNotificationManagerCompat = (NotificationManagerCompat.from(getApplicationContext()));

            mNotificationCompatBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Filtering Now!")
                    .setContentText("click here if you stop filtering")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(mPendingIntent)
                    .setAutoCancel(true);

            mNotification = mNotificationCompatBuilder.build();
            mNotification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(NOTIFY_ID, mNotification);
        }
    }

    private void mainBackGroundProcess() {
        Log.d(TAG,"NOW! start background process");
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        int typeLayer = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        mWindowManager = (WindowManager)getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams (
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                typeLayer,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        final ViewGroup nullParent = null;
        View view = layoutInflater.inflate(R.layout.filtering_layer, nullParent);
        mWindowManager.addView(view, params);
    }
}