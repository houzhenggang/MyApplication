package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacepolar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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

public class PolarClockView extends WatchFaceView {

    public PolarClockView(Context context) {
        this(context, null);
    }

    public PolarClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public PolarClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mLastHour = -1;
        mCurrentRingColorCombo = null;
        mFaceColorAmbient = 0xff000000;
        mRingColorAmbient = Color.argb(255, 255, 255, 255);
        mFacePaint = new WatchFaceFillPaint();
        mRingPaint = new WatchFaceStrokePaint();
        mRingPath = new Path();
        mMinuteCircleRect = new RectF();
        mMinuteCircleOuterRect = new RectF();
        mMinuteCircleInnerRect = new RectF();
        mHourCircleRect = new RectF();
        mHourCircleOuterRect = new RectF();
        mHourCircleInnerRect = new RectF();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        init();
    }

    private void init() {
        mMinuteCircleRect
                .set((getFaceRadius() - mMinuteRingRadius) + mRingStrokeWidth
                        / 2.0F, (getFaceRadius() - mMinuteRingRadius)
                        + mRingStrokeWidth / 2.0F, getFaceWidth()
                        - (getFaceRadius() - mMinuteRingRadius)
                        - mRingStrokeWidth / 2.0F, getFaceHeight()
                        - (getFaceRadius() - mMinuteRingRadius)
                        - mRingStrokeWidth / 2.0F);
        mMinuteCircleOuterRect.set(getFaceRadius() - mMinuteRingRadius,
                getFaceRadius() - mMinuteRingRadius, getFaceWidth()
                        - (getFaceRadius() - mMinuteRingRadius),
                getFaceHeight() - (getFaceRadius() - mMinuteRingRadius));
        mMinuteCircleInnerRect.set((getFaceRadius() - mMinuteRingRadius)
                + mRingStrokeWidth, (getFaceRadius() - mMinuteRingRadius)
                + mRingStrokeWidth, getFaceWidth()
                - (getFaceRadius() - mMinuteRingRadius) - mRingStrokeWidth,
                getFaceHeight() - (getFaceRadius() - mMinuteRingRadius)
                        - mRingStrokeWidth);
        mHourCircleRect.set((getFaceRadius() - mHourRingRadius)
                + mRingStrokeWidth / 2.0F, (getFaceRadius() - mHourRingRadius)
                + mRingStrokeWidth / 2.0F,
                getFaceWidth() - (getFaceRadius() - mHourRingRadius)
                        - mRingStrokeWidth / 2.0F, getFaceHeight()
                        - (getFaceRadius() - mHourRingRadius)
                        - mRingStrokeWidth / 2.0F);
        mHourCircleOuterRect.set(getFaceRadius() - mHourRingRadius,
                getFaceRadius() - mHourRingRadius, getFaceWidth()
                        - (getFaceRadius() - mHourRingRadius), getFaceHeight()
                        - (getFaceRadius() - mHourRingRadius));
        mHourCircleInnerRect.set((getFaceRadius() - mHourRingRadius)
                + mRingStrokeWidth, (getFaceRadius() - mHourRingRadius)
                + mRingStrokeWidth, getFaceWidth()
                - (getFaceRadius() - mHourRingRadius) - mRingStrokeWidth,
                getFaceHeight() - (getFaceRadius() - mHourRingRadius)
                        - mRingStrokeWidth);
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
            mRingPaint.setColor(mRingColorAmbient);
            return;
        } else {
            mFacePaint.setColor(mCurrentRingColorCombo.getFaceColor());
            mRingPaint.setColor(mCurrentRingColorCombo.getRingColor());
            return;
        }
    }

    private void updateRings(WatchCurrentTime watchcurrenttime) {
        if (watchcurrenttime.get12Hour() != (float) mLastHour) {
            mCurrentRingColorCombo = PolarColorSelector
                    .getNextColorCombination();
            setColors(watchcurrenttime);
            mLastHour = (int) watchcurrenttime.get12Hour();
        }
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        mRingPaint.setStrokeWidth(mTickStrokeWidth);
        for (int i = 0; i < 12; i++) {
            float f2 = (float) Math.toRadians(TimeHelper.getDegreesFromHour(i));
            TrigHelper.getPointOnCircle(mTickStartPoint, getFaceRadius()
                    - mRingStrokeWidth, f2, getCenter());
            TrigHelper.getPointOnCircle(mTickEndPoint, getFaceRadius(), f2,
                    getCenter());
            mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                    mTickStartPoint.y, mTickEndPoint.x, mTickEndPoint.y,
                    mRingPaint);
        }

        float f = 90F + watchcurrenttime.getMinuteDegreesContinuous();
        float f1;
        if (isAmbient()) {
            mRingPaint.setStrokeWidth(mRingStrokeWidth);
            mRingPaint.setColor(mFaceColorAmbient);
            mRingPath.reset();
            mRingPath.arcTo(mMinuteCircleRect, -90F, f);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
            mRingPath.reset();
            mRingPath.moveTo(getCenterX(), mMinuteCircleInnerRect.top);
            mRingPath.lineTo(getCenterX(), mMinuteCircleOuterRect.top);
            mRingPath.arcTo(mMinuteCircleOuterRect, -90F, f);
            double d3 = Math.toRadians(f - 90F);
            double d4 = (double) (mMinuteCircleInnerRect.width() / 2.0F)
                    * Math.cos(d3) + (double) getCenterX();
            double d5 = (double) (mMinuteCircleInnerRect.height() / 2.0F)
                    * Math.sin(d3) + (double) getCenterY();
            mRingPath.lineTo((float) d4, (float) d5);
            mRingPath.arcTo(mMinuteCircleInnerRect, f - 90F, -f);
            mRingPaint.setStrokeWidth(mTickStrokeWidth);
            mRingPaint.setColor(mRingColorAmbient);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
        } else {
            mRingPaint.setStrokeWidth(mRingStrokeWidth);
            mRingPath.reset();
            mRingPath.arcTo(mMinuteCircleRect, -90F, f);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
        }
        f1 = 90F + watchcurrenttime.getHourDegreesContinuous();
        if (isAmbient()) {
            mRingPaint.setStrokeWidth(mRingStrokeWidth);
            mRingPaint.setColor(mFaceColorAmbient);
            mRingPath.reset();
            mRingPath.arcTo(mHourCircleRect, -90F, f);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
            mRingPath.reset();
            mRingPath.moveTo(getCenterX(), mHourCircleInnerRect.top);
            mRingPath.lineTo(getCenterX(), mHourCircleOuterRect.top);
            mRingPath.arcTo(mHourCircleOuterRect, -90F, f1);
            double d = Math.toRadians(f1 - 90F);
            double d1 = (double) (mHourCircleInnerRect.width() / 2.0F)
                    * Math.cos(d) + (double) getCenterX();
            double d2 = (double) (mHourCircleInnerRect.height() / 2.0F)
                    * Math.sin(d) + (double) getCenterY();
            mRingPath.lineTo((float) d1, (float) d2);
            mRingPath.arcTo(mHourCircleInnerRect, f1 - 90F, -f1);
            mRingPaint.setStrokeWidth(mTickStrokeWidth);
            mRingPaint.setColor(mRingColorAmbient);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
            return;
        } else {
            mRingPaint.setStrokeWidth(mRingStrokeWidth);
            mRingPath.reset();
            mRingPath.arcTo(mHourCircleRect, -90F, f1);
            mBackgroundBitmapCanvas.drawPath(mRingPath, mRingPaint);
            return;
        }
    }

    protected boolean isContinuous() {
        return false;
    }

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        if (watchcurrenttime.get12Hour() != (float) mLastHour) {
            mCurrentRingColorCombo = PolarColorSelector.getNextColorCombination();
        }
        mLastHour = (int) watchcurrenttime.get12Hour();
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mRingStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.polar_ring_stroke_width);
        mMinuteRingRadius = DisplayHelper.getPixels(this,
                R.dimen.polar_ring_minute_radius);
        mHourRingRadius = DisplayHelper.getPixels(this,
                R.dimen.polar_ring_hour_radius);
        mTickStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.polar_tick_stroke_width);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        updateRings(watchcurrenttime);
    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
        updateRings(watchcurrenttime);
    }

    protected void onUpdateMinuteContinuous(WatchCurrentTime watchcurrenttime) {
        updateRings(watchcurrenttime);
    }

    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private PolarColorCombination mCurrentRingColorCombo;
    private int mFaceColorAmbient;
    private Paint mFacePaint;
    private RectF mHourCircleInnerRect;
    private RectF mHourCircleOuterRect;
    private RectF mHourCircleRect;
    private float mHourRingRadius;
    private int mLastHour;
    private RectF mMinuteCircleInnerRect;
    private RectF mMinuteCircleOuterRect;
    private RectF mMinuteCircleRect;
    private float mMinuteRingRadius;
    private int mRingColorAmbient;
    private Paint mRingPaint;
    private Path mRingPath;
    private float mRingStrokeWidth;
    private PointF mTickEndPoint;
    private PointF mTickStartPoint;
    private float mTickStrokeWidth;
}
