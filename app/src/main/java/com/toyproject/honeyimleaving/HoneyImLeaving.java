package com.toyproject.honeyimleaving;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.toyproject.honeyimleaving.myutil.Dlog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.ContentValues.TAG;

/**
 * Created by iamfe on 2018-08-22.
 */

public  class HoneyImLeaving {

    public static final float PLACE_RADIUS = 500f;

    public static final class RequestedPermissionCode {
        public static final int ACCESS_PERMISSION_ALL = 9999;
    }

    public static final class DBQuery {
        public static final String TB_PLACE_ALERT_LIST = "TB_PLACE_ALERT_LIST";
        public static final String TB_ALERT_LIST = "TB_ALERT_LIST";
        public static final String TB_PLACE_TASK_LIST = "TB_PLACE_TASK_LIST";
        public static final String TB_SEND_HISTORY = "TB_SEND_HISTORY";

        public static final String QCreateTB_PLACE_ALERT_LIST =
                "CREATE TABLE " + TB_PLACE_ALERT_LIST + " (" +
                        "_id integer PRIMARY KEY autoincrement, " +
                        "place_name text not null, " +
                        "address text, " +
                        "latitude real not null, " +
                        "longitude real not null, " +
                        "sms_contents text );";

        public static final String QCreateTB_ALERT_LIST =
                "CREATE TABLE " + TB_ALERT_LIST + " ( " +
                    "_id integer PRIMARY KEY autoincrement, " +
                    "start_hour integer, " +
                    "start_min integer, " +
                    "finish_hour integer, " +
                    "finish_min integer, " +
                    "repeat_days integer, " +
                    "alert_type integer, " +
                    "repeat_yn text, " +
                    "alert_execute_type integer);";

        public static final String QCreateTB_PLACE_TASK_LIST =
                "CREATE TABLE " + TB_PLACE_TASK_LIST + " ( " +
                        "_id integer PRIMARY KEY autoincrement, " +
                        "place_alert_id integer, " +
                        "alert_id integer, " +
                        "mobile_number text, " +
                        "is_use_yn text, " +
                        "is_alert_me text);";

        public static final String QCreateTB_SEND_HISTORY =
                "CREATE TABLE " + TB_SEND_HISTORY + " ( " +
                        "_id integer PRIMARY KEY autoincrement, " +
                        "send_date text, " +
                        "mobile_number text, " +
                        "place_name text, " +
                        "address, " +
                        "latitude real, " +
                        "longitude real, " +
                        "alert_type integer, " +
                        "sms_contents text," +
                        "state_code integer );";

        public static String getQueryDropTable(String table) {
            return "DROP TABLE " + table  + " ;";
        }
    }
}
