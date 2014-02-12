
package com.mephone.hellohwlockscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * The helper class which makes views support magnifier easily.
 */
public class ViewMagnifier {

    private static final int TOLERANCE_TOUCH = 10;

    private View mHostView;
    private Context mContext;

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mLayoutParams = null;
    private MagnifierView mMagnifierView = null;

    protected int mWidth;
    protected int mHeight;
    protected float mScale = 1.0f;
    protected int mRadius = MIN_RADIUS;
    private int mMaxMoveLenght = 0;
    private static final int MAX_RADIUS = 650;
    private static final int MIN_RADIUS = 40;
    private static final float MAX_SCALE = 5.0f;
    private static final float MIN_SCALE = 1.0f;

    protected int mScreenX;
    protected int mScreenY;
    protected int mMoveX = 0;
    protected int mMoveY = 0;
    protected int mMoveLenght = 0;
    protected boolean mShowBottomView = true;

    protected boolean mShowing = false;

    private int mPositionX = 0;
    private int mPositionY = 0;
    private float mDrawingX = 0;
    private float mDrawingY = 0;

    public ViewMagnifier(View hostView) {
        this.mHostView = hostView;
        this.mContext = mHostView.getContext();

        this.mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int displayWidth = mWindowManager.getDefaultDisplay().getWidth();
        int displayHeight = mWindowManager.getDefaultDisplay().getHeight();
        int x = Math.min(displayWidth, displayHeight);
        this.mWidth = displayWidth;
        this.mHeight = displayHeight;
        this.mMagnifierView = new MagnifierView(mContext);
        mMaxMoveLenght = (int) Math.sqrt(mWidth * mWidth + mHeight * mHeight);
    }

    /**
     * @return The width of magnifier.
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Set the width of magnifier.
     * 
     * @param width
     */
    public void setWidth(int width) {
        this.mWidth = width;
    }

    /**
     * @return The height of magnifier.
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Set the height of magnifier.
     * 
     * @param height
     */
    public void setHeight(int height) {
        this.mHeight = height;
    }

    /**
     * @return The scale of magnifier content.
     */
    public float getScale() {
        return mScale;
    }

    /**
     * Set the scale of magnifier content.
     * 
     * @param scale
     */
    public void setScale(float scale) {
        this.mScale = scale;
    }

    /**
     * @return Whether the magnifier is showing.
     */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Show magnifier at assigned position relative to left-top of screen.
     * 
     * @param screenX
     * @param screenY
     */
    public void show(int screenX, int screenY) {
        show(screenX, screenY, true);
    }

    /**
     * Show magnifier at assigned position relative to left-top of screen.
     * 
     * @param screenX
     * @param screenY
     * @param animate Whether enable animation or not.
     */
    public void show(int screenX, int screenY, boolean animate) {
        if (!mShowing) {
            this.mScreenX = screenX;
            this.mScreenY = screenY;
            // calculate the position and drawing translation of magnifier.
            calculatePosition(screenX, screenY);
            calculateDrawingPosition(screenX, screenY);
            // show
            showInternal(animate);
        }
    }

    /**
     * Move magnifier to assigned position relative to left-top of screen.
     * 
     * @param screenX
     * @param screenY
     */
    public void move(int screenX, int screenY) {
        if (mShowing) {
            if (mScreenX == screenX && mScreenY == screenY) {
                return;
            }
            this.mScreenX = screenX;
            this.mScreenY = screenY;
            // calculate the position and drawing translation of magnifier.
            calculatePosition(screenX, screenY);
            calculateDrawingPosition(screenX, screenY);
            // move
            moveInternal();
        }
    }

    /**
     * Hide magnifier.
     */
    public void hide() {
        if (mShowing) {
            try {
                mWindowManager.removeViewImmediate(mMagnifierView);
            } finally {
                // set showing flag whether hiding view is successful.
                mShowing = false;
            }
        }
    }

    /**
     * Destroy magnifier.
     */
    public void destroy() {
        Bitmap bitmap = mMagnifierView.mMagnifierBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    protected void showInternal(boolean animate) {
        if (mLayoutParams == null) {

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.token = mHostView.getWindowToken();
            lp.x = 0;
            lp.y = 0;
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.LEFT | Gravity.TOP;
            lp.format = PixelFormat.TRANSLUCENT;
            lp.type = WindowManager.LayoutParams.TYPE_TOAST;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            lp.packageName = mContext.getPackageName();
            if (animate) {
                // lp.windowAnimations = R.style.zzz_text_magnifier_popup;
            }
            mLayoutParams = lp;
        }
        // mLayoutParams.x = 0;
        // mLayoutParams.y = 0;
        mWindowManager.addView(mMagnifierView, mLayoutParams);
        // set showing flag
        mShowing = true;
    }

    protected void moveInternal() {
        // reposition the magnifier.
        WindowManager.LayoutParams lp = mLayoutParams;
        // lp.x = 0;
        // lp.y = 0;
        mWindowManager.updateViewLayout(mMagnifierView, lp);
        // redraw the content of magnifier.
        mMagnifierView.invalidate();
    }

    protected void calculatePosition(int screenX, int screenY) {
        mPositionX = screenX - mWidth / 2;
        mPositionY = screenY - mHeight / 2;// - TOLERANCE_TOUCH;
        mPositionY = Math.max(mPositionY, -mHeight / 3);
    }

    protected void calculateDrawingPosition(int screenX, int screenY) {
        int[] location = new int[2];
        mHostView.getRootView().getLocationOnScreen(location);
        mDrawingX = screenX - location[0] - mWidth / mScale / 2f;
        mDrawingY = screenY - location[1] - mHeight / mScale / 2f;
    }

    public int calculateMoveLenght(int x, int y) {
        this.mMoveX = x;
        this.mMoveY = y;
        this.mMoveLenght = (int) Math.abs(Math.sqrt(x * x + y * y));
        return this.mMoveLenght;
    }

    public int getMoveLenght() {
        return this.mMoveLenght;
    }

    public void showBottomView(boolean show) {
        mShowBottomView = show;
    }

    protected class MagnifierView extends View {

        private Bitmap mMagnifierBitmap;
        private Bitmap mAlphaBitmap;
        private int mOffsetX;
        private int mOffsetY;

        protected MagnifierView(Context context) {
            super(context);
            init();
        }

        private void init() {
            // Drawable drawable =
            // mContext.getResources().getDrawable(R.raw.light);
            mMagnifierBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.raw.light);
            // Canvas canvas = new Canvas(mMagnifierBitmap);
            // drawable.setBounds(0, 0, mWidth, mHeight);
            // drawable.draw(canvas);
            mAlphaBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.device_temp);
            //mAlphaBitmap = ((BitmapDrawable) mHostView.getBackground()).getBitmap();
            int oldWidth = mAlphaBitmap.getWidth();
            int oldHeight = mAlphaBitmap.getHeight();
            mAlphaBitmap = zoomBitmap(mAlphaBitmap, mScale);
            mOffsetX = (mAlphaBitmap.getWidth() - oldWidth) / 2;
            mOffsetY = (mAlphaBitmap.getHeight() - oldHeight) / 2;
        }

        private Bitmap zoomBitmap(Bitmap bitmap, float scale) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return newbmp;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(mWidth, mHeight);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float tempScale = mMoveLenght * MAX_SCALE / mMaxMoveLenght + MIN_SCALE;
            mRadius = mMoveLenght * MAX_RADIUS / mMaxMoveLenght + MIN_RADIUS;
            Bitmap tempBitmap = mMagnifierBitmap;// zoomBitmap(mMagnifierBitmap,tempScale);
            canvas.drawBitmap(tempBitmap, mScreenX - tempBitmap.getWidth() / 2,
                    mScreenY - tempBitmap.getHeight() / 2,
                    null);
            if (mShowBottomView) {
                Bitmap cropBitmap = cropImage(mAlphaBitmap, mRadius, (int) (mScreenX * mScale),
                        (int) (mScreenY * mScale));
                //canvas.drawBitmap(setBitmapAlpha(cropBitmap, (int) (mScreenX * mScale),
                //        (int) (mScreenY * mScale)), -mOffsetX, -mOffsetY, null);
                int cropWight = cropBitmap.getWidth();
                int cropHeight = cropBitmap.getHeight(); 
                int cropOffsetX = mScreenX - cropWight / 2;
                int cropOffsetY = mScreenY - cropHeight / 2;
                
                int cropCenterX = cropWight / 2;
                int cropCenterY = cropHeight / 2;
                
                int retX = mScreenX - mRadius;
                int retY = mScreenY - mRadius;
                int retX2 = mScreenX + mRadius;
                int retY2 = mScreenY + mRadius;
                if (retX < 0) {
                    cropOffsetX = 0;
                    cropCenterX += retX;
                }
                if (retY < 0) {
                    cropOffsetY = 0;
                    cropCenterY += retY;
                }
                if (retY2 > mAlphaBitmap.getHeight()) {
                    cropCenterY += retY2 - mAlphaBitmap.getHeight();
                    cropOffsetY = mScreenY - mRadius;
                }
                if (retX2 > mAlphaBitmap.getWidth()) {
                    cropCenterX += retX2 - mAlphaBitmap.getWidth();
                    cropOffsetX = mScreenX - mRadius;
                }
                
                if (cropWight >= mAlphaBitmap.getWidth()) {
                    cropOffsetX = 0;
                }
                if (cropHeight >= mAlphaBitmap.getHeight()) {
                    cropOffsetY = 0;
                }
                Log.i("huanghua", "cropOffsetX:" + cropOffsetX + " cropOffsetY:" + cropOffsetY + " cropCenterY:" + cropCenterY);
                canvas.drawBitmap(setBitmapAlpha(cropBitmap, cropCenterX, cropCenterY), cropOffsetX, cropOffsetY, null);
            }
        }
    }

    private Bitmap cropImage(Bitmap bitmap, int range, int x, int y) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int nw = range * 2;
        int nh = range * 2;

        int retX = x - range;
        int retY = y - range;
        int retX2 = x + range;
        int retY2 = y + range;
        if (retX < 0) {
            retX = 0;
            nw = nw + retX;
        }
        if (retY < 0) {
            retY = 0;
            nh = nh + retY;
        }
        if (retY2 > h) {
            nh = nh - retY2 + h;
        }
        if (retX2 > w) {
            nw = nw - retX2 + w;
        }
        if (nw > w) {
            nw = w;
        }
        if (nh > h) {
            nh = h;
        }
        return Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null, false);
    }

    public Bitmap setBitmapAlpha(Bitmap sourceImg, int x, int y) {
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        int[] argb = new int[width * height];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(),
                sourceImg.getHeight());
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (0 << 24) | (argb[i] & 0x00FFFFFF);
        }
        int range = mRadius;
        for (int j = -range; j <= range; j++) {
            int y1 = y + j;
            if (y1 >= 0 && y1 < height) { // 越界判断
                for (int i = -range; i <= 0; i++) {
                    int x1 = x + i;
                    int x2 = y1 * width + x1;
                    if (x1 >= 0 && x1 < width) { // 越界判断
                        int sqrt = (int) Math.sqrt(i * i + j * j);
                        if (sqrt < range) {
                            int alpha = (range - Math.abs(sqrt)) * 250 / range;
                            if (alpha > 210) {
                                alpha = 210;
                            }
                            argb[x2] = (alpha << 24) | (argb[x2] & 0x00FFFFFF);
                        }
                    }
                    x1 = x - i;
                    x2 = y1 * width + x1;
                    if (x1 >= 0 && x1 < width) { // 越界判断
                        int sqrt = (int) Math.sqrt(i * i + j * j);
                        if (sqrt < range) {
                            int alpha = (range - Math.abs(sqrt)) * 250 / range;
                            if (alpha > 210) {
                                alpha = 210;
                            }
                            argb[x2] = (alpha << 24) | (argb[x2] & 0x00FFFFFF);
                        }
                    }
                }
            }
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(),
                Config.ARGB_8888);
        return sourceImg;
    }

}
