package com.toyproject.honeyimleaving.myutil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.toyproject.honeyimleaving.model.Alert;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by iamfe on 2018-10-06.
 */

public class Util {
    public static String changeTimeToString(int hour, int min) {
        String AM = "오전";
        String PM = "오후";

        StringBuffer timeString = new StringBuffer();

        if(hour < 12) {
            timeString.append(AM + " " + (hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour)) + " : "
                    + (min < 10 ? "0" + Integer.toString(min) : Integer.toString(min)));
        }
        else {
            hour = hour % 12;
            timeString.append(PM + " " +  (hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour))  + " : "
                    + (min < 10 ? "0" + Integer.toString(min) : Integer.toString(min)));
        }
        return timeString.toString();
    }

    // Calender의 현재 요일을 Alert 기준의 바이트로 변경
    public static int getDaysByte(Calendar cal) {
        int daySytem = cal.get(Calendar.DAY_OF_WEEK);
        if(daySytem - 2 < 0) {
            return Alert.SUNDAY;
        }
        return Alert.MONDAY << (daySytem -2);
    }

    public static String getMobileString(ArrayList<String> mobileArrList) {
        if(mobileArrList == null) return null;
        String mobileNum = "";
        for(int i = 0 ; i < mobileArrList.size() ; i++) {
            if(i == 0) {
                mobileNum = mobileArrList.get(i);
            }
            else {
                mobileNum += ("," + mobileArrList.get(i));
            }
        }
        return mobileNum;
    }

    public static ArrayList<String> getMobileArrList(String mobile) {
        if(mobile == null) return null;
        if(mobile.length() == 0) return null;

        ArrayList<String> mobileArrList = new ArrayList<>();
        String[] splitedStr = mobile.split(",");
        for(int i = 0 ; i < splitedStr.length ; i ++) {
            mobileArrList.add(splitedStr[i].trim());
        }
        return mobileArrList;
    }


    public static String checkNullForQuery(String target) {
        if(target == null) return "null";

        if(target.length() == 0) return "null";

        return "'" + target.trim() + "'";
    }

    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch(PackageManager.NameNotFoundException e) { }

        return version;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            String temp = str.trim();
            temp = temp.replace("-","");
            double d = Double.parseDouble(temp);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
