
package com.spreadst.cphonelockscreen;

import android.view.View;

public class MyViewMagnifier extends ViewMagnifier {

    MyViewMagnifier(View hostView) {
        super(hostView);
    }

    void show(int screenX, int screenY, int realScreenX, int realScreenY) {
        show(screenX, screenY, realScreenX, realScreenY, true);
    }

    void show(int screenX, int screenY, int realScreenX, int realScreenY, boolean animate) {
        if (!mShowing) {
            this.mScreenX = screenX;
            this.mScreenY = screenY;
            calculatePosition(realScreenX, realScreenY);
            calculateDrawingPosition(screenX, screenY);
            showInternal(animate);
        }
    }

    void move(int screenX, int screenY, int realScreenX, int realScreenY) {
        if (mShowing) {
            calculatePosition(realScreenX, realScreenY);
            if (mScreenX != screenX || mScreenY != screenY) {
                this.mScreenX = screenX;
                this.mScreenY = screenY;
                calculateDrawingPosition(screenX, screenY);
            }
            moveInternal();
        }
    }

}
