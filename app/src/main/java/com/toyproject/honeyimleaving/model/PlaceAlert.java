package com.toyproject.honeyimleaving.model;

import java.io.Serializable;

/**
 * Created by iamfe on 2018-09-11.
 */

public class PlaceAlert implements Serializable {

    private int mPlaceAlertID;
    private String mPlaceName;
    private String mAddress;
    private double mLatitude; // 위도
    private double mLongitude; // 경도
    private String mSmsContents;

    public  PlaceAlert(Builder builder) {
        this.mPlaceAlertID = builder.placeAlertID;
        this.mPlaceName = builder.placeName;
        this.mAddress =  builder.address;
        this.mLatitude = builder.latitude;
        this.mLongitude = builder.longitude;
        this.mSmsContents = builder.smsContents;
    }

    public static class Builder{
        private int placeAlertID;
        private String placeName;
        private String address;
        private double latitude; // 위도
        private double longitude; // 경도
        private String smsContents;

        public Builder(int placeAlertID, String placeName, double latitude, double longitude) {
            this.placeAlertID = placeAlertID;
            this.placeName = placeName;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setSmsContents(String contents) {
            this.smsContents = contents;
            return this;
        }

    }

    public int getPlaceAlertID() {
        return mPlaceAlertID;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getSmsContents() {
        return mSmsContents;
    }

    public void setSmsContents(String contents) {
        this.mSmsContents = contents;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }
}
