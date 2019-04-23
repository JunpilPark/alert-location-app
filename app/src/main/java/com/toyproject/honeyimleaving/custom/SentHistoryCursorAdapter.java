package com.toyproject.honeyimleaving.custom;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.model.Alert;

public class SentHistoryCursorAdapter extends CursorAdapter {

    public SentHistoryCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public SentHistoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_send_history, parent ,false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder == null || cursor == null) return;

        holder.txtPlaceName.setText(cursor.getString(cursor.getColumnIndex("place_name")));
        holder.txtReceiverMobile.setText(insertEnterNextCpmma(cursor.getString(cursor.getColumnIndex("mobile_number"))));
        holder.txtSendDateTime.setText(cursor.getString(cursor.getColumnIndex("send_date")));
        holder.txtContents.setText(cursor.getString(cursor.getColumnIndex("sms_contents")));
        holder.txtStateCode.setText(getStateText(cursor.getInt(cursor.getColumnIndex("state_code")), context));
        if(cursor.getInt(cursor.getColumnIndex("alert_type")) == Alert.ALERT_TYPE_IN_REGION) {
            holder.imgPlaceTaskType.setImageResource(R.drawable.ic_in_arr);
        }
        else {
            holder.imgPlaceTaskType.setImageResource(R.drawable.ic_out_arr);
        }
    }

    private String insertEnterNextCpmma(String str) {
        if(str == null) {
            return "";
        }

        return str.replace(",",",\n");
    }

    private String getStateText(int code, Context context) {
        String stateText;
        if(code == Activity.RESULT_OK) {
            stateText = context.getString(R.string.txt_send_success);
        }
        else {
            stateText = context.getString(R.string.txt_send_fail) + " (" + code + ")";
        }
        return stateText;
    }

    public class ViewHolder {
        public TextView txtSendDateTime;
        public TextView txtReceiverMobile;
        public TextView txtPlaceName;
        public ImageView imgPlaceTaskType;
        public TextView txtContents;
        public TextView txtStateCode;
        public ViewHolder(View view) {
            if(view == null) return;

            txtStateCode = view.findViewById(R.id.txt_send_code);
            txtContents = view.findViewById(R.id.txt_send_contents);
            imgPlaceTaskType = view.findViewById(R.id.img_in_out_type_icon);
            txtPlaceName = view.findViewById(R.id.txt_place_name);
            txtReceiverMobile = view.findViewById(R.id.txt_receiver_mobile);
            txtSendDateTime =  view.findViewById(R.id.txt_send_datetime);
        }
    }
}
