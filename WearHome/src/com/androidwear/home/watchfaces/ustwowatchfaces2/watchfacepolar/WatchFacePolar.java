package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacepolar;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFacePolar extends WatchFace {

    public WatchFacePolar() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        PolarClockView polarclockview = new PolarClockView(this);
        setContentView(polarclockview);
        return polarclockview;
    }
}
