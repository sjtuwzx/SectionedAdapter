package com.wzx.sectionedadapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangzhenxing on 16/12/10.
 */

public interface PinnedSectionedHeaderAdapter {

    int getSectionForPosition(int position);

    View getSectionPinnedHeaderView(int section, View convertView,
                                    ViewGroup parent);

    int getSectionHeaderViewType(int section);

    int getCount();

    boolean hasSectionHeader(int section);

    boolean shouldPinSectionHeader(int section);

    boolean isSectionHeader(int position);

    boolean isSectionFirst(int position);

    int getSectionFirstPosition(int section);
}
