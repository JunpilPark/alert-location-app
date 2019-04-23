package com.toyproject.honeyimleaving.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.toyproject.honeyimleaving.HoneyImLeaving;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import java.util.ArrayList;

public class MyDBHandler {
    private MyDBHelper mDBhelper;

    public MyDBHandler(Context context) {
        mDBhelper = new MyDBHelper(context);
    }

    public int insertPlaceTask(PlaceTask placeTask) {
        if(mDBhelper == null) return -1;
        Dlog.d("insertPlaceTask 의 실행 : " + placeTask.toString());

        int placeAlertKey;
        int placeTaskKey;
        int alertKey;

        placeAlertKey = insertPlaceAlert(placeTask.getPlaceAlert());
        if(placeAlertKey == -1) { Dlog.d("placeAlertKey is -1"); return -1;}

        alertKey = insertAlert(placeTask.getAlert());
        if (alertKey == -1) {
            deleteData(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST, placeAlertKey);
            Dlog.d("alertKey is -1");
            return -1;
        }
        placeTaskKey = insertPlaceTask(placeTask.getTaskID(), placeAlertKey, alertKey, placeTask.getMobileNumbersList(), placeTask.isUseYN(), placeTask.isAlertMe());

        if(placeTaskKey == -1) {
            deleteData(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST, placeAlertKey);
            deleteData(HoneyImLeaving.DBQuery.TB_ALERT_LIST, alertKey);
        }
        Dlog.d("리턴 : " + placeTaskKey);

        return placeTaskKey;
    }

    public boolean deletePlaceTask(@NonNull PlaceTask placeTask) {

        if(placeTask.getPlaceAlert() !=null) {
            deleteData(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST, placeTask.getPlaceAlert().getPlaceAlertID());
        }
        if(placeTask.getAlert() != null) {
            deleteData(HoneyImLeaving.DBQuery.TB_ALERT_LIST, placeTask.getAlert().getAlertID());
        }
        deleteData(HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST, placeTask.getTaskID());
        return true;
    }

    /**
     * placeTaskID 가 -1 이면 모두 조회, placeTaskID 가 있으면 해당 PlaceTaskID 만 조회
     * @param placeTaskID
     * @return
     */
    private Cursor selectPlaceTaskCursor(int placeTaskID) {
        if(mDBhelper == null) return null;
        Dlog.d("selectPlaceTask 의 실행 : " + Integer.toString(placeTaskID));
        String query;
        query = "SELECT A._id AS place_task_id, " +
                "B._id AS place_alert_id, " +
                "B.place_name AS place_name, " +
                "B.address AS address, " +
                "C.alert_type AS alert_type, " +
                "B.latitude AS latitude, " +
                "B.longitude AS longitude, " +
                "B.sms_contents AS sms_contents," +
                "A.mobile_number AS mobile_number," +
                "A.alert_id AS alert_id," +
                "C.start_hour AS start_hour, " +
                "C.start_min AS start_min, " +
                "C.finish_hour AS finish_hour, " +
                "C.finish_min AS finish_min, " +
                "C.repeat_days AS repeat_days, " +
                "C.repeat_yn AS repeat_yn, " +
                "A.is_use_yn AS is_use_yn, " +
                "A.is_alert_me AS is_alert_me, " +
                "C.alert_execute_type AS alert_execute_type " +
                "FROM " + HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST + " AS A JOIN " + HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST + " AS B ON A.place_alert_id = B._id " +
                "JOIN " + HoneyImLeaving.DBQuery.TB_ALERT_LIST + " AS C ON A.alert_id = C._id" ;

        if(placeTaskID != -1) {
            query += " WHERE A._id = " + placeTaskID ;
        }

        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        Dlog.d("query : " + query);
        return db.rawQuery(query, null);
    }

    public ArrayList<PlaceTask> selectPlaceTaskList(int placeTaskID) {
        Cursor cursor = selectPlaceTaskCursor(placeTaskID);

        if(cursor == null) return null;
        if(cursor.getCount() == 0) return null;

        ArrayList<PlaceTask> placeTaskArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            PlaceTask placeTask;

            PlaceAlert.Builder builderPlaceAlert = new PlaceAlert.Builder(
                    cursor.getInt(cursor.getColumnIndex("place_alert_id")),
                    cursor.getString(cursor.getColumnIndex("place_name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")))
                    .setAddress(cursor.getString(cursor.getColumnIndex("address")))
                    .setSmsContents(cursor.getString(cursor.getColumnIndex("sms_contents")));

            Alert.Builder builderAlert = new Alert.Builder(
                    cursor.getInt(cursor.getColumnIndex("alert_id")),
                    new int[]{cursor.getInt(cursor.getColumnIndex("start_hour")), cursor.getInt(cursor.getColumnIndex("start_min"))},
                    new int[]{cursor.getInt(cursor.getColumnIndex("finish_hour")), cursor.getInt(cursor.getColumnIndex("finish_min"))},
                    cursor.getInt(cursor.getColumnIndex("alert_type")),
                    cursor.getInt(cursor.getColumnIndex("alert_execute_type")))
                    .addRepeatDays((byte)cursor.getInt(cursor.getColumnIndex("repeat_days")));

            placeTask = new PlaceTask(cursor.getInt(cursor.getColumnIndex("place_task_id")), new PlaceAlert(builderPlaceAlert), new Alert(builderAlert));
            placeTask.addMobileNumber(cursor.getString(cursor.getColumnIndex("mobile_number")));
            placeTask.setAlertMe(cursor.getString(cursor.getColumnIndex("is_alert_me")));

            if(cursor.getString(cursor.getColumnIndex("is_use_yn")).equals("Y")) {
                placeTask.setUseYN(true);
            }
            else {
                placeTask.setUseYN(false);
            }

            placeTaskArrayList.add(placeTask);
        }
        return placeTaskArrayList;
    }

    // PlaceTask 테이블에 DATA를 INSERT 하는 함수
    private int insertPlaceTask(int placeTaskID, int placeAlertKey, int alertKey, ArrayList mobileNumber, boolean isUseYn, boolean isAlertMe) {
        Dlog.d("insertPlaceTask(int placeTaskID, int placeAlertKey, int alertKey, ArrayList mobileNumber, boolean isUseYn, boolean isAlertMe) 의 실행 : " + placeTaskID + ", " + placeAlertKey + ", " + alertKey);

        if(mDBhelper == null) return -1;
        if(getRowCount(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST, placeAlertKey) <= 0) return -1;
        if(getRowCount(HoneyImLeaving.DBQuery.TB_ALERT_LIST, alertKey) <= 0) return -1;

        int id;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        String query = "INSERT INTO " + HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST + " (_id, place_alert_id, alert_id, mobile_number, is_use_yn, is_alert_me) ";
        if(placeTaskID <=0 ) {
            id =  getID(HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST);
        }
        else {
            id =  placeTaskID;
        }
        query += " VALUES (" + id + ", " + placeAlertKey + ", " + alertKey + ", " +  Util.checkNullForQuery(Util.getMobileString(mobileNumber)) + ", " + (isUseYn == true ? "'Y'" : "'N'") + ", " + (isAlertMe == true ? "'Y'" : "'N'") + ");";
        Dlog.d("query : " + query);

        db.execSQL(query);
        db.close();
        return id;
    }

    /**
     * TB_ALERT_LIST 에 DATA를 Insert 하는 함수
     * @param alert
     * @return TB_ALERT_LIST 의 Key로 쓰이는 id, 실패 시 -1
     */
    private int insertAlert(Alert alert) {
        Dlog.d("insertAlert 의 실행 : " + alert.toString());

        if(mDBhelper == null) return -1;
        if(alert == null ) return -1;

        int id = -1;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        String query = "INSERT INTO " + HoneyImLeaving.DBQuery.TB_ALERT_LIST + " (_id, start_hour, start_min, finish_hour, finish_min, repeat_days, alert_type, repeat_yn, alert_execute_type) ";

        try{
            if(alert.getAlertID() <= 0) {
                id =  getID(HoneyImLeaving.DBQuery.TB_ALERT_LIST);
            }
            else {
                id =  alert.getAlertID();
            }
            query += " VALUES (" + id + ", " + alert.getStartTime()[Alert.HOUR_INDEX] + ", " + alert.getStartTime()[Alert.MIN_INDEX] + ", "
                                    + alert.getFinishTime()[Alert.HOUR_INDEX] + ", " + alert.getFinishTime()[Alert.MIN_INDEX] + ", "
                                    + alert.getRepeatDays() + ", " + alert.getAlertType() + ", "
                                    + Util.checkNullForQuery(alert.getRepeatWeekYN()) + ", " + alert.getAlertExecuteType() + ");";
            Dlog.d("query : " + query);

            db.execSQL(query);
            db.close();
            return id;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return -1;
        }
    }

    /**
     * TB_PLACE_ALERT_LIST 에 데이터를 Insert 하는 함수
     * @param placeAlert
     * @return Insert 성공 시 primary key인 id 반환, 실패의 경우 -1 리턴
     */
    private int insertPlaceAlert(PlaceAlert placeAlert) {
        Dlog.d("insertPlaceAlert 의 실행 : " + placeAlert.toString());

        if(mDBhelper == null) return -1;
        if(placeAlert == null) return -1;

        int id = -1;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        String query = "INSERT INTO " + HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST + " (_id, place_name, address, latitude, longitude, sms_contents) " ;
        try {
            if(placeAlert.getPlaceAlertID() <= 0) {
                id =  getID(HoneyImLeaving.DBQuery.TB_PLACE_ALERT_LIST);
            }
            else {
                id =   placeAlert.getPlaceAlertID();
            }
            query +=  "VALUES (" + id + ", " + Util.checkNullForQuery(placeAlert.getPlaceName()) + ", " + Util.checkNullForQuery(placeAlert.getAddress()) + ", " + placeAlert.getLatitude() + ", " +
                    placeAlert.getLongitude() + ", "  + Util.checkNullForQuery(placeAlert.getSmsContents()) +");";

            Dlog.d("query : " + query);
            db.execSQL(query);
            db.close();
            return id;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return -1;
        }
    }

    private boolean deleteData(String table, int id) {
        if(mDBhelper == null) return false;
        if(table == null) return false;

        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        try {
            String query = "DELETE FROM " + table + " WHERE _id = " + id + ";";
            Dlog.d("query : " + query);
            db.execSQL(query);
            return true;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return false;
        }
    }

    /**
     * 입력되는 테이블의 Primary KEY를 반환하는 함수
     * 참고로, 이 어플에서 이용되는 모든 테이블의 Primary Key 는 INT형으로 순차적으로 부여됨
     * @param table
     * @return 입력되는 테이블의 Primary KEY [MAX(ID) +1], 실패 시 -1
     */
    private int getID(String table) {
        if(mDBhelper == null) return -1;

        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        int id = -1;
        try {
            String query = "SELECT MAX(_id) FROM " + table + ";";
            Dlog.d("query : " + query);
            Cursor cursor = db.rawQuery(query, null);
            Dlog.d("query count : " + cursor.getCount() );
            if(cursor.getCount() == 0) {
                Dlog.d("ID == 0");
                id = 0;
            }
            else {
                while (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                    Dlog.d("ID == " + id);
                }
            }
            return id + 1;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return -1;
        }
    }

    private int getRowCount (String table, int id) {
        if(mDBhelper == null) return -1;

        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        int rowCount = -1;
        try {
            String query = "SELECT _id FROM " + table + " WHERE _id = " + id + ";";
            Dlog.d("query : " + query);
            Cursor cursor = db.rawQuery(query, null);

            return cursor.getCount();
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return -1;
        }
    }
    public boolean updateUseYN (int id, boolean isChecked) {
        if(mDBhelper == null) return false;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        try {
            String query = "UPDATE " + HoneyImLeaving.DBQuery.TB_PLACE_TASK_LIST +
                    " SET is_use_yn = " + (isChecked == true ? "'Y'" : "'N'") +
                    " WHERE _id = " + id + ";";
            Dlog.d("query : " + query);
            db.execSQL(query);
            return true;
        }
        catch (SQLException e) {
            Dlog.d("SQLException : " + e.getMessage());
            return false;
        }
    }

    public void close() {
        mDBhelper.close();
    }
}
