package com.androidwear.home.cuecard;

import android.content.Context;
import android.util.AttributeSet;

import com.androidwear.home.R;
import com.androidwear.home.view.DefaultWearableListItemView;

public class CueCardActionView extends DefaultWearableListItemView {
	private CueCardAction mCueCardAction;
	private int mPrimaryTextColor;
	private int mSecondaryTextColor;

	public CueCardActionView(Context context) {
		this(context, null);
	}

	public CueCardActionView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public CueCardActionView(Context context, AttributeSet attr, int style) {
		super(context, attr, style);
		mPrimaryTextColor = getResources().getColor(R.color.primary_text_light);
		mSecondaryTextColor = getResources().getColor(
				R.color.secondary_text_light);
	}

	public void setCueCardAction(CueCardAction cardAction, boolean mOffline) {
		this.mCueCardAction = cardAction;
		setItemInfo(cardAction.mIconResId, getResources().getString(cardAction.mTextResId));
	}
}
