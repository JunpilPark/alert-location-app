package com.toyproject.honeyimleaving.model;

/**
 * Created by iamfe on 2018-09-11.
 */

public class SendHistory {
    private String mHistoryId;
    private String mSendDate;
    private String mMobileNumber;
    private String mPlaceName;
    private double mLatitude;
    private double mLongitude;
    private int mAlertType;
    private int mStateCode;

    public String getHistoryId() {
        return mHistoryId;
    }

    public void setHistoryId(String mHistoryId) {
        this.mHistoryId = mHistoryId;
    }

    public String getSendDate() {
        return mSendDate;
    }

    public void setSendDate(String mSendDate) {
        this.mSendDate = mSendDate;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String mPlaceName) {
        this.mPlaceName = mPlaceName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public int getAlertType() {
        return mAlertType;
    }

    public void setAlertType(int mAlertType) {
        this.mAlertType = mAlertType;
    }

    public int getStateCode() {
        return mStateCode;
    }

    public void setStateCode(int stateCode) {
        this.mStateCode = stateCode;
    }
}
