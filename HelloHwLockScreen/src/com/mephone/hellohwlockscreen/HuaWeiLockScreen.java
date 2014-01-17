
package com.mephone.hellohwlockscreen;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HuaWeiLockScreen extends RelativeLayout {

    private ImageButton mOpenCamera;
    private Context mContext;
    private float mDownX;
    private float mDownY;
    private float showStartX;
    private float showStartY;
    private float currentX;
    private float currentY;
    private float hoverX;
    private float hoverY;
    private float mRelativelyX;
    private float mRelativelyY;
    private float mXDelay;
    private float mYDelay;
    private boolean isTouched = false;
    private static final int LOCK_MAX_LENGHT = 600;

    private ValueAnimator mLightScaleAnim;
    private MyViewMagnifier mMagnifier;

    private final int HEXAGON_TOTAL = 5;
    private FrameLayout mFlareFrameLayout;
    private FrameLayout lightObj;
    private ImageViewBlended mFlareRainbow0;
    private ImageViewBlended mFlareRainbow1;
    private ImageViewBlended mFlareRainbow2;
    private ImageViewBlended mFlareRainbow3;
    private ImageViewBlended[] mFlarePentagonNear;
    private ImageViewBlended[] mFlarePentagonFar;

    private double distance;
    private float[] hexagonScaleNear;
    private float[] hexagonDistanceNear;
    private float[] hexagonScaleFar;
    private float[] hexagonDistanceFar;
    private int mScreenWidth;
    private int mSCreenHeight;
    private int mDiagonal;

    private final static int MESSAGE_HIDE_MAGNIFIER = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_HIDE_MAGNIFIER:
                    hideMagnifier();
                    break;
            }
        }
    };

    public HuaWeiLockScreen(Context context) {
        this(context, null);
    }

    public HuaWeiLockScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mSCreenHeight = wm.getDefaultDisplay().getHeight();
        mDiagonal = (int) Math.sqrt(mScreenWidth * mScreenWidth + mSCreenHeight * mSCreenHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();
        int mMoveX = (int) (x - mDownX);
        int mMoveY = (int) (y - mDownY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                flareShow(mDownX, mDownY);
                if (isMagnifierShowing()) {
                    moveMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y));
                } else {
                    showMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y), true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int lenght = getMagnifier().calculateMoveLenght(mMoveX, mMoveY);
                if (lenght > LOCK_MAX_LENGHT) {
                    // System.exit(0);
                }
                flareMove(x, y);
                moveMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getMagnifier().calculateMoveLenght(0, 0);
                if (isMagnifierShowing()) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_MAGNIFIER, 100);
                }
                flareHide();
                break;
        }
        return true;
    }

    private void cancelAnimator(Animator animator) {
        if ((animator != null) && (animator.isRunning())) {
            animator.cancel();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOpenCamera = (ImageButton) findViewById(R.id.open_camera);
        mOpenCamera.setOnTouchListener(mOpenCameraListener);
        mFlareFrameLayout = (FrameLayout) findViewById(R.id.flareframelayout);
        lightObj = new FrameLayout(mContext);
        mFlareFrameLayout.addView(lightObj);
        setRainBowLayout();
    }

    private void setRainBowLayout() {
        mFlareRainbow0 = new ImageViewBlended(mContext);
        mFlareRainbow0.setImageResource(R.raw.rainbow0);
        mFlareFrameLayout.addView(mFlareRainbow0, -2, -2);
        setAlphaAndVisibility(mFlareRainbow0, 0.0f);
        mFlareRainbow1 = new ImageViewBlended(mContext);
        mFlareRainbow1.setImageResource(R.raw.rainbow1);
        mFlareFrameLayout.addView(mFlareRainbow1, -2, -2);
        setAlphaAndVisibility(mFlareRainbow1, 0.0f);
        mFlareRainbow2 = new ImageViewBlended(mContext);
        mFlareRainbow2.setImageResource(R.raw.rainbow2);
        mFlareFrameLayout.addView(mFlareRainbow2, -2, -2);
        setAlphaAndVisibility(mFlareRainbow2, 0.0f);
        mFlareRainbow3 = new ImageViewBlended(mContext);
        mFlareRainbow3.setImageResource(R.raw.rainbow3);
        mFlareFrameLayout.addView(mFlareRainbow3, -2, -2);
        setAlphaAndVisibility(mFlareRainbow3, 0.0f);

        mFlarePentagonNear = new ImageViewBlended[HEXAGON_TOTAL];
        mFlarePentagonFar = new ImageViewBlended[HEXAGON_TOTAL];
        hexagonScaleNear = new float[HEXAGON_TOTAL];
        hexagonScaleFar = new float[HEXAGON_TOTAL];
        hexagonDistanceNear = new float[HEXAGON_TOTAL];
        hexagonDistanceFar = new float[HEXAGON_TOTAL];

        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            ImageViewBlended localImageViewBlended = new ImageViewBlended(mContext);
            localImageViewBlended.setImageResource(R.raw.pentagon);
            float f = (float) (20.0D * Math.random());
            setAlphaAndVisibility(localImageViewBlended, 0.0f);
            localImageViewBlended.setRotation(f);
            lightObj.addView(localImageViewBlended, -2, -2);
            mFlarePentagonNear[i] = localImageViewBlended;
        }
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            ImageViewBlended localImageViewBlended = new ImageViewBlended(mContext);
            localImageViewBlended.setImageResource(R.raw.pentagon);
            float f = (float) (20.0D * Math.random());
            setAlphaAndVisibility(localImageViewBlended, 0.0f);
            localImageViewBlended.setRotation(f);
            lightObj.addView(localImageViewBlended, -2, -2);
            mFlarePentagonFar[i] = localImageViewBlended;
        }
    }

    private void setAlphaAndVisibility(View view, float alpha) {
        float mAlpha = getCorrectAlpha(alpha);

        if (mAlpha == 0.0f) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(mAlpha);
        }
    }

    private float getCorrectAlpha(float alpha) {
        if (alpha <= 0.0f) {
            alpha = 0.0f;
        }
        if (alpha >= 1.0f) {
            alpha = 1.0f;
        }
        return alpha;
    }

    public void flareShow(float x, float y) {
        isTouched = true;
        showStartX = mScreenWidth / 2;
        showStartY = mSCreenHeight / 2;
        currentX = x;
        currentY = y;
        calculateDistance(currentX, currentY);
        setHexagonRandomTarget();
        setRainBowShow();
    }

    public void flareHide() {
        isTouched = false;
        setHexagonHide();
        setRainBowHide();
    }

    private void setHexagonHide() {
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            setAlphaAndVisibility(mFlarePentagonNear[i], 0.0f);
        }
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            setAlphaAndVisibility(mFlarePentagonFar[i], 0.0f);
        }
    }

    private void setRainBowHide() {
        setAlphaAndVisibility(mFlareRainbow0, 0.0f);
        setAlphaAndVisibility(mFlareRainbow1, 0.0f);
        setAlphaAndVisibility(mFlareRainbow2, 0.0f);
        setAlphaAndVisibility(mFlareRainbow3, 0.0f);
    }

    private void setRainBowShow() {
        float lenght = (float) Math.sqrt(mYDelay * mYDelay + mXDelay * mXDelay);
        float alpha = 0.8f * lenght / mDiagonal;
        setAlphaAndVisibility(mFlareRainbow0, alpha);
        setAlphaAndVisibility(mFlareRainbow1, alpha);
        setAlphaAndVisibility(mFlareRainbow2, alpha);
        setAlphaAndVisibility(mFlareRainbow3, alpha);
        float xDelay = mRelativelyX - showStartX;
        float yDelay = mRelativelyY - showStartY;
        float angle = 180f;
        float f = 180 + (float) (angle * Math.atan2(yDelay, xDelay) / Math.PI);
        mFlareRainbow2.setRotation(f);
        mFlareRainbow1.setRotation(f);
        mFlareRainbow3.setRotation(f);
        mFlareRainbow0.setRotation(f);
        setCenterPos(mFlareRainbow2, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.3f, 0.6f, 0);
        setCenterPos(mFlareRainbow1, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.6f, 1.2f, 0);
        setCenterPos(mFlareRainbow3, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.75f, 0.8f, 0);
        setCenterPos(mFlareRainbow0, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.8f, 1.1f, 0);
    }

    private void setHexagonRandomTarget() {
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            float f1 = 0.3f + 0.5f * (float) Math.random();
            setAlphaAndVisibility(mFlarePentagonNear[i], f1);
            float f2 = 0.4f * ((float) Math.random() - 0.5f);
            hexagonDistanceNear[i] = (float) (f2 + (0.2f + 0.24f * i));
        }
        float[] shuffle = getShuffleArray(hexagonDistanceNear);
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            hexagonScaleNear[i] = (-0.2f + shuffle[i]);
            setCenterPos(mFlarePentagonNear[i], showStartX, showStartY, currentX, currentY,
                    hexagonDistanceNear[i], hexagonScaleNear[i], 0);
        }

        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            float f1 = 0.3f + 0.5f * (float) Math.random();
            setAlphaAndVisibility(mFlarePentagonFar[i], f1);
            float f2 = 0.4f * ((float) Math.random() - 0.5f);
            hexagonDistanceFar[i] = (float) (f2 + (0.2f + 0.24f * i));
        }
        shuffle = getShuffleArray(hexagonDistanceFar);
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            hexagonScaleFar[i] = (-0.2f + shuffle[i]);
            setCenterPos(mFlarePentagonFar[i], showStartX, showStartY, mRelativelyX, mRelativelyY,
                    hexagonDistanceFar[i], hexagonScaleFar[i], 0);
        }
    }

    private float[] getShuffleArray(float[] array) {

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i] + "");
        }
        Collections.shuffle(list);
        float[] result = new float[array.length];
        for (int i = 0; i < list.size(); i++) {
            result[i] = Float.parseFloat(list.get(i));
        }
        return result;
    }

    private void flareMove(float x, float y) {
        if (!isTouched) {
            flareShow(x, y);
        }
        hoverX = x;
        hoverY = y;
        calculateDistance(hoverX, hoverY);
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            setCenterPos(mFlarePentagonNear[i], showStartX, showStartY, hoverX, hoverY,
                    hexagonDistanceNear[i], 1000f, 0);
        }
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            setCenterPos(mFlarePentagonFar[i], showStartX, showStartY, mRelativelyX, mRelativelyY,
                    hexagonDistanceFar[i], 1000f, 0);
        }
        setRainBowMove();
    }

    private void setRainBowMove() {
        float lenght = (float) Math.sqrt(mYDelay * mYDelay + mXDelay * mXDelay);
        float alpha = 0.8f * lenght / mDiagonal;
        setAlphaAndVisibility(mFlareRainbow0, alpha + 0.2f);
        setAlphaAndVisibility(mFlareRainbow1, alpha);
        setAlphaAndVisibility(mFlareRainbow2, alpha);
        setAlphaAndVisibility(mFlareRainbow3, alpha);
        
        float xDelay = mRelativelyX - showStartX;
        float yDelay = mRelativelyY - showStartY;
        float angle = 180f;
        float f = 180 + (float) (angle * Math.atan2(yDelay, xDelay) / Math.PI);
        mFlareRainbow2.setRotation(f);
        mFlareRainbow1.setRotation(f);
        mFlareRainbow3.setRotation(f);
        mFlareRainbow0.setRotation(f);
        setCenterPos(mFlareRainbow2, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.3f, 1000f, 0);
        setCenterPos(mFlareRainbow1, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.6f, 1000f, 0);
        setCenterPos(mFlareRainbow3, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.75f, 1000f, 0);
        setCenterPos(mFlareRainbow0, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.8f, 1000f, 0);
    }

    private void setCenterPos(View view, float mStartX, float mStartY, float mCurrentX,
            float mCurrentY, float mScaleValue) {
        float f1 = mStartX + mScaleValue * (mCurrentX - mStartX);
        float f2 = mStartY + mScaleValue * (mCurrentY - mStartY);
        float f3 = f1 - view.getWidth() / 2.0f;
        float f4 = f2 - view.getHeight() / 2.0f;
        if (view.getWidth() != 0) {
            view.setX(f3);
            view.setY(f4);
        }
    }

    private void setCenterPos(View view, float mStartX, float mStartY, float mCurrentX,
            float mCurrentY, float mDistanceValue, float mScaleValue, int mRotateAngle) {
        if (mScaleValue != 1000f) {
            float distance = (float) (100.0f * Math.random());
            float f1 = 0.5f + 0.5f * ((float) distance / 720.0f);
            float f2 = (0.5f + 0.5f * 1) * (mScaleValue * f1);
            view.setScaleX(f2);
            view.setScaleY(f2);
        }

        float f3 = 0.5f + 0.5f * 1;
        float f4 = mStartX + f3 * (mDistanceValue * (mCurrentX - mStartX));
        float f5 = mStartY + f3 * (mDistanceValue * (mCurrentY - mStartY));
        if (mRotateAngle != 0) {
            float f8 = mScaleValue * 300.0f;
            float f9 = mScaleValue * (mScaleValue * ((float) distance / 1000.0f));
            float f10 = 1.0f * 1;
            double d = 3.141592653589793D * mRotateAngle / 180.0D + f9 + f10;
            f4 = mCurrentX + (float) (f8 * Math.cos(d) + f8 * Math.sin(d));
            f5 = mCurrentY + (float) (f8 * -Math.sin(d) + f8 * Math.cos(d));
        }
        float f6 = f4 - view.getWidth() / 2.0f;
        float f7 = f5 - view.getHeight() / 2.0f;
        view.setX(f6);
        view.setY(f7);
    }

    private void calculateDistance(float x, float y) {
        float f1 = x - showStartX;
        float f2 = y - showStartY;
        distance = Math.sqrt(Math.pow(f1, 2.0D) + Math.pow(f2, 2.0D));
        mXDelay = Math.abs(x - showStartX);
        mYDelay = Math.abs(y - showStartY);
        float newX = x;
        float newy = y;
        if (x <= showStartX && y <= showStartY) {
            newX = x + mXDelay * 2;
            newy = y + mYDelay * 2;
        } else if (x > showStartX && y <= showStartY) {
            newX = x - mXDelay * 2;
            newy = y + mYDelay * 2;
        } else if (x <= showStartX && y > showStartY) {
            newX = x + mXDelay * 2;
            newy = y - mYDelay * 2;
        } else if (x > showStartX && y > showStartY) {
            newX = x - mXDelay * 2;
            newy = y - mYDelay * 2;
        }
        mRelativelyX = newX;
        mRelativelyY = newy;
    }

    public class ImageViewBlended extends ImageView {
        private Bitmap mBitmap;
        private Paint mPaint = new Paint();
        private Rect mRect;
        private PorterDuff.Mode mMode = PorterDuff.Mode.ADD;

        public ImageViewBlended(Context context) {
            super(context);
        }

        public ImageViewBlended(Context context, PorterDuff.Mode mode) {
            super(context);
            mMode = mode;
        }

        public ImageViewBlended(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public ImageViewBlended(Context context, AttributeSet attributeSet, int param) {
            super(context, attributeSet, param);
        }

        protected void onDraw(Canvas canvas) {
            mPaint.setFlags(1);
            Paint localPaint = mPaint;
            canvas.drawBitmap(mBitmap, null, mRect, localPaint);
        }

        public void setImageResource(int mResId) {
            super.setImageResource(mResId);
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            if (mMode != null) {
                mPaint.setXfermode(new PorterDuffXfermode(mMode));
            }
            mRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        }
    }

    private boolean isMagnifierShowing() {
        if (mMagnifier != null) {
            return mMagnifier.isShowing();
        }
        return false;
    }

    private void moveMagnifier(int curX, int curY, int realX, int realY) {
        int[] location = new int[2];
        this.getLocationOnScreen(location);
        int lx = location[0];
        int ly = location[1];
        getMagnifier().move(curX + lx, realY + ly, realX + lx, realY + ly);
    }

    private void hideMagnifier() {
        if (mMagnifier != null) {
            mMagnifier.hide();
        }
    }

    private void showMagnifier(int curX, int curY, int realX, int realY, boolean animated) {
        int[] location = new int[2];
        this.getLocationOnScreen(location);
        int lx = location[0];
        int ly = location[1];
        getMagnifier().show(curX + lx, realY + ly, realX + lx, realY + ly, animated);
        this.getParent().requestDisallowInterceptTouchEvent(true);
    }

    private synchronized MyViewMagnifier getMagnifier() {
        if (mMagnifier == null) {
            // FrameLayout host = (FrameLayout) findViewById(R.id.bottomView);
            FrameLayout host = new FrameLayout(mContext);
            mMagnifier = new MyViewMagnifier(host);
        }
        return mMagnifier;
    }

    private float mOpenCameraY = 0;
    private OnTouchListener mOpenCameraListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            float x = event.getRawX();
            float y = event.getRawY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    // flareShow(mDownX, mDownY);
                    mOpenCameraY = mOpenCamera.getY();
                    getMagnifier().showBottomView(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // flareMove(x, y);
                    float btnX = mOpenCamera.getX();
                    if (y < mOpenCameraY && x >= btnX && x <= (btnX + mOpenCamera.getWidth())) {
                        mOpenCamera.setY(y);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    getMagnifier().showBottomView(true);
                    // flareHide();
                    mOpenCamera.setY(mOpenCameraY);
            }
            onTouchEvent(event);
            return true;
        }
    };
}
