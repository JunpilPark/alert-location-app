package com.toyproject.honeyimleaving.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.toyproject.honeyimleaving.HoneyImLeaving;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.model.SendHistory;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import java.util.ArrayList;

/**
 * Created by iamfe on 2018-10-17.
 */

public class MyDBHandlerForSentHistory {
    private MyDBHelper mDBhelper;

    public MyDBHandlerForSentHistory(Context context) {
        mDBhelper = new MyDBHelper(context);
    }

    public void close() {
        mDBhelper.close();
    }

    public Cursor selectSentHistoryCursorAll() {
        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        String query = "SELECT _id, " +  "send_date, " +
                            "mobile_number, " +  "place_name, " +
                            "address, " + "latitude, " +
                            "longitude, "  + "alert_type, " +
                            "sms_contents, " + "state_code " +
                            "FROM " + HoneyImLeaving.DBQuery.TB_SEND_HISTORY + " " +
                            "ORDER BY send_date DESC";

        Dlog.d("query : " + query);
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public ArrayList<SendHistory> selectSentHistoryAll() {
        Cursor  cursor = selectSentHistoryCursorAll();
        if(cursor == null) return null;

        ArrayList<SendHistory> sentHistoryArr = new ArrayList<>();
        while (cursor.moveToNext()) {
            SendHistory temp =  new SendHistory();
            temp.setSendDate(cursor.getString(cursor.getColumnIndex("send_date")));
        }
        return sentHistoryArr;
    }

    public boolean insertSendHistory(PlaceTask placeTask, int resultCode) {
        if(mDBhelper == null) return false;
        try {
            SQLiteDatabase db = mDBhelper.getWritableDatabase();

            String query = "INSERT INTO " + HoneyImLeaving.DBQuery.TB_SEND_HISTORY + " " +
                    "(send_date, mobile_number, place_name, address, latitude, longitude, " +
                    "alert_type, sms_contents, state_code) " +
                    "VALUES (datetime('now', 'localtime'), " + Util.checkNullForQuery(Util.getMobileString(placeTask.getMobileNumbersList())) + ", " +
                    Util.checkNullForQuery(placeTask.getPlaceAlert().getPlaceName()) + ", " + Util.checkNullForQuery(placeTask.getPlaceAlert().getAddress()) + ", " +
                    placeTask.getPlaceAlert().getLatitude() + ", " + placeTask.getPlaceAlert().getLongitude() + ", " +
                    placeTask.getAlertType() + ", " + Util.checkNullForQuery(placeTask.getPlaceAlert().getSmsContents()) + ", " + resultCode + ")";

            Dlog.d("query : " + query);
            db.execSQL(query);
            db.close();
            return true;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSendHistoryAll() {
        if(mDBhelper == null) return false;
        try {
            SQLiteDatabase db = mDBhelper.getWritableDatabase();
            String query = "DELETE FROM " + HoneyImLeaving.DBQuery.TB_SEND_HISTORY ;
            Dlog.d("query : " + query);
            db.execSQL(query);
            return true;
        }
        catch(SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return false;
        }
    }
}
