package com.toyproject.honeyimleaving.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.toyproject.honeyimleaving.HoneyImLeaving;
import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.custom.PlaceTaskAdapter;
import com.toyproject.honeyimleaving.db.MyDBHandler;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.receiver.AlarmRegister;
import com.toyproject.honeyimleaving.service.MyServiceHandler;
import com.toyproject.honeyimleaving.service.PlaceTaskGPSCheckService;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private MyDBHandler myDBHandler;
    private PlaceTaskAdapter mPlaceTaskadapter;
    private MyServiceHandler mHandlerService;
    private AlarmRegister mAlarmRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFindViewByID();
        setImgTitleBarText(R.drawable.img_title_txt_main);

        myDBHandler =  new MyDBHandler(this);
        mHandlerService = new MyServiceHandler(this);
        mAlarmRegister = new AlarmRegister();

    }

    public void setFindViewByID() {
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycleView_placeTask_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), mLayoutManager.getOrientation()));

        FloatingActionButton btnAddItem  = findViewById(R.id.action_go_to_new_item);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, AddPlaceTaskActivity.class);
                    startActivity(i);
                }
            }
        );
        FloatingActionButton  btnShowSendingHistory = findViewById(R.id.action_go_to_senthistory);
        btnShowSendingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SendHistoryActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        Dlog.d("onStart() 실행됨");
        if(mHandlerService.BindService(new Intent(this, PlaceTaskGPSCheckService.class), BIND_AUTO_CREATE))
        {
            Dlog.d("mHandlerService 바인드 성공!!");
        }
        else {
            Dlog.d("mHandlerService 바인드 실패!!");
        }
        if(myDBHandler != null) {
            Dlog.d("myDBHandler != null 임");
            ArrayList<PlaceTask> temp = myDBHandler.selectPlaceTaskList(-1);
            if(mPlaceTaskadapter == null) {
                mPlaceTaskadapter = new PlaceTaskAdapter(temp, new PlaceTaskAdapter.ItemLongClick() {
                    @Override
                    public void onLongClick(View view, int position) {
                        showMenuLongClicked(position);                    }
                });

                mPlaceTaskadapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v instanceof Switch) {
                            Dlog.d("Click!!! - " + mPlaceTaskadapter.getClickPostion() + ", " + ((Switch) v).isChecked());
                            changeSwitch(mPlaceTaskadapter.getClickPostion(), ((Switch) v).isChecked());
                        }
                    }
                });

                mRecyclerView.setAdapter(mPlaceTaskadapter);
            }
            else {
                mPlaceTaskadapter.replacePlaceTaskList(temp);
            }
                mPlaceTaskadapter.notifyDataSetChanged();
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                            PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                            PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                            ) {

                                showAlertDialog(getString(R.string.alert_title_info_permission), getString(R.string.alert_info_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                                        Manifest.permission.RECEIVE_SMS,
                                                        Manifest.permission.SEND_SMS};

                                                ActivityCompat.requestPermissions(MainActivity.this,
                                                        permission, HoneyImLeaving.RequestedPermissionCode.ACCESS_PERMISSION_ALL);
                                            }
                                        }, getString(R.string.common_ok));

                    } else {

                        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.SEND_SMS};

                        ActivityCompat.requestPermissions(MainActivity.this,
                                permission, HoneyImLeaving.RequestedPermissionCode.ACCESS_PERMISSION_ALL);
                    }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == HoneyImLeaving.RequestedPermissionCode.ACCESS_PERMISSION_ALL) {
            for(int i = 0 ; i < grantResults.length ; i ++) {
                if(grantResults[i] != 0) // 승인이 거절된 경우
                {
                    showAlertDialog(getString(R.string.alert_title_info_permission), getString(R.string.alert_info_permission_go_to_setting),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 어플리케이션의 세팅화면으로 이동
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }
                            }, getString(R.string.common_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }, getString(R.string.common_cancel));
                    break;
                }
            }
        }
    }

    private void showMenuLongClicked(final int position) {
        final int MODIFY_INDEX = 0;
        final int DELETE_INDEX = 1;

        final String[] menu = {getString(R.string.main_list_long_click_menu_item_1),
                        getString(R.string.main_list_long_click_menu_item_2)};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               PlaceTask placeTaskSeleted =  mPlaceTaskadapter.getPlaceTask(position);
                changeSwitch(position,false);

                switch (which) {
                    case MODIFY_INDEX:
                        Intent i = new Intent(MainActivity.this, AddPlaceTaskActivity.class);
                        i.putExtra("PlaceTask", placeTaskSeleted);
                        startActivity(i);
                        break;
                    case DELETE_INDEX:
                        if(myDBHandler.deletePlaceTask(placeTaskSeleted)) {
                            mPlaceTaskadapter.replacePlaceTaskList(myDBHandler.selectPlaceTaskList(-1));
                            mPlaceTaskadapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    private void changeSwitch(int position, boolean isChecked) {
        Dlog.d("메인 스위치 동작함 : " + position + ", " + isChecked);
        PlaceTask placeTaskSeleted =  mPlaceTaskadapter.getPlaceTask(position);
        // 정상적으로 DB에 반영되었을 때만 동작
        if(myDBHandler.updateUseYN(placeTaskSeleted.getTaskID(), isChecked) == true) {
            placeTaskSeleted.setUseYN(isChecked);
            mAlarmRegister.addAlarm(placeTaskSeleted);
            if (isChecked == true) {
                mAlarmRegister.registAlarmOrStartService(this);
            } else {
                if (mHandlerService.isServiceRunningCheck()) {
                    mHandlerService.removeTask(placeTaskSeleted);
                }
                mAlarmRegister.removeAlarm(this);
            }
        }

    }

    @Override
    protected void onStop() {
        if(mHandlerService != null) mHandlerService.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        myDBHandler.close();
        super.onDestroy();
    }
}
