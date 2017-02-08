package com.jonli.fundkeeper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/12/1.
 */

public class MyHScrollView extends HorizontalScrollView {
    ScrollViewObserver mScrollViewObserver = new ScrollViewObserver();
    public MyHScrollView(Context context) {
        super(context);
    }

    public MyHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("pdwy","MyHScrollView onTouchEvent");
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mScrollViewObserver != null /*&& (l != oldl || t != oldt)*/) {
            mScrollViewObserver.NotifyOnScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void AddOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollViewObserver.AddOnScrollChangedListener(listener);
    }

    public void RemoveOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollViewObserver.RemoveOnScrollChangedListener(listener);
    }

    public static interface OnScrollChangedListener {
        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public static class ScrollViewObserver {
        List<OnScrollChangedListener> mList;

        public ScrollViewObserver() {
            super();
            mList = new ArrayList<OnScrollChangedListener>();
        }

        public void AddOnScrollChangedListener(OnScrollChangedListener listener) {
            mList.add(listener);
        }

        public void RemoveOnScrollChangedListener(OnScrollChangedListener listener) {
            mList.remove(listener);
        }

        public void NotifyOnScrollChanged(int l, int t, int oldl, int oldt) {
            if (mList == null || mList.size() == 0) {
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i) != null) {
                    mList.get(i).onScrollChanged(l, t, oldl, oldt);
                }
            }
        }
    }
}
