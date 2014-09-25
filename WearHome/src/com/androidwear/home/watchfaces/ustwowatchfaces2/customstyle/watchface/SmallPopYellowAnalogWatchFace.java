package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.watchface;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.CompoundAnalogClock;

public class SmallPopYellowAnalogWatchFace extends Activity {
    private ImageView mBackgrounImage;
    private CompoundAnalogClock mClock;
    private boolean mRegistered;
    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (!(paramIntent.hasExtra("ambient_mode")))
                return;
            SmallPopYellowAnalogWatchFace.this.onAmbientModeChanged(paramIntent
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
        Resources localResources = getResources();
        if (paramBoolean) {
            Drawable localDrawable2 = localResources
                    .getDrawable(R.drawable.watch_modi_black_sub_bg);
            if (localDrawable2 != null) {
                this.mBackgrounImage.setImageDrawable(localDrawable2);
            }
        } else {
            Drawable localDrawable1 = localResources
                    .getDrawable(R.drawable.watch_modi_yellow_sub_bg);
            if (localDrawable1 != null) {
                this.mBackgrounImage.setImageDrawable(localDrawable1);
            }
        }
        this.mBackgrounImage.invalidate();
        this.mClock.setAmbient(paramBoolean);

    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_small_pop_yellow_analog_watch_face);
        this.mClock = ((CompoundAnalogClock) findViewById(R.id.analogClock));
        this.mBackgrounImage = ((ImageView) findViewById(R.id.backgroundimage));
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
