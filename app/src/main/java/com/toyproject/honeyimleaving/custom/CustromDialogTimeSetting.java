package com.toyproject.honeyimleaving.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TimePicker;

import com.toyproject.honeyimleaving.R;

/**
 * Created by iamfe on 2018-09-17.
 */

public class CustromDialogTimeSetting extends Dialog {

    private Button mBtnCancel;
    private Button mBtnOK;
    private TimePicker mTimePicker;

    private int mHour;
    private int mMin;

    private onOkButtonClickListener mOnOkButtonClickListener;

    public CustromDialogTimeSetting(@NonNull Context context, onOkButtonClickListener okButtonClickListener) {
        super(context);
        this.mOnOkButtonClickListener = okButtonClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dial_time_setting);

        mBtnOK = findViewById(R.id.btn_ok);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mTimePicker = findViewById(R.id.timePicker);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeFromTimePicker();
                if(mOnOkButtonClickListener != null) {
                    mOnOkButtonClickListener.click();
                }
            }
        });
    }

    private void setTimeFromTimePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mHour = mTimePicker.getHour();
            this.mMin = mTimePicker.getMinute();
        }
        else {
            this.mHour = mTimePicker.getCurrentHour();
            this.mMin = mTimePicker.getCurrentMinute();
        }
    }

    public interface onOkButtonClickListener {
        void click() ;
    }

    public int getHours() {
        return mHour;
    }

    public int getMin() {
        return mMin;
    }


    @Override
    public void show() {
        super.show();
    }

    public void show(int hour, int min) {
        super.show();
        if(hour != -1 && min != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTimePicker.setHour(hour);
                mTimePicker.setMinute(min);
            } else {
                mTimePicker.setCurrentHour(hour);
                mTimePicker.setCurrentMinute(min);
            }
        }

    }
}
