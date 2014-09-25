package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfaceaviator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TimeHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TrigHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceFillPaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceStrokePaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceTextPaint;

public class AviatorClockView extends WatchFaceView {

    public AviatorClockView(Context context) {
        this(context, null);
    }

    public AviatorClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public AviatorClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mBigTickMargin = 4F;
        mBigTickStrokeWidth = 2.0F;
        mDateCircleBorderStrokeWidth = 2.0F;
        mSecondHandStrokeWidth = 1.0F;
        mDotRadius = 2.0F;
        mFaceColorAmbient = 0xff000000;
        mFaceColorLightInteractive = -1;
        mFaceColorDarkInteractive = 0xff000000;
        mTickHandColorAmbient = -1;
        mTickHandColorLightInteractive = 0xff000000;
        mTickHandColorDarkInteractive = -1;
        mSecondHandColor = Color.argb(255, 162, 118, 0);
        mFacePaint = new WatchFaceFillPaint();
        mBigTickPaint = new WatchFaceStrokePaint();
        mSmallTickPaint = new WatchFaceFillPaint();
        mTopTickPaint = new WatchFaceStrokePaint();
        mSecondHandPaint = new WatchFaceStrokePaint();
        mMinuteHandPaint = new WatchFaceFillPaint();
        mMinuteClearPaint = new WatchFaceFillPaint();
        mHourHandPaint = new WatchFaceFillPaint();
        mCenterDotPaint = new WatchFaceFillPaint();
        mDateCirclePaintBorder = new WatchFaceStrokePaint();
        mDateCircleTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.CENTER);
        mSecondHandRect = new RectF();
        mDateCircleTextBounds = new Rect();
        mMinuteHandOuterRect = new RectF();
        mMinuteHandInnerRect = new RectF();
        mHourHandRect = new RectF();
        mDotCenterPoint = new PointF();
        mBigTickStartPoint = new PointF();
        mBigTickEndPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mHourHandBitmapCanvas = new Canvas();
        mMinuteHandBitmapCanvas = new Canvas();
        init();
    }

    private void init() {
        mBigTickPaint.setStrokeWidth(mBigTickStrokeWidth);
        mTopTickPaint.setStrokeWidth(mBigTickStrokeWidth);
        mDateCirclePaintBorder.setStrokeWidth(mDateCircleBorderStrokeWidth);
        mSecondHandPaint.setStrokeWidth(mSecondHandStrokeWidth);
        mDateCircleTextPaint.setTextSize(mCircleDateTextSize);
        mMinuteClearPaint.setXfermode(new PorterDuffXfermode(
                android.graphics.PorterDuff.Mode.CLEAR));
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
            mHourHandBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mHourHandBitmapCanvas.setBitmap(mHourHandBitmap);
            mMinuteHandBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mMinuteHandBitmapCanvas.setBitmap(mMinuteHandBitmap);
            mSecondHandRect.set(getCenterX() - mCenterCircleRadius,
                    getCenterY() - mCenterCircleRadius, getCenterX()
                            + mSecondHandRadius, getCenterY()
                            + mCenterCircleRadius);
            mMinuteHandOuterRect.set(
                    1.0F + (getCenterX() - mCenterCircleRadius),
                    1.0F + (getCenterY() - mCenterCircleRadius),
                    (getCenterX() + mSecondHandRadius) - 2.0F,
                    (getCenterY() + mCenterCircleRadius) - 1.0F);
            mMinuteHandInnerRect.set(2.0F + mMinuteHandOuterRect.left,
                    2.0F + mMinuteHandOuterRect.top, mMinuteHandOuterRect.left
                            + (3F * mSecondHandRadius) / 4F,
                    mMinuteHandOuterRect.bottom - 2.0F);
            mHourHandRect.set(mMinuteHandInnerRect.left - 1.0F,
                    mMinuteHandInnerRect.top - 1.0F,
                    1.0F + mMinuteHandInnerRect.right,
                    1.0F + mMinuteHandInnerRect.bottom);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        if (isAmbient()) {
            mFacePaint.setColor(mFaceColorAmbient);
            mDateCirclePaintBorder.setColor(mTickHandColorAmbient);
            mDateCircleTextPaint.setColor(mTickHandColorAmbient);
            mCenterDotPaint.setColor(mFaceColorAmbient);
            mBigTickPaint.setColor(mTickHandColorAmbient);
            mSmallTickPaint.setColor(mTickHandColorAmbient);
            mTopTickPaint.setColor(mTickHandColorAmbient);
            mHourHandPaint.setColor(mTickHandColorAmbient);
            mMinuteHandPaint.setColor(mTickHandColorAmbient);
        } else {
            if (watchcurrenttime.get24Hour() >= 6F
                    && watchcurrenttime.get24Hour() < 18F) {
                mFacePaint.setColor(mFaceColorLightInteractive);
                mCenterDotPaint.setColor(mFaceColorLightInteractive);
                mBigTickPaint.setColor(mTickHandColorLightInteractive);
                mSmallTickPaint.setColor(mTickHandColorLightInteractive);
                mHourHandPaint.setColor(mTickHandColorLightInteractive);
                mMinuteHandPaint.setColor(mTickHandColorLightInteractive);
            } else {
                mFacePaint.setColor(mFaceColorDarkInteractive);
                mCenterDotPaint.setColor(mFaceColorDarkInteractive);
                mBigTickPaint.setColor(mTickHandColorDarkInteractive);
                mSmallTickPaint.setColor(mTickHandColorDarkInteractive);
                mHourHandPaint.setColor(mTickHandColorDarkInteractive);
                mMinuteHandPaint.setColor(mTickHandColorDarkInteractive);
            }
            mDateCirclePaintBorder.setColor(mSecondHandColor);
            mDateCircleTextPaint.setColor(mSecondHandColor);
            mTopTickPaint.setColor(mSecondHandColor);
            mSecondHandPaint.setColor(mSecondHandColor);
        }
    }

    protected boolean isContinuous() {
        return true;
    }

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onDraw(Canvas canvas) {
        WatchCurrentTime.getCurrent(mCurrentTime);
        canvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        if (!isAmbient()) {
            canvas.save();
            canvas.rotate(mCurrentTime.getSecondDegreesContinuous(),
                    getCenterX(), getCenterY());
            canvas.drawRoundRect(mSecondHandRect,
                    mSecondHandRect.height() / 2.0F,
                    mSecondHandRect.height() / 2.0F, mSecondHandPaint);
            canvas.restore();
        }
        canvas.save();
        canvas.rotate(mCurrentTime.getMinuteDegreesContinuous(), getCenterX(),
                getCenterY());
        canvas.drawBitmap(mMinuteHandBitmap, 0.0F, 0.0F, null);
        canvas.restore();
        canvas.save();
        canvas.rotate(mCurrentTime.getHourDegreesContinuous(), getCenterX(),
                getCenterY());
        canvas.drawBitmap(mHourHandBitmap, 0.0F, 0.0F, null);
        canvas.restore();
        canvas.drawCircle(getCenterX(), getCenterY(), mCenterDotRadius,
                mCenterDotPaint);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mTickEdgeMargin = DisplayHelper.getPixels(this, R.dimen.aviator_tick_edge_margin);
        mBigTickLength = DisplayHelper.getPixels(this, R.dimen.aviator_big_tick_length);
        mDateCircleRectRadius = DisplayHelper.getPixels(this, R.dimen.aviator_date_circle_radius);
        mDateCircleRectRightMargin = DisplayHelper.getPixels(this, R.dimen.aviator_date_circle_right_margin);
        mCenterCircleRadius = DisplayHelper.getPixels(this, R.dimen.aviator_center_circle_radius);
        mCenterDotRadius = DisplayHelper.getPixels(this, R.dimen.aviator_center_dot_radius);
        mCircleDateTextSize = DisplayHelper.getPixels(this, R.dimen.aviator_date_circle_text_size);
        mSecondHandRadius = getFaceRadius() - mTickEdgeMargin - mBigTickLength
                - mTickEdgeMargin;
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        setColors(watchcurrenttime);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        int i = 0;
        while (i < 60) {
            float f1 = (float) Math.toRadians(TimeHelper
                    .getDegreesFromMinute(i));
            TrigHelper.getPointOnCircle(mDotCenterPoint, getFaceRadius()
                    - mTickEdgeMargin - mDotRadius, f1, getCenter());
            TrigHelper.getPointOnCircle(mBigTickStartPoint, getFaceRadius()
                    - mTickEdgeMargin - mBigTickLength, f1, getCenter());
            TrigHelper.getPointOnCircle(mBigTickEndPoint, getFaceRadius()
                    - mTickEdgeMargin, f1, getCenter());
            if (i == 0) {
                if (isAmbient()) {
                    mBackgroundBitmapCanvas.drawLine(mBigTickStartPoint.x
                            - mBigTickMargin, mBigTickStartPoint.y,
                            mBigTickEndPoint.x - mBigTickMargin,
                            mBigTickEndPoint.y, mBigTickPaint);
                    mBackgroundBitmapCanvas.drawLine(mBigTickStartPoint.x
                            + mBigTickMargin, mBigTickStartPoint.y,
                            mBigTickEndPoint.x + mBigTickMargin,
                            mBigTickEndPoint.y, mBigTickPaint);
                } else {
                    mBackgroundBitmapCanvas.drawLine(mBigTickStartPoint.x
                            - mBigTickMargin, mBigTickStartPoint.y,
                            mBigTickEndPoint.x - mBigTickMargin,
                            mBigTickEndPoint.y, mTopTickPaint);
                    mBackgroundBitmapCanvas.drawLine(mBigTickStartPoint.x
                            + mBigTickMargin, mBigTickStartPoint.y,
                            mBigTickEndPoint.x + mBigTickMargin,
                            mBigTickEndPoint.y, mTopTickPaint);
                }
            } else if (i % 5 == 0) {
                mBackgroundBitmapCanvas.drawLine(mBigTickStartPoint.x,
                        mBigTickStartPoint.y, mBigTickEndPoint.x,
                        mBigTickEndPoint.y, mBigTickPaint);
            } else {
                mBackgroundBitmapCanvas.drawCircle(mDotCenterPoint.x,
                        mDotCenterPoint.y, mDotRadius, mSmallTickPaint);
            }
            i++;
        }
        float f = getFaceWidth() - mTickEdgeMargin - mBigTickLength
                - mDateCircleRectRightMargin - mDateCircleRectRadius;
        String s = TimeHelper.getTwoDigitNumber((int) watchcurrenttime
                .getDayOfMonth());
        mDateCircleTextPaint.getTextBounds(s, 0, s.length(),
                mDateCircleTextBounds);
        mBackgroundBitmapCanvas.drawCircle(f, getCenterY(),
                mDateCircleRectRadius, mDateCirclePaintBorder);
        mBackgroundBitmapCanvas.drawText(s, f, getCenterY()
                + (float) (mDateCircleTextBounds.height() / 2),
                mDateCircleTextPaint);
        DisplayHelper.clearCanvas(mHourHandBitmapCanvas);
        mHourHandBitmapCanvas.drawRoundRect(mHourHandRect,
                mHourHandRect.height() / 2.0F, mHourHandRect.height() / 2.0F,
                mHourHandPaint);
        DisplayHelper.clearCanvas(mMinuteHandBitmapCanvas);
        mMinuteHandBitmapCanvas.drawPaint(mMinuteClearPaint);
        mMinuteHandBitmapCanvas.drawRoundRect(mMinuteHandOuterRect,
                mMinuteHandOuterRect.height() / 2.0F,
                mMinuteHandOuterRect.height() / 2.0F, mMinuteHandPaint);
        mMinuteHandBitmapCanvas.drawRoundRect(mMinuteHandInnerRect,
                mMinuteHandInnerRect.height() / 2.0F,
                mMinuteHandInnerRect.height() / 2.0F, mMinuteClearPaint);
    }

    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private PointF mBigTickEndPoint;
    private float mBigTickLength;
    private float mBigTickMargin;
    private Paint mBigTickPaint;
    private PointF mBigTickStartPoint;
    private float mBigTickStrokeWidth;
    private float mCenterCircleRadius;
    private Paint mCenterDotPaint;
    private float mCenterDotRadius;
    private float mCircleDateTextSize;
    private WatchCurrentTime mCurrentTime;
    private float mDateCircleBorderStrokeWidth;
    private Paint mDateCirclePaintBorder;
    private float mDateCircleRectRadius;
    private float mDateCircleRectRightMargin;
    private Rect mDateCircleTextBounds;
    private Paint mDateCircleTextPaint;
    private PointF mDotCenterPoint;
    private float mDotRadius;
    private int mFaceColorAmbient;
    private int mFaceColorDarkInteractive;
    private int mFaceColorLightInteractive;
    private Paint mFacePaint;
    private Bitmap mHourHandBitmap;
    private Canvas mHourHandBitmapCanvas;
    private Paint mHourHandPaint;
    private RectF mHourHandRect;
    private Paint mMinuteClearPaint;
    private Bitmap mMinuteHandBitmap;
    private Canvas mMinuteHandBitmapCanvas;
    private RectF mMinuteHandInnerRect;
    private RectF mMinuteHandOuterRect;
    private Paint mMinuteHandPaint;
    private int mSecondHandColor;
    private Paint mSecondHandPaint;
    private float mSecondHandRadius;
    private RectF mSecondHandRect;
    private float mSecondHandStrokeWidth;
    private Paint mSmallTickPaint;
    private float mTickEdgeMargin;
    private int mTickHandColorAmbient;
    private int mTickHandColorDarkInteractive;
    private int mTickHandColorLightInteractive;
    private Paint mTopTickPaint;
}
