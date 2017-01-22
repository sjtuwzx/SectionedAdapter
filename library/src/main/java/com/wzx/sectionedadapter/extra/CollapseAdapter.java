package com.wzx.sectionedadapter.extra;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by wangzhenxing on 16/7/7.
 */
public abstract class CollapseAdapter extends BaseAdapter implements View.OnClickListener {

    protected BaseAdapter mAdapter;
    protected int mMinItemCount = Integer.MAX_VALUE;
    protected boolean mIsCollapsed = true;
    private boolean mCanCollapseByUser = true;

    public abstract View getControllerView(View convertView, ViewGroup parent);

    public CollapseAdapter(BaseAdapter adapter, boolean canCollapseByUser) {
        mAdapter = adapter;
        mCanCollapseByUser = canCollapseByUser;
    }

    public void setup(int minItemCount, boolean collapsed) {
        mMinItemCount = minItemCount;
        mIsCollapsed = collapsed;
    }

    @Override
    public int getCount() {
        int count = mAdapter.getCount();
        if (count <= mMinItemCount || !mCanCollapseByUser && !mIsCollapsed) {
            return count;
        } else if (!mIsCollapsed) {
            return count + 1;
        } else {
            return mMinItemCount + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position < mMinItemCount || !mIsCollapsed && position < mAdapter.getCount()) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < mMinItemCount || !mIsCollapsed && position < mAdapter.getCount()) {
            return mAdapter.getItemId(position);
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mMinItemCount || !mIsCollapsed && position < mAdapter.getCount()) {
            return mAdapter.getItemViewType(position);
        } else {
            return mAdapter.getViewTypeCount();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < mMinItemCount || !mIsCollapsed && position < mAdapter.getCount()) {
            return mAdapter.getView(position, convertView, parent);
        } else {
            View controllerView = getControllerView(convertView, parent);
            controllerView.setOnClickListener(this);
            return controllerView;
        }
    }

    @Override
    public void onClick(View v) {
        mIsCollapsed ^= true;
        notifyDataSetChanged();
    }
}
