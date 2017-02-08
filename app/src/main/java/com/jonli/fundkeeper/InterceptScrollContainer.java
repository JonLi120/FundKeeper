package com.jonli.fundkeeper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Li on 2016/12/1.
 */

public class InterceptScrollContainer extends LinearLayout{
    public InterceptScrollContainer(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public InterceptScrollContainer(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        Log.i("pdwy","ScrollContainer onInterceptTouchEvent");
        return true;
    }
}
