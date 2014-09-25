package com.androidwear.home.watchfaces.ustwowatchfaces2.shared;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class WatchFace extends Activity {

    private final BroadcastReceiver mActionReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("ambient_mode")) {
                onAmbientModeChanged(intent.getBooleanExtra("ambient_mode",
                        false));
            }
        }
    };

    private WatchFaceView mWatchFaceView;

    public WatchFace() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mWatchFaceView = onReadyForContent();
        IntentFilter intentfilter = new IntentFilter(
                "com.google.android.clockwork.home.action.BACKGROUND_ACTION");
        registerReceiver(mActionReceiver, intentfilter);
    }

    public void onDestroy() {
        mWatchFaceView.destroy();
        unregisterReceiver(mActionReceiver);
        super.onDestroy();
    }

    public boolean isRound() {
        return false;
    }

    protected void onAmbientModeChanged(boolean flag) {
        if (mWatchFaceView != null) {
            mWatchFaceView.setAmbient(flag);
        }
    }

    protected abstract WatchFaceView onReadyForContent();

}
