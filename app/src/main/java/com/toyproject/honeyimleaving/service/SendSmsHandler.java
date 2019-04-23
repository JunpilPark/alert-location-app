package com.toyproject.honeyimleaving.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.app.Activity;
import android.app.PendingIntent;

import com.toyproject.honeyimleaving.db.MyDBHandlerForSentHistory;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;

import java.util.ArrayList;

/**
 * Created by iamfe on 2018-10-16.
 */

public class SendSmsHandler extends Handler {

    private Context mContext;
    private PlaceTask placeTask;
    public SendSmsHandler(Looper looper, Context context) {
        super(looper);
        this.mContext = context;


    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == 0) {
            placeTask = (PlaceTask) msg.obj;
            if (placeTask != null) {
                try {
                    Dlog.e("Send Msg is 0");
                    sendSMS(placeTask.getTaskID(), placeTask.getMobileNumbersList(), placeTask.getPlaceAlert().getSmsContents());
                } catch (Exception e) {
                    Dlog.e("Exception :" + e.getMessage());
                    if (placeTask == null) return;
                    MyDBHandlerForSentHistory dbSentHistory = new MyDBHandlerForSentHistory(mContext);
                    dbSentHistory.insertSendHistory(placeTask, -9999);
                    dbSentHistory.close();
                }
            }
        }
    }

    private void sendSMS(int id, ArrayList<String> mobile, String message) {
        if(mContext == null) return;

        for(int i = 0 ; i < mobile.size() ; i++) {
            sendSMS(id, mobile.get(i), message);
        }
    }

    private void sendSMS(int id, String phoneNumber, String message) {
        if(mContext == null) return;

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, id, new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, id, new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);

        mContext.registerReceiver(new MySmsBroadcastReceiver(), new IntentFilter(SENT));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        Dlog.d("Send Message: " + phoneNumber + ", " +  message);
    }

    public class MySmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dlog.d("getResultCode : " + getResultCode());
            if (placeTask == null) return;
            MyDBHandlerForSentHistory dbSentHistory = new MyDBHandlerForSentHistory(mContext);
            dbSentHistory.insertSendHistory(placeTask, getResultCode());
            dbSentHistory.close();
        }
    }
}
