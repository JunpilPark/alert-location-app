package com.toyproject.honeyimleaving.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.app.ActivityManager;
import android.support.annotation.NonNull;
import android.app.ActivityManager.*;

import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;

/**
 * Created by iamfe on 2018-10-10.
 */

public class MyServiceHandler {
    private PlaceTaskGPSCheckService mService = null;
    private boolean mBound = false;
    private Context mContext;

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

    public MyServiceHandler(@NonNull Context context) {
        mContext = context;
    }


    public ServiceConnection getServiceConnection() {
        return mServiceConnection;
    }

    public void onStop() {
        if (mBound) {
            mContext.unbindService(mServiceConnection);
            mBound = false;
            Dlog.d("unbinding 함");
        }
    }

    public boolean BindService(@NonNull Intent intent,@NonNull int mode) {
        if (mServiceConnection == null) return false;
        return mContext.bindService(intent, mServiceConnection, mode);
    }

    public boolean isServiceRunningCheck() {
        final String SERVICE_NAME_FOR_CHECKING = "com.toyproject.honeyimleaving.service.PlaceTaskGPSCheckService";
        ActivityManager manager =  (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);

        for(RunningServiceInfo runningServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Dlog.d("getClassName() : " + runningServiceInfo.service.getClassName());
            if (SERVICE_NAME_FOR_CHECKING.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyWaitingTaskQueue() {
        return mService.isEmptyWaitingTaskQueue();
    }

    public void addTask(PlaceTask placeTask) {
        mService.addPlaceTask(placeTask);
    }

    public void removeTask(PlaceTask placeTask) {
        mService.removePlaceTask(Integer.valueOf(placeTask.getTaskID()));
    }
    public void startService() {
        if(mService == null) {
            Dlog.d("mService가 null 입니다.");
            return;
        }
        Dlog.d("mService.requestLocationUpdates(); 실행함");
        mService.requestLocationUpdates();
        //mService.startForground(); 이거 활성화 할지 조금만 생각해보자.
    }

}