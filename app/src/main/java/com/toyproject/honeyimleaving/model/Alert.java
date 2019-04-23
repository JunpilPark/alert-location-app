package com.toyproject.honeyimleaving.model;

import java.io.Serializable;

/**
 * Created by iamfe on 2018-09-11.
 */

public class Alert implements Serializable {

    public final static int ALERT_TYPE_IN_REGION = 11;
    public final static int ALERT_TYPE_OUT_REGION = 12;

    public final static int ALERT_EXECUTE_TYPE_NOW = 21;
    public final static int ALERT_EXECUTE_TYPE_SCHEDULE = 22;

    public static final int HOUR_INDEX = 0;
    public static final int MIN_INDEX = 1;

    public static byte MONDAY = 0x01;
    public static byte TUESDAY = (byte) (MONDAY << 1);
    public static byte WEDNSDAY = (byte) (MONDAY << 2);
    public static byte THURSDAY = (byte) (MONDAY << 3);
    public static byte FRIDAY = (byte) (MONDAY << 4);
    public static byte SATURDAY = (byte) (MONDAY << 5);
    public static byte SUNDAY = (byte) (MONDAY << 6);

    public static String REPEAT_WEEK_YES = "Y";
    public static String REPEAT_WEEK_NO = "N";

    private int mAlertID;
    private int[] mStartTime = new int[2];
    private int[] mFinishTime = new int[2];
    private int mRepeatDays;
    private String mRepeatWeekYN;
    private int mAlertType;
    private int mAlertExecuteType;


    public Alert(Builder builder) {
        this.mAlertID = builder.alertID;
        this.mStartTime = builder.startTime;
        this.mFinishTime = builder.finishTime;
        this.mRepeatWeekYN = builder.repeatWeekYN;
        this.mRepeatDays = builder.repeatDays;
        this.mAlertType = builder.alertType;
        this.mAlertExecuteType = builder.alertExecuteType;
    }

    public static class Builder {
        private int alertID;
        private int[] startTime;
        private int[] finishTime;
        private int repeatDays;
        private String repeatWeekYN;
        private int alertType;
        private int alertExecuteType;

        public Builder(int id, int[] startTime, int[] finishTime, int alertType, int alertExecuteType) {
            this.alertID = id;
            this.startTime = startTime;
            this.finishTime = finishTime;
            this.repeatWeekYN = REPEAT_WEEK_YES;
            this.repeatDays = 0x00;
            this.alertType = alertType;
            this.alertExecuteType = alertExecuteType;
        }

        public Builder setStartTime(int[] startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setFinishTime(int[] finishTime) {
            this.finishTime = finishTime;
            return this;
        }

        public Builder setRepeatWeekOption(String option) {
            if(option == REPEAT_WEEK_YES || option == REPEAT_WEEK_NO) {
                this.repeatWeekYN = option;
            }
            else {
                this.repeatWeekYN = REPEAT_WEEK_YES;
            }
            return this;
        }

        public Builder addRepeatDays(byte day) {
            this.repeatDays = this.repeatDays | day;
            return this;
        }

        public Builder removeRepeatDays(byte day) {
            this.repeatDays = this.repeatDays & (~ day);
            return this;
        }
    }

    public void addRepeatDays(byte day) {
        this.mRepeatDays = this.mRepeatDays | day;
    }

    public void removeRepeatDays(byte day) {
        this.mRepeatDays = this.mRepeatDays & (~ day);
    }

    public void setStartTime(int[] startTime) {
        this.mStartTime = startTime;
    }

    public void setFinishTime(int[] finishTime) {
        this.mFinishTime = finishTime;
    }

    public void setRepeatWeekOption(String option) {
        if(option == REPEAT_WEEK_YES || option == REPEAT_WEEK_NO) {
            this.mRepeatWeekYN = option;
        }
        else {
            this.mRepeatWeekYN = REPEAT_WEEK_YES;
        }
    }

    public int getAlertID() {
        return mAlertID;
    }

    public int[] getStartTime() {
        return mStartTime;
    }

    public int[] getFinishTime() {
        return mFinishTime;
    }

    public int getRepeatDays() {
        return mRepeatDays;
    }

    public String getRepeatWeekYN() {
        return mRepeatWeekYN;
    }

    public int getAlertType() {
        return mAlertType;
    }

    public void setAlertType(int type) { this.mAlertType = type; }

    public int getAlertExecuteType() {
        return this.mAlertExecuteType;
    }

    public void setAlertExecuteType(int type) { this.mAlertExecuteType = type; }
}
