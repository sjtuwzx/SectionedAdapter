package com.wzx.sectionedadapter.extra;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.wzx.sectionedadapter.SectionedListAdapter;

/**
 * Created by wangzhenxing on 16/7/10.
 */
public abstract class CollapseSectionedListAdapter extends BaseAdapter {

    protected SectionedListAdapter mAdapter;
    protected int mMinSectionCount = Integer.MAX_VALUE;
    protected boolean mIsCollapsed = false;

    public CollapseSectionedListAdapter(SectionedListAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObservable);
        }
    }

    public void setup(int minSectionCount, boolean collapsed) {
        mMinSectionCount = minSectionCount;
        mIsCollapsed = collapsed;
    }

    private DataSetObserver mDataSetObservable = new DataSetObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            notifyDataSetInvalidated();
        }
    };

    @Override
    public int getCount() {
        int sectionCount = mAdapter.getSectionCount();
        if (mIsCollapsed && sectionCount > mMinSectionCount) {
            return mAdapter.getSectionFirstPosition(mMinSectionCount) + 1;
        } else {
            return mAdapter.getCount();
        }
    }

    public int getSectionCount() {
        int sectionCount = mAdapter.getSectionCount();
        if (mIsCollapsed && sectionCount > mMinSectionCount) {
            return mMinSectionCount;
        } else {
            return sectionCount;
        }
    }

    @Override
    public Object getItem(int position) {
        int section = mAdapter.getSectionForPosition(position);
        if (!mIsCollapsed || section < mMinSectionCount) {
            return mAdapter.getItem(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        int section = mAdapter.getSectionForPosition(position);
        if (!mIsCollapsed || section < mMinSectionCount) {
            return mAdapter.getItemId(position);
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        int section = mAdapter.getSectionForPosition(position);
        if (!mIsCollapsed || section < mMinSectionCount) {
            return mAdapter.getItemViewType(position);
        } else {
            return mAdapter.getViewTypeCount();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int section = mAdapter.getSectionForPosition(position);
        if (!mIsCollapsed || section < mMinSectionCount) {
            return mAdapter.getView(position, convertView, parent);
        } else {
            return getControllerView(convertView, parent);
        }
    }

    public abstract View getControllerView(View convertView, ViewGroup parent);
}
