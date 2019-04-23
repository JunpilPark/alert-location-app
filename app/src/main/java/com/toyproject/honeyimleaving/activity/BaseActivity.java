package com.toyproject.honeyimleaving.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.toyproject.honeyimleaving.R;


/**
 * Created by iamfe on 2018-08-22.
 */

public class BaseActivity extends AppCompatActivity {
    protected ImageButton btn_info;
    protected LinearLayout infoVIew;

    protected void setImgTitleBarText(int res) {
        ImageView imgtitleBarText = findViewById(R.id.img_title_bar_txt);
        infoVIew = findViewById(R.id.info_view);
        btn_info = findViewById(R.id.btn_main_info);

        if(imgtitleBarText == null) return;

        imgtitleBarText.setImageResource(res);


        if(btn_info == null) return;

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(infoVIew == null) {
                    Toast.makeText(getBaseContext(), R.string.txt_not_info_view, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(infoVIew.getVisibility() == View.VISIBLE) {
                    infoVIew.setVisibility(View.GONE);
                    btn_info.setRotation(0);
                }
                else {
                    infoVIew.setVisibility(View.VISIBLE);
                    btn_info.setRotation(-90);
                }

            }
        });
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setCancelable(false);
        builder.show();
    }

    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText,onNegativeButtonClickListener);
        builder.setCancelable(false);
        builder.show();
    }
}
