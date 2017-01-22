package com.wzx.sectionedadapter.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wzx.sectionedadapter.extra.CollapseAdapter;
import com.wzx.sectionedadapter.sample.R;

/**
 * Created by wangzhenxing on 17/1/22.
 */

public class ItemCollapseAdapter extends CollapseAdapter {

    private LayoutInflater mInflater;

    public ItemCollapseAdapter(Context context, BaseAdapter adapter) {
        super(adapter, true);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getControllerView(View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_collapse_controller, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mIsCollapsed) {
            holder.mTextView.setText("展开");
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        } else {
            holder.mTextView.setText("收起");
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);

        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView mTextView;

        private ViewHolder(View view) {
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }
}
