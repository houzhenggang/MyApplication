package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacegrid;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceGrid extends WatchFace {

    public WatchFaceGrid() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        Object obj;
        if (isRound()) {
            obj = new GridCircleClockView(this);
        } else {
            obj = new GridSquareAClockView(this);
        }
        setContentView(((android.view.View) (obj)));
        return ((WatchFaceView) (obj));
    }
}
