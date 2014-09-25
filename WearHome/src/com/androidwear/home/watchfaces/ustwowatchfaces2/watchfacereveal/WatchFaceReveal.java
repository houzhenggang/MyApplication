package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacereveal;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceReveal extends WatchFace {

    public WatchFaceReveal() {
    }

    public WatchFaceView onReadyForContent() {
        RevealClockView revealclockview = new RevealClockView(this);
        setContentView(revealclockview);
        return revealclockview;
    }
}
