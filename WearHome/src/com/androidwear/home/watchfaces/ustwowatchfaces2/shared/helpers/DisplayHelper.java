package com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.WatchFaceView;

public class DisplayHelper {

    public static void clearCanvas(Canvas canvas) {
        if (sClearPaint == null) {
            sClearPaint = new Paint();
            sClearPaint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.CLEAR));
        }
        canvas.drawPaint(sClearPaint);
    }

    public static float convertDpToPixel(DisplayMetrics displaymetrics, float f) {
        return f * ((float) displaymetrics.densityDpi / 160F);
    }

    public static float getNumberTextXOffset(int i) {
        if (mNumberTextXOffsets.length == 0) {
            mNumberTextXOffsets = new float[24];
            mNumberTextXOffsets[1] = 2.0F;
            mNumberTextXOffsets[11] = 2.0F;
            mNumberTextXOffsets[21] = 2.0F;
            mNumberTextXOffsets[22] = 1.0F;
            mNumberTextXOffsets[23] = 1.0F;
            mNumberTextXOffsets[4] = -2.0F;
            mNumberTextXOffsets[12] = -2.0F;
            mNumberTextXOffsets[13] = -2.0F;
            mNumberTextXOffsets[14] = -2.0F;
            mNumberTextXOffsets[15] = -2.0F;
            mNumberTextXOffsets[16] = -2.0F;
            mNumberTextXOffsets[17] = -2.0F;
            mNumberTextXOffsets[18] = -2.0F;
            mNumberTextXOffsets[19] = -2.0F;
        }
        if (i >= 0 && i <= 23)
            return mNumberTextXOffsets[i];
        else
            return 0.0F;
    }

    public static float getPixels(WatchFaceView watchfaceview, int i) {
        return ((float) watchfaceview.getResources().getDimensionPixelSize(i) / 160F)
                * (float) watchfaceview.getWidth();
    }

    private static float mNumberTextXOffsets[] = new float[0];
    private static Paint sClearPaint;

}
