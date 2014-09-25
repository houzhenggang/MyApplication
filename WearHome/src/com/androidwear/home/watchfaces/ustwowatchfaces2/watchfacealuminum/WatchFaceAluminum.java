package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacealuminum;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceAluminum extends WatchFace {

    public WatchFaceAluminum() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        AluminumClockView aluminumclockview = new AluminumClockView(this);
        setContentView(aluminumclockview);
        return aluminumclockview;
    }
}
