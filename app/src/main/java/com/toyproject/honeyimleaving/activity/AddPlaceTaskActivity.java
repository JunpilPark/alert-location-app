package com.toyproject.honeyimleaving.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.db.MyDBHandler;
import com.toyproject.honeyimleaving.fragment.AlertSettingFragment;
import com.toyproject.honeyimleaving.fragment.FragmentReturnErrCheckUtil;
import com.toyproject.honeyimleaving.fragment.FragmentReturnInterface;
import com.toyproject.honeyimleaving.fragment.PlaceFragment;
import com.toyproject.honeyimleaving.fragment.SmsSettingFragment;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.receiver.AlarmRegister;

import java.io.Serializable;


public class AddPlaceTaskActivity extends BaseActivity {
    private final int MIN_STEP_INDEX = 0;
    private final int MAX_STEP_INDEX = 2;

    private int mCurrentStep = 0; // 현재 플래그먼트 위치

    Button mBtnNextStep;
    Button mBtnBackStep;

    TextView mTxtHeadInfoTitle;
    TextView mTxtHeadInfoDetail1;
    TextView mTxtHeadInfoDetail2;
    PlaceFragment mPlaceFragment;
    AlertSettingFragment mAlertSettingFragment;
    SmsSettingFragment mSmsSettingFragment;

    PlaceTask mPlaceTask;
    MyDBHandler mDBHandler;
    AlarmRegister mAlarmRegister;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Dlog.d("Activity Create 됨.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_placetask);

        mBtnBackStep = findViewById(R.id.btn_back_step);
        mBtnNextStep = findViewById(R.id.btn_next_step);

        mTxtHeadInfoTitle =  findViewById(R.id.txt_head_info_title);

        mBtnBackStep.setOnClickListener(new onClicklistener());
        mBtnNextStep.setOnClickListener(new onClicklistener());

        mPlaceTask = (PlaceTask) getIntent().getSerializableExtra("PlaceTask");
        setImgTitleBarText(R.drawable.img_title_txt_reg);
        replaceFragment(-1);
        setHeadInfoText(-1);
        mTxtHeadInfoDetail1 = findViewById(R.id.txt_info_line_1);
        mTxtHeadInfoDetail2 = findViewById(R.id.txt_info_line_2);
        mDBHandler = new MyDBHandler(this);
        mAlarmRegister = new AlarmRegister();
    }

    public class onClicklistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 앞으로 가기 클릭
            if(v.getId() == R.id.btn_next_step) {
                FragmentReturnInterface fragmentReturnInterface = getInterface();

                if(FragmentReturnErrCheckUtil.isErrorFragment(fragmentReturnInterface)) {
                    showAlertDialog(getString(R.string.common_alert_title),
                            FragmentReturnErrCheckUtil.getErrorFragment(fragmentReturnInterface),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            getString(R.string.common_ok));
                }
                else {
                    if(mPlaceTask == null) {mPlaceTask = new PlaceTask(-1, fragmentReturnInterface);}
                    else {mPlaceTask.setPlaceTaskObj(fragmentReturnInterface);}
                    mPlaceTask.printLog("Next 클릭 ----- ");
                    replaceFragment(nextStep());
                }

            }
            // 뒤로 가기 클릭
            if(v.getId() == R.id.btn_back_step) {
                replaceFragment(backStep());
            }
        }

        private FragmentReturnInterface getInterface() {
            FragmentReturnInterface fragmentReturnInterface;
            switch (mCurrentStep)
            {
                case 0 :
                    fragmentReturnInterface = mPlaceFragment;
                    break;
                case 1 :
                    fragmentReturnInterface = mAlertSettingFragment;
                    break;
                case 2 :
                    fragmentReturnInterface = mSmsSettingFragment;
                    break;
                default:
                    fragmentReturnInterface = mPlaceFragment;
                    break;
            }
            return fragmentReturnInterface;
        }
    }


    private void setBottomButton(int step) {
        if(step <= MIN_STEP_INDEX) {
            mBtnNextStep.setVisibility(View.VISIBLE);
            mBtnBackStep.setVisibility(View.INVISIBLE);
            mBtnNextStep.setText(R.string.next_step);
            mBtnBackStep.setText(R.string.back_step);
        }
        else if (step >= MAX_STEP_INDEX) {
            mBtnNextStep.setVisibility(View.VISIBLE);
            mBtnBackStep.setVisibility(View.VISIBLE);
            mBtnNextStep.setText(R.string.common_save);
        }
        else {
            mBtnNextStep.setVisibility(View.VISIBLE);
            mBtnBackStep.setVisibility(View.VISIBLE);
            mBtnNextStep.setText(R.string.next_step);
            mBtnBackStep.setText(R.string.back_step);
        }
    }

    private void setHeadInfoText(int step) {
        switch (step)
        {
            case 0:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                if(mTxtHeadInfoTitle != null) mTxtHeadInfoTitle.setText(R.string.head_info_text_step1);
                if(mTxtHeadInfoDetail1 != null) mTxtHeadInfoDetail1.setText(getString(R.string.head_info_text_step1_1));
                if(mTxtHeadInfoDetail2 != null) mTxtHeadInfoDetail2.setText(getString(R.string.head_info_text_step1_2));
                break;
            case 1:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                if(mTxtHeadInfoTitle != null) mTxtHeadInfoTitle.setText(R.string.head_info_text_step2);
                if(mTxtHeadInfoDetail1 != null) mTxtHeadInfoDetail1.setText(getString(R.string.head_info_text_step2_1));
                if(mTxtHeadInfoDetail2 != null) mTxtHeadInfoDetail2.setText(getString(R.string.head_info_text_step2_2));
                break;
            case 2:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                if(mTxtHeadInfoTitle != null) mTxtHeadInfoTitle.setText(R.string.head_info_text_step3);
                if(mTxtHeadInfoDetail1 != null) mTxtHeadInfoDetail1.setText(getString(R.string.head_info_text_step3_1));
                if(mTxtHeadInfoDetail2 != null) mTxtHeadInfoDetail2.setText(getString(R.string.head_info_text_step3_2));
                break;
            default:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                if(mTxtHeadInfoTitle != null) mTxtHeadInfoTitle.setText(R.string.head_info_text_step1);
                if(mTxtHeadInfoDetail1 != null) mTxtHeadInfoDetail1.setText(getString(R.string.head_info_text_step1_1));
                if(mTxtHeadInfoDetail2 != null) mTxtHeadInfoDetail2.setText(getString(R.string.head_info_text_step1_2));
                break;
        }
    }


    private void replaceFragment(int step) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        switch (step)
        {
            case 0:
                if(mPlaceFragment == null) {mPlaceFragment = new PlaceFragment();}

                if(mPlaceTask != null && mPlaceTask.getPlaceAlert() != null) {
                    args.putSerializable("placeAlert", (Serializable) mPlaceTask.getPlaceAlert());
                    mPlaceFragment.setArguments(args);
                }

                fragmentTransaction.replace(R.id.fragmentContainer, mPlaceFragment);
                break;
            case 1:
                if(mAlertSettingFragment == null) {mAlertSettingFragment = new AlertSettingFragment();}

                if(mPlaceTask != null && mPlaceTask.getAlert() != null) {
                    args.putSerializable("Alert", (Serializable) mPlaceTask.getAlert());
                    mAlertSettingFragment.setArguments(args);
                }
                fragmentTransaction.replace(R.id.fragmentContainer, mAlertSettingFragment);
                break;
            case 2:
                if(mSmsSettingFragment == null) {mSmsSettingFragment = new SmsSettingFragment();}

                if(mPlaceTask != null) {
                    args.putString("smsContents", mPlaceTask.getPlaceAlert().getSmsContents());
                    args.putSerializable("mobileNumbers", mPlaceTask.getMobileNumbersList());
                    args.putBoolean("alertMe", mPlaceTask.isAlertMe());
                    mSmsSettingFragment.setArguments(args);
                }

                fragmentTransaction.replace(R.id.fragmentContainer, mSmsSettingFragment);
                break;
            case 3:
                if(mDBHandler == null) {
                    Dlog.e("mDBHandler is null");
                    return;
                }

                if(mPlaceTask.getTaskID() > 0) {
                    Dlog.d("mPlaceTask 업데이트로 삭제합니다.");
                    mDBHandler.deletePlaceTask(mPlaceTask);
                    mAlarmRegister.addAlarm(mPlaceTask);
                    mAlarmRegister.removeAlarm(this);
                }

                mPlaceTask.setPlaceTaskID(mDBHandler.insertPlaceTask(mPlaceTask));
                if(mPlaceTask.getTaskID() == -1) {
                    Toast.makeText(getBaseContext(), "저장에 실패했습니다.",Toast.LENGTH_SHORT).show();
                    Dlog.d("저장에 실패했습니다.");
                    break;
                }
                else {
                    if(mAlarmRegister != null) {
                        Dlog.d("mAlarmRegister is not null");
                        mAlarmRegister.addAlarm(mPlaceTask);
                        mAlarmRegister.registAlarmOrStartService(getBaseContext());
                    }
                    finish();
                }
                break;
            default: // 초기화
                if(mPlaceFragment == null) {mPlaceFragment = new PlaceFragment();}
                if(mPlaceTask != null && mPlaceTask.getPlaceAlert() != null) {
                    args.putSerializable("placeAlert", (Serializable) mPlaceTask.getPlaceAlert());
                    mPlaceFragment.setArguments(args);
                }
                fragmentTransaction.add(R.id.fragmentContainer, mPlaceFragment);
        }
        fragmentTransaction.commit();
        setBottomButton(step);
        setHeadInfoText(step);
    }

    @Override
    protected void onStop() {
        Dlog.d("Activity Stop 됨.");
        super.onStop();
    }

    private int nextStep() {
        if(mCurrentStep <= MAX_STEP_INDEX ) {
            mCurrentStep++;
        }
        return  mCurrentStep;
    }

    private int backStep() {
        if (mCurrentStep > MIN_STEP_INDEX ) {
            mCurrentStep--;
        }
        return  mCurrentStep;
    }

    @Override
    protected void onDestroy() {
        mDBHandler.close();
        super.onDestroy();
    }

}
