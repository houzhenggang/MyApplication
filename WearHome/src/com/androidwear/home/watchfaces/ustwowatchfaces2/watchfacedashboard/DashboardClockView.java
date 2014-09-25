package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacedashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.DateFormat;
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

public class DashboardClockView extends WatchFaceView {

    public DashboardClockView(Context context) {
        this(context, null);
    }

    public DashboardClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public DashboardClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mTrailingTicks = 10;
        mFaceColorAmbient = 0xff000000;
        mRingTickColorAmbient = Color.argb(255, 94, 94, 94);
        mRingTickColorInteractive = Color.argb(127, 31, 128, 235);
        mRingSecondTickColorAmbient = -1;
        mRingSecondTickColorInteractive = Color.argb(255, 87, 255, 118);
        mHourDotColorAmbient = Color.argb(255, 94, 94, 94);
        mHourDotColorInteractive = Color.argb(127, 31, 128, 235);
        mHourHandColorAmbient = -1;
        mHourHandColorInteractive = -1;
        mMinuteHandColorAmbient = Color.argb(255, 172, 172, 172);
        mMinuteHandColorInteractive = Color.argb(255, 31, 128, 235);
        mHourMinuteTextCircleColorAmbient = Color.argb(255, 62, 62, 62);
        mHourMinuteTextCircleColorInteractive = Color.argb(51, 31, 128, 235);
        mHourTextColor = -1;
        mMinuteTextColorAmbient = Color.argb(255, 172, 172, 172);
        mMinuteTextColorInteractive = Color.argb(255, 31, 128, 235);
        mFacePaint = new WatchFaceFillPaint();
        mRingTickPaint = new WatchFaceStrokePaint();
        mRingSecondTickPaint = new WatchFaceStrokePaint();
        mHourDotPaint = new WatchFaceFillPaint();
        mHourHandPaint = new WatchFaceFillPaint();
        mMinuteHandPaint = new WatchFaceFillPaint();
        mHourMinuteTextCirclePaint = new WatchFaceStrokePaint();
        mHourTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.LEFT);
        mMinuteTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.LEFT);
        mHourTextBounds = new Rect();
        mMinuteTextBounds = new Rect();
        mHandPathTipArc = new RectF();
        mHourCircleCenterPoint = new PointF();
        mHandTipTopPoint = new PointF();
        mHandTipBottomPoint = new PointF();
        mHandBaseTopPoint = new PointF();
        mHandBaseBottomPoint = new PointF();
        mMinuteCircleCenterPoint = new PointF();
        mDotCenterPoint = new PointF();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mTimeBitmapCanvas = new Canvas();
        mHourHandPath = new Path();
        mMinuteHandPath = new Path();
        init();
    }

    private void generateTimeBitmap(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mTimeBitmapCanvas);
        String s;
        String s1;
        if (DateFormat.is24HourFormat(getContext())) {
            s = TimeHelper
                    .getTwoDigitNumber((int) watchcurrenttime.get24Hour());
        } else {
            if (watchcurrenttime.get12Hour() == 0.0F) {
                s = Integer.toString(12 + (int) watchcurrenttime.get12Hour());
            } else {
                s = Integer.toString((int) watchcurrenttime.get12Hour());
            }
        }
        mHourCircleCenterPoint.set(getCenterX(), getCenterY()
                - mHourMinuteCircleMargin / 2.0F - mHourMinuteCircleRadius);
        if (!isAmbient()) {
            mTimeBitmapCanvas.drawCircle(mHourCircleCenterPoint.x,
                    mHourCircleCenterPoint.y, mHourMinuteCircleRadius,
                    mHourMinuteTextCirclePaint);
        }
        mHourTextPaint.getTextBounds(s, 0, s.length(), mHourTextBounds);
        mTimeBitmapCanvas.drawText(s, mHourCircleCenterPoint.x
                - (float) (mHourTextBounds.width() / 2) - 2.0F,
                mHourCircleCenterPoint.y
                        + (float) (mHourTextBounds.height() / 2),
                mHourTextPaint);
        s1 = TimeHelper.getTwoDigitNumber((int) watchcurrenttime.getMinute());
        mMinuteCircleCenterPoint.set(getCenterX(), getCenterY()
                + mHourMinuteCircleMargin / 2.0F + mHourMinuteCircleRadius);
        if (!isAmbient()) {
            mTimeBitmapCanvas.drawCircle(mMinuteCircleCenterPoint.x,
                    mMinuteCircleCenterPoint.y, mHourMinuteCircleRadius,
                    mHourMinuteTextCirclePaint);
        }
        mMinuteTextPaint.getTextBounds(s1, 0, s1.length(), mMinuteTextBounds);
        mTimeBitmapCanvas.drawText(
                s1,
                mMinuteCircleCenterPoint.x
                        - (float) (mMinuteTextBounds.width() / 2) - 2.0F,
                mMinuteCircleCenterPoint.y
                        + (float) (mMinuteTextBounds.height() / 2),
                mMinuteTextPaint);
    }

    private void init() {
        mHourMinuteTextCirclePaint.setStrokeWidth(mHourMinuteCircleStrokeWidth);
        mHourTextPaint.setTextSize(mHourMinuteTextSize);
        mMinuteTextPaint.setTextSize(mHourMinuteTextSize);
        mTrailingColors = new int[mTrailingTicks];
        mTrailingColors[0] = Color.argb(255, 86, 255, 118);
        mTrailingColors[1] = Color.argb(255, 80, 239, 123);
        mTrailingColors[2] = Color.argb(255, 74, 221, 129);
        mTrailingColors[3] = Color.argb(255, 66, 204, 134);
        mTrailingColors[4] = Color.argb(255, 61, 188, 139);
        mTrailingColors[5] = Color.argb(255, 55, 170, 143);
        mTrailingColors[6] = Color.argb(255, 48, 153, 146);
        mTrailingColors[7] = Color.argb(255, 43, 136, 150);
        mTrailingColors[8] = Color.argb(255, 39, 118, 156);
        mTrailingColors[9] = Color.argb(255, 35, 101, 160);
        int i = Color.argb(255, 21, 37, 96);
        int j = Color.argb(255, 4, 19, 53);
        mFaceGradient = new LinearGradient(0.0F, 0.0F, 0.0F, getFaceHeight(),
                i, j, android.graphics.Shader.TileMode.CLAMP);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
            mTimeBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mTimeBitmapCanvas.setBitmap(mTimeBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        if (isAmbient()) {
            mFacePaint.setColor(mFaceColorAmbient);
            mFacePaint.setShader(null);
            mRingTickPaint.setColor(mRingTickColorAmbient);
            mRingSecondTickPaint.setColor(mRingSecondTickColorAmbient);
            mHourDotPaint.setColor(mHourDotColorAmbient);
            mHourHandPaint.setColor(mHourHandColorAmbient);
            mMinuteHandPaint.setColor(mMinuteHandColorAmbient);
            mHourMinuteTextCirclePaint
                    .setColor(mHourMinuteTextCircleColorAmbient);
            mMinuteTextPaint.setColor(mMinuteTextColorAmbient);
        } else {
            mFacePaint.setShader(mFaceGradient);
            mRingTickPaint.setColor(mRingTickColorInteractive);
            mRingSecondTickPaint.setColor(mRingSecondTickColorInteractive);
            mHourDotPaint.setColor(mHourDotColorInteractive);
            mHourHandPaint.setColor(mHourHandColorInteractive);
            mMinuteHandPaint.setColor(mMinuteHandColorInteractive);
            mHourMinuteTextCirclePaint
                    .setColor(mHourMinuteTextCircleColorInteractive);
            mMinuteTextPaint.setColor(mMinuteTextColorInteractive);
        }
        mHourTextPaint.setColor(mHourTextColor);
    }

    private void updateHour(WatchCurrentTime watchcurrenttime) {
        float f = mHypotenuse - mHourDotCircleRadius;
        float f1 = mHourHandBaseWidth / 2.0F - mHourHandTipWidth / 2.0F;
        float f2 = (float) Math.hypot(f1, f);
        float f3 = (float) Math.atan(f1 / f);
        mHandTipTopPoint.set(getCenterX() + mHourDotCircleRadius, getCenterY()
                - mHourHandTipWidth / 2.0F);
        mHandTipBottomPoint.set(mHandTipTopPoint.x, mHandTipTopPoint.y
                + mHourHandTipWidth);
        TrigHelper.getPointOnCircle(mHandBaseTopPoint, f2, -f3,
                mHandTipTopPoint);
        mHandBaseBottomPoint.set(mHandBaseTopPoint.x, mHandBaseTopPoint.y
                + mHourHandBaseWidth);
        mHourHandPath.reset();
        mHourHandPath.moveTo(mHandTipTopPoint.x, mHandTipTopPoint.y);
        mHourHandPath.lineTo(mHandBaseTopPoint.x, mHandBaseTopPoint.y);
        mHourHandPath.lineTo(mHandBaseBottomPoint.x, mHandBaseBottomPoint.y);
        mHourHandPath.lineTo(mHandTipBottomPoint.x, mHandTipBottomPoint.y);
        float f4 = (mHandTipBottomPoint.y - mHandTipTopPoint.y) / 2.0F;
        mHandPathTipArc.set(mHandTipTopPoint.x - f4, mHandTipTopPoint.y, f4
                + mHandTipTopPoint.x, mHandTipBottomPoint.y);
        mHourHandPath.arcTo(mHandPathTipArc, 90F, 180F);
    }

    private void updateMinute(WatchCurrentTime watchcurrenttime) {
        float f = mHypotenuse - mHourDotCircleRadius;
        float f1 = mMinuteHandBaseWidth / 2.0F - mMinuteHandTipWidth / 2.0F;
        float f2 = (float) Math.hypot(f1, f);
        float f3 = (float) Math.atan(f1 / f);
        mHandTipTopPoint.set(getCenterX() + mHourDotCircleRadius, getCenterY()
                - mMinuteHandTipWidth / 2.0F);
        mHandTipBottomPoint.set(mHandTipTopPoint.x, mHandTipTopPoint.y
                + mMinuteHandTipWidth);
        TrigHelper.getPointOnCircle(mHandBaseTopPoint, f2, -f3,
                mHandTipTopPoint);
        mHandBaseBottomPoint.set(mHandBaseTopPoint.x, mHandBaseTopPoint.y
                + mMinuteHandBaseWidth);
        mMinuteHandPath.reset();
        mMinuteHandPath.moveTo(mHandTipTopPoint.x, mHandTipTopPoint.y);
        mMinuteHandPath.lineTo(mHandBaseTopPoint.x, mHandBaseTopPoint.y);
        mMinuteHandPath.lineTo(mHandBaseBottomPoint.x, mHandBaseBottomPoint.y);
        mMinuteHandPath.lineTo(mHandTipBottomPoint.x, mHandTipBottomPoint.y);
        float f4 = (mHandTipBottomPoint.y - mHandTipTopPoint.y) / 2.0F;
        mHandPathTipArc.set(mHandTipTopPoint.x - f4, mHandTipTopPoint.y, f4
                + mHandTipTopPoint.x, mHandTipBottomPoint.y);
        mMinuteHandPath.arcTo(mHandPathTipArc, 90F, 180F);
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
            int i = (int) (((double) (1000F * mCurrentTime.getSecond() + mCurrentTime
                    .getMillisecond()) / 60000D) * (double) 180F);
            int j = 0;
            while ((float) j < 180F) {
                int k = i - j;
                boolean flag;
                if (i >= j && k < mTrailingTicks) {
                    mRingSecondTickPaint.setColor(mTrailingColors[k]);
                    flag = true;
                } else {
                    flag = false;
                    if (j > i) {
                        int l = (int) ((float) k - 180F + (float) mTrailingTicks);
                        flag = false;
                        if (l < 0) {
                            int i1 = (int) (180F + (float) k);
                            if (i1 < mTrailingTicks) {
                                mRingSecondTickPaint
                                        .setColor(mTrailingColors[i1]);
                                flag = true;
                            }
                        }
                    }
                }
                if (flag) {
                    double d = (float) j * (360F / 180F) - 90F;
                    if (d % 90D != 0.0D) {
                        float f = (float) Math.toRadians(d);
                        TrigHelper.getPointOnCircle(mTickStartPoint,
                                mRingTickCircleRadius, f, getCenter());
                        TrigHelper.getPointOnCircle(mTickEndPoint,
                                mRingTickCircleRadius - mRingTickLength, f,
                                getCenter());
                        canvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                                mTickEndPoint.x, mTickEndPoint.y,
                                mRingSecondTickPaint);
                    }
                }
                j++;
            }
        }
        canvas.drawBitmap(mTimeBitmap, 0.0F, 0.0F, null);
        canvas.rotate(mCurrentTime.getHourDegreesContinuous(), getCenterX(),
                getCenterY());
        canvas.drawPath(mHourHandPath, mHourHandPaint);
        canvas.save();
        canvas.restore();
        canvas.rotate(mCurrentTime.getMinuteDegreesContinuous(), getCenterX(),
                getCenterY());
        canvas.drawPath(mMinuteHandPath, mMinuteHandPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mRingTickCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.dashboard_ring_tick_circle_radius);
        mRingTickLength = DisplayHelper.getPixels(this,
                R.dimen.dashboard_ring_tick_length);
        mHourDotCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_dot_circle_radius);
        mHourDotRadius = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_dot_radius);
        mHourHandBaseWidth = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_hand_base_width);
        mHourHandTipWidth = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_hand_tip_width);
        mMinuteHandBaseWidth = DisplayHelper.getPixels(this,
                R.dimen.dashboard_minute_hand_base_width);
        mMinuteHandTipWidth = DisplayHelper.getPixels(this,
                R.dimen.dashboard_minute_hand_tip_width);
        mHourMinuteCircleMargin = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_minute_circle_margin);
        mHourMinuteCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_minute_circle_radius);
        mHourMinuteCircleStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_minute_circle_stroke_width);
        mHourMinuteTextSize = DisplayHelper.getPixels(this,
                R.dimen.dashboard_hour_minute_text_size);
        mHypotenuse = getFaceRadius() * (float) Math.sqrt(2D);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        int i = 0;
        while ((float) i < 180F) {
            double d = (float) i * (360F / 180F) - 90F;
            float f1 = (float) Math.toRadians(d);
            TrigHelper.getPointOnCircle(mTickStartPoint, mRingTickCircleRadius,
                    f1, getCenter());
            TrigHelper.getPointOnCircle(mTickEndPoint, mRingTickCircleRadius
                    - mRingTickLength, f1, getCenter());
            if (!isAmbient()) {
                if (d % 90D == 0.0D) {
                    mRingSecondTickPaint.setColor(mTrailingColors[0]);
                    mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                            mTickStartPoint.y, mTickEndPoint.x,
                            mTickEndPoint.y, mRingSecondTickPaint);
                } else {
                    mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                            mTickStartPoint.y, mTickEndPoint.x,
                            mTickEndPoint.y, mRingTickPaint);
                }
            } else {
                if (d % 90D == 0.0D) {
                    mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                            mTickStartPoint.y, mTickEndPoint.x,
                            mTickEndPoint.y, mRingSecondTickPaint);
                }
            }
            i++;
        }
        if (!isAmbient()) {
            for (int j = 0; j < 12; j++) {
                float f = (float) Math.toRadians(TimeHelper
                        .getDegreesFromHour(j));
                TrigHelper.getPointOnCircle(mDotCenterPoint,
                        mHourDotCircleRadius, f, getCenter());
                mBackgroundBitmapCanvas.drawCircle(mDotCenterPoint.x,
                        mDotCenterPoint.y, mHourDotRadius, mHourDotPaint);
            }
        }

        updateHour(watchcurrenttime);
        generateTimeBitmap(watchcurrenttime);
    }

    protected void onUpdateHourContinuous(WatchCurrentTime watchcurrenttime) {
        updateHour(watchcurrenttime);
    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
        updateMinute(watchcurrenttime);
        generateTimeBitmap(watchcurrenttime);
    }

    protected void onUpdateMinuteContinuous(WatchCurrentTime watchcurrenttime) {
        updateMinute(watchcurrenttime);
    }

    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private WatchCurrentTime mCurrentTime;
    private PointF mDotCenterPoint;
    private int mFaceColorAmbient;
    private LinearGradient mFaceGradient;
    private Paint mFacePaint;
    private PointF mHandBaseBottomPoint;
    private PointF mHandBaseTopPoint;
    private RectF mHandPathTipArc;
    private PointF mHandTipBottomPoint;
    private PointF mHandTipTopPoint;
    private PointF mHourCircleCenterPoint;
    private float mHourDotCircleRadius;
    private int mHourDotColorAmbient;
    private int mHourDotColorInteractive;
    private Paint mHourDotPaint;
    private float mHourDotRadius;
    private float mHourHandBaseWidth;
    private int mHourHandColorAmbient;
    private int mHourHandColorInteractive;
    private Paint mHourHandPaint;
    private Path mHourHandPath;
    private float mHourHandTipWidth;
    private float mHourMinuteCircleMargin;
    private float mHourMinuteCircleRadius;
    private float mHourMinuteCircleStrokeWidth;
    private int mHourMinuteTextCircleColorAmbient;
    private int mHourMinuteTextCircleColorInteractive;
    private Paint mHourMinuteTextCirclePaint;
    private float mHourMinuteTextSize;
    private Rect mHourTextBounds;
    private int mHourTextColor;
    private Paint mHourTextPaint;
    private float mHypotenuse;
    private PointF mMinuteCircleCenterPoint;
    private float mMinuteHandBaseWidth;
    private int mMinuteHandColorAmbient;
    private int mMinuteHandColorInteractive;
    private Paint mMinuteHandPaint;
    private Path mMinuteHandPath;
    private float mMinuteHandTipWidth;
    private Rect mMinuteTextBounds;
    private int mMinuteTextColorAmbient;
    private int mMinuteTextColorInteractive;
    private Paint mMinuteTextPaint;
    private int mRingSecondTickColorAmbient;
    private int mRingSecondTickColorInteractive;
    private Paint mRingSecondTickPaint;
    private float mRingTickCircleRadius;
    private int mRingTickColorAmbient;
    private int mRingTickColorInteractive;
    private float mRingTickLength;
    private Paint mRingTickPaint;
    private PointF mTickEndPoint;
    private PointF mTickStartPoint;
    private Bitmap mTimeBitmap;
    private Canvas mTimeBitmapCanvas;
    private int mTrailingColors[];
    private int mTrailingTicks;
}
