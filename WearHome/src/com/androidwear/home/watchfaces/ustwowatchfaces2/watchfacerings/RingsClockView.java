package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacerings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TimeHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.TrigHelper;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceFillPaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceStrokePaint;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint.WatchFaceTextPaint;

public class RingsClockView extends WatchFaceView {

    public RingsClockView(Context context) {
        this(context, null);
    }

    public RingsClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public RingsClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mSecondTickStrokeWidth = 1.0F;
        mFaceColorAmbient = 0xff000000;
        mFaceColorInteractive = -1;
        mFacePieColorAmbient = Color.argb(255, 26, 26, 26);
        mFacePieColorInteractive = Color.argb(255, 248, 248, 248);
        mSecondTickColorAmbient = Color.argb(255, 102, 102, 102);
        mSecondTickColorInteractive = Color.argb(255, 179, 179, 179);
        mCurrentSecondTickColor = Color.argb(255, 0, 151, 94);
        mHourRingColorAmbient = -1;
        mHourRingColorInteractive = Color.argb(255, 0, 151, 94);
        mHourRingArrowColorAmbient = -1;
        mHourRingArrowColorInteractive = Color.argb(255, 0, 151, 94);
        mMinuteRingColorAmbient = Color.argb(255, 179, 179, 179);
        mMinuteRingColorInteractive = Color.argb(255, 102, 102, 102);
        mMinuteRingArrowColorAmbient = Color.argb(255, 179, 179, 179);
        mMinuteRingArrowColorInteractive = Color.argb(255, 102, 102, 102);
        mHourTextColorAmbient = -1;
        mHourTextColorInteractive = Color.argb(255, 0, 151, 94);
        mMinuteTextColorAmbient = Color.argb(255, 179, 179, 179);
        mMinuteTextColorInteractive = Color.argb(255, 102, 102, 102);
        mFacePaint = new WatchFaceFillPaint();
        mFacePiePaint = new WatchFaceFillPaint();
        mCirclePaint = new WatchFaceStrokePaint();
        mSecondTickPaint = new WatchFaceStrokePaint();
        mCurrentSecondTickPaint = new WatchFaceStrokePaint();
        mHourRingPaint = new WatchFaceStrokePaint();
        mHourRingArrowPaint = new WatchFaceFillPaint();
        mMinuteRingPaint = new WatchFaceStrokePaint();
        mMinuteRingArrowPaint = new WatchFaceFillPaint();
        mHourTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.CENTER);
        mMinuteTextPaint = new WatchFaceTextPaint("sans-serif", 0,
                android.graphics.Paint.Align.CENTER);
        mFacePiePath = new Path();
        mHourRingRect = new RectF();
        mMinuteRingRect = new RectF();
        mRingPath = new Path();
        mHourTextBounds = new Rect();
        mColonTextBounds = new Rect();
        mMinuteTextBounds = new Rect();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mHourDotPoint = new PointF();
        mMinuteDotPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mTimeBitmapCanvas = new Canvas();
        mHourRingBitmapCanvas = new Canvas();
        mMinuteRingBitmapCanvas = new Canvas();
        init();
    }

    private void generateBackgroundBitmap(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        WatchFace watchface = (WatchFace) getContext();
        if (!isAmbient()) {
            if (watchface.isRound()) {
                mFacePiePath.reset();
                mFacePiePath.moveTo(getCenterX(), getCenterY());
                mFacePiePath.lineTo(getCenterX(), 0.0F);
                mFacePiePath.arcTo(getFaceRect(), 270F, 90F);
                mFacePiePath.lineTo(getCenterX(), getCenterY());
                mBackgroundBitmapCanvas.drawPath(mFacePiePath, mFacePiePaint);
            } else {
                mFacePiePath.reset();
                mFacePiePath.moveTo(getCenterX(), getCenterY());
                mFacePiePath.lineTo(getCenterX(), 0.0F);
                mFacePiePath.lineTo(getFaceWidth(), 0.0F);
                mFacePiePath.lineTo(getFaceWidth(), getCenterY());
                mFacePiePath.lineTo(getCenterX(), getCenterY());
                mBackgroundBitmapCanvas.drawPath(mFacePiePath, mFacePiePaint);
            }
            if (watchface.isRound()) {
                mFacePiePath.reset();
                mFacePiePath.moveTo(getCenterX(), getCenterY());
                mFacePiePath.lineTo(getCenterX(), getFaceHeight());
                mFacePiePath.arcTo(getFaceRect(), 90F, 90F);
                mFacePiePath.lineTo(getCenterX(), getCenterY());
                mBackgroundBitmapCanvas.drawPath(mFacePiePath, mFacePiePaint);
            } else {
                mFacePiePath.reset();
                mFacePiePath.moveTo(getCenterX(), getCenterY());
                mFacePiePath.lineTo(getCenterX(), getFaceHeight());
                mFacePiePath.lineTo(0.0F, getFaceHeight());
                mFacePiePath.lineTo(0.0F, getCenterY());
                mFacePiePath.lineTo(getCenterX(), getCenterY());
                mBackgroundBitmapCanvas.drawPath(mFacePiePath, mFacePiePaint);
            }

            mCirclePaint.setStrokeWidth(mOuterCircleWidth);
            if (watchface.isRound())
                mBackgroundBitmapCanvas.drawCircle(getCenterX(), getCenterY(),
                        getFaceRadius() - mOuterCircleWidth, mCirclePaint);
            else
                mBackgroundBitmapCanvas.drawRect(getFaceRect(), mCirclePaint);
            mCirclePaint.setStrokeWidth(mInnerCircleWidth);
            mBackgroundBitmapCanvas.drawCircle(getCenterX(), getCenterY(),
                    mInnerCircleRadius, mFacePaint);
            float f;
            for (int i = 0; i < 60; i++) {
                f = (float) Math.toRadians(TimeHelper.getDegreesFromSecond(i));
                TrigHelper.getPointOnCircle(mTickStartPoint,
                        mSecondTickCircleRadius - mSecondTickLength / 2.0F, f,
                        getCenter());
                TrigHelper.getPointOnCircle(mTickEndPoint,
                        mSecondTickCircleRadius + mSecondTickLength / 2.0F, f,
                        getCenter());
                mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                        mTickStartPoint.y, mTickEndPoint.x, mTickEndPoint.y,
                        mSecondTickPaint);
            }
        }

    }

    private void generateTimeBitmap(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mTimeBitmapCanvas);
        String s;
        String s1;
        if (DateFormat.is24HourFormat(getContext()))
            s = TimeHelper
                    .getTwoDigitNumber((int) watchcurrenttime.get24Hour());
        else if (watchcurrenttime.get12Hour() == 0.0F)
            s = TimeHelper.getTwoDigitNumber(12 + (int) watchcurrenttime
                    .get12Hour());
        else
            s = TimeHelper
                    .getTwoDigitNumber((int) watchcurrenttime.get12Hour());
        s1 = TimeHelper.getTwoDigitNumber((int) watchcurrenttime.getMinute());
        mHourTextPaint.getTextBounds(s, 0, s.length(), mHourTextBounds);
        mMinuteTextPaint.getTextBounds(s1, 0, s1.length(), mMinuteTextBounds);
        mTimeBitmapCanvas.drawText(s, getCenterX() - mTimeTextSpacing
                - (float) (mHourTextBounds.width() / 2), getCenterY()
                + (float) (mHourTextBounds.height() / 2), mHourTextPaint);
        mTimeBitmapCanvas.drawText(s1, getCenterX() + mTimeTextSpacing
                + (float) (mMinuteTextBounds.width() / 2), getCenterY()
                + (float) (mMinuteTextBounds.height() / 2), mMinuteTextPaint);
    }

    private void init() {
        mSecondTickPaint.setStrokeWidth(mSecondTickStrokeWidth);
        mCurrentSecondTickPaint.setStrokeWidth(mCurrentSecondTickStrokeWidth);
        mHourRingPaint.setStrokeWidth(mRingWidth);
        mHourRingArrowPaint.setStrokeWidth(mRingWidth);
        mMinuteRingPaint.setStrokeWidth(mRingWidth);
        mMinuteRingArrowPaint.setStrokeWidth(mRingWidth);
        mHourTextPaint.setTextSize(mTimeTextSize);
        mMinuteTextPaint.setTextSize(mTimeTextSize);
        float f = getFaceRadius() - mHourRingRadius;
        mHourRingRect.set(f, f, getFaceWidth() - f, getFaceHeight() - f);
        float f1 = getFaceRadius() - mMinuteRingRadius;
        mMinuteRingRect.set(f1, f1, getFaceWidth() - f1, getFaceHeight() - f1);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
            mTimeBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mTimeBitmapCanvas.setBitmap(mTimeBitmap);
            mHourRingBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mHourRingBitmapCanvas.setBitmap(mHourRingBitmap);
            mMinuteRingBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mMinuteRingBitmapCanvas.setBitmap(mMinuteRingBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        if (isAmbient()) {
            mFacePaint.setColor(mFaceColorAmbient);
            mCirclePaint.setColor(mFaceColorAmbient);
            mFacePiePaint.setColor(mFacePieColorAmbient);
            mSecondTickPaint.setColor(mSecondTickColorAmbient);
            mHourRingPaint.setColor(mHourRingColorAmbient);
            mHourRingArrowPaint.setColor(mHourRingArrowColorAmbient);
            mMinuteRingPaint.setColor(mMinuteRingColorAmbient);
            mMinuteRingArrowPaint.setColor(mMinuteRingArrowColorAmbient);
            mHourTextPaint.setColor(mHourTextColorAmbient);
            mMinuteTextPaint.setColor(mMinuteTextColorAmbient);
        } else {
            mFacePaint.setColor(mFaceColorInteractive);
            mCirclePaint.setColor(mFaceColorInteractive);
            mFacePiePaint.setColor(mFacePieColorInteractive);
            mSecondTickPaint.setColor(mSecondTickColorInteractive);
            mHourRingPaint.setColor(mHourRingColorInteractive);
            mHourRingArrowPaint.setColor(mHourRingArrowColorInteractive);
            mMinuteRingPaint.setColor(mMinuteRingColorInteractive);
            mMinuteRingArrowPaint.setColor(mMinuteRingArrowColorInteractive);
            mHourTextPaint.setColor(mHourTextColorInteractive);
            mMinuteTextPaint.setColor(mMinuteTextColorInteractive);
        }
        mCurrentSecondTickPaint.setColor(mCurrentSecondTickColor);
    }

    private void updateHour(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mHourRingBitmapCanvas);
        float f = watchcurrenttime.getHourDegreesContinuous();
        float f1 = (float) Math.toRadians(f);
        mRingPath.reset();
        mRingPath.arcTo(mHourRingRect, -90F, 90F + f);
        mHourRingBitmapCanvas.drawPath(mRingPath, mHourRingPaint);
        TrigHelper.getPointOnCircle(mHourDotPoint, mHourRingRadius, f1,
                getCenter());
        mHourRingBitmapCanvas.drawCircle(mHourDotPoint.x, mHourDotPoint.y,
                mRingDotRadius, mHourRingArrowPaint);
    }

    private void updateMinute(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mMinuteRingBitmapCanvas);
        float f = watchcurrenttime.getMinuteDegreesContinuous();
        float f1 = (float) Math.toRadians(f);
        mRingPath.reset();
        mRingPath.arcTo(mMinuteRingRect, -90F, 90F + f);
        mMinuteRingBitmapCanvas.drawPath(mRingPath, mMinuteRingPaint);
        TrigHelper.getPointOnCircle(mMinuteDotPoint, mMinuteRingRadius, f1,
                getCenter());
        mMinuteRingBitmapCanvas.drawCircle(mMinuteDotPoint.x,
                mMinuteDotPoint.y, mRingDotRadius, mMinuteRingArrowPaint);
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
        canvas.drawBitmap(mMinuteRingBitmap, 0.0F, 0.0F, null);
        canvas.drawBitmap(mHourRingBitmap, 0.0F, 0.0F, null);
        canvas.drawBitmap(mTimeBitmap, 0.0F, 0.0F, null);
        if (!isAmbient()) {
            float f = (float) Math.toRadians(mCurrentTime.getSecondDegrees());
            TrigHelper.getPointOnCircle(mTickStartPoint,
                    mSecondTickCircleRadius - mCurrentSecondTickLength / 2.0F,
                    f, getCenter());
            TrigHelper.getPointOnCircle(mTickEndPoint, mSecondTickCircleRadius
                    + mCurrentSecondTickLength / 2.0F, f, getCenter());
            canvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                    mTickEndPoint.x, mTickEndPoint.y, mCurrentSecondTickPaint);
        }
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
        generateBackgroundBitmap(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mMinuteRingRadius = DisplayHelper.getPixels(this,
                R.dimen.rings_minute_ring_radius);
        mSecondTickCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.rings_second_tick_circle_radius);
        mHourRingRadius = DisplayHelper.getPixels(this,
                R.dimen.rings_hour_ring_radius);
        mInnerCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.rings_inner_circle_radius);
        mOuterCircleWidth = DisplayHelper.getPixels(this,
                R.dimen.rings_outer_circle_width);
        mInnerCircleWidth = DisplayHelper.getPixels(this,
                R.dimen.rings_inner_circle_width);
        mSecondTickLength = DisplayHelper.getPixels(this,
                R.dimen.rings_second_tick_length);
        mCurrentSecondTickLength = DisplayHelper.getPixels(this,
                R.dimen.rings_current_second_tick_length);
        mCurrentSecondTickStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.rings_current_second_tick_stroke_width);
        mRingWidth = DisplayHelper.getPixels(this, R.dimen.rings_ring_width);
        mRingDotRadius = DisplayHelper.getPixels(this,
                R.dimen.rings_ring_dot_radius);
        mTimeTextSpacing = DisplayHelper.getPixels(this,
                R.dimen.rings_time_text_spacing);
        mTimeTextSize = DisplayHelper.getPixels(this,
                R.dimen.rings_time_text_size);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
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
    private Paint mCirclePaint;
    private Rect mColonTextBounds;
    private int mCurrentSecondTickColor;
    private float mCurrentSecondTickLength;
    private Paint mCurrentSecondTickPaint;
    private float mCurrentSecondTickStrokeWidth;
    private WatchCurrentTime mCurrentTime;
    private int mFaceColorAmbient;
    private int mFaceColorInteractive;
    private Paint mFacePaint;
    private int mFacePieColorAmbient;
    private int mFacePieColorInteractive;
    private Paint mFacePiePaint;
    private Path mFacePiePath;
    private PointF mHourDotPoint;
    private int mHourRingArrowColorAmbient;
    private int mHourRingArrowColorInteractive;
    private Paint mHourRingArrowPaint;
    private Bitmap mHourRingBitmap;
    private Canvas mHourRingBitmapCanvas;
    private int mHourRingColorAmbient;
    private int mHourRingColorInteractive;
    private Paint mHourRingPaint;
    private float mHourRingRadius;
    private RectF mHourRingRect;
    private Rect mHourTextBounds;
    private int mHourTextColorAmbient;
    private int mHourTextColorInteractive;
    private Paint mHourTextPaint;
    private float mInnerCircleRadius;
    private float mInnerCircleWidth;
    private PointF mMinuteDotPoint;
    private int mMinuteRingArrowColorAmbient;
    private int mMinuteRingArrowColorInteractive;
    private Paint mMinuteRingArrowPaint;
    private Bitmap mMinuteRingBitmap;
    private Canvas mMinuteRingBitmapCanvas;
    private int mMinuteRingColorAmbient;
    private int mMinuteRingColorInteractive;
    private Paint mMinuteRingPaint;
    private float mMinuteRingRadius;
    private RectF mMinuteRingRect;
    private Rect mMinuteTextBounds;
    private int mMinuteTextColorAmbient;
    private int mMinuteTextColorInteractive;
    private Paint mMinuteTextPaint;
    private float mOuterCircleWidth;
    private float mRingDotRadius;
    private Path mRingPath;
    private float mRingWidth;
    private float mSecondTickCircleRadius;
    private int mSecondTickColorAmbient;
    private int mSecondTickColorInteractive;
    private float mSecondTickLength;
    private Paint mSecondTickPaint;
    private float mSecondTickStrokeWidth;
    private PointF mTickEndPoint;
    private PointF mTickStartPoint;
    private Bitmap mTimeBitmap;
    private Canvas mTimeBitmapCanvas;
    private float mTimeTextSize;
    private float mTimeTextSpacing;
}
