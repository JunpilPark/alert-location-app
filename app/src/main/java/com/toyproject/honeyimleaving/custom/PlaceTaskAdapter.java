package com.toyproject.honeyimleaving.custom;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.toyproject.honeyimleaving.db.MyDBHandler;
import com.toyproject.honeyimleaving.model.Alert;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.model.PlaceTask;
import com.toyproject.honeyimleaving.myutil.Dlog;

import java.util.ArrayList;

/**
 * Created by iamfe on 2018-10-06.
 */

public class PlaceTaskAdapter extends RecyclerView.Adapter {

    private ArrayList<PlaceTask> mPlaceTaskList;
    private ItemLongClick mItemLongClick;
    private View.OnClickListener mOnClickListener;
    public int clickedPostion  = -1;

    public PlaceTaskAdapter(ArrayList placeTaskList, ItemLongClick itemLongClick) {
        this.mItemLongClick = itemLongClick;
        this.mPlaceTaskList = placeTaskList;
    }

    public interface ItemLongClick {
        void onLongClick(View view,int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceTaskListItemViewHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder == null) return;
        Dlog.d("onBindeViweHolder : " + position + ", " + holder.getItemId());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mItemLongClick == null) return false;
                mItemLongClick.onLongClick(v, position);
                return true;
            }
        });

        PlaceTask placeTask = mPlaceTaskList.get(position);

        if(holder instanceof PlaceTaskListItemViewHolder) {
            Dlog.d("PlaceTaskListItemViewHolder 세팅~!!");
            PlaceTaskListItemViewHolder placeTaskHolder = (PlaceTaskListItemViewHolder) holder;
            placeTaskHolder.setPlaceName(placeTask.getPlaceAlert().getPlaceName());
            placeTaskHolder.setAddress(placeTask.getPlaceAlert().getAddress());
            placeTaskHolder.setTypeImage(placeTask.getAlertType());
            placeTaskHolder.setSwitchONOFF(placeTask.isUseYN());
            placeTaskHolder.setStartTime(placeTask.getAlert().getStartTime()[Alert.HOUR_INDEX],placeTask.getAlert().getStartTime()[Alert.MIN_INDEX]);
            placeTaskHolder.setmFinishTime(placeTask.getAlert().getFinishTime()[Alert.HOUR_INDEX],placeTask.getAlert().getFinishTime()[Alert.MIN_INDEX]);
            placeTaskHolder.setRepeat(placeTask.getAlert().getRepeatWeekYN());
            placeTaskHolder.setDaysTextView(placeTask.getAlert().getRepeatDays());
            placeTaskHolder.setAlertMe(placeTask.isAlertMe());
            placeTaskHolder.setAlertSms(placeTask.getMobileNumbersList());
            placeTaskHolder.setExecuteType(placeTask.getAlert().getAlertExecuteType());

            placeTaskHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnClickListener == null) return;
                    clickedPostion =  position;
                    mOnClickListener.onClick(v);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mPlaceTaskList == null)
            return 0;
        else {
            return mPlaceTaskList.size();
        }
    }

    public void replacePlaceTaskList(ArrayList<PlaceTask> placeTaskArrayList) {
        this.mPlaceTaskList = placeTaskArrayList;
    }

    public PlaceTask getPlaceTask(int position) {
        if(mPlaceTaskList == null) return null;
        return mPlaceTaskList.get(position);
    }

    public void deletePlaceTask(int position) {
        if(mPlaceTaskList == null) return;
        mPlaceTaskList.remove(position);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public int getClickPostion() {
        return clickedPostion;
    }
}
