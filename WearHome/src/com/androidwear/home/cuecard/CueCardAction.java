package com.androidwear.home.cuecard;

public class CueCardAction {
	public final static int TYPY_SETTINGS = 1;
	public final static int TYPY_LAUNCHER = 2;
	public final static int TYPE_AGENDA = 3;
	public final static int TYPE_SETALARM = 4;
	public final static int TYPE_STOPWATCH = 5;
	public final static int TYPE_SHOWALARMS = 6;
	public final static int TYPE_TIMER = 7;

	public int mIconResId;
	public int mTextResId;
	public int mType;
	
	public CueCardAction(int type, int iconResId, int textResId){
		mType = type;
		mTextResId = textResId;
		mIconResId = iconResId;
	}
}
