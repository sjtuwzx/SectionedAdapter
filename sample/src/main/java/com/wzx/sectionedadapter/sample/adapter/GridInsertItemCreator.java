package com.wzx.sectionedadapter.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.sectionedadapter.extra.InsertItemAdapter;
import com.wzx.sectionedadapter.sample.R;

/**
 * Created by wangzhenxing on 17/1/22.
 */

public class GridInsertItemCreator implements InsertItemAdapter.InsertedItemCreator {

    private LayoutInflater mInflater;

    public GridInsertItemCreator(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.insert_item_layout, parent, false);
        }
        return convertView;
    }
}
