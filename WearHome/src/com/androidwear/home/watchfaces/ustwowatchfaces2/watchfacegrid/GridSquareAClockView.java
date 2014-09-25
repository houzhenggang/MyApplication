package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacegrid;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class GridSquareAClockView extends WatchFaceView {

    public GridSquareAClockView(Context context) {
        this(context, null);
    }

    public GridSquareAClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public GridSquareAClockView(Context context, AttributeSet attributeset,
            int i) {
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
        mOuterMargin = 8F;
        mOuterRect = new RectF();
        mTextMargin = 10F;
        mCurrentHour = -1F;
        mCurrentMinute = -1F;
        mAnimationInterval = 16;
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
        mHourGridSlotPaint = new Paint(1);
        mHourGridSlotPaint
                .setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        mHourGridSlotPaint.setColor(Color.argb(255, 234, 234, 234));
        mHourGridSlotPaint2 = new Paint(1);
        mHourGridSlotPaint2
                .setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        mHourGridSlotPaint2.setColor(Color.argb(255, 234, 234, 234));
        mMinuteGridSlotPaint = new Paint(1);
        mMinuteGridSlotPaint
                .setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        mMinuteGridSlotPaint.setColor(Color.argb(255, 234, 234, 234));
        mMinuteGridSlotPaint2 = new Paint(1);
        mMinuteGridSlotPaint2
                .setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        mMinuteGridSlotPaint2.setColor(Color.argb(255, 234, 234, 234));
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
        boolean flag = false;
        float f = mOuterRect.width() / 12F;
        java.util.Date date = Calendar.getInstance().getTime();
        float f1 = Calendar.getInstance().get(11);
        if (mCurrentHour == -1F)
            mCurrentHour = f1;
        float f2 = Calendar.getInstance().get(12);
        if (mCurrentMinute == -1F)
            mCurrentMinute = f2;
        float f3 = mWidth / 2;
        float f4 = mOuterRect.top + 2.0F * f;
        float f5 = mOuterRect.bottom - 5F * f;
        float f6 = f4 + (f5 - f4) / 2.0F;
        if (isAmbient())
            canvas.drawRect(mFaceRect, mFacePaintAmbient);
        else
            canvas.drawRect(mFaceRect, mFacePaintInteractive);
        canvas.drawRect(mOuterRect, mGridPaint);
        canvas.drawLine(mOuterRect.left, f + mOuterRect.top, mOuterRect.right,
                f + mOuterRect.top, mGridPaint);
        canvas.drawLine(mOuterRect.left, mOuterRect.top + 2.0F * f,
                mOuterRect.right, mOuterRect.top + 2.0F * f, mGridPaint);
        for (int i = 1; i <= 11; i++)
            canvas.drawLine(mOuterRect.left + f * (float) i, mOuterRect.top,
                    mOuterRect.left + f * (float) i, mOuterRect.top + 2.0F * f,
                    mGridPaint);

        boolean flag1;
        if (f1 != mCurrentHour) {
            mHandler.removeCallbacks(updateRunnable);
            int l = mHourGridSlotPaint.getAlpha() - mAnimationInterval;
            if (l <= 0)
                l = 0;
            mHourGridSlotPaint.setAlpha(l);
            float f16 = mOuterRect.left + f * (mCurrentHour % 12F);
            float f17 = mOuterRect.top + f * (float) (int) (mCurrentHour / 12F);
            canvas.drawRect(f16, f17, f16 + f, f17 + f, mHourGridSlotPaint);
            mHourGridSlotPaint2.setAlpha(255 - mHourGridSlotPaint.getAlpha());
            float f18 = mOuterRect.left + f * (f1 % 12F);
            float f19 = mOuterRect.top + f * (float) (int) (f1 / 12F);
            canvas.drawRect(f18, f19, f18 + f, f19 + f, mHourGridSlotPaint2);
            if (mHourGridSlotPaint.getAlpha() <= 0) {
                mHourGridSlotPaint.setAlpha(255);
                mCurrentHour = f1;
                flag1 = true;
            } else {
                flag = true;
                flag1 = false;
            }
        } else {
            float f7 = mOuterRect.left + f * (f1 % 12F);
            float f8 = mOuterRect.top + f * (float) (int) (f1 / 12F);
            canvas.drawRect(f7, f8, f7 + f, f8 + f, mHourGridSlotPaint);
            flag = false;
            flag1 = false;
        }
        canvas.drawLine(mOuterRect.left, mOuterRect.bottom - f,
                mOuterRect.right, mOuterRect.bottom - f, mGridPaint);
        canvas.drawLine(mOuterRect.left, mOuterRect.bottom - 2.0F * f,
                mOuterRect.right, mOuterRect.bottom - 2.0F * f, mGridPaint);
        canvas.drawLine(mOuterRect.left, mOuterRect.bottom - 3F * f,
                mOuterRect.right, mOuterRect.bottom - 3F * f, mGridPaint);
        canvas.drawLine(mOuterRect.left, mOuterRect.bottom - 4F * f,
                mOuterRect.right, mOuterRect.bottom - 4F * f, mGridPaint);
        canvas.drawLine(mOuterRect.left, mOuterRect.bottom - 5F * f,
                mOuterRect.right, mOuterRect.bottom - 5F * f, mGridPaint);
        for (int j = 1; j <= 11; j++) {
            canvas.drawLine(mOuterRect.left + f * (float) j, mOuterRect.bottom,
                    mOuterRect.left + f * (float) j,
                    mOuterRect.bottom - 5F * f, mGridPaint);
        }

        if (f2 != mCurrentMinute) {
            mHandler.removeCallbacks(updateRunnable);
            int k = mMinuteGridSlotPaint.getAlpha() - mAnimationInterval;
            if (k <= 0)
                k = 0;
            mMinuteGridSlotPaint.setAlpha(k);
            float f12 = mOuterRect.left + f * (mCurrentMinute % 12F);
            float f13 = f5 + f * (float) (int) (mCurrentMinute / 12F);
            canvas.drawRect(f12, f13, f12 + f, f13 + f, mMinuteGridSlotPaint);
            mMinuteGridSlotPaint2.setAlpha(255 - mMinuteGridSlotPaint
                    .getAlpha());
            float f14 = mOuterRect.left + f * (f2 % 12F);
            float f15 = f5 + f * (float) (int) (f2 / 12F);
            canvas.drawRect(f14, f15, f14 + f, f15 + f, mMinuteGridSlotPaint2);
            if (mMinuteGridSlotPaint.getAlpha() <= 0) {
                mMinuteGridSlotPaint.setAlpha(255);
                mCurrentMinute = f2;
                flag1 = true;
            } else {
                flag = true;
            }
        } else {
            float f9 = mOuterRect.left + f * (mCurrentMinute % 12F);
            float f10 = f5 + f * (float) (int) (mCurrentMinute / 12F);
            canvas.drawRect(f9, f10, f9 + f, f10 + f, mMinuteGridSlotPaint);
        }
        String s;
        String s1;
        float f11;
        s = mTimeTextFormat.format(date);
        mTimeTextPaint.getTextBounds(s, 0, s.length(), mTimeTextBounds);
        s1 = mDateTextFormat.format(date);
        mDateTextPaint.getTextBounds(s1, 0, s1.length(), mDateTextBounds);
        f11 = (float) mTimeTextBounds.height() + mTextMargin
                + (float) mDateTextBounds.height();
        canvas.drawText(s, f3,
                (f6 - f11 / 2.0F) + (float) mTimeTextBounds.height(),
                mTimeTextPaint);
        canvas.drawText(s1, f3, f6 + f11 / 2.0F, mDateTextPaint);
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
        mOuterRect = new RectF(mOuterMargin, mOuterMargin, (float) mWidth
                - mOuterMargin, (float) mHeight - mOuterMargin);
    }

    private static long UPDATE_RATE_MS = 1000L;
    private int mAnimationInterval;
    private float mCurrentHour;
    private float mCurrentMinute;
    private Rect mDateTextBounds;
    private SimpleDateFormat mDateTextFormat;
    private Paint mDateTextPaint;
    private int mFaceColorAmbient;
    private int mFaceColorInteractive;
    private Paint mFacePaintAmbient;
    private Paint mFacePaintInteractive;
    private RectF mFaceRect;
    private Paint mGridPaint;
    private Handler mHandler;
    private int mHeight;
    private Paint mHourGridSlotPaint;
    private Paint mHourGridSlotPaint2;
    private Paint mMinuteGridSlotPaint;
    private Paint mMinuteGridSlotPaint2;
    private float mOuterMargin;
    private RectF mOuterRect;
    private float mTextMargin;
    private Rect mTimeTextBounds;
    private SimpleDateFormat mTimeTextFormat;
    private Paint mTimeTextPaint;
    private int mWidth;
    private Runnable updateRunnable = new Runnable() {

        public void run() {
            update();
            mHandler.postDelayed(updateRunnable,
                    GridSquareAClockView.UPDATE_RATE_MS);
        }
    };

}
