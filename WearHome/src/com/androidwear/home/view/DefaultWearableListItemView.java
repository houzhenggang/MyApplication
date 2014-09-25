package com.androidwear.home.view;

import com.androidwear.home.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.wearable.view.WearableListView;

public class DefaultWearableListItemView extends RelativeLayout implements
		WearableListView.Item {

	private int mExpansionAmount;
	private int mFadedCircleColor;
	private float mFadedTextAlpha;
	private CircledImageView mIconView;
	private int mSelectedColor;
	private TextView mTextView;

	public DefaultWearableListItemView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public DefaultWearableListItemView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public DefaultWearableListItemView(Context context, AttributeSet attr,
			int style) {
		super(context, attr, style);
		setGravity(16);
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.wearable_list_item, this);
		mIconView = ((CircledImageView) view.findViewById(R.id.icon));
		mTextView = ((TextView) view.findViewById(R.id.action_text));
		mExpansionAmount = getResources().getDimensionPixelSize(
				R.dimen.action_icon_radius_expand);
		mFadedTextAlpha = (getResources().getInteger(
				R.integer.action_text_faded_alpha) / 100.0F);
		mSelectedColor = getResources().getColor(R.color.cw_blue);
		mFadedCircleColor = getResources().getColor(R.color.cw_wof_gray);
	}

	@Override
	public float getCurrentProximityValue() {
		return mIconView.getCircleRadius();
	}

	@Override
	public float getProximityMaxValue() {
		return (mIconView.getInitialCircleRadius() + mExpansionAmount);
	}

	@Override
	public float getProximityMinValue() {
		return mIconView.getInitialCircleRadius();
	}

	@Override
	public void onScaleDownStart() {
		mTextView.setAlpha(mFadedTextAlpha);
		mIconView.setCircleColor(mFadedCircleColor);
	}

	@Override
	public void onScaleUpStart() {
		mTextView.setAlpha(1.0f);
		mIconView.setCircleColor(mSelectedColor);
	}

	@Override
	public void setScalingAnimatorValue(float arg0) {
		if (arg0 == mIconView.getInitialCircleRadius()) {
			setClipChildren(true);
		}else{
			setClipChildren(false);
		}
		mIconView.setCircleRadius(arg0);
	}

	public void setItemInfo(int iconResId, CharSequence title) {
		mIconView.setImageResource(iconResId);
		mTextView.setText(title);
		mTextView.setAlpha(mFadedTextAlpha);
	}
}
