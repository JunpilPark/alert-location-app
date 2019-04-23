package com.toyproject.honeyimleaving.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.retrofit.model.Candidates;

import java.util.List;

public class CustomDialogSearchedPlace extends Dialog {

    private ListView mListContetns;
    private Button mBtnCancel;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private SearchedPlaceAdapter mAdapter;

    public CustomDialogSearchedPlace(@NonNull Context context, @NonNull AdapterView.OnItemClickListener onItemClickListener) {
        super(context);
        this.mOnItemClickListener =  onItemClickListener;
    }


    public void show( @NonNull List mArrayListCandidates) {
        if(mAdapter == null) {
            mAdapter = new SearchedPlaceAdapter(mArrayListCandidates);
        }
        else {
            mAdapter.setData(mArrayListCandidates);
        }
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dial_searched_place);

        mBtnCancel =  (Button)findViewById(R.id.btn_cancel);
        mListContetns = (ListView)findViewById(R.id.list_searched_place);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mListContetns.setAdapter(mAdapter);
        mListContetns.setOnItemClickListener(mOnItemClickListener);
    }
    
    public Candidates getItem(int index) {
        return  (Candidates) mAdapter.getItem(index);
    }

}
