package com.wzx.sectionedadapter;

import android.database.DataSetObserver;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/1/26.
 */
public class SectionedListAdapter extends SectionedBaseAdapter {

    public static final String TAG = SectionedListAdapter.class.getSimpleName();

    protected List<SectionInfo> mSections = new ArrayList<SectionInfo>();

    private SparseArrayCompat<Boolean> mSectionHasHeaderCache = new SparseArrayCompat<Boolean>();

    private View mCurrentPinnedHeaderView;
    private int mCurrentPinnedSection = -1;

    private static final int DEFAULT_TYPE_COUNT_HEADER = 10;
    private static final int DEFAULT_TYPE_COUNT_ITEM = 10;

    private int mHeaderViewTypeCount;
    private int mItemViewTypeCount;

    private int mCurrentMaxHeaderType = -1;
    private int mCurrentMaxItemType = -1;
    private SparseArrayCompat<Integer> mHeaderTypeCache = new SparseArrayCompat<>();
    private SparseArrayCompat<Integer> mItemTypeOffsetCache = new SparseArrayCompat<>();

    public SectionedListAdapter() {
        this(DEFAULT_TYPE_COUNT_HEADER, DEFAULT_TYPE_COUNT_ITEM);
    }

    public SectionedListAdapter(int headerTypeCount, int itemTypeCount) {
        mHeaderViewTypeCount = headerTypeCount;
        mItemViewTypeCount = itemTypeCount;
    }

    public final void addSection(SectionInfo sectionInfo) {
        addSection(mSections.size(), sectionInfo);
    }

    public void addSection(int index, SectionInfo sectionInfo) {
        mSections.add(index, sectionInfo);
        registerDataSetObserver(sectionInfo);

        notifyDataSetChanged();
    }

    public void clean() {
        mSections.clear();
        for (SectionInfo sectionInfo : mSections) {
            unregisterDataSetObserver(sectionInfo);
        }

        notifyDataSetChanged();
    }

    private void registerDataSetObserver(SectionInfo sectionInfo) {
        SectionInfo.HeaderCreator headerCreator = sectionInfo.mHeaderCreator;
        if (headerCreator != null) {
            headerCreator.registerDataSetObserver(mDataSetObservable);
        }
        BaseAdapter adapter = sectionInfo.mAdapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataSetObservable);
        }
    }

    private void unregisterDataSetObserver(SectionInfo sectionInfo) {
        SectionInfo.HeaderCreator headerCreator = sectionInfo.mHeaderCreator;
        if (headerCreator != null) {
            headerCreator.unregisterDataSetObserver();
        }
        BaseAdapter adapter = sectionInfo.mAdapter;
        if (adapter != null) {
            adapter.unregisterDataSetObserver(mDataSetObservable);
        }
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

    public SectionInfo getSectionInfoForPosition(int position) {
        int section = getSectionForPosition(position);
        return mSections.get(section);
    }

    @Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        if (section < mSections.size()) {
            SectionInfo info = mSections.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null && position >= 0) {
                return adapter.getItem(position);
            }
        }
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        if (section < mSections.size()) {
            SectionInfo info = mSections.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null && position >= 0) {
                return adapter.getItemId(position);
            }
        }
        return -1;
    }

    @Override
    public int getSectionCount() {
        // TODO Auto-generated method stub
        return mSections.size();
    }

    @Override
    public int getCountForSection(int section) {
        // TODO Auto-generated method stub
        if (section < mSections.size()) {
            SectionInfo info = mSections.get(section);
            if (info.mIsExpanded) {
                BaseAdapter adapter = info.mAdapter;
                if (adapter != null) {
                    return adapter.getCount();
                }
            }
        }
        return 0;
    }

    @Override
    public View getItemView(int section, int position, View convertView,
                            ViewGroup parent) {
        // TODO Auto-generated method stub
        if (section < mSections.size()) {
            SectionInfo info = mSections.get(section);
            BaseAdapter adapter = info.mAdapter;
            if (adapter != null) {
                return adapter.getView(position, convertView, parent);
            }
        }
        return null;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView,
                                     ViewGroup parent) {
        // TODO Auto-generated method stub
        if (section < mSections.size() && hasSectionHeader(section)) {
            return createSectionHeader(section, convertView, parent);
        }
        return null;
    }

    @Override
    public View getSectionPinnedHeaderView(int section, View convertView, ViewGroup parent) {
        if (mCurrentPinnedSection != section) {
            mCurrentPinnedHeaderView = createSectionHeader(section, convertView, parent);
            mCurrentPinnedSection = section;
        }
        return mCurrentPinnedHeaderView;
    }

    private View createSectionHeader(int section, View convertView,
                                     ViewGroup parent) {
        SectionInfo info = mSections.get(section);

        if (info.mHeaderCreator != null) {
            return info.mHeaderCreator.getView(convertView, parent);
        }

        return null;
    }


    @Override
    public boolean hasSectionHeader(int section) {
        // TODO Auto-generated method stub
        if (section < mSections.size()) {
            if (mSectionHasHeaderCache.indexOfKey(section) < 0) {
                SectionInfo info = mSections.get(section);
                mSectionHasHeaderCache.put(section, info.hasHeader());
            }
            return mSectionHasHeaderCache.get(section);
        }
        return false;
    }

    public SectionInfo getSectionInfo(int section) {
        if (section >= 0 && section < mSections.size()) {
            return mSections.get(section);
        }
        return null;
    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return mHeaderViewTypeCount;
    }

    @Override
    public int getItemViewTypeCount() {
        return mItemViewTypeCount;
    }

    @Override
    public int getSectionHeaderViewType(int section) {
        SectionInfo sectionInfo = getSectionInfo(section);
        SectionInfo.HeaderCreator headerCreator = sectionInfo.mHeaderCreator;
        if (headerCreator != null) {
            int headerCreatorClassCode = headerCreator.getClass().hashCode();
            Integer headerViewType = mHeaderTypeCache.get(headerCreatorClassCode);
            if (headerViewType == null) {
                ++mCurrentMaxHeaderType;
                headerViewType = mCurrentMaxHeaderType;
                mHeaderTypeCache.put(headerCreatorClassCode, headerViewType);
            }
            if (headerCreator instanceof SectionInfo.UnRecycleHeaderView) {
                return AbsListView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
            } else {
                return headerViewType;
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int section, int position) {
        SectionInfo sectionInfo = getSectionInfo(section);
        BaseAdapter adapter = sectionInfo.mAdapter;
        int adapterClassCode = adapter.getClass().hashCode();
        Integer itemTypeOffset = mItemTypeOffsetCache.get(adapterClassCode);
        if (itemTypeOffset == null) {
            itemTypeOffset = mCurrentMaxItemType + 1;
            mCurrentMaxItemType += adapter.getViewTypeCount();
            mItemTypeOffsetCache.put(adapterClassCode, itemTypeOffset);
        }
        int originItemType = adapter.getItemViewType(position);
        if (originItemType >= 0) {
            return itemTypeOffset + originItemType;
        } else {
            return originItemType;
        }
    }

    @Override
    public boolean shouldPinSectionHeader(int section) {
        if (section < mSections.size()) {
            SectionInfo info = mSections.get(section);
            return info.mShouldPinHeader;
        }
        return false;
    }


    @Override
    public void notifyDataSetChanged() {
        reset();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        reset();
        super.notifyDataSetInvalidated();
    }

    protected void reset() {
        mSectionHasHeaderCache.clear();
        mCurrentPinnedHeaderView = null;
        mCurrentPinnedSection = -1;
    }

    public static final class SectionInfo {

        private BaseAdapter mAdapter;
        private HeaderCreator mHeaderCreator;
        private boolean mShouldPinHeader = false;
        private boolean mIsExpanded = true;

        private SectionInfo(Builder b) {
            mAdapter = b.mAdapter;
            mHeaderCreator = b.mHeaderCreator;
            mShouldPinHeader = b.mShouldPinHeader;
            mIsExpanded = b.mIsExpanded;
        }

        public boolean hasHeader() {
            if (mHeaderCreator != null) {
                return mHeaderCreator.hasHeader();
            }
            return false;
        }

        public HeaderCreator getHeaderCreator() {
            return mHeaderCreator;
        }

        public BaseAdapter getAdapter() {
            return mAdapter;
        }

        public void setIsExpanded(boolean isExpanded) {
            mIsExpanded = isExpanded;
        }

        public boolean isExpanded() {
            return mIsExpanded;
        }

        public abstract static class HeaderCreator {

            public abstract boolean hasHeader();

            public abstract View getView(View convertView, ViewGroup parent);

            private DataSetObserver mDataSetObservable;

            protected void notifyDataSetChanged() {
                if (mDataSetObservable != null) {
                    mDataSetObservable.onChanged();
                }
            }

            private void registerDataSetObserver(DataSetObserver observable) {
                mDataSetObservable = observable;
            }

            private void unregisterDataSetObserver() {
                mDataSetObservable = null;
            }
        }

        //实现该接口的headerCreator创建的view, ListView不回收复用
        public interface UnRecycleHeaderView {

        }

        public static class Builder {

            private BaseAdapter mAdapter;
            private HeaderCreator mHeaderCreator;
            private boolean mShouldPinHeader = false;
            private boolean mIsExpanded = true;

            public Builder setAdapter(BaseAdapter adapter) {
                mAdapter = adapter;
                return this;
            }

            public Builder setHeaderCreator(HeaderCreator creator) {
                mHeaderCreator = creator;
                return this;
            }

            public Builder pinnedHeader() {
                mShouldPinHeader = true;
                return this;
            }

            public Builder collapse() {
                mIsExpanded = false;
                return this;
            }

            public Builder setIsExpanded(boolean isExpanded) {
                mIsExpanded = isExpanded;
                return this;
            }

            public SectionInfo build() {
                return new SectionInfo(this);
            }
        }

    }

}
