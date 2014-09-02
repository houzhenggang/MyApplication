/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.keyguard;
//------------------------yunlan start--------------------------
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.security.auth.PrivateCredentialPermission;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.content.res.Configuration;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.FileObserver;
import android.os.SystemProperties;
//------------------------yunlan end  --------------------------
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.android.internal.policy.IKeyguardShowCallback;
import com.android.internal.widget.LockPatternUtils;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.FrameLayout;

/* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @{ */
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
/* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @} */

/**
 * Manages creating, showing, hiding and resetting the keyguard.  Calls back
 * via {@link KeyguardViewMediator.ViewMediatorCallback} to poke
 * the wake lock and report that the keyguard is done, which is in turn,
 * reported to this class by the current {@link KeyguardViewBase}.
 */
public class KeyguardViewManager {
    private final static boolean DEBUG = KeyguardViewMediator.DEBUG;
    private static String TAG = "KeyguardViewManager";
    public final static String IS_SWITCHING_USER = "is_switching_user";

    // Delay dismissing keyguard to allow animations to complete.
    private static final int HIDE_KEYGUARD_DELAY = 500;

    // Timeout used for keypresses
    static final int DIGIT_PRESS_WAKE_MILLIS = 5000;

    private final Context mContext;
    private final ViewManager mViewManager;
    private final KeyguardViewMediator.ViewMediatorCallback mViewMediatorCallback;

    private WindowManager.LayoutParams mWindowLayoutParams;
    private boolean mNeedsInput = false;

    private ViewManagerHost mKeyguardHost;
    private KeyguardHostView mKeyguardView;

    private boolean mScreenOn = false;
    private LockPatternUtils mLockPatternUtils;

    // SPRD: Modify 20131230 Spreadst of Bug 262132 modify UUI lockscreen policy
    static boolean mIsShowUUILock = true;

    /* SPRD: Modify 20140226 Spreadst of Bug 278959 lock view not refresh @{ */
    private int[] mChangeMCCOrMNC;
    /* @} */
	public static boolean YLUNLOCK_SUPPORT = SystemProperties.getBoolean("yl_unlock_support",false);
    
	private KeyguardUpdateMonitorCallback mBackgroundChanger = new KeyguardUpdateMonitorCallback() {
        @Override
        public void onSetBackground(Bitmap bmp) {
            mKeyguardHost.setCustomBackground(bmp != null ?
                    new BitmapDrawable(mContext.getResources(), bmp) : null);
            updateShowWallpaper(bmp == null);
        }
    };

    public interface ShowListener {
        void onShown(IBinder windowToken);
    };

    /**
     * @param context Used to create views.
     * @param viewManager Keyguard will be attached to this.
     * @param callback Used to notify of changes.
     * @param lockPatternUtils
     */
    public KeyguardViewManager(Context context, ViewManager viewManager,
            KeyguardViewMediator.ViewMediatorCallback callback,
            LockPatternUtils lockPatternUtils) {
        mContext = context;
        mViewManager = viewManager;
        mViewMediatorCallback = callback;
        mLockPatternUtils = lockPatternUtils;
		 //------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){		
			mSecurityModel = new KeyguardSecurityModel(mContext);
			registerFileObserver();
		}
    	//------------------------yunlan end  --------------------------
        /* SPRD: Modify 20140226 Spreadst of Bug 278959 lock view not refresh @{ */
        mChangeMCCOrMNC = new int[2];
        mChangeMCCOrMNC[0] = context.getResources().getConfiguration().mcc;
        mChangeMCCOrMNC[1] = context.getResources().getConfiguration().mnc;
        /* @} */
    }

    /**
     * Show the keyguard.  Will handle creating and attaching to the view manager
     * lazily.
     */
    public synchronized void show(Bundle options) {
        if (DEBUG) Log.d(TAG, "show(); mKeyguardView==" + mKeyguardView);
        //------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){
        	isYunlanOpen = getYunlanLockEnable(mContext);
		}
    	//------------------------yunlan end  --------------------------
        boolean enableScreenRotation = shouldEnableScreenRotation();

        maybeCreateKeyguardLocked(enableScreenRotation, false, options);
        maybeEnableScreenRotation(enableScreenRotation);

        // Disable common aspects of the system/status/navigation bars that are not appropriate or
        // useful on any keyguard screen but can be re-shown by dialogs or SHOW_WHEN_LOCKED
        // activities. Other disabled bits are handled by the KeyguardViewMediator talking
        // directly to the status bar service.
        int visFlags = View.STATUS_BAR_DISABLE_HOME;
        if (shouldEnableTranslucentDecor()) {
            mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                       | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }
        if (DEBUG) Log.v(TAG, "show:setSystemUiVisibility(" + Integer.toHexString(visFlags)+")");
        mKeyguardHost.setSystemUiVisibility(visFlags);

        //------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){
        	updateWinLayoutP();
		}
        //------------------------yunlan end  --------------------------
        mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);
        mKeyguardHost.setVisibility(View.VISIBLE);
        mKeyguardView.show();
        mKeyguardView.requestFocus();
    }

    private boolean shouldEnableScreenRotation() {
        Resources res = mContext.getResources();
        return SystemProperties.getBoolean("lockscreen.rot_override",false)
                || res.getBoolean(R.bool.config_enableLockScreenRotation);
    }

    private boolean shouldEnableTranslucentDecor() {
        Resources res = mContext.getResources();
        return res.getBoolean(R.bool.config_enableLockScreenTranslucentDecor);
    }

    class ViewManagerHost extends FrameLayout {
        private static final int BACKGROUND_COLOR = 0x70000000;

        private Drawable mCustomBackground;

        // This is a faster way to draw the background on devices without hardware acceleration
        private final Drawable mBackgroundDrawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                if (mCustomBackground != null) {
                    final Rect bounds = mCustomBackground.getBounds();
                    final int vWidth = getWidth();
                    final int vHeight = getHeight();

                    final int restore = canvas.save();
                    canvas.translate(-(bounds.width() - vWidth) / 2,
                            -(bounds.height() - vHeight) / 2);
                    mCustomBackground.draw(canvas);
                    canvas.restoreToCount(restore);
                } else {
                    canvas.drawColor(BACKGROUND_COLOR, PorterDuff.Mode.SRC);
                }
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };

        public ViewManagerHost(Context context) {
            super(context);
            setBackground(mBackgroundDrawable);
        }

        public void setCustomBackground(Drawable d) {
            mCustomBackground = d;
            if (d != null) {
                d.setColorFilter(BACKGROUND_COLOR, PorterDuff.Mode.SRC_OVER);
            }
            computeCustomBackgroundBounds();
            invalidate();
        }

        private void computeCustomBackgroundBounds() {
            if (mCustomBackground == null) return; // Nothing to do

            /* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @{ */
            // the lockscreen will show transparent and user only see the wallpaper of system
            // but not the lockscreen wallpaper when device power on and showed the lockscreen.
            // because the isLaidOut allways return false untill you unlock once.
            // so we add condition getHeight, if onSizeChanged called, widht and height is ok,
            // then we can calc the bounds of the custom drawable.
            if ((!isLaidOut()) && (getHeight() <= 0)) {
                return; // We'll do this later
            }
            /* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @} */

            final int bgWidth = mCustomBackground.getIntrinsicWidth();
            final int bgHeight = mCustomBackground.getIntrinsicHeight();
            final int vWidth = getWidth();
            final int vHeight = getHeight();

            final float bgAspect = (float) bgWidth / bgHeight;
            final float vAspect = (float) vWidth / vHeight;

            if (bgAspect > vAspect) {
                mCustomBackground.setBounds(0, 0, (int) (vHeight * bgAspect), vHeight);
            } else {
                mCustomBackground.setBounds(0, 0, vWidth, (int) (vWidth / bgAspect));
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            computeCustomBackgroundBounds();
        }

        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            /* SPRD: Modify 20140226 Spreadst of Bug 278959 lock view not refresh  @{
            if (mKeyguardHost.getVisibility() == View.VISIBLE)  */
            if (mKeyguardHost.getVisibility() == View.VISIBLE
                    && !configurationChangedByMCCOrMNC(newConfig)) {
            /* @} */
                // only propagate configuration messages if we're currently showing
                maybeCreateKeyguardLocked(shouldEnableScreenRotation(), true, null);
            } else {
                if (DEBUG) Log.v(TAG, "onConfigurationChanged: view not visible");
            }
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (mKeyguardView != null) {
                // Always process back and menu keys, regardless of focus
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    int keyCode = event.getKeyCode();
                    if (keyCode == KeyEvent.KEYCODE_BACK && mKeyguardView.handleBackKey()) {
                        return true;
                    }
                    /* SPRD: Modify 20131126 Spreadst of Bug 244476 unlock lockscreen when clcik MENU @{
                    else if (keyCode == KeyEvent.KEYCODE_MENU && mKeyguardView.handleMenuKey()) {
                        return true;
                    } */
                    /* @} */
                }
                // Always process media keys, regardless of focus
                if (mKeyguardView.dispatchKeyEvent(event)) {
                    return true;
                }
                /* SPRD: Modify 20140214 Spreadst of Bug 279392 delete 2 chars for password lockscreen @{ */
                if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    return true;
                }
                /* @} */
            }
            return super.dispatchKeyEvent(event);
        }
        /* SPRD: Modify 20140226 Spreadst of Bug 278959 lock view not refresh @{ */
        private boolean configurationChangedByMCCOrMNC(Configuration newConfig) {
            boolean value = false;
            if (mChangeMCCOrMNC[0] != newConfig.mcc || mChangeMCCOrMNC[1] != newConfig.mnc) {
                value = true;
            }
            mChangeMCCOrMNC[0] = newConfig.mcc;
            mChangeMCCOrMNC[1] = newConfig.mnc;
            return value;
        }
        /* @} */
    }

    SparseArray<Parcelable> mStateContainer = new SparseArray<Parcelable>();

    private void maybeCreateKeyguardLocked(boolean enableScreenRotation, boolean force,
            Bundle options) {
        if (mKeyguardHost != null) {
            mKeyguardHost.saveHierarchyState(mStateContainer);
        }

        if (mKeyguardHost == null) {
            if (DEBUG) Log.d(TAG, "keyguard host is null, creating it...");

            mKeyguardHost = new ViewManagerHost(mContext);

            int flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;

            if (!mNeedsInput) {
                flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }

            final int stretch = ViewGroup.LayoutParams.MATCH_PARENT;
            final int type = WindowManager.LayoutParams.TYPE_KEYGUARD;
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    stretch, stretch, type, flags, PixelFormat.TRANSLUCENT);
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
            lp.windowAnimations = R.style.Animation_LockScreen;
            lp.screenOrientation = enableScreenRotation ?
                    ActivityInfo.SCREEN_ORIENTATION_USER : ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;

            if (ActivityManager.isHighEndGfx()) {
                lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
                lp.privateFlags |=
                        WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED;
            }
            lp.privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_SET_NEEDS_MENU_KEY;
            lp.inputFeatures |= WindowManager.LayoutParams.INPUT_FEATURE_DISABLE_USER_ACTIVITY;
            lp.setTitle("Keyguard");
            mWindowLayoutParams = lp;
			//------------------------yunlan begin--------------------------
			if(YLUNLOCK_SUPPORT){
            	updateWinLayoutP();
			}
            //------------------------yunlan end  --------------------------
            mViewManager.addView(mKeyguardHost, lp);

            KeyguardUpdateMonitor.getInstance(mContext).registerCallback(mBackgroundChanger);
        }

        if (force || mKeyguardView == null) {
            /* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @{ */
            // default show lockscreen wallpaper
            WallpaperManager wm = (WallpaperManager) mContext
                    .getSystemService(Context.WALLPAPER_SERVICE);
            Drawable drawable = wm.getDrawable(WallpaperInfo.WALLPAPER_LOCKSCREEN_TYPE);
            Log.d(TAG, "wallpaper drawable=" + drawable);
            mKeyguardHost.setCustomBackground(drawable);
            updateShowWallpaper(drawable == null);
            /* SPRD: bug268719 2014-01-19 can not set lockscreen wallpaper @} */
            View v = mKeyguardHost.findViewById(R.id.keyguard_host_view);
            Log.d(TAG, "maybeCreateKeyguardLocked keyguard_host_view =" + v + "  mKeyguardHost="
                    + mKeyguardHost);
            if (v != null) {
                ((KeyguardHostView) v).onPauseForUUI();
            }
            mKeyguardHost.removeAllViews();
            inflateKeyguardView(options);
            mKeyguardView.requestFocus();
        }

        updateUserActivityTimeoutInWindowLayoutParams();
	    //------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){
        	updateWinLayoutP();
		}
        //------------------------yunlan end  --------------------------
        mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);

        mKeyguardHost.restoreHierarchyState(mStateContainer);
    }

    private void inflateKeyguardView(Bundle options) {
        View v = mKeyguardHost.findViewById(R.id.keyguard_host_view);
        if (v != null) {
            mKeyguardHost.removeView(v);
        }
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.keyguard_host_view, mKeyguardHost, true);
        mKeyguardView = (KeyguardHostView) view.findViewById(R.id.keyguard_host_view);
        mKeyguardView.setLockPatternUtils(mLockPatternUtils);
        mKeyguardView.setViewMediatorCallback(mViewMediatorCallback);
        mKeyguardView.initializeSwitchingUserState(options != null &&
                options.getBoolean(IS_SWITCHING_USER));

        // HACK
        // The keyguard view will have set up window flags in onFinishInflate before we set
        // the view mediator callback. Make sure it knows the correct IME state.
        if (mViewMediatorCallback != null) {
            KeyguardPasswordView kpv = (KeyguardPasswordView) mKeyguardView.findViewById(
                    R.id.keyguard_password_view);

            if (kpv != null) {
                mViewMediatorCallback.setNeedsInput(kpv.needsInput());
            }
        }

        if (options != null) {
            int widgetToShow = options.getInt(LockPatternUtils.KEYGUARD_SHOW_APPWIDGET,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (widgetToShow != AppWidgetManager.INVALID_APPWIDGET_ID) {
                mKeyguardView.goToWidget(widgetToShow);
            }
        }
    }

    public void updateUserActivityTimeout() {
        updateUserActivityTimeoutInWindowLayoutParams();
		//------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){
        	updateWinLayoutP();
		}
        //------------------------yunlan end  --------------------------
        mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);
    }

    private void updateUserActivityTimeoutInWindowLayoutParams() {
        // Use the user activity timeout requested by the keyguard view, if any.
        if (mKeyguardView != null) {
            long timeout = mKeyguardView.getUserActivityTimeout();
            if (timeout >= 0) {
                mWindowLayoutParams.userActivityTimeout = timeout;
                return;
            }
        }

        // Otherwise, use the default timeout.
        mWindowLayoutParams.userActivityTimeout = KeyguardViewMediator.AWAKE_INTERVAL_DEFAULT_MS;
    }

    private void maybeEnableScreenRotation(boolean enableScreenRotation) {
        // TODO: move this outside
        if (enableScreenRotation) {
            if (DEBUG) Log.d(TAG, "Rotation sensor for lock screen On!");
            mWindowLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
        } else {
            if (DEBUG) Log.d(TAG, "Rotation sensor for lock screen Off!");
            mWindowLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
        }
        //------------------------yunlan begin--------------------------
		if(YLUNLOCK_SUPPORT){
        	updateWinLayoutP();
		}
        //------------------------yunlan end  --------------------------
        mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);
    }

    void updateShowWallpaper(boolean show) {
        if (show) {
            mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        } else {
            mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        }

        mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);
    }

    public void setNeedsInput(boolean needsInput) {
        mNeedsInput = needsInput;
        if (mWindowLayoutParams != null) {
            if (needsInput) {
                mWindowLayoutParams.flags &=
                    ~WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            } else {
                mWindowLayoutParams.flags |=
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }
            //------------------------yunlan begin--------------------------
			if(YLUNLOCK_SUPPORT){
            	updateWinLayoutP();
			}
            //------------------------yunlan end  --------------------------

            try {
                mViewManager.updateViewLayout(mKeyguardHost, mWindowLayoutParams);
            } catch (java.lang.IllegalArgumentException e) {
                // TODO: Ensure this method isn't called on views that are changing...
                Log.w(TAG,"Can't update input method on " + mKeyguardHost + " window not attached");
            }
        }
    }

    /**
     * Reset the state of the view.
     */
    public synchronized void reset(Bundle options) {
        if (DEBUG) Log.d(TAG, "reset()");
        // User might have switched, check if we need to go back to keyguard
        // TODO: It's preferable to stay and show the correct lockscreen or unlock if none
        maybeCreateKeyguardLocked(shouldEnableScreenRotation(), true, options);
    }

    public synchronized void onScreenTurnedOff() {
        if (DEBUG) Log.d(TAG, "onScreenTurnedOff()");
        mScreenOn = false;
        if (mKeyguardView != null) {
            mKeyguardView.onScreenTurnedOff();
        }
        // SPRD: Modify 20131230 Spreadst of Bug 262132 modify UUI lockscreen policy
        KeyguardViewManager.setShowUUILock(true);
    }

    public synchronized void onScreenTurnedOn(final IKeyguardShowCallback callback) {
        if (DEBUG) Log.d(TAG, "onScreenTurnedOn()");
        mScreenOn = true;

        // If keyguard is not showing, we need to inform PhoneWindowManager with a null
        // token so it doesn't wait for us to draw...
        final IBinder token = isShowing() ? mKeyguardHost.getWindowToken() : null;

        if (DEBUG && token == null) Slog.v(TAG, "send wm null token: "
                + (mKeyguardHost == null ? "host was null" : "not showing"));

        if (mKeyguardView != null) {
            mKeyguardView.onScreenTurnedOn();

            // Caller should wait for this window to be shown before turning
            // on the screen.
            if (callback != null) {
                if (mKeyguardHost.getVisibility() == View.VISIBLE) {
                    // Keyguard may be in the process of being shown, but not yet
                    // updated with the window manager...  give it a chance to do so.
                    mKeyguardHost.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onShown(token);
                            } catch (RemoteException e) {
                                Slog.w(TAG, "Exception calling onShown():", e);
                            }
                        }
                    });
                } else {
                    try {
                        callback.onShown(token);
                    } catch (RemoteException e) {
                        Slog.w(TAG, "Exception calling onShown():", e);
                    }
                }
            }
        } else if (callback != null) {
            try {
                callback.onShown(token);
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception calling onShown():", e);
            }
        }
    }

    public synchronized void verifyUnlock() {
        if (DEBUG) Log.d(TAG, "verifyUnlock()");
        show(null);
        mKeyguardView.verifyUnlock();
    }

    /**
     * Hides the keyguard view
     */
    public synchronized void hide() {
        if (DEBUG) Log.d(TAG, "hide()");

        if (mKeyguardHost != null) {
            mKeyguardHost.setVisibility(View.GONE);

            // We really only want to preserve keyguard state for configuration changes. Hence
            // we should clear state of widgets (e.g. Music) when we hide keyguard so it can
            // start with a fresh state when we return.
            mStateContainer.clear();

            // Don't do this right away, so we can let the view continue to animate
            // as it goes away.
            if (mKeyguardView != null) {
                final KeyguardViewBase lastView = mKeyguardView;
                mKeyguardView = null;
                mKeyguardHost.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (KeyguardViewManager.this) {
                            lastView.cleanUp();
                            // Let go of any large bitmaps.
                            mKeyguardHost.setCustomBackground(null);
                            updateShowWallpaper(true);
                            mKeyguardHost.removeView(lastView);
                            // SPRD: Modify 20131230 Spreadst of Bug 262132 modify UUI lockscreen policy
                            KeyguardViewManager.setShowUUILock(true);
                            mViewMediatorCallback.keyguardGone();
                        }
                    }
                }, HIDE_KEYGUARD_DELAY);
            }
        }
    }

    /**
     * Dismisses the keyguard by going to the next screen or making it gone.
     */
    public synchronized void dismiss() {
        if (mScreenOn) {
            mKeyguardView.dismiss();
        }
    }

    /**
     * @return Whether the keyguard is showing
     */
    public synchronized boolean isShowing() {
        return (mKeyguardHost != null && mKeyguardHost.getVisibility() == View.VISIBLE);
    }

    public void showAssistant() {
        if (mKeyguardView != null) {
            mKeyguardView.showAssistant();
        }
    }

    public void dispatch(MotionEvent event) {
        if (mKeyguardView != null) {
            mKeyguardView.dispatch(event);
        }
    }

    public void launchCamera() {
        if (mKeyguardView != null) {
            mKeyguardView.launchCamera();
        }
    }

    /* SPRD: Modify 20131230 Spreadst of Bug 262132 modify UUI lockscreen policy @{ */
    static void setShowUUILock(boolean show) {
        mIsShowUUILock = show;
    }

    static boolean IsShowUUILock() {
        return mIsShowUUILock;
    }
    /* @} */

    //------------------------yunlan begin--------------------------
    public static boolean isYunlanOpen = true;
    
	  public boolean getYunlanLockEnable(Context context){
		boolean res = mLockPatternUtils.getYunlanLockScreenEnabled();
		if (DEBUG) Log.d(TAG, " xu ---------res="+res);
		return res;
    }
    
    private KeyguardSecurityModel mSecurityModel;
    private void updateWinLayoutP() {
//  if (DEBUG) Log.d(TAG, " -------------yunlan--------updateWinLayoutP--mSecurityModel="+mSecurityModel);
		if (mSecurityModel.getSecurityMode() == KeyguardSecurityModel.SecurityMode.None
				&& isYunlanOpen) {
			if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER) != 0){
			    mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
	    	}
			if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN) == 0){
			    mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
     		}
			if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0){
			    mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
     		}
		} else {
            if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER) == 0){
			    mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
     		}
			if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN) != 0){
			    mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
	    	}
			if((mWindowLayoutParams.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0){
			    mWindowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
	    	}
		}
	}


	public static BitmapDrawable mYunlanDrawable1=null;
	public static BitmapDrawable mYunlanDrawable2=null;
    public static String savePackageName1 = "";
    public static String  savePackageName2 = "";
    public static int sType1=-1;
    public static int sType2=-1;
    
    
    public void preInitRes(Boolean isSystem, String filePath, String pkgName, ArrayList<String> list) {
    	synchronized (lock) {
	    	BitmapDrawable tmpBitmapDrawable=null;
	    	
	    	if (DEBUG) Log.d(TAG, " -----yunlan----mYunlanDrawable2=" + mYunlanDrawable2 + "," +
	    			" savePackageName2="+savePackageName2+",  sType2="+sType2);
    		if(mYunlanDrawable2 != null && sType2 != 0){
    			recycleisVaildBitmap(mYunlanDrawable2);
    		}
    		
	    	if(isSystem){ 		
	    		Bitmap originalBgBitmap = getSystemLockScreenPaper(filePath);
				if(originalBgBitmap != null) {
					tmpBitmapDrawable = new BitmapDrawable(mContext.getResources(), originalBgBitmap);
					sType2 = 1;
				}
	    	}
		
	    	if(pkgName != null && list != null && tmpBitmapDrawable == null) {
	    		
	        	Resources mThemeRes=null;
	    		if (!TextUtils.isEmpty(pkgName)) {
	    			mThemeRes = getThemeResources(mContext.getPackageManager(),pkgName);
	    		}
	    		if (DEBUG) Log.e(TAG, " -----yunlan-----pkgName="+pkgName + "   mThemeRes"+mThemeRes+"  list.size()"+list.size());
	   	
		    	if(mThemeRes != null && list.size()>0){
		    		tmpBitmapDrawable = (BitmapDrawable)(getDrawable(mThemeRes, pkgName, list.get(0)));
		    		sType2 = 0;
		    	}		
		    }
		
	    	savePackageName2 = pkgName;
    		mYunlanDrawable2 = tmpBitmapDrawable; 
    		
	    	if (DEBUG) Log.d(TAG, " -----yunlan-----sType2="+sType2+", mYunlanDrawable2=" + mYunlanDrawable2
	    			+ ", savePackageName2="+savePackageName2);
	    	
		    if (DEBUG) Log.d(TAG, " -----yunlan---preInitRes--Thread="+Thread.currentThread().getName());
    	}
    } 
    private static byte[] lock = new byte[0];
    public static BitmapDrawable initYunlanDrawable(){
    	synchronized (lock) {
	    	if (DEBUG) Log.d(TAG, " -----yunlan--initYunlanDrawable pre--sType1="+sType1+", mYunlanDrawable1=" + mYunlanDrawable1
	    			+ ", savePackageName1="+savePackageName1);
			if(mYunlanDrawable2 != null){
				if (DEBUG) Log.d("TAG", "--------initYunlanDrawable---------");
				if(mYunlanDrawable1 != null && sType1 != 0){
					recycleisVaildBitmap(mYunlanDrawable1);
				}
		    	mYunlanDrawable1 = mYunlanDrawable2;
		    	mYunlanDrawable2 = null;
		    	savePackageName1 = savePackageName2;
		    	savePackageName2 = "";
		    	sType1 = sType2;
		    	sType2 = -1;
			}
    	}
    	if (DEBUG) Log.d(TAG, " -----yunlan---initYunlanDrawable end--sType1="+sType1+", mYunlanDrawable1=" 
    				+ mYunlanDrawable1 + ", savePackageName1="+savePackageName1);
    	return mYunlanDrawable1;
    }
    
    private static void recycleisVaildBitmap(BitmapDrawable releaseBitmap){
    	if(releaseBitmap != null){
    		releaseBitmap.setCallback(null);
    		Bitmap tmpBitmap=releaseBitmap.getBitmap();
    		if (DEBUG) Log.d(TAG, "--------initYunlanDrawable---------tmpBitmap="+tmpBitmap);
    		if(!tmpBitmap.isRecycled()){
    			tmpBitmap.recycle();
    			System.gc();
    		}
    	}
    	releaseBitmap=null;
    }
    
	public Resources getThemeResources(PackageManager manager,
			String packageName) {
		try {
			return manager.getResourcesForApplication(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Drawable getDrawable(Resources themeRes, String pkgName,
			String resName) {
		Drawable d = null;
		if (DEBUG) Log.d(TAG, "--------xu----pkgName="+pkgName+"   resName="+resName);
		try {
			int id = themeRes.getIdentifier(resName, "drawable", pkgName);
			d = themeRes.getDrawable(id);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return d;
	}

	public Bitmap getSystemLockScreenPaper(String filePath) {
		
		boolean res = chmodFile(filePath);

		if (DEBUG) Log.d(TAG, "xu----------------res=" + res);
		if(!res)
			return null;
		
		File file = new File(filePath);
		if (DEBUG) Log.d(TAG, "xu----------------file.exists()=" +file.exists());
		if(!file.exists()) {
			return null;
		}
		ParcelFileDescriptor fd = null;
		Bitmap bitmap = null;
		try {
			fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
			bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, null);
		} catch (FileNotFoundException e) {
			if (DEBUG) Log.d(TAG, "xu------LockScreen wallpaper not exists,set default wallpaper");
			bitmap = null;
		} finally {
			try {
				fd.close();
			} catch (IOException ioex) {
				if (DEBUG) Log.d(TAG, "xu-----Occur IOException when close FileDescriptor");
				ioex.printStackTrace();
			}
		} 
		return bitmap;
	}
	
	public boolean chmodFile(String toFile) {
		boolean ret = true;
		try {
			String[] cmds = new String[3];
			cmds[0] = "sh";
			cmds[1] = "-c";
			Runtime rt = Runtime.getRuntime();
			cmds[2] = ("chmod 777 " + toFile);
			rt.exec(cmds);
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
    
 	public static UnlockFileObserver mFileObserver=null;
 	private final String unlockWallpaperPathString = "/data/data/com.android.wallpaper.multipicker/files/lockpaper";
 	
 	public void registerFileObserver(){
 		if (DEBUG) Log.d(TAG,"-----yunlan--registerFileObserver ");
		mFileObserver = new UnlockFileObserver(unlockWallpaperPathString);
 		if (DEBUG) Log.d(TAG,"-----yunlan--registerFileObserver mFileObserver ="+mFileObserver);
 		if(mFileObserver != null)
			mFileObserver.startWatching();
 	}
 	
    class UnlockFileObserver extends FileObserver {   
 
        public UnlockFileObserver(String path, int mask) {   
            super(path, mask);   
        }   
  
        public UnlockFileObserver(String path) {   
            super(path);   
        }   
  
        @Override  
        public void onEvent(int event, String path) {   
            final int action = event & FileObserver.ALL_EVENTS;   
            switch (action) {   
            case FileObserver.ACCESS:   
  
                break;   
                   
            case FileObserver.DELETE:   

                break;   
                   
            case FileObserver.OPEN:   

                break;   
                   
            case FileObserver.MODIFY:   

            	break;
            case FileObserver.CLOSE_WRITE: 
            	if (DEBUG) Log.d(TAG,"-----yunlan--close event: path: " + path); 
            	preInitRes(true, unlockWallpaperPathString, "", null);
            	
                break;   
            }   
        }   
           
    }  
    
    //------------------------yunlan end  --------------------------
}
