package com_7idear.framework.ext;

import android.content.Context;
import android.graphics.PointF;

import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * 平滑滚动控件（用于列表在一屏内的精准滑动，原生滑动方法在一屏内滑动不精准）
 * @author ieclipse 19-12-10
 * @description
 */
public class BaseSmoothScroller
        extends LinearSmoothScroller {

    private float mDurationTime = 10;

    public enum ScrollerSpeed {
        SLOWEST,
        SLOWER,
        SLOW,
        NORMAL,
        FAST,
    }

    public BaseSmoothScroller(Context context) {
        this(context, ScrollerSpeed.NORMAL, 0);
    }

    public BaseSmoothScroller(Context context, ScrollerSpeed speed) {
        this(context, speed, 0);
    }

    public BaseSmoothScroller(Context context, ScrollerSpeed speed, int position) {
        super(context);

        init(speed, position);
    }

    private void init(ScrollerSpeed speed, int position) {
        switch (speed) {
            case SLOWEST:
                mDurationTime = 10f;
                break;
            case SLOWER:
                mDurationTime = 5f;
                break;
            case SLOW:
                mDurationTime = 2f;
                break;
            case FAST:
                mDurationTime = 0.5f;
                break;
            default:
                mDurationTime = 1f;
        }
        setTargetPosition(position);
    }

    @Override
    protected int calculateTimeForScrolling(int dx) {
        return (int) (super.calculateTimeForScrolling(dx) * mDurationTime);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }
}
