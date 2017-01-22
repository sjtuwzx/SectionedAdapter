package com.wzx.sectionedadapter.extra;

import android.database.DataSetObserver;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangzhenxing on 16/10/12.
 */

public class InsertItemAdapter extends BaseAdapter {

    private static final int DEFAULT_TYPE_COUNT_INSERT_ITEM = 3;

    private BaseAdapter mOriginAdapter;

    private int mInsertedItemTypeCount;
    private List<InsertItemHolder> mInsertedItemList = new ArrayList<>();
    private List<Integer> mInsertedItemRealPositionList = new ArrayList<>();
    private SparseArray<InsertItemHolder> mRealInsertedItemMap = new SparseArray<>();

    private SparseIntArray mOriginPositionCache = new SparseIntArray();
    private int mCurrentMaxItemType = -1;
    private SparseIntArray mInsertedItemTypeCache = new SparseIntArray();

    public InsertItemAdapter(BaseAdapter originAdapter) {
        this(originAdapter, DEFAULT_TYPE_COUNT_INSERT_ITEM);
    }

    public InsertItemAdapter(BaseAdapter originAdapter, int insertItemTypeCount) {
        mOriginAdapter = originAdapter;
        mInsertedItemTypeCount = insertItemTypeCount;

        mOriginAdapter.registerDataSetObserver(mDataSetObservable);
    }

    public void addInsertItem(int position, InsertedItemCreator itemCreator) {
        InsertItemHolder holder = new InsertItemHolder(position, itemCreator);
        mInsertedItemList.add(holder);

        notifyDataSetChanged();
    }

    public void clearInsertItem() {
        mInsertedItemList.clear();
        notifyDataSetChanged();
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
    public void notifyDataSetChanged() {
        reset();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        reset();
        super.notifyDataSetInvalidated();
    }

    private void reset() {
        mOriginPositionCache.clear();

        //TimSort是稳定的排序算法
        Collections.sort(mInsertedItemList, sComparator);

        mInsertedItemRealPositionList.clear();
        mRealInsertedItemMap.clear();
        for (int i = 0, size = mInsertedItemList.size(); i < size; i++) {
            InsertItemHolder holder = mInsertedItemList.get(i);
            if (holder.mPosition >= 0 && holder.mPosition <= mOriginAdapter.getCount()) {
                int realPosition = mRealInsertedItemMap.size() + holder.mPosition;
                mInsertedItemRealPositionList.add(realPosition);
                mRealInsertedItemMap.append(realPosition, holder);
            }
        }
    }

    @Override
    public int getCount() {
        return mOriginAdapter.getCount() + mRealInsertedItemMap.size();
    }

    @Override
    public Object getItem(int position) {
        if (isInsertItem(position)) {
            return null;
        } else {
            return mOriginAdapter.getItem(getOriginPosition(position));
        }
    }

    @Override
    public long getItemId(int position) {
        if (isInsertItem(position)) {
            return 0;
        } else {
            return mOriginAdapter.getItemId(getOriginPosition(position));
        }
    }

    @Override
    public int getViewTypeCount() {
        return mInsertedItemTypeCount + mOriginAdapter.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isInsertItem(position)) {
            InsertItemHolder holder = mRealInsertedItemMap.get(position);
            if (holder.mItemCreator instanceof UnRecycleItemView) {
                return AbsListView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
            } else {
                return mOriginAdapter.getViewTypeCount() + getInsertedItemViewType(position);
            }
        } else {
            return mOriginAdapter.getItemViewType(getOriginPosition(position));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isInsertItem(position)) {
            InsertItemHolder holder = mRealInsertedItemMap.get(position);
            return holder.mItemCreator.getView(convertView, parent);
        } else {
            return mOriginAdapter.getView(getOriginPosition(position), convertView, parent);
        }
    }

    private boolean isInsertItem(int position) {
        return mRealInsertedItemMap.indexOfKey(position) >= 0;
    }

    private int getOriginPosition(int position) {
        int originPosition = mOriginPositionCache.get(position, -1);
        if (originPosition == -1) {
            originPosition = internalGetOriginPosition(position);
            mOriginPositionCache.put(position, originPosition);
        }

        return originPosition;
    }

    private int internalGetOriginPosition(int position) {
        for (int i = mInsertedItemRealPositionList.size() - 1; i >= 0; i--) {
            if (mInsertedItemRealPositionList.get(i) < position) {
                return position - i - 1;
            }
        }
        return position;
    }

    public int getInsertedItemViewType(int position) {
        InsertItemHolder holder = mRealInsertedItemMap.get(position);
        if (holder != null) {
            int itemCreatorClassCode = holder.mItemCreator.getClass().hashCode();
            int itemViewType = mInsertedItemTypeCache.get(itemCreatorClassCode, -1);
            if (itemViewType == -1) {
                ++mCurrentMaxItemType;
                itemViewType = mCurrentMaxItemType;
                mInsertedItemTypeCache.put(itemCreatorClassCode, itemViewType);
            }
            return itemViewType;
        }
        return 0;
    }


    public interface InsertedItemCreator {

        View getView(View convertView, ViewGroup parent);
    }

    //实现该接口的itemCreator创建的view, ListView不回收复用
    public interface UnRecycleItemView {

    }

    private static final class InsertItemHolder {
        private int mPosition;
        private InsertedItemCreator mItemCreator;

        private InsertItemHolder(int position, InsertedItemCreator itemCreator) {
            mPosition = position;
            mItemCreator = itemCreator;
        }
    }

    private static Comparator<InsertItemHolder> sComparator = new Comparator<InsertItemHolder>() {

        @Override
        public int compare(InsertItemHolder lhs, InsertItemHolder rhs) {
            return lhs.mPosition - rhs.mPosition;
        }
    };
}
