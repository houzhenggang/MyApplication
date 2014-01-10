package com.mephone.hellohwlockscreen;

import android.widget.ImageView;

public class MyViewMagnifier extends ViewMagnifier {

    MyViewMagnifier(ImageView hostView) {
        super(hostView);
    }

    void show(int screenX, int screenY, int realScreenX, int realScreenY) {
        show(screenX, screenY, realScreenX, realScreenY, true);
    }

    void show(int screenX, int screenY, int realScreenX, int realScreenY, boolean animate) {
        if (!mShowing) {
            this.mScreenX = screenX;
            this.mScreenY = screenY;
            // calculate the position and drawing translation of magnifier.
            calculatePosition(realScreenX, realScreenY);
            calculateDrawingPosition(screenX, screenY);
            // show
            showInternal(animate);
        }
    }

    void move(int screenX, int screenY, int realScreenX, int realScreenY) {
        if (mShowing) {
            // calculate the position, then reposition the magnifier.
            calculatePosition(realScreenX, realScreenY);
            // calculate the drawing translation, then redraw the content of magnifier.
            if (mScreenX != screenX || mScreenY != screenY) {
                this.mScreenX = screenX;
                this.mScreenY = screenY;
                calculateDrawingPosition(screenX, screenY);
            }
            // move
            moveInternal();
        }
    }

}
}
