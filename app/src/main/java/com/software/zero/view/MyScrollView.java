package com.software.zero.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ScrollView;

import com.software.zero.enums.ScrollViewLocation;

public class MyScrollView extends ScrollView {
    private static final String TAG = "MyScrollView";
    private ScrollViewLocation location = ScrollViewLocation.MIDDLE;
    private float startX;
    private float startY;
    private float firstMoveLocationX;
    private float firstMoveLocationY;
    private boolean flag = true;

    private float moveDistanceY;
    Animation animation;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            startX = ev.getX();
            startY = ev.getY();
            flag = true;
        } else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
            if(flag) {
                firstMoveLocationX = ev.getX();
                firstMoveLocationY = ev.getY();
                moveDistanceY = startY - firstMoveLocationY;
                handleMoveEvent(moveDistanceY);
                flag = false;
            }

        } else if(ev.getAction() == MotionEvent.ACTION_UP) {

        }

        Log.d(TAG, "onTouchEvent: " + moveDistanceY);
        return true;
    }

    private void handleMoveEvent(float moveDistanceY) {
        int height = getHeight();
        int last = 0;
        if(moveDistanceY < 0) { // 下滑，收起
            if(location == ScrollViewLocation.TOP) {
                last = 500;
                location = ScrollViewLocation.MIDDLE;
            } else if(location == ScrollViewLocation.MIDDLE) {
                last = 100;
                location = ScrollViewLocation.BOTTOM;
            }
        } else {
            if(location == ScrollViewLocation.BOTTOM) {
                last = 500;
                location = ScrollViewLocation.MIDDLE;
            } else if(location == ScrollViewLocation.MIDDLE) {
                last = 1500;
                location = ScrollViewLocation.TOP;
            }
        }
        Log.d(TAG, "handleMoveEvent: " + last);
        if(last != 0) changeLayoutHeight(height, last);
    }

    private void changeLayoutHeight(int from, int to) {
        ValueAnimator heightAnimator = ValueAnimator.ofInt(from, to);
        heightAnimator.setDuration(500);
            heightAnimator.addUpdateListener(
                    animation1 -> {
                        int cur = (int) animation1.getAnimatedValue();
                        ViewGroup.LayoutParams params = getLayoutParams();
                        params.height = cur;
                        setLayoutParams(params);
                    }
            );
        heightAnimator.start();
    }
}