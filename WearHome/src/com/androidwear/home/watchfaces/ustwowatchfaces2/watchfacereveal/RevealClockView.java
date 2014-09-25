package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacereveal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TimeHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TrigHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceFillPaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceRoundStrokePaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceStrokePaint;

public class RevealClockView extends WatchFaceView {

    public RevealClockView(Context context) {
        this(context, null);
    }

    public RevealClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public RevealClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mMinuteTickStrokeWidth = 1.0F;
        mHourTickStrokeWidth = 4F;
        mFaceColorAmbient = 0xff000000;
        mMinuteTickColor = -1;
        mHourTickColor = -1;
        mHourMinuteHandColor = -1;
        mSecondHandColor = -1;
        mFacePaint = new WatchFaceFillPaint();
        mMinuteTickPaint = new WatchFaceStrokePaint();
        mHourTickPaint = new WatchFaceStrokePaint();
        mHourMinuteHandPaint = new WatchFaceRoundStrokePaint();
        mSecondHandPaint = new WatchFaceRoundStrokePaint();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mHourHandEnd = new PointF();
        mMinuteHandEnd = new PointF();
        mSecondHandStart = new PointF();
        mSecondHandEnd = new PointF();
        init();
    }

    private void init() {
        mMinuteTickPaint.setStrokeWidth(mMinuteTickStrokeWidth);
        mHourTickPaint.setStrokeWidth(mHourTickStrokeWidth);
        mHourMinuteHandPaint.setStrokeWidth(mHourMinuteStrokeWidth);
        mSecondHandPaint.setStrokeWidth(mSecondStrokeWidth);
        int i = Color.argb(255, 105, 118, 170);
        int j = Color.argb(255, 61, 83, 157);
        int k = Color.argb(255, 47, 48, 111);
        mNightGradient = new LinearGradient(0.0F, getFaceHeight(), 0.0F, 0.0F,
                new int[] { i, j, k }, new float[] { 0.06F, 0.46F, 0.93F },
                android.graphics.Shader.TileMode.CLAMP);
        int l = Color.argb(255, 255, 161, 161);
        int i1 = Color.argb(255, 244, 185, 159);
        int j1 = Color.argb(255, 245, 190, 135);
        int k1 = Color.argb(255, 254, 180, 77);
        mMorningGradient = new LinearGradient(0.0F, getFaceHeight(), 0.0F,
                0.0F, new int[] { l, i1, j1, k1 }, new float[] { 0.07F, 0.37F,
                        0.66F, 1.0F }, android.graphics.Shader.TileMode.CLAMP);
        int l1 = Color.argb(255, 139, 235, 166);
        int i2 = Color.argb(255, 148, 219, 205);
        int j2 = Color.argb(255, 113, 194, 231);
        int k2 = Color.argb(255, 12, 158, 224);
        mDayGradient = new LinearGradient(0.0F, getFaceHeight(), 0.0F, 0.0F,
                new int[] { l1, i2, j2, k2 }, new float[] { 0.01F, 0.31F,
                        0.65F, 1.0F }, android.graphics.Shader.TileMode.CLAMP);
        int l2 = Color.argb(255, 229, 132, 127);
        int i3 = Color.argb(255, 195, 101, 132);
        int j3 = Color.argb(255, 155, 60, 134);
        int k3 = Color.argb(255, 87, 54, 108);
        mDuskGradient = new LinearGradient(0.0F, getFaceHeight(), 0.0F, 0.0F,
                new int[] { l2, i3, j3, k3 }, new float[] { 0.01F, 0.37F,
                        0.73F, 0.99F }, android.graphics.Shader.TileMode.CLAMP);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        mMinuteTickPaint.setColor(mMinuteTickColor);
        mHourTickPaint.setColor(mHourTickColor);
        mHourMinuteHandPaint.setColor(mHourMinuteHandColor);
        mSecondHandPaint.setColor(mSecondHandColor);
        if (isAmbient()) {
            mFacePaint.setShader(null);
            mFacePaint.setColor(mFaceColorAmbient);
        } else {
            if (watchcurrenttime.get24Hour() < (float) MORNING_HOUR_START
                    || watchcurrenttime.get24Hour() >= (float) NIGHT_HOUR_START) {
                mFacePaint.setShader(mNightGradient);
                return;
            }
            if (watchcurrenttime.get24Hour() < (float) DAY_HOUR_START) {
                mFacePaint.setShader(mMorningGradient);
                return;
            }
            if (watchcurrenttime.get24Hour() < (float) DUSK_HOUR_START) {
                mFacePaint.setShader(mDayGradient);
                return;
            }
            if (watchcurrenttime.get24Hour() < (float) NIGHT_HOUR_START) {
                mFacePaint.setShader(mDuskGradient);
                return;
            }
        }
    }

    private void updateHour(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getHourDegreesContinuous());
        TrigHelper.getPointOnCircle(mHourHandEnd, mHourHandCircleRadius
                - mHourMinuteStrokeWidth / 2.0F, f, getCenter());
    }

    private void updateMinute(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getMinuteDegreesContinuous());
        TrigHelper.getPointOnCircle(mMinuteHandEnd, mMinuteHandCircleRadius
                - mHourMinuteStrokeWidth / 2.0F, f, getCenter());
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
        double d = mCurrentTime.get12Hour() + mCurrentTime.getMinute() / 60F;
        double d1 = mCurrentTime.getMinute() + mCurrentTime.getSecond() / 60F;
        double d2 = mCurrentTime.getSecond() + mCurrentTime.getMillisecond()
                / 1000F;
        double d3 = 255D / (double) (1 + ADJACENT_TICKS_VISIBLE);
        int i = 0;
        while (i < 60) {
            double d4 = Math.abs(5D * d - (double) i);
            if (d4 > 30D)
                d4 = 60D - d4;
            double d5 = Math.max(255D - d4 * d3, 0.0D);
            double d6 = Math.abs(d1 - (double) i);
            if (d6 > 30D)
                d6 = 60D - d6;
            int j = (int) Math.max(Math.max(255D - d6 * d3, 0.0D), d5);
            if (!isAmbient()) {
                double d7 = Math.abs(d2 - (double) i);
                if (d7 > 30D)
                    d7 = 60D - d7;
                int k = (int) Math.max(255D - d7 * d3, 0.0D);
                j = Math.max(j, k);
            }
            if (j > 0) {
                mMinuteTickPaint.setAlpha(j);
                mHourTickPaint.setAlpha(j);
                float f = (float) Math.toRadians(TimeHelper
                        .getDegreesFromMinute(i));
                if (i % 5 == 0) {
                    TrigHelper.getPointOnCircle(mTickStartPoint,
                            mHourTickCircleRadius, f, getCenter());
                    TrigHelper.getPointOnCircle(mTickEndPoint, mHypotenuse, f,
                            getCenter());
                    canvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                            mTickEndPoint.x, mTickEndPoint.y, mHourTickPaint);
                } else {
                    TrigHelper.getPointOnCircle(mTickStartPoint,
                            mMinuteTickCircleRadius, f, getCenter());
                    TrigHelper.getPointOnCircle(mTickEndPoint, mHypotenuse, f,
                            getCenter());
                    canvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                            mTickEndPoint.x, mTickEndPoint.y, mMinuteTickPaint);
                }
            }
            i++;
        }
        canvas.drawLine(getCenterX(), getCenterY(), mHourHandEnd.x,
                mHourHandEnd.y, mHourMinuteHandPaint);
        canvas.drawLine(getCenterX(), getCenterY(), mMinuteHandEnd.x,
                mMinuteHandEnd.y, mHourMinuteHandPaint);
        if (!isAmbient())
            canvas.drawLine(mSecondHandStart.x, mSecondHandStart.y,
                    mSecondHandEnd.x, mSecondHandEnd.y, mSecondHandPaint);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        if (((WatchFace) getContext()).isRound()) {
            mMinuteTickCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_minute_tick_circle_radius_round);
            mHourTickCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_hour_tick_circle_radius_round);
            mMinuteHandCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_minute_hand_circle_radius_round);
            mHourHandCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_hour_hand_circle_radius_round);
        } else {
            mMinuteTickCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_minute_tick_circle_radius_square);
            mHourTickCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_hour_tick_circle_radius_square);
            mMinuteHandCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_minute_hand_circle_radius_square);
            mHourHandCircleRadius = DisplayHelper.getPixels(this,
                    R.dimen.reveal_hour_hand_circle_radius_square);
        }
        mHourMinuteStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.reveal_hour_minute_stroke_width);
        mSecondStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.reveal_second_stroke_width);
        mHypotenuse = getFaceRadius() * (float) Math.sqrt(2D);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        updateHour(watchcurrenttime);
    }

    protected void onUpdateHourContinuous(WatchCurrentTime watchcurrenttime) {
        updateHour(watchcurrenttime);
    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
        updateMinute(watchcurrenttime);
    }

    protected void onUpdateMinuteContinuous(WatchCurrentTime watchcurrenttime) {
        updateMinute(watchcurrenttime);
    }

    protected void onUpdateSecondContinuous(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getSecondDegreesContinuous());
        TrigHelper.getPointOnCircle(mSecondHandStart, mHourHandCircleRadius
                + mSecondStrokeWidth / 2.0F, f, getCenter());
        TrigHelper.getPointOnCircle(mSecondHandEnd, mMinuteHandCircleRadius
                - mSecondStrokeWidth / 2.0F, f, getCenter());
    }

    private static int ADJACENT_TICKS_VISIBLE = 1;
    private static int DAY_HOUR_START = 11;
    private static int DUSK_HOUR_START = 17;
    private static int MORNING_HOUR_START = 6;
    private static int NIGHT_HOUR_START = 20;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private WatchCurrentTime mCurrentTime;
    private LinearGradient mDayGradient;
    private LinearGradient mDuskGradient;
    private int mFaceColorAmbient;
    private Paint mFacePaint;
    private float mHourHandCircleRadius;
    private PointF mHourHandEnd;
    private int mHourMinuteHandColor;
    private Paint mHourMinuteHandPaint;
    private float mHourMinuteStrokeWidth;
    private float mHourTickCircleRadius;
    private int mHourTickColor;
    private Paint mHourTickPaint;
    private float mHourTickStrokeWidth;
    private float mHypotenuse;
    private float mMinuteHandCircleRadius;
    private PointF mMinuteHandEnd;
    private float mMinuteTickCircleRadius;
    private int mMinuteTickColor;
    private Paint mMinuteTickPaint;
    private float mMinuteTickStrokeWidth;
    private LinearGradient mMorningGradient;
    private LinearGradient mNightGradient;
    private int mSecondHandColor;
    private PointF mSecondHandEnd;
    private Paint mSecondHandPaint;
    private PointF mSecondHandStart;
    private float mSecondStrokeWidth;
    private PointF mTickEndPoint;
    private PointF mTickStartPoint;

}
