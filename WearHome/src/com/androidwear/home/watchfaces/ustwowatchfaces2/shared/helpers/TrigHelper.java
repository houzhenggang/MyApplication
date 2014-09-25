package com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers;

import android.graphics.PointF;

public class TrigHelper {

    public static void getPointOnCircle(PointF pointf, float f, float f1,
            PointF pointf1) {
        pointf.set((float) ((double) f * Math.cos(f1) + (double) pointf1.x),
                (float) ((double) f * Math.sin(f1) + (double) pointf1.y));
    }
}
