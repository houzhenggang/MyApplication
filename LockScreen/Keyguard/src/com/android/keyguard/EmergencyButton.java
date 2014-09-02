/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;

/**
 * This class implements a smart emergency button that updates itself based
 * on telephony state.  When the phone is idle, it is an emergency call button.
 * When there's a call in progress, it presents an appropriate message and
 * allows the user to return to the call.
 */
public class EmergencyButton extends Button {

    private static final int EMERGENCY_CALL_TIMEOUT = 10000; // screen timeout after starting e.d.
    private static final String ACTION_EMERGENCY_DIAL = "com.android.phone.EmergencyDialer.DIAL";

    /* SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service @{ */
    private static final String TAG = "EmergencyButtons";
    private static final boolean DEBUG = /* Debug.isDebug() */true;
    private int numPhones;
    private TelephonyManager[] mTelephonyManager;
    private PhoneStateListener[] mPhoneStateListener;
    protected int mPhoneState;
    ServiceState[] mServiceState;
    /* @} */

    KeyguardUpdateMonitorCallback mInfoCallback = new KeyguardUpdateMonitorCallback() {

        @Override
        // SPRD: Modify 20131127 Spreadst of Bug 244716 keyguard support multi-card
        public void onSimStateChanged(State simState,int subscription) {
            if (DEBUG) Log.d(TAG, "onSimStateChanged , simState = " + simState + " , subscription = " + subscription);
            int phoneState = KeyguardUpdateMonitor.getInstance(mContext).getPhoneState();
            updateEmergencyCallButton(simState, phoneState);
        }

        void onPhoneStateChanged(int phoneState) {
            if (DEBUG) Log.d(TAG, "onPhoneStateChanged , phoneState = " + phoneState);
            // SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service
            mPhoneState = phoneState;
            State simState = KeyguardUpdateMonitor.getInstance(mContext).getSimState();
            updateEmergencyCallButton(simState, phoneState);
        };
    };
    private LockPatternUtils mLockPatternUtils;
    private PowerManager mPowerManager;

    public EmergencyButton(Context context) {
        this(context, null);
    }

    public EmergencyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor.getInstance(mContext).registerCallback(mInfoCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor.getInstance(mContext).removeCallback(mInfoCallback);
        /* SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service @{ */
        for (int i = 0; i < numPhones; i++) {
            ((TelephonyManager) getContext().getSystemService(TelephonyManager.getServiceName(Context.TELEPHONY_SERVICE,i))).listen(
                    mPhoneStateListener[i],
                    PhoneStateListener.LISTEN_NONE);
        }
        /* @} */
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLockPatternUtils = new LockPatternUtils(mContext);
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        /* SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service @{ */
        numPhones = TelephonyManager.getPhoneCount();
        mServiceState = new ServiceState[numPhones];
        mTelephonyManager = new TelephonyManager[numPhones];
        mPhoneStateListener = new PhoneStateListener[numPhones];
        for (int i = 0; i < numPhones; i++) {
            mTelephonyManager[i] = (TelephonyManager) getContext().getSystemService(TelephonyManager.getServiceName(
                            Context.TELEPHONY_SERVICE, i));
            mPhoneStateListener[i] = getPhoneStateListener(i);
            mTelephonyManager[i].listen(mPhoneStateListener[i], PhoneStateListener.LISTEN_SERVICE_STATE);
        }
        if (!canEmergencyCall()) {
            setClickable(false);
            setText(R.string.kg_emergency_call_no_service);
        } else {
            setClickable(true);
            setText(R.string.kg_emergency_call_label);
        }
        /* @} */
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                takeEmergencyCallAction();
            }
        });
        int phoneState = KeyguardUpdateMonitor.getInstance(mContext).getPhoneState();
        State simState = KeyguardUpdateMonitor.getInstance(mContext).getSimState();
        updateEmergencyCallButton(simState, phoneState);
    }

    /**
     * Shows the emergency dialer or returns the user to the existing call.
     */
    public void takeEmergencyCallAction() {
        // TODO: implement a shorter timeout once new PowerManager API is ready.
        // should be the equivalent to the old userActivity(EMERGENCY_CALL_TIMEOUT)
        mPowerManager.userActivity(SystemClock.uptimeMillis(), true);
        if (TelephonyManager.getDefault().getCallState()
                == TelephonyManager.CALL_STATE_OFFHOOK) {
            mLockPatternUtils.resumeCall();
        } else {
            final boolean bypassHandler = true;
            KeyguardUpdateMonitor.getInstance(mContext).reportEmergencyCallAction(bypassHandler);
            Intent intent = new Intent(ACTION_EMERGENCY_DIAL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getContext().startActivityAsUser(intent,
                    new UserHandle(mLockPatternUtils.getCurrentUser()));
        }
    }

    private void updateEmergencyCallButton(State simState, int phoneState) {
        /* SPRD: Modify 20131126 Spreadst of Bug 244385 can not emergency call @{
        boolean enabled = false;
        if (phoneState == TelephonyManager.CALL_STATE_OFFHOOK) {
            enabled = true; // always show "return to call" if phone is off-hook
        } else if (mLockPatternUtils.isEmergencyCallCapable()) {
            boolean simLocked = KeyguardUpdateMonitor.getInstance(mContext).isSimLocked();
            if (simLocked) {
                // Some countries can't handle emergency calls while SIM is locked.
                enabled = mLockPatternUtils.isEmergencyCallEnabledWhileSimLocked();
            } else {
                // True if we need to show a secure screen (pin/pattern/SIM pin/SIM puk);
                // hides emergency button on "Slide" screen if device is not secure.
                enabled = mLockPatternUtils.isSecure();
            }
        } */
        mLockPatternUtils.updateEmergencyCallButtonState(this, phoneState, true, false);
        /* @} */
        /* SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service @{ */
        if (!canEmergencyCall()) {
            setClickable(false);
            setText(R.string.kg_emergency_call_no_service);
        } else {
            setClickable(true);
        }
        /* @} */
        if ("cucc".equals(SystemProperties.get("ro.operator", ""))) {
            boolean hasIccCard = false;
            for (int i = 0; i < TelephonyManager.getPhoneCount(); i++) {
                hasIccCard |= mTelephonyManager[i].hasIccCard();
            }
            if (!hasIccCard) {
                setVisibility(View.INVISIBLE);
            }
        }
    }

    /* SPRD: Modify 20131127 Spreadst of Bug 244769 lockscreen show emergency call when no card no service @{ */
    private PhoneStateListener getPhoneStateListener(final int phoneId) {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState state) {
                if (DEBUG)
                    Log.v(TAG, "onServiceStateChanged(), serviceState = " + state);
                if (state != null) {
                    mServiceState[phoneId] = state;
                }
                State simState = KeyguardUpdateMonitor.getInstance(mContext).getSimState();
                updateEmergencyCallButton(simState, mPhoneState);
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

    boolean canEmergencyCall(){
        boolean isEmergencyOnly = false;
        boolean hasService = false;
        for (int i = 0; i < this.numPhones; i++) {
            if (mServiceState[i] != null) {
                isEmergencyOnly = isEmergencyOnly ? true : mServiceState[i].isEmergencyOnly();
                hasService = hasService(i);
                Log.d(TAG, "canEmergencyCall i = "+ i +"| isEmergencyOnly = " + isEmergencyOnly + "|hasService = " + hasService);
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
    /* @} */
}
