package com.github.rasmussaks.akenajalukku.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class NoTouchSlidingUpPanelLayout extends SlidingUpPanelLayout {
    public NoTouchSlidingUpPanelLayout(Context context) {
        super(context);
    }

    public NoTouchSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
