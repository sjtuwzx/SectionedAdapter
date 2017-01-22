package com.wzx.sectionedadapter;

import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SectionedBaseAdapter extends BaseAdapter implements PinnedSectionedHeaderAdapter {

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 0;

    /**
     * Holds the calculated values of @{link getPositionInSectionForPosition}
     */
    private SparseArrayCompat<Integer> mSectionPositionCache;
    /**
     * Holds the calculated values of @{link getSectionForPosition}
     */
    private SparseArrayCompat<Integer> mSectionCache;
    /**
     * Holds the calculated values of @{link getCountForSection}
     */
    private SparseArrayCompat<Integer> mSectionCountCache;

    /**
     * Caches the item count
     */
    private int mCount;
    /**
     * Caches the section count
     */
    private int mSectionCount;

    public SectionedBaseAdapter() {
        super();
        mSectionCache = new SparseArrayCompat<Integer>();
        mSectionPositionCache = new SparseArrayCompat<Integer>();
        mSectionCountCache = new SparseArrayCompat<Integer>();
        mCount = -1;
        mSectionCount = -1;
    }

    @Override
    public void notifyDataSetChanged() {
        mSectionCache.clear();
        mSectionPositionCache.clear();
        mSectionCountCache.clear();
        mCount = -1;
        mSectionCount = -1;
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        mSectionCache.clear();
        mSectionPositionCache.clear();
        mSectionCountCache.clear();
        mCount = -1;
        mSectionCount = -1;
        super.notifyDataSetInvalidated();
    }

    @Override
    public final int getCount() {
        if (mCount >= 0) {
            return mCount;
        }
        int count = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            count += internalGetCountForSection(i);
            if (hasSectionHeader(i)) {
                count++; // for the header view
            }
        }
        mCount = count;
        return count;
    }

    @Override
    public final Object getItem(int position) {
        return getItem(getSectionForPosition(position),
                getPositionInSectionForPosition(position));
    }

    @Override
    public final long getItemId(int position) {
        return getItemId(getSectionForPosition(position),
                getPositionInSectionForPosition(position));
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (isSectionHeader(position)) {
            return getSectionHeaderView(getSectionForPosition(position),
                    convertView, parent);
        }
        return getItemView(getSectionForPosition(position),
                getPositionInSectionForPosition(position), convertView, parent);
    }

    @Override
    public final int getItemViewType(int position) {
        if (isSectionHeader(position)) {
            int sectionHeaderViewType = getSectionHeaderViewType(getSectionForPosition(position));
            if (sectionHeaderViewType >= 0) {
                return getItemViewTypeCount() + sectionHeaderViewType;
            } else {
                return sectionHeaderViewType;
            }
        }
        return getItemViewType(getSectionForPosition(position),
                getPositionInSectionForPosition(position));
    }

    @Override
    public final int getViewTypeCount() {
        return getItemViewTypeCount() + getSectionHeaderViewTypeCount();
    }

    public final int getSectionForPosition(int position) {
        // first try to retrieve values from cache
        Integer cachedSection = mSectionCache.get(position);
        if (cachedSection != null) {
            return cachedSection;
        }
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            boolean hasSectionHeader = hasSectionHeader(i);
            int sectionCount = internalGetCountForSection(i);
            int sectionEnd = sectionStart + sectionCount + (hasSectionHeader ? 1 : 0);
            if (position >= sectionStart && position < sectionEnd) {
                mSectionCache.put(position, i);
                return i;
            }
            sectionStart = sectionEnd;
        }
        return 0;
    }

    public int getPositionInSectionForPosition(int position) {
        // first try to retrieve values from cache
        Integer cachedPosition = mSectionPositionCache.get(position);
        if (cachedPosition != null) {
            return cachedPosition;
        }
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            boolean hasSectionHeader = hasSectionHeader(i);
            int sectionCount = internalGetCountForSection(i);
            int sectionEnd = sectionStart + sectionCount + (hasSectionHeader ? 1 : 0);
            if (position >= sectionStart && position < sectionEnd) {
                int positionInSection = position - sectionStart - (hasSectionHeader ? 1 : 0);
                mSectionPositionCache.put(position, positionInSection);
                return positionInSection;
            }
            sectionStart = sectionEnd;
        }
        return 0;
    }

    @Override
    public final boolean isSectionHeader(int position) {
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            boolean hasSectionHeader = hasSectionHeader(i);
            if (position == sectionStart && hasSectionHeader) {
                return true;
            } else if (position < sectionStart) {
                return false;
            }
            sectionStart += internalGetCountForSection(i) + (hasSectionHeader ? 1 : 0);
        }
        return false;
    }

    @Override
    public final boolean isSectionFirst(int position) {
        // TODO Auto-generated method stub
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            boolean hasSectionHeader = hasSectionHeader(i);
            if (position == sectionStart) {
                return true;
            } else if (position < sectionStart) {
                return false;
            }
            sectionStart += internalGetCountForSection(i) + (hasSectionHeader ? 1 : 0);
        }
        return false;
    }

    public int getItemViewType(int section, int position) {
        return ITEM_VIEW_TYPE;
    }

    public int getItemViewTypeCount() {
        return 1;
    }

    public int getSectionHeaderViewType(int section) {
        return HEADER_VIEW_TYPE;
    }

    public int getSectionHeaderViewTypeCount() {
        return 1;
    }

    public abstract Object getItem(int section, int position);

    public abstract long getItemId(int section, int position);

    public abstract int getSectionCount();

    public abstract int getCountForSection(int section);

    public abstract View getItemView(int section, int position,
                                     View convertView, ViewGroup parent);

    public abstract View getSectionHeaderView(int section, View convertView,
                                              ViewGroup parent);

    public abstract boolean hasSectionHeader(int section);

    @Override
    public View getSectionPinnedHeaderView(int section, View convertView,
                                           ViewGroup parent) {
        return getSectionHeaderView(section, convertView, parent);
    }

    private int internalGetCountForSection(int section) {
        Integer cachedSectionCount = mSectionCountCache.get(section);
        if (cachedSectionCount != null) {
            return cachedSectionCount;
        }
        int sectionCount = getCountForSection(section);
        mSectionCountCache.put(section, sectionCount);
        return sectionCount;
    }

    private int internalGetSectionCount() {
        if (mSectionCount >= 0) {
            return mSectionCount;
        }
        mSectionCount = getSectionCount();
        return mSectionCount;
    }

    @Override
    public int getSectionFirstPosition(int section) {
        if (section < 0 || section > internalGetSectionCount()) {
            return -1;
        }
        int position = 0;
        for (int i = 0; i < section; i++) {
            if (hasSectionHeader(i)) {
                ++position;
            }
            position += internalGetCountForSection(i);
        }
        return position;
    }

    @Override
    public boolean shouldPinSectionHeader(int section) {
        return true;
    }
}
