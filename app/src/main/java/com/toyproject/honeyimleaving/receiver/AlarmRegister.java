package com.toyproject.honeyimleaving.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;
import com.toyproject.honeyimleaving.service.HandlerServcie;
import android.app.AlarmManager;
import android.app.PendingIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by iamfe on 2018-10-10.
 */

public class AlarmRegister {

    private PlaceTask mPlaceTask;

    public AlarmRegister() {

    }

    public void addAlarm(@NonNull PlaceTask placeTask) {
        this.mPlaceTask = placeTask;
    }

    /**
     * 현재시스템과 PlaceTask 의 시간을 비교하여, PlaceTask의 동작 활성화 시간 내에 있는 지 확인하는 함수
     * Alert 객체가 NULL 일 경우 true 리턴 (바로 서비스 실행)
     * @return true : 동작조건 만족함, 위치 추적 서비스 실행, false : 현재 시간은 동작조건 밖에 있음. 알람등록
     */
    private boolean compareAlarmConditionAndSystemTime() {
        Alert alert = mPlaceTask.getAlert();
        long now = System.currentTimeMillis();
        Calendar calendar =  Calendar.getInstance();
        SimpleDateFormat sdfNowHour = new SimpleDateFormat("HH");
        SimpleDateFormat sdfNowMin = new SimpleDateFormat("mm");

        int systemTotalMin = (Integer.parseInt(sdfNowHour.format(now)) * 60) +  Integer.parseInt(sdfNowMin.format(now));
        int conditionStartTimeTotalMin = (alert.getStartTime()[Alert.HOUR_INDEX]* 60) + alert.getStartTime()[Alert.MIN_INDEX];
        int conditionFinishTimeTotalMin =  (alert.getFinishTime()[Alert.HOUR_INDEX]* 60) + alert.getFinishTime()[Alert.MIN_INDEX];

        if ((alert.getRepeatDays() & Util.getDaysByte(calendar)) == Util.getDaysByte(calendar)) {
            if ((conditionStartTimeTotalMin <= systemTotalMin) &&
                    (systemTotalMin < conditionFinishTimeTotalMin)) {
                return true;
            }
        }
        return false;
    }

    public void registAlarmOrStartService(@NonNull Context context) {
        if(mPlaceTask == null || mPlaceTask.getAlert() == null ){
            Dlog.d("mPlaceTask 또는 mPlaceTask.getAlert() 이 null 입니다.");
            return;
        }
        if(mPlaceTask.isUseYN() == false) {
            Dlog.d("mPlaceTask 가 사용 안함 입니다.");
            return;
        }
        if(mPlaceTask.getAlert().getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_NOW) {
            Dlog.d("바로 서비스 실행~!!!!!!!!!!!!");
            Intent intent = new Intent(context, HandlerServcie.class);
            intent.putExtra("PlaceTask",mPlaceTask);
            intent.putExtra("place_task_id",mPlaceTask.getTaskID());
            context.startService(intent);
        }
        else {
            if (compareAlarmConditionAndSystemTime()) {
                // 서비스 실행
                Dlog.d("서비스 실행~!!!!!!!!!!!!!!!");
                Intent intent = new Intent(context, HandlerServcie.class);
                intent.putExtra("PlaceTask", mPlaceTask);
                intent.putExtra("place_task_id", mPlaceTask.getTaskID());
                context.startService(intent);
            }
            // 리시버에 등록
            Dlog.d("리시버 등록");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, mPlaceTask.getAlert().getStartTime()[Alert.HOUR_INDEX]);
            calendar.set(Calendar.MINUTE, mPlaceTask.getAlert().getStartTime()[Alert.MIN_INDEX]);
            calendar.set(Calendar.SECOND, 0);
            setAlarm(context, calendar);
        }
    }

    public void removeAlarm(Context context) {
        if(mPlaceTask == null) {
            Dlog.d("mPlaceTask가 null 입니다.");
            return;
        }
        if(mPlaceTask.getAlert().getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_NOW) {
            Dlog.d("mPlaceTask가 바로실행형 입니다. 알람을 해제할 필요없습니다.");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, HandlerServcie.class);
        intent.putExtra("place_task_id", mPlaceTask.getTaskID());

        PendingIntent pIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pIntent = PendingIntent.getForegroundService(context, mPlaceTask.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pIntent = PendingIntent.getService(context, mPlaceTask.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        alarmManager.cancel(pIntent);
        pIntent.cancel();
        Dlog.d("알람취소함 - PlaceTaskID: " + mPlaceTask.getTaskID());
    }

    private void setAlarm(Context context, Calendar calendar) {
        Dlog.d("알람등록함 " + mPlaceTask.getAlert().getStartTime()[Alert.HOUR_INDEX] + " : " + mPlaceTask.getAlert().getStartTime()[Alert.MIN_INDEX]);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, HandlerServcie.class);
        intent.putExtra("place_task_id", mPlaceTask.getTaskID());

        PendingIntent pIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pIntent = PendingIntent.getForegroundService(context, mPlaceTask.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pIntent = PendingIntent.getService(context, mPlaceTask.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        int interval = 1000 * 60 * 60 * 24; // 24Hour
        long nowTime = System.currentTimeMillis();
        long reserveTime = calendar.getTimeInMillis();

        if(nowTime > calendar.getTimeInMillis()){
            reserveTime += interval;
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reserveTime, interval, pIntent);

    }

}
