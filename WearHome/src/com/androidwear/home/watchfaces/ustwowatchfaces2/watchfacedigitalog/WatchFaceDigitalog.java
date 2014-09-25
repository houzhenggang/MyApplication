package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacedigitalog;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceDigitalog extends WatchFace {

    public WatchFaceDigitalog() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        DigitalogClockView digitalogclockview = new DigitalogClockView(this);
        setContentView(digitalogclockview);
        return digitalogclockview;
    }
}
