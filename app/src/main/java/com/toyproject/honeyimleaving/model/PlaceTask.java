package com.toyproject.honeyimleaving.model;

import com.toyproject.honeyimleaving.fragment.FragmentReturnInterface;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by iamfe on 2018-09-11.
 */

public class PlaceTask implements Serializable {

    private int mTaskID;
    private PlaceAlert mPlaceAlert;
    private Alert mAlert;
    private ArrayList<String> mMobileNumbersList;
    private String isUseYN;
    private String isAlertMe;

    public PlaceTask(int id, PlaceAlert placeAlert, Alert alert) {
        this.mTaskID = id;
        this.mPlaceAlert = placeAlert;
        this.mAlert = alert;
        this.isUseYN = "Y";
        this.isAlertMe = "N";
    }

    public PlaceTask(int id, PlaceAlert placeAlert, Alert alert, ArrayList mobileList) {
        this.mTaskID = id;
        this.mPlaceAlert = placeAlert;
        this.mAlert = alert;
        this.mMobileNumbersList = mobileList;
        this.isUseYN = "Y";
        this.isAlertMe = "N";
    }

    public PlaceTask(int id, PlaceAlert placeAlert) {
        this.mTaskID = id;
        this.mPlaceAlert = placeAlert;
        this.mAlert = null;
        this.isUseYN = "Y";
        this.isAlertMe = "N";
    }

    public PlaceTask(int id, Alert alert) {
        this.mTaskID = id;
        this.mPlaceAlert = null;
        this.mAlert = alert;
        this.isUseYN = "Y";
        this.isAlertMe = "N";
    }

    public PlaceTask(int id, FragmentReturnInterface fragmentReturnInterface) {
        this.mTaskID = id;
        this.isUseYN = "Y";
        this.isAlertMe = "N";
        setPlaceTaskObj(fragmentReturnInterface);
    }

    public void setPlaceTaskObj(FragmentReturnInterface fragmentReturnInterface) {
        Object obj = fragmentReturnInterface.getFragementReturn();
        if(obj == null) {
        //    this.mPlaceAlert.setAlertType(PlaceAlert.ALERT_TYPE_IN_REGION);
            return;
        }

        if(obj instanceof PlaceAlert) {
            this.mPlaceAlert =  (PlaceAlert) obj;
        }

        if(obj instanceof Alert) {
            this.mAlert = (Alert) obj;
        }

        if(obj instanceof ArrayList) {
            ArrayList<String> temp =  (ArrayList<String>) obj;
            this.isAlertMe = temp.get(0);
            temp.remove(0);
            this.setSmsContents(temp.get(0));
            temp.remove(0);
            this.mMobileNumbersList = temp;
        }
    }

    public void printLog(String log) {
        Dlog.d(log);
        Dlog.d("TaskID : " +  (this.mTaskID == -1 ? "null"  : this.mTaskID));
        if(mPlaceAlert != null) {
            Dlog.d("PlaceAlert - ID: " + mPlaceAlert.getPlaceAlertID());
            Dlog.d("PlaceAlert - placeName: " + mPlaceAlert.getPlaceName());
            Dlog.d("PlaceAlert - address : " + mPlaceAlert.getAddress());
            Dlog.d("PlaceAlert - contents : " + mPlaceAlert.getSmsContents());
            Dlog.d("PlaceAlert - lat, lon : " + mPlaceAlert.getLatitude() + ", " + mPlaceAlert.getLongitude());
        }

        if(mAlert != null) {
            Dlog.d("Alert - ID : " + mAlert.getAlertID());
            Dlog.d("Alert - stat : " + mAlert.getStartTime());
            Dlog.d("Alert - finish : " + mAlert.getFinishTime());
            Dlog.d("Alert - days : " + Integer.toBinaryString(mAlert.getRepeatDays()));
            Dlog.d("Alert - repeat_yn : " +  mAlert.getRepeatWeekYN());
            Dlog.d("Alert - Type : " + mAlert.getAlertType());

        }

        if(mMobileNumbersList != null) {
            for (int i = 0 ; i < mMobileNumbersList.size(); i++) {
                Dlog.d("mobile - (" + i + ") ; " + mMobileNumbersList.get(i));
            }
        }
    }

    public void setAlert(Alert alert) {
        this.mAlert =  alert;
    }

    public void setPlaceAlert(PlaceAlert placeAlert) {
        this.mPlaceAlert = placeAlert;
    }

    public void addMobileNumber(String mobileNumber) {
        mMobileNumbersList = Util.getMobileArrList(mobileNumber);
    }

    public int getSizeMobileList()    {
        if(mMobileNumbersList == null ) return 0;
        return mMobileNumbersList.size();
    }

    public String getMobileNumber(int index) {
        if(mMobileNumbersList == null ) return null;
        return mMobileNumbersList.get(index);
    }

    public void setAddrees(String addrees) {
        if(mPlaceAlert != null) {
            mPlaceAlert.setAddress(addrees);
        }
    }

    public void setSmsContents(String contents) {
        if(mPlaceAlert != null) {
            mPlaceAlert.setSmsContents(contents);
        }
    }

    public int getTaskID() {
        return mTaskID;
    }

    public PlaceAlert getPlaceAlert() {
        return mPlaceAlert;
    }

    public Alert getAlert() {
        return mAlert;
    }

    public ArrayList<String> getMobileNumbersList() {
        return mMobileNumbersList;
    }

    public int getAlertType() {
        if(mAlert != null) {
            return mAlert.getAlertType();
        }
        else {
            return -1;
        }
    }

    public void setPlaceTaskID(int id) {
        this.mTaskID = id;
    }

    public boolean isUseYN() {
        if(isUseYN.equals("Y")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setUseYN(boolean yn) {
        if(yn == true) {
            isUseYN = "Y";
        }
        else {
            isUseYN = "N";
        }
    }

    public boolean isAlertMe() {
        if(isAlertMe.equals("Y")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setAlertMe(boolean yn) {
        if(yn == true) {
            isAlertMe = "Y";
        }
        else {
            isAlertMe = "N";
        }
    }


    public void setAlertMe(String yn) {
        if(yn.equals("Y") == true) {
            isAlertMe = "Y";
        }
        else {
            isAlertMe = "N";
        }
    }

}
