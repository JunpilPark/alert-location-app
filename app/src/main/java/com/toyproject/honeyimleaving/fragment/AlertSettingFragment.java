package com.toyproject.honeyimleaving.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.custom.CustromDialogTimeSetting;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.myutil.Util;

/**
 * Created by iamfe on 2018-09-12.
 */

public class AlertSettingFragment extends Fragment implements FragmentReturnInterface<Alert>, View.OnClickListener {

    private CheckBox[] mChkBoxDaysArray = new CheckBox[7];
    private RadioGroup mRadioGrpAlertType;
    private RadioButton mRadioBtnAlertTypeIn ;
    private RadioButton mRadioBtnAlertTypeOut;
    private RadioGroup mRadioGrpAlertExecuteType;
    private RadioButton mRadioBtnAlertExecuteNow ;
    private RadioButton mRadioBtnAlertExecuteSched;

    private Button mBtnStartTime;
    private Button mBtnFinishTime;
    private CustromDialogTimeSetting mDialogTimeSetting;

    private int mBtnTimeIdWhenClick = 0;
    private int[] mStrtTime = new int[2];
    private int[] mFinishTime = new int[2];

    private Alert mParam;

    private boolean mIsErr;
    private String mErrString;

    public AlertSettingFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParam = null;
        if(getArguments() != null)
        {
            mParam = (Alert) getArguments().getSerializable("Alert");
        }
        mIsErr = false;
        mErrString = "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.frag_alert_setting, container, false);
        InitLayout(layout);
        initBtnTimeTempValue();
        initChkBoxDays();
        initRadioBtnAlertType();
        initRadioBtnAlertExecuteType();
        return layout;
    }

    private void InitLayout(View layout) {

        mBtnStartTime = layout.findViewById(R.id.btn_start_time);
        mBtnFinishTime = layout.findViewById(R.id.btn_finish_time);
        mRadioGrpAlertType = layout.findViewById(R.id.radio_gr_alert_type);
        mRadioBtnAlertTypeIn = layout.findViewById(R.id.radio_btn_alert_type_1);
        mRadioBtnAlertTypeOut = layout.findViewById(R.id.radio_btn_alert_type_2);
        mRadioGrpAlertExecuteType = layout.findViewById(R.id.radio_gr_alert_execute_type);
        mRadioBtnAlertExecuteNow =layout.findViewById(R.id.radio_btn_execute_alert_type_1);
        mRadioBtnAlertExecuteSched = layout.findViewById(R.id.radio_btn_execute_alert_type_2);

        for(int i = 0 ; i < mChkBoxDaysArray.length ; i++) {
            mChkBoxDaysArray[i] = layout.findViewById(R.id.chk_days_01 + i);
        }

        mBtnStartTime.setOnClickListener(this);
        mBtnFinishTime.setOnClickListener(this);
    }

    private void initBtnTimeTempValue() {

        if(mParam != null) {
            if(mParam.getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_SCHEDULE) {
                mStrtTime = mParam.getStartTime();
                mFinishTime = mParam.getFinishTime();

                mBtnStartTime.setText(Util.changeTimeToString(mStrtTime[Alert.HOUR_INDEX], mStrtTime[Alert.MIN_INDEX]));
                mBtnFinishTime.setText(Util.changeTimeToString(mFinishTime[Alert.HOUR_INDEX], mFinishTime[Alert.MIN_INDEX]));
            }
            else {
                mBtnStartTime.setText("시간을 설정해주세요.");
                mBtnFinishTime.setText("시간을 설정해주세요.");
            }
        }
        else {
            for (int i = 0; i < mStrtTime.length; i++) {
                mStrtTime[i] = -1;
                mFinishTime[i] = -1;
            }
            mBtnStartTime.setText("시간을 설정해주세요.");
            mBtnFinishTime.setText("시간을 설정해주세요.");
        }

        mDialogTimeSetting = new CustromDialogTimeSetting(getContext(),
                new CustromDialogTimeSetting.onOkButtonClickListener() {
                    @Override
                    public void click() {
                        mDialogTimeSetting.dismiss();

                        switch (mBtnTimeIdWhenClick) {
                            case R.id.btn_start_time:
                                mStrtTime[Alert.HOUR_INDEX] = mDialogTimeSetting.getHours();
                                mStrtTime[Alert.MIN_INDEX] = mDialogTimeSetting.getMin();

                                mBtnStartTime.setText(Util.changeTimeToString(mStrtTime[Alert.HOUR_INDEX], mStrtTime[Alert.MIN_INDEX]));

                                break;
                            case R.id.btn_finish_time:
                                mFinishTime[Alert.HOUR_INDEX] = mDialogTimeSetting.getHours();
                                mFinishTime[Alert.MIN_INDEX] = mDialogTimeSetting.getMin();

                                mBtnFinishTime.setText(Util.changeTimeToString(mFinishTime[Alert.HOUR_INDEX], mFinishTime[Alert.MIN_INDEX]));
                                break;
                        }
                    }
                }
        );

        mRadioGrpAlertType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mIsErr = false;
                mErrString = "";
            }
        });

        mRadioGrpAlertExecuteType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_btn_execute_alert_type_1) {
                    setEnableSchduleGroup(false);
                }
                else {
                    setEnableSchduleGroup(true);
                }
            }
        });
    }

    private void initChkBoxDays() {
        if(mParam != null) {
            for (int i = 0 ; i < mChkBoxDaysArray.length ; i++) {
              if  ((mParam.getRepeatDays() & (Alert.MONDAY << i)) == (Alert.MONDAY << i)) {
                  mChkBoxDaysArray[i].setChecked(true);
              }
              else {
                  mChkBoxDaysArray[i].setChecked(false);
              }
            }
        }
        else {
            for (int i = 0 ; i < mChkBoxDaysArray.length ; i++) {
                mChkBoxDaysArray[i].setChecked(false);
            }
        }
    }

    private void initRadioBtnAlertType() {
        if(mParam == null || mParam.getAlertType() == Alert.ALERT_TYPE_IN_REGION) {
            mRadioBtnAlertTypeIn.setChecked(true);
        }
        else {
            mRadioBtnAlertTypeOut.setChecked(true);
        }
    }

    private void initRadioBtnAlertExecuteType() {
        if(mParam == null || mParam.getAlertExecuteType() == Alert.ALERT_EXECUTE_TYPE_SCHEDULE) {
            mRadioBtnAlertExecuteSched.setChecked(true);
        }
        else {
            mRadioBtnAlertExecuteNow.setChecked(true);
        }
    }

    private boolean chkAvailable() {
        mIsErr = false;
        if(getAlertExecuteTypeFromCheckRadioBtn() == Alert.ALERT_EXECUTE_TYPE_SCHEDULE) {
            if (mStrtTime[Alert.HOUR_INDEX] == -1 || mStrtTime[Alert.MIN_INDEX] == -1) {
                mIsErr = true;
                mErrString = "시작시간을 설정해야 합니다.";
                return false;
            }

            if (mFinishTime[Alert.HOUR_INDEX] == -1 || mFinishTime[Alert.MIN_INDEX] == -1) {
                mIsErr = true;
                mErrString = "종료시간을 설정해야 합니다.";
                return false;
            }

            if((mStrtTime[Alert.HOUR_INDEX] *60 +  mStrtTime[Alert.MIN_INDEX]) >=  (mFinishTime[Alert.HOUR_INDEX] * 60 + mFinishTime[Alert.MIN_INDEX])) {
                mIsErr = true;
                mErrString = "종료시간이 시작시간보다 커야합니다.";
                return false;
            }

            boolean isChk = false;
            for (int i = 0; i < mChkBoxDaysArray.length; i++) {
                if (mChkBoxDaysArray[i].isChecked()) {
                    isChk = true;
                    break;
                }
            }
            if (isChk == false) {
                mIsErr = true;
                mErrString = "요일 중 하나는 체크 해야합니다.";
                return false;
            }
        }
        return true;
    }

    @Override
    public Alert getFragementReturn() {
        Alert alert = new Alert(new Alert.Builder((mParam == null ? -1 : mParam.getAlertID()), mStrtTime, mFinishTime,
                getAlertTypeFromCheckRadioBtn(), getAlertExecuteTypeFromCheckRadioBtn()));
        for (int i = 0; i < mChkBoxDaysArray.length; i++) {
            if (mChkBoxDaysArray[i].isChecked()) {
                alert.addRepeatDays((byte) (Alert.MONDAY << i));
            }
        }
        return alert;
    }

    @Override
    public String getErrorString() {
        return mErrString;
    }

    @Override
    public boolean isError() {
        chkAvailable();
        return mIsErr;
    }

    @Override
    public void onClick(View v) {
        if (mDialogTimeSetting == null) return;

        switch (v.getId()) {
            case R.id.btn_start_time:
                mDialogTimeSetting.show(mStrtTime[Alert.HOUR_INDEX], mStrtTime[Alert.MIN_INDEX]);
                mBtnTimeIdWhenClick = v.getId();

                break;
            case R.id.btn_finish_time:
                    mDialogTimeSetting.show(mFinishTime[Alert.HOUR_INDEX], mFinishTime[Alert.MIN_INDEX]);
                    mBtnTimeIdWhenClick = v.getId();
                break;
        }
    }

    private int getAlertTypeFromCheckRadioBtn() {
        int alertType;

        if(mRadioGrpAlertType.getCheckedRadioButtonId() == R.id.radio_btn_alert_type_2) {
            alertType = Alert.ALERT_TYPE_OUT_REGION;
        }
        else {
            alertType = Alert.ALERT_TYPE_IN_REGION;
        }
        return alertType;
    }


    private int getAlertExecuteTypeFromCheckRadioBtn() {
        int alertType;

        if(mRadioGrpAlertExecuteType.getCheckedRadioButtonId() == R.id.radio_btn_execute_alert_type_1) {
            alertType = Alert.ALERT_EXECUTE_TYPE_NOW;
        }
        else {
            alertType = Alert.ALERT_EXECUTE_TYPE_SCHEDULE;
        }
        return alertType;
    }

    private void setEnableSchduleGroup(boolean enable) {
        mBtnFinishTime.setEnabled(enable);
        mBtnStartTime.setEnabled(enable);

        if(enable == true) {
            mBtnFinishTime.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            mBtnStartTime.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }
        else {
            mBtnFinishTime.setTextColor(getResources().getColor(R.color.colorTextHint));
            mBtnStartTime.setTextColor(getResources().getColor(R.color.colorTextHint));
        }

        for(int i = 0 ; i < mChkBoxDaysArray.length ; i++ ){
            mChkBoxDaysArray[i].setEnabled(enable);
        }
    }
}
