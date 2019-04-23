package com.toyproject.honeyimleaving.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.toyproject.honeyimleaving.HoneyImLeaving;

/**
 * Created by iamfe on 2018-10-01.
 */

public class MyDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HoneyImLeaving.db";

    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HoneyImLeaving.DBQuery.QCreateTB_PLACE_ALERT_LIST);
        db.execSQL(HoneyImLeaving.DBQuery.QCreateTB_ALERT_LIST);
        db.execSQL(HoneyImLeaving.DBQuery.QCreateTB_PLACE_TASK_LIST);
        db.execSQL(HoneyImLeaving.DBQuery.QCreateTB_SEND_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HoneyImLeaving.DBQuery.getQueryDropTable(HoneyImLeaving.DBQuery.TB_ALERT_LIST));
        db.execSQL(HoneyImLeaving.DBQuery.getQueryDropTable(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST));
        db.execSQL(HoneyImLeaving.DBQuery.getQueryDropTable(HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST));
        db.execSQL(HoneyImLeaving.DBQuery.getQueryDropTable(HoneyImLeaving.DBQuery.TB_SEND_HISTORY));
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       onUpgrade(db, oldVersion, newVersion);
    }

}
