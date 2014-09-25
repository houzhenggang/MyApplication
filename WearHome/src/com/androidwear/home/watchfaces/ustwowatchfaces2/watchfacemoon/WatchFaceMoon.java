package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacemoon;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceMoon extends WatchFace {

    public WatchFaceMoon() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        MoonClockView moonclockview = new MoonClockView(this);
        setContentView(moonclockview);
        return moonclockview;
    }
}
