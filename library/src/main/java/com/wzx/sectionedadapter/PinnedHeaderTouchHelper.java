package com.wzx.sectionedadapter;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by wang_zx on 2015/1/26.
 */
public class PinnedHeaderTouchHelper {

    private static final String TAG = PinnedHeaderTouchHelper.class.getSimpleName();

    private View mOwner;
    private boolean mIsScrollableOwner = true;
    private boolean mHasTouchPinnedHeader = false;

    private boolean mShouldDelayChildPressedState = true;

    public PinnedHeaderTouchHelper(View owner) {
        this(owner, true);
    }

    public PinnedHeaderTouchHelper(View owner, boolean isScrollableOwner) {
        mOwner = owner;
        mIsScrollableOwner = isScrollableOwner;
    }

    public void reset() {
        mHasTouchPinnedHeader = false;
        mShouldDelayChildPressedState = true;
    }

    public boolean shouldDelayChildPressedState() {
        return mShouldDelayChildPressedState;
    }

    public boolean dispatchPinnedHeaderTouchEvent(View header, Rect headerRect, MotionEvent ev, int offsetX,
                                                  int offsetY) {
        if (mOwner == null || header == null) {
            return false;
        }

        Rect dirtyRect = new Rect(headerRect);
        dirtyRect.offset(mOwner.getScrollX(), mOwner.getScrollY());

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (headerRect.contains(x, y)) {
            int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
            if (action != MotionEvent.ACTION_DOWN && !mHasTouchPinnedHeader) {
                return false;
            } else if (action == MotionEvent.ACTION_DOWN) {
                mHasTouchPinnedHeader = true;
            } else if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL) {
                mHasTouchPinnedHeader = false;
            }

            MotionEvent event = MotionEvent.obtain(ev);
            event.offsetLocation(offsetX, offsetY);
            mShouldDelayChildPressedState = false;
            header.dispatchTouchEvent(event);
            mShouldDelayChildPressedState = true;
            event.recycle();

            if (action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_POINTER_DOWN) {
                if (mIsScrollableOwner && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mOwner.invalidate(dirtyRect);
                } else {
                    mOwner.postInvalidateDelayed(ViewConfiguration.getTapTimeout(), dirtyRect.left, dirtyRect.top, dirtyRect.right,
                            dirtyRect.bottom);
                }
            } else if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_POINTER_UP) {
                // 点击事件完成后再刷新
                int delayMs = mIsScrollableOwner && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : ViewConfiguration
                        .getPressedStateDuration();
                mOwner.postInvalidateDelayed(delayMs, dirtyRect.left, dirtyRect.top, dirtyRect.right,
                        dirtyRect.bottom);
            } else if (action == MotionEvent.ACTION_MOVE
                    || action == MotionEvent.ACTION_CANCEL) {
                mOwner.invalidate(dirtyRect);
            }
            return true;
        }

        if (mHasTouchPinnedHeader) {
            mHasTouchPinnedHeader = false;
            MotionEvent event = MotionEvent.obtain(ev);
            event.offsetLocation(offsetX, offsetY);
            event.setAction(MotionEvent.ACTION_CANCEL);
            header.dispatchTouchEvent(event);
            event.recycle();

            mOwner.invalidate(dirtyRect);
        }
        return false;
    }
}
