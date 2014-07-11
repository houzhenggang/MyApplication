
package com.huanghua.onelockscreen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class OneLockScreen extends RelativeLayout {

    public static final int UNLOCK_OFFICE = 30;
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageView mButtonDialer;
    private ImageView mButtonMess;
    private ImageView mButtonUnLock;
    private int mScreenWidth;
    private LinearLayout mTopView;

    public OneLockScreen(Context context) {
        this(context, null);
    }

    public OneLockScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mInflater.inflate(R.layout.activity_main, this, true);
        mButtonDialer = (ImageView) findViewById(R.id.button_dialer);
        mButtonMess = (ImageView) findViewById(R.id.button_message);
        mButtonUnLock = (ImageView) findViewById(R.id.button_unlock);
        mButtonDialer.setOnTouchListener(mButtonMoveRightListener);
        mButtonMess.setOnTouchListener(mButtonMoveLeftListener);
        mButtonUnLock.setOnTouchListener(mButtonMoveRightListener);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();

        mTopView = (LinearLayout) findViewById(R.id.top_date_view);
        mTopView.addView(new DigitalClock(context));
        DateView dateView = new DateView(mContext);
        dateView.setTextColor(Color.WHITE);
        dateView.setTextSize(13);
        dateView.setGravity(Gravity.RIGHT);
        mTopView.addView(dateView);
    }

    private float mDownX;
    private float mDownY;

    private float mButtonX;
    private float mButtonRawX;
    private int mMoveLenght;

    private View.OnTouchListener mButtonMoveLeftListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            float x = event.getRawX();
            float y = event.getRawY();

            int mMoveX = (int) (x - mDownX);
            int mMoveY = (int) (y - mDownY);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    mButtonRawX = event.getX();
                    mButtonX = v.getX();
                    mMoveLenght = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    calculateMoveLenght(mMoveX, mMoveY);
                    if (x < mButtonX + mButtonRawX) {
                        v.setX(x - mButtonRawX);
                    }
                    if (x < mButtonX + mButtonRawX && mMoveLenght > mScreenWidth / 2 - UNLOCK_OFFICE) {
                        System.exit(0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    v.setX(mButtonX);
                    if (x < mButtonX + mButtonRawX && mMoveLenght > mScreenWidth / 2 - UNLOCK_OFFICE) {
                        System.exit(0);
                    }
            }
            return true;
        }
    };

    private View.OnTouchListener mButtonMoveRightListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            float x = event.getRawX();
            float y = event.getRawY();

            int mMoveX = (int) (x - mDownX);
            int mMoveY = (int) (y - mDownY);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    mButtonRawX = event.getX();
                    mButtonX = v.getX();
                    mMoveLenght = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    calculateMoveLenght(mMoveX, mMoveY);
                    if (x > mButtonX + mButtonRawX) {
                        v.setX(x - mButtonRawX);
                    }
                    if (x > mButtonX + mButtonRawX && mMoveLenght > mScreenWidth / 2 - UNLOCK_OFFICE) {
                        System.exit(0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    v.setX(mButtonX);
                    if (x > mButtonX + mButtonRawX && mMoveLenght > mScreenWidth / 2 - UNLOCK_OFFICE) {
                        System.exit(0);
                    }
            }
            return true;
        }
    };

    public void calculateMoveLenght(int x, int y) {
        mMoveLenght = (int) Math.abs(Math.sqrt(x * x + y * y));
    }
}
