package com.toyproject.honeyimleaving.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.toyproject.honeyimleaving.HoneyImLeaving;
import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.activity.AlertActivity;
import com.toyproject.honeyimleaving.activity.SplashActivity;
import com.toyproject.honeyimleaving.db.MyDBHandler;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

public class PlaceTaskGPSCheckService extends Service {

    private static final String PACKAGE_NAME = " com.toyproject.honeyimleaving";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".notification";
    private static final String CHANNEL_ID = "noti_channel_01";
    private static final int NOTIFICATION_ID = 85854954;
    private static final int NOTIFICATION_RESULT_ID = 85854955;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private FusedLocationProviderClient mFusedLocationClient;
    private Queue<PlaceTask> mWaitingTaskQueue;

    private Looper mServiceLooper;

    private final IBinder mBinder = new LocalBinder();
    private ServiceHandler mServiceHandler;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private NotificationManager mNotificationManager;

    private SendSmsHandler mSendSmsHandler;
    private MyDBHandler mDBHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            Dlog.d("handleMessage");
            if(mWaitingTaskQueue == null || mWaitingTaskQueue.isEmpty() == true) {
                Dlog.d("서비스 종료");
                removeLocationUpdates();
            }
            else {
                for(int i = mWaitingTaskQueue.size() ; i > 0 && mWaitingTaskQueue.isEmpty() == false ; i--) {
                    PlaceTask temp =  mWaitingTaskQueue.poll();

                    switch (temp.getAlertType()) {
                        case Alert.ALERT_TYPE_OUT_REGION:
                            Dlog.d("OUT 타입입니다.");
                            if (timeCompareIsOverToCurrenTime(temp.getAlert()) == true) {
                                Dlog.d(temp.getPlaceAlert().getPlaceName() + "이 시간이 종료되어 끝납니다.");
                                mainNotifyUpdate();
                                break;
                            } else {
                                if (distanceCompare(temp.getPlaceAlert()) == true) {
                                    Dlog.d(temp.getPlaceAlert().getPlaceName() + " : 거리 확인 후 알림 실행됩니다.");
                                    executeAlert(temp);
                                } else {
                                    mWaitingTaskQueue.add(temp);
                                    Dlog.d(temp.getPlaceAlert().getPlaceName() + " : 거리 확인 후 Queue에 다시 들어갑니다.");
                                }
                            }
                            break;
                        case Alert.ALERT_TYPE_IN_REGION:
                            Dlog.d("IN 타입입니다.");
                            if (timeCompareIsOverToCurrenTime(temp.getAlert()) == true) {
                                Dlog.d(temp.getPlaceAlert().getPlaceName() + "이 시간이 종료되어 끝납니다.");
                                mainNotifyUpdate();
                                break;
                            } else {
                                if (distanceCompare(temp.getPlaceAlert()) == false) {
                                    Dlog.d(temp.getPlaceAlert().getPlaceName() + " : 거리 확인 후 알림 실행됩니다.");
                                    executeAlert(temp);
                                } else {
                                    mWaitingTaskQueue.add(temp);
                                    Dlog.d(temp.getPlaceAlert().getPlaceName() + " : 거리 확인 후 Queue에 다시 들어갑니다.");
                                }
                            }
                            break;
                        default:
                            break;
                    }

                }
            }
       }
    }

    // 모바일 번호 리스트가 null 이 아니고, 리스트가 존재할 때만 SMS 전송됨.
    // alertMe 가 설정되어 있는 경우 Activity 호출 후 진동 발생
    private void executeAlert(PlaceTask temp) {
        if(mNotificationManager == null) return;
        if(mSendSmsHandler == null) return;

        if(temp.getAlert().getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_NOW) {
            if(mDBHandler!=null) mDBHandler.updateUseYN(temp.getTaskID(), false);
        }

        mNotificationManager.notify(NOTIFICATION_RESULT_ID, getNotification(temp));
        if(temp.getMobileNumbersList() != null && temp.getMobileNumbersList().size() >0) {
            Dlog.d("Send message!!!!!!!!!!!!! " );
            mSendSmsHandler.sendMessage(mSendSmsHandler.obtainMessage(0, temp));
        }

        if(temp.isAlertMe() == true) {
            Dlog.d("Alert Me!!!!!!!!!!!!!!");
            Intent intent = new Intent(this, AlertActivity.class);
            intent.putExtra("placeTask", temp);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        mainNotifyUpdate();
    }
    // true 이면 시간이 도달한 것
    private boolean timeCompareIsOverToCurrenTime(Alert alert) {
        if(alert.getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_NOW) return false;

        final int  finishTimeMin =  (alert.getFinishTime()[Alert.HOUR_INDEX]*60) + alert.getFinishTime()[Alert.MIN_INDEX];

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat curHourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat curMinFormat = new SimpleDateFormat("mm");

        int curTimeMin = (Integer.parseInt(curHourFormat.format(date)) * 60) +  Integer.parseInt(curMinFormat.format(date));

        Dlog.d("시스템 시간 : " + curHourFormat.format(date) + "시 " + curMinFormat.format(date) + " 분");
        Dlog.d("Alert 시간 : " + Integer.toString(alert.getFinishTime()[Alert.HOUR_INDEX]) + "시 " +
                Integer.toString(alert.getFinishTime()[Alert.MIN_INDEX])  + " 분");

        if(finishTimeMin <= curTimeMin) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean distanceCompare(PlaceAlert place) {
        Location location = new Location("place");
        location.setLatitude(place.getLatitude());
        location.setLongitude(place.getLongitude());
        float distance = mCurrentLocation.distanceTo(location);
        Dlog.d("현재 위치와 " + place.getPlaceName() + "의 거리 차이는 : " + distance + "m 입니다.");
        if(distance >= HoneyImLeaving.PLACE_RADIUS) {
            return true;
        }
        return false;
    }


    @Override
    public void onCreate() {
        Dlog.d("service Create 됨.");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(mWaitingTaskQueue == null) mWaitingTaskQueue = new ArrayDeque<PlaceTask>();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onNewLocation(locationResult.getLastLocation());
                    Message msg = mServiceHandler.obtainMessage();
                    mServiceHandler.sendMessage(msg);
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread thread = new HandlerThread("PlaceTaskGPSCheckService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        HandlerThread threadSendSms = new HandlerThread("SendSmsThreadInGpsCheckService",
                Process.THREAD_PRIORITY_BACKGROUND);
        threadSendSms.start();

        mSendSmsHandler = new SendSmsHandler(threadSendSms.getLooper(), this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mDBHandler = new MyDBHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dlog.d("onStartCommand 가 실행됩니다.");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);
        if (startedFromNotification) {
            Dlog.d("서비스가 중지됩니다.");
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Dlog.d("bind 되었습니다.");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Dlog.d("reBind 되었습니다.");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Dlog.d("unBind 되었습니다.");
        if (serviceIsRunningInForeground(this) == false) {
            Dlog.d("Foregroud 로 서비스가 동작합니다.");
            startForeground(NOTIFICATION_ID, getNotificationForForeground());
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Dlog.d("onDestroy 되었습니다. -------");
        while ( mWaitingTaskQueue.isEmpty() == false) {
            Dlog.d("Queue가 비어 있지 않습니다. 지금 바로 시작은  off 처리");
            PlaceTask temp =  mWaitingTaskQueue.poll();
            if(temp.getAlert().getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_NOW) {
                 mDBHandler.updateUseYN(temp.getTaskID(), false);
                Dlog.d(temp.getPlaceAlert().getPlaceName() + "off 함");
            }
        }
        mDBHandler.close();
        mServiceHandler.removeCallbacksAndMessages(null);
        mSendSmsHandler.removeCallbacksAndMessages(null);
        Dlog.d("onDestroy 되었습니다. -------");
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mCurrentLocation = task.getResult();
                            } else {
                                Dlog.e("Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Dlog.e("Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Dlog.d("New location: " + location);

        mCurrentLocation = location;
    }

    public void requestLocationUpdates() {
        Dlog.d("Requesting location updates");
       startService(new Intent(getApplicationContext(), PlaceTaskGPSCheckService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, mServiceLooper);
        } catch (SecurityException unlikely) {
            Dlog.e("Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void removeLocationUpdates() {
        Dlog.d("Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Dlog.e("Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    public class LocalBinder extends Binder {
        public PlaceTaskGPSCheckService getService() {
            return PlaceTaskGPSCheckService.this;
        }
    }

    public boolean addPlaceTask(PlaceTask placeTask) {
        if(mWaitingTaskQueue == null) {
            Dlog.d("mWaitingTaskQueue 가 null 입니다.");
            return false;
        }

        Dlog.d("Task가 mWaitingTaskQueue 에 추가되었습니다.");
        mWaitingTaskQueue.add(placeTask);
        mainNotifyUpdate();
        return true;
    }

    public boolean removePlaceTask(Integer id) {
        if(mWaitingTaskQueue == null) {
            Dlog.d("mWaitingTaskQueue 가 null 입니다.");
            return false;
        }

        if(mWaitingTaskQueue.isEmpty() == true ) {
            Dlog.d("mWaitingTaskQueue 가 비어 있습니다.");
            return false;
        }

        for (int i = mWaitingTaskQueue.size() ; i > 0 && mWaitingTaskQueue.isEmpty() == false  ; i--) {
            PlaceTask temp  = mWaitingTaskQueue.poll();
            if(temp.getTaskID() == id) {
                mainNotifyUpdate();
                return true;
            }
            else {
                mWaitingTaskQueue.add(temp);
            }
        }
        Dlog.d("mPlaceTaskJobList에 해당 ID를 가진 PlaceTask가 없습니다.");
        return false;
    }

    private Notification getNotification(PlaceTask placeTask) {
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);

        String text;
        if(placeTask.getAlertType() == Alert.ALERT_TYPE_IN_REGION) {
            text  = placeTask.getPlaceAlert().getPlaceName() + "에 진입했습니다.";
        }
        else {
            text = placeTask.getPlaceAlert().getPlaceName() + "에서 벗어났습니다.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notify_template)
                .setContentTitle(getString(R.string.noti_title))
                .setContentText(text);

        builder.setContentIntent(activityPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    private Notification getNotificationForForeground() {
        Intent intent = new Intent(this, PlaceTaskGPSCheckService.class);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        CharSequence text;
        if(mCurrentLocation != null && mWaitingTaskQueue != null) {
            text = "현재위치의 확인 중인 위치알람은 " + mWaitingTaskQueue.size() + "건 있습니다.";

        }
        else {
            text = "현재위치 확인 중입니다. ";
        }


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
                .setPriority(Notification.PRIORITY_HIGH)
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

    public boolean isEmptyWaitingTaskQueue() {
        if(mWaitingTaskQueue != null) {
            return mWaitingTaskQueue.isEmpty();
        }
        else
        {
            return true;
        }
    }
    private void mainNotifyUpdate() {
        if(mNotificationManager == null) return;

        if (serviceIsRunningInForeground(this) == true) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotificationForForeground());
        }
    }

    public void startForground() {
        if (serviceIsRunningInForeground(this) == false) {
            Dlog.d("Foregroud 로 서비스가 동작합니다.");
            startForeground(NOTIFICATION_ID, getNotificationForForeground());
        }
    }
}
