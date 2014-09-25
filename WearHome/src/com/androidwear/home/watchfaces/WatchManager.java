package com.androidwear.home.watchfaces;

import android.content.Context;
import android.view.LayoutInflater;

public class WatchManager {
	private static WatchManager mWatchManager = null;
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;

	public WatchManager getSignal(Context context){
		if(mWatchManager == null){
			mWatchManager = new WatchManager(context);
		}
		return mWatchManager;
	}

	private WatchManager(Context context){
		
	}
}
