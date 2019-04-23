package com.toyproject.honeyimleaving.service;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.activity.SplashActivity;
import com.toyproject.honeyimleaving.db.MyDBHandler;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by iamfe on 2018-10-13.
 */

public class HandlerServcie  extends IntentService  {
    private static final String PACKAGE_NAME = " com.toyproject.honeyimleaving";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".notification";

    public static String TAG = "HandlerServcie";
    private NotificationManager mNotificationManager;
    private static final String CHANNEL_ID = "noti_channel_01";
    private static final int NOTIFICATION_ID = 85854954;
    private static final int NOTIFICATION_RESULT_ID = 85854955;

    private PlaceTaskGPSCheckService mService = null;
    private boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Dlog.d("onServiceConnected 실행됨.");
            PlaceTaskGPSCheckService.LocalBinder binder = (PlaceTaskGPSCheckService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Dlog.d("onServiceDisconnected 실행됨.");
            mService = null;
            mBound = false;
        }
    };

    public HandlerServcie() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dlog.d("onStartCommand 실행됨");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Dlog.d("onHandleIntent 실행됨");
       final int MAX_REPEAT_COUNT = 20;
       final int SLEEP_TIME_MILLISEC = 500;

       int placeTaskID = intent.getIntExtra("place_task_id", -1);
       PlaceTask placeTask = (PlaceTask) intent.getSerializableExtra("PlaceTask");
       int i;
       for (i = 0 ; i < MAX_REPEAT_COUNT ; i++) {
           if(mService != null) {
               Dlog.d("mService != null 실행됨 : " + i);
               if (placeTask != null) {
                   startService(placeTask);
               } else {
                   if (placeTaskID != -1) {
                       MyDBHandler dbHandler = new MyDBHandler(this);
                       try {
                           ArrayList<PlaceTask> placeTasklist = dbHandler.selectPlaceTaskList(placeTaskID);
                           if (placeTasklist == null) {
                               Dlog.d("placeTasklist is null");
                           }
                           if (placeTasklist.size() == 0) {
                               Dlog.d("placeTasklist size is 0");
                           }
                           startService(placeTasklist.get(0));
                       } catch (Exception e) {
                           Dlog.e(e.getMessage());
                           Thread.currentThread().interrupt();
                       }
                   }
               }
               break;
           }
           try {
               Thread.sleep(SLEEP_TIME_MILLISEC);
           } catch (InterruptedException e) {
               Dlog.e(e.getMessage());
               Thread.currentThread().interrupt();
           }
       }
       if(i >= MAX_REPEAT_COUNT) {
           mNotificationManager.notify(NOTIFICATION_ID, getNotificationForForeground(getText(R.string.txt_notify_err)));
       }
    }

    @Override
    public void onCreate() {

        if(mBound == false) {
            bindService(new Intent(this, PlaceTaskGPSCheckService.class), mServiceConnection, BIND_AUTO_CREATE);
            Dlog.d("onCreate 실행됨, 바인드");
        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

       if (serviceIsRunningInForeground(this) == false) {
            Dlog.d("Foregroud 로 서비스가 동작합니다.");
           startForeground(NOTIFICATION_ID, getNotificationForForeground(getText(R.string.txt_notify_background)));
        }

        super.onCreate();

    }


    @Override
    public void onDestroy() {
        Dlog.d("onDestroy() 호출합니다.");
        onStop();
    }

    public void startService( PlaceTask placeTask) {
        if (placeTask == null) {
            Dlog.d("placeTask가 null 입니다.");
            return;
        }
        if(mService == null) {
            Dlog.d("mService가 null 입니다.");
            return;
        }

        // 알람이 있을 경우 오늘이 설정된 요일에 해당하는 지 확인
        if(placeTask.getAlert() != null && placeTask.getAlert().getAlertID() > 0) {
            if(placeTask.getAlert().getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_SCHEDULE) {
                if (isToday(placeTask.getAlert()) == false) {
                    Dlog.d("오늘은 설정된 요일이 아닙니다.");
                    return;
                }
            }
        }

        // 서비스 실행
        if (isServiceRunningCheck()) {             // 서비스 동작 중
            Dlog.d("서비스가 동작중입니다.");
            if (isEmptyWaitingTaskQueue()) {
                Dlog.d("WaitingTaskQueue가 비었습니다. 서비스를 동작 시킵니다.");
                startService();
            }
            addTask(placeTask);
        } else { // 서비스 동작 안함
            Dlog.d("서비스가 동작 안하고 있습니다. 서비스를 동작 시킵니다.");
            addTask(placeTask);
            startService();
        }
    }

    public void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public void startService() {
        if(mService == null) {
            Dlog.d("mService가 null 입니다.");
            return;
        }
        Dlog.d("mService.requestLocationUpdates(); 실행함");
        mService.requestLocationUpdates();
    }


    public boolean isServiceRunningCheck() {
        final String SERVICE_NAME_FOR_CHECKING = "com.toyproject.honeyimleaving.service.PlaceTaskGPSCheckService";
        ActivityManager manager =  (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo runningServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Dlog.d("getClassName() : " + runningServiceInfo.service.getClassName());
            if (SERVICE_NAME_FOR_CHECKING.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyWaitingTaskQueue() {
        if(mService == null) {
            Dlog.d("mService가 null 입니다.");
        }
        return mService.isEmptyWaitingTaskQueue();
    }

    public void addTask(PlaceTask placeTask) {
        mService.addPlaceTask(placeTask);
    }


    private Notification getNotificationForForeground(CharSequence text) {
        Intent intent = new Intent(this, PlaceTaskGPSCheckService.class);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch, getString(R.string.noti_launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.noti_remove_location_updates),
                        servicePendingIntent)
                .setContentText(text)
                .setContentTitle(getString(R.string.noti_title))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_stat_notify_template)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isToday(Alert alert) {
        Calendar calendar =  Calendar.getInstance();
        Dlog.d("금일은 " + Util.getDaysByte(calendar) + " 입니다");
        if ((alert.getRepeatDays() & Util.getDaysByte(calendar)) == Util.getDaysByte(calendar)) {
            return true;
        }
        return false;
    }

}
