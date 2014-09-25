package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacemoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
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

public class MoonClockView extends WatchFaceView {

    public MoonClockView(Context context) {
        this(context, null);
    }

    public MoonClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public MoonClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mCurrentTime = WatchCurrentTime.getCurrent();
        mHourTickStrokeWidth = 1.0F;
        mAmbientCircleStrokeWidth = 1.0F;
        mFaceColorAmbient = 0xff000000;
        mRingBorderColorInteractive = Color.argb(255, 249, 249, 249);
        mHourTickColorAmbient = -1;
        mHourTickColorInteractive = Color.argb(127, 255, 255, 255);
        mSecondHandColor = Color.argb(255, 239, 118, 0);
        mOuterShadowColor = Color.argb(255, 244, 244, 244);
        mShadowColor = Color.argb(255, 153, 153, 153);
        mAmbientCircleColor = -1;
        mFacePaint = new WatchFaceFillPaint();
        mRingBorderPaint = new WatchFaceFillPaint();
        mRingPaint = new WatchFaceFillPaint();
        mHourTickPaint = new WatchFaceStrokePaint();
        mHourMoonPaint = new WatchFaceFillPaint();
        mMinuteMoonPaint = new WatchFaceFillPaint();
        mSecondHandPaint = new WatchFaceStrokePaint();
        mOuterShadowPaint = new WatchFaceFillPaint();
        mAmbientCirclePaint = new WatchFaceStrokePaint();
        mSquareFaceInsetRect = new RectF();
        mOuterArcPath = new Path();
        mTickStartPoint = new PointF();
        mTickEndPoint = new PointF();
        mCenterMinuteMoonPoint = new PointF();
        mCenterHourMoonPoint = new PointF();
        mBackgroundBelowSecondHandBitmapCanvas = new Canvas();
        mBackgroundAboveSecondHandBitmapCanvas = new Canvas();
        mCenterHourMoon = new PointF();
        mCenterMinuteMoon = new PointF();
        mSecondHandStart = new PointF();
        mSecondHandEnd = new PointF();
        init();
    }

    private void init() {
        mHourTickPaint.setStrokeWidth(mHourTickStrokeWidth);
        mSecondHandPaint.setStrokeWidth(mSecondHandStrokeWidth);
        mAmbientCirclePaint.setStrokeWidth(mAmbientCircleStrokeWidth);
        int i = Color.argb(255, 244, 244, 244);
        int j = Color.argb(255, 217, 217, 217);
        mFaceGradient = new LinearGradient(0.0F, 0.0F, 0.0F, getFaceHeight(),
                i, j, android.graphics.Shader.TileMode.CLAMP);
        mRingBorderPaint.setColor(mRingBorderColorInteractive);
        int k = Color.argb(255, 202, 202, 202);
        int l = Color.argb(255, 180, 180, 180);
        mRingGradient = new LinearGradient(0.0F, 0.0F, 0.0F, getFaceHeight(),
                k, l, android.graphics.Shader.TileMode.CLAMP);
        mHourTickPaint.setColor(mHourTickColorInteractive);
        mAmbientCirclePaint.setColor(mAmbientCircleColor);
        if (getFaceWidth() > 0.0F) {
            mBackgroundBelowSecondHandBitmap = Bitmap.createBitmap(
                    (int) getFaceWidth(), (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundBelowSecondHandBitmapCanvas
                    .setBitmap(mBackgroundBelowSecondHandBitmap);
            mBackgroundAboveSecondHandBitmap = Bitmap.createBitmap(
                    (int) getFaceWidth(), (int) getFaceHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundAboveSecondHandBitmapCanvas
                    .setBitmap(mBackgroundAboveSecondHandBitmap);
        }
    }

    private void setColors(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getHourDegreesContinuous());
        TrigHelper
                .getPointOnCircle(mCenterHourMoonPoint, mRingCircleInnerRadius
                        - 1.0F - mHourMoonRadius, f, getCenter());
        float f1 = (float) Math.toRadians(watchcurrenttime
                .getMinuteDegreesContinuous());
        TrigHelper.getPointOnCircle(mCenterMinuteMoonPoint, mHourMoonRadius
                - 3F - mMinuteMoonRadius, f1, getCenter());
        if (isAmbient()) {
            mFacePaint.setColor(mFaceColorAmbient);
            mFacePaint.setShader(null);
            mRingPaint.setShader(null);
            mHourTickPaint.setColor(mHourTickColorAmbient);
            mHourMoonPaint.setShader(null);
            mMinuteMoonPaint.setShader(null);
        } else {
            mFacePaint.setShader(mFaceGradient);
            mRingBorderPaint.setColor(mRingBorderColorInteractive);
            mRingPaint.setShader(mRingGradient);
            mHourTickPaint.setColor(mHourTickColorInteractive);
            int i = Color.argb(255, 255, 174, 40);
            int j = Color.argb(255, 234, 145, 0);
            mHourMoonGradient = new LinearGradient(0.0F, mCenterHourMoonPoint.y
                    - mHourMoonRadius / 2.0F, 0.0F, mCenterHourMoonPoint.y
                    + mHourMoonRadius / 2.0F, i, j,
                    android.graphics.Shader.TileMode.CLAMP);
            mHourMoonPaint.setShader(mHourMoonGradient);
            int k = Color.argb(255, 255, 255, 255);
            int l = Color.argb(255, 254, 254, 254);
            mMinuteMoonGradient = new LinearGradient(0.0F,
                    mCenterMinuteMoonPoint.y - mMinuteMoonRadius / 2.0F, 0.0F,
                    mCenterMinuteMoonPoint.y + mMinuteMoonRadius / 2.0F, k, l,
                    android.graphics.Shader.TileMode.CLAMP);
            mMinuteMoonPaint.setShader(mMinuteMoonGradient);
        }
        mSecondHandPaint.setColor(mSecondHandColor);
        mOuterShadowPaint.setColor(mOuterShadowColor);
        mOuterShadowPaint.setShadowLayer(4F, 0.0F, 0.0F, mShadowColor);
    }

    private void updateHour(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getHourDegreesContinuous());
        TrigHelper
                .getPointOnCircle(mCenterHourMoonPoint, mRingCircleInnerRadius
                        - 1.0F - mHourMoonRadius, f, getCenter());
        mCenterHourMoon = mCenterHourMoonPoint;
    }

    private void updateMinute(WatchCurrentTime watchcurrenttime) {
        float f = (float) Math.toRadians(watchcurrenttime
                .getMinuteDegreesContinuous());
        TrigHelper.getPointOnCircle(mCenterMinuteMoonPoint, mHourMoonRadius
                - 3F - mMinuteMoonRadius, f, mCenterHourMoon);
        mCenterMinuteMoon = mCenterMinuteMoonPoint;
    }

    protected boolean isContinuous() {
        return true;
    }

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundBelowSecondHandBitmap, 0.0F, 0.0F, null);
        if (!isAmbient())
            canvas.drawLine(mSecondHandStart.x, mSecondHandStart.y,
                    mSecondHandEnd.x, mSecondHandEnd.y, mSecondHandPaint);
        canvas.drawBitmap(mBackgroundAboveSecondHandBitmap, 0.0F, 0.0F, null);
        if (isAmbient()) {
            canvas.drawCircle(mCenterHourMoon.x, mCenterHourMoon.y,
                    mHourMoonRadius, mAmbientCirclePaint);
            canvas.drawCircle(mCenterHourMoon.x, mCenterHourMoon.y,
                    mHourMoonRadius, mAmbientCirclePaint);
            canvas.drawCircle(mCenterMinuteMoon.x, mCenterMinuteMoon.y,
                    mMinuteMoonRadius, mAmbientCirclePaint);
        } else {
            canvas.drawCircle(mCenterHourMoon.x, mCenterHourMoon.y,
                    mHourMoonRadius, mOuterShadowPaint);
            canvas.drawCircle(mCenterHourMoon.x, mCenterHourMoon.y,
                    mHourMoonRadius, mHourMoonPaint);
            canvas.drawCircle(mCenterMinuteMoon.x, mCenterMinuteMoon.y,
                    mMinuteMoonRadius, mMinuteMoonPaint);
        }
        super.onDraw(canvas);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
        setColors(watchcurrenttime);
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mSquareInsetMargin = DisplayHelper.getPixels(this,
                R.dimen.moon_square_inset_margin);
        mRingCircleInnerRadius = DisplayHelper.getPixels(this,
                R.dimen.moon_ring_circle_inner_radius);
        mHourMoonRadius = DisplayHelper.getPixels(this,
                R.dimen.moon_hour_moon_radius);
        mMinuteMoonRadius = DisplayHelper.getPixels(this,
                R.dimen.moon_minute_moon_radius);
        mSecondHandStrokeWidth = DisplayHelper.getPixels(this,
                R.dimen.moon_second_hand_stroke_width);
        mSquareFaceInsetRect.set(mSquareInsetMargin, mSquareInsetMargin,
                getFaceWidth() - mSquareInsetMargin, getFaceHeight()
                        - mSquareInsetMargin);
        if (((WatchFace) getContext()).isRound())
            mRingRadius = getFaceRadius();
        else
            mRingRadius = getFaceRadius() - mSquareInsetMargin;
        init();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
        WatchFace watchface = (WatchFace) getContext();
        DisplayHelper.clearCanvas(mBackgroundBelowSecondHandBitmapCanvas);
        DisplayHelper.clearCanvas(mBackgroundAboveSecondHandBitmapCanvas);
        mBackgroundBelowSecondHandBitmapCanvas.drawRect(getFaceRect(),
                mFacePaint);
        if (isAmbient()) {
            mBackgroundBelowSecondHandBitmapCanvas.drawCircle(getCenterX(),
                    getCenterY(), mRingRadius, mAmbientCirclePaint);
            mBackgroundBelowSecondHandBitmapCanvas.drawCircle(getCenterX(),
                    getCenterY(), mRingCircleInnerRadius, mAmbientCirclePaint);
            for (int j = 0; j < 12; j++) {
                float f1 = (float) Math.toRadians(TimeHelper
                        .getDegreesFromHour(j));
                TrigHelper.getPointOnCircle(mTickStartPoint,
                        mRingCircleInnerRadius, f1, getCenter());
                TrigHelper.getPointOnCircle(mTickEndPoint, mRingRadius, f1,
                        getCenter());
                mBackgroundBelowSecondHandBitmapCanvas.drawLine(
                        mTickStartPoint.x, mTickStartPoint.y, mTickEndPoint.x,
                        mTickEndPoint.y, mHourTickPaint);
            }

        } else {
            mBackgroundBelowSecondHandBitmapCanvas.drawCircle(getCenterX(),
                    getCenterY(), mRingRadius, mRingPaint);
            for (int i = 0; i < 12; i++) {
                float f = (float) Math.toRadians(TimeHelper
                        .getDegreesFromHour(i));
                TrigHelper.getPointOnCircle(mTickStartPoint,
                        mRingCircleInnerRadius, f, getCenter());
                TrigHelper.getPointOnCircle(mTickEndPoint, mRingRadius, f,
                        getCenter());
                mBackgroundBelowSecondHandBitmapCanvas.drawLine(
                        mTickStartPoint.x, mTickStartPoint.y, mTickEndPoint.x,
                        mTickEndPoint.y, mHourTickPaint);
            }

            if (!watchface.isRound()) {
                mOuterArcPath.reset();
                mOuterArcPath.moveTo(getCenterX(), 0.0F);
                mOuterArcPath.lineTo(0.0F, 0.0F);
                mOuterArcPath.lineTo(0.0F, getCenterY());
                mOuterArcPath.lineTo(mSquareFaceInsetRect.left, getCenterY());
                mOuterArcPath.arcTo(mSquareFaceInsetRect, 180F, 90F);
                mOuterArcPath.lineTo(getCenterX(), 0.0F);
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mOuterShadowPaint);
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mFacePaint);
                mOuterArcPath.reset();
                mOuterArcPath.moveTo(getFaceWidth(), getCenterY());
                mOuterArcPath.lineTo(getFaceWidth(), 0.0F);
                mOuterArcPath.lineTo(getCenterX(), 0.0F);
                mOuterArcPath.lineTo(getCenterX(), mSquareFaceInsetRect.top);
                mOuterArcPath.arcTo(mSquareFaceInsetRect, 270F, 90F);
                mOuterArcPath.lineTo(getFaceWidth(), getCenterY());
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mOuterShadowPaint);
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mFacePaint);
                mOuterArcPath.reset();
                mOuterArcPath.moveTo(getCenterX(), getFaceHeight());
                mOuterArcPath.lineTo(getFaceWidth(), getFaceHeight());
                mOuterArcPath.lineTo(getFaceWidth(), getCenterY());
                mOuterArcPath.lineTo(mSquareFaceInsetRect.right, getCenterY());
                mOuterArcPath.arcTo(mSquareFaceInsetRect, 0.0F, 90F);
                mOuterArcPath.lineTo(getCenterX(), getFaceHeight());
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mOuterShadowPaint);
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mFacePaint);
                mOuterArcPath.reset();
                mOuterArcPath.moveTo(0.0F, getCenterY());
                mOuterArcPath.lineTo(0.0F, getFaceHeight());
                mOuterArcPath.lineTo(getCenterX(), getFaceHeight());
                mOuterArcPath.lineTo(getCenterX(), mSquareFaceInsetRect.bottom);
                mOuterArcPath.arcTo(mSquareFaceInsetRect, 90F, 90F);
                mOuterArcPath.lineTo(0.0F, getCenterY());
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mOuterShadowPaint);
                mBackgroundAboveSecondHandBitmapCanvas.drawPath(mOuterArcPath,
                        mFacePaint);
            }
            mBackgroundAboveSecondHandBitmapCanvas.drawCircle(getCenterX(),
                    getCenterY(), mRingCircleInnerRadius, mOuterShadowPaint);
            mBackgroundAboveSecondHandBitmapCanvas.drawCircle(getCenterX(),
                    getCenterY(), mRingCircleInnerRadius, mFacePaint);
        }
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
        TrigHelper.getPointOnCircle(mSecondHandStart, mRingCircleInnerRadius,
                f, getCenter());
        TrigHelper
                .getPointOnCircle(mSecondHandEnd, mRingRadius, f, getCenter());
    }

    private int mAmbientCircleColor;
    private Paint mAmbientCirclePaint;
    private float mAmbientCircleStrokeWidth;
    private Bitmap mBackgroundAboveSecondHandBitmap;
    private Canvas mBackgroundAboveSecondHandBitmapCanvas;
    private Bitmap mBackgroundBelowSecondHandBitmap;
    private Canvas mBackgroundBelowSecondHandBitmapCanvas;
    private PointF mCenterHourMoon;
    private PointF mCenterHourMoonPoint;
    private PointF mCenterMinuteMoon;
    private PointF mCenterMinuteMoonPoint;
    private WatchCurrentTime mCurrentTime;
    private int mFaceColorAmbient;
    private LinearGradient mFaceGradient;
    private Paint mFacePaint;
    private LinearGradient mHourMoonGradient;
    private Paint mHourMoonPaint;
    private float mHourMoonRadius;
    private int mHourTickColorAmbient;
    private int mHourTickColorInteractive;
    private Paint mHourTickPaint;
    private float mHourTickStrokeWidth;
    private LinearGradient mMinuteMoonGradient;
    private Paint mMinuteMoonPaint;
    private float mMinuteMoonRadius;
    private Path mOuterArcPath;
    private int mOuterShadowColor;
    private Paint mOuterShadowPaint;
    private int mRingBorderColorInteractive;
    private Paint mRingBorderPaint;
    private float mRingCircleInnerRadius;
    private LinearGradient mRingGradient;
    private Paint mRingPaint;
    private float mRingRadius;
    private int mSecondHandColor;
    private PointF mSecondHandEnd;
    private Paint mSecondHandPaint;
    private PointF mSecondHandStart;
    private float mSecondHandStrokeWidth;
    private int mShadowColor;
    private RectF mSquareFaceInsetRect;
    private float mSquareInsetMargin;
    private PointF mTickEndPoint;
    private PointF mTickStartPoint;
}
