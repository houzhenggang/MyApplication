package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacerings;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceRings extends WatchFace {

    public WatchFaceRings() {
    }

    public WatchFaceView onReadyForContent() {
        RingsClockView ringsclockview = new RingsClockView(this);
        setContentView(ringsclockview);
        return ringsclockview;
    }
}
