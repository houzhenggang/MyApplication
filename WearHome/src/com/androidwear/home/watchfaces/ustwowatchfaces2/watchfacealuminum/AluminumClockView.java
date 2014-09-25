package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacealuminum;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.DisplayHelper;

public class AluminumClockView extends WatchFaceView implements
        SensorEventListener {

    private static long UPDATE_RATE_MS = 200L;
    private Bitmap mBmpAmbientBevels;
    private Bitmap mBmpAmbientBrushEffect;
    private Bitmap mBmpAmbientCap;
    private Bitmap mBmpAmbientCapBrushEffect;
    private Bitmap mBmpAmbientInnerCircle;
    private Bitmap mBmpAmbientOuterCircle;
    private Bitmap mBmpFaceAmbient;
    private Bitmap mBmpFaceInteractive;
    private Bitmap mBmpInteractiveBevels;
    private Bitmap mBmpInteractiveBrushEffect;
    private Bitmap mBmpInteractiveCap;
    private Bitmap mBmpInteractiveCapBrushEffect;
    private Bitmap mBmpInteractiveInnerCircle;
    private Bitmap mBmpInteractiveOuterCircle;
    private Context mContext;
    private float mCurrentRotationDegree;
    private Sensor mGyroSensor;
    private Handler mHandler;
    private int mHeight;
    private Paint mHourHandPaintAmbient;
    private Paint mHourHandPaintInteractive;
    private float mLastRotationDegree;
    private float mLastZRotationValue;
    private Paint mMinuteHandPaintAmbient;
    private Paint mMinuteHandPaintInteractive;
    private Paint mSecondHandPaint;
    private boolean mSensorListening;
    private SensorManager mSensorManager;
    private int mWidth;
    private Runnable updateRunnable = new Runnable() {

        public void run() {
            update();
        }

    };

    public AluminumClockView(Context context) {
        this(context, null);
    }

    public AluminumClockView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public AluminumClockView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mGyroSensor = null;
        mSensorListening = false;
        mLastZRotationValue = 0.0F;
        mLastRotationDegree = 0.0F;
        mCurrentRotationDegree = 0.0F;
        mHandler = new Handler();
        mWidth = 0;
        mHeight = 0;
        mContext = context;
        init();
    }

    private void init() {
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(11);
        android.util.DisplayMetrics displaymetrics = getResources()
                .getDisplayMetrics();
        mHourHandPaintAmbient = new Paint(1);
        mHourHandPaintAmbient.setColor(Color.argb(255, 141, 141, 141));
        mHourHandPaintAmbient.setStyle(android.graphics.Paint.Style.STROKE);
        mHourHandPaintAmbient.setStrokeWidth(DisplayHelper.convertDpToPixel(
                displaymetrics, 8F));
        mHourHandPaintInteractive = new Paint(1);
        mHourHandPaintInteractive.setColor(Color.argb(255, 43, 43, 43));
        mHourHandPaintInteractive.setStyle(android.graphics.Paint.Style.STROKE);
        mHourHandPaintInteractive.setStrokeWidth(DisplayHelper
                .convertDpToPixel(displaymetrics, 8F));
        mMinuteHandPaintAmbient = new Paint(1);
        mMinuteHandPaintAmbient.setColor(Color.argb(255, 141, 141, 141));
        mMinuteHandPaintAmbient.setStyle(android.graphics.Paint.Style.STROKE);
        mMinuteHandPaintAmbient.setStrokeWidth(DisplayHelper.convertDpToPixel(
                displaymetrics, 2.0F));
        mMinuteHandPaintInteractive = new Paint(1);
        mMinuteHandPaintInteractive.setColor(Color.argb(255, 43, 43, 43));
        mMinuteHandPaintInteractive
                .setStyle(android.graphics.Paint.Style.STROKE);
        mMinuteHandPaintInteractive.setStrokeWidth(DisplayHelper
                .convertDpToPixel(displaymetrics, 2.0F));
        mSecondHandPaint = new Paint(1);
        mSecondHandPaint.setColor(Color.argb(255, 232, 5, 83));
        mSecondHandPaint.setStyle(android.graphics.Paint.Style.STROKE);
        mSecondHandPaint.setStrokeWidth(2.0F);
        mHandler.postDelayed(updateRunnable, UPDATE_RATE_MS);
    }

    private void update() {
        postInvalidate();
    }

    protected boolean isContinuous() {
        return true;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = mWidth / 2;
        float f1 = mHeight / 2;
        Calendar calendar = Calendar.getInstance();
        double hour = calendar.get(Calendar.HOUR);
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);
        double millsecond = calendar.get(Calendar.MILLISECOND);
        double d4;
        float f3;
        double d5;
        double d6;
        float f4;
        double d7;
        double d8;
        Paint paint;
        double d9;
        float f5;
        double d10;
        double d11;
        float f6;
        double d12;
        double d13;
        Paint paint1;
        if (isAmbient()) {
            canvas.drawBitmap(mBmpAmbientOuterCircle, f
                    - (float) (mBmpAmbientOuterCircle.getWidth() / 2), f1
                    - (float) (mBmpAmbientOuterCircle.getHeight() / 2), null);
            canvas.drawBitmap(mBmpAmbientInnerCircle, f
                    - (float) (mBmpAmbientInnerCircle.getWidth() / 2), f1
                    - (float) (mBmpAmbientInnerCircle.getHeight() / 2), null);
            canvas.drawBitmap(mBmpAmbientBevels,
                    f - (float) (mBmpAmbientBevels.getWidth() / 2), f1
                            - (float) (mBmpAmbientBevels.getHeight() / 2), null);
            canvas.drawBitmap(mBmpAmbientBrushEffect, f
                    - (float) (mBmpAmbientBrushEffect.getWidth() / 2), f1
                    - (float) (mBmpAmbientBrushEffect.getHeight() / 2), null);
            canvas.drawBitmap(mBmpAmbientCap,
                    f - (float) (mBmpAmbientCap.getWidth() / 2), f1
                            - (float) (mBmpAmbientCap.getHeight() / 2), null);
            canvas.drawBitmap(mBmpAmbientCapBrushEffect, f
                    - (float) (mBmpAmbientCapBrushEffect.getWidth() / 2), f1
                    - (float) (mBmpAmbientCapBrushEffect.getHeight() / 2), null);
        } else {
            float f2 = mCurrentRotationDegree - mLastRotationDegree;
            canvas.save();
            canvas.rotate(f2, f, f1);
            canvas.drawBitmap(mBmpInteractiveOuterCircle, f
                    - (float) (mBmpInteractiveOuterCircle.getWidth() / 2), f1
                    - (float) (mBmpInteractiveOuterCircle.getHeight() / 2),
                    null);
            canvas.restore();
            canvas.save();
            canvas.rotate(-f2, f, f1);
            canvas.drawBitmap(mBmpInteractiveInnerCircle, f
                    - (float) (mBmpInteractiveInnerCircle.getWidth() / 2), f1
                    - (float) (mBmpInteractiveInnerCircle.getHeight() / 2),
                    null);
            canvas.restore();
            canvas.drawBitmap(mBmpInteractiveBevels, f
                    - (float) (mBmpInteractiveBevels.getWidth() / 2), f1
                    - (float) (mBmpInteractiveBevels.getHeight() / 2), null);
            canvas.drawBitmap(mBmpInteractiveBrushEffect, f
                    - (float) (mBmpInteractiveBrushEffect.getWidth() / 2), f1
                    - (float) (mBmpInteractiveBrushEffect.getHeight() / 2),
                    null);
            canvas.save();
            canvas.rotate(f2, f, f1);
            canvas.drawBitmap(mBmpInteractiveCap, f
                    - (float) (mBmpInteractiveCap.getWidth() / 2), f1
                    - (float) (mBmpInteractiveCap.getHeight() / 2), null);
            canvas.restore();
            canvas.drawBitmap(
                    mBmpInteractiveCapBrushEffect,
                    f - (float) (mBmpInteractiveCapBrushEffect.getWidth() / 2),
                    f1
                            - (float) (mBmpInteractiveCapBrushEffect
                                    .getHeight() / 2), null);
        }
        if (!isAmbient()) {
            double d14 = Math
                    .toRadians(360D * ((millsecond + 1000D * second) / 60000D) - 90D);
            float f7 = (float) (mBmpInteractiveInnerCircle.getWidth() / 2) - 2.0F;
            double d15 = (double) f7 * Math.cos(d14) + (double) f;
            double d16 = (double) f7 * Math.sin(d14) + (double) f1;
            float f8 = (float) (mBmpInteractiveOuterCircle.getWidth() / 2) - 1.0F;
            double d17 = (double) f8 * Math.cos(d14) + (double) f;
            double d18 = (double) f8 * Math.sin(d14) + (double) f1;
            canvas.drawLine((float) d15, (float) d16, (float) d17, (float) d18,
                    mSecondHandPaint);
        }
        d4 = Math.toRadians(360D * ((minute + second / 60D) / 60D) - 90D);
        f3 = (float) (mBmpInteractiveCapBrushEffect.getWidth() / 2) - 2.0F;
        d5 = (double) f3 * Math.cos(d4) + (double) f;
        d6 = (double) f3 * Math.sin(d4) + (double) f1;
        f4 = (float) (mBmpInteractiveOuterCircle.getWidth() / 2) - 1.0F;
        d7 = (double) f4 * Math.cos(d4) + (double) f;
        d8 = (double) f4 * Math.sin(d4) + (double) f1;
        if (isAmbient())
            paint = mMinuteHandPaintAmbient;
        else
            paint = mMinuteHandPaintInteractive;
        canvas.drawLine((float) d5, (float) d6, (float) d7, (float) d8, paint);
        d9 = Math.toRadians(360D * ((hour + minute / 60D) / 12D) - 90D);
        f5 = (float) (mBmpInteractiveCapBrushEffect.getWidth() / 2) - 2.0F;
        d10 = (double) f5 * Math.cos(d9) + (double) f;
        d11 = (double) f5 * Math.sin(d9) + (double) f1;
        f6 = (float) (mBmpInteractiveInnerCircle.getWidth() / 2) - 2.0F;
        d12 = (double) f6 * Math.cos(d9) + (double) f;
        d13 = (double) f6 * Math.sin(d9) + (double) f1;
        if (isAmbient()) {
            paint1 = mHourHandPaintAmbient;
        } else {
            paint1 = mHourHandPaintInteractive;
        }
        canvas.drawLine((float) d10, (float) d11, (float) d12, (float) d13,
                paint1);
        if (!isAmbient()) {
            UPDATE_RATE_MS = 60000L;
        } else {
            UPDATE_RATE_MS = 25L;
        }
        if (mSensorListening) {
            if (mGyroSensor != null)
                mSensorManager.unregisterListener(this);
            mSensorListening = false;
        }
        mHandler.postDelayed(updateRunnable, UPDATE_RATE_MS);

        if (!mSensorListening) {
            if (mGyroSensor != null)
                mSensorManager.registerListener(this, mGyroSensor, 3);
            mSensorListening = true;
        }
    }

    public void onSensorChanged(SensorEvent sensorevent) {
        mLastRotationDegree = mCurrentRotationDegree;
        float f = sensorevent.values[2];
        mCurrentRotationDegree = 360F * (f - mLastZRotationValue);
        mLastZRotationValue = f;
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mWidth = i;
        mHeight = i;
        Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_interactive_outer_circle));
        Bitmap bitmap1 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_interactive_inner_circle));
        Bitmap bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_interactive_bevels));
        Bitmap bitmap3 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_interactive_brush_effect));
        Bitmap bitmap4 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_cap));
        Bitmap bitmap5 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(),
                R.drawable.aluminum_interactive_cap_brush_effect));
        mBmpInteractiveOuterCircle = Bitmap.createScaledBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.aluminum_interactive_outer_circle), mWidth,
                mHeight, true);
        mBmpInteractiveInnerCircle = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.aluminum_interactive_inner_circle),
                        (int) (((float) bitmap1.getWidth() / (float) bitmap
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap1.getHeight() / (float) bitmap
                                .getHeight()) * (float) mHeight), true);
        mBmpInteractiveBevels = Bitmap
                .createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.aluminum_interactive_bevels),
                        (int) (((float) bitmap2.getWidth() / (float) bitmap
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap2.getHeight() / (float) bitmap
                                .getHeight()) * (float) mHeight), true);
        mBmpInteractiveBrushEffect = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.aluminum_interactive_brush_effect),
                        (int) (((float) bitmap3.getWidth() / (float) bitmap
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap3.getHeight() / (float) bitmap
                                .getHeight()) * (float) mHeight), true);
        mBmpInteractiveCap = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.aluminum_ambient_cap),
                        (int) (((float) bitmap4.getWidth() / (float) bitmap
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap4.getHeight() / (float) bitmap
                                .getHeight()) * (float) mHeight), true);
        mBmpInteractiveCapBrushEffect = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.aluminum_interactive_cap_brush_effect),
                        (int) (((float) bitmap5.getWidth() / (float) bitmap
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap5.getHeight() / (float) bitmap
                                .getHeight()) * (float) mHeight), true);
        Bitmap bitmap6 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_outer_circle));
        Bitmap bitmap7 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_inner_circle));
        Bitmap bitmap8 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_bevels));
        Bitmap bitmap9 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_brush_effect));
        Bitmap bitmap10 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_cap));
        Bitmap bitmap11 = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.aluminum_ambient_cap_brush_effect));
        mBmpAmbientOuterCircle = Bitmap.createScaledBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.aluminum_ambient_outer_circle), mWidth,
                mHeight, true);
        mBmpAmbientInnerCircle = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.aluminum_ambient_inner_circle),
                        (int) (((float) bitmap7.getWidth() / (float) bitmap6
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap7.getHeight() / (float) bitmap6
                                .getHeight()) * (float) mHeight), true);
        mBmpAmbientBevels = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.aluminum_ambient_bevels),
                        (int) (((float) bitmap8.getWidth() / (float) bitmap6
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap8.getHeight() / (float) bitmap6
                                .getHeight()) * (float) mHeight), true);
        mBmpAmbientBrushEffect = Bitmap
                .createScaledBitmap(BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.aluminum_ambient_brush_effect),
                        (int) (((float) bitmap9.getWidth() / (float) bitmap6
                                .getWidth()) * (float) mWidth),
                        (int) (((float) bitmap9.getHeight() / (float) bitmap6
                                .getHeight()) * (float) mHeight), true);
        mBmpAmbientCap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),
                        R.drawable.aluminum_ambient_cap),
                (int) (((float) bitmap10.getWidth() / (float) bitmap6
                        .getWidth()) * (float) mWidth),
                (int) (((float) bitmap10.getHeight() / (float) bitmap6
                        .getHeight()) * (float) mHeight), true);
        mBmpAmbientCapBrushEffect = Bitmap.createScaledBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.aluminum_ambient_cap_brush_effect),
                (int) (((float) bitmap11.getWidth() / (float) bitmap6
                        .getWidth()) * (float) mWidth),
                (int) (((float) bitmap11.getHeight() / (float) bitmap6
                        .getHeight()) * (float) mHeight), true);
        float f = mWidth / 2;
        float f1 = mHeight / 2;
        mBmpFaceAmbient = Bitmap.createBitmap(
                mBmpAmbientOuterCircle.getWidth(),
                mBmpAmbientOuterCircle.getHeight(),
                mBmpAmbientOuterCircle.getConfig());
        Canvas canvas = new Canvas(mBmpFaceAmbient);
        canvas.drawBitmap(mBmpAmbientOuterCircle, f
                - (float) (mBmpAmbientOuterCircle.getWidth() / 2), f1
                - (float) (mBmpAmbientOuterCircle.getHeight() / 2), null);
        canvas.drawBitmap(mBmpAmbientInnerCircle, f
                - (float) (mBmpAmbientInnerCircle.getWidth() / 2), f1
                - (float) (mBmpAmbientInnerCircle.getHeight() / 2), null);
        canvas.drawBitmap(mBmpAmbientBevels,
                f - (float) (mBmpAmbientBevels.getWidth() / 2), f1
                        - (float) (mBmpAmbientBevels.getHeight() / 2), null);
        canvas.drawBitmap(mBmpAmbientBrushEffect, f
                - (float) (mBmpAmbientBrushEffect.getWidth() / 2), f1
                - (float) (mBmpAmbientBrushEffect.getHeight() / 2), null);
        canvas.drawBitmap(mBmpAmbientCap,
                f - (float) (mBmpAmbientCap.getWidth() / 2), f1
                        - (float) (mBmpAmbientCap.getHeight() / 2), null);
        canvas.drawBitmap(mBmpAmbientCapBrushEffect, f
                - (float) (mBmpAmbientCapBrushEffect.getWidth() / 2), f1
                - (float) (mBmpAmbientCapBrushEffect.getHeight() / 2), null);
        mBmpFaceInteractive = Bitmap.createBitmap(
                mBmpInteractiveOuterCircle.getWidth(),
                mBmpInteractiveOuterCircle.getHeight(),
                mBmpInteractiveOuterCircle.getConfig());
        Canvas canvas1 = new Canvas(mBmpFaceInteractive);
        canvas1.drawBitmap(mBmpInteractiveOuterCircle, f
                - (float) (mBmpInteractiveOuterCircle.getWidth() / 2), f1
                - (float) (mBmpInteractiveOuterCircle.getHeight() / 2), null);
        canvas1.drawBitmap(mBmpInteractiveInnerCircle, f
                - (float) (mBmpInteractiveInnerCircle.getWidth() / 2), f1
                - (float) (mBmpInteractiveInnerCircle.getHeight() / 2), null);
        canvas1.drawBitmap(mBmpInteractiveBevels, f
                - (float) (mBmpInteractiveBevels.getWidth() / 2), f1
                - (float) (mBmpInteractiveBevels.getHeight() / 2), null);
        canvas1.drawBitmap(mBmpInteractiveBrushEffect, f
                - (float) (mBmpInteractiveBrushEffect.getWidth() / 2), f1
                - (float) (mBmpInteractiveBrushEffect.getHeight() / 2), null);
        canvas1.drawBitmap(mBmpInteractiveCap,
                f - (float) (mBmpInteractiveCap.getWidth() / 2), f1
                        - (float) (mBmpInteractiveCap.getHeight() / 2), null);
        canvas1.drawBitmap(mBmpInteractiveCapBrushEffect, f
                - (float) (mBmpInteractiveCapBrushEffect.getWidth() / 2), f1
                - (float) (mBmpInteractiveCapBrushEffect.getHeight() / 2), null);
    }

}
