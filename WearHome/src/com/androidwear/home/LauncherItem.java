package com.androidwear.home;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.res.Resources;
import android.support.wearable.view.WearableListView;

public class LauncherItem extends LinearLayout implements WearableListView.Item{

	private TextView mAppLabel;
	private float mFadedTextAlpha;
	private ImageView mIcon;
	private float mIconScale;
	private float mIconScaleMin;
	private float mIconScalePercent;
	private ApplicationInfo mApplicationInfo = null;

	public LauncherItem(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public LauncherItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public LauncherItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT));
		Resources res = getResources();
		int padding = res.getDimensionPixelSize(R.dimen.launcher_item_vertical_padding);
		setPadding(0, padding, 0, padding);
		mIconScalePercent = res.getFraction(R.dimen.launcher_icon_scale_percent, 1, 1);
		float f = 1.0f - mIconScalePercent;
	    mIconScaleMin = f;
	    mIconScale = f;
	    LayoutInflater.from(context).inflate(R.layout.launcher_item_layout, this);
	    mIcon = ((ImageView)findViewById(R.id.icon));
	    mAppLabel = ((TextView)findViewById(R.id.label));
	    mFadedTextAlpha = (getResources().getInteger(R.integer.action_text_faded_alpha) / 100.0f);
	}

	public void setApplicationInfo(ApplicationInfo aInfo, IconCache iconCache){
		mApplicationInfo = aInfo;
		mAppLabel.setText(aInfo.title);
		mIcon.setImageBitmap(iconCache.getIcon(aInfo.intent));
	}

	@Override
	public float getCurrentProximityValue() {
		// TODO Auto-generated method stub
		return mIconScale;
	}

	@Override
	public float getProximityMaxValue() {
		// TODO Auto-generated method stub
		return 1.0f;
	}

	@Override
	public float getProximityMinValue() {
		// TODO Auto-generated method stub
		return mIconScaleMin;
	}

	@Override
	public void onScaleDownStart() {
		// TODO Auto-generated method stub
		mAppLabel.setAlpha(this.mFadedTextAlpha);
	}

	@Override
	public void onScaleUpStart() {
		// TODO Auto-generated method stub
		mAppLabel.setAlpha(1.0f);
	}

	@Override
	public void setScalingAnimatorValue(float arg0) {
		// TODO Auto-generated method stub
		if(arg0 == mIconScaleMin){
			setClipChildren(true);
		}else{
			setClipChildren(false);
			mIconScale = arg0;
		    mIcon.setScaleX(arg0);
		    mIcon.setScaleY(arg0);
		}
	}
}
