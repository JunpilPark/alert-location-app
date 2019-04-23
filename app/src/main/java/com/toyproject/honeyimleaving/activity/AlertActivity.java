package com.toyproject.honeyimleaving.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceTask;

public class AlertActivity extends BaseActivity{
    private TextView mTxtAlertTitle;
    private Button mBtnConfirmAlert;
    private PlaceTask mParam;
    private Vibrator mVibrator;
    private ImageView mAlertSymbol;
    private PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        mTxtAlertTitle = findViewById(R.id.txt_alert_title);
        mBtnConfirmAlert = findViewById(R.id.btn_confirm_alert);
        mAlertSymbol = findViewById(R.id.imng_alert_symbol);

        mParam = (PlaceTask) getIntent().getSerializableExtra("placeTask");

        if(mParam != null) {
            if(mParam.getAlertType() == Alert.ALERT_TYPE_IN_REGION) {
                mTxtAlertTitle.setText(mParam.getPlaceAlert().getPlaceName() + "\n진입했습니다.");
                mAlertSymbol.setImageResource(R.drawable.img_logout_sketch_in);
            }
            else {
                mTxtAlertTitle.setText(mParam.getPlaceAlert().getPlaceName() + "\n벗어났습니다.");
                mAlertSymbol.setImageResource(R.drawable.img_logout_sketch_out);
            }
        }

        mBtnConfirmAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        wakeUp();
        playVibrator();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void playVibrator() {
        if(mVibrator == null) {
            mVibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
        }
        long[] pattern = { 0, 3000, 300, 3000, 300, 5000 };
        mVibrator.vibrate(pattern, 0);
    }

    private void wakeUp() {
        if(mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE | PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "alertWakeUp");
        }
        mWakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        if(mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}
