package com.toyproject.honeyimleaving.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.custom.SentHistoryCursorAdapter;
import com.toyproject.honeyimleaving.db.MyDBHandlerForSentHistory;

import java.util.List;

/**
 * Created by iamfe on 2018-10-22.
 */

public class SendHistoryActivity extends BaseActivity{
    private MyDBHandlerForSentHistory mDbSentHistroy;
    private ListView mListView;
    private Button mDeleteHistory;
    private SentHistoryCursorAdapter mSendHistroyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendhistory);
        mListView = findViewById(R.id.list_send_history);
        mDeleteHistory = findViewById(R.id.btn_del_history);
        setImgTitleBarText(R.drawable.img_title_txt_history);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDbSentHistroy == null) {
            mDbSentHistroy = new MyDBHandlerForSentHistory(this);
        }
        if (mListView != null) {
            if (mSendHistroyAdapter == null) {
                Cursor cursor = mDbSentHistroy.selectSentHistoryCursorAll();
                mSendHistroyAdapter = new SentHistoryCursorAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                mListView.setAdapter(mSendHistroyAdapter);
            }
        }
        mDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDbSentHistroy == null) return;
                if(mDbSentHistroy.deleteSendHistoryAll() == true) {
                    Cursor cursor = mDbSentHistroy.selectSentHistoryCursorAll();
                    mSendHistroyAdapter.changeCursor(cursor);
                    mSendHistroyAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDbSentHistroy.close();
    }
}
