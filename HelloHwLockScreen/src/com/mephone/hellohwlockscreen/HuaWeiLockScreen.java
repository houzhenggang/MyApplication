
package com.mephone.hellohwlockscreen;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HuaWeiLockScreen extends RelativeLayout {

    private FrameLayout mFlareFrameLayout;
    private ImageView mLight;
    private Context mContext;
    private float mDownX;
    private float mDownY;
    private float showStartX;
    private float showStartY;
    private float hoverX;
    private float hoverY;

    private float mDefalutScale = 2.0f;
    private float mCurrentScale = mDefalutScale;
    private ValueAnimator mLightScaleAnim;

    public HuaWeiLockScreen(Context context) {
        this(context, null);
    }

    public HuaWeiLockScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
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
                mCurrentScale = mDefalutScale;
                cancelAnimator(mLightScaleAnim);
                flareShow(mDownX, mDownY);
                mLight.setAlpha(0.8f);
                mLight.setScaleX(mCurrentScale);
                mLight.setScaleY(mCurrentScale);
                mLight.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                hoverMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                doLightScaleAnim(mCurrentScale);
                break;
        }
        return true;
    }

    private void doLightScaleAnim(float currentScale) {
        mLightScaleAnim = ValueAnimator.ofFloat(currentScale, 0);
        mLightScaleAnim.setDuration(300);
        mLightScaleAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                mLight.setScaleX(value);
                mLight.setScaleY(value);
                float alpha = value;
                alpha -= 0.5f;
                if (alpha > 0.8f) {
                    alpha = 0.8f;
                }
                mLight.setAlpha(alpha);
                if (0 == value) {
                    mLight.setVisibility(View.GONE);
                }
            }
        });
        mLightScaleAnim.start();
    }

    private void cancelAnimator(Animator animator) {
        if ((animator != null) && (animator.isRunning())) {
            animator.cancel();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFlareFrameLayout = (FrameLayout) findViewById(R.id.flareframelayout);
        mLight = (ImageView) findViewById(R.id.light);
    }

    public void flareShow(float x, float y) {
        showStartX = x;
        showStartY = y;
        setCenterPos(mLight, showStartX, showStartY, showStartX, showStartY, 1.0f);
    }

    private void hoverMove(float x, float y) {
        hoverX = x;
        hoverY = y;
        scaleLightView(showStartX, showStartY, hoverX, hoverY);
        setCenterPos(mLight, showStartX, showStartY, hoverX, hoverY, 1.0f);
    }

    private void scaleLightView(float mStartX, float mStartY, float mCurrentX,
            float mCurrentY) {
        float lenght = (float) Math.hypot(Math.abs(mStartX - mCurrentX),
                Math.abs(mStartY - mCurrentY));
        //mCurrentScale = lenght;
        //mLight.setScaleX(lenght);
        //mLight.setScaleY(lenght);
    }

    private void setCenterPos(View view, float mStartX, float mStartY, float mCurrentX,
            float mCurrentY, float mScaleValue) {
        float f1 = mStartX + mScaleValue * (mCurrentX - mStartX);
        float f2 = mStartY + mScaleValue * (mCurrentY - mStartY);
        float f3 = f1 - view.getWidth() / 2.0f;
        float f4 = f2 - view.getHeight() / 2.0f;
        view.setX(f3);
        view.setY(f4);
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
}
