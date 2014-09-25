package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacecoordinates;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceCoordinates extends WatchFace {

    public WatchFaceCoordinates() {
    }

    public WatchFaceView onReadyForContent() {
        Object obj;
        if (isRound()) {
            obj = new CoordinatesRoundClockView(this);
        } else {
            obj = new CoordinatesSquareClockView(this);
        }
        setContentView(((android.view.View) (obj)));
        return ((WatchFaceView) (obj));
    }
}
