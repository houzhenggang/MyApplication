/**
 * @File AnimatorValue.java
 * @Auth Haosu
 * @Date 2014.08.15
 * @Desc Conver View and Property to Animator
 */
package com.androidwear.home.statusbar;

import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;

public class AnimatorValue implements Map.Entry<View, Property<View, Float>> {

    /*
     * Interface that will be used for get float[2].
     * Start float value set to float[0]; End float value set to float[1].
     */
    public static interface OnGetFloat {
        float getStartFloat(AnimatorValue val);
        float getEndFloat(AnimatorValue val);
    }

    private View mView;
    private Property<View, Float> mProp;
    private OnGetFloat mOnGetFloat;

    public static AnimatorValue CREATOR(View view, Property<View, Float> prop, OnGetFloat cb) {
        return new AnimatorValue(view, prop, cb);
    }

    private AnimatorValue(View view, Property<View, Float> prop, OnGetFloat cb) {
        mView = view;
        mProp = prop;
        mOnGetFloat = cb;
    }

    @Override
    public View getKey() {
        return mView;
    }

    @Override
    public Property<View, Float> getValue() {
        return mProp;
    }

    @Override
    public Property<View, Float> setValue(Property<View, Float> prop) {
        return (mProp = prop);
    }

    public void setOnGetEndValue(OnGetFloat val) {
        if (mOnGetFloat != val) {
            mOnGetFloat = val;
        }
    }

    public float[] getFloat() {
        float [] f = null;
        if (mOnGetFloat != null) {
            f = new float[2];
            f[0] = mOnGetFloat.getStartFloat(this);
            f[1] = mOnGetFloat.getEndFloat(this);
        }
        return f;
    }

    public Animator getAnimator() {
        return ObjectAnimator.ofFloat(mView, mProp, getFloat());
    }
}
