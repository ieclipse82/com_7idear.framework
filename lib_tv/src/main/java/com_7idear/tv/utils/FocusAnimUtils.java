package com_7idear.tv.utils;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com_7idear.tv.R;


/**
 * @author ieclipse 19-11-26
 * @description
 */
public class FocusAnimUtils {

    private static FocusAnimUtils mInstance;

    private Context mContext;

    private AnimatorSet mAnimLeftOut, mAnimLeftIn;
    private AnimatorSet mAnimRightOut, mAnimRightIn;
    private AnimatorSet mAnimTopOut, mAnimTopIn;
    private AnimatorSet mAnimBottomOut, mAnimBottomIn;
    private AnimatorSet mAnimNullOut, mAnimNullIn;

    FocusAnimUtils() {
        //TODO
        mContext = null;
        init();
    }

    public static FocusAnimUtils getInstance() {
        if (mInstance == null) {
            synchronized (FocusAnimUtils.class) {
                if (mInstance == null) mInstance = new FocusAnimUtils();
            }
        }
        return mInstance;
    }

    private void init() {
        mAnimLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_left_out);
        mAnimLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_left_in);
        mAnimRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_right_out);
        mAnimRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_right_in);
        mAnimTopOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_top_out);
        mAnimTopIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_top_in);
        mAnimBottomOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_bottom_out);
        mAnimBottomIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_bottom_in);
        mAnimNullOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                R.animator.anim_focus_change_null_out);
        mAnimNullIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
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
    }

    public void animFocusLeftIn(View v) {

    }
}
