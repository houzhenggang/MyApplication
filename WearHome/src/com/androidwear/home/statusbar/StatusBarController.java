/**
 * @File StatusBarController.java
 * @Auth Hoasu
 * @Date 2014.08.15
 * @Desc Control all status bar swipe.
 */
package com.androidwear.home.statusbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.support.wearable.view.SimpleAnimatorListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.androidwear.home.R;
import com.androidwear.home.statusbar.AnimatorValue.OnGetFloat;
import com.androidwear.home.utils.MathUtil;

public class StatusBarController implements View.OnLayoutChangeListener, OnGetFloat {

    /* The threshold when status bar will be visible */
    private static final float STATUS_BAR_VISIBLE_THRESHOLD = 0.0f;

    private static final long STATUS_BAR_ANI_BACK_DURING = 300l;
    private static final long STATUS_BAR_ANI_BACK_DELAY = 0l;

    /* The root view that hold all status bar icons */
    private FrameLayout mSecondaryStatusBar;
    /* The view show Date */
    private TextClock mStatusBarDate;
    /* The view show the the percentage of remain power */
    private final TextView mBatteryView;
    /* The view show the icon of remain power */
    private final ImageView mBatteryIcon;
    /* The text prompt Zne Mode Mute is enter or exit */
    private final TextView mZenModeMute;
    /* Mark Zen Mode status icon */
    private final ImageView mZenModeIcon;
    /* Foreground mask */
    private final ImageView mForegroundMask;

    /* Secondary */
    private int mSecondaryStatusBarHeight;
    /* Calculated ZenMode Text height */
    private int mZenModeTextHeight;

    private int mShowMuteIconThreshold;

    /* The set store all views swipe tanslate distence */
    private AnimatorSet mBackAnimatorSet;
    /* The list of animator views */
    private final List<AnimatorValue> mAnimatorList = new ArrayList<AnimatorValue>();

    /* Receiver listening Battery changed infomation */
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String act = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(act)) {
                updateBatteryStatus(intent);
            }
        }

    };

    /* Record battery level and status */
    private int mBatteryLevel;

    public StatusBarController(Context context, ViewGroup v) {
        /* Get resource handler */
        Resources res = context.getResources();

        /* Inflate statusbar layout */
        mSecondaryStatusBar =
                (FrameLayout) View.inflate(context, R.layout.secondary_status_bar_circular, null);
        mSecondaryStatusBar.addOnLayoutChangeListener(this);

        /* Get Date handler, and set date format */
        mStatusBarDate = (TextClock) mSecondaryStatusBar.findViewById(R.id.status_bar_date);
        final String fmt = res.getString(R.string.status_bar_date_format);
        mStatusBarDate.setFormat12Hour(fmt);
        mStatusBarDate.setFormat24Hour(fmt);

        /* Battery status view handlers */
        mBatteryView = (TextView) mSecondaryStatusBar.findViewById(R.id.status_bar_battery);
        mBatteryIcon = (ImageView) mSecondaryStatusBar.findViewById(R.id.status_bar_battery_icon);
        mBatteryIcon.setImageDrawable(res.getDrawable(R.drawable.battery_level_icon));

        /* Zne Mode status view handlers */
        mZenModeMute = (TextView) mSecondaryStatusBar.findViewById(R.id.zen_mode_toggle_text);
        mZenModeIcon = (ImageView) mSecondaryStatusBar.findViewById(R.id.zen_mode_toggle_icon);

        /* Add secondary status bar to parent view */
        v.addView(mSecondaryStatusBar,
                new ViewGroup.LayoutParams(-1, (int) res.getDimension(R.dimen.cw_topbar_height_circular)));

        mForegroundMask = (ImageView) v.findViewById(R.id.fg_mask);
    }

    @Override
    public void onLayoutChange(View view,
            int l1, int t1, int r1, int b1,
            int l2, int t2, int r2, int b2) {
        /* Get layout height */
        mSecondaryStatusBarHeight = b1 - t1;
        /* Get zen mode text height */
        mZenModeTextHeight = mZenModeMute.getLayoutParams().height;
        /* Calculate Zen Mode Mute icon show threshold */
        mShowMuteIconThreshold = mSecondaryStatusBarHeight - mZenModeTextHeight;

        /* Set TranslationY to all views */
        final float transY = (-mShowMuteIconThreshold);
        mBatteryView.setTranslationY(transY);
        mStatusBarDate.setTranslationY(transY);
        mBatteryIcon.setTranslationY(transY);
        mZenModeMute.setTranslationY(transY);
        mZenModeIcon.setTranslationY(transY);
    }

    public void maybeSwipe(float threshold) {
        /* clear animator list */
        mAnimatorList.clear();

        if (threshold > STATUS_BAR_VISIBLE_THRESHOLD) {
            mSecondaryStatusBar.setVisibility(View.VISIBLE);
        }

        /* Set zen mode mute text to visible */
        mZenModeMute.setVisibility(View.VISIBLE);

        /* Get translation Y for views */
        final float transY =
            Math.min(threshold, mShowMuteIconThreshold + mZenModeTextHeight) - mShowMuteIconThreshold;

        /* Translate action */
        setTranslationY(mBatteryView, transY);
        setTranslationY(mStatusBarDate, transY);
        setTranslationY(mBatteryIcon, transY);
        setTranslationY(mZenModeMute, transY);
        setTranslationY(mZenModeIcon, Math.min(0.0F, threshold - mShowMuteIconThreshold));

        setAlpha(mZenModeMute, Math.min(1.0F, transY / 60.0F));

        final float freq = threshold / mSecondaryStatusBarHeight;
        setAlpha(mForegroundMask, MathUtil.clamp(freq, 0.0f, 1.0f));
    }

    public void translateY(float y) {
        setTranslationY(mSecondaryStatusBar, y);
    }

    public void setTranslationY(View v, float y) {
        if (v != null) {
            v.setTranslationY(y);
            /* Record animate action */
            mAnimatorList.add(AnimatorValue.CREATOR(v, View.TRANSLATION_Y, this));
        }
    }

    private void setAlpha(View v, float alpha) {
        if (v != null) {
            v.setAlpha(alpha);
            /* Record animate action */
            mAnimatorList.add(AnimatorValue.CREATOR(v, View.ALPHA, this));
        }
    }

    public void animateBack() {
        /* Set animate back sequence list. */
        ArrayList<Animator> listAnimator = new ArrayList<Animator>();
        for (AnimatorValue ani : mAnimatorList) {
            listAnimator.add(ani.getAnimator());
        }

        /* Do animate back by swipe sequence */
        mBackAnimatorSet = new AnimatorSet();
        mBackAnimatorSet.addListener(new SimpleAnimatorListener() {
            public void onAnimationComplete(Animator paramAnimator) {
                mZenModeMute.setVisibility(View.GONE);
            }
        });
        mBackAnimatorSet.setDuration(STATUS_BAR_ANI_BACK_DURING);
        mBackAnimatorSet.setStartDelay(STATUS_BAR_ANI_BACK_DELAY);
        mBackAnimatorSet.playTogether(listAnimator);
        mBackAnimatorSet.start();
    }

    @Override
    public float getStartFloat(AnimatorValue val) {
        float ret;
        if (View.ALPHA.equals(val.getValue())) {
            ret = val.getKey().getAlpha();
        } else {
            ret = val.getKey().getTranslationY();
        }
        return ret;
    }

    @Override
    public float getEndFloat(AnimatorValue val) {
        float ret;
        if (View.ALPHA.equals(val.getValue())) {
            ret = 0.0f;
        } else {
            ret = -mShowMuteIconThreshold;
        }
        return ret;
    }

    public void start(Context context) {
        context.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stop(Context context) {
        context.unregisterReceiver(mBatteryReceiver);
    }

    private void updateBatteryStatus(Intent intent) {
        final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        if (mBatteryLevel != level) {
            mBatteryLevel = level;
            mBatteryIcon.setImageLevel(mBatteryLevel);
            mBatteryView.setText(NumberFormat.getPercentInstance().format(mBatteryLevel / 100.0F));
        }
    }
}
