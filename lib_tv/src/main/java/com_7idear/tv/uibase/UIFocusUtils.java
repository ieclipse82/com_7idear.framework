package com_7idear.tv.uibase;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com_7idear.tv.R;


/**
 * UI焦点工具类
 * @author ieclipse 19-11-21
 * @description TODO 待完善细节
 */
public class UIFocusUtils {

    private static UIFocusUtils mInstance;

    private View.OnFocusChangeListener mFocusChangeListener;

    private AnimatorSet mAnimLeftOut, mAnimLeftIn;
    private AnimatorSet mAnimRightOut, mAnimRightIn;
    private AnimatorSet mAnimTopOut, mAnimTopIn;
    private AnimatorSet mAnimBottomOut, mAnimBottomIn;
    private AnimatorSet mAnimNullOut, mAnimNullIn;

    UIFocusUtils(Context context) {
        init(context);
    }

    public static UIFocusUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (UIFocusUtils.class) {
                if (mInstance == null) mInstance = new UIFocusUtils(context);
            }
        }
        return mInstance;
    }

    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return mFocusChangeListener;
    }

    private void init(Context context) {
        mAnimLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_left_out);
        mAnimLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_left_in);
        mAnimRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_right_out);
        mAnimRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_right_in);
        mAnimTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_top_out);
        mAnimTopIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_top_in);
        mAnimBottomOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_bottom_out);
        mAnimBottomIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_bottom_in);
        mAnimNullOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_null_out);
        mAnimNullIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.anim_focus_change_null_in);

        mAnimLeftOut.setInterpolator(new DecelerateInterpolator());
        mAnimLeftIn.setInterpolator(new OvershootInterpolator());
        mAnimRightOut.setInterpolator(new DecelerateInterpolator());
        mAnimRightIn.setInterpolator(new OvershootInterpolator());
        mAnimTopOut.setInterpolator(new DecelerateInterpolator());
        mAnimTopIn.setInterpolator(new OvershootInterpolator());
        mAnimBottomOut.setInterpolator(new DecelerateInterpolator());
        mAnimBottomIn.setInterpolator(new OvershootInterpolator());
        mAnimNullOut.setInterpolator(new DecelerateInterpolator());
        mAnimNullIn.setInterpolator(new OvershootInterpolator());


        mFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.animate().cancel();
                Object objDirection = v.getTag(R.id.focus_change_direction);
                if (v instanceof UIBaseTVFocusItem && objDirection instanceof Integer) {
                    animFocus((UIBaseTVFocusItem) v, hasFocus, (int) objDirection);
                } else {
                    if (hasFocus) {
                        v.setTranslationZ(2f);
                        AnimatorSet anim = mAnimNullIn.clone();
                        anim.setTarget(v);
                        anim.start();
                    } else {
                        v.setTranslationZ(1f);
                        AnimatorSet anim = mAnimNullOut.clone();
                        anim.setTarget(v);
                        anim.start();
                    }
                }
            }
        };
    }

    private void animFocus(UIBaseTVFocusItem v, boolean hasFocus, int direction) {
        switch (direction) {
            case View.FOCUS_RIGHT:
                if (hasFocus) {
                    v.setTranslationZ(2f);
                    AnimatorSet anim = mAnimLeftIn.clone();
                    anim.setTarget(v);
                    anim.start();
                    v.getFocusMiddleView().setBackgroundColor(Color.YELLOW);
                    v.getFocusMiddleView().animate().setDuration(1000).rotationY(30).start();
                    v.getFocusNearView().animate().setDuration(1000).rotationY(40).start();
                } else {
                    v.setTranslationZ(1f);
                    AnimatorSet anim = mAnimLeftOut.clone();
                    anim.setTarget(v);
                    anim.start();
                    v.getFocusMiddleView().setBackgroundColor(Color.RED);
                    v.getFocusMiddleView().animate().setDuration(1000).rotationY(0).start();
                    v.getFocusNearView().animate().setDuration(1000).rotationY(0).start();
                }
                break;
            case View.FOCUS_LEFT:
                if (hasFocus) {
                    v.setTranslationZ(2f);
                    AnimatorSet anim = mAnimRightIn.clone();
                    anim.setTarget(v);
                    anim.start();
                    v.getFocusMiddleView().setBackgroundColor(Color.YELLOW);
                    v.getFocusMiddleView().animate().setDuration(1000).rotationY(-30).start();
                    v.getFocusNearView().animate().setDuration(1000).rotationY(-40).start();
                } else {
                    v.setTranslationZ(1f);
                    AnimatorSet anim = mAnimRightOut.clone();
                    anim.setTarget(v);
                    anim.start();
                    v.getFocusMiddleView().setBackgroundColor(Color.RED);
                    v.getFocusMiddleView().animate().setDuration(1000).rotationY(0).start();
                    v.getFocusNearView().animate().setDuration(1000).rotationY(0).start();
                }
                break;
            case View.FOCUS_UP:
                if (hasFocus) {
                    v.setTranslationZ(2f);
                    AnimatorSet anim = mAnimBottomIn.clone();
                    anim.setTarget(v);
                    anim.start();
                } else {
                    v.setTranslationZ(1f);
                    AnimatorSet anim = mAnimBottomOut.clone();
                    anim.setTarget(v);
                    anim.start();
                }
                break;
            case View.FOCUS_DOWN:
                if (hasFocus) {
                    v.setTranslationZ(2f);
                    AnimatorSet anim = mAnimTopIn.clone();
                    anim.setTarget(v);
                    anim.start();
                } else {
                    v.setTranslationZ(1f);
                    AnimatorSet anim = mAnimTopOut.clone();
                    anim.setTarget(v);
                    anim.start();
                }
                break;
        }
    }
}
