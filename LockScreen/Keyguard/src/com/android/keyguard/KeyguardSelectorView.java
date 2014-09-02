/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.multiwaveview.GlowPadView;
import com.android.internal.widget.multiwaveview.GlowPadView.OnTriggerListener;
import android.os.SystemProperties;

public class KeyguardSelectorView extends LinearLayout implements KeyguardSecurityView {
    private static final boolean DEBUG = KeyguardHostView.DEBUG;
    private static final String TAG = "SecuritySelectorView";
    private static final String ASSIST_ICON_METADATA_NAME =
        "com.android.systemui.action_assist_icon";

    private KeyguardSecurityCallback mCallback;
    private GlowPadView mGlowPadView;
    private ObjectAnimator mAnim;
    private View mFadeView;
    private boolean mIsBouncing;
    private boolean mCameraDisabled;
    private boolean mSearchDisabled;
    private LockPatternUtils mLockPatternUtils;
    private SecurityMessageDisplay mSecurityMessageDisplay;
    private Drawable mBouncerFrame;

    /* SPRD: Modify 20131126 Spreadst of Bug 244515 original lockscreen change by UUI @{ */
    private static String universeSupportKey = "universe_ui_support";
    boolean isUniverseSupport = SystemProperties.getBoolean(universeSupportKey, false);
    /* @} */

    /* SPRD: Modify 20140120 Spreadst of Bug 266285 do not show unread message @{ */
    private int mMessageCount = 0;
    private int mMissedCallCount = 0;
    private TextView mMessageCountTextView;
    private TextView mMissedCallCountTextView;
    /* @} */
/* Innova:fengchuan on: Tue, 03 Jun 2014 14:16:37 +0800
 * TODO: replace this line with your comment
 */
    public static boolean SINGLEUNLOCKSCREEN = SystemProperties.getBoolean("ro.unlockscreen.pattern",true);
// End of Innova:fengchuan

    OnTriggerListener mOnTriggerListener = new OnTriggerListener() {

        public void onTrigger(View v, int target) {
            final int resId = mGlowPadView.getResourceIdForTarget(target);
            /* SPRD: Modify 20131126 Spreadst of Bug 244515 original lockscreen change by UUI @{ */
            if (isUniverseSupport) {
                switch (resId) {
                    case R.drawable.ic_lockscreen_call:
                        Intent callIntent = new Intent();
                        callIntent.setAction(Intent.ACTION_DIAL);
                        if (callIntent != null) {
                            mActivityLauncher.launchActivity(callIntent, false, true, null, null);
                        } else {
                            Log.w(TAG, "Failed to get intent for assist activity");
                        }
                        mCallback.userActivity(0);
                        break;

                    case R.drawable.ic_lockscreen_sms:
                        Intent smsIntent = new Intent(Intent.ACTION_MAIN);
                        smsIntent.setType("vnd.android.cursor.dir/mms");
                        if (smsIntent != null) {
                            mActivityLauncher.launchActivity(smsIntent, false, true, null, null);
                        } else {
                            Log.w(TAG, "Failed to get intent for assist activity");
                        }
                        mCallback.userActivity(0);
                        break;

                    case R.drawable.ic_lockscreen_camera:
                        mActivityLauncher.launchCamera(null, null);
                        mCallback.userActivity(0);
                        break;

                    case R.drawable.ic_lockscreen_unlock_phantom:
                    case R.drawable.ic_lockscreen_unlock:
                        mCallback.userActivity(0);
                        mCallback.dismiss(false);
                        break;
                }
                // SPRD: Modify 20131230 Spreadst of Bug 262132 modify UUI lockscreen policy
                KeyguardViewManager.setShowUUILock(false);
            } else {
                switch (resId) {
                    case R.drawable.ic_action_assist_generic:
                        Intent assistIntent =
                                ((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                                        .getAssistIntent(mContext, true, UserHandle.USER_CURRENT);
                        if (assistIntent != null) {
                            mActivityLauncher.launchActivity(assistIntent, false, true, null, null);
                        } else {
                            Log.w(TAG, "Failed to get intent for assist activity");
                        }
                        mCallback.userActivity(0);
                        break;

                    case R.drawable.ic_lockscreen_camera:
                        mActivityLauncher.launchCamera(null, null);
                        mCallback.userActivity(0);
                        break;

                    case R.drawable.ic_lockscreen_unlock_phantom:
                    case R.drawable.ic_lockscreen_unlock:
                        mCallback.userActivity(0);
                        mCallback.dismiss(false);
                        break;
                }
            }
            /* @} */
            mGlowPadView.setOnTriggerListener(null);
        }

        public void onReleased(View v, int handle) {
            if (!mIsBouncing) {
                doTransition(mFadeView, 1.0f);
            }
        }

        public void onGrabbed(View v, int handle) {
            mCallback.userActivity(0);
            doTransition(mFadeView, 0.0f);
        }

        public void onGrabbedStateChange(View v, int handle) {

        }

        public void onFinishFinalAnimation() {

        }

    };

    KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() {

        @Override
        public void onDevicePolicyManagerStateChanged() {
            updateTargets();
        }

        @Override
        // SPRD: Modify 20131127 Spreadst of Bug 244716 keyguard support multi-card
        public void onSimStateChanged(State simState, int subscription) {
            updateTargets();
        }

        /* SPRD: Modify 20140120 Spreadst of Bug 266285 do not show unread message @{ */
        void onMessageCountChanged(int messageCount) {
            Log.d(TAG, "onMessageCountChanged messageCount is " + messageCount);
            updateUnreadMessageInfo(messageCount);
        };

        void onMissedCallCountChanged(int count) {
            Log.d(TAG, "onMissedCallCountChanged messageCount is " + count);
            updateMissedCallInfo(count);
        };
        /* @} */
    };

    private final KeyguardActivityLauncher mActivityLauncher = new KeyguardActivityLauncher() {

        @Override
        KeyguardSecurityCallback getCallback() {
            return mCallback;
        }

        @Override
        LockPatternUtils getLockPatternUtils() {
            return mLockPatternUtils;
        }

        @Override
        Context getContext() {
            return mContext;
        }};

    public KeyguardSelectorView(Context context) {
        this(context, null);
    }

    public KeyguardSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLockPatternUtils = new LockPatternUtils(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        /* SPRD: Modify 20130909 Spreadst of Bug 213683 original lockscreen change by UUI @{ */
        if (isUniverseSupport){
            mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view_uui);
            findViewById(R.id.glow_pad_view).setVisibility(View.GONE);
        } else {
            mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
            View glow_pad_view_uui = findViewById(R.id.glow_pad_view_uui);
            if (null != glow_pad_view_uui) {
                glow_pad_view_uui.setVisibility(View.GONE);
            }
        }
        findViewById(R.id.glow_pad_view).setVisibility(View.GONE);
        /* @} */
        mGlowPadView.setOnTriggerListener(mOnTriggerListener);
        updateTargets();

        mSecurityMessageDisplay = new KeyguardMessageArea.Helper(this);
        View bouncerFrameView = findViewById(R.id.keyguard_selector_view_frame);
        mBouncerFrame = bouncerFrameView.getBackground();

        /* SPRD: Modify 20140120 Spreadst of Bug 266285 do not show unread message @{ */
        mMessageCountTextView = (TextView) findViewById(R.id.messageCount);
        mMissedCallCountTextView = (TextView) findViewById(R.id.missedCallCount);
        if (mMessageCountTextView != null) {
            if (mMessageCount == 0) {
                mMessageCountTextView.setVisibility(View.GONE);
                messageCountViewsetOnClickListener(false);
            } else {
                mMessageCountTextView.setVisibility(View.VISIBLE);
                // SPRD: Modify 20140208 Spreadst of Bug 276882 click unread message and missing call can into mms and dialer
                messageCountViewsetOnClickListener(false);
            }
        }
        if (mMissedCallCountTextView != null) {
            if (mMissedCallCount == 0) {
                mMissedCallCountTextView.setVisibility(View.GONE);
                missedCallTextViewSetOnClickListener(false);
            } else {
                mMissedCallCountTextView.setVisibility(View.VISIBLE);
                // SPRD: Modify 20140208 Spreadst of Bug 276882 click unread message and missing call can into mms and dialer
                missedCallTextViewSetOnClickListener(false);
            }
        }
        /* @} */
    }

    public void setCarrierArea(View carrierArea) {
        mFadeView = carrierArea;
    }

    public boolean isTargetPresent(int resId) {
        return mGlowPadView.getTargetPosition(resId) != -1;
    }

    @Override
    public void showUsabilityHint() {
        mGlowPadView.ping();
    }

    private void updateTargets() {
        int currentUserHandle = mLockPatternUtils.getCurrentUser();
        DevicePolicyManager dpm = mLockPatternUtils.getDevicePolicyManager();
        int disabledFeatures = dpm.getKeyguardDisabledFeatures(null, currentUserHandle);
        boolean secureCameraDisabled = mLockPatternUtils.isSecure()
                && (disabledFeatures & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_CAMERA) != 0;
        boolean cameraDisabledByAdmin = dpm.getCameraDisabled(null, currentUserHandle)
                || secureCameraDisabled;
        final KeyguardUpdateMonitor monitor = KeyguardUpdateMonitor.getInstance(getContext());
        boolean disabledBySimState = monitor.isSimLocked();
        boolean cameraTargetPresent =
            isTargetPresent(R.drawable.ic_lockscreen_camera);
        boolean searchTargetPresent =
            isTargetPresent(R.drawable.ic_action_assist_generic);

        if (cameraDisabledByAdmin) {
            Log.v(TAG, "Camera disabled by Device Policy");
        } else if (disabledBySimState) {
            Log.v(TAG, "Camera disabled by Sim State");
        }
        boolean currentUserSetup = 0 != Settings.Secure.getIntForUser(
                mContext.getContentResolver(),
                Settings.Secure.USER_SETUP_COMPLETE,
                0 /*default */,
                currentUserHandle);
        boolean searchActionAvailable =
                ((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                .getAssistIntent(mContext, false, UserHandle.USER_CURRENT) != null;
        /* SPRD: Modify 20140110 Spreadst of Bug 263747 disable camrea when simlock @{
        mCameraDisabled = cameraDisabledByAdmin || disabledBySimState || !cameraTargetPresent
                || !currentUserSetup; */
        mCameraDisabled = cameraDisabledByAdmin || !cameraTargetPresent
                || !currentUserSetup;
        /* @} */
        mSearchDisabled = disabledBySimState || !searchActionAvailable || !searchTargetPresent
                || !currentUserSetup;
        updateResources();
    }

    public void updateResources() {
        int resId;
        /* SPRD: Modify 20131126 Spreadst of Bug 244515 original lockscreen change by UUI @{ */
        if (isUniverseSupport) {
            resId = R.array.lockscreen_targets_with_camera_uui;
        } else {
            resId = R.array.lockscreen_targets_with_camera;
        }
        if (mGlowPadView.getTargetResourceId() != resId) {
            mGlowPadView.setTargetResources(resId);
        }
        /* @} */
        // Update the search icon with drawable from the search .apk
        if (!mSearchDisabled) {
            Intent intent = ((SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE))
                    .getAssistIntent(mContext, false, UserHandle.USER_CURRENT);
            if (intent != null) {
                // XXX Hack. We need to substitute the icon here but haven't formalized
                // the public API. The "_google" metadata will be going away, so
                // DON'T USE IT!
                ComponentName component = intent.getComponent();
                boolean replaced = mGlowPadView.replaceTargetDrawablesIfPresent(component,
                        ASSIST_ICON_METADATA_NAME + "_google", R.drawable.ic_action_assist_generic);

                if (!replaced && !mGlowPadView.replaceTargetDrawablesIfPresent(component,
                            ASSIST_ICON_METADATA_NAME, R.drawable.ic_action_assist_generic)) {
                        Slog.w(TAG, "Couldn't grab icon from package " + component);
                }
            }
        }

        mGlowPadView.setEnableTarget(R.drawable.ic_lockscreen_camera, !mCameraDisabled);
        mGlowPadView.setEnableTarget(R.drawable.ic_action_assist_generic, !mSearchDisabled);
    }

    void doTransition(View view, float to) {
        if (mAnim != null) {
            mAnim.cancel();
        }
        mAnim = ObjectAnimator.ofFloat(view, "alpha", to);
        mAnim.start();
    }

    public void setKeyguardCallback(KeyguardSecurityCallback callback) {
        mCallback = callback;
    }

    public void setLockPatternUtils(LockPatternUtils utils) {
        mLockPatternUtils = utils;
    }

    @Override
    public void reset() {
        mGlowPadView.reset(false);
    }

    @Override
    public boolean needsInput() {
        return false;
    }

    @Override
    public void onPause() {
        KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(mUpdateCallback);
    }

    @Override
    public void onResume(int reason) {
        KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(mUpdateCallback);
/* Innova:fengchuan on: Fri, 30 May 2014 10:20:51 +0800
 * TODO: replace this line with your comment
 * set single unlockscreen
 */
 	if(Settings.System.getInt(mContext.getContentResolver(), Settings.System.TEXT_NO_OTHER_LOCK, 1) == 1){
        if (SINGLEUNLOCKSCREEN && mLockPatternUtils.isSecure()){
           mCallback.userActivity(0);
           mCallback.dismiss(false);
        }
 	}
// End of Innova:fengchuan
    }

    @Override
    public KeyguardSecurityCallback getCallback() {
        return mCallback;
    }

    @Override
    public void showBouncer(int duration) {
        mIsBouncing = true;
        KeyguardSecurityViewHelper.
                showBouncer(mSecurityMessageDisplay, mFadeView, mBouncerFrame, duration);
    }

    @Override
    public void hideBouncer(int duration) {
        mIsBouncing = false;
        KeyguardSecurityViewHelper.
                hideBouncer(mSecurityMessageDisplay, mFadeView, mBouncerFrame, duration);
    }

    /* SPRD: Modify 20140120 Spreadst of Bug 266285 do not show unread message @{ */
    /**
     * Update Unread Message info for Lock Screen
     *
     * @param count The count of unread message
     */
    void updateUnreadMessageInfo(int messagecount) {
        if (mMissedCallCountTextView != null) {
            if (messagecount > 0) {
                // SPRD: Modify 20140208 Spreadst of Bug 276882 click unread message and missing call can into mms and dialer
                messageCountViewsetOnClickListener(false);
                mMessageCount = messagecount;
                mMessageCountTextView.setVisibility(View.VISIBLE);
                /* SPRD: Modify 20140225 Spreadst of Bug 282069 display message when message count is 3 @{ */
                mMessageCountTextView.setText(getContext().getString(
                        ((messagecount > 1) ? R.string.unread_messages : R.string.unread_message),
                        mMessageCount));
                /* @} */
            } else {
                mMessageCountTextView.setVisibility(View.GONE);
                messageCountViewsetOnClickListener(false);
            }
        }
    }

    /**
     * Update Missed call info for Lock Screen
     *
     * @param count The count of missed call
     */
    private void updateMissedCallInfo(int count) {
        if (mMissedCallCountTextView != null) {
            if (count > 0) {
                // SPRD: Modify 20140208 Spreadst of Bug 276882 click unread message and missing call can into mms and dialer
                missedCallTextViewSetOnClickListener(false);
                mMissedCallCount = count;
                mMissedCallCountTextView.setVisibility(View.VISIBLE);
                mMissedCallCountTextView.setText(getContext().getString(R.string.missed_call,
                        mMissedCallCount));
            } else {
                mMissedCallCountTextView.setVisibility(View.GONE);
                missedCallTextViewSetOnClickListener(false);
            }
        }
    }

    private void missedCallTextViewSetOnClickListener(boolean enable) {
        if (mMissedCallCountTextView != null) {
            if (enable) {
                mMissedCallCountTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL_BUTTON);
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        if (callIntent != null) {
                            mActivityLauncher.launchActivity(callIntent, false, true, null, null);
                        } else {
                            Log.w(TAG, "Failed to get intent for assist activity");
                        }
                        mCallback.userActivity(0);
                    }
                });
            } else {
                mMissedCallCountTextView.setOnClickListener(null);
            }
        }
    }

    private void messageCountViewsetOnClickListener(boolean enable) {
        if (mMessageCountTextView != null) {
            if (enable) {
                mMessageCountTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMessageCount == 0) {
                            return;
                        }
                        Intent smsIntent = new Intent(Intent.ACTION_MAIN);
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        if (smsIntent != null) {
                            mActivityLauncher.launchActivity(smsIntent, false, true, null, null);
                        } else {
                            Log.w(TAG, "Failed to get intent for assist activity");
                        }
                        mCallback.userActivity(0);
                    }
                });
            } else {
                mMessageCountTextView.setOnClickListener(null);
            }
        }
    }
    /* @} */
}
