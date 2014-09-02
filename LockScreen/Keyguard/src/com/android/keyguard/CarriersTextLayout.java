/** Created by Spreadst */
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

import android.content.Context;
//import android.sim.SimManager;
import android.os.Debug;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.String;

import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.IccCardConstants.State;
import com.android.internal.widget.LockPatternUtils;

public class CarriersTextLayout extends LinearLayout {
    private static CharSequence mSeparator;

    // private LockPatternUtils mLockPatternUtils;
    private CharSequence[] mPlmn;
    private CharSequence[] mSpn;
    private State[] mSimState;
    private TextView[] mCarrierViews;
    private CharSequence[] mCarrierTexts;
	// private SimManager simManager;

    private static final boolean DEBUG = Debug.isDebug();
    private static final String TAG = "CarriersTextLayout";

    private KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() {

        @Override
        public void onRefreshCarrierInfo(CharSequence plmn, CharSequence spn, int subscription) {
            if (DEBUG)
                Log.d(TAG, "onRefreshCarrierInfo implements: plmn = " + plmn
                        + ", spn =" + spn + ", subscription=" + subscription);
            mPlmn[subscription] = plmn;
            mSpn[subscription] = spn;
            updateCarrierText(mSimState[subscription], mPlmn[subscription], mSpn[subscription], subscription);
        }

        @Override
        public void onSimStateChanged(IccCardConstants.State simState, int subscription) {
            if (DEBUG)
                Log.d(TAG, "onSimStateChanged implements: simState = "
                        + simState + ", subscription=" + subscription);
            mSimState[subscription] = simState;
            updateCarrierText(mSimState[subscription], mPlmn[subscription], mSpn[subscription], subscription);
        }
    };
    /**
     * The status of this lock screen. Primarily used for widgets on LockScreen.
     */
    private static enum StatusMode {
        Normal, // Normal case (sim card present, it's not locked)
        NetworkLocked, // SIM card is 'network locked'.
        SimMissing, // SIM card is missing.
        SimMissingLocked, // SIM card is missing, and device isn't provisioned; don't allow access
        SimPukLocked, // SIM card is PUK locked because SIM entered wrong too many times
        SimLocked, // SIM card is currently locked
        SimPermDisabled, // SIM card is permanently disabled due to PUK unlock failure
        SimNotReady, // SIM is not ready yet. May never be on devices w/o a SIM.
        /** SPRD: add for sim lock @{ */
        NetworkSubsetLocked,//network subset lock
        ServiceProviderLocked,//Service Provider lock
        CorporateLocked,//Corporate lock
        /** @} */
        SimPinLocked;
    }

    public CarriersTextLayout(Context context) {
        this(context, null);
    }

    public CarriersTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // mLockPatternUtils = new LockPatternUtils(mContext);
    }

    protected void updateCarrierText(State simState, CharSequence plmn, CharSequence spn,
            int subscription) {
        mCarrierTexts[subscription] = getCarrierTextForSimState(simState, plmn, spn);
        int phoneCount = TelephonyManager.getPhoneCount();
        /* SPRD: Modify 20131226 Spreadst of Bug 258891 lockscreen carrier info show wrong @{ */
        if (phoneCount > 1) {
            boolean isAllCardsAbsent = true;
            for (int i = 0; i < phoneCount; i++) {
                isAllCardsAbsent = isAllCardsAbsent && (mSimState[i] == IccCardConstants.State.ABSENT);
            }
            if (isAllCardsAbsent) {
                mCarrierViews[0].setText(mCarrierTexts[0]);
                for (int j = 1; j < phoneCount; j++) {
                    mCarrierViews[j].setVisibility(View.INVISIBLE);
                }
            } else {
                for (int k = 0; k < phoneCount; k++) {
/* Innova:fengchuan on: Fri, 11 Jul 2014 16:04:34 +0800
                        if(str != null && str.contains("3G")){
                            str.replace("3G", "");
                        }
 */
                    if(Build.DEVICE.equals("w138_txt_091") || Build.DEVICE.equals("w138_txt_092") || Build.DEVICE.equals("w138_dht_091")){
                        String str = "";
                        if(mCarrierTexts[k] != null){
                            str = mCarrierTexts[k].toString();
                            int length = str.length();
                            if(str.endsWith("3G")){
                                str = str.substring(0,length - 2);
                            }
                        }
                        mCarrierViews[k].setText(str);
                    }else {
                        mCarrierViews[k].setText(mCarrierTexts[k]);
                    }
// End of Innova:fengchuan
                    mCarrierViews[k].setVisibility(View.VISIBLE);
                }
            }
        } else {
            mCarrierViews[0].setText(mCarrierTexts[0]);
        }
        /* @} */
        /* SPRD: modify for bug261529 by Spreadst @{ */
        int[] carrierAry = new int[phoneCount];
        boolean allAbsent = true;
        for (int i = 0; i < phoneCount; i++) {
            if (mSimState[i] == IccCardConstants.State.ABSENT) {
                carrierAry[i] = -1;
            } else {
                carrierAry[i] = i;
                allAbsent = false;
            }
        }
        for (int i = 0; i < carrierAry.length; i++) {
            if (allAbsent) {
                mCarrierViews[0].setVisibility(View.VISIBLE);
                mCarrierViews[0].setText(mCarrierTexts[0]);
                mCarrierViews[i].setVisibility(View.INVISIBLE);
            } else {
                if ("cucc".equals(SystemProperties.get("ro.operator", ""))) {
                    if (carrierAry[i] == -1) {
                        mCarrierViews[i].setVisibility(View.GONE);
                    } else {
                        mCarrierViews[i].setText(mCarrierTexts[i]);
                    }
                }
            }
        }
        /* @} */
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSeparator = getResources().getString(R.string.kg_text_message_separator);
//        simManager = SimManager.get(mContext);
        int phoneCount = TelephonyManager.getPhoneCount();
        mPlmn = new CharSequence[phoneCount];
        mSpn = new CharSequence[phoneCount];
        mSimState = new State[phoneCount];
        mCarrierViews = new TextView[phoneCount];
        mCarrierTexts = new CharSequence[phoneCount];
        for (int i = 0; i < phoneCount; i++) {
            TextView carrier = new TextView(getContext());
            carrier.setGravity(Gravity.CENTER_HORIZONTAL);
            carrier.setSingleLine();
/* Innova:fengchuan on: Wed, 09 Jul 2014 17:57:06 +0800
 * TODO: replace this line with your comment
            carrier.setTextSize(13);
 */
            if(Build.DEVICE.equals("w138_mbt_051")){
                carrier.setTextSize(15);
            }else {
                carrier.setTextSize(13);
            }
// End of Innova: fengchuan
            addView(carrier);
            mCarrierViews[i] = carrier;
        }
        setSelected(true); // Allow marquee to work.
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor.getInstance(mContext).registerCallback(mCallback);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor.getInstance(mContext).removeCallback(mCallback);
    }

    /**
     * Top-level function for creating carrier text. Makes text based on simState, PLMN
     * and SPN as well as device capabilities, such as being emergency call capable.
     *
     * @param simState
     * @param plmn
     * @param spn
     * @return
     */
    private CharSequence getCarrierTextForSimState(IccCardConstants.State simState,
            CharSequence plmn, CharSequence spn) {
        CharSequence carrierText = null;
        StatusMode status = getStatusForIccState(simState);
        /* SPRD: Modify 20140116 Spreadst of Bug 269704 carrier info is different statusbar @{ */
        switch (status) {
            case Normal:
                carrierText = makeCarierString(plmn, spn);
                break;

            case SimNotReady:
                /* SPRD: Modify 20130905 Spreadst of 212023 carrier show wrong when airplane mode
                carrierText = null; // nothing to display yet. @{ */
                carrierText = makeCarierString(plmn, spn);
                break;

            case NetworkLocked:
                carrierText = makeCarierString(plmn,
                        mContext.getText(R.string.keyguard_network_locked_message));
                break;

            case SimMissing:
                // Shows "No SIM card | Emergency calls only" on devices that are voice-capable.
                // This depends on mPlmn containing the text "Emergency calls only" when the radio
                // has some connectivity. Otherwise, it should be null or empty and just show
                // "No SIM card"
                /* SPRD: Modify 20140114 Spreadst of Bug 263732 do not show emergency calls info @{
                carrierText =  makeCarrierStringOnEmergencyCapable(
                        getContext().getText(R.string.keyguard_missing_sim_message_short),
                        plmn); */
                if("cucc".equals(SystemProperties.get("ro.operator", ""))){
                    carrierText =  makeCarierString("",
                            getContext().getText(R.string.keyguard_missing_sim_message_short));
                } else {
                    carrierText =  makeCarierString(plmn,
                            getContext().getText(R.string.keyguard_missing_sim_message_short));
                }
                /* @} */
                break;

            case SimPermDisabled:
                carrierText = getContext().getText(
                        R.string.keyguard_permanent_disabled_sim_message_short);
                break;

            case SimMissingLocked:
                carrierText =  makeCarierString(plmn,
                        getContext().getText(R.string.keyguard_missing_sim_message_short));
                break;

            case SimLocked:
                carrierText = makeCarierString(plmn, getContext().getText(R.string.keyguard_sim_locked_message));
                break;
                /** SPRD: add for sim lock @{ */
            case NetworkSubsetLocked:
                carrierText = makeCarierString(plmn, getContext().getText(com.android.internal.R.string.lockscreen_sim_nws_locked_mssage));
                break;
            case ServiceProviderLocked:
                carrierText = makeCarierString(plmn, getContext().getText(com.android.internal.R.string.lockscreen_sim_sp_locked_mssage));
                break;
            case CorporateLocked:
                carrierText = makeCarierString(plmn, getContext().getText(com.android.internal.R.string.lockscreen_sim_corporate_locked_mssage));
                break;
                /** @} */
            case SimPinLocked:
                carrierText = makeCarierString(plmn,
                        getContext().getText(com.android.internal.R.string.lockscreen_sim_pin_locked_message));
                break;
            case SimPukLocked:
                carrierText = makeCarierString(plmn, getContext()
                        .getText(R.string.keyguard_sim_puk_locked_message));
            break;
        }

        return carrierText;
    }

    /*
     * Add emergencyCallMessage to carrier string only if phone supports emergency calls.
     */
    /* private CharSequence makeCarrierStringOnEmergencyCapable(
            CharSequence simMessage, CharSequence emergencyCallMessage) {
        if (mLockPatternUtils.isEmergencyCallCapable()) {
            if ("cucc".equals(SystemProperties.get("ro.operator", ""))) {
                return concatenateForCUCC(simMessage, emergencyCallMessage);
            } else {
                return concatenate(simMessage, emergencyCallMessage);
            }
        }
        return simMessage;
    } */
    /* @} */

    /**
     * Determine the current status of the lock screen given the SIM state and other stuff.
     */
    private StatusMode getStatusForIccState(IccCardConstants.State simState) {
        // Since reading the SIM may take a while, we assume it is present until told otherwise.
        if (simState == null) {
            return StatusMode.Normal;
        }

        final boolean missingAndNotProvisioned =
                !KeyguardUpdateMonitor.getInstance(mContext).isDeviceProvisioned()
                && (simState == IccCardConstants.State.ABSENT ||
                        simState == IccCardConstants.State.PERM_DISABLED);

        // Assume we're NETWORK_LOCKED if not provisioned
        simState = missingAndNotProvisioned ? IccCardConstants.State.NETWORK_LOCKED : simState;
        switch (simState) {
            case ABSENT:
                return StatusMode.SimMissing;
            case NETWORK_LOCKED:
                return StatusMode.SimMissingLocked;
            case NOT_READY:
                return StatusMode.SimNotReady;
            case PIN_REQUIRED:
                return StatusMode.SimLocked;
            case PUK_REQUIRED:
                return StatusMode.SimPukLocked;
            case READY:
                return StatusMode.Normal;
            case PERM_DISABLED:
                return StatusMode.SimPermDisabled;
            /** SPRD: add for sim lock @{ */
            case NETWORK_SUBSET_LOCKED:
                return StatusMode.NetworkSubsetLocked;
            case SERVICE_PROVIDER_LOCKED:
                return StatusMode.ServiceProviderLocked;
            case CORPORATE_LOCKED:
                return StatusMode.CorporateLocked;
            /** @} */
            case UNKNOWN:
                return StatusMode.SimMissing;
        }
        return StatusMode.SimMissing;
    }

    private static CharSequence concatenate(CharSequence plmn, CharSequence spn) {
        final boolean plmnValid = !TextUtils.isEmpty(plmn);
        final boolean spnValid = !TextUtils.isEmpty(spn);
        if (plmnValid && spnValid) {
            return new StringBuilder().append(plmn).append(mSeparator).append(spn).toString();
        } else if (plmnValid) {
            return plmn;
        } else if (spnValid) {
            return spn;
        } else {
            return "";
        }
    }

    private CharSequence getCarrierHelpTextForSimState(IccCardConstants.State simState,
            String plmn, String spn) {
        int carrierHelpTextId = 0;
        StatusMode status = getStatusForIccState(simState);
        switch (status) {
            case NetworkLocked:
                carrierHelpTextId = R.string.keyguard_instructions_when_pattern_disabled;
                break;

            case SimMissing:
                carrierHelpTextId = R.string.keyguard_missing_sim_instructions_long;
                break;

            case SimPermDisabled:
                carrierHelpTextId = R.string.keyguard_permanent_disabled_sim_instructions;
                break;

            case SimMissingLocked:
                carrierHelpTextId = R.string.keyguard_missing_sim_instructions;
                break;

            case Normal:
            case SimLocked:
            case SimPukLocked:
                break;
        }

        return mContext.getText(carrierHelpTextId);
    }

    /* SPRD: Modify 20140114 Spreadst of Bug 263732 do not show emergency calls info @{ */
    private static CharSequence concatenateForCUCC(CharSequence plmn, CharSequence spn) {
        final boolean plmnValid = !TextUtils.isEmpty(plmn);
        final boolean spnValid = !TextUtils.isEmpty(spn);
        if (plmnValid) {
            return plmn ;
        } else if (spnValid) {
            return spn;
        } else {
            return "";
        }
    }
    /* @} */

    /* SPRD: Modify 20140116 Spreadst of Bug 269704 carrier info is different statusbar @{ */
    /**
     * Performs concentenation of PLMN/SPN
     * @param plmn
     * @param spn
     * @return
     */
    private static CharSequence makeCarierString(CharSequence plmn, CharSequence spn) {
        if("cucc".equals(SystemProperties.get("ro.operator", ""))){
            return concatenateForCUCC(plmn, spn);
        }else{
            return concatenate(plmn, spn);
        }
    }
    /* @} */
}
