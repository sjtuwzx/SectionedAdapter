package com.wzx.sectionedadapter.sample.adapter.section;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.sectionedadapter.SectionedListAdapter;
import com.wzx.sectionedadapter.sample.R;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public class UnRecycleSectionHeaderCreator extends SectionedListAdapter.SectionInfo.HeaderCreator
        implements SectionedListAdapter.SectionInfo.UnRecycleHeaderView {

    private LayoutInflater mInflater;
    private View mView;

    public UnRecycleSectionHeaderCreator(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.unrecycle_header_layout, parent, false);
        }
        return mView;
    }
}
