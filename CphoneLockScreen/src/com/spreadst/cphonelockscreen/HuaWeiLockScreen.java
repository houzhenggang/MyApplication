
package com.spreadst.cphonelockscreen;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.TextView;
import android.os.BatteryManager;
import android.widget.AbsLockScreen;
import android.widget.ILockScreenListener;
import android.os.SystemProperties;

import com.android.internal.R;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;

import android.telephony.TelephonyManager;

public class HuaWeiLockScreen extends AbsLockScreen implements OnClickListener {

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
    private static final int LOCK_MAX_LENGHT = 500;

    private ValueAnimator mLightScaleAnim;
    private MyViewMagnifier mMagnifier;

    private final int HEXAGON_TOTAL = 9;
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
    private Vibrator mVibrator;
    private boolean mShowBottomView = true;

    private ImageView mCameraTip1;
    private ImageView mCameraTip2;
    private ImageView mCameraTip3;
    private ValueAnimator mTipAnimator1;
    private ValueAnimator mTipAnimator2;
    private ValueAnimator mTipAnimator3;
    private LinearLayout mSldieView;

    private final static int MESSAGE_HIDE_MAGNIFIER = 1;
    private final static int MESSAGE_START_TIP_FLASH = 2;
    private final static int MESSAGE_ANIM1 = 3;
    private final static int MESSAGE_ANIM2 = 4;
    private final static int MESSAGE_ANIM3 = 5;

    protected static final String PACKAGE_NAME = "com.spreadst.cphonelockscreen";
    LockPatternUtils mLockPatternUtils;
    ServiceState[] mServiceState;
    private String battery_no;
    private String battery_full;
    private String battery_low;
    private Resources res;
    protected int mPhoneState;
    private TelephonyManager[] mTelephonyManager;
    private PhoneStateListener[] mPhoneStateListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_HIDE_MAGNIFIER:
                    hideMagnifier();
                    break;
                case MESSAGE_START_TIP_FLASH:
                    int stap = msg.getData().getInt("stap");
                    if (stap == 4) {
                        setTipAlpha1(1.0f);
                    } else {
                        startFlashTip(stap);
                    }
                    break;
                case MESSAGE_ANIM1:
                    float alpha1 = msg.getData().getFloat("alpha");
                    setTipAlpha1(alpha1);
                    break;
                case MESSAGE_ANIM2:
                    float alpha2 = msg.getData().getFloat("alpha");
                    setTipAlpha2(alpha2);
                    break;
                case MESSAGE_ANIM3:
                    float alpha3 = msg.getData().getFloat("alpha");
                    setTipAlpha3(alpha3);
                    break;
            }
        }
    };

    public HuaWeiLockScreen(Context context, ILockScreenListener listener) {
        super(context, listener);
        try {
            mContext = context;
            mLockScreenListener = listener;
            mLockPatternUtils = new LockPatternUtils(context);
            res = context.getPackageManager().getResourcesForApplication(
                    PACKAGE_NAME);
            final LayoutInflater inflater = LayoutInflater.from(context);
            inflater.inflate(res.getLayout(res.getIdentifier(PACKAGE_NAME
                    + ":layout/simple_unlock_view", null, null)), this, true);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            mScreenWidth = wm.getDefaultDisplay().getWidth();
            mSCreenHeight = wm.getDefaultDisplay().getHeight();
            mDiagonal = (int) Math.sqrt(mScreenWidth * mScreenWidth + mSCreenHeight * mSCreenHeight);
            mServiceState = new ServiceState[TelephonyManager.getPhoneCount()];
            mPhoneStateListener = new PhoneStateListener[TelephonyManager.getPhoneCount()];
            mTelephonyManager = new TelephonyManager[TelephonyManager.getPhoneCount()];
            for (int i=0; i < TelephonyManager.getPhoneCount(); i++) {
                mTelephonyManager[i] = (TelephonyManager)getContext().getSystemService(TelephonyManager.getServiceName(
                        Context.TELEPHONY_SERVICE, i));
                mPhoneStateListener[i] = getPhoneStateListener(i);
                mTelephonyManager[i].listen(mPhoneStateListener[i],PhoneStateListener.LISTEN_SERVICE_STATE);
            }
            mEmergencyCallButton = (Button) findViewById(res.getIdentifier(PACKAGE_NAME
                    + ":id/emergencycallbutton", null, null));
            if (mEmergencyCallButton != null) {
                if (!canEmergencyCall()) {
                    mEmergencyCallButton.setClickable(false);
                    mEmergencyCallButton.setText(R.string.emergency_call_no_service);
    
                } else {
                    mEmergencyCallButton.setClickable(true);
                    mEmergencyCallButton.setText(R.string.lockscreen_emergency_call);
                }
                mEmergencyCallButton.setOnClickListener(this);
                mEmergencyCallButton.setFocusable(false); // touch only!
            }
            mSldieView = (LinearLayout) findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/sldie_event_View", null, null));
            TextView locktip = (TextView) findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/locktip", null, null));
            locktip.setText(res.getString(res.getIdentifier(
                    PACKAGE_NAME + ":string/slide_to_unlock", null, null)));

            View eventView = inflater.inflate(res.getLayout(res.getIdentifier(PACKAGE_NAME
                    + ":layout/slide_event_notify_view", null, null)), null, true);
            mSldieView.addView(eventView);

            mNewMmsView = eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/new_mms", null, null));
            mNewMmsText = (TextView) eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/mmsNum", null, null));
            ImageView mNewMmsImage = (ImageView) eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/mmsImage", null, null));
            mNewMmsImage.setImageDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":drawable/message", null, null)));
            mMissCallView = eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/miss_call", null, null));
            mMissCallText = (TextView) eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/callNum", null, null));
            ImageView mMissCallImage = (ImageView) eventView.findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/callImage", null, null));
            mMissCallImage.setImageDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":drawable/call", null, null)));
            mChargingView = (TextView) findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/chargingstatus", null, null));
            mTopView = findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/top_view", null, null));
            mTopView.setOnTouchListener(mNotUnLockTouchListener);
            mOpenCamera = (ImageButton) findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/open_camera", null, null));
            mOpenCamera.setBackgroundDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":drawable/lock_background", null, null)));
            mOpenCamera.setOnTouchListener(mOpenCameraListener);
            mFlareFrameLayout = (FrameLayout) findViewById(res.getIdentifier(
                    PACKAGE_NAME + ":id/flareframelayout2", null, null));
            lightObj = new FrameLayout(mContext);
            mFlareFrameLayout.addView(lightObj);

            battery_no = res.getString(res.getIdentifier(PACKAGE_NAME
                    + ":string/keyguard_plugged_in", null, null));
            battery_full = res.getString(res.getIdentifier(PACKAGE_NAME
                    + ":string/keyguard_charged", null, null));
            battery_low = res.getString(res.getIdentifier(PACKAGE_NAME
                    + ":string/keyguard_low_battery", null, null));

            setRainBowLayout();
            initFlashTip();
            onRefreshBatteryInfo(listener.shouldShowBatteryInfo(),
                    listener.isDevicePluggedIn(), listener.getBatteryLevel());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                stopFlashTip();
                mLockScreenListener.pokeWakelock();
                if (isMagnifierShowing()) {
                    moveMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y));
                } else {
                    showMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y), true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int lenght = getMagnifier().calculateMoveLenght(mMoveX, mMoveY);
                if (lenght > LOCK_MAX_LENGHT && mShowBottomView) {
                    getMagnifier().calculateMoveLenght(0, 0);
                    if (isMagnifierShowing()) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_MAGNIFIER, 100);
                    }
                    mLockScreenListener.goToUnlockScreen();
                    return true;
                }
                moveMagnifier(Math.round(x), Math.round(y), Math.round(x), Math.round(y));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int lenght2 = getMagnifier().calculateMoveLenght(mMoveX, mMoveY);
                if (lenght2 > LOCK_MAX_LENGHT && mShowBottomView) {
                } else {
                    startFlashTip(3);
                }
                getMagnifier().calculateMoveLenght(0, 0);
                if (isMagnifierShowing()) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_MAGNIFIER, 100);
                }
                break;
        }
        return true;
    }

    private void setTipAlpha1(float alpha) {
        mCameraTip1.setAlpha(alpha);
        if (alpha >= 0.4f && alpha <= 0.5f) {
            Message m = new Message();
            m.what = MESSAGE_ANIM2;
            Bundle b = new Bundle();
            b.putFloat("alpha", 1.0f);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 50);
        }
        if (alpha >= 0.0f) {
            float newAlpha = alpha - 0.10f;
            Message m = new Message();
            m.what = MESSAGE_ANIM1;
            Bundle b = new Bundle();
            b.putFloat("alpha", newAlpha);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 50);
        }
    }

    private void setTipAlpha2(float alpha) {
        mCameraTip2.setAlpha(alpha);
        if (alpha >= 0.4f && alpha <= 0.5f) {
            Message m = new Message();
            m.what = MESSAGE_ANIM3;
            Bundle b = new Bundle();
            b.putFloat("alpha", 1.0f);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 40);
        }
        if (alpha >= 0.0f) {
            float newAlpha = alpha - 0.10f;
            Message m = new Message();
            m.what = MESSAGE_ANIM2;
            Bundle b = new Bundle();
            b.putFloat("alpha", newAlpha);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 40);
        }
    }

    private void setTipAlpha3(float alpha) {
        mCameraTip3.setAlpha(alpha);
        if (alpha < 0.1f) {
            Message m = new Message();
            m.what = MESSAGE_START_TIP_FLASH;
            Bundle b = new Bundle();
            b.putInt("stap", 1);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 200);
        } else {
            Message m = new Message();
            m.what = MESSAGE_ANIM3;
            Bundle b = new Bundle();
            b.putFloat("alpha", alpha - 0.10f);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 40);
        }
    }
    private void initFlashTip() {
        mCameraTip1 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/camera_tip1", null, null));
        mCameraTip2 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/camera_tip2", null, null));
        mCameraTip3 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/camera_tip3", null, null));
        mCameraTip1.setBackgroundDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":drawable/tip", null, null)));
        mCameraTip2.setBackgroundDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":drawable/tip", null, null)));
        mCameraTip3.setBackgroundDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":drawable/tip", null, null)));
    }

    private void stopFlashTip() {
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_ANIM1);
            mHandler.removeMessages(MESSAGE_ANIM2);
            mHandler.removeMessages(MESSAGE_ANIM3);
            mHandler.removeMessages(MESSAGE_START_TIP_FLASH);
        }
        mCameraTip1.setAlpha(1.0f);
        mCameraTip2.setAlpha(1.0f);
        mCameraTip3.setAlpha(1.0f);
    }

    private void showFlashTip() {
        mCameraTip1.setVisibility(View.VISIBLE);
        mCameraTip2.setVisibility(View.VISIBLE);
        mCameraTip3.setVisibility(View.VISIBLE);
    }
    
    private void hideFlashTip() {
        mCameraTip1.setVisibility(View.GONE);
        mCameraTip2.setVisibility(View.GONE);
        mCameraTip3.setVisibility(View.GONE);
    }

    private void startFlashTip(int stap) {
        if (stap == 1) {
            mCameraTip1.setAlpha(1.0f);
            Message m = new Message();
            m.what = MESSAGE_START_TIP_FLASH;
            Bundle b = new Bundle();
            b.putInt("stap", 2);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 150);
        } else if (stap == 2) {
            mCameraTip2.setAlpha(1.0f);
            Message m = new Message();
            m.what = MESSAGE_START_TIP_FLASH;
            Bundle b = new Bundle();
            b.putInt("stap", 3);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 150);
        } else if (stap == 3) {
            mCameraTip3.setAlpha(1.0f);
            Message m = new Message();
            m.what = MESSAGE_START_TIP_FLASH;
            Bundle b = new Bundle();
            b.putInt("stap", 4);
            m.setData(b);
            mHandler.sendMessageDelayed(m, 150);
        }
    }

    private void cancelAnimator(Animator animator) {
        if ((animator != null) && (animator.isRunning())) {
            animator.cancel();
        }
    }

    private void setRainBowLayout() {
        mFlareRainbow0 = new ImageViewBlended(mContext);
        mFlareRainbow0.setImageDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":raw/rainbow0", null, null)));
        mFlareFrameLayout.addView(mFlareRainbow0, -2, -2);
        setAlphaAndVisibility(mFlareRainbow0, 0.0f);
        mFlareRainbow1 = new ImageViewBlended(mContext);
        mFlareRainbow1.setImageDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":raw/rainbow1", null, null)));
        mFlareFrameLayout.addView(mFlareRainbow1, -2, -2);
        setAlphaAndVisibility(mFlareRainbow1, 0.0f);
        mFlareRainbow2 = new ImageViewBlended(mContext);
        mFlareRainbow2.setImageDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":raw/rainbow2", null, null)));
        mFlareFrameLayout.addView(mFlareRainbow2, -2, -2);
        setAlphaAndVisibility(mFlareRainbow2, 0.0f);
        mFlareRainbow3 = new ImageViewBlended(mContext);
        mFlareRainbow3.setImageDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":raw/rainbow3", null, null)));
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
            localImageViewBlended.setImageDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":raw/pentagon", null, null)));
            float f = (float) (20.0D * Math.random());
            setAlphaAndVisibility(localImageViewBlended, 0.0f);
            localImageViewBlended.setRotation(f);
            lightObj.addView(localImageViewBlended, -2, -2);
            mFlarePentagonNear[i] = localImageViewBlended;
        }
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            ImageViewBlended localImageViewBlended = new ImageViewBlended(mContext);
            localImageViewBlended.setImageDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":raw/pentagon", null, null)));
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
                0.75f, 1.2f, 0);
        setCenterPos(mFlareRainbow0, showStartX, showStartY, mRelativelyX, mRelativelyY,
                0.8f, 1.1f, 0);
    }

    private void setHexagonRandomTarget() {
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            float f1 = 0.3f + 0.5f * (float) Math.random();
            setAlphaAndVisibility(mFlarePentagonNear[i], f1);
            float f2 = 0.4f *  ((float) Math.random() - 0.5f);
            hexagonDistanceNear[i] = 0.4f * (getHexagonDistance(i)-1) / 3;//(float) (f2 + (0.2f + 0.24f * i));
        }
        float[] shuffle = getShuffleArray(hexagonDistanceNear);

        int radius = mDiagonal / 2;
        float lenght = (float) Math.sqrt(mYDelay * mYDelay + mXDelay * mXDelay);
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            /*
            float scale = (i + 1) / 3 * 0.1f;
            if (i > HEXAGON_TOTAL / 2) {
                scale = (HEXAGON_TOTAL - i + 1) / 3 * 0.1f;
            } else if (i == HEXAGON_TOTAL / 2) {
                scale = 0.4f;
            }*/
            float scale = getHexagonScale(i);
            if (lenght >= 0 && lenght < radius / 2) {
                hexagonScaleNear[i] = 0.6f;
            } else {
                hexagonScaleNear[i] = 0.6f - (0.6f / radius / 2) * lenght;
            }
            hexagonScaleNear[i] = hexagonScaleNear[i] + scale;
            setCenterPos(mFlarePentagonNear[i], showStartX, showStartY, currentX, currentY,
                    hexagonDistanceNear[i], hexagonScaleNear[i], 0);
        }

        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            float f1 = 0.3f + 0.5f * (float) Math.random();
            setAlphaAndVisibility(mFlarePentagonFar[i], f1);
            float f2 = 0.4f * ((float) Math.random() - 0.5f);
            hexagonDistanceFar[i] = 0.4f * (getHexagonDistance(i)+1) / 3;//(float) (f2 + (0.2f + 0.24f * i));
        }
        shuffle = getShuffleArray(hexagonDistanceFar);
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            /*
            float scale = (i + 1) / 3 * 0.1f;
            if (i > HEXAGON_TOTAL / 2) {
                scale = (HEXAGON_TOTAL - i + 1) / 3 * 0.1f;
            } else if (i == HEXAGON_TOTAL / 2) {
                scale = 0.4f;
            }
            */
            float scale = getHexagonScale(i);
            if (lenght >= 0 && lenght < radius / 2) {
                hexagonScaleFar[i] = 0.6f;
            } else {
                hexagonScaleFar[i] = 0.6f - (0.6f / radius / 2) * lenght;
            }
            hexagonScaleFar[i] = hexagonScaleFar[i] + scale;
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

        int radius = mDiagonal / 2;
        float lenght = (float) Math.sqrt(mYDelay * mYDelay + mXDelay * mXDelay);

        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            /*
            float scale = (i + 1) / 3 * 0.1f;
            if (i > HEXAGON_TOTAL / 2) {
                scale = (HEXAGON_TOTAL - i + 1) / 3 * 0.1f;
            } else if (i == HEXAGON_TOTAL / 2) {
                scale = 0.4f;
            }
            */
            float scale = getHexagonScale(i);
            if (lenght >= 0 && lenght < radius / 2) {
                hexagonScaleNear[i] = 0.6f;
            } else {
                hexagonScaleNear[i] = (float) (0.7 - (0.8 - 0.6) / (radius / 2) * lenght);//0.6f - (0.6f / radius / 2) * lenght;
            }
            hexagonScaleNear[i] = hexagonScaleNear[i] + scale;
            setCenterPos(mFlarePentagonNear[i], showStartX, showStartY, hoverX, hoverY,
                    hexagonDistanceNear[i], hexagonScaleNear[i], 0);
        }
        for (int i = 0; i < HEXAGON_TOTAL; i++) {
            /*
            float scale = (i + 1) / 3 * 0.1f;
            if (i > HEXAGON_TOTAL / 2) {
                scale = (HEXAGON_TOTAL - i + 1) / 3 * 0.1f;
            } else if (i == HEXAGON_TOTAL / 2) {
                scale = 0.4f;
            }
            */
            float scale = getHexagonScale(i);
            if (lenght >= 0 && lenght < radius / 2) {
                hexagonScaleFar[i] = 0.6f;
            } else {
                hexagonScaleFar[i] = (float) (0.7 - (0.8 - 0.6) / (radius / 2) * lenght);//0.6f - (0.6f / radius / 2) * lenght;
            }
            hexagonScaleFar[i] = hexagonScaleFar[i] + scale;
            setCenterPos(mFlarePentagonFar[i], showStartX, showStartY, mRelativelyX, mRelativelyY,
                    hexagonDistanceFar[i], hexagonScaleFar[i], 0);
        }
        setRainBowMove();
    }

    private float getHexagonDistance(int i) {
        float distance = 0.0f;
        switch(i) {
            case 0:
                distance = 1.5f;
                break;
            case 1:
                distance = 2.5f;
                break;
            case 2:
                distance = 4.3f;
                break;
            case 3:
                distance = 4.5f;
                break;
            case 4:
                distance = 4.6f;
                break;
            case 5:
                distance = 6;
                break;
            case 6:
                distance = 6;
                break;
            case 7:
                distance = 6.5f;
                break;
            case 8:
                distance = 7;
                break;
        }
        return distance;
    }

    private float getHexagonScale(int i) {
        float scale = 0.0f;
        switch(i) {
            case 0:
                scale = -0.18f;
            case 1:
                scale = -0.2f;
                break;
            case 2:
                scale = -0.25f;
                break;
            case 3:
                scale = -0.27f;
                break;
            case 4:
                scale = 0.15f;
                break;
            case 5:
                scale = -0.3f;
                break;
            case 6:
                scale = -0.15f;
                break;
            case 7:
                scale = -0.3f;
                break;
            case 8:
                scale = -0.05f;
                break;
        }
        return scale;
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
            float distance = 90.f;
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
        public void setImageDrawable(Drawable drawable) {
            super.setImageDrawable(drawable);
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
            FrameLayout host = new FrameLayout(mContext);
            host.setBackgroundDrawable(res.getDrawable(res.getIdentifier(
                    PACKAGE_NAME + ":drawable/host_background", null, null)));
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
            float btnX = mOpenCamera.getX();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    hideFlashTip();
                    mDownX = x;
                    mDownY = y;
                    flareShow(mDownX, mDownY);
                    mOpenCameraY = mOpenCamera.getY();
                    mShowBottomView = false;
                    getMagnifier().showBottomView(false);
                    vibrate(100);
                    break;
                case MotionEvent.ACTION_MOVE:
                    flareMove(x, y);
                    if (y < mOpenCameraY && x >= btnX) {
                        mOpenCamera.setY(y);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    showFlashTip();
                    mShowBottomView = true;
                    getMagnifier().showBottomView(true);
                    flareHide();
                    mOpenCamera.setY(mOpenCameraY);
                    int lenght = getMagnifier().getMoveLenght();
                    if (y < mOpenCameraY && x >= btnX) {
                        if (lenght >= mScreenWidth / 2) {
                            openCamera();
                        }
                    }
            }
            onTouchEvent(event);
            return true;
        }
    };

    private void openCamera() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setClassName("com.android.gallery3d", "com.android.camera.CameraLauncher");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mOnUnLockListener.onUnLockStartActivity(intent, true);
    }

    public void vibrate(long milliseconds) {
        if (mVibrator == null) {
            mVibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(milliseconds);
    }

    private View mNewMmsView;
    private View mMissCallView;
    private TextView mNewMmsText;
    private TextView mMissCallText;
    private TextView mChargingView;
    private View mTopView;
    private View mBottomView;
    private Button mEmergencyCallButton;
    private OnUnLockListener mOnUnLockListener;
    protected boolean mBatteryCharged;
    protected boolean mBatteryIsLow;
    boolean mShowingBatteryInfo = false;
    boolean mCharging = false;
    int mBatteryLevel = 100;

    public void setMissCallCount(int count) {
        if (count > 0) {
            mMissCallView.setVisibility(View.VISIBLE);
            mMissCallText.setText(count + "");
        } else {
            mMissCallView.setVisibility(View.INVISIBLE);
        }
    }

    public void setNewSmsCount(int count) {
        if (count > 0) {
            mNewMmsView.setVisibility(View.VISIBLE);
            mNewMmsText.setText(count + "");
        } else {
            mNewMmsView.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnUnLockListener {
        void onUnLockStartActivity(Intent intent, boolean startActivity);
    }

    public void setOnUnLockListener(OnUnLockListener listener) {
        mOnUnLockListener = listener;
    }

    private OnTouchListener mNotUnLockTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mShowBottomView = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mShowBottomView = true;
            }
            onTouchEvent(event);
            return true;
        }
    };

    private void updateChargedView() {
        String charge = getChargeInfo();
        if (mChargingView != null) {
            if (charge != null && !"".equals(charge)) {
                mChargingView.setText(charge);
                mChargingView.setVisibility(View.VISIBLE);
            } else {
                mChargingView.setVisibility(View.GONE);
            }
        }
    }

    private String getChargeInfo() {
        String string = null;
        if (mShowingBatteryInfo) {
            // Battery status
            if (mCharging) {
                // Charging, charged or waiting to charge.
                string = res.getString(mBatteryCharged
                        ? res.getIdentifier(PACKAGE_NAME
                                + ":string/keyguard_charged", null, null)
                        : res.getIdentifier(PACKAGE_NAME
                                + ":string/keyguard_plugged_in", null, null), mBatteryLevel);
            } else if (mBatteryIsLow) {
                // Battery is low
                string = res.getString(
                        res.getIdentifier(PACKAGE_NAME
                                + ":string/keyguard_low_battery", null, null));
            }
        }
        return string;
    }

    private void updateEmergencyCallButtonState(int phoneState) {
        if (mEmergencyCallButton != null) {
            mLockPatternUtils.updateEmergencyCallButtonState(mEmergencyCallButton, phoneState,
                    true, false);
            if (!canEmergencyCall()) {
                mEmergencyCallButton.setClickable(false);
                mEmergencyCallButton.setText(R.string.emergency_call_no_service);
            } else {
                mEmergencyCallButton.setClickable(true);
            }
        }
        if ("cucc".equals(SystemProperties.get("ro.operator", ""))) {
            boolean hasIccCard = false;
            for (int i = 0; i < TelephonyManager.getPhoneCount(); i++) {
                hasIccCard |= mTelephonyManager[i].hasIccCard();
            }
            if (!hasIccCard) {
                mEmergencyCallButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setBottomView(View view) {
        mBottomView = view;
    }
    public void setBottomViewVisibility(int visibility) {
        if (mBottomView != null) {
            mBottomView.setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == res.getIdentifier(PACKAGE_NAME + ":id/emergencycallbutton", null, null)) {
            Log.d(PACKAGE_NAME, "takeEmergencyCallAction()");
            mLockScreenListener.takeEmergencyCallAction();
        }
    }

    private boolean canEmergencyCall(){
        boolean isEmergencyOnly = false;
        boolean hasService = false;
        for (int i = 0; i < TelephonyManager.getPhoneCount(); i++) {
            if (mServiceState[i] != null) {
                isEmergencyOnly = isEmergencyOnly ? true : mServiceState[i].isEmergencyOnly();
                hasService = hasService(i);
                if(hasService){
                    return true;
                } else {
                    if(isEmergencyOnly){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private PhoneStateListener getPhoneStateListener(final int phoneId) {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState state) {
                mServiceState[phoneId] = state;
                updateEmergencyCallButtonState(mPhoneState);
            }
        };
        return phoneStateListener;
    }

    private boolean hasService(int subscription) {
        if (mServiceState[subscription] != null) {
            switch (mServiceState[subscription].getState()) {
                case ServiceState.STATE_OUT_OF_SERVICE:
                    return false;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onRefreshBatteryInfo(boolean showBatteryInfo,
            boolean pluggedIn, int batteryLevel) {
        if (pluggedIn) {
            mChargingView.setText(batteryLevel == 100 ? battery_full : battery_no
                    + batteryLevel + "%");
            mChargingView.setVisibility(View.VISIBLE);
        } else {
            if (batteryLevel < 15) {
                mChargingView.setText(battery_low);
                mChargingView.setVisibility(View.VISIBLE);
            } else {
                mChargingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onTimeChanged() {
    }
    @Override
    public boolean needsInput() {
        return false;
    }
    @Override
    public void onPhoneStateChanged(int phoneState) {
        mPhoneState = phoneState;
        updateEmergencyCallButtonState(phoneState);
    }
    @Override
    public void onRefreshCarrierInfo(CharSequence plmn, CharSequence spn,
            int subscription) {
    }

    @Override
    public void onRingerModeChanged(int state) {
    }

    @Override
    public void onSimStateChanged(State simState, int subscription) {
    }
    @Override
    public void onPause() {
        for (int i = 0; i < TelephonyManager.getPhoneCount(); i++) {
            ((TelephonyManager) getContext().getSystemService(TelephonyManager.getServiceName(Context.TELEPHONY_SERVICE,i))).listen(
                    mPhoneStateListener[i],
                    PhoneStateListener.LISTEN_NONE);
        }
        /* @} */
    }

    @Override
    public void onResume() {
        for (int i=0; i < TelephonyManager.getPhoneCount(); i++) {
            ((TelephonyManager)getContext().getSystemService(TelephonyManager.getServiceName(
                    Context.TELEPHONY_SERVICE, i))).listen(mPhoneStateListener[i],PhoneStateListener.LISTEN_SERVICE_STATE);
        }
        /* @} */
    }

    @Override
    public void cleanUp() {
        if (mMagnifier != null) {
            mMagnifier.destroy();
        }
        stopFlashTip();
    }

    @Override
    public void onStartAnim() {
    }

    @Override
    public void onStopAnim() {
    }

    @Override
    public void onClockVisibilityChanged() {
    }

    @Override
    public void onDeviceProvisioned() {
    }
    @Override
    public void onMessageCountChanged(int messagecount) {
        setNewSmsCount(messagecount);
    }
    @Override
    public void onDeleteMessageCount(int messagecount) {
        setNewSmsCount(messagecount);
    }

    @Override
    public void onMissedCallCountChanged(int count) {
        setMissCallCount(count);
    }
    @Override
    public void onScreenTurnedOn() {
        startFlashTip(3);
    }

    @Override
    public void onScreenTurnedOff(int why) {
        stopFlashTip();
    }
}
