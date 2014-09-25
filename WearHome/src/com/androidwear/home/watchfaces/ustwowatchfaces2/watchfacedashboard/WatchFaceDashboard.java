package com.androidwear.home.watchfaces.ustwowatchfaces2.watchfacedashboard;

import android.os.Bundle;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFace;
import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class WatchFaceDashboard extends WatchFace {

    public WatchFaceDashboard() {
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public WatchFaceView onReadyForContent() {
        DashboardClockView dashboardclockview = new DashboardClockView(this);
        setContentView(dashboardclockview);
        return dashboardclockview;
    }
}
