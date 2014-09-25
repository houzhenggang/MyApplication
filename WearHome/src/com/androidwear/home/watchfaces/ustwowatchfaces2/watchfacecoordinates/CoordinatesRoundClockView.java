package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacecoordinates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TrigHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceFillPaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceStrokePaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceTextPaint;

public class CoordinatesRoundClockView extends WatchFaceView {

    public CoordinatesRoundClockView(Context context) {
        this(context, null);
    }

    public CoordinatesRoundClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public CoordinatesRoundClockView(Context context,
            AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mFaceColorAmbient = 0xff000000;
        mFaceColorInteractive = -1;
        mOuterCircleColorAmbient = Color.argb(255, 75, 75, 75);
        mOuterCircleColorInteractive = Color.argb(255, 236, 236, 236);
        mInnerCircleColorAmbient = -1;
        mInnerCircleColorInteractive = Color.argb(255, 255, 0, 71);
        mMinuteCirclesColorAmbient = Color.argb(255, 75, 75, 75);
        mMinuteCirclesColorInteractive = Color.argb(255, 241, 241, 241);
        mHourLineColorAmbient = -1;
        mHourLineColorInteractive = Color.argb(255, 255, 0, 71);
        mTargetColorAmbient = -1;
        mTargetColorInteractive = Color.argb(255, 255, 0, 71);
        mAxesTextColorAmbient = Color.argb(255, 75, 75, 75);
        mAxesTextColorInteractive = Color.argb(255, 189, 189, 189);
        mFacePaint = new WatchFaceFillPaint();
        mOuterCirclePaint = new WatchFaceStrokePaint();
        mInnerCirclePaint = new WatchFaceStrokePaint();
        mMinuteCirclesPaint = new WatchFaceStrokePaint();
        mHourLinePaint = new WatchFaceStrokePaint();
        mTargetPaint = new WatchFaceStrokePaint();
        mAxesTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.CENTER);
        mAxesTextBounds = new Rect();
        mNumberTextPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mHourLine1StartPoint = new PointF();
        mHourLine1EndPoint = new PointF();
        mHourLine2StartPoint = new PointF();
        mHourLine2EndPoint = new PointF();
        mTargetCenterPoint = new PointF();
        init();
    }

    private void generateBackgroundBitmap(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        mBackgroundBitmapCanvas.drawCircle(getCenterX(), getCenterY(),
                mOuterCircleRadius, mOuterCirclePaint);
        mBackgroundBitmapCanvas.drawCircle(getCenterX(), getCenterY(),
                mInnerCircleRadius, mInnerCirclePaint);
        float f = (mOuterCircleRadius - mInnerCircleRadius) / 14F;
        for (int i = 1; (float) i < 14F; i++)
            mBackgroundBitmapCanvas.drawCircle(getCenterX(), getCenterY(),
                    mInnerCircleRadius + f * (float) i, mMinuteCirclesPaint);

        float f1 = mOuterCircleRadius + (getFaceRadius() - mOuterCircleRadius)
                / 2.0F;
        for (int j = 0; j < 12; j++) {
            int k = j;
            if (k == 0)
                k = 12;
            String s = Integer.toString(k);
            float f2 = (float) Math.toRadians(360D * ((double) j / 12D) - 90D);
            TrigHelper.getPointOnCircle(mNumberTextPoint, f1, f2, getCenter());
            mAxesTextPaint.getTextBounds(s, 0, s.length(), mAxesTextBounds);
            mBackgroundBitmapCanvas
                    .drawText(s, mNumberTextPoint.x, mNumberTextPoint.y
                            + (float) (mAxesTextBounds.height() / 2),
                            mAxesTextPaint);
        }

    }

    private void init() {
        mOuterCirclePaint.setStrokeWidth(mOuterCircleStrokeWidth);
        mInnerCirclePaint.setStrokeWidth(mInnerCirclesStrokeWidth);
        mMinuteCirclesPaint.setStrokeWidth(mInnerCirclesStrokeWidth);
        mHourLinePaint.setStrokeWidth(mHourLineStrokeWidth);
        mTargetPaint.setStrokeWidth(mTargetStrokeWidth);
        mAxesTextPaint.setTextSize(mAxesTextSize);
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
            mOuterCirclePaint.setColor(mOuterCircleColorAmbient);
            mInnerCirclePaint.setColor(mInnerCircleColorAmbient);
            mMinuteCirclesPaint.setColor(mMinuteCirclesColorAmbient);
            mHourLinePaint.setColor(mHourLineColorAmbient);
            mTargetPaint.setColor(mTargetColorAmbient);
            mAxesTextPaint.setColor(mAxesTextColorAmbient);
            return;
        } else {
            mFacePaint.setColor(mFaceColorInteractive);
            mOuterCirclePaint.setColor(mOuterCircleColorInteractive);
            mInnerCirclePaint.setColor(mInnerCircleColorInteractive);
            mMinuteCirclesPaint.setColor(mMinuteCirclesColorInteractive);
            mHourLinePaint.setColor(mHourLineColorInteractive);
            mTargetPaint.setColor(mTargetColorInteractive);
            mAxesTextPaint.setColor(mAxesTextColorInteractive);
            return;
        }
    }

    private void updateCrosshairs(WatchCurrentTime watchcurrenttime) {
        float f = (mOuterCircleRadius - mInnerCircleRadius) / 14F;
        float f1 = (float) Math.toRadians(watchcurrenttime
                .getHourDegreesContinuous());
        float f2 = f + mInnerCircleRadius;
        float f3 = mOuterCircleRadius - f;
        float f4 = f2
                + ((watchcurrenttime.getMinute() + watchcurrenttime.getSecond() / 60F) / 60F)
                * (f3 - f2);
        TrigHelper.getPointOnCircle(mTargetCenterPoint, f4, f1, getCenter());
        TrigHelper.getPointOnCircle(mHourLine1StartPoint, mInnerCircleRadius,
                f1, getCenter());
        TrigHelper.getPointOnCircle(mHourLine1EndPoint, f4 - mTargetRadius, f1,
                getCenter());
        TrigHelper.getPointOnCircle(mHourLine2StartPoint, f4 + mTargetRadius,
                f1, getCenter());
        TrigHelper.getPointOnCircle(mHourLine2EndPoint, mOuterCircleRadius
                - mOuterCircleStrokeWidth / 2.0F, f1, getCenter());
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
        canvas.drawCircle(mTargetCenterPoint.x, mTargetCenterPoint.y,
                mTargetRadius, mTargetPaint);
        canvas.drawLine(mHourLine1StartPoint.x, mHourLine1StartPoint.y,
                mHourLine1EndPoint.x, mHourLine1EndPoint.y, mHourLinePaint);
        canvas.drawLine(mHourLine2StartPoint.x, mHourLine2StartPoint.y,
                mHourLine2EndPoint.x, mHourLine2EndPoint.y, mHourLinePaint);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
        generateBackgroundBitmap(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mOuterCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_outer_circle_radius);
        mOuterCircleStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_outer_circle_stroke_width);
        mInnerCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_inner_circle_radius);
        mInnerCirclesStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_inner_circles_stroke_width);
        mHourLineStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_hour_line_stroke_width);
        mTargetStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_target_stroke_width);
        mTargetRadius = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_target_radius);
        mAxesTextSize = DisplayHelper.getPixels(this,
                R.dimen.coordinates_round_axes_text_size);
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
    private WatchCurrentTime mCurrentTime;
    private int mFaceColorAmbient;
    private int mFaceColorInteractive;
    private Paint mFacePaint;
    private PointF mHourLine1EndPoint;
    private PointF mHourLine1StartPoint;
    private PointF mHourLine2EndPoint;
    private PointF mHourLine2StartPoint;
    private int mHourLineColorAmbient;
    private int mHourLineColorInteractive;
    private Paint mHourLinePaint;
    private float mHourLineStrokeWidth;
    private int mInnerCircleColorAmbient;
    private int mInnerCircleColorInteractive;
    private Paint mInnerCirclePaint;
    private float mInnerCircleRadius;
    private float mInnerCirclesStrokeWidth;
    private int mMinuteCirclesColorAmbient;
    private int mMinuteCirclesColorInteractive;
    private Paint mMinuteCirclesPaint;
    private PointF mNumberTextPoint;
    private int mOuterCircleColorAmbient;
    private int mOuterCircleColorInteractive;
    private Paint mOuterCirclePaint;
    private float mOuterCircleRadius;
    private float mOuterCircleStrokeWidth;
    private PointF mTargetCenterPoint;
    private int mTargetColorAmbient;
    private int mTargetColorInteractive;
    private Paint mTargetPaint;
    private float mTargetRadius;
    private float mTargetStrokeWidth;
}
