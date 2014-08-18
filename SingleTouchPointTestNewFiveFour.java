
package com.innova.onekeyinstall;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

//add by hewei for TP test 
public class SingleTouchPointTestNewFiveFour extends Activity implements View.OnTouchListener {

    private int mRectWidth;

    private int mRectHeight;

    MyView mMyView = null;

    private final int HORIZONTALNUM = 5;
    private final int VERTICALNUM = 5;
    private final int DIAGONALNUM = 2;
    private int OFFSET = 25;
    private int LINEOFFSET = 50;
    private int mRectHSize = 0;
    private int mRectVSize = 0;
    private int mCount = 1;
    private int[] mPassNumber;
    private boolean mIsHorizontal = true;
    private boolean mIsVertical = false;
    private int mDy = 0;
    private int mDx = 0;
    private List<RectEntity> mDrawRectList = new ArrayList<RectEntity>();
    private List<PointEntity> mDrawPointList = new ArrayList<PointEntity>();
    private List<LineEntity> mDrawLineList = new ArrayList<LineEntity>();
    private boolean hasShowButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int isFullTest = getIntent().getIntExtra("isFullTest", 0);
        int fullTestActivityId = getIntent().getIntExtra("fullTestActivityId", 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics dm = getApplicationContext().getResources()
                .getDisplayMetrics();
        mRectWidth = dm.widthPixels;
        mRectHeight = dm.heightPixels;
        setContentView(mMyView = new MyView(this));
        mMyView.setOnTouchListener(this);
        mRectHSize = mRectHeight / HORIZONTALNUM;
        mRectVSize = mRectWidth / VERTICALNUM;
        mDy = mRectHSize / 2;
        mDx = mRectVSize / 2;
        RectEntity ce = new RectEntity(0, mDy - OFFSET, mRectWidth, mDy
                + OFFSET);
        mDrawRectList.add(ce);
    }

    public class MyView extends View {
        public MyView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFF0000);
            for (int i = 0; i < mDrawRectList.size(); i++) {
                RectEntity re = mDrawRectList.get(i);
                canvas.drawRect(re.rx, re.ry, re.rWidth, re.rHeight, re.rPaint);
            }
            for (int j = 0; j < mDrawPointList.size(); j++) {
                PointEntity pe = mDrawPointList.get(j);
                canvas.drawPoint((int) pe.px, (int) pe.py, pe.pPaint);
            }

            for (int k = 0; k < mDrawLineList.size(); k++) {
                LineEntity le = mDrawLineList.get(k);
                canvas.drawLines(le.pts, le.lPaint);
            }
        }
    }

    public class PointEntity {
        private Paint pPaint = null;
        int px, py = 0;

        PointEntity(int x, int y) {
            px = x;
            py = y;
            pPaint = new Paint();
            pPaint.setAntiAlias(true);
            pPaint.setARGB(255, 255, 0, 0);
            pPaint.setStyle(Paint.Style.STROKE);
            pPaint.setStrokeWidth(4);
        }
    }

    public class LineEntity {
        private Paint lPaint = null;
        float[] pts = {};

        LineEntity(float[] _pts) {
            pts = _pts;
            lPaint = new Paint();
            lPaint.setAntiAlias(true);
            lPaint.setARGB(255, 0, 0, 255);
            lPaint.setStyle(Paint.Style.STROKE);
            lPaint.setStrokeWidth(4);
        }
    }

    private class RectEntity {
        private Paint rPaint = null;
        private int rx, ry, rWidth, rHeight = 0;
        private boolean isPass = false;

        RectEntity(int _rx, int _ry, int _rWidth, int _rHeight) {
            rx = _rx;
            ry = _ry;
            rWidth = _rWidth;
            rHeight = _rHeight;
            rPaint = new Paint();
            rPaint.setAntiAlias(true);
            rPaint.setARGB(255, 0, 0, 255);
            rPaint.setStyle(Paint.Style.STROKE);
            rPaint.setStrokeWidth(3);
        }
    }

    public void initRect(int rx, int ry, int rwidth, int rhight, int count) {
        RectEntity ce = new RectEntity(rx, ry, rwidth, rhight);
        mDrawRectList.add(ce);
    }

    public void initPoint(int px, int py) {
        PointEntity pe = new PointEntity(px, py);
        mDrawPointList.add(pe);
    }

    private boolean IsCollision(int dx, int dy) {
        if (mIsHorizontal || mIsVertical) {
            RectEntity rect = mDrawRectList.get(mCount - 1);
            if (rect.rx < dx && rect.rWidth > dx && rect.ry < dy
                    && rect.rHeight > dy) {
                return false;
            } else {
                return true;
            }
        } else {
            LineEntity le = mDrawLineList.get(mCount - 1);
            return isInZone(dx, dy, le.pts[0], le.pts[1],
                    le.pts[2], le.pts[3], le.pts[4], le.pts[5],
                    le.pts[6], le.pts[7]);
        }
    }

    public boolean isInZone(double x, double y, double x1, double y1,
            double x2, double y2, double x3, double y3, double x4, double y4) {
        double k = (y2 - y1) / (x2 - x1);
        double b1 = y4 - k * x4;
        double b2 = y2 - k * x2;
        double b = y - k * x;
        if (b > b1 && b < b2)
            return false;
        else
            return true;
    }

    private boolean isSuccess(int count) {
        if (mDrawPointList.size() > 0) {
            PointEntity p0 = mDrawPointList.get(0);
            PointEntity pN = mDrawPointList.get(mDrawPointList.size() - 1);
            if (mIsHorizontal) {
                RectEntity re = mDrawRectList.get(count);
                if (Math.abs(pN.px - p0.px) >= mRectWidth * 0.8) {
                    re.rPaint.setARGB(255, 0, 255, 0);
                    re.isPass = true;
                    mCount++;
                    mDrawRectList.add(new RectEntity(0, mRectHSize * mCount
                            - mDy - OFFSET, mRectWidth, mRectHSize * mCount
                            - mDy + OFFSET));
                    if (mCount > HORIZONTALNUM) {
                        mIsHorizontal = false;
                        mIsVertical = true;
                        mCount = 1;
                        mDrawRectList.clear();
                        mDrawRectList.add(new RectEntity(Math.abs(mRectVSize
                                * mCount - mDx - OFFSET), 0, Math
                                .abs(mRectVSize * mCount - mDx + OFFSET),
                                mRectHeight));
                    }
                    return true;
                } else {
                    re.rPaint.setARGB(255, 255, 0, 0);
                    return false;
                }
            } else if (mIsVertical) {
                RectEntity re = mDrawRectList.get(count);
                if (Math.abs(pN.py - p0.py) >= mRectHeight * 0.8) {
                    re.rPaint.setARGB(255, 0, 255, 0);
                    re.isPass = true;
                    mCount++;
                    mDrawRectList.add(new RectEntity(Math.abs(mRectVSize
                            * mCount - mDx - OFFSET), 0, Math.abs(mRectVSize
                            * mCount - mDx + OFFSET), mRectHeight));
                    if (mCount > VERTICALNUM) {
                        mIsHorizontal = false;
                        mIsVertical = false;
                        mCount = 1;
                        mDrawRectList.clear();
                        float[] pts = {
                                0, LINEOFFSET, mRectWidth - LINEOFFSET, mRectHeight,
                                LINEOFFSET, 0, mRectWidth, mRectHeight - LINEOFFSET
                        };
                        mDrawLineList.add(new LineEntity(pts));
                    }
                    return true;
                } else {
                    re.rPaint.setARGB(255, 255, 0, 0);
                    return false;
                }
            } else {
                LineEntity le = mDrawLineList.get(count);
                int diagonalLength = (int) Math.sqrt((le.pts[2] - le.pts[0])
                        * (le.pts[2] - le.pts[0]) + (le.pts[3] - le.pts[1])
                        * (le.pts[3] - le.pts[1]));
                int pointLength = (int) Math.sqrt((pN.px - p0.px) * (pN.px - p0.px)
                        + (pN.py - p0.py) * (pN.py - p0.py));
                if (pointLength >= diagonalLength * 0.8) {
                    le.lPaint.setARGB(255, 0, 255, 0);
                    mCount++;
                    if (mCount > DIAGONALNUM) {
                        mCount = 1;
                        if (!hasShowButton) {
                            hasShowButton = true;
                        }
                    } else {
                        float[] pts = {
                                mRectWidth, LINEOFFSET, LINEOFFSET, mRectHeight,
                                mRectWidth - LINEOFFSET, 0, 0, mRectHeight - LINEOFFSET
                        };
                        mDrawLineList.add(new LineEntity(pts));
                    }
                    mMyView.invalidate();
                    return true;
                } else {
                    le.lPaint.setARGB(255, 255, 0, 0);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initPoint((int) e.getX(), (int) e.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                initPoint((int) e.getX(), (int) e.getY());
                for (int i = 0; i < mDrawPointList.size(); i++) {
                    PointEntity pe = mDrawPointList.get(i);
                    if (IsCollision(pe.px, pe.py)) {
                        mDrawPointList.clear();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isSuccess(mCount - 1);
                mDrawPointList.clear();
                break;
        }
        mMyView.invalidate();
        return true;
    }
}
// end by hewei for Tp test
