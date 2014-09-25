package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfaceaviator;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceAviator extends WatchFace {

    public WatchFaceAviator() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        AviatorClockView aviatorclockview = new AviatorClockView(this);
        setContentView(aviatorclockview);
        return aviatorclockview;
    }
}
