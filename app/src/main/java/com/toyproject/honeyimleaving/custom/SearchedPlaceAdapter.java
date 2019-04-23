package com.toyproject.honeyimleaving.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.retrofit.model.Candidates;

import java.util.List;

/**
 * Created by iamfe on 2018-09-15.
 */

public class SearchedPlaceAdapter extends BaseAdapter {

    private List<Candidates> mListCandidates;

    public SearchedPlaceAdapter(List<Candidates> mListCandidates) {
        this.mListCandidates = mListCandidates;
    }

    public void setData( List<Candidates> list) {
        this.mListCandidates = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListCandidates.size();
    }

    @Override
    public Object getItem(int position) {
      //  return position;
        return mListCandidates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            Dlog.d("List의 View 생성(NEW)");
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_searched_place_dial, parent, false);
        }

        TextView txtPlaceName = convertView.findViewById(R.id.txt_place_name);
        TextView txtPlaceAddress = convertView.findViewById(R.id.txt_address);

        txtPlaceName.setText(mListCandidates.get(position).getPlaceName());
        txtPlaceAddress.setText(mListCandidates.get(position).getFormattedAddress());
        return  convertView;
    }

}
