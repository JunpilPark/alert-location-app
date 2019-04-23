package com.toyproject.honeyimleaving.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.myutil.Util;

/**
 * Created by iamfe on 2018-08-22.
 */

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imgLoading = findViewById(R.id.img_loading);
        TextView txtVersion = findViewById(R.id.txt_version);

        txtVersion.setText(Util.getVersion(this)); ;

        ObjectAnimator rotate = ObjectAnimator.ofFloat(
                imgLoading,
                "rotation",
                1440
                );
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.playTogether(rotate);
        aniSet.setDuration(3000);
        mHandler = new Handler();
        aniSet.start();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }

}
