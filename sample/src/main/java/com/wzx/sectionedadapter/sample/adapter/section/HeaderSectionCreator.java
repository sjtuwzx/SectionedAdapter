package com.wzx.sectionedadapter.sample.adapter.section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wzx.sectionedadapter.SectionedListAdapter;
import com.wzx.sectionedadapter.sample.R;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public class HeaderSectionCreator extends SectionedListAdapter.SectionInfo.HeaderCreator implements View.OnClickListener {

    private LayoutInflater mInflater;
    private int mIndex;

    private SectionedListAdapter.SectionInfo mSectionInfo;
    private View.OnClickListener mOnClickListener;

    public HeaderSectionCreator(Context context, int index) {
        mInflater = LayoutInflater.from(context);
        mIndex = index;
    }

    public void setSectionInfo(SectionedListAdapter.SectionInfo sectionInfo) {
        mSectionInfo = sectionInfo;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) mInflater.inflate(R.layout.header_layout, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(String.format("header[%d]", mIndex));
        if (mSectionInfo.isExpanded()) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
        }
        textView.setOnClickListener(this);

        return textView;
    }

    @Override
    public void onClick(View v) {
        mSectionInfo.setIsExpanded(!mSectionInfo.isExpanded());
        notifyDataSetChanged();

        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
    }
}
