package com.androidwear.home.view;

import com.androidwear.home.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class CircledImageView extends FrameLayout {
	private static final int DEFAULT_CIRCLE_COLOR = Color.BLACK;
	private static final int DEFAULT_CIRCLE_BORDER_COLOR = Color.BLACK;
	private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;

	private ImageView mImageView;
	private final RectF mOval;
	private final Paint mPaint;
	private ColorStateList mCircleColor;
	float mCircleRadius;
	private float mCircleRadiusPressed;
	private int mCircleBorderColor;
	private float mCircleBorderWidth;
	protected float mProgress;
	private final float mShadowWidth;
	private float mShadowVisibility;
	private boolean mCircleHidden;
	private float mInitialCircleRadius;
	private boolean mPressed;

	public CircledImageView(Context context) {
		this(context, null);
	}

	public CircledImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircledImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mProgress = 1.0f;
		mCircleHidden = false;
		mPressed = false;

		mImageView = new ImageView(context);
		FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		imageViewLayoutParams.gravity = Gravity.CENTER;
		mImageView.setLayoutParams(imageViewLayoutParams);
		addView(mImageView);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CircledImageView);
		Drawable d = a.getDrawable(R.styleable.CircledImageView_circle_src);
		if (d != null) {
			mImageView.setImageDrawable(d);
		}

		mCircleColor = a.getColorStateList(R.styleable.CircledImageView_circle_color);
		if (mCircleColor == null) {
			mCircleColor = ColorStateList.valueOf(DEFAULT_CIRCLE_COLOR);
		}

		mCircleRadius = a.getDimension(R.styleable.CircledImageView_circle_radius, 0.0f);

		mInitialCircleRadius = this.mCircleRadius;
		mCircleRadiusPressed = a.getDimension(
				R.styleable.CircledImageView_circle_radius_pressed, mCircleRadius);

		mCircleBorderColor = a.getColor(
				R.styleable.CircledImageView_circle_border_color, DEFAULT_CIRCLE_BORDER_COLOR);

		mCircleBorderWidth = a.getDimension(
				R.styleable.CircledImageView_circle_border_width, 0.0f);
		if (mCircleBorderWidth > 0.0f) {
			mCircleRadius -= mCircleBorderWidth;
			mCircleRadiusPressed -= mCircleBorderWidth;
		}
		float circlePadding = a.getDimension(R.styleable.CircledImageView_circle_padding, 0.0f);
		if (circlePadding > 0.0F) {
			mCircleRadius -= circlePadding;
			mCircleRadiusPressed -= circlePadding;
		}
		mShadowWidth = a.getDimension(R.styleable.CircledImageView_circle_shadow_width, 0.0f);
		a.recycle();

		mOval = new RectF();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		setWillNotDraw(false);
	}

	public void setCircleHidden(boolean circleHidden) {
		mCircleHidden = circleHidden;
		invalidate();
	}

	protected void onDraw(Canvas canvas) {
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		float circleRadius = (mPressed) ? mCircleRadiusPressed : mCircleRadius;
		if ((mShadowWidth > 0.0f) && (mShadowVisibility > 0.0f)) {
			mOval.set(paddingLeft, paddingTop, getWidth()
					- getPaddingRight(), getHeight() - getPaddingBottom());

			float radius = circleRadius + mCircleBorderWidth
					+ mShadowWidth * mShadowVisibility;

			mPaint.setColor(DEFAULT_SHADOW_COLOR);
			mPaint.setStyle(Paint.Style.FILL);

			mPaint.setShader(new RadialGradient(mOval.centerX(),
					mOval.centerY(), radius, new int[] { DEFAULT_SHADOW_COLOR, 0 },
					new float[] { 0.6f, 1.0f }, Shader.TileMode.MIRROR));

			canvas.drawCircle(mOval.centerX(), mOval.centerY(),
					radius, mPaint);
			mPaint.setShader(null);
		}
		if (mCircleBorderWidth > 0.0F) {
			mOval.set(paddingLeft, paddingTop, getWidth()
					- getPaddingRight(), getHeight() - getPaddingBottom());

			mOval
					.set(mOval.centerX() - circleRadius,
							mOval.centerY() - circleRadius,
							mOval.centerX() + circleRadius,
							mOval.centerY() + circleRadius);

			mPaint.setColor(mCircleBorderColor);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(mCircleBorderWidth);
			canvas.drawArc(mOval, -90.0f, 360.0f * mProgress, false, mPaint);
		}
		if (!(mCircleHidden)) {
			mOval.set(paddingLeft, paddingTop, getWidth()
					- getPaddingRight(), getHeight() - getPaddingBottom());

			int color = mCircleColor.getColorForState(getDrawableState(),
					mCircleColor.getDefaultColor());

			mPaint.setColor(color);
			mPaint.setStyle(Paint.Style.FILL);
			float centerX = mOval.centerX();
			float centerY = mOval.centerY();
			if (this.mImageView.getDrawable() != null) {
				centerX = Math.round(mImageView.getLeft() + mImageView.getWidth() / 2.0F);
				centerY = Math.round(mImageView.getTop() + mImageView.getHeight() / 2.0F);
			}
			canvas.drawCircle(centerX, centerY, circleRadius, mPaint);
		}
		super.onDraw(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float radius = mCircleRadius + mCircleBorderWidth + mShadowWidth * mShadowVisibility;

		float desiredWidth = radius * 2.0F;
		float desiredHeight = radius * 2.0F;

		int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
		int width;
		if (widthMode == View.MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			if (widthMode == View.MeasureSpec.AT_MOST)
				width = (int) Math.min(desiredWidth, widthSize);
			else
				width = (int) desiredWidth;
		}
		int height;
		if (heightMode == View.MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			if (heightMode == View.MeasureSpec.AT_MOST)
				height = (int) Math.min(desiredHeight, heightSize);
			else {
				height = (int) desiredHeight;
			}
		}
		super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
	}

	public void setImageDrawable(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
		invalidate();
	}

	public void setImageResource(int resId) {
		mImageView.setImageResource(resId);
		invalidate();
	}

	public float getCircleRadius() {
		return mCircleRadius;
	}

	public void setCircleRadius(float circleRadius) {
		mCircleRadius = circleRadius;
		invalidate();
	}

	public void setCircleRadiusPressed(float circleRadiusPressed) {
		mCircleRadiusPressed = circleRadiusPressed;
		invalidate();
	}

	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	public void setCircleColor(int circleColor) {
		setCircleColorStateList(ColorStateList.valueOf(circleColor));
	}

	public void setCircleColorStateList(ColorStateList circleColor) {
		mCircleColor = circleColor;
		invalidate();
	}

	public void setProgress(float progress) {
		mProgress = progress;
		invalidate();
	}

	public void setShadowVisibility(float shadowVisibility) {
		if (shadowVisibility != mShadowVisibility) {
			mShadowVisibility = shadowVisibility;
			invalidate();
		}
	}

	public float getInitialCircleRadius() {
		return mInitialCircleRadius;
	}

	public void setCircleBorderColor(int circleBorderColor) {
		mCircleBorderColor = circleBorderColor;
	}

	public void setPressed(boolean pressed) {
		super.setPressed(pressed);
		mPressed = pressed;
		invalidate();
	}
}
