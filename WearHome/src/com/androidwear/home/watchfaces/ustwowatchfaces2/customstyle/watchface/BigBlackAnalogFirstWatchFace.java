package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.watchface;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.BlackAnalogClock1;

public class BigBlackAnalogFirstWatchFace extends Activity {
    private BlackAnalogClock1 mClock;
    private boolean mRegistered;
    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (!(paramIntent.hasExtra("ambient_mode")))
                return;
            BigBlackAnalogFirstWatchFace.this.onAmbientModeChanged(paramIntent
                    .getBooleanExtra("ambient_mode", false));
        }
    };

    private void registerReceiver() {
        if (this.mRegistered)
            return;
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter
                .addAction("com.google.android.clockwork.home.action.BACKGROUND_ACTION");
        registerReceiver(this.mStateReceiver, localIntentFilter);
        this.mRegistered = true;
    }

    private void unregisterReceiver() {
        if (!(this.mRegistered))
            return;
        unregisterReceiver(this.mStateReceiver);
        this.mRegistered = false;
    }

    protected void onAmbientModeChanged(boolean paramBoolean) {
        this.mClock.setAmbient(paramBoolean);
        this.mClock.postInvalidate();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_black_analog1_watch_face);
        this.mClock = ((BlackAnalogClock1) findViewById(R.id.black_analog_clock));
        registerReceiver();
    }

    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

    protected void onPause() {
        this.mClock.activityPaused();
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        this.mClock.activityResumed();
    }
}
