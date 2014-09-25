package com.androidwear.home.watchfaces.ustwowatchfaces2.shared.paint;

import android.graphics.Typeface;

public class WatchFaceTextPaint extends WatchFacePaint {

    public WatchFaceTextPaint(String s, int i,
            android.graphics.Paint.Align align) {
        setTypeface(Typeface.create(s, i));
        setTextAlign(align);
    }
}
