package com.wzx.sectionedadapter.sample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wzx.sectionedadapter.sample.R;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public class ItemsAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;

    public ItemsAdapter(Context context) {
        super(context, View.NO_ID);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.items_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String item = getItem(position);

        holder.mTextView.setText(item);
        return convertView;
    }

    private static class ViewHolder {
        private TextView mTextView;

        private ViewHolder(View view) {
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }
}
