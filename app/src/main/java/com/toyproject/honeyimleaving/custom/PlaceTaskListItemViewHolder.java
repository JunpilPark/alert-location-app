package com.toyproject.honeyimleaving.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.myutil.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by iamfe on 2018-10-06.
 */

public class PlaceTaskListItemViewHolder extends RecyclerView.ViewHolder {

    private TextView mPlaceNameTxtView;
    private TextView mAddressTxtView;
    private TextView mStartTimeTxtView;
    private TextView mFinishTimeTxtView;
    private TextView mDashTextView;

    private TextView[] mDaysTxtViewArr;
    private ImageView mImgPlaceTaskType;
    private TextView mTxtPlaceTaskType;
    private ImageView mImgRepeat;
    private ImageView mImgAlertMe;
    private ImageView mImgAlertSms;

    private Switch mSwitch;

    public PlaceTaskListItemViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.list_item_place_task_in_main, parent, false));
        mPlaceNameTxtView = itemView.findViewById(R.id.txt_place_name);
        mAddressTxtView = itemView.findViewById(R.id.txt_address);
        mStartTimeTxtView = itemView.findViewById(R.id.txt_start_time);
        mFinishTimeTxtView = itemView.findViewById(R.id.txt_finish_time);
        mTxtPlaceTaskType = itemView.findViewById(R.id.txt_in_out_type);
        mImgPlaceTaskType = itemView.findViewById(R.id.img_in_out_type_icon);
        mImgRepeat = itemView.findViewById(R.id.img_repeat);
        mImgAlertSms = itemView.findViewById(R.id.img_sms);
        mImgAlertMe = itemView.findViewById(R.id.img_alert_me);
        mDashTextView = itemView.findViewById(R.id.txt_time_dash);

        mSwitch = itemView.findViewById(R.id.switch_on_off);

        mDaysTxtViewArr = new TextView[7];
        for (int i = 0; i < 7; i++) {
            mDaysTxtViewArr[i] = itemView.findViewById(R.id.txt_day_01 + i);
        }
    }

    public void setSwitchONOFF(boolean switchOnOff) {
        mSwitch.setChecked(switchOnOff);
    }

    public void setPlaceName(String placeName) {
        if (mPlaceNameTxtView == null) return;
        mPlaceNameTxtView.setText(placeName);
    }

    public void setAddress(String address) {
        if (mAddressTxtView == null) return;
        if (address == null || address.length() == 0) {
            mAddressTxtView.setVisibility(View.GONE);
        } else {
            mAddressTxtView.setVisibility(View.VISIBLE);
            mAddressTxtView.setText(address);
        }
    }

    public void setStartTime(int hour, int min) {
        if (mStartTimeTxtView == null) return;
        mStartTimeTxtView.setText(Util.changeTimeToString(hour, min));
    }

    public void setmFinishTime(int hour, int min) {
        if (mFinishTimeTxtView == null) return;
        mFinishTimeTxtView.setText(Util.changeTimeToString(hour, min));
    }

    public void setRepeat(String isRepeat) {
        if (isRepeat.equals("Y")) {
            mImgRepeat.setVisibility(View.VISIBLE);
        } else {
            mImgRepeat.setVisibility(View.GONE);
        }
    }

    public void setAlertMe(boolean isAlertMe) {
        if (isAlertMe == true) {
            mImgAlertMe.setVisibility(View.VISIBLE);
        } else {
            mImgAlertMe.setVisibility(View.GONE);
        }
    }

    public void setAlertSms(ArrayList<String> moblieList) {
        if (moblieList != null && moblieList.size() > 0) {
            mImgAlertSms.setVisibility(View.VISIBLE);
        } else {
            mImgAlertSms.setVisibility(View.GONE);
        }

    }

    public void setTypeImage(int type) {
        if (mImgPlaceTaskType == null) return;
        if (mTxtPlaceTaskType == null) return;

        if (Alert.ALERT_TYPE_OUT_REGION == type) {
            mImgPlaceTaskType.setImageResource(R.drawable.ic_out_arr);
            mTxtPlaceTaskType.setText(R.string.placeTask_tupe_out);
        } else {
            mImgPlaceTaskType.setImageResource(R.drawable.ic_in_arr);
            mTxtPlaceTaskType.setText(R.string.placeTask_tupe_in);
        }
    }

    public void setDaysTextView(int days) {
        for (int i = 0; i < 7; i++) {
            if ((days & (Alert.MONDAY << i)) == (Alert.MONDAY << i)) {
                mDaysTxtViewArr[i].setVisibility(View.VISIBLE);
            } else {
                mDaysTxtViewArr[i].setVisibility(View.GONE);
            }
        }
    }

    public void setExecuteType(int type) {
        if (type == Alert.ALERT_EXECUTE_TYPE_NOW) {
            mFinishTimeTxtView.setVisibility(View.GONE);
            mStartTimeTxtView.setVisibility(View.GONE);
            mDashTextView.setVisibility(View.GONE);
            mImgRepeat.setVisibility(View.GONE);
            setDaysTextView(0);
        } else {
            mFinishTimeTxtView.setVisibility(View.VISIBLE);
            mStartTimeTxtView.setVisibility(View.VISIBLE);
            mDashTextView.setVisibility(View.VISIBLE);
            mImgRepeat.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickListener(@NonNull View.OnClickListener onClickListener) {
        if (mSwitch == null) return;
        Dlog.d("mSwitch에 Click 리스너를 set 함");
        mSwitch.setOnClickListener(onClickListener);
    }

}