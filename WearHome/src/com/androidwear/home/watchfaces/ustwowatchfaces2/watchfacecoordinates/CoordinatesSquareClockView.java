// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacecoordinates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceFillPaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceStrokePaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceTextPaint;

public class CoordinatesSquareClockView extends WatchFaceView {

    public CoordinatesSquareClockView(Context context) {
        this(context, null);
    }

    public CoordinatesSquareClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public CoordinatesSquareClockView(Context context,
            AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mFaceColorAmbient = 0xff000000;
        mFaceColorInteractive = -1;
        mBorderColorAmbient = Color.argb(255, 75, 75, 75);
        mBorderColorInteractive = Color.argb(255, 236, 236, 236);
        mDotColorAmbient = Color.argb(255, 75, 75, 75);
        mDotColorInteractive = Color.argb(255, 188, 188, 188);
        mAxesTextColorAmbient = Color.argb(255, 75, 75, 75);
        mAxesTextColorInteractive = Color.argb(255, 189, 189, 189);
        mCrosshairColorAmbient = -1;
        mCrosshairColorInteractive = Color.argb(255, 255, 0, 71);
        mTargetColorAmbient = -1;
        mTargetColorInteractive = Color.argb(255, 255, 0, 71);
        mFacePaint = new WatchFaceFillPaint();
        mBorderPaint = new WatchFaceStrokePaint();
        mDotPaint = new WatchFaceFillPaint();
        mCrosshairPaint = new WatchFaceStrokePaint();
        mTargetPaint = new WatchFaceStrokePaint();
        mAxesTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.CENTER);
        mBorderRect = new RectF();
        mAxesTextBounds = new Rect();
        mBackgroundBitmapCanvas = new Canvas();
        mLeftCrosshairStartPoint = new PointF();
        mLeftCrosshairEndPoint = new PointF();
        mTopCrosshairStartPoint = new PointF();
        mTopCrosshairEndPoint = new PointF();
        mRightCrosshairStartPoint = new PointF();
        mRightCrosshairEndPoint = new PointF();
        mBottomCrosshairStartPoint = new PointF();
        mBottomCrosshairEndPoint = new PointF();
        mTargetCenterPoint = new PointF();
        init();
    }

    private void generateBackgroundBitmap(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        mBackgroundBitmapCanvas.drawRect(mBorderRect, mBorderPaint);
        float f = getFaceRadius() - mBorderRadius;
        float f1 = (2.0F * mBorderRadius) / 14F;
        for (int i = 1; (float) i < 14F; i++) {
            for (int l = 1; (float) l < 14F; l++) {
                float f7 = f + f1 * (float) i;
                float f8 = f + f1 * (float) l;
                mBackgroundBitmapCanvas.drawCircle(f7, f8, mDotRadius,
                        mDotPaint);
            }

        }

        float f2 = (getFaceRadius() - mBorderRadius) / 2.0F;
        for (int j = 1; (float) j < 14F; j++)
            if ((j - 1) % 2 == 0) {
                String s1 = Integer.toString(5 * (j - 1));
                float f6 = f + f1 * (float) j;
                mAxesTextPaint
                        .setTextAlign(android.graphics.Paint.Align.CENTER);
                mAxesTextPaint.getTextBounds(s1, 0, s1.length(),
                        mAxesTextBounds);
                mBackgroundBitmapCanvas.drawText(s1, f6, f2
                        + (float) (mAxesTextBounds.height() / 2),
                        mAxesTextPaint);
            }

        float f3 = (getFaceRadius() - mBorderRadius) / 2.0F;
        mAxesTextPaint.getTextBounds("12", 0, 2, mAxesTextBounds);
        float f4 = mAxesTextBounds.width();
        for (int k = 1; (float) k < 14F; k++)
            if ((k - 1) % 2 == 0) {
                String s = Integer.toString(k - 1);
                float f5 = (getFaceRadius() + mBorderRadius) - f1 * (float) k;
                mAxesTextPaint.setTextAlign(android.graphics.Paint.Align.RIGHT);
                mAxesTextPaint.getTextBounds(s, 0, s.length(), mAxesTextBounds);
                mBackgroundBitmapCanvas.drawText(s, (f3 + f4 / 2.0F) - 1.0F, f5
                        + (float) (mAxesTextBounds.height() / 2),
                        mAxesTextPaint);
            }

    }

    private void init() {
        mBorderPaint.setStrokeWidth(mBorderStrokeWidth);
        mCrosshairPaint.setStrokeWidth(mCrosshairStrokeWidth);
        mTargetPaint.setStrokeWidth(mTargetStrokeWidth);
        mAxesTextPaint.setTextSize(mAxesTextSize);
        mBorderRect.set(getFaceRadius() - mBorderRadius, getFaceRadius()
                - mBorderRadius, getFaceRadius() + mBorderRadius,
                getFaceRadius() + mBorderRadius);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        if (isAmbient()) {
            mFacePaint.setColor(mFaceColorAmbient);
            mBorderPaint.setColor(mBorderColorAmbient);
            mDotPaint.setColor(mDotColorAmbient);
            mAxesTextPaint.setColor(mAxesTextColorAmbient);
            mCrosshairPaint.setColor(mCrosshairColorAmbient);
            mTargetPaint.setColor(mTargetColorAmbient);
            return;
        } else {
            mFacePaint.setColor(mFaceColorInteractive);
            mBorderPaint.setColor(mBorderColorInteractive);
            mDotPaint.setColor(mDotColorInteractive);
            mAxesTextPaint.setColor(mAxesTextColorInteractive);
            mCrosshairPaint.setColor(mCrosshairColorInteractive);
            mTargetPaint.setColor(mTargetColorInteractive);
            return;
        }
    }

    private void updateCrosshairs(WatchCurrentTime watchcurrenttime) {
        float f = 2.0F * mBorderRadius;
        float f1 = f / 14F;
        float f2 = watchcurrenttime.get12Hour() + watchcurrenttime.getMinute()
                / 60F;
        float f3 = watchcurrenttime.getMinute() + watchcurrenttime.getSecond()
                / 60F;
        float f4 = (getFaceRadius() + mBorderRadius) - f1 - (f2 / 12F)
                * (f - 2.0F * f1);
        float f5 = f1 + (getFaceRadius() - mBorderRadius) + (f3 / 60F)
                * (f - 2.0F * f1);
        mTargetCenterPoint.set(f5, f4);
        float f6 = (getFaceRadius() - mBorderRadius) + mBorderStrokeWidth
                / 2.0F;
        float f7 = f4 - mTargetRadius;
        mTopCrosshairStartPoint.set(f5, f6);
        mTopCrosshairEndPoint.set(f5, f7);
        float f8 = f4 + mTargetRadius;
        float f9 = (getFaceRadius() + mBorderRadius) - mBorderStrokeWidth
                / 2.0F;
        mBottomCrosshairStartPoint.set(f5, f8);
        mBottomCrosshairEndPoint.set(f5, f9);
        float f10 = (getFaceRadius() - mBorderRadius) + mBorderStrokeWidth
                / 2.0F;
        float f11 = f5 - mTargetRadius;
        mLeftCrosshairStartPoint.set(f10, f4);
        mLeftCrosshairEndPoint.set(f11, f4);
        float f12 = f5 + mTargetRadius;
        float f13 = (getFaceRadius() + mBorderRadius) - mBorderStrokeWidth
                / 2.0F;
        mRightCrosshairStartPoint.set(f12, f4);
        mRightCrosshairEndPoint.set(f13, f4);
    }

    protected boolean isContinuous() {
        return false;
    }

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
        generateBackgroundBitmap(watchcurrenttime);
    }

    protected void onDraw(Canvas canvas) {
        WatchCurrentTime.getCurrent(mCurrentTime);
        canvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        canvas.drawLine(mLeftCrosshairStartPoint.x, mLeftCrosshairStartPoint.y,
                mLeftCrosshairEndPoint.x, mLeftCrosshairEndPoint.y,
                mCrosshairPaint);
        canvas.drawLine(mTopCrosshairStartPoint.x, mTopCrosshairStartPoint.y,
                mTopCrosshairEndPoint.x, mTopCrosshairEndPoint.y,
                mCrosshairPaint);
        canvas.drawLine(mRightCrosshairStartPoint.x,
                mRightCrosshairStartPoint.y, mRightCrosshairEndPoint.x,
                mRightCrosshairEndPoint.y, mCrosshairPaint);
        canvas.drawLine(mBottomCrosshairStartPoint.x,
                mBottomCrosshairStartPoint.y, mBottomCrosshairEndPoint.x,
                mBottomCrosshairEndPoint.y, mCrosshairPaint);
        canvas.drawCircle(mTargetCenterPoint.x, mTargetCenterPoint.y,
                mTargetRadius, mTargetPaint);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
        generateBackgroundBitmap(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mBorderRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_border_radius);
        mBorderStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_border_stroke_width);
        mCrosshairStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_crosshair_stroke_width);
        mTargetStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_target_stroke_width);
        mTargetRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_target_radius);
        mDotRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_dot_diameter) / 2.0F;
        mAxesTextSize = DisplayHelper.getPixels(this,
                R.dimen.coordinates_square_axes_text_size);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        updateCrosshairs(watchcurrenttime);
    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
        updateCrosshairs(watchcurrenttime);
    }

    protected void onUpdateMinuteContinuous(WatchCurrentTime watchcurrenttime) {
        updateCrosshairs(watchcurrenttime);
    }

    private Rect mAxesTextBounds;
    private int mAxesTextColorAmbient;
    private int mAxesTextColorInteractive;
    private Paint mAxesTextPaint;
    private float mAxesTextSize;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private int mBorderColorAmbient;
    private int mBorderColorInteractive;
    private Paint mBorderPaint;
    private float mBorderRadius;
    private RectF mBorderRect;
    private float mBorderStrokeWidth;
    private PointF mBottomCrosshairEndPoint;
    private PointF mBottomCrosshairStartPoint;
    private int mCrosshairColorAmbient;
    private int mCrosshairColorInteractive;
    private Paint mCrosshairPaint;
    private float mCrosshairStrokeWidth;
    private WatchCurrentTime mCurrentTime;
    private int mDotColorAmbient;
    private int mDotColorInteractive;
    private Paint mDotPaint;
    private float mDotRadius;
    private int mFaceColorAmbient;
    private int mFaceColorInteractive;
    private Paint mFacePaint;
    private PointF mLeftCrosshairEndPoint;
    private PointF mLeftCrosshairStartPoint;
    private PointF mRightCrosshairEndPoint;
    private PointF mRightCrosshairStartPoint;
    private PointF mTargetCenterPoint;
    private int mTargetColorAmbient;
    private int mTargetColorInteractive;
    private Paint mTargetPaint;
    private float mTargetRadius;
    private float mTargetStrokeWidth;
    private PointF mTopCrosshairEndPoint;
    private PointF mTopCrosshairStartPoint;
}
