package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacedigitalog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
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

public class DigitalogClockView extends WatchFaceView {

    public DigitalogClockView(Context context) {
        this(context, null);
    }

    public DigitalogClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public DigitalogClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mFaceColor = 0xff000000;
        mTickColor = Color.argb(255, 63, 63, 63);
        mHourTextMinuteTickColorAmbient = -1;
        mHourTextMinuteTickColorInteractive = Color.argb(255, 255, 242, 73);
        mSecondTickColor = -1;
        mFacePaint = new WatchFaceFillPaint();
        mBorderPaint = new WatchFaceStrokePaint();
        mTickPaint = new WatchFaceStrokePaint();
        mMinuteTickPaint = new WatchFaceStrokePaint();
        mSecondTickPaint = new WatchFaceStrokePaint();
        mHourTextPaint = new WatchFaceTextPaint("sans-serif-light", 0,
                android.graphics.Paint.Align.CENTER);
        mHourTextBounds = new Rect();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mBackgroundBitmapCanvas = new Canvas();
        mMinuteBitmapCanvas = new Canvas();
        init();
    }

    private void init() {
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mTickPaint.setStrokeWidth(mTickStrokeWidth);
        mSecondTickPaint.setStrokeWidth(mSecondTickStrokeWidth);
        mMinuteTickPaint.setStrokeWidth(mMinuteTickStrokeWidth);
        mHourTextPaint.setTextSize(mHourTextSize);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBitmapCanvas.setBitmap(mBackgroundBitmap);
            mMinuteBitmap = Bitmap.createBitmap((int) getFaceWidth(),
                    (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mMinuteBitmapCanvas.setBitmap(mMinuteBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        if (isAmbient()) {
            mMinuteTickPaint.setColor(mHourTextMinuteTickColorAmbient);
            mHourTextPaint.setColor(mHourTextMinuteTickColorAmbient);
        } else {
            mMinuteTickPaint.setColor(mHourTextMinuteTickColorInteractive);
            mHourTextPaint.setColor(mHourTextMinuteTickColorInteractive);
        }
        mFacePaint.setColor(mFaceColor);
        mTickPaint.setColor(mTickColor);
        mSecondTickPaint.setColor(mSecondTickColor);
    }

    protected boolean isContinuous() {
        return false;
    }

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onDraw(Canvas canvas) {
        WatchCurrentTime.getCurrent(mCurrentTime);
        canvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        if (!isAmbient()) {
            float f = (float) Math.toRadians(mCurrentTime.getSecondDegrees());
            TrigHelper.getPointOnCircle(mTickStartPoint, mTickCircleRadius, f,
                    getCenter());
            TrigHelper.getPointOnCircle(mTickEndPoint, mHypotenuse, f,
                    getCenter());
            canvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                    mTickEndPoint.x, mTickEndPoint.y, mSecondTickPaint);
        }
        canvas.drawBitmap(mMinuteBitmap, 0.0F, 0.0F, null);
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mBorderWidth = DisplayHelper.getPixels(this,
                R.dimen.digitalog_border_width);
        mTickCircleRadius = DisplayHelper.getPixels(this,
                R.dimen.digitalog_tick_circle_radius);
        mTickStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.digitalog_tick_stroke_width);
        mSecondTickStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.digitalog_second_tick_stroke_width);
        mMinuteTickStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.digitalog_minute_tick_stroke_width);
        mHourTextSize = DisplayHelper.getPixels(this,
                R.dimen.digitalog_hour_text_size);
        mHypotenuse = getFaceRadius() * (float) Math.sqrt(2D);
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mBackgroundBitmapCanvas);
        mBackgroundBitmapCanvas.drawRect(getFaceRect(), mFacePaint);
        String s;
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
        mHourTextPaint.getTextBounds(s, 0, s.length(), mHourTextBounds);
        mBackgroundBitmapCanvas.drawText(
                s,
                getCenterX()
                        + DisplayHelper
                                .getNumberTextXOffset((int) watchcurrenttime
                                        .get12Hour()), getCenterY()
                        + (float) (mHourTextBounds.height() / 2),
                mHourTextPaint);
        if (!isAmbient()) {
            for (int i = 0; i < 60; i++) {
                float f = (float) Math
                        .toRadians(TimeHelper.getDegreesFromMinute(i));
                TrigHelper.getPointOnCircle(mTickStartPoint, mTickCircleRadius, f,
                        getCenter());
                TrigHelper.getPointOnCircle(mTickEndPoint, mHypotenuse, f,
                        getCenter());
                mBackgroundBitmapCanvas.drawLine(mTickStartPoint.x,
                        mTickStartPoint.y, mTickEndPoint.x, mTickEndPoint.y,
                        mTickPaint);
            }
        }

    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
        DisplayHelper.clearCanvas(mMinuteBitmapCanvas);
        float f = (float) Math.toRadians(watchcurrenttime.getMinuteDegrees());
        TrigHelper.getPointOnCircle(mTickStartPoint, mTickCircleRadius, f,
                getCenter());
        TrigHelper.getPointOnCircle(mTickEndPoint, mHypotenuse, f, getCenter());
        mMinuteBitmapCanvas.drawLine(mTickStartPoint.x, mTickStartPoint.y,
                mTickEndPoint.x, mTickEndPoint.y, mMinuteTickPaint);
    }

    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundBitmapCanvas;
    private Paint mBorderPaint;
    private float mBorderWidth;
    private WatchCurrentTime mCurrentTime;
    private int mFaceColor;
    private Paint mFacePaint;
    private Rect mHourTextBounds;
    private int mHourTextMinuteTickColorAmbient;
    private int mHourTextMinuteTickColorInteractive;
    private Paint mHourTextPaint;
    private float mHourTextSize;
    private float mHypotenuse;
    private Bitmap mMinuteBitmap;
    private Canvas mMinuteBitmapCanvas;
    private Paint mMinuteTickPaint;
    private float mMinuteTickStrokeWidth;
    private int mSecondTickColor;
    private Paint mSecondTickPaint;
    private float mSecondTickStrokeWidth;
    private float mTickCircleRadius;
    private int mTickColor;
    private PointF mTickEndPoint;
    private Paint mTickPaint;
    private PointF mTickStartPoint;
    private float mTickStrokeWidth;
}
