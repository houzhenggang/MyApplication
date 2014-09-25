package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacegrid;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class GridCircleClockView extends WatchFaceView {

    public GridCircleClockView(Context context) {
        this(context, null);
    }

    public GridCircleClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public GridCircleClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mHandler = new Handler();
        mFaceColorAmbient = Color.argb(255, 58, 54, 50);
        mFaceColorInteractive = Color.argb(255, 206, 102, 20);
        mFaceRect = new RectF();
        mWidth = 0;
        mHeight = 0;
        mTimeTextBounds = new Rect();
        mTimeTextFormat = new SimpleDateFormat("HH:mm");
        mDateTextBounds = new Rect();
        mDateTextFormat = new SimpleDateFormat("EEE d MMM");
        mTextMargin = 10F;
        mOuterCircleMargin = 8F;
        mOuterCircleRect = new RectF();
        mMinuteCircleMargin = 24F;
        mMinuteCircleRect = new RectF();
        mHourCircleMargin = 40F;
        mHourCircleRect = new RectF();
        mPieSlicePath = new Path();
        mCurrentHour = -1F;
        mCurrentMinute = -1F;
        mAnimationInterval = 0.07F;
        init();
    }

    private void init() {
        mFacePaintAmbient = new Paint(1);
        mFacePaintAmbient.setStyle(android.graphics.Paint.Style.FILL);
        mFacePaintAmbient.setColor(mFaceColorAmbient);
        mFacePaintInteractive = new Paint(1);
        mFacePaintInteractive.setStyle(android.graphics.Paint.Style.FILL);
        mFacePaintInteractive.setColor(mFaceColorInteractive);
        mGridPaint = new Paint(1);
        mGridPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(1.0F);
        mGridPaint.setColor(Color.argb(255, 234, 234, 234));
        mGridSlotPaint = new Paint(1);
        mGridSlotPaint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        mGridSlotPaint.setColor(Color.argb(255, 234, 234, 234));
        mDarkFillPaint = new Paint(1);
        mDarkFillPaint.setColor(mFaceColorAmbient);
        mDarkFillPaint.setStyle(android.graphics.Paint.Style.FILL);
        mTimeTextPaint = new Paint(1);
        mTimeTextPaint.setTextSize(56F);
        mTimeTextPaint.setTypeface(Typeface.create("sans-serif", 0));
        mTimeTextPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        mTimeTextPaint.setColor(Color.argb(255, 234, 234, 234));
        mDateTextPaint = new Paint(1);
        mDateTextPaint.setTextSize(26F);
        mDateTextPaint.setTypeface(Typeface.create("sans-serif", 0));
        mDateTextPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        mDateTextPaint.setColor(Color.argb(255, 234, 234, 234));
        mHandler.postDelayed(updateRunnable, UPDATE_RATE_MS);
    }

    private void update() {
        postInvalidate();
    }

    protected boolean isContinuous() {
        return false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = mWidth / 2;
        float f1 = mHeight / 2;
        java.util.Date date = Calendar.getInstance().getTime();
        float f2 = Calendar.getInstance().get(10);
        if (mCurrentHour == -1F)
            mCurrentHour = f2;
        float f3 = Calendar.getInstance().get(12);
        if (mCurrentMinute == -1F)
            mCurrentMinute = f3;
        double d;
        double d1;
        if (isAmbient()) {
            canvas.drawRect(mFaceRect, mFacePaintAmbient);
            mDarkFillPaint.setColor(mFaceColorAmbient);
        } else {
            canvas.drawRect(mFaceRect, mFacePaintInteractive);
            mDarkFillPaint.setColor(mFaceColorInteractive);
        }
        canvas.drawCircle(f, f1, mOuterCircleRect.width() / 2.0F, mGridPaint);
        d = 360D / 60D;
        d1 = 270D - d / 2D;
        for (int i = 0; (double) i < 60D; i++) {
            double d16 = d1 + d * (double) i;
            double d17 = Math.toRadians(d16);
            float f8 = mOuterCircleRect.width() / 2.0F;
            double d18 = (double) f8 * Math.cos(d17) + (double) f;
            double d19 = (double) f8 * Math.sin(d17) + (double) f1;
            mPieSlicePath.reset();
            mPieSlicePath.moveTo(f, f1);
            mPieSlicePath.lineTo((float) d18, (float) d19);
            mPieSlicePath.arcTo(mOuterCircleRect, (float) d16, (float) d);
            mPieSlicePath.lineTo(f, f1);
            canvas.drawPath(mPieSlicePath, mGridPaint);
        }

        boolean j = f3 != mCurrentMinute;
        boolean flag = false;
        boolean flag1 = false;
        double d6;
        double d7;
        if (!j) {
            mHandler.removeCallbacks(updateRunnable);
            mCurrentMinute = mCurrentMinute + mAnimationInterval;
            if (mCurrentMinute >= f3) {
                mCurrentMinute = f3;
                flag1 = true;
            } else {
                flag = true;
                flag1 = false;
            }
        }
        double d2;
        double d3;
        float f4;
        double d4;
        double d5;
        int k;
        double d12;
        double d13;
        float f7;
        double d14;
        double d15;
        d2 = d1 + d * (double) mCurrentMinute;
        d3 = Math.toRadians(d2);
        f4 = mOuterCircleRect.width() / 2.0F;
        d4 = (double) f4 * Math.cos(d3) + (double) f;
        d5 = (double) f4 * Math.sin(d3) + (double) f1;
        mPieSlicePath.reset();
        mPieSlicePath.moveTo(f, f1);
        mPieSlicePath.lineTo((float) d4, (float) d5);
        mPieSlicePath.arcTo(mOuterCircleRect, (float) d2, (float) d);
        mPieSlicePath.lineTo(f, f1);
        canvas.drawPath(mPieSlicePath, mGridSlotPaint);
        canvas.drawCircle(f, f1, mMinuteCircleRect.width() / 2.0F, mGridPaint);
        canvas.drawCircle(f, f1, mMinuteCircleRect.width() / 2.0F - 1.0F,
                mDarkFillPaint);
        d6 = 360D / 12D;
        d7 = 270D - d6 / 2D;
        for (k = 0; (double) k < 12D; k++) {
            d12 = d7 + d6 * (double) k;
            d13 = Math.toRadians(d12);
            f7 = mMinuteCircleRect.width() / 2.0F;
            d14 = (double) f7 * Math.cos(d13) + (double) f;
            d15 = (double) f7 * Math.sin(d13) + (double) f1;
            mPieSlicePath.reset();
            mPieSlicePath.moveTo(f, f1);
            mPieSlicePath.lineTo((float) d14, (float) d15);
            mPieSlicePath.arcTo(mMinuteCircleRect, (float) d12, (float) d6);
            mPieSlicePath.lineTo(f, f1);
            canvas.drawPath(mPieSlicePath, mGridPaint);
        }

        if (f2 != mCurrentHour) {
            mHandler.removeCallbacks(updateRunnable);
            mCurrentHour = mCurrentHour + mAnimationInterval;
            if (mCurrentHour >= f2) {
                mCurrentHour = f2;
                flag1 = true;
            } else {
                flag = true;
            }
        }
        double d8;
        double d9;
        float f5;
        double d10;
        double d11;
        String s;
        String s1;
        float f6;
        d8 = d7 + d6 * (double) mCurrentHour;
        d9 = Math.toRadians(d8);
        f5 = mMinuteCircleRect.width() / 2.0F;
        d10 = (double) f5 * Math.cos(d9) + (double) f;
        d11 = (double) f5 * Math.sin(d9) + (double) f1;
        mPieSlicePath.reset();
        mPieSlicePath.moveTo(f, f1);
        mPieSlicePath.lineTo((float) d10, (float) d11);
        mPieSlicePath.arcTo(mMinuteCircleRect, (float) d8, (float) d6);
        mPieSlicePath.lineTo(f, f1);
        canvas.drawPath(mPieSlicePath, mGridSlotPaint);
        canvas.drawCircle(f, f1, mHourCircleRect.width() / 2.0F, mGridPaint);
        canvas.drawCircle(f, f1, mHourCircleRect.width() / 2.0F - 1.0F,
                mDarkFillPaint);
        s = mTimeTextFormat.format(date);
        mTimeTextPaint.getTextBounds(s, 0, s.length(), mTimeTextBounds);
        s1 = mDateTextFormat.format(date);
        mDateTextPaint.getTextBounds(s1, 0, s1.length(), mDateTextBounds);
        f6 = (float) mTimeTextBounds.height() + mTextMargin
                + (float) mDateTextBounds.height();
        canvas.drawText(s, f,
                (f1 - f6 / 2.0F) + (float) mTimeTextBounds.height(),
                mTimeTextPaint);
        canvas.drawText(s1, f, f1 + f6 / 2.0F, mDateTextPaint);
        if (flag)
            invalidate();
        if (flag1)
            mHandler.postDelayed(updateRunnable, UPDATE_RATE_MS);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mWidth = i;
        mHeight = i;
        mFaceRect = new RectF(0.0F, 0.0F, mWidth, mHeight);
        mOuterCircleRect = new RectF(mOuterCircleMargin, mOuterCircleMargin,
                (float) mWidth - mOuterCircleMargin, (float) mHeight
                        - mOuterCircleMargin);
        mMinuteCircleRect = new RectF(mMinuteCircleMargin, mMinuteCircleMargin,
                (float) mWidth - mMinuteCircleMargin, (float) mHeight
                        - mMinuteCircleMargin);
        mHourCircleRect = new RectF(mHourCircleMargin, mHourCircleMargin,
                (float) mWidth - mHourCircleMargin, (float) mHeight
                        - mHourCircleMargin);
    }

    private static long UPDATE_RATE_MS = 1000L;
    private float mAnimationInterval;
    private float mCurrentHour;
    private float mCurrentMinute;
    private Paint mDarkFillPaint;
    private Rect mDateTextBounds;
    private SimpleDateFormat mDateTextFormat;
    private Paint mDateTextPaint;
    private int mFaceColorAmbient;
    private int mFaceColorInteractive;
    private Paint mFacePaintAmbient;
    private Paint mFacePaintInteractive;
    private RectF mFaceRect;
    private Paint mGridPaint;
    private Paint mGridSlotPaint;
    private Handler mHandler;
    private int mHeight;
    private float mHourCircleMargin;
    private RectF mHourCircleRect;
    private float mMinuteCircleMargin;
    private RectF mMinuteCircleRect;
    private float mOuterCircleMargin;
    private RectF mOuterCircleRect;
    private Path mPieSlicePath;
    private float mTextMargin;
    private Rect mTimeTextBounds;
    private SimpleDateFormat mTimeTextFormat;
    private Paint mTimeTextPaint;
    private int mWidth;
    private Runnable updateRunnable = new Runnable() {

        public void run() {
            update();
            mHandler.postDelayed(updateRunnable,
                    GridCircleClockView.UPDATE_RATE_MS);
        }
    };

}
