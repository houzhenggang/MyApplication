
package com.spreadst.cphonelockscreen;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

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
    private static final int MAX_RADIUS = 400;
    private static final int MIN_RADIUS = 40;
    private static final float MAX_SCALE = 5.0f;
    private static final float MIN_SCALE = 1.0f;

    protected int mScreenX;
    protected int mScreenY;
    protected int mMoveX = 0;
    protected int mMoveY = 0;
    protected int mMoveLenght = 0;
    protected boolean mShowBottomView = true;
    protected static final String PACKAGE_NAME = "com.spreadst.cphonelockscreen";

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
        Bitmap bitmap2 = mMagnifierView.mAlphaBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            bitmap2 = null;
        }
        mMagnifierView.mAlphaARG = null;
        System.gc();
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
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            lp.privateFlags |=
                WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED;
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
        lp.privateFlags |=
            WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED;
        lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
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
        private int[] mAlphaARG;
        private int mOffsetX;
        private int mOffsetY;
        private Resources res;

        protected MagnifierView(Context context) {
            super(context);
            try {
                res = context.getPackageManager().getResourcesForApplication(
                        PACKAGE_NAME);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            init();
        }

        private void init() {
            //mMagnifierBitmap = BitmapFactory.decodeResource(mContext.getResources(),
            //        R.raw.light);
            Log.i("huanghua", "res:" + res);
            mMagnifierBitmap = readBitmap(mContext, res.getIdentifier(PACKAGE_NAME + ":raw/light", null, null));
            mAlphaBitmap = ((BitmapDrawable) mHostView.getBackground()).getBitmap();
            if (mAlphaBitmap != null) {
                mAlphaBitmap = mAlphaBitmap.copy(Bitmap.Config.ARGB_8888, true);
                int oldWidth = mAlphaBitmap.getWidth();
                int oldHeight = mAlphaBitmap.getHeight();
                //mAlphaBitmap = zoomBitmap(mAlphaBitmap, mScale);
                int newWidth = mAlphaBitmap.getWidth();
                int newHeight = mAlphaBitmap.getHeight();
                mOffsetX = (newWidth - oldWidth) / 2;
                mOffsetY = (newHeight - oldHeight) / 2;
                mAlphaARG = null;
                mAlphaARG = new int[newWidth * newHeight];
                mAlphaBitmap.getPixels(mAlphaARG, 0, newWidth, 0, 0, newWidth,newHeight);
/*                for (int i = 0; i < mAlphaARG.length; i++) {
                    mAlphaARG[i] = (0 << 24) | (mAlphaARG[i] & 0x00FFFFFF);
                }*/
            }
        }

        public Bitmap readBitmap(Context context, int id) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inInputShareable = true;
            opt.inPurgeable = true;
            InputStream is = res.openRawResource(id);
            return BitmapFactory.decodeStream(is, null, opt);
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
            if (mShowBottomView && mAlphaBitmap != null) {
                setBitmapAlpha(mAlphaBitmap, (int) (mScreenX * mScale), (int) (mScreenY * mScale), mAlphaARG);
                canvas.drawBitmap(mAlphaBitmap, 0, 0, null);
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
        // 1
        if (retX < 0) {
            retX = 0;
            nw = nw + retX;
        }
        if (retY < 0) {
            retY = 0;
            nh = nh + retY;
        }
        // 2
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
        if (nh <=0) {
            retY = h - 1;
            nh = 1;
        }
        return Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null, false);
    }

    public void setBitmapAlpha(Bitmap sourceImg, int x, int y, int[] argbs) {
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        int[] argb = argbs;//argbs.clone();

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
                            argb[x2] = (alpha << 24) | (argb[x2] & 0x00FFFFFF);
                        }
                    }
                    x1 = x - i;
                    x2 = y1 * width + x1;
                    if (x1 >= 0 && x1 < width) { // 越界判断
                        int sqrt = (int) Math.sqrt(i * i + j * j);
                        if (sqrt < range) {
                            int alpha = (range - Math.abs(sqrt)) * 250 / range;
                            argb[x2] = (alpha << 24) | (argb[x2] & 0x00FFFFFF);
                        }
                    }
                }
            }
        }
        sourceImg.setPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());
    }

}
